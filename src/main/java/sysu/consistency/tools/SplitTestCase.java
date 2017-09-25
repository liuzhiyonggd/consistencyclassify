package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class SplitTestCase {
	
	public static void main(String[] args) throws IOException {
		List<String> testList = FileUtils.readLines(new File("D:/work/5.10/test2.arff"),"UTF-8");
		
		
		
		List<String> headList = new ArrayList<String>();
		int index = 0;
		for(String str:testList){
			index++;
			headList.add(str);
			if(str.equals("@data")){
				break;
			}
		}
		List<List<String>> diffLineTestList = new ArrayList<List<String>>();
		for(int i=0;i<=12;i++){
			List<String> temp = new ArrayList<String>();
			temp.addAll(headList);
			diffLineTestList.add(temp);
		}
		for(;index<testList.size();index++){
			String[] temps = testList.get(index).split(" ");
			int lineNum = Integer.parseInt(temps[20]);
			diffLineTestList.get(lineNum).add(testList.get(index));
		}
		
		for(int i=0;i<diffLineTestList.size();i++){
			FileUtils.writeLines(new File("D:/work/5.15/test2/test"+i+".arff"), diffLineTestList.get(i));
		}
	}

}
