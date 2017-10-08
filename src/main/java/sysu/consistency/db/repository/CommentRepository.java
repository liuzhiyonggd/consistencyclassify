package sysu.consistency.db.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import sysu.consistency.db.bean.CommentEntry;

public interface CommentRepository extends MongoRepository<CommentEntry,String>{
	List<CommentEntry> findByProjectAndType(String project,String type);
	List<CommentEntry> findByIsChange(boolean isChange);
	List<CommentEntry> findByProjectAndIsChange(String project,boolean isChange);
	CommentEntry findASingleByCommentID(int commentID);
	List<CommentEntry> findByProject(String project);
	List<CommentEntry> findByProjectAndCommitIDAndClassName(String project,String commitID,String className);
	
	
}
