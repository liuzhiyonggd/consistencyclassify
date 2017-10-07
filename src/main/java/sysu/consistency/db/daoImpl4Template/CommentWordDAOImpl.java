package sysu.consistency.db.daoImpl4Template;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import sysu.consistency.db.bean.CommentWord;
import sysu.consistency.db.dao.CommentWordDAO;

public class CommentWordDAOImpl implements CommentWordDAO{

    private MongoTemplate mongoTemplate;
    
    public CommentWordDAOImpl(MongoTemplate mongoTemplate){
    	this.mongoTemplate = mongoTemplate;
    }
	
    public List<CommentWord> findByProject(String project, int start, int end) {
		Query query = new Query(Criteria.where("project").is(project)).skip(start-1).limit(end-start+1);
		List<CommentWord> commentWordList = mongoTemplate.find(query, CommentWord.class);
		return commentWordList;
	}

	public CommentWord findByCommentID(int commentID) {
		Query query = new Query(Criteria.where("commentID").is(commentID));
		CommentWord commentWord = mongoTemplate.findOne(query, CommentWord.class);
		return commentWord;
	}

}
