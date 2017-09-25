package sysu.consistency.datamining;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.CommentWord;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentWordRepository;

public class FrequencyAnalysis {
	
	public static void analysis(String type) throws IOException{
		List<String> frequency = FileUtils.readLines(new File("D:/temp/"+type+"/frequencyitem.txt"),"UTF-8");
		List<String> fileLines = new ArrayList<String>();
		Map<String,Integer> countMap = new HashMap<String,Integer>();
		Map<String,Integer> changeMap = new HashMap<String,Integer>();
		
		for(String str:frequency){
			countMap.put(str, 0);
			changeMap.put(str, 0);
		}
		
		CommentWordRepository commentWordRepo = RepositoryFactory.getCommentWordRepository();
		
		String[] projects = new String[]{"jhotdraw","jedit","ejbca","htmlunit","freecol","kablink","jamwiki","opennms"};
		for(String project:projects){
			
			List<CommentWord> commentWordList = commentWordRepo.findByProjectAndType(project, type);
			for(CommentWord commentWord:commentWordList){
				
				if(commentWord.getCommentID()%3==0){
					continue;
				}
				List<String> words = null;
				if(type.equals("oldcomment")){
					words = commentWord.getOldCommentWords();
				}
				if(type.equals("newcode")){
					words = commentWord.getNewCodeWords();
				}
				if(type.equals("oldcode")){
				    words = commentWord.getOldCodeWords();
				}
				for(String str:frequency){
					String[] temps = str.split(";");
					boolean flag = true;
					for(String str2:temps){
						if(!words.contains(str2)){
							flag = false;
							break;
						}
					}
					if(flag){
						countMap.put(str, countMap.get(str)+1);
						if(commentWord.isIschange()){
							changeMap.put(str, changeMap.get(str)+1);
						}
					}
				}
			}
			System.out.println(project+" is done.");
		}
		for(Map.Entry<String, Integer> entry:countMap.entrySet()){
			fileLines.add(entry.getKey()+","+entry.getValue()+","+changeMap.get(entry.getKey()));
		}
		FileUtils.writeLines(new File("D:/temp/"+type+"/frequency.csv"), fileLines);
		
	}

}
