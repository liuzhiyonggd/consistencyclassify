package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentRepository;

public class RefactorID {
	public static void main(String[] args) {
		
		CommentRepository commentRepo = RepositoryFactory.getCommentRepository();
		List<String> commentIDList = new ArrayList<String>();
		for(int i=1;i<111180;i++){
			CommentEntry comment = commentRepo.findASingleByCommentID(i);
			if(comment.isRefactor1()){
				commentIDList.add(i+"");
			}
		}
		try {
			FileUtils.writeLines(new File("D:/temp/refactorID.txt"), commentIDList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
