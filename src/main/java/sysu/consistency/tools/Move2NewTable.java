package sysu.consistency.tools;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

public class Move2NewTable {
	
	public static void main(String[] args) {
		MongoClient client = new MongoClient("192.168.2.168",27017);
		MongoCollection<Document> comments = client.getDatabase("sourcebase").getCollection("comment6");
	    MongoCollection<Document> comments2 = client.getDatabase("scopebase").getCollection("comment6");
	    
	    MongoCursor<Document> cursor = comments.find().iterator();
	    while(cursor.hasNext()) {
	    	Document doc = cursor.next();
	    	comments2.insertOne(doc);
	    }
	
	}

}
