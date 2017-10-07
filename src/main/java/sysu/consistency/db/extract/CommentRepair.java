package sysu.consistency.db.extract;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

import sysu.consistency.db.bean.ClassMessage;
import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.bean.DiffType;
import sysu.consistency.db.bean.Line;
import sysu.consistency.db.bean.Token;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.ClassMessageRepository;

public class CommentRepair {
	private static DBCollection comments = new Mongo("localhost",27017).getDB("sourcebase").getCollection("comment3");
	private static ClassMessageRepository classRepo = RepositoryFactory.getClassRepository();
	private static List<String> removeCommentIDList = new ArrayList<String>();
	private static List<String> updateCommentList = new ArrayList<String>();
	public static void repair(List<CommentEntry> commentList){
		
		for(CommentEntry comment2:commentList){
			for(CommentEntry comment1:commentList){
				if(comment1.getCommentID()==comment2.getCommentID()){
					continue;
				}
				
				if(comment1.getNew_scope_startLine()<=comment2.getNew_scope_startLine()&&comment1.getNew_scope_endLine()>=comment2.getNew_scope_endLine()
						&&comment1.getOld_scope_startLine()<=comment2.getOld_scope_startLine()&&comment1.getOld_scope_endLine()>=comment2.getOld_scope_endLine()){
					
					removeCommentIDList.add(comment2.getCommentID()+"");
				}
				
				if(comment1.getNew_scope_startLine()<=comment2.getNew_scope_startLine()&&comment1.getNew_scope_endLine()>=comment2.getNew_scope_endLine()
						&&comment1.getOld_scope_startLine()<=comment2.getOld_scope_startLine()&&comment1.getOld_scope_endLine()<comment2.getOld_scope_endLine()){
					
					removeCommentIDList.add(comment2.getCommentID()+"");

					updateCommentList.add("old"+","+comment1.getCommentID()+","+comment1.getNew_scope_endLine()+","+comment2.getOld_scope_endLine());
				}
				
				if(comment1.getNew_scope_startLine()<=comment2.getNew_scope_startLine()&&comment1.getNew_scope_endLine()<comment2.getNew_scope_endLine()
						&&comment1.getOld_scope_startLine()<=comment2.getOld_scope_startLine()&&comment1.getOld_scope_endLine()>=comment2.getOld_scope_endLine()){
					removeCommentIDList.add(comment2.getCommentID()+"");
					
					updateCommentList.add("new"+","+comment1.getCommentID()+","+comment2.getNew_scope_endLine()+","+comment1.getOld_scope_endLine());
				}
			}
			
		}
		
		
		
	}
	
	public static DBObject commentEntry2DBObject(CommentEntry comment) {
		DBObject obj = new BasicDBObject();
		obj.put("project", comment.getProject());
		obj.put("commit_id", comment.getCommitID());
		obj.put("class_name", comment.getClassName());
		obj.put("comment_id", comment.getCommentID());
		obj.put("type", comment.getType());
		obj.put("new_code", comment.getNewCode());
		obj.put("old_code", comment.getOldCode());
		obj.put("new_comment", comment.getNewComment());
		obj.put("old_comment", comment.getOldComment());
		obj.put("new_token", tokenList2DBObjectList(comment.getNewToken()));
		obj.put("old_token", tokenList2DBObjectList(comment.getOldToken()));
		obj.put("ischange", comment.isChange());
		obj.put("ischange2", comment.isChange2());
		obj.put("diffs", diffTypeList2DBObjectList(comment.getDiffList()));
		obj.put("new_scope_startline", comment.getNew_scope_startLine());
		obj.put("new_scope_endline", comment.getNew_scope_endLine());
		obj.put("old_scope_startline", comment.getOld_scope_startLine());
		obj.put("old_scope_endline", comment.getOld_scope_endLine());
		obj.put("new_comment_startline", comment.getNew_comment_startLine());
		obj.put("new_comment_endline", comment.getNew_comment_endLine());
		obj.put("old_comment_startline", comment.getOld_comment_startLine());
		obj.put("old_comment_endline", comment.getOld_comment_endLine());
		obj.put("isverify", comment.isVerify());
		obj.put("ischange_probability", comment.getIsChangeProbability());
		obj.put("refactor1", comment.isRefactor1());
		obj.put("refactor2", comment.isRefactor2());
		obj.put("refactor3", comment.isRefactor3());
		obj.put("refactor4", comment.isRefactor4());
		return obj;
	}
	
	public static DBObject token2DBObject(Token token){
		DBObject obj = new BasicDBObject();
		obj.put("token_name", token.getTokenName());
		obj.put("keyword", token.getKeyword());
		obj.put("start_line", token.getStartLine());
		obj.put("end_line", token.getEndLine());
		obj.put("hash_number", token.getHashNumber());
		return obj;
	}
	
