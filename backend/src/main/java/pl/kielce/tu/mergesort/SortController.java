package main.java.pl.kielce.tu.mergesort;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SortController {

    @RequestMapping("/")
    public String helloWorld(){
        return "Hello World from Spring Boot";
    }
}