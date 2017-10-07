package sysu.consistency.download.github;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class CommitDAO {
	
	private static DB db = new Mongo("192.168.2.168",27017).getDB("commitbase");
	private static DBCollection commits = db.getCollection("commit2");
	
	public static DBObject Commit2DBObject(CommitBean commit){
		DBObject obj = new BasicDBObject();
		
		obj.put("project", commit.getProject());
		obj.put("commit_id", commit.getCommitID());
		obj.put("commit_id2", commit.getCommitID2());
		obj.put("author", commit.getAuthor());
		obj.put("date", commit.getDate());
		obj.put("message", commit.getMessage());
		obj.put("filelist", commit.getFileList());
		obj.put("has_core_class", commit.isHasCoreClass());
		obj.put("has_core_method", commit.isHasCoreMethod());
		
		return obj;
	}
	
	public static void insert(CommitBean commit){
		DBObject obj = Commit2DBObject(commit);
		commits.insert(obj);
	}

}
