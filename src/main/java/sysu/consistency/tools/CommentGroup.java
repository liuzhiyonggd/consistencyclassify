package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentRepository;

public class CommentGroup {
	public static void main(String[] args) throws IOException{
		CommentRepository commentRepo = RepositoryFactory.getCommentRepository();
		Map<String,String> groupMap = new HashMap<String,String>();
		List<String> commentList = new ArrayList<String>();
		for(int i=1;i<=111180;i++){
			if(i%1000==0){
				System.out.println(i+" is done.");
			}
			CommentEntry comment = commentRepo.findASingleByCommentID(i);
			String group = comment.getProject()+","+comment.getCommitID()+","+comment.getClassName();
			if(groupMap.containsKey(group)){
				groupMap.put(group, groupMap.get(group)+" "+comment.getCommentID());
			}else{
				groupMap.put(group, comment.getCommentID()+"");
			}
			commentList.add(comment.getProject()+","+comment.getCommitID()+","+comment.getClassName()+","+comment.getCommentID()+","
			+comment.getNew_scope_startLine()+","+comment.getNew_scope_endLine()+","+comment.getOld_scope_startLine()+","+comment.getOld_scope_endLine());
		}
		List<String> groupList = new ArrayList<String>();
		for(Map.Entry<String, String> entry:groupMap.entrySet()){
			groupList.add(entry.getKey()+","+entry.getValue());
		}
		
		FileUtils.writeLines(new File("D:/work/4.27/group.csv"), groupList);
		FileUtils.writeLines(new File("D:/work/4.27/comment.csv"), commentList);
	}

}
