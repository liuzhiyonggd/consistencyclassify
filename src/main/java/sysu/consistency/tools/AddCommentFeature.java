package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.ClassMessage;
import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.bean.MethodBean;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.ClassMessageRepository;
import sysu.consistency.db.repository.CommentRepository;
import sysu.consistency.db.repository.MethodRepository;

public class AddCommentFeature {
	private static ClassMessageRepository classRepo = RepositoryFactory.getClassRepository();
	private static MethodRepository methodRepo = RepositoryFactory.getMethodRepository();
	private static CommentRepository commentRepo = RepositoryFactory.getCommentRepository();
	
	private static Map<Integer,Integer> classLineMap = new HashMap<Integer,Integer>();
	private static Map<Integer,Integer> methodLineMap = new HashMap<Integer,Integer>();
	private static Map<Integer,Integer> commentLineMap = new HashMap<Integer,Integer>();
	
	private static Map<Integer,Double> classWeightMap = new HashMap<Integer,Double>();
	private static  Map<Integer,Double> methodWeightMap = new HashMap<Integer,Double>();
	private static Map<Integer,Double> commentWeightMap = new HashMap<Integer,Double>();
	
	public static void main(String[] args) throws IOException {
		
		
		List<String> commentLineList = FileUtils.readLines(new File("D:/work/5.7/commentline.csv"),"UTF-8");
		for(String str:commentLineList){
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			int commentLine = Integer.parseInt(temps[1]);
			int codeLine = Integer.parseInt(temps[2]);
			if(codeLine == 0){
				codeLine = 1;
			}
			commentLineMap.put(commentID, commentLine);
			commentWeightMap.put(commentID, commentLine*1.0d/codeLine);
		}
		
		Map<Integer,Integer> classMap = new HashMap<Integer,Integer>();
		Map<Integer,Integer> methodMap = new HashMap<Integer,Integer>();
		List<String> classMethodList = FileUtils.readLines(new File("D:/work/5.7/commentclassline.csv"),"UTF-8");
		for(String str:classMethodList){
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			
			int methodLine = Integer.parseInt(temps[2]);
			int classLine = Integer.parseInt(temps[3]);
			
			classMap.put(commentID, classLine);
			methodMap.put(commentID, methodLine);
		}
		
		List<String> classMethodLineList = FileUtils.readLines(new File("D:/work/5.7/classmethodcommentline.csv"),"UTF-8");
		List<String> output1 = new ArrayList<String>();
		for(String str:classMethodLineList){
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			int classCommentLine = Integer.parseInt(temps[2]);
			int methodCommentLine = Integer.parseInt(temps[3]);
			
			int classLine = 1;
			if(classMap.containsKey(commentID)){
			    classLine = classMap.get(commentID);
			}
			
			int methodLine = 1;
			if(methodMap.containsKey(commentID)){
				methodLine = methodMap.get(commentID);
			}
			
			classLineMap.put(commentID, classCommentLine);
			methodLineMap.put(commentID, methodCommentLine);
			
			
			
			methodWeightMap.put(commentID, methodCommentLine*1.0d/methodLine);
			classWeightMap.put(commentID, classCommentLine*1.0d/classLine);
			
			
			
		}
		
		List<String> trainList = FileUtils.readLines(new File("D:/work/5.5/train2.arff"),"UTF-8");
		List<String> testList = FileUtils.readLines(new File("D:/work/5.5/test2.arff"),"UTF-8");
		
		List<String> newTrainList = addFeatures(trainList);
		List<String> newTestList = addFeatures(testList);
		
		FileUtils.writeLines(new File("D:/work/5.8/train.arff"), newTrainList);
		FileUtils.writeLines(new File("D:/work/5.8/test.arff"), newTestList);
	}
	
	private static List<String> addFeatures(List<String> dataList){
		List<String> newDataList = new ArrayList<String>();
		
		int index = 0;
		for(String str:dataList){
			index++;
			newDataList.add(str);
			if(str.equals("@data")){
				newDataList.add(newDataList.size()-2,"@attribute 'commentLine' numeric");
				newDataList.add(newDataList.size()-2,"@attribute 'mcommentLine' numeric");
				newDataList.add(newDataList.size()-2,"@attribute 'ccommentLine' numeric");
				newDataList.add(newDataList.size()-2,"@attribute 'commentWeight' numeric");
				newDataList.add(newDataList.size()-2,"@attribute 'mcommentWeight' numeric");
				newDataList.add(newDataList.size()-2,"@attribute 'ccommentWeight' numeric");
				break;
			}
		}
		
		for(;index<dataList.size();index++){
			String[] temps = dataList.get(index).split(" ");
			int commentID = Integer.parseInt(temps[0]);
			StringBuilder sb = new StringBuilder();
			for(int i=0;i<temps.length-1;i++){
				sb.append(temps[i]+" ");
			}
			sb.append(discridizeNum(commentLineMap.get(commentID))+" "+discridizeNum(methodLineMap.get(commentID))+" "+discridizeNum(classLineMap.get(commentID))+" "
			             +discridizeWeight(commentWeightMap.get(commentID))+" "+discridizeWeight(methodWeightMap.get(commentID))+" "+discridizeWeight(classWeightMap.get(commentID))
			             +" "+temps[temps.length-1]);
			
			newDataList.add(sb.toString());
		}
		
		
		return newDataList;
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
		if(num>5&&num<=10){
			return 6;
		}
		if(num>10&&num<=20){
			return 7;
		}
		if(num>20){
			return 8;
		}
		
		return 0;
		
	}
	
	private static int discridizeWeight(double weight){
		int num = (int)(weight*100);
		if(num==0){
			return 0;
		}
		if(num>0&&num<=10){
			return 1;
		}
		if(num>10&&num<=20){
			return 2;
		}
		if(num>20&&num<=30){
			return 3;
		}
		if(num>30&&num<=40){
			return 4;
		}
		if(num>40&&num<=50){
			return 5;
		}
		if(num>50&&num<=60){
			return 6;
		}
		if(num>60&&num<=70){
			return 7;
		}
		if(num>70&&num<=80){
			return 8;
		}
		if(num>80&&num<=90){
			return 9;
		}
		if(num>90&&num<100){
			return 10;
		}
		if(num==100){
			return 11;
		}
		return 0;
	}
	

}
