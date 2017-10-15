package sysu.consistency.tools.statistic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentRepository;

public class Test {
	
	public static void main(String[] args) throws IOException {
		List<String> output = new ArrayList<String>();
		
		CommentRepository commentRepo = RepositoryFactory.getCommentRepository();
		for(int commentID=1;commentID<48171;commentID++) {
			if(commentID%10000==0) {
				System.out.println(commentID+" is done.");
			}
			CommentEntry comment = commentRepo.findASingleByCommentID(commentID);
			List<String> oldCommentList = comment.getOldComment();
			String oldComment = "";
			for(String str:oldCommentList) {
				oldComment += str+" ";
			}
			output.add(commentID+","+oldComment);
		}
		FileUtils.writeLines(new File("D:/output.txt"), output);
	}

}
