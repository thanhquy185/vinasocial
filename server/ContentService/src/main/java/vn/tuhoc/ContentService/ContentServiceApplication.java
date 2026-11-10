package vn.tuhoc.ContentService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "vn.tuhoc")
@EnableDiscoveryClient
@EnableFeignClients
public class ContentServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(ContentServiceApplication.class, args);
	}
}
