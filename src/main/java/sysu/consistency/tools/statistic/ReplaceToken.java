package sysu.consistency.tools.statistic;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class ReplaceToken {
	
	public static void main(String[] args) throws IOException{
		String[] fontColors = new String[]{"&lt;font colo<span style='display:none;'>r:red&gt;",
				"&lt;font color:red&gt;"};
		String[] fontColorCloses = new String[]{"&lt;/font&gt;"};
		
		String[] nextLines = new String[]{"&lt;nextline&gt;"};
		
		String fileString = FileUtils.readFileToString(new File("D:/work/5.13/one.htm"),"UTF-8");
		
		for(String str:fontColors){
			fileString = fileString.replaceAll(str, "<font color=\"red\">");
		}
		for(String str:fontColorCloses){
			fileString = fileString.replaceAll(str, "</font>");
		}
		for(String str:nextLines){
			fileString = fileString.replaceAll(str, "<br>");
		}
		
		FileUtils.writeStringToFile(new File("D:/work/5.13/one.htm"), fileString,"UTF-8");
	}

}
