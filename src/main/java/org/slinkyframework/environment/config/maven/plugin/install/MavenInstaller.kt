package org.slinkyframework.environment.config.maven.plugin.install

import org.slinkyframework.environment.config.maven.plugin.AbstractMavenGoal

import java.io.File
import java.util.Properties

class MavenInstaller(projectDir: File, groupId: String, version: String, targetDir: File) : AbstractMavenGoal(projectDir, groupId, version, targetDir)
{
    override val goal: String
        get() = MAVEN_GOAL

    override fun getAdditionalProperties(zipFile: File): Properties
    {
        return Properties()
    }

    companion object
    {
        private const val MAVEN_GOAL = "install:install-file"
    }
}
