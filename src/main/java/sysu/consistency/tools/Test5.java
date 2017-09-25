package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentRepository;

public class Test5 {

	public static void main(String[] args) throws IOException {
		CommentRepository commentRepo = RepositoryFactory.getCommentRepository();
		List<String> output = new ArrayList<String>();
		for(int i=1;i<=111180;i++){
			CommentEntry comment = commentRepo.findASingleByCommentID(i);
			List<String> newComments = comment.getNewComment();
			List<String> oldComments = comment.getOldComment();
			
			List<String> newWords = new ArrayList<String>();
			List<String> oldWords = new ArrayList<String>();
			
			String splitToken = " .,;:/&|`~%+=-*<>$#@!^\\()[]{}''\"\r\n\t";
			
			for(String str:newComments){
			    StringTokenizer st = new StringTokenizer(str,splitToken,false);
			    while(st.hasMoreTokens()){
			    	newWords.add(st.nextToken());
			    }
			}
			for(String str:oldComments){
				StringTokenizer st = new StringTokenizer(str,splitToken,false);
			    while(st.hasMoreTokens()){
			    	oldWords.add(st.nextToken());
			    }
			}
			if(newWords.size()<3&&oldWords.size()<3){
				output.add(i+"");
			}
			
			if(i%10000==0){
				System.out.println(i+" is done.");
			}
		}
		
		List<String> commentIDList = FileUtils.readLines(new File("D:/work/4.20/wordlt3.txt"), "UTF-8");
		for(String str:commentIDList){
			int commentID = Integer.parseInt(str);
			CommentEntry comment = commentRepo.findASingleByCommentID(commentID);
			String newComment = "";
			for(String str2:comment.getNewComment()){
				newComment += str2+" ";
			}
			String oldComment = "";
			for(String str2:comment.getOldComment()){
				oldComment += str2+" ";
			}
			output.add(comment.getCommentID()+","+newComment+","+oldComment);
		}
		FileUtils.writeLines(new File("D:/work/4.20/wordlt3.csv"), output);
	}
}
