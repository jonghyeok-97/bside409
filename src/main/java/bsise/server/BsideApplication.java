package bsise.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BsideApplication {

    public static void main(String[] args) {
        SpringApplication.run(BsideApplication.class, args);
    }

}
