package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentRepository;

public class CommentLine {
	
	public static void main(String[] args) throws IOException {
		CommentRepository commentRepo = RepositoryFactory.getCommentRepository();
		List<String> output = new ArrayList<String>();

		for(int i=1;i<=111180;i++){
			
			CommentEntry comment = commentRepo.findASingleByCommentID(i);
			
			List<String> oldCommentList = comment.getOldComment();
			int line = oldCommentList.size();
			int allLine = comment.getOld_scope_endLine()-comment.getOld_scope_startLine()+1;
			output.add(comment.getCommentID()+","+line+","+allLine+","+comment.isChange());
			if(i%10000==0){
				FileUtils.writeLines(new File("D:/work/5.7/commentLine.csv"), output);
				System.out.println(i+" is done.");
			}
		}
		
		FileUtils.writeLines(new File("D:/work/5.7/commentLine.csv"), output);
	}

}
