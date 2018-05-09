package sysu.consistency.classify.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentRepository;
import sysu.consistency.tools.splitword.StringTools;

public class Test {

	// 生成 comment2 数据库中的数据集
	public static void main(String[] args) throws IOException {
		List<String> file1 = FileUtils.readLines(new File("dataset/data10-2.arff"),"UTF-8");
		List<String> file2 = FileUtils.readLines(new File("F:/file/similarfeature.txt"),"UTF-8");
		List<String> file4 = FileUtils.readLines(new File("dataset/filter_diffFeature.csv"),"UTF-8");
		List<String> file3 = FileUtils.readLines(new File("dataset/label2.csv"),"UTF-8");
		
		Map<Integer,String> similarFeatureMap = new HashMap<Integer,String>();
		for(String str:file2) {
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			
			similarFeatureMap.put(commentID, temps[1]);
		}
		
		Map<Integer,String> diffFeatureMap = new HashMap<Integer,String>();
		for(String str:file4) {
			String[] temps = str.split(" ");
			int commentID = Integer.parseInt(temps[0]);
			String temp = "";
			for(int i=1;i<temps.length;i++) {
				temp += temps[i]+" ";
			}
			diffFeatureMap.put(commentID, temp);
		}
		
		
		Map<Integer,String> labelMap = new HashMap<Integer,String>();
		for(String str:file3) {
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			labelMap.put(commentID, temps[1]);
		}
		
		List<String> output = new ArrayList<String>();
		int index = 0;
		for(String str:file1) {
			output.add(str);
			index ++;
			if(str.equals("@data")) {
				break;
			}
		}
		for(;index<file1.size();index++) {
			int commentID = Integer.parseInt(file1.get(index).split(" ")[0]);
			String similarFeature = similarFeatureMap.get(commentID);
			String diffFeature = diffFeatureMap.get(commentID);
			String label = labelMap.get(commentID);
			output.add(file1.get(index)+" "+diffFeature+label);
		}
		
		FileUtils.writeLines(new File("dataset/data12-2.arff"), output);
	}
	
	//生成comment 数据库中的数据集
//	public static void main(String[] args) throws IOException {
//		List<String> file1 = FileUtils.readLines(new File("dataset/data9.txt"),"UTF-8");
//		List<String> file2 = FileUtils.readLines(new File("file/data_1_2_5_6_3.csv"),"UTF-8");
//		
//		int index = 0;
//		for(String str:file1) {
//			index ++;
//			if(str.equals("@data")) {
//				break;
//			}
//		}
//		
//		List<Integer> idList = new ArrayList<Integer>();
//		for(;index<file1.size();index++) {
//			idList.add(Integer.parseInt(file1.get(index).split(" ")[0]));
//		}
//		
//		List<String> output = new ArrayList<String>();
//		for(String str:file2) {
//			int commentID = Integer.parseInt(str.split(",")[0]);
//			if(idList.contains(commentID)) {
//				output.add(str);
//			}
//		}
//		
//		FileUtils.writeLines(new File("dataset/data11.arff"), output);
//	}
	
	// 生成数据标签文件
//	public static void main(String[] args) throws IOException {
//		List<String> file1 = FileUtils.readLines(new File("file/data_1_2_5_6_3.csv"),"UTF-8");
//		
//		List<String> output = new ArrayList<String>();
//		for(String str:file1) {
//			String[] temps = str.split(",");
//			int commentID = Integer.parseInt(temps[0]);
//			int label = Integer.parseInt(temps[temps.length-1]);
//			output.add(commentID+","+label);
//		}
//		
//		FileUtils.writeLines(new File("dataset/label.csv"), output);
//	}
	
