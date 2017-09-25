package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class Test11 {
	
	public static void main(String[] args) throws IOException {
		List<String> typeList = FileUtils.readLines(new File("D:/work/5.3/containskeyword.csv"),"UTF-8");
		int[] containsKeywords = new int[60];
		int[] ischanges = new int[60];
		for(String str:typeList){
			String[] temps = str.split(",");
			
			for(int i=1;i<=60;i++){
				if(!temps[i].equals("0")){
					containsKeywords[i-1]++;
					if(temps[61].equals("true")){
						ischanges[i-1]++;
					}
				}
			}
		}
		
		for(int value:containsKeywords){
			System.out.print(value+" ");
		}
		System.out.println("");
		
		for(int value:ischanges){
			System.out.print(value+" ");
		}
		System.out.println("");
	}

}
