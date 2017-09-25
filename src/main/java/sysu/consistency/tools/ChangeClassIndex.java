package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class ChangeClassIndex {
	
	public static void main(String[] args) throws IOException {
//		List<String> class0 = FileUtils.readLines(new File("D:/work/5.10/class0.txt"),"UTF-8");
		List<String> class1 = FileUtils.readLines(new File("D:/work/5.13/verifyid.txt"),"UTF-8");
//		List<String> error = FileUtils.readLines(new File("D:/work/5.10/error.txt"),"UTF-8");
		List<String> testList = FileUtils.readLines(new File("D:/work/5.13/allTest.arff"),"UTF-8");
		
		int index = 0;
		List<String> newTestList = new ArrayList<String>();
		for(String str:testList){
			index ++;
			newTestList.add(str);
			if(str.equals("@data")){
				break;
			}
		}
		
		for(;index<testList.size();index++){
			String[] temps = testList.get(index).split(" ");
//			if(error.contains(temps[0])){
//				continue;
//			}
			String classIndex = temps[temps.length-1];
//			if(class0.contains(temps[0])){
//				classIndex = "0";
//			}
			if(class1.contains(temps[0])){
				classIndex = "1";
			}
			String vector = "";
			for(int i=0;i<temps.length-1;i++){
				vector += temps[i]+" ";
			}
			vector += classIndex;
			newTestList.add(vector);
		}
		FileUtils.writeLines(new File("D:/work/5.13/allTest_2.arff"), newTestList);
		
	}
}
