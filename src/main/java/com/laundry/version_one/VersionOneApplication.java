package com.laundry.version_one;

import com.laundry.version_one.role.Role;
import com.laundry.version_one.role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableAsync
@EnableScheduling
public class VersionOneApplication {

	public static void main(String[] args) {
		System.out.println("hello world");
		SpringApplication.run(VersionOneApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(RoleRepository roleRepository){
		return args -> {
			if(roleRepository.findByName("USER").isEmpty()){
				roleRepository.save(
						Role.builder().name("USER").build()
				);
			}
			if(roleRepository.findByName("OWNER").isEmpty()){
				roleRepository.save(
						Role.builder().name("OWNER").build()
				);
			}
		};
	}

}
