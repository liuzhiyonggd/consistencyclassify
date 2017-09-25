package sysu.consistency.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

import sysu.consistency.db.bean.ClassMessage;
import sysu.consistency.db.bean.CodeComment;
import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.bean.Line;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.ClassMessageRepository;
import sysu.consistency.db.tool.CommentEntryTool;
import sysu.consistency.tools.splitword.StringTools;
import sysu.consistency.tools.splitword.WordSpliter;

public class UpdateCommentWord {
	
	
	private static DBCollection comments = new Mongo("172.18.217.106").getDB("sourcebase").getCollection("comment3");
	
	public static void main(String[] args) {
		
		ClassMessageRepository classRepo = RepositoryFactory.getClassRepository();
		for(int i=1;i<=111180;i++){
			
			if(i%1000==0){
				System.out.println(i+" is done.");
			}
			
			DBObject query = new BasicDBObject();
			query.put("comment_id", i);
			CommentEntry comment = CommentEntryTool.DBObject2CommentEntry(comments.findOne(query));
			ClassMessage clazz = classRepo.findASingleByProjectAndCommitIDAndClassName(comment.getProject(), comment.getCommitID(), comment.getClassName());
			
			List<Line> newCode = clazz.getNewCode();
			List<Line> oldCode = clazz.getOldCode();
			
			List<CodeComment> newComment = clazz.getNewComment();
			
			List<CodeComment> oldComment = clazz.getOldComment();
			
			
			List<String> newCommentList = new ArrayList<String>();
			List<String> oldCommentList = new ArrayList<String>();
			
			int newStartLine = comment.getNew_scope_startLine();
			int newEndLine = comment.getNew_scope_endLine();
			int oldStartLine = comment.getOld_scope_startLine();
			int oldEndLine = comment.getOld_scope_endLine();
			
			for(CodeComment cc:newComment){
				if(cc.getStartLine()>=newStartLine&&cc.getEndLine()<=newEndLine){
					List<String> strList = new ArrayList<String>();
					String sentense = "";
					for(int index=cc.getStartLine()-1;index<cc.getEndLine();index++){
	                    strList.add(newCode.get(index).getLine());
	                    sentense += newCode.get(index).getLine()+" ";
					}
					if(StringTools.isCode(strList)){
						continue;
					}
					
					String splitToken = " .,;:/&|`~%+=-*<>$#@!^\\()[]{}''\"\r\n\t";
					StringTokenizer st = new StringTokenizer(sentense,splitToken,false);
					
					if(st.countTokens()<3){
						continue;
					}
					newCommentList.addAll(strList);
				}
			}
			
			for(CodeComment cc:oldComment){
				if(cc.getStartLine()>=oldStartLine&&cc.getEndLine()<=oldEndLine){
					List<String> strList = new ArrayList<String>();
					String sentense = "";
					for(int index=cc.getStartLine()-1;index<cc.getEndLine();index++){
	                    strList.add(oldCode.get(index).getLine());
	                    sentense += oldCode.get(index).getLine()+" ";
					}
					if(StringTools.isCode(strList)){
						continue;
					}
					String splitToken = " .,;:/&|`~%+=-*<>$#@!^\\()[]{}''\"\r\n\t";
					StringTokenizer st = new StringTokenizer(sentense,splitToken,false);
					
					if(st.countTokens()<3){
						continue;
					}
					oldCommentList.addAll(strList);
				}
			}
			
			comment.setNewComment(newCommentList);
			comment.setOldComment(oldCommentList);
			
			comments.update(query, CommentEntryTool.commentEntry2DBObject(comment));
		}
	}

}
