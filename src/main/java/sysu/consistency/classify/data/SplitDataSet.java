package sysu.consistency.classify.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class SplitDataSet {
	
	public static void main(String[] args) throws IOException {
		List<String> dataLines = FileUtils.readLines(new File("file/data_1_2_5_6_3.csv"),"UTF-8");
		List<String> heads = FileUtils.readLines(new File("file/head.txt"),"UTF-8");
		List<String> verifyDatas = new ArrayList<String>();
		verifyDatas.addAll(heads);
		List<String> trainDatas = new ArrayList<String>();
		trainDatas.addAll(heads);
		Set<String> verifyIDSet = new HashSet<String>();
        Map<String,List<String>> projects = new HashMap<String,List<String>>();
		for(String str:dataLines) {
			String[] temps = str.split(",");
			String projectID = temps[1];
			String commentID = temps[0];
			
			if(!projects.containsKey(projectID)){
				projects.put(projectID, new ArrayList<String>());
			}
			projects.get(projectID).add(commentID);
		}
		
		for(Map.Entry<String, List<String>> entry:projects.entrySet()) {
			List<String> commentIDList = entry.getValue();
			Random random = new Random();
			int count = 0;
			while(count<400) {
				int index = random.nextInt(commentIDList.size()-1);
				String commentID = commentIDList.get(index);
				if(!verifyIDSet.contains(commentID)) {
					verifyIDSet.add(commentID);
					count++;
				}
			}
		}
		
		for(String str:dataLines) {
			String[] temps = str.split(",");
			String commentID = temps[0];
			
			if(verifyIDSet.contains(commentID)) {
				verifyDatas.add(str);
			}else {
				trainDatas.add(str);
			}
		}
		
		FileUtils.writeLines(new File("file/verify.arff"), verifyDatas);
		FileUtils.writeLines(new File("file/train.arff"), trainDatas);
	}

}
