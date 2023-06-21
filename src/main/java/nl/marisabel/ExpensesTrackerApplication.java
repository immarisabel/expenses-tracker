package nl.marisabel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
@PropertySource("classpath:github.properties")
public class ExpensesTrackerApplication {

	public static void main(String[] args) {

		SpringApplication.run(ExpensesTrackerApplication.class, args);
	}
	@Bean
	public WebClient webClient() {
		return WebClient.create();
	}

}
