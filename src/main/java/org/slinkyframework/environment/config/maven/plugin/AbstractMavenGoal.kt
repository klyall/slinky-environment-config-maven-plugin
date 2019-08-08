package org.slinkyframework.environment.config.maven.plugin

import org.apache.maven.shared.invoker.DefaultInvocationRequest
import org.apache.maven.shared.invoker.DefaultInvoker
import org.apache.maven.shared.invoker.InvocationRequest
import org.apache.maven.shared.invoker.InvocationResult
import org.apache.maven.shared.invoker.Invoker
import org.apache.maven.shared.invoker.MavenInvocationException
import org.slinkyframework.environment.config.maven.plugin.config.EnvironmentConfigException

import java.io.File
import java.io.FileFilter
import java.util.Collections
import java.util.Properties

abstract class AbstractMavenGoal(private val projectDir: File, private val groupId: String, private val version: String, private val targetDir: File)
{
    private val invoker: Invoker = DefaultInvoker()

    abstract val goal: String

//    init
//    {
//        invoker = DefaultInvoker()
//    }

    abstract fun getAdditionalProperties(zipFile: File): Properties

    fun processEnvironments()
    {
        targetDir.listFiles(FileFilter { it.isDirectory() })
                .filterNotNull()
                .forEach { processEnvironment(it)}
    }

    private fun processEnvironment(environmentDir: File)
    {
        val environmentName = environmentDir.name

        environmentDir.listFiles { dir, name -> name.toLowerCase().endsWith(".zip") }
                .forEach { executeMavenGoal(it, environmentName, goal, getAdditionalProperties(it)) }
    }

    fun executeMavenGoal(zipFile: File, environmentName: String, goal: String, additionalProperties: Properties)
    {
        val request = DefaultInvocationRequest()
        request.isShellEnvironmentInherited = true
        request.baseDirectory = projectDir
        request.pomFile = File(projectDir, "pom.xml")
        request.isInteractive = false
        request.goals = listOf(goal)

        val environmentGroupId = "$groupId.$environmentName"
        val artifactId = zipFile.name.replace("-$version.zip", "")

        val props = Properties(additionalProperties)
        props.setProperty("groupId", environmentGroupId)
        props.setProperty("artifactId", artifactId)
        props.setProperty("version", version)
        props.setProperty("generatePom", "true")
        props.setProperty("packaging", "zip")
        props.setProperty("file", zipFile.absolutePath)

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

        if (result.exitCode != 0)
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
}
