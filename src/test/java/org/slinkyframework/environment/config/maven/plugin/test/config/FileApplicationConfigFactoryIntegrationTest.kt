package org.slinkyframework.environment.config.maven.plugin.test.config

import org.apache.commons.io.FileUtils
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.slinkyframework.environment.config.maven.plugin.config.ConfigFileFactory
import org.slinkyframework.environment.config.maven.plugin.config.files.FileApplicationConfigFactory
import org.slinkyframework.environment.config.maven.plugin.test.matchers.FileDoesNotExistMatcher.Companion.fileDoesNotExist
import org.slinkyframework.environment.config.maven.plugin.test.matchers.FileExistsMatcher.Companion.fileExists
import org.slinkyframework.environment.config.maven.plugin.test.matchers.FileHasPropertyMatcher.Companion.hasProperty
import java.io.File
import java.io.IOException

class FileApplicationConfigFactoryIntegrationTest {

    @Before
    @Throws(IOException::class)
    fun setUp() {
        FileUtils.deleteQuietly(File(TARGET_DIR))
        val testee: ConfigFileFactory = FileApplicationConfigFactory(File(SOURCE_DIR), File(TARGET_DIR))
        testee.generateFiles()
    }

    @Test
    @Throws(IOException::class)
    fun shouldCopyGlobalFilesToAllApplicationsAndEnvironments() {
        Assert.assertThat(File("$TARGET_DIR/env1/app1/global-file1.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env2/app1/global-file1.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env1/app2/global-file1.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env2/app2/global-file1.conf"), fileExists())
    }

    @Test
    @Throws(IOException::class)
    fun shouldCopyGlobalFilesInSubDirectoryToAllApplicationsAndEnvironments() {
        Assert.assertThat(File("$TARGET_DIR/env1/app1/sub/dir/global-file2.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env2/app1/sub/dir/global-file2.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env1/app2/sub/dir/global-file2.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env2/app2/sub/dir/global-file2.conf"), fileExists())
    }

    @Test
    @Throws(IOException::class)
    fun shouldCopyApplicationFilesToAllEnvironmentsForSpecificApplication() {
        Assert.assertThat(File("$TARGET_DIR/env1/app1/application-file1.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env2/app1/application-file1.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env1/app2/application-file1.conf"), fileDoesNotExist())
        Assert.assertThat(File("$TARGET_DIR/env2/app2/application-file1.conf"), fileDoesNotExist())
    }

    @Test
    @Throws(IOException::class)
    fun shouldCopyApplicationFilesFromSubDirectoryToAllEnvironmentsForSpecificApplication() {
        Assert.assertThat(File("$TARGET_DIR/env1/app1/sub/dir/application-file2.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env2/app1/sub/dir/application-file2.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env1/app2/sub/dir/application-file2.conf"), fileDoesNotExist())
        Assert.assertThat(File("$TARGET_DIR/env2/app2/sub/dir/application-file2.conf"), fileDoesNotExist())
    }

    @Test
    @Throws(IOException::class)
    fun shouldCopyEnvironmentFilesToAllApplicationsForSpecificEnvironment() {
        Assert.assertThat(File("$TARGET_DIR/env1/app1/environment-file1.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env1/app2/environment-file1.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env2/app1/environment-file1.conf"), fileDoesNotExist())
        Assert.assertThat(File("$TARGET_DIR/env2/app2/environment-file1.conf"), fileDoesNotExist())
    }

    @Test
    @Throws(IOException::class)
    fun shouldCopyEnvironmentFilesInSubDirectoryToAllApplicationsAForSpecificEnvironment() {
        Assert.assertThat(File("$TARGET_DIR/env1/app1/sub/dir/environment-file2.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env1/app2/sub/dir/environment-file2.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env2/app1/sub/dir/environment-file2.conf"), fileDoesNotExist())
        Assert.assertThat(File("$TARGET_DIR/env2/app2/sub/dir/environment-file2.conf"), fileDoesNotExist())
    }

    @Test
    @Throws(IOException::class)
    fun shouldCopyApplicationEnvironmentFilesToAllSpecificApplicationEnvironment() {
        Assert.assertThat(File("$TARGET_DIR/env1/app1/application-environment-file1.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env1/app2/application-environment-file1.conf"), fileDoesNotExist())
        Assert.assertThat(File("$TARGET_DIR/env2/app1/application-environment-file1.conf"), fileDoesNotExist())
        Assert.assertThat(File("$TARGET_DIR/env2/app2/application-environment-file1.conf"), fileDoesNotExist())
    }

    @Test
    @Throws(IOException::class)
    fun shouldCopyApplicationEnvironmentFilesInSubDirectoryToSpecificApplicationEnvironment() {
        Assert.assertThat(File("$TARGET_DIR/env1/app1/sub/dir/application-environment-file2.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env1/app2/sub/dir/application-environment-file2.conf"), fileDoesNotExist())
        Assert.assertThat(File("$TARGET_DIR/env2/app1/sub/dir/application-environment-file2.conf"), fileDoesNotExist())
        Assert.assertThat(File("$TARGET_DIR/env2/app2/sub/dir/application-environment-file2.conf"), fileDoesNotExist())
    }

    @Test
    @Throws(IOException::class)
    fun shouldOverrideGlobalFileWithApplicationFile() {
        Assert.assertThat(File("$TARGET_DIR/env1/app1/application-file-override.conf"), hasProperty("source", CoreMatchers.equalTo("application")))
        Assert.assertThat(File("$TARGET_DIR/env2/app1/application-file-override.conf"), hasProperty("source", CoreMatchers.equalTo("application")))
        Assert.assertThat(File("$TARGET_DIR/env1/app2/application-file-override.conf"), hasProperty("source", CoreMatchers.equalTo("global")))
        Assert.assertThat(File("$TARGET_DIR/env2/app2/application-file-override.conf"), hasProperty("source", CoreMatchers.equalTo("global")))
    }

    @Test
    @Throws(IOException::class)
    fun shouldOverrideApplicationFileWithEnvironmentFile() {
        Assert.assertThat(File("$TARGET_DIR/env1/app1/environment-file-override.conf"), hasProperty("source", CoreMatchers.equalTo("environment")))
        Assert.assertThat(File("$TARGET_DIR/env1/app2/environment-file-override.conf"), hasProperty("source", CoreMatchers.equalTo("environment")))
        Assert.assertThat(File("$TARGET_DIR/env2/app1/environment-file-override.conf"), hasProperty("source", CoreMatchers.equalTo("application")))
        Assert.assertThat(File("$TARGET_DIR/env2/app2/environment-file-override.conf"), hasProperty("source", CoreMatchers.equalTo("global")))
    }

    @Test
    @Throws(IOException::class)
    fun shouldOverrideEnvironmentFileWithApplicationEnvironmentFile() {
        Assert.assertThat(File("$TARGET_DIR/env1/app1/application-environment-file-override.conf"), hasProperty("source", CoreMatchers.equalTo("application-environment")))
        Assert.assertThat(File("$TARGET_DIR/env1/app2/application-environment-file-override.conf"), hasProperty("source", CoreMatchers.equalTo("environment")))
        Assert.assertThat(File("$TARGET_DIR/env2/app1/application-environment-file-override.conf"), hasProperty("source", CoreMatchers.equalTo("global")))
        Assert.assertThat(File("$TARGET_DIR/env2/app2/application-environment-file-override.conf"), hasProperty("source", CoreMatchers.equalTo("global")))
    }

    companion object {
        const val TARGET_DIR = "target/generated-config/"
        const val SOURCE_DIR = "src/test/resources"
    }
}
