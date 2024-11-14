package pedribault.game;  // Assurez-vous que ce package correspond à votre structure de projet

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"pedribault.game.utils", "pedribault.game.service", "pedribault.game.mappers", "pedribault.game.controller", "pedribault.game.exceptions"})
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}
