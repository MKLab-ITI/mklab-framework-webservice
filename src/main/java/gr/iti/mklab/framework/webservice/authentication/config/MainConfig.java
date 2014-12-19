package gr.iti.mklab.framework.webservice.authentication.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;

/**
 * Main configuration class for the application.
 * Turns on @Component scanning, loads externalized application.properties, and sets up the database.
 * @author Keith Donald
 */
@Configuration
@ComponentScan(basePackages = "org.springframework.social.quickstart", excludeFilters = { @Filter(Configuration.class) })
@PropertySource("classpath:application.properties")
public class MainConfig {

	@Bean(destroyMethod = "shutdown")
	public DataSource dataSource() {
		EmbeddedDatabaseFactory factory = new EmbeddedDatabaseFactory();
		factory.setDatabaseName("spring-social-quickstart");
		factory.setDatabaseType(EmbeddedDatabaseType.H2);
		factory.setDatabasePopulator(databasePopulator());
		return factory.getDatabase();
	}

	// internal helpers

	private DatabasePopulator databasePopulator() {
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScript(new ClassPathResource("JdbcUsersConnectionRepository.sql", JdbcUsersConnectionRepository.class));
		return populator;
	}
}