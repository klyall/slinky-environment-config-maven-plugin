//package org.slinkyframework.environment.config.maven.plugin.test.install
//
//import org.apache.commons.io.FileUtils
//import org.apache.maven.artifact.Artifact
//import org.apache.maven.execution.MavenSession
//import org.apache.maven.model.building.StringModelSource
//import org.apache.maven.project.MavenProject
//import org.apache.maven.project.ProjectBuilder
//import org.apache.maven.project.ProjectBuildingRequest
//import org.apache.maven.project.ProjectBuildingResult
//import org.apache.maven.shared.transfer.project.install.ProjectInstaller
//import org.apache.maven.shared.transfer.repository.RepositoryManager
//import org.junit.Assert
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.mockito.ArgumentMatchers
//import org.mockito.Mock
//import org.mockito.Mockito
//import org.mockito.junit.MockitoJUnitRunner
//import org.slinkyframework.environment.config.maven.plugin.install.MavenInstaller
//import org.slinkyframework.environment.config.maven.plugin.test.matchers.FileExistsMatcher.Companion.fileExists
//import java.io.File
//import java.nio.file.Paths
//
//@RunWith(MockitoJUnitRunner::class)
//class MavenInstallerIntegrationTest {
//    @Mock
//    private val mockSession: MavenSession? = null
//
//    @Mock
//    private val mockProjectBuilder: ProjectBuilder? = null
//
//    @Mock
//    private val mockRepositoryManager: RepositoryManager? = null
//
//    @Mock
//    private val mockInstaller: ProjectInstaller? = null
//
//    @Mock
//    private val mockProjectBuildingRequest: ProjectBuildingRequest? = null
//
//    @Mock
//    private val mockMavenProject: MavenProject? = null
//
//    @Mock
//    private val mockProjectBuildingResult: ProjectBuildingResult? = null
//
//    @Mock
//    private val mockArtifact: Artifact? = null
//    @Before
//    fun setUp() {
//        FileUtils.deleteQuietly(File(USER_DIR + "/.m2/repository/org/slinkyframework/test"))
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun shouldInstallZipFilesIntoLocalRepo() {
//        Mockito.`when`(mockSession!!.projectBuildingRequest).thenReturn(mockProjectBuildingRequest)
//        Mockito.`when`(mockProjectBuilder!!.build(ArgumentMatchers.any(StringModelSource::class.java), ArgumentMatchers.any(ProjectBuildingRequest::class.java))).thenReturn(mockProjectBuildingResult)
//        Mockito.`when`(mockProjectBuildingResult!!.project).thenReturn(mockMavenProject)
//        Mockito.`when`(mockMavenProject!!.artifact).thenReturn(mockArtifact)
//        if (System.getProperty(MAVEN_HOME) == null) {
//            System.setProperty(MAVEN_HOME, "/usr/local/Cellar/maven/3.6.0/libexec")
//        }
//        val projectDir = Paths.get(".")
//        val mavenInstaller = MavenInstaller(
//                projectDir, GROUP_ID, VERSION, TARGET_DIR, mockSession, mockProjectBuilder, mockRepositoryManager!!, mockInstaller!!)
//        mavenInstaller.processEnvironments()
//        try {
//            Thread.sleep(4000)
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//        }
//        Assert.assertThat(File(USER_DIR + "/.m2/repository/org/slinkyframework/test/environments/env1/app1-config/1.0.0/app1-config-1.0.0.zip"), fileExists())
//        Assert.assertThat(File(USER_DIR + "/.m2/repository/org/slinkyframework/test/environments/env1/app2-config/1.0.0/app2-config-1.0.0.zip"), fileExists())
//        Assert.assertThat(File(USER_DIR + "/.m2/repository/org/slinkyframework/test/environments/env2/app1-config/1.0.0/app1-config-1.0.0.zip"), fileExists())
//        Assert.assertThat(File(USER_DIR + "/.m2/repository/org/slinkyframework/test/environments/env2/app2-config/1.0.0/app2-config-1.0.0.zip"), fileExists())
//    }
//
//    companion object {
//        private const val GROUP_ID = "org.slinkyframework.test.environments"
//        private const val VERSION = "1.0.0"
//        private val TARGET_DIR = File("src/test/resources/install-test").toPath()
//        private val USER_DIR = System.getProperty("user.home")
//        private const val MAVEN_HOME = "maven.home"
//    }
//}
