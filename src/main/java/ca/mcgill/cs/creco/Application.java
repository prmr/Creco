package ca.mcgill.cs.creco;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@EnableAutoConfiguration
public class Application {

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }

}
