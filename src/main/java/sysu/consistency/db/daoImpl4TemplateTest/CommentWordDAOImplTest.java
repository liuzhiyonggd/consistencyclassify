package sysu.consistency.db.daoImpl4TemplateTest;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import sysu.consistency.db.bean.CommentWord;
import sysu.consistency.db.config.MongoConfig;
import sysu.consistency.db.dao.CommentWordDAO;
import sysu.consistency.db.daoImpl4Template.CommentWordDAOImpl;

public class CommentWordDAOImplTest {
	
	private CommentWordDAO wordDAO  = null;  
	 
    @Before  
    public void setup() {  
        AnnotationConfigApplicationContext cxt = new AnnotationConfigApplicationContext(MongoConfig.class);  
        MongoTemplate mongoTemplate = cxt.getBean(MongoTemplate.class);
        wordDAO = new CommentWordDAOImpl(mongoTemplate);
    }
    
    @Test
    public void find(){
    	List<CommentWord> wordList = wordDAO.findByProject("jhotdraw", 1, 100);
    	for(CommentWord word:wordList){
    		Assert.assertEquals("jhotdraw", word.getProject());
    	}
    	Assert.assertTrue(wordList.size()==100);
    	CommentWord word = wordDAO.findByCommentID(10010);
    	Assert.assertTrue(word.getCommentID()==10010);
    }

}
