package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class Update2Class0 {
	
	public static void main(String[] args) throws IOException {
		List<String> idList = FileUtils.readLines(new File("D:/work/5.4/update2class0.txt"),"UTF-8");
		DBCollection comments = new Mongo("172.18.217.106").getDB("sourcebase").getCollection("comment3");
		
		for(String str:idList){
			int comment_id = Integer.parseInt(str);
			DBObject obj = comments.findOne(new BasicDBObject().append("comment_id", comment_id));
			obj.put("ischange", false);
			comments.update(new BasicDBObject().append("comment_id", comment_id), obj);
		}
	}

}
