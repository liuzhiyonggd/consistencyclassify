package sysu.consistency.db.tool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.bean.DiffType;
import sysu.consistency.db.bean.Token;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentRepository;

public class ScopeReseter {
	
	public static void main(String[] args) throws IOException {
		
		CommentRepository commRepo = RepositoryFactory.getCommentRepository();
		
		List<String> oldScopeLines = FileUtils.readLines(new File("file/CommentScopeEndLine_old.csv"),"UTF-8");
		Map<Integer,Integer> oldScopeMap = new HashMap<Integer,Integer>();
		
		for(String str:oldScopeLines) {
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			int endLine = Integer.parseInt(temps[1]);
			oldScopeMap.put(commentID, endLine);
		}
		
		List<String> newScopeLines = FileUtils.readLines(new File("file/CommentScopeEndLine_new.csv"),"UTF-8");
		Map<Integer,Integer> newScopeMap = new HashMap<Integer,Integer>();
		for(String str:newScopeLines) {
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			int endLine = Integer.parseInt(temps[1]);
			newScopeMap.put(commentID, endLine);
		}
		
		Set<Integer> idSet = new TreeSet<Integer>();
		
		for(int id :oldScopeMap.keySet()) {
			if(newScopeMap.containsKey(id)) {
				idSet.add(id);
			}
		}
		int count = 0;
		for(int id:idSet) {
			count ++;
			if(count%1000==0) {
				System.out.println(count+" is done.");
			}
			CommentEntry comment = commRepo.findASingleByCommentID(id);
			int oldScopeEndLine = oldScopeMap.get(id);
			int newScopeEndLine = newScopeMap.get(id);
			
			comment.setOld_scope_endLine(oldScopeEndLine);
			comment.setNew_scope_endLine(newScopeEndLine);
			
			List<DiffType> diffList = comment.getDiffList();
			List<DiffType> newDiffList = new ArrayList<DiffType>();
			
			for(DiffType diff:diffList) {
				if(diff.getNewStartLine()>0&&diff.getNewEndLine()<=newScopeEndLine
						&&diff.getOldStartLine()>0&&diff.getOldEndLine()<=oldScopeEndLine) {
					newDiffList.add(diff);
				}else if(diff.getNewStartLine()<=0&&diff.getOldStartLine()>0&&diff.getOldEndLine()<=oldScopeEndLine) {
					newDiffList.add(diff);
				}else if(diff.getOldStartLine()<=0&&diff.getNewStartLine()>0&&diff.getNewEndLine()<=newScopeEndLine) {
					newDiffList.add(diff);
				}
			}
			comment.setDiffList(newDiffList);
			
			List<Token> newTokenList = comment.getNewToken();
			List<Token> newNewTokenList = new ArrayList<Token>();
			for(Token token:newTokenList) {
				if(token.getEndLine()<=newScopeEndLine) {
					newNewTokenList.add(token);
				}
			}
			comment.setNewToken(newNewTokenList);
			
			List<Token> oldTokenList = comment.getOldToken();
			List<Token> newOldTokenList = new ArrayList<Token>();
			for(Token token:oldTokenList) {
				if(token.getEndLine()<=oldScopeEndLine) {
					newOldTokenList.add(token);
				}
			}
			comment.setOldToken(newOldTokenList);
			
			List<String> newCodeList = comment.getNewCode();
			List<String> newNewCodeList = new ArrayList<String>();
			for(int i=0;i<newScopeEndLine-comment.getNew_comment_endLine();i++) {
				newNewCodeList.add(newCodeList.get(i));
			}
			comment.setNewCode(newNewCodeList);
//			for(String str:newNewCodeList) {
//				System.out.println(str);
//			}
			
			List<String> oldCodeList = comment.getOldCode();
			List<String> newOldCodeList = new ArrayList<String>();
			for(int i=0;i<oldScopeEndLine-comment.getOld_comment_endLine();i++) {
				newOldCodeList.add(oldCodeList.get(i));
			}
			comment.setOldCode(newOldCodeList);
//			for(String str:newOldCodeList) {
//				System.out.println(str);
//			}
			
			
			commRepo.save(comment);
			
		}
		
		
	}

}
