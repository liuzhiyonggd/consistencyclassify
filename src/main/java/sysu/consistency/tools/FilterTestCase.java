package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class FilterTestCase {
	public static void main(String[] args) throws IOException {
		List<String> testList = FileUtils.readLines(new File("D:/work/5.10/test2.arff"),"UTF-8");
		List<String> valiteTestList = FileUtils.readLines(new File("D:/work/5.10/test_2.arff"),"UTF-8");
		
		Set<String> set = new HashSet<String>();
		int index = 0;
		for(String str:valiteTestList){
			index++;
			if(str.equals("@data")){
				break;
			}
		}
		for(;index<valiteTestList.size();index++){
			String[] temps = valiteTestList.get(index).split(" ");
			set.add(temps[0]);
		}
		
		List<String> newTestList = new ArrayList<String>();
		index = 0;
		for(String str:testList){
			newTestList.add(str);
			index++;
			if(str.equals("@data")){
				break;
			}
		}
		
		for(;index<testList.size();index++){
			String[] temps = testList.get(index).split(" ");
			if(set.contains(temps[0])){
				continue;
			}
			newTestList.add(testList.get(index));
		}
		
		FileUtils.writeLines(new File("D:/work/5.10/newTest.arff"), newTestList);
		
	}
}
