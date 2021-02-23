package org.slinkyframework.environment.config.maven.plugin.config.templates

import java.io.File

interface FileGenerator {
    fun generateFile(templateFile: File)
}
