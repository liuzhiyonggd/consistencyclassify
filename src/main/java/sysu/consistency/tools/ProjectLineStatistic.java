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

import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.config.MongoConfig;
import sysu.consistency.db.dao.CommentEntryDAO;
import sysu.consistency.db.daoImpl4Template.CommentEntryDAOImpl;

public class ProjectLineStatistic {
	
	public static void main(String[] args) throws IOException {
		
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(MongoConfig.class);
		MongoTemplate mongoTemplate = ctx.getBean(MongoTemplate.class);
		CommentEntryDAO commentDAO = new CommentEntryDAOImpl(mongoTemplate);
		
		List<Map<Integer,Integer>> projectLineList = new ArrayList<Map<Integer,Integer>>();
		List<Map<Integer,Integer>> projectCommentLineList = new ArrayList<Map<Integer,Integer>>();
		for(int i=0;i<10;i++){
			projectLineList.add(new HashMap<Integer,Integer>());
			projectCommentLineList.add(new HashMap<Integer,Integer>());
		}
		String[] projects = new String[]{"jgit","spring","struts","hibernate","commons-csv","commons-io","elasticsearch","maven","strman-java","tablesaw"};
		Map<String,Integer> projectMap = new HashMap<String,Integer>();
		for(int i=0;i<projects.length;i++){
			projectMap.put(projects[i], i);
		}
		
		for(int i=0;i<50;i++){
			List<CommentEntry> commentList = commentDAO.find(1000*i+1, 1000*(i+1));
			System.out.println(commentList.size()+"");
			for(CommentEntry comment:commentList){
			String project = comment.getProject();
			Map<Integer,Integer> lineMap = projectLineList.get(projectMap.get(project));
			Map<Integer,Integer> commentLineMap = projectCommentLineList.get(projectMap.get(project));
			int line = comment.getOld_scope_endLine()-comment.getOld_scope_startLine()+1;
			int commentLine = comment.getOld_comment_endLine()-comment.getOld_comment_startLine()+1;
			if(lineMap.containsKey(line)){
				lineMap.put(line, lineMap.get(line)+1);
			}else{
				lineMap.put(line, 1);
			}
			if(commentLineMap.containsKey(commentLine)){
				commentLineMap.put(commentLine, commentLineMap.get(commentLine)+1);
			}else{
				commentLineMap.put(commentLine, 1);
			}
			}
			System.out.println(((i+1)*1000)+" is done.");
		}
		
		
		
		for(int i=0;i<10;i++){
			List<String> output = new ArrayList<String>();
			Map<Integer,Integer> map = projectLineList.get(i);
			for(Map.Entry<Integer, Integer> entry:map.entrySet()){
				output.add(entry.getKey()+","+entry.getValue());
			}
			FileUtils.writeLines(new File("D:/work/5.20/"+projects[i]+"_line.csv"), output);
			
			output = new ArrayList<String>();
			map = projectCommentLineList.get(i);
			for(Map.Entry<Integer, Integer> entry:map.entrySet()){
				output.add(entry.getKey()+","+entry.getValue());
			}
			FileUtils.writeLines(new File("D:/work/5.20/"+projects[i]+"_commentline.csv"), output);
		}
	}

}
