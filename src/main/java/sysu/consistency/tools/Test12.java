package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentRepository;

public class Test12 {
	public static void main(String[] args)throws IOException {
		
		List<String> idList = FileUtils.readLines(new File("D:/work/5.2/id.txt"),"UTF-8");
		
		List<String> lt3List = new ArrayList<String>();
		List<String> classList = new ArrayList<String>();
		CommentRepository commentRepo = RepositoryFactory.getCommentRepository();
		for(String str:idList){
			int id = Integer.parseInt(str);
			CommentEntry comment = commentRepo.findASingleByCommentID(id);
			List<String> oldCommentList = comment.getOldComment();
			List<String> newCommentList = comment.getNewComment();
			
			String oldComment = "";
			for(String str2:oldCommentList){
				oldComment += str2+" ";
			}
			String newComment = "";
			for(String str2:newCommentList){
				newComment += str2+" ";
			}
			String splitToken = " .,;:/&|`~%+=-*<>$#@!^\\()[]{}''\"\r\n\t";
			StringTokenizer st = new StringTokenizer(oldComment,splitToken,false);
			List<String> oldWordList = new ArrayList<String>();
			while(st.hasMoreTokens()){
				oldWordList.add(st.nextToken());
			}
			if(oldWordList.size()<3){
				lt3List.add(str);
			}
			
			st = new StringTokenizer(newComment,splitToken,false);
			List<String> newWordList = new ArrayList<String>();
			while(st.hasMoreTokens()){
				newWordList.add(st.nextToken());
			}
			
			boolean flag = false;
			if(newWordList.size()==oldWordList.size()){
				flag = true;
				for(int i=0;i<newWordList.size();i++){
					if(!newWordList.get(i).equals(oldWordList.get(i))){
						flag = false;
						break;
					}
				}
			}
			
			int ischange = flag?1:0;
			classList.add(ischange+"");
		}
		FileUtils.writeLines(new File("D:/work/5.3/lt3.txt"), lt3List);
		FileUtils.writeLines(new File("D:/work/5.3/class.txt"), classList);
	}
	
	private static List<String> getClass1IDList(List<String> trainFile) throws IOException{
		List<String> newTrainFile = new ArrayList<String>();
		int index = 0;
		for(;index<trainFile.size();index++){
			
			if(trainFile.get(index).equals("@data")){
				newTrainFile.add("@data");
				break;
			}
		}
		index++;
		for(;index<trainFile.size();index++){
			String str = trainFile.get(index);
			String[] temps = str.split(",");
			
			int commentID = Integer.parseInt(temps[0]);
			int classIndex = Integer.parseInt(temps[temps.length-1]);
			if(classIndex==1){
			    newTrainFile.add(commentID+"");
			}
			
		}
		return newTrainFile;
	}

}
