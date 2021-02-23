package org.slinkyframework.environment.config.maven.plugin.test.matchers

import com.typesafe.config.ConfigFactory
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import java.io.File

class FileHasPropertyMatcher private constructor(private val propertyName: String, private val valueMatcher: Matcher<String>) : TypeSafeMatcher<File>() {

    override fun matchesSafely(file: File): Boolean {
        return if (file.exists()) {
            val config = ConfigFactory.parseFile(file)
            valueMatcher.matches(config.getString(propertyName))
        } else {
            false
        }
    }

    override fun describeTo(description: Description) {
        description.appendText(String.format("File should have property \"%s\" with value ", propertyName))
        description.appendDescriptionOf(valueMatcher)
    }

    override fun describeMismatchSafely(file: File, description: Description) {
        if (file.exists()) {
            val config = ConfigFactory.parseFile(file)
            description.appendText(String.format("File '%s' with property \"%s\" ", file, propertyName))
            valueMatcher.describeMismatch(config.getString(propertyName), description)
        } else {
            description.appendText(String.format("File '%s' does not exist", file))
        }
    }

    companion object {
        @JvmStatic
        fun hasProperty(propertyName: String, valueMatcher: Matcher<String>): FileHasPropertyMatcher {
            return FileHasPropertyMatcher(propertyName, valueMatcher)
        }
    }
}
