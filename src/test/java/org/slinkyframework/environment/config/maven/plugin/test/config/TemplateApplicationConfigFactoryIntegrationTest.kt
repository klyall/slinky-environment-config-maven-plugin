package org.slinkyframework.environment.config.maven.plugin.test.config

import org.apache.commons.io.FileUtils
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.slinkyframework.environment.config.maven.plugin.config.ConfigFileFactory
import org.slinkyframework.environment.config.maven.plugin.config.templates.TemplateApplicationConfigFactory
import org.slinkyframework.environment.config.maven.plugin.test.matchers.FileDoesNotExistMatcher.Companion.fileDoesNotExist
import org.slinkyframework.environment.config.maven.plugin.test.matchers.FileExistsMatcher.Companion.fileExists
import org.slinkyframework.environment.config.maven.plugin.test.matchers.FileHasPropertyMatcher.Companion.hasProperty
import java.io.File
import java.io.IOException
import java.util.*

class TemplateApplicationConfigFactoryIntegrationTest {
    private val delimiters = LinkedHashSet<String>()

    @Before
    @Throws(IOException::class)
    fun setUp() {
        delimiters.add("{{*}}")
        val file = File(".")
        FileUtils.deleteQuietly(File(TARGET_DIR))
        val testee: ConfigFileFactory = TemplateApplicationConfigFactory(File(SOURCE_DIR), File(TARGET_DIR), delimiters)
        testee.generateFiles()
    }

    @Test
    @Throws(IOException::class)
    fun shouldGenerateGlobalFilesToAllApplicationsAndEnvironments() {
        Assert.assertThat(File("$TARGET_DIR/env1/app1/global-template1.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env2/app1/global-template1.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env1/app2/global-template1.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env2/app2/global-template1.conf"), fileExists())
    }

    @Test
    @Throws(IOException::class)
    fun shouldGenerateGlobalFilesInSubDirectoryToAllApplicationsAndEnvironments() {
        Assert.assertThat(File("$TARGET_DIR/env1/app1/sub/dir/global-template2.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env2/app1/sub/dir/global-template2.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env1/app2/sub/dir/global-template2.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env2/app2/sub/dir/global-template2.conf"), fileExists())
    }

    @Test
    @Throws(IOException::class)
    fun shouldGenerateApplicationFilesToAllEnvironmentsForSpecificApplication() {
        Assert.assertThat(File("$TARGET_DIR/env1/app1/application-template1.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env2/app1/application-template1.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env1/app2/application-template1.conf"), fileDoesNotExist())
        Assert.assertThat(File("$TARGET_DIR/env2/app2/application-template1.conf"), fileDoesNotExist())
    }

    @Test
    @Throws(IOException::class)
    fun shouldGenerateApplicationFilesFromSubDirectoryToAllEnvironmentsForSpecificApplication() {
        Assert.assertThat(File("$TARGET_DIR/env1/app1/sub/dir/application-template2.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env2/app1/sub/dir/application-template2.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env1/app2/sub/dir/application-template2.conf"), fileDoesNotExist())
        Assert.assertThat(File("$TARGET_DIR/env2/app2/sub/dir/application-template2.conf"), fileDoesNotExist())
    }

    @Test
    @Throws(IOException::class)
    fun shouldGenerateEnvironmentFilesToAllApplicationsForSpecificEnvironment() {
        Assert.assertThat(File("$TARGET_DIR/env1/app1/environment-template1.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env1/app2/environment-template1.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env2/app1/environment-template1.conf"), fileDoesNotExist())
        Assert.assertThat(File("$TARGET_DIR/env2/app2/environment-template1.conf"), fileDoesNotExist())
    }

    @Test
    @Throws(IOException::class)
    fun shouldGenerateEnvironmentFilesInSubDirectoryToAllApplicationsAForSpecificEnvironment() {
        Assert.assertThat(File("$TARGET_DIR/env1/app1/sub/dir/environment-template2.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env1/app2/sub/dir/environment-template2.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env2/app1/sub/dir/environment-template2.conf"), fileDoesNotExist())
        Assert.assertThat(File("$TARGET_DIR/env2/app2/sub/dir/environment-template2.conf"), fileDoesNotExist())
    }

