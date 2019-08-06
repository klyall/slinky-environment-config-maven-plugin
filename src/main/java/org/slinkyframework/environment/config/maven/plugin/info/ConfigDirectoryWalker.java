package org.slinkyframework.environment.config.maven.plugin.info;

import org.apache.commons.io.DirectoryWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slinkyframework.environment.config.maven.plugin.config.EnvironmentConfigException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static java.lang.String.format;

public class ConfigDirectoryWalker extends DirectoryWalker {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigDirectoryWalker.class);

    private static final String GLOBAL = "global";
    private static final String APPLICATIONS = "applications";
    private static final String ENVIRONMENTS = "environments";

    private Set<Object> allPropertyNames = new TreeSet<>();

    private Properties globalProperties;
    private Map<String, Properties> applicationProperties = new TreeMap<>();
    private Map<String, Properties> environmentProperties = new TreeMap<>();
    private Map<String, Map<String, Properties>> applicationEnvironmentProperties = new TreeMap<>();
    private String level1;
    private String level2;

    public List walk(File startDirectory) {
        List results = new ArrayList();
        try {
            walk(startDirectory, results);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        logPropertiesFound();

        return results;
    }


    @Override
    protected boolean handleDirectory(File dir, int depth, Collection results) {
        if (depth == 1) {
            level1 = dir.getName();
        } else if (depth == 2) {
            level2 = dir.getName();
        }

        return (depth == 0
                || depth == 1
                && (dir.getName().equals(GLOBAL)
                    || dir.getName().equals(APPLICATIONS)
                    || dir.getName().equals(ENVIRONMENTS)
                    )
                || depth > 1
                );
    }

    @Override
    protected void handleFile(File file, int depth, Collection results) {
        if (!file.getName().endsWith(".properties")) {
            return;
        }

        if (file.getName().equals("global.properties")) {
            globalProperties = loadProperties(file);
            allPropertyNames.addAll(globalProperties.keySet());

        } else if (depth == 3 && level1.equals(APPLICATIONS)) {
            Properties properties = loadProperties(file);
            applicationProperties.put(level2, properties);
            allPropertyNames.addAll(properties.keySet());

        } else if (depth == 3 && level1.equals(ENVIRONMENTS)) {
            Properties properties = loadProperties(file);
            environmentProperties.put(level2, properties);
            allPropertyNames.addAll(properties.keySet());

        } else if (depth == 5 && level1.equals(ENVIRONMENTS)) {
            Properties properties = loadProperties(file);
            applicationEnvironmentProperties.putIfAbsent(level2, new TreeMap<>());
            applicationEnvironmentProperties.get(level2).put(file.getParentFile().getName(), properties);
            allPropertyNames.addAll(properties.keySet());
        }

        results.add(file);
    }

    private Properties loadProperties(File file) {
        Properties properties = new Properties();

        try {
            if (file.exists()) {
                try (FileReader fr = new FileReader(file)) {
                    properties.load(fr);
                }
            } else {
                LOG.debug("WARNING: Properties file '{}' does not exist and will not be loaded", file.getAbsolutePath());
            }
        } catch (IOException e) {
            throw new EnvironmentConfigException(format("Unable to load properties file %s", file), e);
        }

        return properties;
    }

    private void logPropertiesFound() {
        LOG.info("{} {} {} {}={}", format("%-15s", "Property Type"), format("%-20s", "Environment"), format("%-30s", "Application"), "key", "value");
        LOG.info("{} {} {} {}={}", format("%-15s", "============="), format("%-20s", "==========="), format("%-30s", "==========="), "===", "=====");

        for (Object propertyName: allPropertyNames) {

            if (globalProperties.containsKey(propertyName)) {
                logProperty("GLOBAL", "", "", propertyName, globalProperties.getProperty((String) propertyName));
            }

            for (Map.Entry<String, Properties> applicationEntry: applicationProperties.entrySet()) {
                Properties properties = applicationEntry.getValue();
                if (properties.containsKey(propertyName)) {
                    logProperty("APPLICATION", "", applicationEntry.getKey(), propertyName, properties.getProperty((String) propertyName));
                }
            }

            for (Map.Entry<String, Properties> environmentEntry: environmentProperties.entrySet()) {
                Properties properties = environmentEntry.getValue();
                if (properties.containsKey(propertyName)) {
                    logProperty("ENVIRONMENT", environmentEntry.getKey(), "", propertyName, properties.getProperty((String) propertyName));
                }
            }

            for (Map.Entry<String, Map<String, Properties>> applicationEnvironmentEntry: applicationEnvironmentProperties.entrySet()) {
                Map<String, Properties> environments = applicationEnvironmentEntry.getValue();

                for (Map.Entry<String, Properties> environmentsEntry: environments.entrySet()) {
                    Properties properties = environmentsEntry.getValue();
                    if (properties.containsKey(propertyName)) {
                        logProperty("APPL_ENV", applicationEnvironmentEntry.getKey(), environmentsEntry.getKey(), propertyName, properties.getProperty((String) propertyName));
                    }
                }
            }
        }

        LOG.info("Number of unique properties   : {}", format("%,7d", calculateNumberOfPropertiesManaged()));
        LOG.info("Number of configuration lines : {}", format("%,7d", calculateNumberOfConfigurationLines()));
    }

    private void logProperty(String propertyType, String environmentName, String applicationName, Object propertyName, String value) {
        LOG.info("{} {} {} {}={}", format("%-15s", propertyType), format("%-20s", environmentName), format("%-30s", applicationName), propertyName, value);
    }

    private int calculateNumberOfPropertiesManaged() {
        return allPropertyNames.size();
    }

    private int calculateNumberOfConfigurationLines() {
        return globalProperties.size()
                + countConfigLines(applicationProperties)
                + countConfigLines(environmentProperties)
                + applicationEnvironmentProperties.values().stream().map(this::countConfigLines).mapToInt(Integer::intValue).sum();
    }

    private int countConfigLines(Map<String, Properties> map) {
        return map.values().stream().map(Properties::size).mapToInt(Integer::intValue).sum();
    }
}
