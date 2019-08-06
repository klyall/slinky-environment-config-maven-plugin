package org.slinkyframework.environment.config.maven.plugin.config.templates;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.plexus.interpolation.PropertiesBasedValueSource;
import org.codehaus.plexus.interpolation.SimpleRecursionInterceptor;
import org.codehaus.plexus.interpolation.ValueSource;
import org.codehaus.plexus.interpolation.multi.MultiDelimiterInterpolatorFilterReader;
import org.codehaus.plexus.interpolation.multi.MultiDelimiterStringSearchInterpolator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slinkyframework.environment.config.maven.plugin.config.EnvironmentConfigException;

import java.io.*;
import java.util.Properties;
import java.util.Set;

import static org.apache.commons.io.FileUtils.forceMkdir;
import static org.apache.commons.io.IOUtils.copy;
import static org.apache.commons.lang3.StringUtils.removeEndIgnoreCase;

public class FilterFileGenerator implements FileGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(FilterFileGenerator.class);

    private File targetDir;
    private Properties properties;
    private Set<String> delimiters;

    private boolean failOnMissingProperty = false;

    public FilterFileGenerator(File targetDir, Properties properties, Set<String> delimiters) {
        this.targetDir = targetDir;
        this.properties = properties;
        this.delimiters = delimiters;
    }

    public void setFailOnMissingProperty(boolean failOnMissingProperty) {
        this.failOnMissingProperty = failOnMissingProperty;
    }

    @Override
    public void generateFile(File templateFile) {
        try {
            String subDir = StringUtils.substringAfter(templateFile.getParent(), TemplateApplicationConfigFactory.TEMPLATES_DIR);
            File targetSubDir = new File(targetDir, subDir);
            forceMkdir(targetSubDir);

            String targetFilename = removeEndIgnoreCase(templateFile.getName(), ".tmpl");
            File targetFile = new File(targetSubDir, targetFilename);

            LOG.debug("Generating config file '{}' using properties {}", targetFile, properties);

            MultiDelimiterStringSearchInterpolator interpolator = new MultiDelimiterStringSearchInterpolator();
            interpolator.addValueSource(createValueSource());
            delimiters.forEach(interpolator::addDelimiterSpec);

            try (Reader reader = new FileReader(templateFile);
                 MultiDelimiterInterpolatorFilterReader filteringReader = new MultiDelimiterInterpolatorFilterReader(
                         reader,
                         interpolator,
                         new SimpleRecursionInterceptor()
                 );
                Writer writer = new FileWriter(targetFile);
            ) {
                delimiters.forEach(filteringReader::addDelimiterSpec);
                copy(filteringReader, writer);
            }
        } catch (IOException e) {
            throw new EnvironmentConfigException("Unable to filter file", e);
        }
    }

    private ValueSource createValueSource() {
        if (failOnMissingProperty) {
            return new ManadatoryValueSource(new PropertiesBasedValueSource(properties));
        } else {
            return new LoggingValueSource(new PropertiesBasedValueSource(properties));
        }
    }
}
