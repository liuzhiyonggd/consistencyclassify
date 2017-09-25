package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentRepository;

public class InstanceFilter {
	
	private static CommentRepository commentRepo = RepositoryFactory.getCommentRepository();
	
	public static void main(String[] args)throws IOException {
		
		List<String> filterFile = FileUtils.readLines(new File("D:/work/5.2/wordlt3.txt"),"UTF-8");
		List<String> trainFile = FileUtils.readLines(new File("D:/work/4.27/train4.arff"),"UTF-8");
		List<String> testFile = FileUtils.readLines(new File("D:/work/4.27/test4.arff"),"UTF-8");
		
		Set<Integer> filterSet = new HashSet<Integer>();
		for(String str:filterFile){
		    filterSet.add(Integer.parseInt(str));
		}
		List<String> newTrainFile = getNewFile(trainFile,filterSet);
		List<String> newTestFile = getNewFile(testFile,filterSet);
		
		FileUtils.writeLines(new File("D:/work/5.2/train5.arff"), newTrainFile);
		FileUtils.writeLines(new File("D:/work/5.2/test5.arff"), newTestFile);
	}
	
	private static List<String> getNewFile(List<String> trainFile,Set<Integer> filterSet) throws IOException{
		List<String> newTrainFile = new ArrayList<String>();
		int index = 0;
		for(;index<trainFile.size();index++){
			
			if(trainFile.get(index).equals("@data")){
				newTrainFile.add("@data");
				break;
			}
			newTrainFile.add(trainFile.get(index));
		}
		index++;
		for(;index<trainFile.size();index++){
			String str = trainFile.get(index);
			String[] temps = str.split(",");
			
			int commentID = Integer.parseInt(temps[0]);
			
//			CommentEntry comment = commentRepo.findASingleByCommentID(commentID);
			
			if(!filterSet.contains(commentID)){
				newTrainFile.add(str);
			}
			
		}
		return newTrainFile;
	}

}
