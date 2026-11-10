package vn.tuhoc.RelationshipService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "vn.tuhoc")
@EnableFeignClients
public class RelationshipServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RelationshipServiceApplication.class, args);
	}

}
