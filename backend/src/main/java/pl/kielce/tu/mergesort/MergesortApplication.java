package pl.kielce.tu.mergesort;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class MergesortApplication {

    public static void main(String[] args) {
        SpringApplication.run(MergesortApplication.class, args);
    }

}

@Configuration
class CustomContainer implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
    public void customize(ConfigurableServletWebServerFactory factory){
        factory.setPort(8080);
    }
}