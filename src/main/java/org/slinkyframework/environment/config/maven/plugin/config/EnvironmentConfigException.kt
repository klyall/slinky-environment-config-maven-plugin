package org.slinkyframework.environment.config.maven.plugin.config

class EnvironmentConfigException : RuntimeException
{
    constructor(message: String) : super(message)
    {
    }

    constructor(message: String, cause: Throwable) : super(message, cause)
    {
    }
}
