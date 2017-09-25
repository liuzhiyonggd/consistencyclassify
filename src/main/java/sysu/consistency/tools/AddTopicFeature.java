package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

public class AddTopicFeature {
	
	public static void main(String[] args) throws IOException{
		List<String> trainFile = FileUtils.readLines(new File("D:/work/5.9/train.arff"),"UTF-8");
		List<String> testFile = FileUtils.readLines(new File("D:/work/5.9/test.arff"),"UTF-8");
		
		List<String> codeTopicFile = FileUtils.readLines(new File("D:/work/5.9/codetopic2.csv"),"UTF-8");
		List<String> commentTopicFile = FileUtils.readLines(new File("D:/work/5.9/commenttopic2.csv"),"UTF-8");
		
		Map<Integer,Integer> newTopicMap = new HashMap<Integer,Integer>();
		Map<Integer,Integer> oldTopicMap = new HashMap<Integer,Integer>();
		for(int i=0;i<codeTopicFile.size();i+=2){
			String[] temps = codeTopicFile.get(i).split(",");
			newTopicMap.put(Integer.parseInt(temps[0]),Integer.parseInt(temps[1]));
			temps = codeTopicFile.get(i+1).split(",");
			oldTopicMap.put(Integer.parseInt(temps[0]),Integer.parseInt(temps[1]));
		}
		
		Map<Integer,Integer> commentTopicMap = new HashMap<Integer,Integer>();
		for(int i=0;i<commentTopicFile.size();i++){
			String[] temps = commentTopicFile.get(i).split(",");
			commentTopicMap.put(Integer.parseInt(temps[0]), Integer.parseInt(temps[1]));
		}
		
		List<String> newTrainFile = getNewFile(trainFile,newTopicMap,oldTopicMap,commentTopicMap);
		List<String> newTestFile = getNewFile(testFile,newTopicMap,oldTopicMap,commentTopicMap);
		
		FileUtils.writeLines(new File("D:/work/5.9/train2.arff"), newTrainFile);
		FileUtils.writeLines(new File("D:/work/5.9/test2.arff"), newTestFile);
		
		
	}
	
	private static List<String> getNewFile(List<String> trainFile,Map<Integer,Integer> newTopicMap,Map<Integer,Integer> oldTopicMap,Map<Integer,Integer> commentTopicMap){
		List<String> newTrainFile = new ArrayList<String>();
		
		int index = 0;
		for(;index<trainFile.size();index++){
			
			if(trainFile.get(index).equals("@data")){
				newTrainFile.add(newTrainFile.size()-1, "@attribute 'newCodeTopic' numeric");
				newTrainFile.add(newTrainFile.size()-1, "@attribute 'oldCodeTopic' numeric");
				newTrainFile.add(newTrainFile.size()-1, "@attribute 'subCodeTopic' numeric");
				newTrainFile.add(newTrainFile.size()-1,"@attribute 'commentTopic' numeric");
				newTrainFile.add("@data");
				break;
			}
			newTrainFile.add(trainFile.get(index));
		}
		index++;
		for(;index<trainFile.size();index++){
			String str = trainFile.get(index);
			String[] temps = str.split(" ");
			int commentID = Integer.parseInt(temps[0]);
			int newTopic = -1;
			int oldTopic = -1;
			int commentTopic = -1;
			if(newTopicMap.containsKey(commentID)){				
				newTopic = newTopicMap.get(commentID);
			}
			if(oldTopicMap.containsKey(commentID)){
				oldTopic = oldTopicMap.get(commentID);
			}
			if(commentTopicMap.containsKey(commentID)){
				commentTopic = commentTopicMap.get(commentID);
			}
			StringBuilder newTrainBuilder = new StringBuilder(); 
			for(int i=0;i<temps.length-1;i++){
				newTrainBuilder.append(temps[i]+" ");
			}
			int subTopic = newTopic==oldTopic?0:1;
			newTrainBuilder.append(newTopic+" "+oldTopic+" "+subTopic+" "+commentTopic+" "+temps[temps.length-1]);
			
			newTrainFile.add(newTrainBuilder.toString());
		}
		return newTrainFile;
	}

}
