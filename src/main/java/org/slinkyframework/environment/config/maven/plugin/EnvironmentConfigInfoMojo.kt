package org.slinkyframework.environment.config.maven.plugin

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.slinkyframework.environment.config.maven.plugin.info.ConfigDirectoryWalker

import java.io.File

@Mojo(name = "info")
class EnvironmentConfigInfoMojo : AbstractMojo()
{
    @Parameter(property = "config.sourceDir", defaultValue = "src/main/resources", readonly = true)
    private lateinit var sourceDir: String

    @Throws(MojoExecutionException::class, MojoFailureException::class)
    override fun execute()
    {
        val directoryWalker = ConfigDirectoryWalker()

        directoryWalker.walk(File(sourceDir))
    }
}
