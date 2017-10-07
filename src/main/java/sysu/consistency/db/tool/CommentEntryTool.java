package sysu.consistency.db.tool;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.bean.DiffType;
import sysu.consistency.db.bean.Token;

public class CommentEntryTool {
	
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

}
