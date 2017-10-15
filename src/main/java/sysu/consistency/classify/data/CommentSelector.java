package sysu.consistency.classify.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

public class CommentSelector {
	
    public static void main(String[] args) throws IOException {
		MongoClient client = new MongoClient("192.168.2.168",27017);
		MongoCollection<Document> verifications = client.getDatabase("scopebase").getCollection("consistency_verify");
	    
		List<String> selectID = new ArrayList<String>();
		List<String> isChangeList = new ArrayList<String>();
		List<String> refactorList = new ArrayList<String>();
		
        MongoCursor<Document> cursor = verifications.find().iterator();
        while(cursor.hasNext()) {
        	Document doc = cursor.next();
        	if(doc.getString("change_reason").equals("scope_error")) {
        		continue;
        	}
        	selectID.add(doc.getInteger("comment_id")+"");
        	isChangeList.add(doc.getBoolean("ischange")?"1":"0");
        	
        	if(doc.getString("change_reason").equals("refactoring")) {
        		refactorList.add("1");
        	}else {
        		refactorList.add("0");
        	}
        }
        
        FileUtils.writeLines(new File("E:\\注释一致性实验\\数据\\selectID.txt"), selectID);
        FileUtils.writeLines(new File("E:\\注释一致性实验\\数据\\label.txt"), isChangeList);
        FileUtils.writeLines(new File("E:\\注释一致性实验\\数据\\refactor.txt"), refactorList);
    }

}
