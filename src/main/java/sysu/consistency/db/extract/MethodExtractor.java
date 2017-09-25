package sysu.consistency.db.extract;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import sysu.consistency.db.bean.ClassMessage;
import sysu.consistency.db.bean.DiffType;
import sysu.consistency.db.bean.Line;
import sysu.consistency.db.bean.MethodBean;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.ClassMessageRepository;
import sysu.consistency.db.repository.MethodRepository;

public class MethodExtractor {
	
	public static void extract(ClassMessage clazz){
		MethodRepository methodRepo = RepositoryFactory.getMethodRepository();
		StringBuilder sb =  new StringBuilder();
		for(Line line:clazz.getNewCode()){
			sb.append(line.getLine()+"\n");
		}
		String newCode = sb.toString();
		
		sb = new StringBuilder();
		for(Line line:clazz.getOldCode()){
			sb.append(line.getLine()+"\n");
		}
		String oldCode = sb.toString();
		
		//��ȡ������ķ����б�
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(oldCode.toCharArray());
		CompilationUnit oldUnit = (CompilationUnit)parser.createAST(null);
		parser.setSource(newCode.toCharArray());
		CompilationUnit newUnit =(CompilationUnit)parser.createAST(null);

		List<MethodDeclaration> newMethodList = new ArrayList<MethodDeclaration>();
		List<MethodDeclaration> oldMethodList = new ArrayList<MethodDeclaration>();
		
		List<Statement> newStatementList = new ArrayList<Statement>();
		List<Statement> oldStatementList = new ArrayList<Statement>();
		for(Object obj:newUnit.types()){
			if(!(obj instanceof TypeDeclaration)){
				continue;
			}
			TypeDeclaration newClass = (TypeDeclaration)obj;
			MethodDeclaration[] newMethods = newClass.getMethods();
			for(MethodDeclaration method:newMethods){
				newMethodList.add(method);
				if(method.getBody()!=null&&method.getBody().statements()!=null){
					newStatementList.addAll(method.getBody().statements());
				}
			}
		}
		
		for(Object obj:oldUnit.types()){
			if(!(obj instanceof TypeDeclaration)){
				continue;
			}
			TypeDeclaration oldClass = (TypeDeclaration)obj;
			MethodDeclaration[] oldMethods = oldClass.getMethods();
			for(MethodDeclaration method:oldMethods){
				oldMethodList.add(method);
				if(method.getBody()!=null&&method.getBody().statements()!=null){
					oldStatementList.addAll(method.getBody().statements());
				}
			}
		}
		
		//��ɾ���ķ����������ķ����漰��ע�ʹ��б���ɾ��
		for(DiffType diff:clazz.getDiffList()){
			if (diff.getType().equals("ADDITIONAL_FUNCTIONALITY")) {
				removeMethod(newMethodList, newUnit,diff.getNewStartLine());
			} else if (diff.getType().equals("REMOVED_FUNCTIONALITY")) {
				removeMethod(oldMethodList, oldUnit,diff.getOldStartLine());
			}
		}
		
		if(newMethodList.size()!=oldMethodList.size()){
			return;
		}
		
		for(int i=0;i<newMethodList.size();i++){
			MethodDeclaration newMethod = newMethodList.get(i);
			MethodDeclaration oldMethod = oldMethodList.get(i);
			
			String newMethodName = newMethod.getName().toString();
			String oldMethodName = oldMethod.getName().toString();
			String newReturnType;
			if(newMethod.getReturnType2()==null){
				newReturnType = "null";
			}else{
			    newReturnType = newMethod.getReturnType2().toString();
			}
			String oldReturnType;
			if(oldMethod.getReturnType2()==null){
				oldReturnType = "null";
			}else{
				oldReturnType = oldMethod.getReturnType2().toString();
			}
			
			List<String> newParameters = new ArrayList<String>();
			for(Object obj:newMethod.parameters()){
				newParameters.add(obj.toString());
			}
			
			List<String> oldParameters = new ArrayList<String>();
			for(Object obj:oldMethod.parameters()){
				oldParameters.add(obj.toString());
			}
			
			int newStartLine = newUnit.getLineNumber(newMethod.getStartPosition());
			int oldStartLine = oldUnit.getLineNumber(oldMethod.getStartPosition());
			int newEndLine = newUnit.getLineNumber(newMethod.getStartPosition()+newMethod.getLength()-1);
			int oldEndLine = oldUnit.getLineNumber(oldMethod.getStartPosition()+oldMethod.getLength()-1);
			
			MethodBean method = new MethodBean();
			method.setNewMethodName(newMethodName);
			method.setOldMethodName(oldMethodName);
			method.setProject(clazz.getProject());
			method.setCommitID(clazz.getCommitID());
			method.setClassName(clazz.getClassName());
			
			method.setNewParameters(newParameters);
			method.setOldParameters(oldParameters);
			method.setNewReturnType(newReturnType);
			method.setOldReturnType(oldReturnType);
			method.setNewStartLine(newStartLine);
			method.setNewEndLine(newEndLine);
			method.setOldStartLine(oldStartLine);
			method.setOldEndLine(oldEndLine);
			
			methodRepo.insert(method);
		}
	}
	
    private static void removeMethod( List<MethodDeclaration> methodList, CompilationUnit unit, int startLine) {
		
		for(int i=0,n=methodList.size();i<n;i++){
			MethodDeclaration method = methodList.get(i);
			int methodStartLine = unit.getLineNumber(method.getStartPosition());
			if(methodStartLine==startLine){
				methodList.remove(i);
				break;
			}
		}
	}
    
    public static void main(String[] args) {
		ClassMessageRepository classRepo = RepositoryFactory.getClassRepository();
		String[] projects = new String[]{"jhotdraw","jedit","ejbca","htmlunit","freecol","jamwiki","opennms","kablink"};
		Set<String> projectSet = new HashSet<String>();
		for(String project:projects){
			projectSet.add(project);
		}
		for(int i=669027;i<=678981;i++){
			if(i%1000==0){
				System.out.println(i+" is done.");
			}
			
		    ClassMessage clazz = classRepo.findASingleByClassID(i);
		    if(!projectSet.contains(clazz.getProject())){
		    	continue;
		    }
		    if(clazz.getType().equals("change")){
		    	
		        MethodExtractor.extract(clazz);
		    }
		}
	}

}
