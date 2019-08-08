package org.slinkyframework.environment.config.maven.plugin

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import org.slinkyframework.environment.config.maven.plugin.install.MavenInstaller

import java.io.File

@Mojo(name = "install", defaultPhase = LifecyclePhase.INSTALL)
class EnvironmentConfigInstallMojo : AbstractMojo()
{
    @Parameter(property = "config.targetDir", defaultValue = "target/generated-config", readonly = true)
    private lateinit var targetDir: String

    @Parameter(defaultValue = "\${project}", required = true, readonly = true)
    private lateinit var project: MavenProject

    @Throws(MojoExecutionException::class, MojoFailureException::class)
    override fun execute()
    {
        val mavenInstaller = MavenInstaller(project.basedir, project.groupId, project.version, File(targetDir))

        mavenInstaller.processEnvironments()
    }
}
