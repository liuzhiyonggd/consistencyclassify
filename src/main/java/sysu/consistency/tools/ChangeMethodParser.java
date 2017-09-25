package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.MethodBean;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.MethodRepository;

public class ChangeMethodParser {
	
	public static void main(String[] args) throws IOException{
		MethodRepository methodRepo = RepositoryFactory.getMethodRepository();
		List<String> resultFile = FileUtils.readLines(new File("D:/work/4.19/result.txt"),"UTF-8");
		List<String> output = new ArrayList<String>();
		for(String str : resultFile){
			String[] temps = str.split("\t");
			String project = temps[0];
			int commitID = Integer.parseInt(temps[1]);
			String className = temps[2]+".java";
			String[] temps1 = temps[3].split(" ");
			String newMethodName = "";
			for(String str2:temps1){
				if(str2.indexOf("(")>=0){
					newMethodName = str2.substring(0,str2.indexOf("("));
				}
			}
			String[] temps2 = temps[4].split(" ");
			String oldMethodName = "";
			for(String str2:temps2){
				if(str2.indexOf("(")>=0){
					oldMethodName = str2.substring(0,str2.indexOf("("));
				}
			}
			
			String newParameters = temps[3].substring(temps[3].indexOf("(")+1, temps[3].indexOf(")"));
			String oldParameters = temps[4].substring(temps[4].indexOf("(")+1, temps[4].indexOf(")"));
			
			String tNewParameters = newParameters;
			String tOldParameters = oldParameters;
			
			List<MethodBean> methodList = methodRepo.findByProjectAndCommitIDAndClassName(project, commitID+"", className);
			int oldStartLine = -1;
			int oldEndLine = -1;
			int newStartLine = -1;
			int newEndLine = -1;
			for(MethodBean method:methodList){
				if(method.getOldMethodName().equals(oldMethodName)){

					String methodParameters = "";
					for(int i=0;i<method.getOldParameters().size()-1;i++){
						methodParameters +=method.getOldParameters().get(i)+",";
					}
					if(method.getOldParameters().size()>0){
					    methodParameters +=method.getOldParameters().get(method.getOldParameters().size()-1);
					}
					methodParameters = methodParameters.replace(" ", "");
					oldParameters = oldParameters.replace(" ", "");
					if(methodParameters.equals(oldParameters)){
					oldStartLine = method.getOldStartLine();
					oldEndLine = method.getOldEndLine();
					newStartLine = method.getNewStartLine();
					newEndLine = method.getNewEndLine();
					break;
					}
				}
			}
			
			output.add(project+";"+commitID+";"+className+";"+oldMethodName+";"+tOldParameters+";"+newMethodName+";"+tNewParameters+";"+oldStartLine+";"+oldEndLine+";"+newStartLine+";"+newEndLine);
			System.out.println(project+";"+commitID+";"+className+";"+oldMethodName+";"+tOldParameters+";"+newMethodName+";"+tNewParameters+";"+oldStartLine+";"+oldEndLine+";"+newStartLine+";"+newEndLine);
			
			
		}
		
		FileUtils.writeLines(new File("D:/work/4.19/output.txt"), output);
	}

}
