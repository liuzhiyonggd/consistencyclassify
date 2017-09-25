package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import sysu.consistency.db.bean.ClassMessage;
import sysu.consistency.db.bean.CodeComment;
import sysu.consistency.db.config.MongoConfig;
import sysu.consistency.db.dao.ClassMessageDAO;
import sysu.consistency.db.daoImpl4Template.ClassMessageDAOImpl;

public class CommentDensity {
	
	public static void main(String[] args) {
		statistic();
	}

	private static void statistic(){
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(MongoConfig.class);
		MongoTemplate mongoTemplate = ctx.getBean(MongoTemplate.class);
		ClassMessageDAO classDAO = new ClassMessageDAOImpl(mongoTemplate);
		Map<String,Integer> codeLineMap = new HashMap<String,Integer>();
		Map<String,Integer> commentLineMap = new HashMap<String,Integer>();
		for(int i=400;i<=426;i++){
			List<ClassMessage> classList = classDAO.find(i*1000+1,(i+1)*1000);
			for(ClassMessage clazz:classList){
				String project = clazz.getProject();
				if(clazz.getType().equals("delete")){
					continue;
				}
				int codeLine = clazz.getNewCode().size();
				int commentLine = 0;
				List<CodeComment> commentList = clazz.getNewComment();
				for(CodeComment comment:commentList){
					commentLine += comment.getEndLine()-comment.getStartLine()+1;
				}
				if(codeLineMap.containsKey(project)){
					codeLineMap.put(project, codeLineMap.get(project)+codeLine);
				}else{
					codeLineMap.put(project, codeLine);
				}
				if(commentLineMap.containsKey(project)){
					commentLineMap.put(project, commentLineMap.get(project)+commentLine);
				}else{
					commentLineMap.put(project, commentLine);
				}
			}
			System.out.println(((i+1)*1000)+" is done.");
			List<String> output = new ArrayList<String>();
			for(Map.Entry<String, Integer> entry:codeLineMap.entrySet()){
				output.add(entry.getKey()+","+entry.getValue()+","+commentLineMap.get(entry.getKey()));
			}
			try {
				FileUtils.writeLines(new File("D:/work/5.20/projectCommentDensity.csv"), output);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		List<String> output = new ArrayList<String>();
		for(Map.Entry<String, Integer> entry:codeLineMap.entrySet()){
			output.add(entry.getKey()+","+entry.getValue()+","+commentLineMap.get(entry.getKey()));
		}
		try {
			FileUtils.writeLines(new File("D:/work/5.20/projectCommentDensity.csv"), output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
