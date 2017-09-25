package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class AdjustTrainInstance {
	public static void main(String[] args) throws IOException {
		List<String> trainFile = FileUtils.readLines(new File("D:/work/5.8/train.arff"),"UTF-8");
		
		List<String> newTrainFile = new ArrayList<String>();
		List<String> newTestFile = new ArrayList<String>();
		
		Set<Integer> filterSet = new HashSet<Integer>();
		
		int index = 0;
		for(int i=0;i<trainFile.size();i++){
			String str = trainFile.get(i);
			if(!str.equals("@data")){
				newTrainFile.add(str);
			}else{
				newTrainFile.add(str);
				index = i+1;
				break;
			}
		}
		
		int num = 0;
		for(int i=index;i<trainFile.size();i++){
			String str = trainFile.get(i);
			String[] temps = str.split(" ");
			int classify = Integer.parseInt(temps[temps.length-1]);
			if(classify==0){
				num++;
				if(num%3==0){
					
					continue;
				}
			}
			newTrainFile.add(str);
		}
		
		FileUtils.writeLines(new File("D:/work/5.8/newtrain3.arff"), newTrainFile);
	}

}
