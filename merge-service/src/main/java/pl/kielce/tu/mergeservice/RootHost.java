package pl.kielce.tu.mergeservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RootHost {
    private String address;

    public RootHost(@Value("${rootAddress}") String address) {

        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}