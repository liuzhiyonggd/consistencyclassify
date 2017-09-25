package sysu.consistency.db.config;


import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import sysu.consistency.aop.Log;
import sysu.consistency.tools.Test;

@Configuration
@ComponentScan
@EnableMongoRepositories(basePackages="db.repository")
@EnableAspectJAutoProxy
public class MongoConfig extends AbstractMongoConfiguration{

	@Override
	protected String getDatabaseName() {
		return "sourcebase";
	}

	@Override
	public Mongo mongo() throws Exception {
		MongoCredential credential = MongoCredential.createMongoCRCredential("zhiyong", "sourcebase", "liu888888".toCharArray());
		return new MongoClient(new ServerAddress("172.18.217.106",27017),Arrays.asList(credential));
//		return new MongoClient("localhost");
	}
	
	@Bean
	public Test test(){
		return new Test();
	}
	
}
