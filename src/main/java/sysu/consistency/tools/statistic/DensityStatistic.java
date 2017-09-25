package sysu.consistency.tools.statistic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentRepository;

public class DensityStatistic {
	
	public static void main(String[] args) throws IOException {
//		FileUtils.writeLines(new File("D:/work/5.13/commentDensity.csv"),computeDensity("D:/work/5.7/commentLine.csv") );
		FileUtils.writeLines(new File("D:/work/5.13/methodDensity.csv"),computeDensity("D:/work/5.7/classmethodcommentLine.csv") );
		FileUtils.writeLines(new File("D:/work/5.13/classDensity.csv"),computeDensity("D:/work/5.7/commentclassLine.csv") );
	}
	
	private static List<String> computeDensity(String filePath) throws IOException{
		List<String> idList = FileUtils.readLines(new File("D:/work/5.9/newid.txt"),"UTF-8");
		List<String> densityList = FileUtils.readLines(new File(filePath),"UTF-8");
		
		Map<Integer,Integer> cCommentLineMap = new HashMap<Integer,Integer>();
		Map<Integer,Integer> cLineMap = new HashMap<Integer,Integer>();
		int count = 0;
		for(String str:densityList){
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			int commentLine = Integer.parseInt(temps[1]);
			int line = Integer.parseInt(temps[2]);
			cLineMap.put(commentID, line);
			cCommentLineMap.put(commentID, commentLine);
		}
		
		
		Map<String,Integer> pCommentLineMap = new HashMap<String,Integer>();
		Map<String,Integer> pLineMap = new HashMap<String,Integer>();
		CommentRepository commentRepo = RepositoryFactory.getCommentRepository();
		for(int commentID=1;commentID<=111180;commentID++){
			count++;
			if(count%10000==0){
			    System.out.println(count+" is done.");
			}
			CommentEntry comment = commentRepo.findASingleByCommentID(commentID);
			String project = comment.getProject();
			
			int commentLine = !cCommentLineMap.containsKey(commentID)?0:cCommentLineMap.get(commentID);
			int line = !cLineMap.containsKey(commentID)?0:cLineMap.get(commentID);
			
			if(pCommentLineMap.containsKey(project)){
				pCommentLineMap.put(project, pCommentLineMap.get(project)+commentLine);
			}else{
				pCommentLineMap.put(project, commentLine);
			}
			
			if(pLineMap.containsKey(project)){
				pLineMap.put(project, pLineMap.get(project)+line);
			}else{
				pLineMap.put(project, line);
			}
		}
		
		
		List<String> result = new ArrayList<String>();
		for(Map.Entry<String, Integer> entry: pLineMap.entrySet()){
			result.add(entry.getKey()+","+entry.getValue()+","+pCommentLineMap.get(entry.getKey())+","+pCommentLineMap.get(entry.getKey())*1.0d/entry.getValue());
		}
		
		
		return result;
	}

}
