package nl.marisabel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import org.thymeleaf.dialect.springdata.SpringDataDialect;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

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

	@Bean
	public SpringTemplateEngine templateEngine(ITemplateResolver templateResolver) {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		templateEngine.addDialect(new SpringDataDialect());
		return templateEngine;
	}

}
