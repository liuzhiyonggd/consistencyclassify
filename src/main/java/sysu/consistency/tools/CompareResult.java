package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class CompareResult {
	
	public static void main(String[] args) throws IOException {
		List<String> only1 = FileUtils.readLines(new File("D:/work/5.13/only1.txt"),"UTF-8");
		List<String> change1 = FileUtils.readLines(new File("D:/work/5.13/change1.txt"),"UTF-8");
		List<String> change2 = FileUtils.readLines(new File("D:/work/5.13/change2.txt"),"UTF-8");
		
		Set<String> newChange1 = new HashSet<String>();
		Set<String> newChange2 = new HashSet<String>();
		
		for(String str:change1){
			if(!only1.contains(str)){
				newChange1.add(str);
			}
		}
		
		for(String str:change2){
			if(!only1.contains(str)){
				newChange2.add(str);
			}
		}
		
		FileUtils.writeLines(new File("D:/work/5.13/newChange1.txt"), newChange1);
		FileUtils.writeLines(new File("D:/work/5.13/newChange2.txt"), newChange2);
	}

}
