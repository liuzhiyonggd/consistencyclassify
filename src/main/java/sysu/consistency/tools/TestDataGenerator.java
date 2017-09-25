package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class TestDataGenerator {
	
	public static void main(String[] args) throws IOException {
		List<String> testList = FileUtils.readLines(new File("D:/work/5.9/test2.txt"),"UTF-8");
		
		List<List<String>> projectList = new ArrayList<List<String>>();
		for(int i=0;i<8;i++){
			projectList.add(new ArrayList<String>());
		}
		for(String str:testList){
			String[] temps = str.split(" ");
			int project = Integer.parseInt(temps[1]);
			projectList.get(project-1).add(str);
		}
		List<String> output = new ArrayList<String>();
		for(int i=0;i<8;i++){
			Collections.shuffle(projectList.get(i));
			for(int j=0;j<300&&j<projectList.get(i).size();j++){
				String[] temps = projectList.get(i).get(j).split(" ");
				output.add(temps[0]+","+temps[1]);
			}
		}
		FileUtils.writeLines(new File("D:/work/5.9/testid.csv"), output);
	}

}
