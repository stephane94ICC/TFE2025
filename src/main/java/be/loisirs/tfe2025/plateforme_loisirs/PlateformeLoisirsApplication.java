package be.loisirs.tfe2025.plateforme_loisirs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.time.ZoneId;
import java.util.TimeZone;


@SpringBootApplication
public class PlateformeLoisirsApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Europe/Brussels")));
		SpringApplication.run(PlateformeLoisirsApplication.class, args);
	}

}
