package org.slinkyframework.environment.config.maven.plugin.config.templates

import org.codehaus.plexus.interpolation.ValueSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class LoggingValueSource(private val valueSource: ValueSource) : ValueSource
{
    override fun getValue(expression: String): Any?
    {
        val value = valueSource.getValue(expression)

        if (value == null)
        {
            LOG.debug("Warning: Property not found '{}'", expression)
        }

        return value
    }

    override fun getFeedback(): List<*>
    {
        return valueSource.feedback
    }

    override fun clearFeedback()
    {
        valueSource.clearFeedback()
    }

    companion object
    {
        private val LOG = LoggerFactory.getLogger(LoggingValueSource::class.java)
    }
}
