package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class TestInstance {
	
	public static void main(String[] args) throws IOException {
		List<String> idList = FileUtils.readLines(new File("D:/work/5.10/id1.txt"),"UTF-8");
		Set<String> idSet = new HashSet<String>();
		for(String str:idList){
			idSet.add(str);
		}
		
		List<String> changeList = FileUtils.readLines(new File("D:/work/5.10/changeid.txt"),"UTF-8");
		Set<String> changeSet = new HashSet<String>();
		for(String str:changeList){
			changeSet.add(str);
		}
		
		List<String> testList = FileUtils.readLines(new File("D:/work/5.10/test2.arff"),"UTF-8");
		List<String> newTestList = new ArrayList<String>();
		int index = 0;
		for(String str:testList){
			index++;
			newTestList.add(str);
			if(str.equals("@data")){
				break;
			}
		}
		
		for(;index<testList.size();index++){
			String[] temps = testList.get(index).split(" ");
			if(idSet.contains(temps[0])){
				String vector = "";
				for(int i=0;i<temps.length-1;i++){
					vector+= temps[i]+" ";
				}
				String change = temps[temps.length-1];
				if(changeSet.contains(temps[0])){
					change = change.equals("1")?"0":"1";
				}
				vector += change;
				newTestList.add(vector);
			}
		}
		
		FileUtils.writeLines(new File("D:/work/5.10/test_1.arff"), newTestList);
	}

}
