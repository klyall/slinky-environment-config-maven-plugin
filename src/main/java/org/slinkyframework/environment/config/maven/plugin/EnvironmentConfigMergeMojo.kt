package org.slinkyframework.environment.config.maven.plugin

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.Execute
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.slinkyframework.environment.config.maven.plugin.config.CompositeConfigFileFactory
import org.slinkyframework.environment.config.maven.plugin.config.files.FileApplicationConfigFactory
import org.slinkyframework.environment.config.maven.plugin.config.templates.TemplateApplicationConfigFactory
import java.io.File

@Mojo(name = "merge", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
@Execute(goal = "merge", phase = LifecyclePhase.GENERATE_RESOURCES)
class EnvironmentConfigMergeMojo : AbstractMojo()
{
    @Parameter(property = "config.sourceDir", defaultValue = "\${project.basedir}/src/main/resources", readonly = true)
    private lateinit var sourceDir: String

    @Parameter(property = "config.targetDir", defaultValue = "\${project.build.directory}/generated-config", readonly = true)
    private lateinit var targetDir: String

    /**
     *
     *
     * Set of delimiters for expressions to filter within the resources. These delimiters are specified in the form
     * `beginToken*endToken`. If no `*` is given, the delimiter is assumed to be the same for start and end.
     *
     *
     *
     * So, the default filtering delimiters might be specified as:
     *
     *
     * <pre>
     * &lt;delimiters&gt;
     * &lt;delimiter&gt;${*}&lt;/delimiter&gt;
     * &lt;delimiter&gt;@&lt;/delimiter&gt;
     * &lt;/delimiters&gt;
    </pre> *
     *
     *
     * Since the `@` delimiter is the same on both ends, we don't need to specify `@*@` (though we can).
     *
     */
    @Parameter
    private var delimiters: Set<String>? = null

    @Parameter(property = "config.failOnMissingProperty", defaultValue = "false", readonly = true)
    private val failOnMissingProperty: Boolean = false

    @Throws(MojoExecutionException::class, MojoFailureException::class)
    override fun execute()
    {
        if (delimiters == null || delimiters!!.isEmpty())
        {
            delimiters = setOf(DEFAULT_DELIMITER)
        }

        val fileConfigFileFactory = FileApplicationConfigFactory(File(sourceDir), File(targetDir))
        val templateConfigFileFactory = TemplateApplicationConfigFactory(File(sourceDir), File(targetDir), delimiters!!)
        templateConfigFileFactory.setFailOnMissingProperty(failOnMissingProperty)
        val configFileFactory = CompositeConfigFileFactory(fileConfigFileFactory, templateConfigFileFactory)

        configFileFactory.generateFiles()
    }

    companion object
    {
        private const val DEFAULT_DELIMITER = "\${*}"
    }
}
