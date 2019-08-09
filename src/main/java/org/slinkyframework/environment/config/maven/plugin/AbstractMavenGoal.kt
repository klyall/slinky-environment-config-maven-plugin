package org.slinkyframework.environment.config.maven.plugin

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.apache.maven.shared.invoker.DefaultInvocationRequest
import org.apache.maven.shared.invoker.DefaultInvoker
import org.apache.maven.shared.invoker.InvocationResult
import org.apache.maven.shared.invoker.Invoker
import org.apache.maven.shared.invoker.MavenInvocationException
import org.slf4j.LoggerFactory
import org.slinkyframework.environment.config.maven.plugin.config.EnvironmentConfigException
import org.slinkyframework.environment.config.maven.plugin.config.templates.FilterFileGenerator
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.streams.toList

abstract class AbstractMavenGoal(private val projectDir: Path, private val groupId: String, private val version: String, private val targetDir: Path)
{
    private val invoker: Invoker = DefaultInvoker()

    abstract val goal: String

    abstract fun getAdditionalProperties(zipFile: Path): Properties

    fun processEnvironments()
    {
        Files.list(targetDir)
                .filter { Files.isDirectory(it) }
                .forEach { processEnvironment(it) }
    }

    private fun processEnvironment(environmentDir: Path)
    {
        runBlocking {
            val environmentName = environmentDir.toFile().name

            val jobs = Files.list(environmentDir).toList()
                    .filter { it.toFile().name.toLowerCase().endsWith(".zip") }
                    .map { GlobalScope.async { executeMavenGoal(it, environmentName, goal, getAdditionalProperties(it)) } }

            jobs.map { it.await() }
        }
    }

    private fun executeMavenGoal(zipFile: Path, environmentName: String, goal: String, additionalProperties: Properties): Boolean
    {
        val environmentGroupId = "$groupId.$environmentName"
        val artifactId = zipFile.toFile().name.replace("-$version.zip", "")

        LOG.info("Starting 'mvn {} {}:{}:{}'", goal, environmentGroupId, artifactId, version)

        val request = DefaultInvocationRequest()
        request.isShellEnvironmentInherited = true
        request.baseDirectory = projectDir.toFile()
        request.pomFile = File(projectDir.toFile(), "pom.xml")
        request.isInteractive = false
        request.goals = listOf(goal)

        val props = Properties(additionalProperties)
        props.setProperty("groupId", environmentGroupId)
        props.setProperty("artifactId", artifactId)
        props.setProperty("version", version)
        props.setProperty("generatePom", "true")
        props.setProperty("packaging", "zip")
        props.setProperty("file", zipFile.toFile().absolutePath)

        additionalProperties.forEach { key, value -> props.setProperty(key.toString(), value.toString()) }

        request.properties = props

        val result: InvocationResult
        try
        {
            result = invoker.execute(request)
        }
        catch (e: MavenInvocationException)
        {
            throw EnvironmentConfigException(String.format("Error installing file %s", zipFile), e)
        }

        if (result.exitCode == 0)
        {
            LOG.info("Finished 'mvn {} {}:{}:{}'", goal, environmentGroupId, artifactId, version)
            return true
        }
        else
        {
            if (result.executionException != null)
            {
                throw EnvironmentConfigException("Failed to execute Maven goal.", result.executionException)
            }
            else
            {
                throw EnvironmentConfigException("Failed to execute Maven goal. Exit code: " + result.exitCode)
            }
        }
    }

    companion object
    {
        private val LOG = LoggerFactory.getLogger(FilterFileGenerator::class.java)
    }
}
