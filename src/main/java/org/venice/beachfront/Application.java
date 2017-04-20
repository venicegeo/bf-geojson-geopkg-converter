package org.venice.beachfront;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * Main application class.
 * 
 * Sets up the Spring Boot environment to launch the GeoJSON to GPKG 
 * converter API.
 * 
 * @version 1.0
 */
@SpringBootApplication
@PropertySource("classpath:strings.properties")
public class Application {
    public static void main( String[] args )
    {
       SpringApplication.run(Application.class, args);
    }
}