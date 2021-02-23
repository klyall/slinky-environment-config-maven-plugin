package org.slinkyframework.environment.config.maven.plugin.test.matchers

import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import java.io.File

class FileDoesNotExistMatcher private constructor() : TypeSafeMatcher<File>() {

    override fun matchesSafely(file: File): Boolean {
        return !file.exists()
    }

    override fun describeTo(description: Description) {
        description.appendText("File should not exist")
    }

    override fun describeMismatchSafely(file: File, description: Description) {
        description.appendText("File exists: ")
        description.appendValue(file)
    }

    companion object {
        @JvmStatic
        fun fileDoesNotExist(): FileDoesNotExistMatcher {
            return FileDoesNotExistMatcher()
        }
    }
}
