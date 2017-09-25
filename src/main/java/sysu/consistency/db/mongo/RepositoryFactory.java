package sysu.consistency.db.mongo;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import sysu.consistency.db.repository.ClassMessageRepository;
import sysu.consistency.db.repository.CommentRepository;
import sysu.consistency.db.repository.CommentWordRepository;
import sysu.consistency.db.repository.MethodRepository;

public class RepositoryFactory {
	
	private final static AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(sysu.consistency.db.config.MongoConfig.class); 
	
	public static AnnotationConfigApplicationContext getContext(){
		return context;
	}
	
	public static ClassMessageRepository getClassRepository(){
		return context.getBean(ClassMessageRepository.class);
	}
	
	public static CommentRepository getCommentRepository(){
		return context.getBean(CommentRepository.class);
	}
	
	public static CommentWordRepository getCommentWordRepository(){
		return context.getBean(CommentWordRepository.class);
	}
	
	public static MethodRepository getMethodRepository(){
		return context.getBean(MethodRepository.class);
	}

}
