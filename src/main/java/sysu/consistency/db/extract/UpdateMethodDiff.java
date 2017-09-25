package sysu.consistency.db.extract;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

import sysu.consistency.db.bean.ClassMessage;
import sysu.consistency.db.bean.DiffType;
import sysu.consistency.db.bean.MethodBean;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.ClassMessageRepository;
import sysu.consistency.db.repository.MethodRepository;
import sysu.consistency.db.tool.MethodTool;

public class UpdateMethodDiff {
	
	public static void main(String[] args) {
		MethodRepository methodRepo = RepositoryFactory.getMethodRepository();
		ClassMessageRepository classRepo = RepositoryFactory.getClassRepository();
		
		DBCollection methods = new Mongo("172.18.217.106").getDB("sourcebase").getCollection("method");
		
		for(int i=505001;i<=1125585;i++){
			if(i%1000==0){
				System.out.println(i+" is done.");
			}
			ClassMessage clazz = classRepo.findASingleByClassID(i);
			if(!clazz.getType().equals("change")){
				continue;
			}
			if(clazz.getDiffList().size()<=0){
				continue;
			}
			List<MethodBean> methodList = methodRepo.findByProjectAndCommitIDAndClassName(clazz.getProject(), clazz.getCommitID(), clazz.getClassName());
			List<DiffType> diffList = clazz.getDiffList();
			for(MethodBean method:methodList){
				List<DiffType> methodDiffList = new ArrayList<DiffType>();
				for(DiffType diff:diffList){
					if((diff.getNewStartLine()>=method.getNewStartLine()&&diff.getNewEndLine()<=method.getNewEndLine())||
							(diff.getOldStartLine()>=method.getOldStartLine()&&diff.getOldEndLine()<=method.getOldEndLine())){
						
						methodDiffList.add(diff);
					}
				}
				method.setDiffList(methodDiffList);
				methods.update(new BasicDBObject().append("method_id",method.getMethodID()), MethodTool.methodBean2DBObject(method));
				
			}
		}
	}

}
