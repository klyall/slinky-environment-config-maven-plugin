package org.slinkyframework.environment.config.maven.plugin

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.apache.maven.execution.MavenSession
import org.apache.maven.model.Model
import org.apache.maven.model.building.ModelBuildingException
import org.apache.maven.model.building.StringModelSource
import org.apache.maven.model.io.xpp3.MavenXpp3Writer
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.project.DefaultProjectBuildingRequest
import org.apache.maven.project.MavenProject
import org.apache.maven.project.ProjectBuilder
import org.apache.maven.project.ProjectBuildingException
import org.apache.maven.shared.transfer.artifact.DefaultArtifactCoordinate
import org.apache.maven.shared.transfer.repository.RepositoryManager
import org.apache.maven.shared.utils.Os
import org.codehaus.plexus.util.WriterFactory
import org.slf4j.LoggerFactory
import org.slinkyframework.environment.config.maven.plugin.config.templates.FilterFileGenerator
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

abstract class AbstractMavenGoal(
        private val projectDir: Path,
        private val groupId: String,
        private val version: String,
        private val targetDir: Path,
        protected val session: MavenSession,
        private val projectBuilder: ProjectBuilder,
        private val repositoryManager: RepositoryManager
) {
    abstract val goal: String

    abstract suspend fun execute(groupId: String, artifactId: String, version: String, file: File)

    fun processEnvironments() {
        Files.list(targetDir)
                .filter { Files.isDirectory(it) }
                .sorted { o1, o2 -> o1.fileName.compareTo(o2.fileName) }
                .forEach { processEnvironment(it) }
    }

    private fun processEnvironment(environmentDir: Path) {
        runBlocking {
            val environmentName = environmentDir.toFile().name

            val jobs = Files.list(environmentDir).toList()
                    .filter { it.toFile().name.toLowerCase().endsWith(".zip") }
                    .sortedBy { it.fileName }
                    .map { GlobalScope.async { executeMavenGoal(it, environmentName, goal) } }

            jobs.map { it.await() }
        }
    }

    private suspend fun executeMavenGoal(zipFile: Path, environmentName: String, goal: String): Boolean {
        val environmentGroupId = "$groupId.$environmentName"
        val extension = "-$version.zip"
        val artifactId = zipFile.toFile().name.replace(extension, "")

        LOG.info("Starting 'mvn {} {}:{}:{}'", goal, environmentGroupId, artifactId, version)

        execute(environmentGroupId, artifactId, version, zipFile.toFile())

        LOG.info("Finished 'mvn {} {}:{}:{}'", goal, environmentGroupId, artifactId, version)

        return true
    }

    protected fun createMavenProject(groupId: String, artifactId: String, version: String): MavenProject {
        val modelSource = StringModelSource(
                "<project><modelVersion>4.0.0</modelVersion>" +
                        "<groupId>$groupId</groupId>" +
                        "<artifactId>$artifactId</artifactId>" +
                        "<version>$version</version>" +
                        "<packaging>$PACKAGING</packaging></project>")

        val pbr = DefaultProjectBuildingRequest(session.projectBuildingRequest)
        pbr.isProcessPlugins = false

        try {
            return projectBuilder.build(modelSource, pbr).project
        } catch (e: ProjectBuildingException) {
            if (e.cause is ModelBuildingException) {
                throw MojoExecutionException("The artifact information is not valid:${Os.LINE_SEP}${(e.cause as ModelBuildingException).message}")
            }
            throw MojoFailureException("Unable to create the project.", e)
        }
    }

    protected fun getLocalRepoFile(groupId: String, artifactId: String, version: String): File {
        val coordinate = DefaultArtifactCoordinate()
        coordinate.groupId = groupId
        coordinate.artifactId = artifactId
        coordinate.version = version
        val path = repositoryManager.getPathForLocalArtifact(session.projectBuildingRequest, coordinate)
        return File(repositoryManager.getLocalRepositoryBasedir(session.projectBuildingRequest), path)
    }

    protected fun generatePomFile(groupId: String, artifactId: String, version: String): File {
        val model = generateModel(groupId, artifactId, version)

        val pomFile = File.createTempFile("mvn$goal", ".pom")
        WriterFactory.newXmlWriter(pomFile).use { MavenXpp3Writer().write(it, model) }

        return pomFile
    }

    private fun generateModel(groupId: String, artifactId: String, version: String): Model {
        val model = Model()

        model.modelVersion = "4.0.0"

        model.groupId = groupId
        model.artifactId = artifactId
        model.version = version
        model.packaging = PACKAGING

        model.description = "POM was created from config"

        return model
    }

    companion object {
        const val PACKAGING = "zip"
        private val LOG = LoggerFactory.getLogger(FilterFileGenerator::class.java)
    }
}
