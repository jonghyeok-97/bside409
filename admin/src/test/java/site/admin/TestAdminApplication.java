package site.admin;

import org.springframework.boot.SpringApplication;

public class TestAdminApplication {

	public static void main(String[] args) {
		SpringApplication.from(AdminApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
