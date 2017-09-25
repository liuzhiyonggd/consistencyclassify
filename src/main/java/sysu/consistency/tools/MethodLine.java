package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.ClassMessage;
import sysu.consistency.db.bean.CodeComment;
import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.bean.MethodBean;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.ClassMessageRepository;
import sysu.consistency.db.repository.CommentRepository;
import sysu.consistency.db.repository.MethodRepository;

public class MethodLine {
	
	public static void main(String[] args) throws IOException {
	    CommentRepository commentRepo = RepositoryFactory.getCommentRepository();
	    ClassMessageRepository classRepo = RepositoryFactory.getClassRepository();
	    MethodRepository methodRepo = RepositoryFactory.getMethodRepository();
	    
	    List<String> idList = FileUtils.readLines(new File("D:/work/5.4/newid.txt"),"UTF-8");
	    List<String> output = new ArrayList<String>();
	    int count = 0;
	    for(String str:idList){
	    	count++;
	    	int commentID = Integer.parseInt(str);
	    	CommentEntry comment = commentRepo.findASingleByCommentID(commentID);
	    	String project = comment.getProject();
	    	String commitID = comment.getCommitID();
	    	String className = comment.getClassName();
	    	ClassMessage clazz = classRepo.findASingleByProjectAndCommitIDAndClassName(project, commitID, className);
	    	List<MethodBean> methodList = methodRepo.findByProjectAndCommitIDAndClassName(project, commitID, className);
	    	MethodBean method = null;
	    	for(MethodBean m:methodList){
	    		if(m.getOldStartLine()<=comment.getOld_scope_startLine()&&m.getOldEndLine()>=comment.getOld_scope_endLine()){
	    			method = m;
	    			break;
	    		}
	    	}
	    	
	    	List<CodeComment> classCommentList = clazz.getOldComment();
	    	int classCommentLine = 0;
	    	int methodCommentLine = 0;
	    	for(CodeComment cc:classCommentList){
	    		if(!cc.getType().equals("Javadoc")){
	    		classCommentLine += cc.getEndLine()-cc.getStartLine()+1;
	    		if(method!=null){
	    			if(method.getOldStartLine()<=cc.getStartLine()&&method.getOldEndLine()>=cc.getEndLine()){
	    				methodCommentLine += cc.getEndLine()-cc.getStartLine()+1;
	    			}
	    		}
	    		}
	    	}
	    	int methodID = -1;
	    	boolean isReturnTypeChange = false;
	    	boolean isParameterChange = false;
	    	if(method!=null){
	    		methodID = method.getMethodID();
	    		if(!method.getNewReturnType().equals(method.getOldReturnType())){
	    			isReturnTypeChange = true;
	    		}
	    		if(method.getNewParameters().size()!=method.getOldParameters().size()){
	    			isParameterChange = true;
	    		}else{
	    			for(int i=0;i<method.getNewParameters().size();i++){
	    				if(!method.getNewParameters().get(i).equals(method.getOldParameters().get(i))){
	    					isParameterChange = true;
	    					break;
	    					
	    				}
	    			}
	    		}
	    	}
	    	output.add(comment.getCommentID()+","+comment.getOldComment().size()+","+classCommentLine+","+methodCommentLine+","+comment.isChange()+","+methodID+","+isReturnTypeChange+","+isParameterChange);
	    	
	    	if(count%10000==0){
	    		FileUtils.writeLines(new File("D:/work/5.7/classmethodcommentline.csv"), output);
	    		System.out.println(count+" is done.");
	    	}
	    	
	    }
	    FileUtils.writeLines(new File("D:/work/5.7/classmethodcommentline.csv"), output);
	}

}
