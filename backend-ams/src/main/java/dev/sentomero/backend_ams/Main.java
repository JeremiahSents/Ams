package dev.sentomero.backend_ams;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.crypto.SecretKey;
import java.util.Base64;


@SpringBootApplication
public class Main {

	public static void main(String[] args) {
		SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
		String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
		System.out.println("Generated Key: " + base64Key);
		SpringApplication.run(Main.class, args);
	}

}
