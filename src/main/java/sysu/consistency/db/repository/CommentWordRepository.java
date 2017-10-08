package sysu.consistency.db.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import sysu.consistency.db.bean.CommentWord;

public interface CommentWordRepository extends MongoRepository<CommentWord,String>{
	
	List<CommentWord> findByProject(String project);
	List<CommentWord> findByIschange(boolean ischange);
	List<CommentWord> findByProjectAndIschange(String project,boolean ischange);
	List<CommentWord> findByProjectAndType(String project,String type);
	CommentWord findASingleByCommentIDAndType(int commentID,String type);
}
