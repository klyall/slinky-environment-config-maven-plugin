package org.slinkyframework.environment.config.maven.plugin.config.templates

import org.apache.commons.io.DirectoryWalker
import org.slinkyframework.environment.config.maven.plugin.config.EnvironmentConfigException

import java.io.File
import java.io.IOException
import java.util.ArrayList

class TemplateDirectoryWalker(private val fileGenerator: FileGenerator) : DirectoryWalker<File>()
{
    fun generate(startDirectory: File): List<*>
    {
        val results = mutableListOf<File>()
        try
        {
            walk(startDirectory, results)
        }
        catch (e: IOException)
        {
            throw EnvironmentConfigException(String.format("Problem walking directory %s", startDirectory), e)
        }

        return results
    }

    protected override fun handleDirectory(directory: File?, depth: Int, results: Collection<File>): Boolean
    {
        return true
    }

    protected override fun handleFile(templateFile: File, depth: Int, results: MutableCollection<File>)
    {
        fileGenerator.generateFile(templateFile)
        results.add(templateFile)
    }
}
