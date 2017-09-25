package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class TopicAcceptor {
	
	public static void main(String[] args) throws IOException {
		List<String> codeTheraList = FileUtils.readLines(new File("D:/work/5.5/code_topic2/model-final.theta"),"UTF-8");
		List<String> commentTheraList = FileUtils.readLines(new File("D:/work/5.5/comment_topic2/model-final.theta"),"UTF-8");
		List<String> codeIdList = FileUtils.readLines(new File("D:/work/5.5/codeid.txt"),"UTF-8");
		List<String> commentIdList = FileUtils.readLines(new File("D:/work/5.5/commentid.txt"),"UTF-8");
		
		List<String> codeTopicList = codeTopic(codeTheraList,codeIdList);
		List<String> commentTopicList = commentTopic(commentTheraList,commentIdList);
		
		FileUtils.writeLines(new File("D:/work/5.6/codeTopic2.csv"), codeTopicList);
		FileUtils.writeLines(new File("D:/work/5.6/commentTopic2.csv"), commentTopicList);
	}
	
	public static List<String> codeTopic(List<String> theraList,List<String> idList){
		List<String> output = new ArrayList<String>();
		for(int i=0;i<theraList.size();i++){
			String[] temps = theraList.get(i).split(" ");
			int maxIndex = 0;
			double max = 0;
			for(int j=0;j<temps.length;j++){
				double prob = Double.parseDouble(temps[j]);
				if(prob>max){
					max = prob;
					maxIndex = j;
				}
			}
			output.add(idList.get(i/2)+","+maxIndex);
		}
		return output;
	}
	
	public static List<String> commentTopic(List<String> theraList,List<String> idList){
		List<String> output = new ArrayList<String>();
		for(int i=0;i<theraList.size();i++){
			String[] temps = theraList.get(i).split(" ");
			int maxIndex = 0;
			double max = 0;
			for(int j=0;j<temps.length;j++){
				double prob = Double.parseDouble(temps[j]);
				if(prob>max){
					max = prob;
					maxIndex = j;
				}
			}
			output.add(idList.get(i)+","+maxIndex);
		}
		return output;
	}

}
