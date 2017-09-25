package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class ChangeVector {

	public static void main(String[] args) throws IOException {
		List<String> fileLines = FileUtils.readLines(new File("D:/temp/test_3.arff"),"UTF-8");
		HashSet<Integer> numDiscridizeSet = new HashSet<Integer>();
		HashSet<Integer> weightDiscridizeSet = new HashSet<Integer>();
		
		int[] nums = new int[]{2,3,5,7,9,11,13,15,17,19,21,23,25,27,28,30,33,47,48};
		int[] weights = new int[]{4,6,8,10,12,14,16,18,20,22,24,26,29,44,45,46};
		
		for(Integer i : nums){
			numDiscridizeSet.add(i);
		}
		for(Integer i: weights){
			weightDiscridizeSet.add(i);
		}
		
		List<String> output = new ArrayList<String>();
		for(String str : fileLines){
			String[] temps = str.split(" ");
			int[] vectors = new int[temps.length];
			
			for(int i = 0;i<temps.length;i++){
				int num = Integer.parseInt(temps[i]);
				if(numDiscridizeSet.contains(i)){
					vectors[i] = discridizeNum(num);
				}
				else if(weightDiscridizeSet.contains(i)){
					vectors[i] = discridizeWeight(num);
				}
				else{
					vectors[i] = num;
				}
				
			}
			
			String buff = "";
			for(Integer v:vectors){
				buff = buff+v+" ";
			}
			output.add(buff);
		}
		FileUtils.writeLines(new File("D:/test_d.arff"), output);
	}
	
	private static int discridizeNum(int num){
		if(num==0){
			return 0;
		}
		if(num==1){
			return 1;
		}
		if(num==2){
			return 2;
		}
		if(num==3){
			return 3;
		}
		if(num==4){
			return 4;
		}
		if(num==5){
			return 5;
		}
		if(num>5&&num<=10){
			return 6;
		}
		if(num>10&&num<=20){
			return 7;
		}
		if(num>20){
			return 8;
		}
		
		return 0;
		
	}
	
	private static int discridizeWeight(int num){
		if(num==0){
			return 0;
		}
		if(num>0&&num<=10){
			return 1;
		}
		if(num>10&&num<=20){
			return 2;
		}
		if(num>20&&num<=30){
			return 3;
		}
		if(num>30&&num<=40){
			return 4;
		}
		if(num>40&&num<=50){
			return 5;
		}
		if(num>50&&num<=60){
			return 6;
		}
		if(num>60&&num<=70){
			return 7;
		}
		if(num>70&&num<=80){
			return 8;
		}
		if(num>80&&num<=90){
			return 9;
		}
		if(num>90&&num<100){
			return 10;
		}
		if(num==100){
			return 11;
		}
		return 0;
	}
}
