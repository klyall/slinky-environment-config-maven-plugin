package org.slinkyframework.environment.config.maven.plugin.test.config

import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.slinkyframework.environment.config.maven.plugin.config.CompositeConfigFileFactory
import org.slinkyframework.environment.config.maven.plugin.config.ConfigFileFactory
import org.slinkyframework.environment.config.maven.plugin.config.files.FileApplicationConfigFactory
import org.slinkyframework.environment.config.maven.plugin.config.templates.TemplateApplicationConfigFactory
import org.slinkyframework.environment.config.maven.plugin.test.matchers.FileHasPropertyMatcher.Companion.hasProperty
import java.io.File
import java.util.*

class CompositeConfigFileFactoryIntegrationTest {

    private val delimiters = LinkedHashSet<String>()

    @Before
    fun setUp() {
        delimiters.add("{{*}}")
    }

    @Test
    fun shouldOverwriteCopiedFileWithAGeneratedTemplate() {
        // Given ...
        val fileConfigFileFactory: ConfigFileFactory = FileApplicationConfigFactory(SOURCE_DIR, TARGET_DIR)
        val templateConfigFileFactory: ConfigFileFactory = TemplateApplicationConfigFactory(SOURCE_DIR, TARGET_DIR, delimiters)
        val configFileFactory: ConfigFileFactory = CompositeConfigFileFactory(fileConfigFileFactory, templateConfigFileFactory)

        // When ...
        configFileFactory.generateFiles()

        // Then ...
        Assert.assertThat(File(TARGET_DIR, "env1/app1/global-template-override.conf"), hasProperty("type", CoreMatchers.equalTo("generated")))
        Assert.assertThat(File(TARGET_DIR, "env1/app2/global-template-override.conf"), hasProperty("type", CoreMatchers.equalTo("generated")))
        Assert.assertThat(File(TARGET_DIR, "env2/app1/global-template-override.conf"), hasProperty("type", CoreMatchers.equalTo("generated")))
        Assert.assertThat(File(TARGET_DIR, "env2/app2/global-template-override.conf"), hasProperty("type", CoreMatchers.equalTo("generated")))
    }

    companion object {
        val TARGET_DIR = File("target/generated-config/")
        val SOURCE_DIR = File("src/test/resources")
    }
}
