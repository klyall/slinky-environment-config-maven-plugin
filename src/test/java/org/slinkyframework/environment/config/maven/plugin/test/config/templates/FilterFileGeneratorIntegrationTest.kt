package org.slinkyframework.environment.config.maven.plugin.test.config.templates

import org.apache.commons.io.FileUtils
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.slinkyframework.environment.config.maven.plugin.config.EnvironmentConfigException
import org.slinkyframework.environment.config.maven.plugin.config.templates.FilterFileGenerator
import org.slinkyframework.environment.config.maven.plugin.test.matchers.FileExistsMatcher.Companion.fileExists
import org.slinkyframework.environment.config.maven.plugin.test.matchers.FileHasContentMatcher.Companion.hasContent
import java.io.File
import java.io.IOException
import java.util.*

class FilterFileGeneratorIntegrationTest {

    private val delimiters = LinkedHashSet<String>()

    @Before
    @Throws(IOException::class)
    fun setUp() {
        FileUtils.deleteDirectory(File(FILE1_TXT))
        FileUtils.deleteDirectory(File(FILE2_TXT))
        FileUtils.deleteDirectory(File(FILE3_TXT))
        FileUtils.deleteDirectory(File(FILE4_TXT))

        delimiters.add("{{*}}")
    }

    @Test
    fun shouldGenerateAFile() {
        // Given
        val targetDir = File("target")
        val templateFile = File("src/test/resources/file1.txt.tmpl")
        val properties = Properties()
        properties["message"] = "success"

        val generator = FilterFileGenerator(targetDir, properties, delimiters)

        // When
        generator.generateFile(templateFile)

        // Then
        val generatedFile = File(targetDir, FILE1_TXT)
        Assert.assertThat(generatedFile, fileExists())
        Assert.assertThat(generatedFile, hasContent(Matchers.equalTo("success")))
    }

    @Test
    fun shouldGenerateAFileWithPropertiesWithDots() {
         // Given
        val targetDir = File("target")
        val templateFile = File("src/test/resources/file2.txt.tmpl")
        val message = "still successful"

        val properties = Properties()
        properties["another.message"] = message

        val generator = FilterFileGenerator(targetDir, properties, delimiters)

        // When
        generator.generateFile(templateFile)

        // Then
        val generatedFile = File(targetDir, FILE2_TXT)
        Assert.assertThat(generatedFile, fileExists())
        Assert.assertThat(generatedFile, hasContent(Matchers.equalTo(message)))
    }

    @Test
    fun shouldGenerateAFileWithPropertiesWithDotsAndValuesAtEachLevel() {
        // Given ...
        val targetDir = File("target")
        val templateFile = File("src/test/resources/file3.txt.tmpl")
        val message = "level1=level1\nlevel2=level2\n"

        val properties = Properties()
        properties["level1"] = "level1"
        properties["level1.level2"] = "level2"

        val generator = FilterFileGenerator(targetDir, properties, delimiters)

        // When ...
        generator.generateFile(templateFile)

        // Then ...
        val generatedFile = File(targetDir, FILE3_TXT)
        Assert.assertThat(generatedFile, fileExists())
        Assert.assertThat(generatedFile, hasContent(Matchers.equalTo(message)))
    }

    @Test(expected = EnvironmentConfigException::class)
    fun shouldThrowAnExceptionIfMissingAProperty() {
        // Given
        val targetDir = File("target")
        val templateFile = File("src/test/resources/file4.txt.tmpl")
        val properties = Properties()

        val generator = FilterFileGenerator(targetDir, properties, delimiters)
        generator.setFailOnMissingProperty(true)

        // When
        generator.generateFile(templateFile)
    }

    companion object {
        const val FILE1_TXT = "file1.txt"
        const val FILE2_TXT = "file2.txt"
        const val FILE3_TXT = "file3.txt"
        const val FILE4_TXT = "file4.txt"
    }
}
