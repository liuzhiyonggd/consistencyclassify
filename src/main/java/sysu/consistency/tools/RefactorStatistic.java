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

public class RefactorStatistic {
	public static void main(String[] args) throws IOException {
		CommentWordRepository wordRepo = RepositoryFactory.getCommentWordRepository();
		CommentRepository commentRepo = RepositoryFactory.getCommentRepository();
		List<String> idList = FileUtils.readLines(new File("D:/work/5.9/newid.txt"),"UTF-8");
		Map<String,Integer> tokenMap = new HashMap<String,Integer>();
		Map<String,Integer> changeMap = new HashMap<String,Integer>();
		String[] tokens = new String[]{"todo","fixme","xxx","bug","bugfix"};
		for(String str:tokens){
			tokenMap.put(str, 0);
			changeMap.put(str, 0);
		}
		
		int count = 0;
		for(String str:idList){
			count++;
			if(count%10000==0){
				System.out.println(count+" is done.");
			}
			int commentID = Integer.parseInt(str);
			CommentWord word = wordRepo.findASingleByCommentIDAndType(commentID, "Line");
			CommentEntry comment = commentRepo.findASingleByCommentID(commentID);
			List<String> commentWordList = word.getOldCommentWords();
			for(String token:tokens){
				for(String w:commentWordList){
					if(w.equals(token)){
						tokenMap.put(token, tokenMap.get(token)+1);
						if(comment.isChange()){
							changeMap.put(token, changeMap.get(token)+1);
						}
					}
				}
			}
		}
		List<String> output = new ArrayList<String>();
		for(Map.Entry<String, Integer> entry:tokenMap.entrySet()){
			output.add(entry.getKey()+","+entry.getValue()+","+changeMap.get(entry.getKey()));
		}
		FileUtils.writeLines(new File("D:/work/5.9/tokenstatistic.csv"), output);
	}

}