	public static List<DBObject> tokenList2DBObjectList(List<Token> tokenList){
		List<DBObject> dbList = new ArrayList<DBObject>();
		if(tokenList!=null){
			for(Token token : tokenList){
				DBObject obj = token2DBObject(token);
				dbList.add(obj);
			}
		}
		
		return dbList;
	}
	
	public static DBObject diffType2DBObject(DiffType diff){
		DBObject obj = new BasicDBObject();
		obj.put("type", diff.getType());
		obj.put("new_start_line", diff.getNewStartLine());
		obj.put("new_end_line", diff.getNewEndLine());
		obj.put("old_start_line", diff.getOldStartLine());
		obj.put("old_end_line", diff.getOldEndLine());
		obj.put("new_hashs", diff.getNewHashList());
		obj.put("old_hashs", diff.getOldHashList());
//		obj.put("new_tokens", diff.getNewTokenList());
//		obj.put("old_tokens", diff.getOldTokenList());
		obj.put("new_keywords", diff.getNewKeywordList());
		obj.put("old_keywords", diff.getOldKeywordList());
		return obj;
	}
	
	public static List<DBObject> diffTypeList2DBObjectList(List<DiffType> diffList){
		List<DBObject> dbList = new ArrayList<DBObject>();
		if(diffList!=null){
			for(DiffType diff:diffList){
				DBObject obj = diffType2DBObject(diff);
				dbList.add(obj);
			}
		}
		
		return dbList;
	}
	
