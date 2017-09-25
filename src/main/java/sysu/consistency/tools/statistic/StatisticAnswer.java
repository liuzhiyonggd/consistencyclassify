package sysu.consistency.tools.statistic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class StatisticAnswer {
	
	public static void main(String[] args) throws IOException {
		
		List<String> answer1 = FileUtils.readLines(new File("D:/work/5.13/newChange1.txt"),"UTF-8" );
		List<String> answer2 = FileUtils.readLines(new File("D:/work/5.13/newChange2.txt"),"UTF-8");
		
		Set<String> union = new HashSet<String>();
		Set<String> interset = new HashSet<String>();
		
		for(String str:answer1){
			if(answer2.contains(str)){
				interset.add(str);
			}
			union.add(str);
		}
		
		union.addAll(answer2);
		
		Set<String> onePart = new HashSet<String>();
		for(String str:union){
			if(!interset.contains(str)){
			    onePart.add(str);
			}
		}
		System.out.println("union:"+union.size()+" interset:"+interset.size());
		FileUtils.writeLines(new File("D:/work/5.13/interset.txt"), interset);
		FileUtils.writeLines(new File("D:/work/5.13/union.txt"), union);
		FileUtils.writeLines(new File("D:/work/5.13/one.txt"), onePart);
	}

}
