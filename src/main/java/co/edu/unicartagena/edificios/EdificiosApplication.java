package co.edu.unicartagena.edificios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Punto de entrada de la aplicacion Spring Boot (MVC con Thymeleaf). */
@SpringBootApplication
public class EdificiosApplication {
    public static void main(String[] args) {
        SpringApplication.run(EdificiosApplication.class, args);
    }
}
