package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class CommitStatistic {
	
	public static void main(String[] args) throws IOException {
		DBCollection commits = new Mongo("172.18.217.106").getDB("sourcebase").getCollection("commit");
		DBCursor cursor = commits.find();
		
		Map<String,Integer> projectCountMap = new HashMap<String,Integer>();
		Map<String,Date> projectFirstDateMap = new HashMap<String,Date>();
		Map<String,Date> projectLastDateMap = new HashMap<String,Date>(); 
		Map<Integer,Integer> yearMap = new HashMap<Integer,Integer>();
		
		int i = 0;
		for(DBObject obj:cursor){
			i++;
			if(i%1000==0){
				System.out.println("commit "+i+" is done.");
			}
			String project = (String)obj.get("project");
			Date date = (Date)obj.get("date");
			int year = date.getYear();
			if(yearMap.containsKey(year)){
				yearMap.put(year, yearMap.get(year)+1);
			}else{
				yearMap.put(year, 1);
			}
			if(projectCountMap.containsKey(project)){
				projectCountMap.put(project, projectCountMap.get(project)+1);
				if(date.before(projectFirstDateMap.get(project))){
					projectFirstDateMap.put(project, date);
				}
				if(date.after(projectLastDateMap.get(project))){
					projectLastDateMap.put(project, date);
				}
			}else{
				projectCountMap.put(project, 1);
				projectFirstDateMap.put(project,date);
				projectLastDateMap.put(project,date);
			}
		}
		
		List<String> output = new ArrayList<String>();
		for(Map.Entry<String, Integer> entry:projectCountMap.entrySet()){
			long day = (projectLastDateMap.get(entry.getKey()).getTime()-projectFirstDateMap.get(entry.getKey()).getTime())/(1000*60*60*24);
//			output.add(entry.getKey()+","+entry.getValue()+","+projectFirstDateMap.get(entry.getKey())+","+projectLastDateMap.get(entry.getKey()).getYear()+","+day);
		}
		
		for(Map.Entry<Integer, Integer> entry:yearMap.entrySet()){
			output.add(entry.getKey()+","+entry.getValue());
		}
		FileUtils.writeLines(new File("D:/work/5.4/commit3.csv"), output);
	}

}
