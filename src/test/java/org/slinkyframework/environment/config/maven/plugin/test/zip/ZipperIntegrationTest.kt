package org.slinkyframework.environment.config.maven.plugin.test.zip

import org.junit.Assert
import org.junit.Test
import org.slinkyframework.environment.config.maven.plugin.test.matchers.ZipHasEntryMatcher
import org.slinkyframework.environment.config.maven.plugin.zip.Zipper.zipDirectory
import java.io.File

class ZipperIntegrationTest {

    @Test
    fun shouldCreateAZipWithRootFiles() {
        zipDirectory(SOURCE_DIR, TARGET_FILE)

        Assert.assertThat(TARGET_FILE, ZipHasEntryMatcher.hasEntry("file1.conf"))
    }

    @Test
    fun shouldCreateAZipWithFileInSubFolder() {
        zipDirectory(SOURCE_DIR, TARGET_FILE)

        Assert.assertThat(TARGET_FILE, ZipHasEntryMatcher.hasEntry("sub/"))
        Assert.assertThat(TARGET_FILE, ZipHasEntryMatcher.hasEntry("sub/dir/"))
        Assert.assertThat(TARGET_FILE, ZipHasEntryMatcher.hasEntry("sub/dir/file2.conf"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun shoudlThrowAnExceptionIfSourceDirDoesNotExist() {
        val sourceDir = File("does/not/exist")

        zipDirectory(sourceDir, TARGET_FILE)
    }

    companion object {
        private val SOURCE_DIR = File("src/test/resources/zipper-test")
        private val TARGET_FILE = File("target/zipper-test.zip")
    }
}
