package sysu.consistency.tools;

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

public class TopicFalseStatistic {
	
	public static void main(String[] args) throws IOException {
		Map<Integer,Integer> projectFalse1Map = new HashMap<Integer,Integer>();
		List<String> false1List = FileUtils.readLines(new File("D:/work/5.6/false1CommentList.txt"));
		List<String> commentTopic = FileUtils.readLines(new File("D:/work/5.6/commentTopic2.csv"));
		Map<Integer,Integer> topicMap = new HashMap<Integer,Integer>();
		for(String str:commentTopic){
			String[] temps = str.split(",");
			topicMap.put(Integer.parseInt(temps[0]), Integer.parseInt(temps[1]));
		}
		for(String str:false1List){
			int commentID = Integer.parseInt(str);
			int topic = -1;
			if(topicMap.containsKey(commentID)){
			    topic = topicMap.get(commentID);
			}
			if(projectFalse1Map.containsKey(topic)){
				projectFalse1Map.put(topic, projectFalse1Map.get(topic)+1);
			}else{
				projectFalse1Map.put(topic, 1);
			}
		}
		
		Map<Integer,Integer> projectFalse0Map = new HashMap<Integer,Integer>();
		List<String> false0List = FileUtils.readLines(new File("D:/work/5.6/false0CommentList.txt"));
		for(String str:false0List){
			int commentID = Integer.parseInt(str);
			int topic = -1;
			if(topicMap.containsKey(commentID)){
			    topic = topicMap.get(commentID);
			}
			if(projectFalse0Map.containsKey(topic)){
				projectFalse0Map.put(topic, projectFalse0Map.get(topic)+1);
			}else{
				projectFalse0Map.put(topic, 1);
			}
		}
		List<String> output = new ArrayList<String>();
		for(Map.Entry<Integer, Integer> entry:projectFalse1Map.entrySet()){
			output.add(entry.getKey()+","+entry.getValue()+","+projectFalse0Map.get(entry.getKey()));
		}
		FileUtils.writeLines(new File("D:/work/5.6/topicfalse.csv"), output);
	}

}
