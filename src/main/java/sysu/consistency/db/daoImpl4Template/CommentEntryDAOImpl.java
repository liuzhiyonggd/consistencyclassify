package sysu.consistency.db.daoImpl4Template;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.dao.CommentEntryDAO;

public class CommentEntryDAOImpl implements CommentEntryDAO{
	
	private MongoTemplate mongoTemplate;
	
	public CommentEntryDAOImpl(MongoTemplate mongoTemplate){
		this.mongoTemplate = mongoTemplate;
	}
	
	@Override
	public List<CommentEntry> findByProject(String project, int start, int end) {
		Query query = new Query(Criteria.where("project").is(project)).skip(start-1).limit(end-start+1);
		List<CommentEntry> commentList = mongoTemplate.find(query, CommentEntry.class);
		return commentList;
	}

	@Override
	public List<CommentEntry> findByProjectAndIsChange(String project, boolean isChange) {
		Query query = new Query(Criteria.where("project").is(project).and("isChange").is(isChange));
		List<CommentEntry> commentList = mongoTemplate.find(query, CommentEntry.class);
		return commentList;
	}

	@Override
	public CommentEntry findByCommentID(int commentID) {
		Query query = new Query(Criteria.where("commentID").is(commentID));
		CommentEntry comment = mongoTemplate.findOne(query, CommentEntry.class);
		return comment;
	}
	
	@Override
	public List<CommentEntry> find(int start, int end) {
//		Query query = new Query(Criteria.where("commentID").gte(start).lte(end));
		Query query = new Query().skip(start-1).limit(end-start+1);
		List<CommentEntry> commentList = mongoTemplate.find(query, CommentEntry.class);
		return commentList;
	}
	
	@Override
	public void updateIsChange(int commentID,boolean isChange){
		Query query = new Query(Criteria.where("commentID").is(commentID));
		Update update = new Update().addToSet("isChange", isChange);
		mongoTemplate.updateFirst(query, update, CommentEntry.class);
	}

}
