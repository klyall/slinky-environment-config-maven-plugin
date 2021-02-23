package org.slinkyframework.environment.config.maven.plugin.test.matchers

import org.apache.commons.io.FileUtils
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.util.zip.ZipFile

class ZipHasEntryMatcher private constructor(private val entry: String) : TypeSafeMatcher<File>() {

    override fun matchesSafely(file: File): Boolean {
        return if (file.exists()) {
            try {
                val zipFile = ZipFile(file)
                zipFile.getEntry(entry) != null
            } catch (e: IOException) {
                false
            }
        } else {
            false
        }
    }

    override fun describeTo(description: Description) {
        description.appendText(String.format("ZipFile should have entry "))
        description.appendValue(entry)
    }

    override fun describeMismatchSafely(file: File, description: Description) {
        if (file.exists()) {
            val contents = readFile(file)
            description.appendText("does not exists")
        } else {
            description.appendText(String.format("ZipFile '%s' does not exist", file))
        }
    }

    private fun readFile(file: File): String {
        return try {
            FileUtils.readFileToString(file, Charset.defaultCharset())
        } catch (e: IOException) {
            ""
        }
    }

    companion object {
        fun hasEntry(valueMatcher: String): ZipHasEntryMatcher {
            return ZipHasEntryMatcher(valueMatcher)
        }
    }
}
