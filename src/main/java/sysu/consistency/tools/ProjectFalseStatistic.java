package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentRepository;

public class ProjectFalseStatistic {
	
	public static void main(String[] args) throws IOException {
		Map<String,Integer> projectFalse1Map = new HashMap<String,Integer>();
		CommentRepository commentRepo = RepositoryFactory.getCommentRepository();
		List<String> false1List = FileUtils.readLines(new File("D:/work/5.6/false1CommentList.txt"));
		for(String str:false1List){
			int commentID = Integer.parseInt(str);
			CommentEntry comment = commentRepo.findASingleByCommentID(commentID);
			String project = comment.getProject();
			if(projectFalse1Map.containsKey(project)){
				projectFalse1Map.put(project, projectFalse1Map.get(project)+1);
			}else{
				projectFalse1Map.put(project, 1);
			}
		}
		
		Map<String,Integer> projectFalse0Map = new HashMap<String,Integer>();
		List<String> false0List = FileUtils.readLines(new File("D:/work/5.6/false0CommentList.txt"));
		for(String str:false0List){
			int commentID = Integer.parseInt(str);
			CommentEntry comment = commentRepo.findASingleByCommentID(commentID);
			String project = comment.getProject();
			if(projectFalse0Map.containsKey(project)){
				projectFalse0Map.put(project, projectFalse0Map.get(project)+1);
			}else{
				projectFalse0Map.put(project, 1);
			}
		}
		List<String> output = new ArrayList<String>();
		for(Map.Entry<String, Integer> entry:projectFalse1Map.entrySet()){
			output.add(entry.getKey()+","+entry.getValue()+","+projectFalse0Map.get(entry.getKey()));
		}
		FileUtils.writeLines(new File("D:/work/5.6/projectfalse.csv"), output);
	}

}
