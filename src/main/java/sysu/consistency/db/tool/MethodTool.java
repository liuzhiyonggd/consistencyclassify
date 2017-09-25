package sysu.consistency.db.tool;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import sysu.consistency.db.bean.DiffType;
import sysu.consistency.db.bean.MethodBean;

public class MethodTool {
	
	public static MethodBean DBObject2MethodBean(DBObject obj){
		MethodBean method = new MethodBean();
		method.setProject((String)obj.get("project"));
		method.setCommitID((String)obj.get("commit_id"));
		method.setClassName((String)obj.get("class_name"));
		method.setMethodID((int)obj.get("method_id"));
		method.setNewMethodName((String)obj.get("new_method_name"));
		method.setOldMethodName((String)obj.get("old_method_name"));
		method.setNewParameters((List<String>)obj.get("new_parameters"));
		method.setOldParameters((List<String>)obj.get("old_parameters"));
		method.setNewReturnType((String)obj.get("new_return_type"));
		method.setOldReturnType((String)obj.get("old_return_type"));
		method.setNewStartLine((int)obj.get("new_start_line"));
		method.setNewEndLine((int)obj.get("new_end_line"));
		method.setOldStartLine((int)obj.get("old_start_line"));
		method.setOldEndLine((int)obj.get("old_end_line"));
		method.setDiffList(DBObjectList2DiffTypeList((List<DBObject>)obj.get("diffs")));
		
		return method;
	}
	
	public static DBObject methodBean2DBObject(MethodBean method){
		
		DBObject obj = new BasicDBObject();
		obj.put("project", method.getProject());
		obj.put("commit_id", method.getCommitID());
		obj.put("class_name", method.getClassName());
		obj.put("method_id", method.getMethodID());
		obj.put("new_method_name", method.getNewMethodName());
		obj.put("old_method_name", method.getOldMethodName());
		obj.put("new_parameters", method.getNewParameters());
		obj.put("old_parameters", method.getOldParameters());
		obj.put("new_return_type", method.getNewReturnType());
		obj.put("old_return_type", method.getOldReturnType());
		obj.put("new_start_line", method.getNewStartLine());
		obj.put("new_end_line", method.getNewEndLine());
		obj.put("old_start_line", method.getOldStartLine());
		obj.put("old_end_line", method.getOldEndLine());
		obj.put("diffs", diffTypeList2DBObjectList(method.getDiffList()));
		return obj;
		
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
		diff.setNewStartLine((int)obj.get("new_start_line"));
		diff.setNewEndLine((int)obj.get("new_end_line"));
		diff.setOldStartLine((int)obj.get("old_start_line"));
		diff.setOldEndLine((int)obj.get("old_end_line"));
		diff.setNewKeywordList((List<String>)obj.get("new_keywords"));
		diff.setOldKeywordList((List<String>)obj.get("old_keywords"));
		return diff;
	}

}
