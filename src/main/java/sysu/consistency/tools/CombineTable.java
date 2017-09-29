package sysu.consistency.tools;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class CombineTable {

	public static void main(String[] args) {
		MongoDatabase database = new MongoClient("192.168.2.168", 27017).getDatabase("sourcebase");
		MongoCollection<Document> comments3 = database.getCollection("comment3");
		FindIterable<Document> iter = comments3.find();
		MongoCursor<Document> cursor = iter.iterator();
		while (cursor.hasNext()) {
			Document doc = cursor.next();
			int commitID = doc.getInteger("commit_id");
			doc.put("commit_id", commitID+"");
			Document query = new Document();
			query.put("comment_id", doc.getInteger("comment_id"));
			comments3.replaceOne(query, doc);
		}
	}
}
