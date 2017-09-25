package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Test6 {
	
	public static void main(String[] args) throws IOException {
		List<String> output = new ArrayList<String>();
		List<String> commentIDList = new ArrayList<String>();
		List<String> missingCommentIDList = new ArrayList<String>();
		for(int i=1;i<=111180;i++){
			File newFile = new File("D:/work/4.9/documents/"+i+"_new.txt");
			File oldFile = new File("D:/work/4.9/documents/"+i+"_old.txt");
			if(newFile.exists()&&oldFile.exists()){
			List<String> fileStr = FileUtils.readLines(newFile,"UTF-8");
			String temp = "";
			for(String str:fileStr){
				temp +=str+" ";
			}
			while(temp.indexOf("  ")>=0){
				temp = temp.replaceAll("  ", " ");
			}
			output.add(temp);
			
			fileStr = FileUtils.readLines(oldFile,"UTF-8");
			temp = "";
			for(String str:fileStr){
				temp +=str+" ";
			}
			while(temp.indexOf("  ")>=0){
				temp = temp.replaceAll("  ", " ");
			}
			output.add(temp);
			
			commentIDList.add(i+"");
			if(i%10000==0){
				System.out.println(i+" is done.");
			}
			}else{
				System.out.println("missing comment id:"+i);
				missingCommentIDList.add(i+"");
			}
		}
		FileUtils.writeLines(new File("D:/work/4.15/doc.txt"), output);
		FileUtils.writeLines(new File("D:/work/4.15/commentid.txt"), commentIDList);
		FileUtils.writeLines(new File("D:/work/4.15/missing.txt"), missingCommentIDList);
	}

}
