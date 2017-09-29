package sysu.consistency.db.bean;

import java.util.List;

import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.ClassMessageRepository;

public class Test {
	
	public static void main(String[] args){
		testClassTable();
	}
	
	public static void testClassTable(){
		ClassMessageRepository classRepo = RepositoryFactory.getClassRepository();
		
		List<ClassMessage> classList = classRepo.findByProjectAndCommitID("jhotdraw", "100");
		
		ClassMessage clazz = classList.get(0);
		
		System.out.println(clazz.getProject());
		System.out.println(clazz.getCommitID()+"");
		System.out.println(clazz.getClassName());
		System.out.println(clazz.getType());
		System.out.println("new code:"+clazz.getNewCode().size());
		System.out.println("old code:"+clazz.getOldCode().size());
		System.out.println("new comment:"+clazz.getNewComment().size());
		System.out.println("old comment:"+clazz.getOldComment().size());
		System.out.println("new token:"+clazz.getNewTokenList().size());
		System.out.println("old token:"+clazz.getOldTokenList().size());
		System.out.println("diff:"+clazz.getDiffList().size());
//		System.out.println(clazz.getIsCoreProbability());
	}

}
