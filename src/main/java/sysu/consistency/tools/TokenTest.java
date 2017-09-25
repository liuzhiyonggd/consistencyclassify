package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.bean.Token;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentRepository;

public class TokenTest {
	public static void main(String[] args) throws IOException {
		CommentRepository commentRepo = RepositoryFactory.getCommentRepository();
		
		List<Integer> notchangeCommentIDList = new ArrayList<Integer>();
		List<Integer> changeCommentIDList = new ArrayList<Integer>();
		
		for(int commentID = 1;commentID<=111180;commentID++){
			if(commentID%10000==0){
				System.out.println(commentID+" is done.");
			}
			CommentEntry comment = commentRepo.findASingleByCommentID(commentID);
			List<Token> newTokens = comment.getNewToken();
			List<Token> oldTokens = comment.getOldToken();
			
			if(newTokens.size()==oldTokens.size()){
				boolean flag = true;
				for(int i=0;i<newTokens.size();i++){
					if(!newTokens.get(i).getTokenName().equals(oldTokens.get(i).getTokenName())){
						flag = false;
						break;
					}
				}
				if(flag){
					notchangeCommentIDList.add(comment.getCommentID());
				}else{
					changeCommentIDList.add(comment.getCommentID());
				}
			}
			else{
				changeCommentIDList.add(comment.getCommentID());
			}
		}
		
		List<String> r1 = new ArrayList<String>();
		for(Integer i : notchangeCommentIDList){
			r1.add(i+"");
		}
		List<String> r2 = new ArrayList<String>();
		for(Integer i : changeCommentIDList){
			r2.add(i+"");
		}
		FileUtils.writeLines(new File("D:/temp/notchangeID.txt"), r1);
		FileUtils.writeLines(new File("D:/temp/changeID.txt"), r2);
	}
	

}
