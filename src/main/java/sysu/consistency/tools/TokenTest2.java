package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentRepository;

public class TokenTest2 {
	
	public static void main(String[] args) throws IOException {
		List<String> commentIDList = FileUtils.readLines(new File("D:/temp/notchangeID.txt"),"UTF-8");
		int count = 0;
		CommentRepository commentRepo = RepositoryFactory.getCommentRepository();
		for(String str:commentIDList){
			int commentID = Integer.parseInt(str);
			CommentEntry comment = commentRepo.findASingleByCommentID(commentID);
			if(comment.isChange()){
				count++;
				System.out.println(comment.getCommentID());
			}
		}
		System.out.println(count+"");
	}

}
