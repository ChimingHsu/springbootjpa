package garyhsu.springboot.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "msdbEM", transactionManagerRef = "msdbTM", basePackages = {
		"garyhsu.springboot.reposotory.mesdb" })
@Configuration
public class MesDBConfig {
	
	@Autowired
	private Environment env;
	
	@Bean("mydb1DS")
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource.mesdb")
	public DataSource mesdbDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean("msdbEM")
	public LocalContainerEntityManagerFactoryBean mesdb1EntityManagerFactory() {
	    Map<String, Object> properties = new HashMap<>();
	    properties.put("hibernate.physical_naming_strategy",
	    		env.getProperty("hibernate.physical_naming_strategy"));
	    properties.put("hibernate.auto_quote_keyword", 
	    		env.getProperty("hibernate.auto_quote_keyword"));
	    
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//		vendorAdapter.setGenerateDdl(true);
//		vendorAdapter.setShowSql(true);

		 
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPackagesToScan("garyhsu.springboot.entity");
		factory.setDataSource(mesdbDataSource());
		factory.setJpaPropertyMap(properties);
		return factory;
	}

	@Bean("msdbTM")
	public PlatformTransactionManager mesdbTransactionManager(@Qualifier("msdbEM") EntityManagerFactory entityManagerFactory) {

		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(entityManagerFactory);
		return txManager;
	}
}
