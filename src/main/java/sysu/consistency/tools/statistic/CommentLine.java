package sysu.consistency.tools.statistic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.ClassMessage;
import sysu.consistency.db.bean.CodeComment;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.ClassMessageRepository;

public class CommentLine {
	
	
	public static void main(String[] args) throws IOException{
		Map<String,Integer> allLinesMap = new HashMap<String,Integer>();
		Map<String,Integer> commentLinesMap = new HashMap<String,Integer>();
		
		ClassMessageRepository classRepo = RepositoryFactory.getClassRepository();
		for(int i=520001;i<1125585;i++){
			if(i%10000==0){
				System.out.println(i+" is done.");
				List<String> output2 = new ArrayList<String>();
				for(Map.Entry<String, Integer> entry:allLinesMap.entrySet()){
					output2.add(entry.getKey()+","+entry.getValue()+","+commentLinesMap.get(entry.getKey()));
				}
				
				FileUtils.writeLines(new File("D:/work/4.13/linestatistic"+i/10000+".csv"), output2);
				allLinesMap.clear();
				commentLinesMap.clear();
			}
			ClassMessage clazz = classRepo.findASingleByClassID(i);
			int allLine = clazz.getNewCode().size()+clazz.getOldCode().size();
			int commentLine = 0;
			List<CodeComment> commentList = clazz.getNewComment();
			commentList.addAll(clazz.getOldComment());
			for(CodeComment comment:commentList){
				commentLine += comment.getEndLine()-comment.getStartLine()+1;
			}
			
			if(allLinesMap.containsKey(clazz.getProject())){
				allLinesMap.put(clazz.getProject(), allLinesMap.get(clazz.getProject())+allLine);
				commentLinesMap.put(clazz.getProject(), commentLinesMap.get(clazz.getProject())+commentLine);
			}else{
				allLinesMap.put(clazz.getProject(), allLine);
				commentLinesMap.put(clazz.getProject(),commentLine);
			}
		}
		
		List<String> output = new ArrayList<String>();
		for(Map.Entry<String, Integer> entry:allLinesMap.entrySet()){
			output.add(entry.getKey()+","+entry.getValue()+","+commentLinesMap.get(entry.getKey()));
		}
		
		FileUtils.writeLines(new File("D:/work/4.13/linestatistic.csv"), output);
		
	}

}
