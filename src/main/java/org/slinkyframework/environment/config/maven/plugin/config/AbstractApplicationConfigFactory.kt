package org.slinkyframework.environment.config.maven.plugin.config

import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileFilter

abstract class AbstractApplicationConfigFactory(protected val baseDir: File, protected var targetDir: File) : ConfigFileFactory
{
    protected abstract fun processDirectory(application: String, environment: String, sourceDir: File, targetDir: File)

    override fun generateFiles()
    {
        val applications = findApplications()
        val environments = findEnvironments()

        LOG.debug("Creating config for applications {} in environments {}", applications, environments)

        processGlobalFilesToAll(applications, environments)
        processApplicationFiles(applications, environments)
        processEnvironmentFiles(applications, environments)
        processApplicationEnvironmentFiles(applications, environments)
    }

    private fun findApplications(): List<File>
    {
        return listDirectories(APPLICATIONS_DIR)
    }

    private fun findEnvironments(): List<File>
    {
        return listDirectories(ENVIRONMENTS_DIR)
    }

    private fun listDirectories(dir: String): List<File>
    {
        val directory = File(baseDir, dir)
        val directories = directory.listFiles(FileFilter { it.isDirectory })

        return directories?.asList() ?: NO_FILES
    }

    private fun processApplicationFiles(applications: List<File>, environments: List<File>)
    {
        LOG.debug("Processing application config files")

        for (application in applications)
        {
            for (environment in environments)
            {
                val targetEnvironmentDir = File(targetDir, environment.name)
                val targetApplicationDir = File(targetEnvironmentDir, application.name)

                processDirectory(application.name, environment.name, application, targetApplicationDir)
            }
        }
    }

    private fun processApplicationEnvironmentFiles(applications: List<File>, environments: List<File>)
    {
        LOG.debug("Processing application/environment config files")

        for (environment in environments)
        {
            for (application in applications)
            {

                val applicationDir = File(environment, APPLICATIONS_DIR)
                val applicationEnvironmentDir = File(applicationDir, application.name)

                val targetEnvironmentDir = File(targetDir, environment.name)
                val targetApplicationDir = File(targetEnvironmentDir, application.name)

                processDirectory(application.name, environment.name, applicationEnvironmentDir, targetApplicationDir)
            }
        }
    }

    private fun processEnvironmentFiles(applications: List<File>, environments: List<File>)
    {
        LOG.debug("Processing environment config files")

        for (environment in environments)
        {
            for (application in applications)
            {
                val targetEnvironmentDir = File(targetDir, environment.name)
                val targetApplicationDir = File(targetEnvironmentDir, application.name)

                processDirectory(application.name, environment.name, environment, targetApplicationDir)
            }
        }
    }

    private fun processGlobalFilesToAll(applications: List<File>, environments: List<File>)
    {
        LOG.debug("Processing global config files")

        val globalDir = File(baseDir, GLOBAL_DIR)

        for (application in applications)
        {
            for (environment in environments)
            {
                val targetEnvironmentDir = File(targetDir, environment.name)
                val targetApplicationDir = File(targetEnvironmentDir, application.name)

                processDirectory(application.name, environment.name, globalDir, targetApplicationDir)
            }
        }
    }

    companion object
    {
        private val LOG = LoggerFactory.getLogger(AbstractApplicationConfigFactory::class.java)
        private val NO_FILES = listOf<File>()

        private const val APPLICATIONS_DIR = "applications"
        private const val ENVIRONMENTS_DIR = "environments"
        private const val GLOBAL_DIR = "global"
    }
}
