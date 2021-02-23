package org.slinkyframework.environment.config.maven.plugin.config

import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.lang.String.format
import java.util.*

class ConfigPropertyMerger(sourceDir: File, application: String, environment: String) {
    var properties = Properties()
        internal set

    init {
        val globalFile = File(sourceDir, "/global/global.properties")
        val applicationFile = File(sourceDir, format("applications/%s/application.properties", application))
        val environmentFile = File(sourceDir, format("environments/%s/environment.properties", environment))
        val appEnvFile = File(sourceDir, format("environments/%s/applications/%s/application.properties", environment, application))

        if (LOG.isDebugEnabled) {
            logLoadingOfConfigFiles(globalFile, applicationFile, environmentFile, appEnvFile)
        }

        loadProperties(properties, globalFile)
        loadProperties(properties, applicationFile)
        loadProperties(properties, environmentFile)
        loadProperties(properties, appEnvFile)
    }

    private fun loadProperties(properties: Properties, file: File) {
        try {
            if (file.exists()) {
                FileReader(file).use { fr -> properties.load(fr) }
            } else {
                LOG.debug("WARNING: Properties file '{}' does not exist and will not be loaded", file.absolutePath)
            }
        } catch (e: IOException) {
            throw EnvironmentConfigException(format("Unable to load properties file %s", file), e)
        }
    }

    private fun logLoadingOfConfigFiles(globalFile: File, applicationFile: File, environmentFile: File, appEnvFile: File) {
        logFile("global", globalFile)
        logFile("application", applicationFile)
        logFile("environment", environmentFile)
        logFile("application environment", appEnvFile)
    }

    private fun logFile(configType: String, file: File) {
        LOG.debug("Loading {} config file '{}'", configType, file)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(ConfigPropertyMerger::class.java)
    }
}
