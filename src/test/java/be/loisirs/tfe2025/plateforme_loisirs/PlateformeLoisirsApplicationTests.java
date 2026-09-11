package be.loisirs.tfe2025.plateforme_loisirs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class PlateformeLoisirsApplicationTests {

	@Autowired
	private Environment environment;

	@Test
	void contextLoads() {
	}

	@Test
	void lesTestsPointentBienSurLaBaseDeTest() {
		String url = environment.getProperty("spring.datasource.url");
		System.out.println(">>> Profils actifs : " + String.join(", ", environment.getActiveProfiles()));
		System.out.println(">>> URL JDBC       : " + url);

		assertThat(environment.getActiveProfiles()).contains("test");
		assertThat(url).contains("loisirs_test");
	}

}