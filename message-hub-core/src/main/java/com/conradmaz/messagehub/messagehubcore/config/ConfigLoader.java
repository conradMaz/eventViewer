package com.conradmaz.messagehub.messagehubcore.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public class ConfigLoader<T> {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigLoader.class);

    private static final String CONF_FOLDER = "conf/local/";
    private static final String PREFIX_YAML = "yml";
    
   
    /**
     * Loads an application's yaml config file from the {@link conf/local} 
     * directory into the application's config POJO.
     * 
     * @param applicationName - name of the application file to load
     * @param clazz config class
     * @return the applications config POJO
     */
    public T loadApplicationConfig( String applicationName, Class<T> configClazz) {

        Yaml yaml = new Yaml();
        T config = null;
        String appConfig = String.format(CONF_FOLDER+"%s.%s", applicationName,PREFIX_YAML) ;
        File appConfigFile = new File(appConfig);
        
        if (appConfigFile.exists()) {
            
            LOG.info("Loading config from file, file={}", appConfig);

            try {
                config =  yaml.loadAs(new BufferedReader(new InputStreamReader(new FileInputStream(appConfigFile))), configClazz);
                
            } catch (FileNotFoundException e) {
                throw new IllegalStateException("Failed to load config", e);
            }
            
            LOG.info("Loaded config from file, config={}", config);
            
        }else{
            throw new IllegalStateException(String.format("%s file not found.", appConfig));
        }
        return config;
    }
}
