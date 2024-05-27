package org.hrsys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Collections;
import java.util.List;

@SpringBootApplication
@EnableWebMvc
public class App
{
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
