package mx.edu.upq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class SakilaSpringBootApplication {

	public static void main(String[] args) {
		BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
		System.out.println("user0123 -> " + enc.encode("user0123"));
		System.out.println("admin123 -> " + enc.encode("admin123"));
		SpringApplication.run(SakilaSpringBootApplication.class, args);
	}

}
