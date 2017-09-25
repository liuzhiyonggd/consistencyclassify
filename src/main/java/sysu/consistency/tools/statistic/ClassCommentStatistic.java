package sysu.consistency.tools.statistic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.ClassMessage;
import sysu.consistency.db.bean.CodeComment;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.ClassMessageRepository;

public class ClassCommentStatistic {
	
	public static void main(String[] args) throws IOException {
		ClassMessageRepository classRepo = RepositoryFactory.getClassRepository();
		String[] projects = new String[]{"htmlunit","jhotdraw","jedit","opennms","kablink","ejbca","jamwiki","freecol"};
		List<String> output = new ArrayList<String>();
		
		int count = 0;
		for(String str:projects){
			int codeLine = 0;
			int commentLine = 0;
			
			List<ClassMessage> classList = classRepo.findByProject(str);
			
			for(ClassMessage clazz:classList){
				count++;
				if(count%10000==0){
					System.out.println(count+" is done.");
				}
				int temp = 0;
				codeLine += clazz.getOldCode().size();
				for(CodeComment cc:clazz.getOldComment()){
					commentLine += cc.getEndLine()-cc.getStartLine()+1;
					temp += cc.getEndLine()-cc.getStartLine()+1;
				}
				output.add(clazz.getProject()+","+clazz.getCommitID()+","+clazz.getClassName()+","+clazz.getOldCode().size()+","+temp);
			}
			
			System.out.println(str+":"+codeLine+":"+commentLine+":"+commentLine*1.0d/codeLine);
		}
		FileUtils.writeLines(new File("D:/work/5.13/classLine.csv"), output);
	}
	
	
	

}
