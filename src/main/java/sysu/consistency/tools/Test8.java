package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class Test8 {
	public static void main(String[] args) throws IOException{
		List<String> updateIDList = FileUtils.readLines(new File("D:/work/4.27/updateComment.csv"),"UTF-8");
		Set<String> updateIDSet = new HashSet<String>();
		
		for(String str:updateIDList){
			String[] temps = str.split(",");
			updateIDSet.add(temps[1]);
		}
		
		FileUtils.writeLines(new File("D:/work/4.27/updateIDSet.txt"), updateIDSet);
	}

}
