package org.slinkyframework.environment.config.maven.plugin.deploy

import org.apache.maven.model.DeploymentRepository
import org.slinkyframework.environment.config.maven.plugin.AbstractMavenGoal
import java.nio.file.Path
import java.util.*

class MavenDeployer : AbstractMavenGoal
{
    private val props = Properties()

    constructor(projectDir: Path, groupId: String, version: String, targetDir: Path) : super(projectDir, groupId, version, targetDir)
    {
    }

    constructor(projectDir: Path, groupId: String, version: String, targetDir: Path, repository: DeploymentRepository) : super(projectDir, groupId, version, targetDir)
    {
        props.setProperty(PROPERTY_REPOSITORY_ID, repository.id)
        props.setProperty(PROPERTY_URL, repository.url)
    }

    override val goal: String
        get() = MAVEN_GOAL

    override fun getAdditionalProperties(zipFile: Path): Properties
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
