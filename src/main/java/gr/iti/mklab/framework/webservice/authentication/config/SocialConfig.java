package gr.iti.mklab.framework.webservice.authentication.config;
import gr.iti.mklab.framework.webservice.authentication.SecurityContext;
import gr.iti.mklab.framework.webservice.authentication.SimpleConnectionSignUp;
import gr.iti.mklab.framework.webservice.authentication.SimpleSignInAdapter;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;


/**
 * Spring Social Configuration.
 * @author Craig Walls
 */
@Configuration
@EnableSocial
public class SocialConfig implements SocialConfigurer {

	@Inject
	private DataSource dataSource;

	//
	// SocialConfigurer implementation methods
	//
	
	@Override
	public void addConnectionFactories(ConnectionFactoryConfigurer cfConfig, Environment env) {
		cfConfig.addConnectionFactory(new FacebookConnectionFactory(env.getProperty("facebook.appKey"), env.getProperty("facebook.appSecret")));
	}


	/**
	 * Singleton data access object providing access to connections across all users.
	 */
	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		JdbcUsersConnectionRepository repository = new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, Encryptors.noOpText());
		repository.setConnectionSignUp(new SimpleConnectionSignUp());
		return repository;
	}
	
	public UserIdSource getUserIdSource() {
		return new UserIdSource() {
			@Override
			public String getUserId() {
				return SecurityContext.getCurrentUser().getId();
			}
		};
	}

	@Bean
	@Scope(value="request", proxyMode=ScopedProxyMode.INTERFACES)
	public Facebook facebook(ConnectionRepository repository) {
		Connection<Facebook> connection = repository.findPrimaryConnection(Facebook.class);
		return connection != null ? connection.getApi() : null;
	}

	@Bean
	public ProviderSignInController providerSignInController(ConnectionFactoryLocator connectionFactoryLocator, UsersConnectionRepository usersConnectionRepository) {
		return new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, new SimpleSignInAdapter());
	}

}