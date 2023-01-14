package pl.kielce.tu.mergeservice;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;

import java.io.IOException;

@SpringBootApplication
public class MergeServiceApplication {

    @Autowired
    public RootHost rootHost;

    @Autowired
    ApplicationPort applicationPort;

    public static void main(String[] args) {
        SpringApplication.run(MergeServiceApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        HttpPost request = new HttpPost(String.format("http://%s/register", rootHost.getAddress()));
        request.setEntity(new StringEntity(String.valueOf(applicationPort.port)));

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            CloseableHttpResponse response = httpClient.execute(request);
        } catch (IOException exception) {
            System.out.println("ERROR" + exception.getMessage());
            exception.printStackTrace();
        }
    }


}

@Configuration
class CustomContainer implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
    @Autowired
    ApplicationPort applicationPort;

    public void customize(ConfigurableServletWebServerFactory factory){
        factory.setPort(applicationPort.port);
    }
}
