package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Test9 {
	
	public static void main(String[] args) throws IOException {
		List<String> updateList = FileUtils.readLines(new File("D:/work/4.27/newupdatedata.txt"), "UTF-8");
		List<String> removeIDList = FileUtils.readLines(new File("D:/work/4.27/removeIDSet.txt"),"UTF-8");
		List<String> trainList = FileUtils.readLines(new File("D:/work/4.27/train3.arff"),"UTF-8");
		List<String> testList = FileUtils.readLines(new File("D:/work/4.27/test3.arff"),"UTF-8");
		
		HashMap<Integer,String> updateMap = new HashMap<Integer,String>();
		for(String str:updateList){
			String[] temps = str.split(",");
			int id = Integer.parseInt(temps[0]);
			updateMap.put(id, str);
		}
		
		HashSet<Integer> removeSet = new HashSet<Integer>();
		for(String str:removeIDList){
			int id = Integer.parseInt(str);
			removeSet.add(id);
		}
		
		trainList = getNewDataList(trainList,updateMap,removeSet);
		testList = getNewDataList(testList,updateMap,removeSet);
		FileUtils.writeLines(new File("D:/work/4.27/train4.arff"), trainList);
		FileUtils.writeLines(new File("D:/work/4.27/test4.arff"), testList);
	}

	private static List<String> getNewDataList(List<String> trainList, HashMap<Integer, String> updateMap,
			HashSet<Integer> removeSet) {
		List<String> newDataList = new ArrayList<String>();
		
		int index = 0;
		for(;index<trainList.size();index++){
			String str = trainList.get(index);
			if(str.equals("@data")){
				newDataList.add(str);
				break;
			}
			newDataList.add(str);
		}
		index++;
		for(;index<trainList.size();index++){
			String str = trainList.get(index);
			String[] temps = str.split(",");
			int id = Integer.parseInt(temps[0]);
			if(removeSet.contains(id)){
				continue;
			}
			if(updateMap.containsKey(id)){
				newDataList.add(updateMap.get(id));
				continue;
			}
			newDataList.add(str);
		}
		return newDataList;
	}

}
