package sysu.consistency.datamining;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class StringGenerator {
	
	public static void generate(String type) throws IOException {
		List<String> wordList = FileUtils.readLines(new File("D:/temp/"+type+"/wordlist.csv"),"UTF-8");
		List<String> frequency = FileUtils.readLines(new File("D:/temp/"+type+"/data.txt.dat"),"UTF-8");
		List<String> fileLines = new ArrayList<String>();
		for(String str:frequency){
			StringBuffer sb = new StringBuffer();
			String[] temp1 = str.split(":");
			String[] temp2 = temp1[0].split(" ");
			
			for(int i=0;i<temp2.length-1;i++){
				String tmp = temp2[i];
				sb.append(wordList.get(Integer.parseInt(tmp))+";");
			}
			sb.append(wordList.get(Integer.parseInt(temp2[temp2.length-1])));
			fileLines.add(sb.toString());
		}
		FileUtils.writeLines(new File("D:/temp/"+type+"/frequencyitem.txt"), fileLines);
	}

}
