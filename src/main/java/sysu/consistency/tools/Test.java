package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.bean.DiffType;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentRepository;

public class Test {
	
//	public static void main(String[] args) throws IOException{
//		String[] types = new String[]{"IfStatement","Elseif","ForStatement","EnhancedForStatement","DoStatement","WhileStatement",
//				"CatchClause","TryStatement","ThrowStatement","MethodInvocation","VariableDeclarationStatement","NumberLiteral","CharacterLiteral",
//				"StringLiteral","NullLiteral","BooleanLiteral","AssertStatement"};
//		for(String str:types)
//		System.out.println("@attribute '"+str+"_insert' numeric");
//		
//		for(String str:types)
//			System.out.println("@attribute '"+str+"_update' numeric");
//		
//		for(String str:types)
//			System.out.println("@attribute '"+str+"_delete' numeric");
//	}
	
//	private static void writeFile(List<String> inputList,String output) throws IOException{
//		Map<Integer,Integer> map = new HashMap<Integer,Integer>();
//		for(String str:inputList){
//			Integer commentID = Integer.parseInt(str);
//			if(map.containsKey(commentID)){
//				map.put(commentID, map.get(commentID)+1);
//			}else{
//				map.put(commentID, 1);
//			}
//		}
//		List<String> outputList = new ArrayList<String>();
//		for(Map.Entry<Integer, Integer> entry:map.entrySet()){
//			outputList.add(entry.getKey()+","+entry.getValue());
//		}
//		FileUtils.writeLines(new File(output), outputList);
//	}
	
	public void test(){
		System.out.println("test method invoke.");
	}
	
	public static void main(String[] args) throws IOException {
		CommentRepository commentRepo = RepositoryFactory.getCommentRepository();
		Set<Integer> idSet = new HashSet<Integer>();
		List<String> idFile = FileUtils.readLines(new File("file/id_new_random.txt"),"UTF-8");
		for(String str:idFile) {
			idSet.add(Integer.parseInt(str));
		}
		for(int commentID=1;commentID<=48170;commentID++) {
			CommentEntry comment = commentRepo.findASingleByCommentID(commentID);
			if(idSet.contains(commentID)) {
				comment.setFilter2(true);
			}else {
				comment.setFilter2(false);
			}
			commentRepo.save(comment);
			if(commentID%10000==0) {
				System.out.println(commentID+" is done.");
			}
			
		}
	}

}
