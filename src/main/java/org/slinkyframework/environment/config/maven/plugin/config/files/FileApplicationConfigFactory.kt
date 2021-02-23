package org.slinkyframework.environment.config.maven.plugin.config.files

import org.apache.commons.io.FileUtils.copyDirectory
import org.slf4j.LoggerFactory
import org.slinkyframework.environment.config.maven.plugin.config.AbstractApplicationConfigFactory
import org.slinkyframework.environment.config.maven.plugin.config.EnvironmentConfigException
import java.io.File
import java.io.IOException

class FileApplicationConfigFactory(sourceDir: File, targetDir: File) : AbstractApplicationConfigFactory(sourceDir, targetDir)
{
    override fun processDirectory(application: String, environment: String, sourceDir: File, targetDir: File)
    {
        val filesDir = File(sourceDir, FILES_DIR)

        if (filesDir.exists())
        {
            try
            {
                LOG.debug("Copying directory {} to {}", filesDir, targetDir)

                copyDirectory(filesDir, targetDir)
            }
            catch (e: IOException)
            {
                throw EnvironmentConfigException(String.format("Problem copying from %s to %s", filesDir, targetDir), e)
            }

        }
    }

    companion object
    {
        const val FILES_DIR = "files"

        private val LOG = LoggerFactory.getLogger(FileApplicationConfigFactory::class.java)
    }
}
