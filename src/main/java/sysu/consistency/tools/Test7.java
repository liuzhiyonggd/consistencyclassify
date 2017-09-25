package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class Test7 {
	public static void main(String[] args) throws IOException {
		List<String> trainFile = FileUtils.readLines(new File("D:/work/4.24/newTrain_1.arff"),"UTF-8");
		List<String> testFile = FileUtils.readLines(new File("D:/work/4.24/newTest_1.arff"),"UTF-8");
		
		List<String> diffSize0 = new ArrayList<String>();
		
		int index = 0;
		for(;index<trainFile.size();index++){
			if(trainFile.get(index).contains("@data")){
				break;
			}
		}
		index++;
		for(;index<trainFile.size();index++){
			String str = trainFile.get(index);
			String[] temps = str.split(",");
			int diffSize = Integer.parseInt(temps[1]);
			if(diffSize==0){
				diffSize0.add(temps[0]);
			}
		}
		
		index = 0;
		for(;index<testFile.size();index++){
			if(testFile.get(index).contains("@data")){
				break;
			}
		}
		index++;
		for(;index<testFile.size();index++){
			String str = testFile.get(index);
			String[] temps = str.split(",");
			int diffSize = Integer.parseInt(temps[1]);
			if(diffSize==0){
				diffSize0.add(temps[0]);
			}
		}
		FileUtils.writeLines(new File("D:/work/4.24/diffSize0.arff"), diffSize0);
	}

}
