package cn.edu.just.moocweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MoocWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoocWebApplication.class, args);
    }

}
