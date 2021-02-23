package org.slinkyframework.environment.config.maven.plugin.test.matchers

import org.apache.commons.io.FileUtils
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import java.io.File
import java.io.IOException
import java.nio.charset.Charset

class FileHasContentMatcher private constructor(private val valueMatcher: Matcher<String>) : TypeSafeMatcher<File>() {

    override fun matchesSafely(file: File): Boolean {
        return if (file.exists()) {
            val contents = readFile(file)
            valueMatcher.matches(contents)
        } else {
            false
        }
    }

    override fun describeTo(description: Description) {
        description.appendText(String.format("File should have content "))
        description.appendDescriptionOf(valueMatcher)
    }

    override fun describeMismatchSafely(file: File, description: Description) {
        if (file.exists()) {
            val contents = readFile(file)
            description.appendText(String.format("File %s content ", file))
            valueMatcher.describeMismatch(contents, description)
        } else {
            description.appendText(String.format("File '%s' does not exist", file))
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
        @JvmStatic
        fun hasContent(valueMatcher: Matcher<String>): FileHasContentMatcher {
            return FileHasContentMatcher(valueMatcher)
        }
    }
}
