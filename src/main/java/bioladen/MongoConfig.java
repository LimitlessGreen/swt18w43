package bioladen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import javax.annotation.PostConstruct;

@Configuration
public class MongoConfig {

	@Autowired
	private MongoDbFactory mongoFactory;

	@Autowired
	private MongoMappingContext mongoMappingContext;

	@ConditionalOnMissingBean(MongoConverter.class)
	@Bean
	public MappingMongoConverter mongoConverter() throws Exception {
		DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoFactory);
		MappingMongoConverter mongoConverter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
		//this is my customization
		mongoConverter.setMapKeyDotReplacement("<@D-O-T@>");
		mongoConverter.afterPropertiesSet();
		return mongoConverter;
	}
}