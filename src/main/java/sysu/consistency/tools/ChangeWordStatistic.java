package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.bean.CommentWord;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentRepository;
import sysu.consistency.db.repository.CommentWordRepository;

public class ChangeWordStatistic {
	public static void main(String[] args) {
		CommentWordRepository wordRepo = RepositoryFactory.getCommentWordRepository();
		CommentRepository commentRepo = RepositoryFactory.getCommentRepository();
		
		Map<String,Integer> addMap = new HashMap<String,Integer>();
		Map<String,Integer> addMap2 = new HashMap<String,Integer>();
		
		Map<String,Integer> deleteMap = new HashMap<String,Integer>();
		Map<String,Integer> deleteMap2 = new HashMap<String,Integer>();
		
		for(int i=1;i<=111180;i++){
			if(i%3==0){
				continue;
			}
			CommentEntry comment = commentRepo.findASingleByCommentID(i);
			CommentWord word = wordRepo.findASingleByCommentIDAndType(i, "Line");
			
			if(comment.isChange2()){
				continue;
			}
			
			List<String> addWordList = word.getAddWords();
			List<String> deleteWordList = word.getDeleteWords();
			
			String temp1 = "";
			for(String str:addWordList){
				temp1 += str+";";
			}
			if(addMap.containsKey(temp1)){
				addMap.put(temp1, addMap.get(temp1)+1);
				if(word.isIschange()){
					addMap2.put(temp1, addMap2.get(temp1)+1);
				}
			}else{
				addMap.put(temp1, 1);
				if(word.isIschange()){
					addMap2.put(temp1,1);
				}else{
					addMap2.put(temp1,0);
				}
			}
			
			String temp2 = "";
			for(String str:deleteWordList){
				temp2 += str+";";
			}
			if(deleteMap.containsKey(temp2)){
				deleteMap.put(temp2, deleteMap.get(temp2)+1);
				if(word.isIschange()){
					deleteMap2.put(temp2, deleteMap2.get(temp2)+1);
				}
			}else{
				deleteMap.put(temp2, 1);
				if(word.isIschange()){
					deleteMap2.put(temp2,1);
				}else{
					deleteMap2.put(temp2,0);
				}
			}
		}
		
		List<String> output1 = new ArrayList<String>();
		for(Map.Entry<String, Integer> entry:addMap.entrySet()){
			output1.add(entry.getKey()+","+entry.getValue()+","+addMap2.get(entry.getKey()));
		}
		
		List<String> output2 = new ArrayList<String>();
		for(Map.Entry<String, Integer> entry:deleteMap.entrySet()){
			output2.add(entry.getKey()+","+entry.getValue()+","+deleteMap2.get(entry.getKey()));
		}
		
		try {
			FileUtils.writeLines(new File("D:/work/4.9/addMap.csv"), output1);
			FileUtils.writeLines(new File("D:/work/4.9/deleteMap.csv"), output2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
