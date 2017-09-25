package sysu.consistency.db.daoImpl4TemplateTest;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.config.MongoConfig;
import sysu.consistency.db.dao.CommentEntryDAO;
import sysu.consistency.db.daoImpl4Template.CommentEntryDAOImpl;

public class CommentEntryDAOImplTest {
	
	private CommentEntryDAO commentDAO  = null;  
	 
    @Before  
    public void setup() {  
        AnnotationConfigApplicationContext cxt = new AnnotationConfigApplicationContext(MongoConfig.class);  
        MongoTemplate mongoTemplate = cxt.getBean(MongoTemplate.class);
        commentDAO = new CommentEntryDAOImpl(mongoTemplate);
    }
    
    @Test
    public void find(){
    	List<CommentEntry> commentList = commentDAO.find(1, 100);
    	for(CommentEntry comment : commentList){
//    		Assert.assertTrue(comment.getCommentID()>=1&&comment.getCommentID()<=100);
    	}
    	Assert.assertTrue(commentList.size()==100);
    	commentList = commentDAO.findByProject("jgit", 1, 100);
    	for(CommentEntry comment:commentList){
    		Assert.assertEquals("jgit",comment.getProject());
    	}
    	Assert.assertTrue(commentList.size()==100);
    	
//    	CommentEntry comment = commentDAO.findByCommentID(10010);
//    	Assert.assertTrue(comment.getCommentID()==10010);
    }

}
