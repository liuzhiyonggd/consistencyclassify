package sysu.consistency.tools.statistic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.bean.DiffType;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentRepository;

public class Questionare {
	
	public static void generate(List<CommentEntry> commentList,String filename) throws IOException{
		
		File outputFile = new File("D:/paper/comment/statistic/"+filename+".csv");
		
		List<String> fileLines = new ArrayList<String>();
		
		for(CommentEntry comment:commentList){
			
			int entryNewStartLine = comment.getNew_comment_endLine()+1;
			int entryOldStartLine =comment.getOld_comment_endLine()+1;
			List<Integer> newChangeIndexList = new ArrayList<Integer>();
			List<Integer> oldChangeIndexList = new ArrayList<Integer>();
			for(DiffType diff:comment.getDiffList()){
				if(diff.getNewStartLine()>0){
					for(int k=diff.getNewStartLine();k<diff.getNewEndLine()+1;k++){
						int newChangeIndex = k-entryNewStartLine;
						if(!newChangeIndexList.contains(newChangeIndex)){
							newChangeIndexList.add(newChangeIndex);
						}
					}
				}
				if(diff.getOldStartLine()>0){
					for(int k=diff.getOldStartLine();k<diff.getOldEndLine()+1;k++){
						int oldChangeIndex = k-entryOldStartLine;
						if(!oldChangeIndexList.contains(oldChangeIndex)){
							oldChangeIndexList.add(oldChangeIndex);
						}
					}
				}
				
			}
			
			StringBuffer oldCommentBuffer = new StringBuffer();
			List<String> oldCommentList = comment.getOldComment();
			for(String str:oldCommentList){
				oldCommentBuffer.append(str+"<nextline>");
			}
		    String oldComment = oldCommentBuffer.toString().replace(",", "��");
			
		    StringBuffer newCommentBuffer = new StringBuffer();
			List<String> newCommentList = comment.getNewComment();
			for(String str:newCommentList){
				newCommentBuffer.append(str+"<nextline>");
			}
		    String newComment = newCommentBuffer.toString().replace(",", "��");
		    
			StringBuilder oldCodeBuilder = new StringBuilder();
			List<String> oldCodeList = comment.getOldCode();
			for(int k=0;k<oldCodeList.size();k++){
				String str = oldCodeList.get(k);
				if(oldChangeIndexList.contains(k)){
					oldCodeBuilder.append("<font color:red>"+str+"</font><nextline>");
				}
				else{
					oldCodeBuilder.append(str+"<nextline>");
				}
			}
			String oldCode = oldCodeBuilder.toString().replace(",", "��");
			
			StringBuilder newCodeBuilder = new StringBuilder();
			List<String> newCodeList = comment.getNewCode();
			for(int k=0;k<newCodeList.size();k++){
				String str = newCodeList.get(k);
				if(newChangeIndexList.contains(k)){
					newCodeBuilder.append("<font color:red>"+str+"</font><nextline>");
				}
				else{
					newCodeBuilder.append(str+"<nextline>");
				}
			}
			String newCode = newCodeBuilder.toString().replace(",", "��");
			
			fileLines.add(comment.getCommentID()+","+comment.getType()+","+oldCode+","+newCode+","+oldComment+","+newComment+","+comment.isChange());
			
		}
		FileUtils.writeLines(outputFile, fileLines);
	}
	
	
	public static void main(String[] args) throws IOException {
		
		CommentRepository commentRepo = RepositoryFactory.getCommentRepository();
		List<CommentEntry> commentList = commentRepo.findByIsChange(true);
		
		generate(commentList,"line_change");
	}

}
