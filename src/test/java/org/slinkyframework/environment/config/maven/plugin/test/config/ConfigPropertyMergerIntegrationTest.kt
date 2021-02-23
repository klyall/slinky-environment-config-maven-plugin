package org.slinkyframework.environment.config.maven.plugin.test.config

import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.slinkyframework.environment.config.maven.plugin.config.ConfigPropertyMerger
import java.io.File
import java.util.*

class ConfigPropertyMergerIntegrationTest {

    private val app1 = "app1"
    private val app2 = "app2"
    private val environment = "env1"
    private val baseDir = File("src/test/resources")
    private val app1Env1Properties = ConfigPropertyMerger(baseDir, app1, environment).properties
    private val app2Env1Properties = ConfigPropertyMerger(baseDir, app2, environment).properties

    @Test
    fun shouldGetGlobalDefinedProperty() {
        Assert.assertThat("Property", app1Env1Properties["global.property"], CoreMatchers.`is`(CoreMatchers.equalTo<Any?>("global")))
    }

    @Test
    fun shouldGetGlobalDefinedPropertyAtRootLevel() {
        Assert.assertThat("Property", app1Env1Properties["global"], CoreMatchers.`is`(CoreMatchers.equalTo<Any?>("global")))
    }

    @Test
    fun shouldGetApplicationOnlyProperty() {
        Assert.assertThat("Property", app1Env1Properties["application.only"], CoreMatchers.`is`(CoreMatchers.equalTo<Any?>("application")))
    }

    @Test
    fun shouldGetApplicationOverridenProperty() {
        Assert.assertThat("Property", app1Env1Properties["application.override"], CoreMatchers.`is`(CoreMatchers.equalTo<Any?>("application")))
    }

    @Test
    fun shouldGetEnvironmentOnlyProperty() {
        Assert.assertThat("Property", app1Env1Properties["environment.only"], CoreMatchers.`is`(CoreMatchers.equalTo<Any?>("environment")))
    }

    @Test
    fun shouldGetEnvironmentOverridenProperty() {
        Assert.assertThat("Property", app1Env1Properties["environment.override"], CoreMatchers.`is`(CoreMatchers.equalTo<Any?>("environment")))
    }

    @Test
    fun shouldGetApplicationEnvironmentOnlyProperty() {
        Assert.assertThat("Property", app1Env1Properties["application.environment.only"], CoreMatchers.`is`(CoreMatchers.equalTo<Any?>("app-env")))
    }

    @Test
    fun shouldGetApplicationEnvironmentOverridenProperty() {
        Assert.assertThat("Property", app1Env1Properties["application.environment.override"], CoreMatchers.`is`(CoreMatchers.equalTo<Any?>("app-env")))
    }

    @Test
    fun shouldGetPropertyEvenWhenNotAllFilesExists() {
        Assert.assertThat("Property", app2Env1Properties["application.environment.override"], CoreMatchers.`is`(CoreMatchers.equalTo<Any?>("global")))
    }
}
