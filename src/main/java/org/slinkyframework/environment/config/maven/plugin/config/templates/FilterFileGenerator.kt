package org.slinkyframework.environment.config.maven.plugin.config.templates

import org.apache.commons.io.FileUtils.forceMkdir
import org.apache.commons.io.IOUtils.copy
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.StringUtils.removeEndIgnoreCase
import org.codehaus.plexus.interpolation.PropertiesBasedValueSource
import org.codehaus.plexus.interpolation.SimpleRecursionInterceptor
import org.codehaus.plexus.interpolation.ValueSource
import org.codehaus.plexus.interpolation.multi.MultiDelimiterInterpolatorFilterReader
import org.codehaus.plexus.interpolation.multi.MultiDelimiterStringSearchInterpolator
import org.slf4j.LoggerFactory
import org.slinkyframework.environment.config.maven.plugin.config.EnvironmentConfigException
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.util.*
import java.util.function.Consumer

class FilterFileGenerator(private val targetDir: File, private val properties: Properties, private val delimiters: Set<String>) : FileGenerator {
    private var failOnMissingProperty = false

    fun setFailOnMissingProperty(failOnMissingProperty: Boolean) {
        this.failOnMissingProperty = failOnMissingProperty
    }

    override fun generateFile(templateFile: File) {
        try {
            val subDir = StringUtils.substringAfter(templateFile.parent, TemplateApplicationConfigFactory.TEMPLATES_DIR)
            val targetSubDir = File(targetDir, subDir)

            forceMkdir(targetSubDir)

            val targetFilename = removeEndIgnoreCase(templateFile.name, ".tmpl")
            val targetFile = File(targetSubDir, targetFilename)

            LOG.debug("Generating config file '{}' using properties {}", targetFile, properties)

            val interpolator = MultiDelimiterStringSearchInterpolator()
            interpolator.addValueSource(createValueSource())

            delimiters.forEach(Consumer<String> { interpolator.addDelimiterSpec(it) })

            FileReader(templateFile).use { reader ->
                MultiDelimiterInterpolatorFilterReader(
                        reader,
                        interpolator,
                        SimpleRecursionInterceptor()
                ).use { filteringReader ->
                    FileWriter(targetFile).use { writer ->
                        delimiters.forEach(Consumer<String> { filteringReader.addDelimiterSpec(it) })
                        copy(filteringReader, writer)
                    }
                }
            }
        } catch (e: IOException) {
            throw EnvironmentConfigException("Unable to filter file", e)
        }

    }

    private fun createValueSource(): ValueSource {
        return if (failOnMissingProperty) {
            ManadatoryValueSource(PropertiesBasedValueSource(properties))
        } else {
            LoggingValueSource(PropertiesBasedValueSource(properties))
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(FilterFileGenerator::class.java)
    }
}
