package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentRepository;

public class UpdateDataSet {
	private static CommentRepository commentRepo = RepositoryFactory.getCommentRepository();
	public static void main(String[] args) throws IOException{
		List<String> trainList = FileUtils.readLines(new File("D:/work/5.5/train1.arff"),"UTF-8");
		List<String> testList = FileUtils.readLines(new File("D:/work/5.5/test1.arff"),"UTF-8");
		
		List<String> newTrainList = updateDataSet(trainList);
		List<String> newTestList = updateDataSet(testList);
		
		FileUtils.writeLines(new File("D:/work/5.5/train4.arff"),newTrainList);
		FileUtils.writeLines(new File("D:/work/5.5/test4.arff"), newTestList);
	}
	
	public static List<String> updateDataSet(List<String> set){
		int index = 0;
		List<String> newSet = new ArrayList<String>();
		for(String str:set){
			index++;
			newSet.add(str);
			if(str.equals("@data")){
				break;
			}
		}
		
		
		for(;index<set.size();index++){
			String str = set.get(index);
			String[] temps = str.split(" ");
			int comment_id = Integer.parseInt(temps[0]);
			String vector = "";
			for(int i=0;i<temps.length-1;i++){
				vector+=temps[i]+" ";
			}
//			for(int i=28;i<88;i++){
//				int v = Integer.parseInt(temps[i]);
//				v = v>0?1:0;
//				vector+=v+" ";
//			}
//			for(int i=88;i<temps.length-1;i++){
//				vector+=temps[i]+" ";
//			}
			int classIndex = commentRepo.findASingleByCommentID(comment_id).isChange()?1:0;
			vector+=classIndex+"";
			newSet.add(vector);
		}
		return newSet;
	}
	
	private static int discridizeNum(int num){
		if(num==0){
			return 0;
		}
		if(num==1){
			return 1;
		}
		if(num==2){
			return 2;
		}
		if(num==3){
			return 3;
		}
		if(num==4){
			return 4;
		}
		if(num==5){
			return 5;
		}
		if(num>5){
			return 6;
		}
		
		return 0;
		
	}

}
