package org.slinkyframework.environment.config.maven.plugin.config.templates

import org.slinkyframework.environment.config.maven.plugin.config.AbstractApplicationConfigFactory
import org.slinkyframework.environment.config.maven.plugin.config.ConfigPropertyMerger

import java.io.File
import java.util.Properties

class TemplateApplicationConfigFactory(sourceDir: File, targetDir: File, private val delimiters: Set<String>) : AbstractApplicationConfigFactory(sourceDir, targetDir)
{
    private var failOnMissingProperty = false

    fun setFailOnMissingProperty(failOnMissingProperty: Boolean)
    {
        this.failOnMissingProperty = failOnMissingProperty
    }

    override fun processDirectory(application: String, environment: String, sourceDir: File, targetDir: File)
    {
        val templatesDir = File(sourceDir, TEMPLATES_DIR)

        if (templatesDir.exists())
        {
            val app1Env1factory = ConfigPropertyMerger(baseDir, application, environment)
            val properties = app1Env1factory.properties

            val fileGenerator = FilterFileGenerator(targetDir, properties, delimiters)
            fileGenerator.setFailOnMissingProperty(failOnMissingProperty)

            val directoryWalker = TemplateDirectoryWalker(fileGenerator)
            directoryWalker.generate(templatesDir)
        }
    }

    companion object
    {
        const val TEMPLATES_DIR = "templates"
    }
}
