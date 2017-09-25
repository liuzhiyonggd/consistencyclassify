package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

public class Test10 {
	
	public static void main(String[] args) throws IOException {
		List<String> list = FileUtils.readLines(new File("D:/work/5.6/test6.arff"));
		
		Map<Integer,Integer> topicMap0 = new HashMap<Integer,Integer>();
		Map<Integer,Integer> topicMap1 = new HashMap<Integer,Integer>();
		
		int index = 0;
		for(String str:list){
			index ++;
			if(str.equals("@data"))break;
			
		}
		for(;index<list.size();index++){
			String[] temps = list.get(index).split(" ");
			int topic = Integer.parseInt(temps[temps.length-2]);
			int ischange = Integer.parseInt(temps[temps.length-1]);
			if(ischange==1){
			if(topicMap1.containsKey(topic)){
				topicMap1.put(topic, topicMap1.get(topic)+1);
			}else{
				topicMap1.put(topic, 1);
			}
			}else{
				if(topicMap0.containsKey(topic)){
					topicMap0.put(topic, topicMap0.get(topic)+1);
				}else{
					topicMap0.put(topic, 1);
				}
			}
			
		}
		
		List<String> output = new ArrayList<String>();
		for(Map.Entry<Integer, Integer> entry:topicMap1.entrySet()){
			output.add(entry.getKey()+","+entry.getValue()+","+topicMap0.get(entry.getKey()));
		}
		FileUtils.writeLines(new File("D:/work/topicstatistic.csv"), output);
	}

}
