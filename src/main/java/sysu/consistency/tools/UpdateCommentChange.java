package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.bean.CommentWord;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentRepository;
import sysu.consistency.db.repository.CommentWordRepository;
import sysu.consistency.db.tool.CommentEntryTool;

public class UpdateCommentChange {
	
	public static void main(String[] args) throws IOException {
		CommentRepository commentRepo = RepositoryFactory.getCommentRepository();
		DBCollection comments = new Mongo("172.18.217.106").getDB("sourcebase").getCollection("comment3");
		CommentWordRepository wordRepo = RepositoryFactory.getCommentWordRepository();
		List<String> idList = FileUtils.readLines(new File("D:/work/5.4/newid.txt"),"UTF-8");
		int count=0;
		for(String str:idList){
			count++;
			if(count%1000==0){
				System.out.println("update "+count + " is done.");
			}
			int id = Integer.parseInt(str);
			CommentEntry comment = commentRepo.findASingleByCommentID(id);
			CommentWord word = wordRepo.findASingleByCommentIDAndType(id, "Line");
			
			List<String> newComment = word.getNewCommentWords();
			List<String> oldComment = word.getOldCommentWords();
			
			boolean ischange = false;
			if(newComment.size()!=oldComment.size()){
				ischange = true;
			}else{
				for(int i=0;i<newComment.size();i++){
					if(!newComment.get(i).equals(oldComment.get(i))){
						ischange = true;
						break;
					}
				}
			}
			
			
			comment.setChange(ischange);
			
			DBObject obj = CommentEntryTool.commentEntry2DBObject(comment);
			comments.update(new BasicDBObject().append("comment_id", id), obj);
		}
	}

}
