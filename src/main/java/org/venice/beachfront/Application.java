package org.venice.beachfront;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:strings.properties")
public class Application {
    public static void main( String[] args )
    {
       SpringApplication.run(Application.class, args);
    }
}