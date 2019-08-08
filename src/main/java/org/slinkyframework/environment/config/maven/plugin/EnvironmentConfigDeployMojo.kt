package org.slinkyframework.environment.config.maven.plugin

import org.apache.maven.model.DeploymentRepository
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import org.slinkyframework.environment.config.maven.plugin.deploy.MavenDeployer
import java.io.File

@Mojo(name = "deploy", defaultPhase = LifecyclePhase.DEPLOY)
class EnvironmentConfigDeployMojo : AbstractMojo()
{
    @Parameter(property = "config.targetDir", defaultValue = "target/generated-config", readonly = true)
    private lateinit var targetDir: String

    @Parameter(defaultValue = "\${project}", required = true, readonly = true)
    private lateinit var project: MavenProject

    @Throws(MojoExecutionException::class, MojoFailureException::class)
    override fun execute()
    {
        val repository = deploymentRepository()

        val mavenDeployer: MavenDeployer =
                if (repository != null)
                {
                    MavenDeployer(project.basedir, project.groupId, project.version, File(targetDir), repository)
                }
                else
                {
                    MavenDeployer(project.basedir, project.groupId, project.version, File(targetDir))
                }

        mavenDeployer.processEnvironments()
    }

    private fun deploymentRepository(): DeploymentRepository?
    {
        return if (isSnapshot())
        {
            project.distributionManagement.snapshotRepository
        }
        else
        {
            project.distributionManagement.repository
        }
    }

    private fun isSnapshot(): Boolean
    {
        val version = project.version

        return version != null && version.endsWith("SNAPSHOT")
    }
}