	public static CommentEntry DBObject2CommentEntry(DBObject obj) {
		CommentEntry entry = new CommentEntry();
		entry.setProject((String) obj.get("project"));
		entry.setCommitID((String) obj.get("commit_id"));
		entry.setClassName((String) obj.get("class_name"));
		entry.setCommentID((Integer) obj.get("comment_id"));
		entry.setType((String) obj.get("type"));
		entry.setNewCode((List<String>) obj.get("new_code"));
		entry.setOldCode((List<String>) obj.get("old_code"));
		entry.setNewComment((List<String>) obj.get("new_comment"));
		entry.setOldComment((List<String>) obj.get("old_comment"));
		entry.setNewToken(DBObjectList2TokenList((List<DBObject>)obj.get("new_token")));
		entry.setOldToken(DBObjectList2TokenList((List<DBObject>)obj.get("old_token")));
		entry.setChange((Boolean) obj.get("ischange"));
		entry.setChange2((Boolean)obj.get("ischange2"));
		entry.setDiffList(DBObjectList2DiffTypeList((List<DBObject>) obj.get("diffs")));
		entry.setNew_comment_startLine((Integer)obj.get("new_comment_startline"));
		entry.setNew_comment_endLine((Integer)obj.get("new_comment_endline"));
		entry.setOld_comment_startLine((Integer)obj.get("old_comment_startline"));
		entry.setOld_comment_endLine((Integer)obj.get("old_comment_endline"));
		entry.setNew_scope_startLine((Integer)obj.get("new_scope_startline"));
		entry.setNew_scope_endLine((Integer)obj.get("new_scope_endline"));
		entry.setOld_scope_startLine((Integer)obj.get("old_scope_startline"));
		entry.setOld_scope_endLine((Integer)obj.get("old_scope_endline"));
		entry.setIsChangeProbability((Double)obj.get("ischange_probability"));
		entry.setVerify((Boolean)obj.get("isverify"));	
		if(obj.get("refactor1")!=null){
			entry.setRefactor1((Boolean)obj.get("refactor1"));
		}else{
			entry.setRefactor1(false);
		}
		entry.setRefactor2(false);
		entry.setRefactor3(false);
		entry.setRefactor4(false);
		return entry;
	}
	public static List<Token> DBObjectList2TokenList(List<DBObject> dbList){
		List<Token> tokenList = new ArrayList<Token>();
		if(dbList!=null){
			for(DBObject obj:dbList){
				Token token = DBObject2Token(obj);
				tokenList.add(token);
			}
		}
		return tokenList;
		
	}
	public static Token DBObject2Token(DBObject obj){
		Token token = new Token();
		
		token.setTokenName((String)obj.get("token_name"));
		token.setKeyword((String)obj.get("keyword"));
		token.setStartLine((Integer)obj.get("start_line"));
		token.setEndLine((Integer)obj.get("end_line"));
		token.setHashNumber((Integer)obj.get("hash_number"));

		return token;
	}
	public static List<DiffType> DBObjectList2DiffTypeList(List<DBObject> dbList){
		List<DiffType> diffList = new ArrayList<DiffType>();
		if(dbList!=null){
			for(DBObject obj:dbList){
				DiffType diff = DBObject2DiffType(obj);
				diffList.add(diff);
			}
		}
		return diffList;
	}
	public static DiffType DBObject2DiffType(DBObject obj){
		DiffType diff = new DiffType();
		diff.setType((String)obj.get("type"));
		diff.setNewStartLine((Integer)obj.get("new_start_line"));
		diff.setNewEndLine((Integer)obj.get("new_end_line"));
		diff.setOldStartLine((Integer)obj.get("old_start_line"));
		diff.setOldEndLine((Integer)obj.get("old_end_line"));
		diff.setNewKeywordList((List<String>)obj.get("new_keywords"));
		diff.setOldKeywordList((List<String>)obj.get("old_keywords"));
		return diff;
	}
	public static void repair2(String updateFile) throws IOException{
		List<String> updateLines = FileUtils.readLines(new File(updateFile),"UTF-8");
		for(String str:updateLines){
			String[] temps = str.split(",");
			int comment_id = Integer.parseInt(temps[1]);
			int newScopeEndLine = Integer.parseInt(temps[2]);
			int oldScopeEndLine = Integer.parseInt(temps[3]);
			
			CommentEntry comment = DBObject2CommentEntry(comments.findOne(new BasicDBObject().append("comment_id", comment_id)));
			comment.setNew_scope_endLine(newScopeEndLine);
			comment.setOld_scope_endLine(oldScopeEndLine);
			
			ClassMessage clazz = classRepo.findASingleByProjectAndCommitIDAndClassName(comment.getProject(), comment.getCommitID(), comment.getClassName());
			List<DiffType> diffList = clazz.getDiffList();
			List<Line> newCode = clazz.getNewCode();
			List<Line> oldCode = clazz.getOldCode();
			List<Token> newToken = clazz.getNewTokenList();
			List<Token> oldToken = clazz.getOldTokenList();
			
			List<DiffType> commentDiffList = new ArrayList<DiffType>();
			for(DiffType diff:diffList){
				if((diff.getNewStartLine()>comment.getNew_scope_startLine()&&diff.getNewEndLine()<=comment.getNew_scope_endLine()
						&&diff.getOldStartLine()>comment.getOld_scope_startLine()&&diff.getOldEndLine()<=comment.getOld_scope_endLine())
						||(diff.getNewStartLine()>comment.getNew_scope_startLine()&&diff.getNewEndLine()<=comment.getNew_scope_endLine()
						&&diff.getOldStartLine()==0&&diff.getOldEndLine()==0)
						||(diff.getOldStartLine()>comment.getOld_scope_startLine()&&diff.getOldEndLine()<=comment.getOld_scope_endLine()
						&&diff.getNewStartLine()==0&&diff.getNewEndLine()==0)){
					commentDiffList.add(diff);
				}
			}
			
			List<String> commentNewCode = new ArrayList<String>();
			List<String> commentOldCode = new ArrayList<String>();
			for(int i=comment.getNew_comment_endLine();i<comment.getNew_scope_endLine();i++){
				commentNewCode.add(newCode.get(i).getLine());
			}
			for(int i=comment.getOld_comment_endLine();i<comment.getOld_scope_endLine();i++){
				commentOldCode.add(oldCode.get(i).getLine());
			}
			
			List<Token> commentNewToken = new ArrayList<Token>();
			List<Token> commentOldToken = new ArrayList<Token>();
			
			for(Token token:newToken){
				if(token.getStartLine()>comment.getNew_scope_startLine()&&token.getEndLine()<=comment.getNew_scope_endLine()){
					commentNewToken.add(token);
				}
			}
			
			for(Token token:oldToken){
				if(token.getStartLine()>comment.getOld_scope_startLine()&&token.getEndLine()<=comment.getOld_scope_endLine()){
					commentOldToken.add(token);
				}
			}
			
			comment.setDiffList(commentDiffList);
			comment.setNewCode(commentNewCode);
			comment.setOldCode(commentOldCode);
			comment.setNewToken(commentNewToken);
			comment.setOldToken(commentOldToken);
			
			comments.update(new BasicDBObject().append("comment_id",comment_id ), commentEntry2DBObject(comment));
		}
	}
	public static void main(String[] args) throws IOException{
//		List<String> fileLines = FileUtils.readLines(new File("D:/work/4.27/group.csv"), "UTF-8");
//
//		for(int i=0;i<fileLines.size();i++){
//			if(i%1000==0){
//				System.out.println(i+" is done.");
//			}
//			String str = fileLines.get(i);
//			List<CommentEntry> commentList = new ArrayList<CommentEntry>();
//			String[] temps = str.split(",");
//			String temp = temps[3];
//			String[] temps2 = temp.split(" ");
//			for(String str2:temps2){
//				int commentID = Integer.parseInt(str2);
//				commentList.add(DBObject2CommentEntry(comments.findOne(new BasicDBObject().append("comment_id", commentID))));
//			}
//			repair(commentList);
//		}
//		
//		FileUtils.writeLines(new File("D:/work/4.27/removeID.txt"), removeCommentIDList);
//		FileUtils.writeLines(new File("D:/work/4.27/updateComment.csv"), updateCommentList);
		
		repair2("D:/work/4.27/updateComment.csv");
		
	}

}
