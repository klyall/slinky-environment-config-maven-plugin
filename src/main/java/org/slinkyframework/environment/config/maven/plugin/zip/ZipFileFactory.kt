package org.slinkyframework.environment.config.maven.plugin.zip

import org.slinkyframework.environment.config.maven.plugin.zip.Zipper.zipDirectory
import java.io.File
import java.io.FileFilter

class ZipFileFactory(private val targetDir: File, private val version: String) {
    fun createZipFiles() {
        val environmentDirs = targetDir.listFiles(FileFilter { it.isDirectory })

        if (environmentDirs != null) {
            for (environmentDir in environmentDirs) {
                processEnvironment(environmentDir)
            }
        }
    }

    private fun processEnvironment(environmentDir: File) {
        val applictionDirs = environmentDir.listFiles(FileFilter { it.isDirectory })

        for (applictionDir in applictionDirs) {
            val zipFile = File(environmentDir, String.format("%s-config-%s.zip", applictionDir.name, version))

            zipDirectory(applictionDir, zipFile)
        }
    }
}
