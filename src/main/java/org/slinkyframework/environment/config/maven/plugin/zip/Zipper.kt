package org.slinkyframework.environment.config.maven.plugin.zip

import org.apache.commons.io.IOUtils.copy
import org.apache.commons.lang3.StringUtils.removeStart
import org.slf4j.LoggerFactory
import org.slinkyframework.environment.config.maven.plugin.config.EnvironmentConfigException
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object Zipper
{
    private val LOG = LoggerFactory.getLogger(Zipper::class.java)

    fun zipDirectory(sourceDir: File, outputFile: File)
    {
        if (!sourceDir.exists())
        {
            throw IllegalArgumentException("Directory does not exist: $sourceDir")
        }

        LOG.debug("Zipping directory {} into {}", sourceDir, outputFile)

        try
        {
            FileOutputStream(outputFile).use { fos ->
                ZipOutputStream(fos).use { zos ->

                    compressDirectoryToZipfile(sourceDir, sourceDir, zos)
                }
            }
        }
        catch (e: IOException)
        {
            throw EnvironmentConfigException(String.format("Error creating Zip file %s from %s", outputFile, sourceDir), e)
        }

    }

    @Throws(IOException::class)
    private fun compressDirectoryToZipfile(rootDir: File, sourceDir: File, zos: ZipOutputStream)
    {
        for (file in sourceDir.listFiles()!!)
        {

            val sb = StringBuilder()
            sb.append(removeStart(sourceDir.absolutePath.replace(rootDir.absolutePath, ""), "/"))
            sb.append("/")
            sb.append(file.name)

            if (file.isDirectory)
            {
                sb.append("/")
            }

            val name = removeStart(sb.toString(), "/")

            val entry = ZipEntry(name)
            zos.putNextEntry(entry)

            if (file.isDirectory)
            {
                compressDirectoryToZipfile(rootDir, File(sourceDir, file.name), zos)
            }
            else
            {
                FileInputStream(file).use { fis -> copy(fis, zos) }
            }
        }
    }
}