    @Test
    @Throws(IOException::class)
    fun shouldGenerateApplicationEnvironmentFilesToAllSpecificApplicationEnvironment() {
        Assert.assertThat(File("$TARGET_DIR/env1/app1/application-environment-template1.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env1/app2/application-environment-template1.conf"), fileDoesNotExist())
        Assert.assertThat(File("$TARGET_DIR/env2/app1/application-environment-template1.conf"), fileDoesNotExist())
        Assert.assertThat(File("$TARGET_DIR/env2/app2/application-environment-template1.conf"), fileDoesNotExist())
    }

    @Test
    @Throws(IOException::class)
    fun shouldGenerateApplicationEnvironmentFilesInSubDirectoryToSpecificApplicationEnvironment() {
        Assert.assertThat(File("$TARGET_DIR/env1/app1/sub/dir/application-environment-template2.conf"), fileExists())
        Assert.assertThat(File("$TARGET_DIR/env1/app2/sub/dir/application-environment-template2.conf"), fileDoesNotExist())
        Assert.assertThat(File("$TARGET_DIR/env2/app1/sub/dir/application-environment-template2.conf"), fileDoesNotExist())
        Assert.assertThat(File("$TARGET_DIR/env2/app2/sub/dir/application-environment-template2.conf"), fileDoesNotExist())
    }

    @Test
    @Throws(IOException::class)
    fun shouldOverrideGlobalFileWithApplicationFile() {
        Assert.assertThat(File("$TARGET_DIR/env1/app1/application-template-override.conf"), hasProperty("source", CoreMatchers.equalTo("application")))
        Assert.assertThat(File("$TARGET_DIR/env2/app1/application-template-override.conf"), hasProperty("source", CoreMatchers.equalTo("application")))
        Assert.assertThat(File("$TARGET_DIR/env1/app2/application-template-override.conf"), hasProperty("source", CoreMatchers.equalTo("global")))
        Assert.assertThat(File("$TARGET_DIR/env2/app2/application-template-override.conf"), hasProperty("source", CoreMatchers.equalTo("global")))
    }

    @Test
    @Throws(IOException::class)
    fun shouldOverrideApplicationFileWithEnvironmentFile() {
        Assert.assertThat(File("$TARGET_DIR/env1/app1/environment-template-override.conf"), hasProperty("source", CoreMatchers.equalTo("environment")))
        Assert.assertThat(File("$TARGET_DIR/env1/app2/environment-template-override.conf"), hasProperty("source", CoreMatchers.equalTo("environment")))
        Assert.assertThat(File("$TARGET_DIR/env2/app1/environment-template-override.conf"), hasProperty("source", CoreMatchers.equalTo("application")))
        Assert.assertThat(File("$TARGET_DIR/env2/app2/environment-template-override.conf"), hasProperty("source", CoreMatchers.equalTo("global")))
    }

    @Test
    @Throws(IOException::class)
    fun shouldOverrideEnvironmentFileWithApplicationEnvironmentFile() {
        Assert.assertThat(File("$TARGET_DIR/env1/app1/application-environment-template-override.conf"), hasProperty("source", CoreMatchers.equalTo("app-env")))
        Assert.assertThat(File("$TARGET_DIR/env1/app2/application-environment-template-override.conf"), hasProperty("source", CoreMatchers.equalTo("environment")))
        Assert.assertThat(File("$TARGET_DIR/env2/app1/application-environment-template-override.conf"), hasProperty("source", CoreMatchers.equalTo("global")))
        Assert.assertThat(File("$TARGET_DIR/env2/app2/application-environment-template-override.conf"), hasProperty("source", CoreMatchers.equalTo("global")))
    }

    companion object {
        const val TARGET_DIR = "target/generated-config/"
        const val SOURCE_DIR = "src/test/resources"
    }
}
