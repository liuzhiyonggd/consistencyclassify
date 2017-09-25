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

public class FalseTopic {

	
	public static void main(String[] args) throws IOException {
		
		List<String> topicFile = FileUtils.readLines(new File("D:/work/4.20/codeTopic.csv"), "UTF-8");
		List<String> false1File = FileUtils.readLines(new File("D:/work/4.20/false1CommentList.csv"),"UTF-8");
		List<String> output = new ArrayList<String>();
		
		List<HashSet<Integer>> setList = new ArrayList<HashSet<Integer>>();
		for(int i=0;i<50;i++){
			setList.add(new HashSet<Integer>());
		}
		for(String str:topicFile){
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			int topic = Integer.parseInt(temps[2]);
			setList.get(topic).add(commentID);
		}
		
		Map<Integer,Integer> map = new HashMap<Integer,Integer>();
		for(int i=0;i<50;i++){
			map.put(i, 0);
		}
		
		for(String str:false1File){
			int commentID = Integer.parseInt(str);
			for(int i=0;i<50;i++){
				Set<Integer> set = setList.get(i);
				if(set.contains(commentID)){
					map.put(i, map.get(i)+1);
					break;
				}
			}
		}
		for(Map.Entry<Integer, Integer> entry:map.entrySet()){
			output.add(entry.getKey()+","+entry.getValue());
		}
		FileUtils.writeLines(new File("D:/work/4.20/false1oftopic.csv"), output);
	}
}
