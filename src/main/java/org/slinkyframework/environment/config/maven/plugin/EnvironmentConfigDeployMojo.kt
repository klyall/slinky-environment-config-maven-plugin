package org.slinkyframework.environment.config.maven.plugin

import org.apache.maven.execution.MavenSession
import org.apache.maven.model.DeploymentRepository
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.Component
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import org.apache.maven.project.ProjectBuilder
import org.apache.maven.shared.transfer.artifact.deploy.ArtifactDeployer
import org.apache.maven.shared.transfer.repository.RepositoryManager
import org.slinkyframework.environment.config.maven.plugin.deploy.MavenDeployer
import java.io.File

@Mojo(name = "deploy", defaultPhase = LifecyclePhase.DEPLOY)
class EnvironmentConfigDeployMojo : AbstractMojo() {
    @Parameter(property = "config.targetDir", defaultValue = "${project.build.directory}/generated-config", readonly = true)
    private lateinit var targetDir: String

    @Parameter(defaultValue = "\${project}", required = true, readonly = true)
    private lateinit var project: MavenProject

    /**
     * Flag whether Maven is currently in online/offline mode.
     */
    @Parameter(defaultValue = "\${settings.offline}", readonly = true)
    private var offline: Boolean = false

    @Parameter(defaultValue = "\${session}", required = true, readonly = true)
    private lateinit var session: MavenSession

    @Component
    private lateinit var projectBuilder: ProjectBuilder

    @Component
    private lateinit var repositoryManager: RepositoryManager

    @Component
    private lateinit var deployer: ArtifactDeployer


    @Throws(MojoExecutionException::class, MojoFailureException::class)
    override fun execute() {
        failIfOffline()

        val repository = deploymentRepository()

        val mavenDeployer = MavenDeployer(
                project.basedir.toPath(), project.groupId, project.version, File(targetDir).toPath(), repository,
                session, projectBuilder, repositoryManager, deployer)

        mavenDeployer.processEnvironments()
    }

    private fun failIfOffline() {
        if (offline) {
            throw MojoFailureException("Cannot deploy artifacts when Maven is in offline mode")
        }
    }

    private fun deploymentRepository(): DeploymentRepository {
        return if (isSnapshot()) {
            project.distributionManagement.snapshotRepository
        } else {
            project.distributionManagement.repository
        }
    }

    private fun isSnapshot(): Boolean {
        val version = project.version

        return version != null && version.endsWith("SNAPSHOT")
    }
}
