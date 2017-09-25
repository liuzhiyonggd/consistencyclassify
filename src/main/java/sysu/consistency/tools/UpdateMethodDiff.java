package sysu.consistency.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

import sysu.consistency.db.bean.ClassMessage;
import sysu.consistency.db.bean.DiffType;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.ClassMessageRepository;

public class UpdateMethodDiff {

	public static void main(String[] args) throws IOException {
		
		ClassMessageRepository classRepo = RepositoryFactory.getClassRepository();
		DBCollection methods = new Mongo("172.18.217.106",27017).getDB("sourcebase").getCollection("method");
		for(int i=0;i<=1125584;i++){
			if(i%1000==0){
				System.out.println(i+" is done.");
			}
			ClassMessage clazz = classRepo.findASingleByClassID(i);
			List<DiffType> diffList = clazz.getDiffList();
			
			DBObject query = new BasicDBObject();
			query.put("project", clazz.getProject());
			query.put("commit_id", clazz.getCommitID());
			query.put("class_name", clazz.getClassName());
			DBCursor cursor = methods.find(query);
			
			for(DBObject obj:cursor){
				int methodOldStartLine = (int)obj.get("old_start_line");
				int methodOldEndLine = (int)obj.get("old_end_line");
				int methodNewStartLine = (int)obj.get("new_start_line");
				int methodNewEndLine = (int)obj.get("new_end_line");
				
				List<DBObject> methodDiffList = new ArrayList<DBObject>();
				for(DiffType diff:diffList){
					if((diff.getNewStartLine()>=methodNewStartLine&&diff.getNewEndLine()<=methodNewEndLine)
							||(diff.getOldStartLine()>=methodOldStartLine&&diff.getOldEndLine()<=methodOldEndLine)){
						methodDiffList.add(diff2DBObject(diff));
					}
				}
				obj.put("diffs", methodDiffList);
				DBObject query2 = new BasicDBObject();
				query2.put("method_id", obj.get("method_id"));
				methods.update(query2, obj);
			}
		}
	}
	
	private static DBObject diff2DBObject(DiffType diff){
		DBObject obj = new BasicDBObject();
		obj.put("type", diff.getType());
		obj.put("new_start_line", diff.getNewStartLine());
		obj.put("new_end_line", diff.getNewEndLine());
		obj.put("old_start_line", diff.getOldStartLine());
		obj.put("old_end_line", diff.getOldEndLine());
		obj.put("new_hashs", diff.getNewHashList());
		obj.put("old_hashs", diff.getOldHashList());
		obj.put("new_keywords", diff.getNewKeywordList());
		obj.put("old_keywords", diff.getOldKeywordList());
		return obj;
	}
}
