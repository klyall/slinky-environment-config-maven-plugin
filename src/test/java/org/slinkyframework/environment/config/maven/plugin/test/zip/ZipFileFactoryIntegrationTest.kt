package org.slinkyframework.environment.config.maven.plugin.test.zip

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.slinkyframework.environment.config.maven.plugin.test.matchers.FileExistsMatcher
import org.slinkyframework.environment.config.maven.plugin.zip.ZipFileFactory
import java.io.File

class ZipFileFactoryIntegrationTest {

    private val file1 = File("$TARGET_DIR/env1/app1-config-1.0.0.zip")
    private val file2 = File("$TARGET_DIR/env1/app2-config-1.0.0.zip")
    private val file3 = File("$TARGET_DIR/env2/app1-config-1.0.0.zip")
    private val file4 = File("$TARGET_DIR/env2/app2-config-1.0.0.zip")

    @Before
    fun setUp() {
        file1.delete()
        file2.delete()
        file3.delete()
        file4.delete()
    }

    @Test
    fun shouldCreateAZipFileForEachApplicationAndEnvironment() {
        val testee = ZipFileFactory(TARGET_DIR, VERSION)

        testee.createZipFiles()

        Assert.assertThat(file1, FileExistsMatcher.fileExists())
        Assert.assertThat(file2, FileExistsMatcher.fileExists())
        Assert.assertThat(file3, FileExistsMatcher.fileExists())
        Assert.assertThat(file4, FileExistsMatcher.fileExists())
    }

    @Test
    fun shouldNotThrowExceptionWhenNothingToZip() {
        val targetDir = File("does/not/exist")
        val testee = ZipFileFactory(targetDir, VERSION)

        testee.createZipFiles()
    }

    companion object {
        val TARGET_DIR = File("src/test/resources/package-test")
        private const val VERSION = "1.0.0"
    }
}
