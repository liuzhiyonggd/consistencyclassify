package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.CommentWord;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentWordRepository;
import sysu.consistency.tools.splitword.WordSpliter;

public class GenerateWordFile {
	public static void main(String[] args) throws IOException {
		CommentWordRepository wordRepo = RepositoryFactory.getCommentWordRepository();
		
		List<String> codeDoc = new ArrayList<String>();
		List<String> commentDoc = new ArrayList<String>();
		
		List<String> codeIdList = new ArrayList<String>();
		List<String> commentIdList = new ArrayList<String>();
		
		int codeNum = 0;
		int commentNum = 0;
		List<String> idList = FileUtils.readLines(new File("D:/work/5.4/newid.txt"),"UTF-8");
		for(String id:idList){
			int commentID = Integer.parseInt(id);
			CommentWord word = wordRepo.findASingleByCommentIDAndType(commentID,"Line");
			List<String> oldCodes = word.getOldCodeWords();
			List<String> newCodes = word.getNewCodeWords();
			
			if(newCodes.size()>0&&oldCodes.size()>0){
				codeNum = codeNum + 2;
				
				StringBuilder newCode = new StringBuilder();
				StringBuilder oldCode = new StringBuilder();
				
				for(String str:newCodes){
					newCode.append(str+" ");
				}
				for(String str:oldCodes){
					oldCode.append(str+" ");
				}
				codeDoc.add(newCode.toString());
				codeDoc.add(oldCode.toString());
				codeIdList.add(word.getCommentID()+"");
			}
			
			List<String> oldComments = word.getOldCommentWords();
			if(oldComments.size()>0){
				commentNum++;
				StringBuilder comment = new StringBuilder();
				for(String str:oldComments){
					comment.append(str+" ");
				}
				commentDoc.add(comment.toString());
				commentIdList.add(word.getCommentID()+"");
			}
		}
		codeDoc.add(0, codeNum+"");
		commentDoc.add(0,commentNum+"");
		
		FileUtils.writeLines(new File("D:/work/5.5/codedoc.txt"), codeDoc);
		FileUtils.writeLines(new File("D:/work/5.5/commentdoc.txt"), commentDoc);
		FileUtils.writeLines(new File("D:/work/5.5/commentid.txt"), commentIdList);
		FileUtils.writeLines(new File("D:/work/5.5/codeid.txt"), codeIdList);
	}

}
