package it.pjor94.beerhunter;

import it.pjor94.beerhunter.core.Core;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication()
@EnableAsync()
@EnableMongoRepositories(basePackages = "it.pjor94.beerhunter.repository")
public class BeerhunterApplication {

	@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(20);
		executor.setMaxPoolSize(200);
		executor.setQueueCapacity(10000);
		executor.setThreadNamePrefix("BEERHUNTER-");
		executor.initialize();
		return executor;
	}


	public static void main(String[] args) {
		//SpringApplication.run(BeerHunterApplication.class, args);
		ConfigurableApplicationContext context = SpringApplication.run(BeerhunterApplication.class, args);
		context.getBean(Core.class).start(); // <-- here

	}

}
