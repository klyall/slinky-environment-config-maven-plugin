package org.slinkyframework.environment.config.maven.plugin.config.templates

import org.codehaus.plexus.interpolation.ValueSource
import org.slinkyframework.environment.config.maven.plugin.config.EnvironmentConfigException

import java.lang.String.format

class ManadatoryValueSource(private val valueSource: ValueSource) : ValueSource
{
    override fun getValue(expression: String): Any
    {
        return valueSource.getValue(expression)
                ?: throw EnvironmentConfigException(format("The property '%s' is missing from the configuration files", expression))
    }

    override fun getFeedback(): List<*>
    {
        return valueSource.feedback
    }

    override fun clearFeedback()
    {
        valueSource.clearFeedback()
    }
}
