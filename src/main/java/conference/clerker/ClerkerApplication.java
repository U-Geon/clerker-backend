package conference.clerker;

import conference.clerker.global.aws.AwsProperty;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
public class ClerkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClerkerApplication.class, args);
	}

}
