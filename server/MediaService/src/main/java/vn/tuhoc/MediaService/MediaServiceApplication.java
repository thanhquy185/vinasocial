package vn.tuhoc.MediaService;

// import java.util.HashMap;
// import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import io.mongock.runner.springboot.EnableMongock;

// import org.springframework.vault.authentication.TokenAuthentication;
// import org.springframework.vault.client.VaultEndpoint;
// import org.springframework.vault.support.Versioned;
// import org.springframework.vault.core.VaultTemplate;

@SpringBootApplication(scanBasePackages = "vn.tuhoc")
@EnableDiscoveryClient
@EnableFeignClients
@EnableMongock
public class MediaServiceApplication {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(MediaServiceApplication.class, args);

		// VaultEndpoint vaultEndpoint = new VaultEndpoint();

		// vaultEndpoint.setHost("127.0.0.1");
		// vaultEndpoint.setPort(8200);
		// vaultEndpoint.setScheme("http");

		// // Authenticate
		// VaultTemplate vaultTemplate = new VaultTemplate(
		// vaultEndpoint,
		// new TokenAuthentication("vinasocial"));

		// // Write a secret
		// Map<String, String> data = new HashMap<>();
		// data.put("password", "ThanhQuy");

		// Versioned.Metadata createResponse = vaultTemplate
		// .opsForVersionedKeyValue("secret")
		// .put("test", data);

		// System.out.println("Secret written successfully.");

		// // Read a secret
		// Versioned<Map<String, Object>> readResponse = vaultTemplate
		// .opsForVersionedKeyValue("secret")
		// .get("test");

		// String password = "";
		// if (readResponse != null && readResponse.hasData()) {
		// password = (String) readResponse.getData().get("password");
		// }

		// if (!password.equals("Hashi123")) {
		// throw new Exception("Unexpected password");
		// }

		// System.out.println("Access granted!");
	}
}