	// 生成作用域过滤后的数据标签文件
//	public static void main(String[] args) throws IOException {
//		List<String> file1 = FileUtils.readLines(new File("file/data_1_2_5_6_3.csv"),"UTF-8");
//		List<String> output = new ArrayList<String>();
//		List<Integer> idList = new ArrayList<Integer>();
//		for(String str:file1) {
//			idList.add(Integer.parseInt(str.split(",")[0]));
//		}
//		
//		CommentRepository commentRepo = RepositoryFactory.getCommentRepository();
//		
//		int count = 0;
//		
//		for(int id:idList) {
//			
//			count ++;
//			if(count%1000==0) {
//				System.out.println(count + " is done.");
//			}
//			
//			CommentEntry comment = commentRepo.findASingleByCommentID(id);
//			
//			List<String> newCommentList = comment.getNewComment();
//			List<String> oldCommentList = comment.getOldComment();
//			
//			List<String> newCodeList = comment.getNewCode();
//			List<String> oldCodeList = comment.getOldCode();
//			List<String> newNewCommentList = new ArrayList<String>();
//			for(int i=0;i<newCommentList.size();i++) {
//				String commString = newCommentList.get(i);
//				if(i<=comment.getNew_comment_endLine()-comment.getNew_comment_startLine()) {
//					newNewCommentList.add(commString);
//				}else {
//					if(newCodeList.contains(commString)) {
//						newNewCommentList.add(commString);
//					}
//				}
//			}
//			
//			List<String> newOldCommentList = new ArrayList<String>();
//			for(int i=0;i<oldCommentList.size();i++) {
//				String commString = oldCommentList.get(i);
//				if(i<=comment.getOld_comment_endLine()-comment.getOld_comment_startLine()) {
//					newOldCommentList.add(commString);
//				}else {
//					if(oldCodeList.contains(commString)) {
//						newOldCommentList.add(commString);
//					}
//				}
//			}
//			
//			comment.setNewComment(newNewCommentList);
//			comment.setOldComment(newOldCommentList);
//			
//			double similar = StringTools.computeSimilarity(newNewCommentList, newOldCommentList);
//			boolean ischange = false;
//			if(similar<1) {
//				ischange = true;
//			}
//			comment.setChange(ischange);
//			commentRepo.save(comment);
//			
//			int label = ischange?1:0;
//			output.add(comment.getCommentID()+","+label);
//			
//		}
//		FileUtils.writeLines(new File("dataset/label2.csv"), output);
//		
//	}
	
	// 过滤掉两个数据库中标签不同的数据
//	public static void main(String[] args) throws IOException {
//		List<String> file1 = FileUtils.readLines(new File("dataset/filterid.txt"),"UTF-8");
//		List<String> file2 = FileUtils.readLines(new File("dataset/data10.arff"),"UTF-8");
//		List<String> file3 = FileUtils.readLines(new File("dataset/data11.arff"),"UTF-8");
//		
//		Set<Integer> filterSet = new HashSet<Integer>();
//		for(String str:file1) {
//			filterSet.add(Integer.parseInt(str));
//		}
//		
//		List<String> output = new ArrayList<String>();
//		List<String> output2 = new ArrayList<String>();
//		int index = 0;
//		for(;index<file2.size();index++) {
//			output.add(file2.get(index));
//			if(file2.get(index).equals("@data")) {
//				index++;
//				break;
//			}
//		}
//		
//		for(;index<file2.size();index++) {
//			int commentID = Integer.parseInt(file2.get(index).split(" ")[0]);
//			if(!filterSet.contains(commentID)) {
//				output.add(file2.get(index));
//			}
//		}
//		
//		index = 0;
//		for(;index<file3.size();index++) {
//			output2.add(file3.get(index));
//			if(file3.get(index).equals("@data")) {
//				index++;
//				break;
//			}
//		}
//		
//		for(;index<file3.size();index++) {
//			int commentID = Integer.parseInt(file3.get(index).split(",")[0]);
//			if(!filterSet.contains(commentID)) {
//				output2.add(file3.get(index));
//			}
//		}
//		
//		FileUtils.writeLines(new File("dataset/data10-2.arff"), output);
//		FileUtils.writeLines(new File("dataset/data11-2.arff"), output2);
//		
//	}
	
}
