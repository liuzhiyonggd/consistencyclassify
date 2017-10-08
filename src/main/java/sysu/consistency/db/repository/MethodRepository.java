package sysu.consistency.db.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import sysu.consistency.db.bean.MethodBean;

public interface MethodRepository extends MongoRepository<MethodBean,String>{
	List<MethodBean> findByProjectAndCommitIDAndClassName(String project,String commitID,String className);
	MethodBean findASingleByMethodID(int methodID);
}
