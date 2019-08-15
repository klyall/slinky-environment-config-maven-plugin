package org.slinkyframework.environment.config.maven.plugin.deploy

import org.apache.maven.artifact.Artifact
import org.apache.maven.artifact.repository.ArtifactRepository
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy
import org.apache.maven.artifact.repository.MavenArtifactRepository
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout
import org.apache.maven.execution.MavenSession
import org.apache.maven.model.DeploymentRepository
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.project.ProjectBuilder
import org.apache.maven.shared.transfer.artifact.deploy.ArtifactDeployer
import org.apache.maven.shared.transfer.artifact.deploy.ArtifactDeployerException
import org.apache.maven.shared.transfer.repository.RepositoryManager
import org.codehaus.plexus.util.StringUtils
import org.slf4j.LoggerFactory
import org.slinkyframework.environment.config.maven.plugin.AbstractMavenGoal
import java.io.File
import java.nio.file.Path
import org.apache.maven.project.artifact.ProjectArtifactMetadata

class MavenDeployer(projectDir: Path, groupId: String, version: String, targetDir: Path,
                     private val repository: DeploymentRepository,
                     session: MavenSession,
                     projectBuilder: ProjectBuilder,
                     repositoryManager: RepositoryManager,
                     private val deployer: ArtifactDeployer
) : AbstractMavenGoal(
        projectDir, groupId, version, targetDir, session, projectBuilder, repositoryManager)
{
    override val goal: String
        get() = MAVEN_GOAL

    override suspend fun execute(groupId: String, artifactId: String, version: String, file: File)
    {
        if (!file.exists())
        {
            val message = "The specified file '${file.path}' does not exist"
            LOG.error(message)
            throw MojoFailureException(message)
        }

        val deploymentRepository = createArtifactRepository()

        val project = createMavenProject(groupId, artifactId, version)
        val artifact = project.artifact

        if (file == getLocalRepoFile(groupId, artifactId, version))
        {
            throw MojoFailureException("Cannot deploy artifact from the local repository: $file")
        }

        artifact.file = file

        val pom = generatePomFile(groupId, artifactId, version)

        val metadata = ProjectArtifactMetadata(artifact, pom)
        artifact.addMetadata(metadata)

        artifact.repository = deploymentRepository

        val deployableArtifacts = listOf<Artifact>(artifact)

        deployFiles(deploymentRepository, deployableArtifacts)
    }

    private fun deployFiles(deploymentRepository: ArtifactRepository, deployableArtifacts: List<Artifact>)
    {
        try
        {
            val buildingRequest = session.projectBuildingRequest

            deployer.deploy(buildingRequest, deploymentRepository, deployableArtifacts)
        }
        catch (e: ArtifactDeployerException)
        {
            throw MojoExecutionException(e.message, e)
        }
    }

    private fun createArtifactRepository(): ArtifactRepository
    {
        val deploymentRepository = MavenArtifactRepository(
                repository.id, repository.url, DefaultRepositoryLayout(), ArtifactRepositoryPolicy(), ArtifactRepositoryPolicy())

        LOG.debug("Artifact repository configured for {}:{}", repository.id, repository.url)

        val protocol = deploymentRepository.protocol

        if (StringUtils.isEmpty(protocol))
        {
            throw MojoExecutionException("No transfer protocol found.")
        }

        return deploymentRepository
    }

    companion object
    {
        private const val MAVEN_GOAL = "deploy"
        private val LOG = LoggerFactory.getLogger(MavenDeployer::class.java)
    }
}
