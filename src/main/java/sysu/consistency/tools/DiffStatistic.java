package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.bean.DiffType;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentRepository;

public class DiffStatistic {
	
	public static void main(String[] args) {
		CommentRepository commentRepo = RepositoryFactory.getCommentRepository();
		Map<String,Integer> map = new HashMap<String,Integer>();
		Map<String,Integer> map2 = new HashMap<String,Integer>();
		for(int i=1;i<=111180;i++){
			
			if(i%1000==0){
				System.out.println(i+" is done.");
			}
			
			if(i%3==0){
				continue;
			}
			
			CommentEntry comment = commentRepo.findASingleByCommentID(i);
			if(comment.isChange2()){
				continue;
			}
			List<DiffType> diffList = comment.getDiffList();
			if(diffList.size()==0){
				continue;
			}
			String str = "";
			for(DiffType diff:diffList){
				String type = diff.getType();
				if(type.indexOf("COMMENT")<0){
				    str += diff.getType()+";";
				}
			}
			if(!map.containsKey(str)){
				map.put(str, 1);
				if(comment.isChange()){
					map2.put(str, 1);
				}else{
					map2.put(str, 0);
				}
			}else{
				map.put(str,map.get(str)+1);
				if(comment.isChange()){
					map2.put(str, map2.get(str)+1);
				}
			}
		}
		List<String> output = new ArrayList<String>();
		for(Map.Entry<String, Integer> entry : map.entrySet()){
			output.add(entry.getKey()+","+entry.getValue()+","+map2.get(entry.getKey()));
		}
		try {
			FileUtils.writeLines(new File("D:/work/4.8/diffstatistic.csv"), output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
