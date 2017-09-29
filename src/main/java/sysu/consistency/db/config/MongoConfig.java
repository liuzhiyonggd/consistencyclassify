package sysu.consistency.db.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import sysu.consistency.tools.Test;

@Configuration
@ComponentScan
@EnableMongoRepositories(basePackages="sysu.consistency.db.repository")
@EnableAspectJAutoProxy
public class MongoConfig extends AbstractMongoConfiguration{

	@Override
	protected String getDatabaseName() {
		return "sourcebase";
	}

	@Override
	public Mongo mongo() throws Exception {
//		MongoCredential credential = MongoCredential.createMongoCRCredential("zhiyong", "sourcebase", "liu888888".toCharArray());
		return new MongoClient(new ServerAddress("192.168.2.168",27017));
//		return new MongoClient("localhost");
	}
	
	@Bean
	public Test test(){
		return new Test();
	}
	
}
