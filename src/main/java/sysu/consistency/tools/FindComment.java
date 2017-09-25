package sysu.consistency.tools;

import java.util.List;

import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.bean.CommentWord;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentWordRepository;

public class FindComment {
	
	public static void main(String[] args) {
		
		CommentWordRepository wordRepo = RepositoryFactory.getCommentWordRepository();
		
		List<CommentWord> commentWordList = wordRepo.findByProject("ejbca");
		String[] commentSnippet = new String[]{"fill","log","configuration","data","value","entry","constant","default"};
//		String commentSnippet = "add link to user page and comments page";
		for(CommentWord word : commentWordList){
			List<String> oldComments = word.getOldCommentWords();
			int count = 0;
			for(String w:commentSnippet){
				if(oldComments.contains(w)){
					count ++;
				}
			}
			if(count>6){
				System.out.println(word.getCommentID()+"");
			}
		}
	}

}
