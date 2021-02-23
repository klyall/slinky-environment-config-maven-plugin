package org.slinkyframework.environment.config.maven.plugin.test.matchers

import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import java.io.File

class FileExistsMatcher private constructor() : TypeSafeMatcher<File>() {

    override fun matchesSafely(file: File): Boolean {
        return file.exists()
    }

    override fun describeTo(description: Description) {
        description.appendText("File should exist")
    }

    override fun describeMismatchSafely(file: File, description: Description) {
        description.appendText("File does not exist: ")
        description.appendValue(file)
    }

    companion object {
        @JvmStatic
        fun fileExists(): FileExistsMatcher {
            return FileExistsMatcher()
        }
    }
}
