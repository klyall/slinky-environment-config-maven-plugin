package org.slinkyframework.environment.config.maven.plugin

import org.apache.maven.execution.MavenSession
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.Component
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import org.apache.maven.project.ProjectBuilder
import org.apache.maven.shared.transfer.project.install.ProjectInstaller
import org.apache.maven.shared.transfer.repository.RepositoryManager
import org.slinkyframework.environment.config.maven.plugin.install.MavenInstaller
import java.io.File

@Mojo(name = "install", defaultPhase = LifecyclePhase.INSTALL)
class EnvironmentConfigInstallMojo : AbstractMojo() {
    @Parameter(property = "config.targetDir", defaultValue = "\${project.build.directory}/generated-config", readonly = true)
    private lateinit var targetDir: String

    @Parameter(defaultValue = "\${project}", required = true, readonly = true)
    private lateinit var project: MavenProject

    @Parameter(defaultValue = "\${session}", required = true, readonly = true)
    protected lateinit var session: MavenSession

    @Component
    private lateinit var projectBuilder: ProjectBuilder

    @Component
    private lateinit var repositoryManager: RepositoryManager

    @Component
    private lateinit var installer: ProjectInstaller

    @Throws(MojoExecutionException::class, MojoFailureException::class)
    override fun execute() {
        val mavenInstaller = MavenInstaller(
                project.basedir.toPath(), project.groupId, project.version, File(targetDir).toPath(),
                session, projectBuilder, repositoryManager, installer)

        mavenInstaller.processEnvironments()
    }
}
