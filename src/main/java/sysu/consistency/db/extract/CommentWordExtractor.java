package sysu.consistency.db.extract;

import java.io.IOException;
import java.util.List;

import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.bean.CommentWord;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentRepository;
import sysu.consistency.db.repository.CommentWordRepository;
import sysu.consistency.tools.splitword.WordSpliter;

public class CommentWordExtractor {

	public static void extract() {

		
		CommentRepository commentRepository = RepositoryFactory.getCommentRepository();
		CommentWordRepository commentWordRepository = RepositoryFactory.getCommentWordRepository();
		
		
		for (int commentID=1;commentID<48171;commentID++) {
			CommentEntry comment = commentRepository.findASingleByCommentID(commentID);
			if(commentID%1000==0){
				System.out.println(" inserted "+commentID);
			}
			List<String> newComment = comment.getNewComment();
			List<String> oldComment = comment.getOldComment();
			List<String> newCode = comment.getNewCode();
			List<String> oldCode = comment.getOldCode();

			StringBuffer newCommentStringBuffer = new StringBuffer();
			for (String str : newComment) {
				newCommentStringBuffer.append(str + " ");
			}
			StringBuffer oldCommentStringBuffer = new StringBuffer();
			for (String str : oldComment) {
				oldCommentStringBuffer.append(str + " ");
			}
			StringBuffer newCodeStringBuffer = new StringBuffer();
			for (String str : newCode) {
				newCodeStringBuffer.append(str + " ");
			}
			StringBuffer oldCodeStringBuffer = new StringBuffer();
			for (String str : oldCode) {
				oldCodeStringBuffer.append(str + " ");
			}

			List<String> newCommentWordList = WordSpliter.split(newCommentStringBuffer.toString());
			newCommentStringBuffer = new StringBuffer();
			for (String str : newCommentWordList) {
				newCommentStringBuffer.append(str + " ");
			}
			List<String> oldCommentWordList = WordSpliter.split(oldCommentStringBuffer.toString());
			oldCommentStringBuffer = new StringBuffer();
			for (String str : oldCommentWordList) {
				oldCommentStringBuffer.append(str + " ");
			}
			List<String> newCodeWordList = WordSpliter.split(newCodeStringBuffer.toString());
			newCodeStringBuffer = new StringBuffer();
			for (String str : newCodeWordList) {
				newCodeStringBuffer.append(str + " ");
			}
			List<String> oldCodeWordList = WordSpliter.split(oldCodeStringBuffer.toString());
			oldCodeStringBuffer = new StringBuffer();
			for (String str : oldCodeWordList) {
				oldCodeStringBuffer.append(str + " ");
			}

			CommentWord commentWord = new CommentWord();
			commentWord.setCommentID(comment.getCommentID());
			commentWord.setType(comment.getType());
			commentWord.setProject(comment.getProject());
			commentWord.setIschange(comment.isChange2());
			commentWord.setNewCommentWords(newCommentWordList);
			commentWord.setOldCommentWords(oldCommentWordList);
			commentWord.setNewCodeWords(newCodeWordList);
			commentWord.setOldCodeWords(oldCodeWordList);

			commentWordRepository.insert(commentWord);
		}

	}
	
	public static void main(String[] args) throws IOException {

			CommentWordExtractor.extract();
	}

}
