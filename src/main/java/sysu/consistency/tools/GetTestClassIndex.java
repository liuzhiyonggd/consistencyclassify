package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.CommentWord;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentWordRepository;

public class GetTestClassIndex {
	
	public static void main(String[] args) throws IOException {
		List<String> testList = FileUtils.readLines(new File("D:/work/5.9/test2.arff"),"UTF-8");
		
		CommentWordRepository wordRepo = RepositoryFactory.getCommentWordRepository();
		
		List<String> newTestList = new ArrayList<String>();
		int index = 0;
		for(String str:testList){
			index++;
			newTestList.add(str);
			if(str.equals("@data")){
				break;
			}
		}
		for(;index<testList.size();index++){
			if(index%1000==0){
				System.out.println(index+" is done.");
			}
			String[] temps = testList.get(index).split(" ");
			int commentID = Integer.parseInt(temps[0]);
			int classIndex = 0;
			CommentWord word = wordRepo.findASingleByCommentIDAndType(commentID, "Line");
			List<String> newCommentWordList = word.getNewCommentWords();
			List<String> oldCommentWordList = word.getOldCommentWords();
			
			if(newCommentWordList.size()!=oldCommentWordList.size()){
				classIndex = 1;
			}else{
				for(int i=0;i<newCommentWordList.size();i++){
					if(!newCommentWordList.get(i).equals(oldCommentWordList.get(i))){
						classIndex = 1;
						break;
					}
				}
			}
			String vector = "";
			for(int i=0;i<temps.length-1;i++){
				vector += temps[i]+" ";
			}
			vector += classIndex+"";
			newTestList.add(vector);
		}
		
		FileUtils.writeLines(new File("D:/work/5.10/allTest.arff"), newTestList);
	}

}
