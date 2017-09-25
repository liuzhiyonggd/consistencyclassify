package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class ReplaceTable {
	
	public static void main(String[] args) throws IOException{
		DBCollection comments = new Mongo("172.18.217.106",27017).getDB("sourcebase").getCollection("comment3");
		DBCollection comments2 = new Mongo("localhost",27017).getDB("sourcebase").getCollection("comment3");
		
		List<String> updateIDList = FileUtils.readLines(new File("D:/work/4.27/updateIDSet.txt"),"UTF-8");
		for(String str:updateIDList){
			int i=Integer.parseInt(str);
			if(i%100==0){
				System.out.println(i+" is done.");
			}
			DBObject query = new BasicDBObject();
			query.put("comment_id", i);
			DBObject obj = comments2.findOne(query);
			try{
			    comments.update(query,obj);
			}catch(Exception e){
				System.out.println("comment "+i+" error.");
				System.out.println(e.getMessage());
			}
		}
	}

}
