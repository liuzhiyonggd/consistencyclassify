package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class ProjectSelector {
	
	public static void main(String[] args) throws IOException {
		List<String> train = FileUtils.readLines(new File("D:/work/5.10/train2.arff"),"UTF-8");
		List<String> test = FileUtils.readLines(new File("D:/work/5.10/test_2.arff"),"UTF-8");
		List<String> newTrain = select(train);
		List<String> newTest = select(test);
		
//		FileUtils.writeLines(new File("D:/work/5.10/dataset/train8.arff"), newTrain);
		FileUtils.writeLines(new File("D:/work/5.10/dataset/test8.arff"), newTest);
	}
	
	private static List<String> select(List<String> dataList){
		List<String> newDataList = new ArrayList<String>();
		int index = 0;
		for(String str:dataList){
			index++;
			newDataList.add(str);
			if(str.equals("@data")){
				break;
			}
		}
		for(;index<dataList.size();index++){
			String[] temps = dataList.get(index).split(" ");
			int project = Integer.parseInt(temps[1]);
			//1:jhotdraw 2:jedit 3:ejbca 4:htmlunit 5:opennms 6:jamwiki 7:kablink 8:freecol
			//project==2/*||project==3||project==5||project==6||project==7||project==8*/
			if((project==8)){
				newDataList.add(dataList.get(index));
			}
		}
		
		return newDataList;
	}

}
