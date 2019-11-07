package tech.tisson.sequence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableFeignClients
@RestController
public class SequenceDemo {

    public static void main(String[] args) {
        SpringApplication.run(SequenceDemo.class, args);
    }

    @Autowired
    private SequenceService sequenceService;

    @GetMapping(path = "/test")
    public Long doGet() {
        return sequenceService.next();
    }
}
