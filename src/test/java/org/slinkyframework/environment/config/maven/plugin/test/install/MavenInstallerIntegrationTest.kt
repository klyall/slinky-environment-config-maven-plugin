//package org.slinkyframework.environment.config.maven.plugin.test.install;
//
//import org.apache.commons.io.FileUtils;
//import org.apache.maven.artifact.Artifact;
//import org.apache.maven.execution.MavenSession;
//import org.apache.maven.model.building.StringModelSource;
//import org.apache.maven.project.*;
//import org.apache.maven.shared.transfer.project.install.ProjectInstaller;
//import org.apache.maven.shared.transfer.repository.RepositoryManager;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.slinkyframework.environment.config.maven.plugin.install.MavenInstaller;
//
//import java.io.File;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//import static org.junit.Assert.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.slinkyframework.environment.config.maven.plugin.test.matchers.FileExistsMatcher.fileExists;
//
//@RunWith(MockitoJUnitRunner.class)
//public class MavenInstallerIntegrationTest {
//
//    private static final String GROUP_ID = "org.slinkyframework.test.environments";
//    private static final String VERSION = "1.0.0";
//    private static final Path TARGET_DIR = new File("src/test/resources/install-test").toPath();
//    private static final String USER_DIR = System.getProperty("user.home");
//    private static final String MAVEN_HOME = "maven.home";
//
//    @Mock
//    private MavenSession mockSession;
//    @Mock
//    private ProjectBuilder mockProjectBuilder;
//    @Mock
//    private RepositoryManager mockRepositoryManager;
//    @Mock
//    private ProjectInstaller mockInstaller;
//    @Mock
//    private ProjectBuildingRequest mockProjectBuildingRequest;
//    @Mock
//    private MavenProject mockMavenProject;
//    @Mock
//    private ProjectBuildingResult mockProjectBuildingResult;
//    @Mock
//    private Artifact mockArtifact;
//
//    @Before
//    public void setUp() {
//        FileUtils.deleteQuietly(new File(USER_DIR + "/.m2/repository/org/slinkyframework/test"));
//    }
//
//    @Test
//    public void shouldInstallZipFilesIntoLocalRepo() throws Exception {
//
//        when(mockSession.getProjectBuildingRequest()).thenReturn(mockProjectBuildingRequest);
//        when(mockProjectBuilder.build(any(StringModelSource.class), any(ProjectBuildingRequest.class))).thenReturn(mockProjectBuildingResult);
//        when(mockProjectBuildingResult.getProject()).thenReturn(mockMavenProject);
//        when(mockMavenProject.getArtifact()).thenReturn(mockArtifact);
//
//        if (System.getProperty(MAVEN_HOME) == null) {
//            System.setProperty(MAVEN_HOME, "/usr/local/Cellar/maven/3.6.0/libexec");
//        }
//
//        Path projectDir = Paths.get(".");
//
//        MavenInstaller mavenInstaller = new MavenInstaller(
//                projectDir, GROUP_ID, VERSION, TARGET_DIR, mockSession, mockProjectBuilder, mockRepositoryManager, mockInstaller);
//
//        mavenInstaller.processEnvironments();
//
//        try {
//            Thread.sleep(4000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        assertThat(new File(USER_DIR + "/.m2/repository/org/slinkyframework/test/environments/env1/app1-config/1.0.0/app1-config-1.0.0.zip"), fileExists());
//        assertThat(new File(USER_DIR + "/.m2/repository/org/slinkyframework/test/environments/env1/app2-config/1.0.0/app2-config-1.0.0.zip"), fileExists());
//        assertThat(new File(USER_DIR + "/.m2/repository/org/slinkyframework/test/environments/env2/app1-config/1.0.0/app1-config-1.0.0.zip"), fileExists());
//        assertThat(new File(USER_DIR + "/.m2/repository/org/slinkyframework/test/environments/env2/app2-config/1.0.0/app2-config-1.0.0.zip"), fileExists());
//    }
//}
