package sysu.consistency.db.daoImpl4TemplateTest;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import sysu.consistency.db.bean.ClassMessage;
import sysu.consistency.db.config.MongoConfig;
import sysu.consistency.db.dao.ClassMessageDAO;
import sysu.consistency.db.daoImpl4Template.ClassMessageDAOImpl;

public class ClassMessageDAOImplTest {
	
	 private ClassMessageDAO classDAO  = null;  
	 
	    @Before  
	    public void setup() {  
	        AnnotationConfigApplicationContext cxt = new AnnotationConfigApplicationContext(MongoConfig.class);  
	        MongoTemplate mongoTemplate = cxt.getBean(MongoTemplate.class);
	        classDAO = new ClassMessageDAOImpl(mongoTemplate);
	    }  
	    
	    @Test
	    public void find(){
	    	ClassMessage clazz = classDAO.findByClassID(1);
	    	Assert.assertEquals(1, clazz.getClassID());
	    	List<ClassMessage> classList = classDAO.findByProject("jhotdraw",1,100);
	    	Assert.assertTrue("size is 100", classList.size()==100);
	    	List<ClassMessage> classList2 = classDAO.findByProjectAndType("jhotdraw", "change", 1, 100);
	    	for(ClassMessage clazz2:classList2){
	    		Assert.assertEquals("change", clazz2.getType());
	    		Assert.assertEquals("jhotdraw",clazz2.getProject());
	    	}
	    	Assert.assertTrue(classList2.size()==100);
	    	
	    }
	  
	  

}
