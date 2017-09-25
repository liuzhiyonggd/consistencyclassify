package sysu.consistency.db.daoImpl4Template;

import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import sysu.consistency.db.bean.ClassMessage;
import sysu.consistency.db.dao.ClassMessageDAO;

public class ClassMessageDAOImpl implements ClassMessageDAO{

	private MongoTemplate mongoTemplate;
	
	public ClassMessageDAOImpl(MongoTemplate mongoTemplate){
		this.mongoTemplate = mongoTemplate;
	}
	
	@Override
	public ClassMessage findByClassID(int classID) {
		Query query = new Query(Criteria.where("classID").is(1));
		ClassMessage clazz = mongoTemplate.findOne(query, ClassMessage.class);
		return clazz;
	}

	@Override
	public List<ClassMessage> findByProjectAndCommitID(String project, String commitID) {
		Query query = new Query(Criteria.where(project).is(project).and(commitID).is(commitID));
		List<ClassMessage> classList = mongoTemplate.find(query, ClassMessage.class);
		return classList;
	}

	@Override
	public List<ClassMessage> findByProject(String project,int start,int end) {
		Query query = new Query(Criteria.where("project").is(project)).skip(start-1).limit(end-start+1);
		List<ClassMessage> classList = mongoTemplate.find(query,ClassMessage.class);
		return classList;
	}
	
	@Override
	public List<ClassMessage> findByProjectAndType(String project, String type,int start,int end) {
	    Query query = new Query(Criteria.where("project").is(project).and("type").is(type)).skip(start-1).limit(end-start+1);
	    List<ClassMessage> classList = mongoTemplate.find(query, ClassMessage.class);
		return classList;
	}
	
@Override
public List<ClassMessage> find(int start, int end) {
	Query query = new Query().skip(start-1).limit(end-start+1);
	List<ClassMessage> classList = mongoTemplate.find(query, ClassMessage.class);
	return classList;
}
	

}
