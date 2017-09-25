package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentRepository;

public class ProjectStatistic {
	
	public static void main(String[] args) throws IOException {
		CommentRepository commentRepo = RepositoryFactory.getCommentRepository();
		List<String> commentID = FileUtils.readLines(new File("D:/work/5.4/newid.txt"));
		Map<String,Integer> class1Map = new HashMap<String,Integer>();
		Map<String,Integer> class0Map = new HashMap<String,Integer>();
		Map<String,Set<String>> commitMap = new HashMap<String,Set<String>>();
		int i=0;
		for(String str:commentID){
			
			i++;
			if(i%1000==0){
				System.out.println(i+" is done.");
			}
			int id = Integer.parseInt(str);
			CommentEntry comment = commentRepo.findASingleByCommentID(id);
			String project = comment.getProject();
			if(comment.isChange()){
				if(class1Map.containsKey(project)){
					class1Map.put(project, class1Map.get(project)+1);
				}else{
					class1Map.put(project, 1);
				}
			}else{
				if(class0Map.containsKey(project)){
					class0Map.put(project, class0Map.get(project)+1);
				}else{
					class0Map.put(project, 1);
				}
			}
			
			if(commitMap.containsKey(project)){
				commitMap.get(project).add(comment.getCommitID());
			}else{
				Set<String> commitSet = new HashSet<String>();
				commitSet.add(comment.getCommitID());
				commitMap.put(project, commitSet);
			}
			
		}
		
		List<String> output = new ArrayList<String>();
		for(Map.Entry<String, Integer> entry : class1Map.entrySet()){
			output.add(entry.getKey()+","+entry.getValue()+","+class0Map.get(entry.getKey())+","+commitMap.get(entry.getKey()).size());
		}
		FileUtils.writeLines(new File("D:/work/5.6/project.csv"), output);
	}

}
