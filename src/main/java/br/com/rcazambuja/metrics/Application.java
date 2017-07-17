package br.com.rcazambuja.metrics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableBinding
@ComponentScan("br.com.rcazambuja")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
