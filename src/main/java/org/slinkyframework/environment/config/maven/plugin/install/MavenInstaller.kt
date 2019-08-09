package org.slinkyframework.environment.config.maven.plugin.install

import org.slinkyframework.environment.config.maven.plugin.AbstractMavenGoal

import java.io.File
import java.nio.file.Path
import java.util.Properties

class MavenInstaller(projectDir: Path, groupId: String, version: String, targetDir: Path) : AbstractMavenGoal(projectDir, groupId, version, targetDir)
{
    override val goal: String
        get() = MAVEN_GOAL

    override fun getAdditionalProperties(zipFile: Path): Properties
    {
        return Properties()
    }

    companion object
    {
        private const val MAVEN_GOAL = "install:install-file"
    }
}
