package pl.kielce.tu.mergeservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class ApplicationPort {
    public int port;

    public ApplicationPort(@Value("${port}") String port) {
        this.port = Integer.parseInt(port);
    }

    public int getPort() {
        return port;
    }
}