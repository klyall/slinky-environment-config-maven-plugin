package org.slinkyframework.environment.config.maven.plugin.info

import org.apache.commons.io.DirectoryWalker
import org.slf4j.LoggerFactory
import org.slinkyframework.environment.config.maven.plugin.config.EnvironmentConfigException
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.lang.String.format
import java.util.*

class ConfigDirectoryWalker : DirectoryWalker<File>() {
    private val allPropertyNames = mutableSetOf<String>()

    private val globalProperties = mutableMapOf<String, String>()
    private val applicationProperties = mutableMapOf<String, Map<String, String>>()
    private val environmentProperties = mutableMapOf<String, Map<String, String>>()
    private val applicationEnvironmentProperties = mutableMapOf<String, MutableMap<String, Map<String, String>>>()
    private var level1: String? = null
    private var level2: String? = null

    fun walk(startDirectory: File): List<File> {
        val results = mutableListOf<File>()

        try {
            walk(startDirectory, results)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

        logPropertiesFound()

        return results
    }

    override fun handleDirectory(dir: File, depth: Int, results: Collection<File>): Boolean {
        if (depth == 1) {
            level1 = dir.name
        } else if (depth == 2) {
            level2 = dir.name
        }

        return (depth == 0
                || depth == 1 && (dir.name == GLOBAL || dir.name == APPLICATIONS || dir.name == ENVIRONMENTS)
                || depth > 1)
    }

    override fun handleFile(file: File, depth: Int, results: MutableCollection<File>) {
        if (!file.name.endsWith(".properties")) {
            return
        }

        if (file.name == "global.properties") {
            globalProperties.putAll(loadProperties(file))

            globalProperties.let { allPropertyNames.addAll(it.keys) }

        } else if (depth == 3 && level1 == APPLICATIONS) {
            val properties = loadProperties(file)
            applicationProperties[level2!!] = properties
            allPropertyNames.addAll(properties.keys)

        } else if (depth == 3 && level1 == ENVIRONMENTS) {
            val properties = loadProperties(file)
            environmentProperties[level2!!] = properties
            allPropertyNames.addAll(properties.keys)

        } else if (depth == 5 && level1 == ENVIRONMENTS) {
            val properties = loadProperties(file)
            applicationEnvironmentProperties.putIfAbsent(level2!!, mutableMapOf())
            applicationEnvironmentProperties[level2!!]?.put(file.parentFile.name, properties)
            allPropertyNames.addAll(properties.keys)
        }

        results.add(file)
    }

    private fun loadProperties(file: File): Map<String, String> {
        val properties = Properties()

        try {
            if (file.exists()) {
                FileReader(file).use { fr -> properties.load(fr) }
            } else {
                LOG.debug("WARNING: Properties file '{}' does not exist and will not be loaded", file.absolutePath)
            }
        } catch (e: IOException) {
            throw EnvironmentConfigException(format("Unable to load properties file %s", file), e)
        }

        val mapOfProperties = mutableMapOf<String, String>()

        properties.forEach { (k, v) -> mapOfProperties[k.toString()] = v.toString() }

        return mapOfProperties
    }

    private fun logPropertiesFound() {
        LOG.info("{} {} {} {}={}", format("%-15s", "Property Type"), format("%-20s", "Environment"), format("%-30s", "Application"), "key", "value")
        LOG.info("{} {} {} {}={}", format("%-15s", "============="), format("%-20s", "==========="), format("%-30s", "==========="), "===", "=====")

        for (propertyName in allPropertyNames.sortedBy { p -> p }) {
            if (globalProperties.containsKey(propertyName)) {
                logProperty("GLOBAL", "", "", propertyName, globalProperties[propertyName])
            }

            for ((key, properties) in applicationProperties) {
                if (properties.containsKey(propertyName)) {
                    logProperty("APPLICATION", "", key, propertyName, properties[propertyName])
                }
            }

            for ((key, properties) in environmentProperties) {
                if (properties.containsKey(propertyName)) {
                    logProperty("ENVIRONMENT", key, "", propertyName, properties[propertyName])
                }
            }

            for ((key, environments) in applicationEnvironmentProperties) {
                for ((key1, properties) in environments) {
                    if (properties.containsKey(propertyName)) {
                        logProperty("APPL_ENV", key, key1, propertyName, properties[propertyName])
                    }
                }
            }
        }

        LOG.info("Number of unique properties   : {}", format("%,7d", calculateNumberOfPropertiesManaged()))
        LOG.info("Number of configuration lines : {}", format("%,7d", calculateNumberOfConfigurationLines()))
    }

    private fun logProperty(propertyType: String, environmentName: String, applicationName: String, propertyName: Any, value: String?) {
        LOG.info("{} {} {} {}={}", format("%-15s", propertyType), format("%-20s", environmentName), format("%-30s", applicationName), propertyName, value)
    }

    fun calculateNumberOfPropertiesManaged(): Int {
        return allPropertyNames.size
    }

    fun calculateNumberOfConfigurationLines(): Int {
        val globalPropertyCount = globalProperties.size
        val applicationCount = countConfigLines(applicationProperties)
        val environmentCount = countConfigLines(environmentProperties)
        val applicationEnvironmentCount = applicationEnvironmentProperties.values.stream()
                .mapToInt { this.countConfigLines(it) }
                .sum()

        return (globalPropertyCount
                + applicationCount
                + environmentCount
                + applicationEnvironmentCount)
    }

    private fun countConfigLines(map: Map<String, Map<String, String>>): Int {
        return map.values.stream()
                .mapToInt { it.size }
                .sum()
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(ConfigDirectoryWalker::class.java)

        private const val GLOBAL = "global"
        private const val APPLICATIONS = "applications"
        private const val ENVIRONMENTS = "environments"
    }
}
