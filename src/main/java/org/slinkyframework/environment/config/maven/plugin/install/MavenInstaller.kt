package org.slinkyframework.environment.config.maven.plugin.install

import org.apache.maven.artifact.handler.DefaultArtifactHandler
import org.apache.maven.execution.MavenSession
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.project.ProjectBuilder
import org.apache.maven.shared.transfer.project.install.ProjectInstaller
import org.apache.maven.shared.transfer.project.install.ProjectInstallerRequest
import org.apache.maven.shared.transfer.repository.RepositoryManager
import org.codehaus.plexus.util.FileUtils
import org.slf4j.LoggerFactory
import org.slinkyframework.environment.config.maven.plugin.AbstractMavenGoal
import java.io.File
import java.nio.file.Path

class MavenInstaller(projectDir: Path, groupId: String, version: String, targetDir: Path,
                     session: MavenSession,
                     projectBuilder: ProjectBuilder,
                     repositoryManager: RepositoryManager,
                     private val installer: ProjectInstaller
) : AbstractMavenGoal(projectDir, groupId, version, targetDir, session, projectBuilder, repositoryManager) {
    override val goal: String
        get() = MAVEN_GOAL

    override suspend fun execute(groupId: String, artifactId: String, version: String, file: File) {
        if (!file.exists()) {
            val message = "The specified file '${file.path}' does not exist"
            LOG.error(message)
            throw MojoFailureException(message)
        }


        val project = createMavenProject(groupId, artifactId, version)

        // We need to set a new ArtifactHandler otherwise
        // the extension will be set to the packaging type
        // which is sometimes wrong.
        val ah = DefaultArtifactHandler(PACKAGING)
        ah.extension = FileUtils.getExtension(file.name)

        project.artifact.artifactHandler = ah
        val artifact = project.artifact

        if (file == getLocalRepoFile(groupId, artifactId, version)) {
            throw MojoFailureException("Cannot install artifact. "
                    + "Artifact is already in the local repository.\n\nFile in question is: " + file + "\n")
        }

        artifact.file = file

        val temporaryPom = generatePomFile(groupId, artifactId, version)

        LOG.debug("Installing generated POM")

        project.file = temporaryPom

        try {
            val projectInstallerRequest = ProjectInstallerRequest().setProject(project)
            val buildingRequest = session.projectBuildingRequest

            installer.install(buildingRequest, projectInstallerRequest)
        } catch (e: Exception) {
            throw MojoExecutionException(e.message, e)
        } finally {
            temporaryPom.delete()
        }
    }

    companion object {
        private const val MAVEN_GOAL = "install"
        private val LOG = LoggerFactory.getLogger(MavenInstaller::class.java)
    }
}
