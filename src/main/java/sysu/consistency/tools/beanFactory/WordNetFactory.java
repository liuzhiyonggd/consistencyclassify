package sysu.consistency.tools.beanFactory;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import edu.mit.jwi.IRAMDictionary;
import edu.sussex.nlp.jws.Lin;

public class WordNetFactory {
	private final static AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(sysu.consistency.db.config.WordNetConfig.class);
	
	public static IRAMDictionary getDictionary(){
		return (IRAMDictionary) context.getBean("dictionary");
	}
	
	public static Lin getLin(){
		return (Lin) context.getBean("lin");
	}
}
