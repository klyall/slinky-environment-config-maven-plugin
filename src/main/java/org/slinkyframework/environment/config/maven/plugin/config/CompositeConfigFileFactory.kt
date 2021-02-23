package org.slinkyframework.environment.config.maven.plugin.config

class CompositeConfigFileFactory(vararg factories: ConfigFileFactory) : ConfigFileFactory
{
    private val configFileFactories = ArrayList<ConfigFileFactory>()

    init
    {
        for (factory in factories)
        {
            configFileFactories.add(factory)
        }
    }

    override fun generateFiles()
    {
        for (configFileFactory in configFileFactories)
        {
            configFileFactory.generateFiles()
        }
    }
}
