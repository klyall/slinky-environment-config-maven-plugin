package org.slinkyframework.environment.config.maven.plugin.test.info

import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Test
import org.slinkyframework.environment.config.maven.plugin.info.ConfigDirectoryWalker
import java.io.File

class ConfigDirectoryWalkerIntegrationIntegrationTest {

    @Test
    fun shouldFindPropertyFiles() {
        val sourceDir = File("src/test/resources")
        val directoryWalker = ConfigDirectoryWalker()

        val results: List<*> = directoryWalker.walk(sourceDir)

        Assert.assertThat(results.size, Matchers.`is`(Matchers.equalTo(5)))
        Assert.assertThat(directoryWalker.calculateNumberOfPropertiesManaged(), Matchers.`is`(Matchers.equalTo(11)))
        Assert.assertThat(directoryWalker.calculateNumberOfConfigurationLines(), Matchers.`is`(Matchers.equalTo(15)))
    }
}
