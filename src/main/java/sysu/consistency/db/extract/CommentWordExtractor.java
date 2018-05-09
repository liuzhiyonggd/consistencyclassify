package sysu.consistency.db.extract;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.ClassMessage;
import sysu.consistency.db.bean.CodeComment;
import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.bean.CommentWord;
import sysu.consistency.db.bean.Token;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.ClassMessageRepository;
import sysu.consistency.db.repository.CommentRepository;
import sysu.consistency.db.repository.CommentWordRepository;
import sysu.consistency.tools.splitword.WordSpliter;

public class CommentWordExtractor {

	public static void extract() throws IOException {

		
		CommentRepository commentRepository = RepositoryFactory.getCommentRepository();
		CommentWordRepository commentWordRepository = RepositoryFactory.getCommentWordRepository();
		ClassMessageRepository classRepository = RepositoryFactory.getClassRepository();
		
		List<String> lines = FileUtils.readLines(new File("file/data_1_2_5_6_3.csv"), "UTF-8");
		List<Integer> idList = new ArrayList<Integer>();
		for(String str:lines) {
			int id = Integer.parseInt(str.split(",")[0]);
			idList.add(id);
		}
		
		for (int commentID:idList) {
			
			if(commentID%1000==0){
				System.out.println(" inserted "+commentID);
			}
			
			CommentEntry comment = commentRepository.findASingleByCommentID(commentID);
			if(comment==null) {
				continue;
			}
			ClassMessage clazz = classRepository.findASingleByProjectAndCommitIDAndClassName(comment.getProject(), comment.getCommitID(), comment.getClassName());
			
			List<CodeComment> newClassCommentList = clazz.getNewComment();
			List<CodeComment> oldClassCommentList = clazz.getOldComment();
			
			//记录注释出现的行号
			Set<Integer> newCommentLineSet = new HashSet<Integer>();
			Set<Integer> oldCommentLineSet = new HashSet<Integer>();
			
			for(CodeComment c:newClassCommentList) {
				if(c.getStartLine()>=comment.getNew_comment_startLine()&&c.getEndLine()<=comment.getNew_scope_endLine()) {
					for(int i=c.getStartLine();i<=c.getEndLine();i++) {
						newCommentLineSet.add(i);
					}
				}
			}
			
			for(CodeComment c:oldClassCommentList) {
				if(c.getStartLine()>=comment.getOld_comment_startLine()&&c.getEndLine()<=comment.getOld_scope_endLine()) {
					for(int i=c.getStartLine();i<=c.getEndLine();i++) {
						oldCommentLineSet.add(i);
					}
				}
			}
			
			
			List<String> newComment = comment.getNewComment();
			List<String> oldComment = comment.getOldComment();
			List<String> newCode = new ArrayList<String>();
			List<String> oldCode = new ArrayList<String>();
			
			List<Token> newTokenList = comment.getNewToken();
			List<Token> oldTokenList = comment.getOldToken();
			
			//记录代码出现的行号（可能包含注释，故需要判断该行是否是注释，如果不跟注释同一行，才算做是代码行）
			Set<Integer> newCodeLineSet = new HashSet<Integer>();
			Set<Integer> oldCodeLineSet = new HashSet<Integer>();
			
			for(Token token:newTokenList) {
				for(int i=token.getStartLine();i<=token.getEndLine();i++) {
					newCodeLineSet.add(i);
				}
			}
			for(Token token:oldTokenList) {
				for(int i=token.getStartLine();i<=token.getEndLine();i++) {
					oldCodeLineSet.add(i);
				}
			}
			for(int i=0;i<comment.getNewCode().size();i++) {
				if(newCodeLineSet.contains(i+comment.getNew_comment_endLine()+1)&&!newCommentLineSet.contains(i+comment.getNew_comment_endLine()+1)) {
					newCode.add(comment.getNewCode().get(i));
				}
			}
			for(int i=0;i<comment.getOldCode().size();i++) {
				if(oldCodeLineSet.contains(i+comment.getOld_comment_endLine()+1)&&!oldCommentLineSet.contains(i+comment.getOld_comment_endLine()+1)) {
					oldCode.add(comment.getOldCode().get(i));
				}
			}
			

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
