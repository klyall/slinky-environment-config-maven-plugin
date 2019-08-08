package org.slinkyframework.environment.config.maven.plugin.deploy

import org.apache.maven.model.DeploymentRepository
import org.slinkyframework.environment.config.maven.plugin.AbstractMavenGoal
import org.slinkyframework.environment.config.maven.plugin.install.MavenInstaller

import java.io.File
import java.util.Properties

class MavenDeployer : AbstractMavenGoal
{
    private val props = Properties()

    constructor(projectDir: File, groupId: String, version: String, targetDir: File) : super(projectDir, groupId, version, targetDir)
    {
    }

    constructor(projectDir: File, groupId: String, version: String, targetDir: File, repository: DeploymentRepository) : super(projectDir, groupId, version, targetDir)
    {
        props.setProperty(PROPERTY_REPOSITORY_ID, repository.id)
        props.setProperty(PROPERTY_URL, repository.url)
    }

    override val goal: String
        get() = MAVEN_GOAL

    override fun getAdditionalProperties(zipFile: File): Properties
    {
        return props
    }

    companion object
    {
        private const val MAVEN_GOAL = "deploy:deploy-file"
        private const val PROPERTY_REPOSITORY_ID = "repositoryId"
        private const val PROPERTY_URL = "url"
    }
}
