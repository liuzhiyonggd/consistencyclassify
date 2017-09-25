package sysu.consistency.db.extract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import sysu.consistency.db.bean.ClassMessage;
import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.bean.DiffType;
import sysu.consistency.db.bean.Line;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.ClassMessageRepository;
import sysu.consistency.db.repository.CommentRepository;

public class JavadocExtractor {
	
	
	
	@SuppressWarnings("unchecked")
	public static void extract(String project){
		
		ClassMessageRepository classRepository = RepositoryFactory.getClassRepository();
		CommentRepository commentRepository = RepositoryFactory.getCommentRepository();
		
		List<ClassMessage> classList = classRepository.findByProject(project);
		
		for(ClassMessage clazz:classList){
			
			//ֻ��ȡ�޸ĵ���
			if(clazz.getType().equals("new")||clazz.getType().equals("delete")){
				continue;
			}
			
			//����diff�б�Ϊ�յ��࣬����
			if(clazz.getDiffList().isEmpty()){
				continue;
			}
			
			List<DiffType> diffList = clazz.getDiffList();
			List<Line> oldCode = clazz.getOldCode();
			List<Line> newCode = clazz.getNewCode();
			
			
			
			StringBuffer oldCodes = new StringBuffer();
			for(Line line:oldCode){
				oldCodes.append(line.getLine()+"\n");
			}
			StringBuffer newCodes = new StringBuffer();
			for(Line line:newCode){
				newCodes.append(line.getLine()+"\n");
			}
			
			//��ȡ������ķ����б�
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setSource(oldCodes.toString().toCharArray());
			CompilationUnit oldUnit = (CompilationUnit)parser.createAST(null);
			parser.setSource(newCodes.toString().toCharArray());
			CompilationUnit newUnit =(CompilationUnit)parser.createAST(null);

			List<MethodDeclaration> newMethodList = new ArrayList<MethodDeclaration>();
			List<MethodDeclaration> oldMethodList = new ArrayList<MethodDeclaration>();
			
			for(TypeDeclaration newClass:(List<TypeDeclaration>)newUnit.types()){
				MethodDeclaration[] newMethods = newClass.getMethods();
				for(MethodDeclaration method:newMethods){
					newMethodList.add(method);
				}
			}
			
			for(TypeDeclaration oldClass:(List<TypeDeclaration>)oldUnit.types()){
				MethodDeclaration[] oldMethods = oldClass.getMethods();
				for(MethodDeclaration method:oldMethods){
					oldMethodList.add(method);
				}
			}
			
			//��ɾ���ķ����������ķ����ӷ����б��кͷ���token�б���ɾ��
			for(DiffType diff:diffList){
				if (diff.getType().equals("ADDITIONAL_FUNCTIONALITY")) {
					removeMethod(newMethodList, newUnit,diff.getNewStartLine());
				} else if (diff.getType().equals("REMOVED_FUNCTIONALITY")) {
					removeMethod(oldMethodList, oldUnit,diff.getOldStartLine());
				}
			}
			
			//���ȥ��ɾ���������ķ����󷽷���������ͬ������Ϊ����
			if(newMethodList.size()!=oldMethodList.size()){
				System.out.println("project:"+clazz.getProject()+" commit:"+clazz.getCommitID()+" class:"+clazz.getClassName()+" method list not equal error.");
				System.out.println("diff size:"+clazz.getDiffList().size());
				continue;
			}
			
			for(int i=0,n=newMethodList.size();i<n;i++){
				
				MethodDeclaration newMethod = newMethodList.get(i);
				MethodDeclaration oldMethod = oldMethodList.get(i);
				
				int newMethodStartLine = newUnit.getLineNumber(newMethod.getStartPosition());
				int newMethodEndLine = newUnit.getLineNumber(newMethod.getStartPosition()+newMethod.getLength()-1);
				int oldMethodStartLine = oldUnit.getLineNumber(oldMethod.getStartPosition());
				int oldMethodEndLine = oldUnit.getLineNumber(oldMethod.getStartPosition()+oldMethod.getLength()-1);
				CommentEntry commentEntry = new CommentEntry();
				commentEntry.setNew_scope_startLine(newMethodStartLine);
				commentEntry.setNew_scope_endLine(newMethodEndLine);
				commentEntry.setOld_scope_startLine(oldMethodStartLine);
				commentEntry.setOld_scope_endLine(oldMethodEndLine);
				
				boolean flag = false;//����ע���Ƿ�仯
				
				//Ѱ�ҷ����еı仯�������뵽commentEntry��diffList��
				for(DiffType diff:diffList){
					if((diff.getNewStartLine()>=commentEntry.getNew_scope_startLine()&&diff.getNewEndLine()<=commentEntry.getNew_scope_endLine()
							&&diff.getOldStartLine()>=commentEntry.getOld_scope_startLine()&&diff.getOldEndLine()<=commentEntry.getOld_scope_endLine())
							||(diff.getNewStartLine()>=commentEntry.getNew_scope_startLine()&&diff.getNewEndLine()<=commentEntry.getNew_scope_endLine()
							&&diff.getOldStartLine()==0&&diff.getOldEndLine()==0)
							||(diff.getNewStartLine()==0&&diff.getNewEndLine()==0&&diff.getOldStartLine()>=commentEntry.getOld_scope_startLine()
							&&diff.getOldEndLine()<=commentEntry.getOld_scope_endLine())){
						
						commentEntry.getDiffList().add(diff);
						if(diff.getType().equals("DOC_UPDATE")){
							flag =true;
						}
					}
					
					
				}
				
				//��������ޱ仯��������
				if(commentEntry.getDiffList().isEmpty()){
					continue;
				}
				
				
				commentEntry.setProject(clazz.getProject());
				commentEntry.setCommitID(clazz.getCommitID());
				commentEntry.setClassName(clazz.getClassName());
				commentEntry.setType("Javadoc");
				commentEntry.setChange(flag);
				
				//�������û��ע�ͣ�������
				if(newMethod.getJavadoc()==null||oldMethod.getJavadoc()==null){
					continue;
				}
				
				int newCommentStartLine = newUnit.getLineNumber(newMethod.getJavadoc().getStartPosition());
				int newCommentEndLine = newUnit.getLineNumber(newMethod.getJavadoc().getStartPosition()+newMethod.getJavadoc().getLength()-1);
				int oldCommentStartLine = oldUnit.getLineNumber(oldMethod.getJavadoc().getStartPosition());
				int oldCommentEndLine = oldUnit.getLineNumber(oldMethod.getJavadoc().getStartPosition()+oldMethod.getJavadoc().getLength()-1);
				
				commentEntry.setNew_comment_startLine(newCommentStartLine);
				commentEntry.setNew_comment_endLine(newCommentEndLine);
				commentEntry.setOld_comment_startLine(oldCommentStartLine);
				commentEntry.setOld_comment_endLine(oldCommentEndLine);
				
				if(commentEntry.getNew_comment_endLine()<commentEntry.getNew_scope_endLine()){
					List<String> newCodeList = getCodeList(newCode,commentEntry.getNew_comment_endLine()+1,commentEntry.getNew_scope_endLine());
					List<String> oldCodeList = getCodeList(oldCode,commentEntry.getOld_comment_endLine()+1,commentEntry.getOld_scope_endLine());
					
					commentEntry.setNewCode(newCodeList);
					commentEntry.setOldCode(oldCodeList);
				}
				
				List<String> newComment = getCodeList(newCode,commentEntry.getNew_comment_startLine(),commentEntry.getNew_comment_endLine());
				List<String> oldComment = getCodeList(oldCode,commentEntry.getOld_comment_startLine(),commentEntry.getOld_comment_endLine());
				
				commentEntry.setNewComment(newComment);
				commentEntry.setOldComment(oldComment);
				
				commentRepository.insert(commentEntry);
				
			}
			
			
		}
		
	}

	private static List<String> getCodeList(List<Line> newCode, int startLine, int endLine) {
		
		List<String> codeList = new ArrayList<String>();
		for(int i=startLine-1;i<endLine;i++){
			codeList.add(newCode.get(i).getLine());
		}
		return codeList;
	}

	private static void removeMethod( List<MethodDeclaration> methodList, CompilationUnit unit,int startLine) {
		
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
		
		String[] projects = new String[]{"htmlunit","springframework","jamwiki","symmetricds","fedora-commons","opennms","kablink","ejbca"};
		DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		for(String project:projects){
			
			JavadocExtractor.extract(project);
			System.out.println(project +" is done.");
			System.out.println(sdf2.format(new Date()));
		}
	}

}
