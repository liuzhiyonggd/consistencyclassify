package sysu.consistency.classify.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class BadInstanceFilter {
	
	public static void filteSameVectorInClass0And1(String input,String output) throws IOException{
		List<String> inputList = FileUtils.readLines(new File(input),"UTF-8");
		List<String> outputList = new ArrayList<String>();
		
		Set<String> class1List = new HashSet<String>();
		Set<String> class0List = new HashSet<String>();
		
		int remove1 = 0;
		int remove0 = 0;
		
		for(String str:inputList){
			String[] temps = str.split(",");
			StringBuilder sb = new StringBuilder();
			for(int i=1;i<temps.length-1;i++){
				sb.append(temps[i]+" ");
			}
			String vector = sb.toString();
			if(temps[temps.length-1].equals("1")){
				class1List.add(vector);
			}else{
				class0List.add(vector);
			}
		}
		System.out.println("class1List:"+class1List.size());
		System.out.println("class0List:"+class0List.size());
		for(String str:inputList){
			String[] temps = str.split(",");
			StringBuilder sb = new StringBuilder();
			for(int i=1;i<temps.length-1;i++){
				sb.append(temps[i]+" ");
			}
			String vector = sb.toString();
			if(temps[temps.length-1].equals("1")){
				if(!class0List.contains(vector)){
					outputList.add(str);
				}else{
					remove1++;
				}
			}else{
				if(!class1List.contains(vector)){
					outputList.add(str);
				}else{
					remove0++;
				}
			}
		}
		
		System.out.println("remove 1:"+remove1);
		System.out.println("remove 0:"+remove0);
		
		FileUtils.writeLines(new File(output), outputList);
	}
	
	public static void main(String[] args) {
		try {
			BadInstanceFilter.filteSameVectorInClass0And1("D:/work/4.27/data.txt", "D:/work/4.27/newdata.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
