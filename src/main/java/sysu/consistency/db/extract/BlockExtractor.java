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
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import sysu.consistency.db.bean.ClassMessage;
import sysu.consistency.db.bean.CodeComment;
import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.bean.DiffType;
import sysu.consistency.db.bean.Line;
import sysu.consistency.db.bean.Token;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.ClassMessageRepository;
import sysu.consistency.db.repository.CommentRepository;
import sysu.consistency.tools.splitword.StringTools;

public class BlockExtractor {

	@SuppressWarnings("unchecked")
	public static void extract(String project) {

		int errorClassNum = 0;
		
		ClassMessageRepository classRepository = RepositoryFactory.getClassRepository();
		CommentRepository commentRepository = RepositoryFactory.getCommentRepository();
		
		List<ClassMessage> classList = classRepository.findByProject(project);
		for (ClassMessage clazz:classList) {

			// 只锟斤拷取锟睫改碉拷锟斤拷
			if (clazz.getType().equals("new") || clazz.getType().equals("delete")) {
				continue;
			}

			// 锟斤拷锟斤拷diff锟叫憋拷为锟秸碉拷锟洁，锟斤拷锟斤拷
			if (clazz.getDiffList().isEmpty()) {
				continue;
			}
			
			boolean isError = false;

			List<DiffType> diffList = clazz.getDiffList();
			List<Line> oldCode = clazz.getOldCode();
			List<Line> newCode = clazz.getNewCode();

			StringBuffer oldCodes = new StringBuffer();
			for (Line line : oldCode) {
				oldCodes.append(line.getLine() + "\n");
			}
			StringBuffer newCodes = new StringBuffer();
			for (Line line : newCode) {
				newCodes.append(line.getLine() + "\n");
			}

			// 锟斤拷取锟斤拷锟斤拷锟斤拷姆锟斤拷锟斤拷斜锟�
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setSource(oldCodes.toString().toCharArray());
			CompilationUnit oldUnit = (CompilationUnit) parser.createAST(null);
			parser.setSource(newCodes.toString().toCharArray());
			CompilationUnit newUnit = (CompilationUnit) parser.createAST(null);

			List<MethodDeclaration> newMethodList = new ArrayList<MethodDeclaration>();
			List<MethodDeclaration> oldMethodList = new ArrayList<MethodDeclaration>();

			List<Statement> newStatementList = new ArrayList<Statement>();
			List<Statement> oldStatementList = new ArrayList<Statement>();
			for (TypeDeclaration newClass : (List<TypeDeclaration>) newUnit.types()) {
				MethodDeclaration[] newMethods = newClass.getMethods();
				for (MethodDeclaration method : newMethods) {
					newMethodList.add(method);
					if (method.getBody() != null && method.getBody().statements() != null) {
						newStatementList.addAll(method.getBody().statements());
					}
				}
			}

			for (TypeDeclaration oldClass : (List<TypeDeclaration>) oldUnit.types()) {
				MethodDeclaration[] oldMethods = oldClass.getMethods();
				for (MethodDeclaration method : oldMethods) {
					oldMethodList.add(method);
					if (method.getBody() != null && method.getBody().statements() != null) {
						oldStatementList.addAll(method.getBody().statements());
					}
				}
			}

			List<CodeComment> newCommentList_temp = clazz.getNewComment();
			List<CodeComment> oldCommentList_temp = clazz.getOldComment();
			
			List<CodeComment> newCommentList = new ArrayList<CodeComment>();
			List<CodeComment> oldCommentList = new ArrayList<CodeComment>();

			// 锟斤拷删锟斤拷锟侥凤拷锟斤拷锟斤拷锟斤拷锟斤拷锟侥凤拷锟斤拷锟接凤拷锟斤拷锟叫憋拷锟叫和凤拷锟斤拷token锟叫憋拷锟斤拷删锟斤拷
			for (DiffType diff : diffList) {
				if (diff.getType().equals("ADDITIONAL_FUNCTIONALITY")) {
					removeMethod(newMethodList, newUnit, diff.getNewStartLine());
				} else if (diff.getType().equals("REMOVED_FUNCTIONALITY")) {
					removeMethod(oldMethodList, oldUnit, diff.getOldStartLine());
				}
			}
			
			if(newMethodList.size()!=oldMethodList.size()){
				continue;
			}
			
			for(int i=0,n=newMethodList.size();i<n;i++){
				MethodDeclaration newMethod = newMethodList.get(i);
				MethodDeclaration oldMethod = oldMethodList.get(i);
				
				int newMethodStartLine = newUnit.getLineNumber(newMethod.getStartPosition());
				int newMethodEndLine = newUnit.getLineNumber(newMethod.getStartPosition()+newMethod.getLength()-1);
				
				int oldMethodStartLine = oldUnit.getLineNumber(oldMethod.getStartPosition());
				int oldMethodEndLine = oldUnit.getLineNumber(oldMethod.getStartPosition()+oldMethod.getLength()-1);
				
				List<CodeComment> newMethodCommentList = new ArrayList<CodeComment>();
				List<CodeComment> oldMethodCommentList = new ArrayList<CodeComment>();
				
				List<CodeComment> newMoveCommentList = new ArrayList<CodeComment>();
				List<CodeComment> oldMoveCommentList = new ArrayList<CodeComment>();
				
				for(CodeComment newComment : newCommentList_temp){
					if(newComment.getStartLine()>newMethodStartLine&&newComment.getEndLine()<=newMethodEndLine&&newComment.getType().toString().equals("Block")){
						newMethodCommentList.add(newComment);
					}
				}
				
				for(CodeComment oldComment:oldCommentList_temp){
					if(oldComment.getStartLine()>oldMethodStartLine&&oldComment.getEndLine()<=oldMethodEndLine&&oldComment.getType().toString().equals("Block")){
						oldMethodCommentList.add(oldComment);
					}
				}
				
				//锟斤拷取Line注锟斤拷锟叫憋拷锟斤拷删锟斤拷锟斤拷锟斤拷锟侥猴拷删锟斤拷锟斤拷注锟斤拷
				for(int j=0,m=newMethodCommentList.size();j<m;j++){
					CodeComment newComment = newMethodCommentList.get(j);
					List<String> newCommentMessage = getCodeList(newCode,newComment.getStartLine(),newComment.getEndLine());
					
					//锟斤拷锟斤拷锟阶拷痛锟斤拷锟斤拷注锟酵ｏ拷删锟斤拷
					if(StringTools.isCode(newCommentMessage)){
						newMethodCommentList.remove(j);
						j--;
						m--;
					}else{
						//锟斤拷锟剿碉拷锟斤拷注锟斤拷
						List<Token> tokenList = clazz.getNewTokenList();
						boolean flag = true;
						for(Token token:tokenList){
							if(token.getStartLine()==newComment.getStartLine()&&token.getEndLine()==newComment.getEndLine()){
								newMethodCommentList.remove(j);
								j--;
								m--;
								flag = false;
								break;
							}
						}
						
						if(flag){
							//删锟斤拷锟斤拷锟斤拷注锟酵ｏ拷锟斤拷锟斤拷注锟酵合诧拷锟斤拷锟斤拷锟斤拷锟叫讹拷锟斤拷锟斤拷锟斤拷锟侥ｏ拷锟斤拷删锟斤拷锟斤拷
							for(DiffType diff:diffList){
								if(diff.getNewStartLine()==newComment.getStartLine()&&diff.getNewEndLine()==newComment.getEndLine()&&
										(diff.getType().equals("COMMENT_INSERT")||diff.getType().equals("COMMENT_MOVE"))){
									newMethodCommentList.remove(j);
									j--;
									m--;
									if(diff.getType().equals("COMMENT_MOVE")){
										newMoveCommentList.add(newComment);
									}
									break;
								}
							}
						}
					}
				}
				
				//锟斤拷取Line注锟斤拷锟叫憋拷锟斤拷删锟斤拷锟斤拷锟斤拷锟侥猴拷删锟斤拷锟斤拷注锟斤拷
				for(int j=0,m=oldMethodCommentList.size();j<m;j++){
					CodeComment oldComment = oldMethodCommentList.get(j);
					List<String> oldCommentMessage = getCodeList(oldCode,oldComment.getStartLine(),oldComment.getEndLine());
					
					//锟斤拷锟斤拷锟阶拷痛锟斤拷锟斤拷注锟酵ｏ拷删锟斤拷
					if(StringTools.isCode(oldCommentMessage)){
						oldMethodCommentList.remove(j);
						j--;
						m--;
					}else{
							//删锟斤拷锟斤拷锟斤拷注锟斤拷
							for(DiffType diff:diffList){
								if(diff.getOldStartLine()==oldComment.getStartLine()&&
										(diff.getType().equals("COMMENT_DELETE")||diff.getType().equals("COMMENT_MOVE"))){
									oldMethodCommentList.remove(j);
									j--;
									m--;
									if(diff.getType().equals("COMMENT_MOVE")){
										oldMoveCommentList.add(oldComment);
									}
									break;
								}
							}
						}
					}
				
				
				if(newMoveCommentList.size()==oldMoveCommentList.size()){
					//锟斤拷锟狡讹拷锟斤拷注锟斤拷锟斤拷锟铰诧拷锟斤拷注锟斤拷锟叫憋拷
					for(CodeComment comment:newMoveCommentList){
						newMethodCommentList.add(comment);
					}
				
					for(CodeComment comment:oldMoveCommentList){
						oldMethodCommentList.add(comment);
					}
				}else{
					System.out.println("move comment error.");
				}
				
				if(newMethodCommentList.size()==oldMethodCommentList.size()){
					newCommentList.addAll(newMethodCommentList);
					oldCommentList.addAll(oldMethodCommentList);
				}else{
					System.out.println("project:"+clazz.getProject()+" commit:"+clazz.getCommitID()+" class:"+clazz.getClassName()+
							" method:"+newMethod.getName().getFullyQualifiedName()+" "+oldMethod.getName().getFullyQualifiedName()+" block comment size not equal.");
					System.out.println("new:"+newMethodCommentList.size()+" old:"+oldMethodCommentList.size());
					
					isError = true;
				}
				
			}
			
			if(isError){
				errorClassNum++;
			}

			// 锟斤拷锟斤拷注锟酵的凤拷围
			int[] newLevels = CommentScopeTool.computeCommentLevels(newStatementList, newCommentList, newUnit);
			for (int i = 0, n = newCommentList.size(); i < n; i++) {
				CodeComment newComment = newCommentList.get(i);
				int methodEndLine = -1;
				for (MethodDeclaration method : newMethodList) {
					if (newComment.getStartLine() > newUnit.getLineNumber(method.getStartPosition())
							&& newComment.getStartLine() < newUnit
									.getLineNumber(method.getStartPosition() + method.getLength() - 1)) {
						methodEndLine = newUnit.getLineNumber(method.getStartPosition() + method.getLength() - 1);
						break;
					}
				}
				newComment.setScopeEndLine(CommentScopeTool.computeCommentScope(newComment, i, newCommentList,
						newStatementList, newUnit,  methodEndLine));
			}
			int[] oldLevels = CommentScopeTool.computeCommentLevels(oldStatementList, oldCommentList, oldUnit);
			for (int i = 0, n = oldCommentList.size(); i < n; i++) {
				CodeComment oldComment = oldCommentList.get(i);
				int methodEndLine = -1;
				for (MethodDeclaration method : oldMethodList) {
					if (oldComment.getStartLine() > oldUnit.getLineNumber(method.getStartPosition())
							&& oldComment.getStartLine() < oldUnit
									.getLineNumber(method.getStartPosition() + method.getLength() - 1)) {
						methodEndLine = newUnit.getLineNumber(method.getStartPosition() + method.getLength() - 1);
						break;
					}
				}
				oldComment.setScopeEndLine(CommentScopeTool.computeCommentScope(oldComment, i, oldCommentList,
						oldStatementList, oldUnit,  methodEndLine));
			}

			for (int i = 0, n = newCommentList.size(); i < n; i++) {
				CodeComment newComment = newCommentList.get(i);
				CodeComment oldComment = oldCommentList.get(i);

				List<DiffType> commentDiffList = new ArrayList<DiffType>();
				for (DiffType diff : diffList) {
					if ((diff.getNewStartLine() > newComment.getStartLine()
							&& diff.getNewEndLine() <= newComment.getScopeEndLine()
							&& diff.getOldStartLine() > oldComment.getStartLine()
							&& diff.getOldEndLine() <= oldComment.getScopeEndLine())
							|| (diff.getNewStartLine() > newComment.getStartLine()
									&& diff.getNewEndLine() <= newComment.getScopeEndLine()
									&& diff.getOldStartLine() == 0 && diff.getOldEndLine() == 0)
							|| (diff.getOldStartLine() > oldComment.getStartLine()
									&& diff.getOldEndLine() <= oldComment.getScopeEndLine()
									&& diff.getNewStartLine() == 0 && diff.getNewEndLine() == 0)) {
						commentDiffList.add(diff);
					}
				}

				if (commentDiffList.isEmpty()) {
					continue;
				}

				//锟斤拷锟斤拷CommentEntry锟斤拷锟襟，诧拷锟斤拷锟诫到锟斤拷锟捷匡拷锟叫碉拷comment2锟斤拷锟斤拷
				CommentEntry comment = new CommentEntry();
				comment.setProject(project);
				comment.setCommitID(clazz.getCommitID());
				comment.setClassName(clazz.getClassName());
				comment.setType(newComment.getType().toString());
				comment.setNew_comment_startLine(newComment.getStartLine());
				comment.setNew_comment_endLine(newComment.getEndLine());
				comment.setNew_scope_startLine(newComment.getStartLine());
				comment.setNew_scope_endLine(newComment.getScopeEndLine());
				comment.setOld_comment_startLine(oldComment.getStartLine());
				comment.setOld_comment_endLine(oldComment.getEndLine());
				comment.setOld_scope_startLine(oldComment.getStartLine());
				comment.setOld_scope_endLine(oldComment.getScopeEndLine());
				comment.setNewCode(getCodeList(newCode, newComment.getEndLine() + 1, newComment.getScopeEndLine()));
				comment.setOldCode(getCodeList(oldCode, oldComment.getEndLine() + 1, oldComment.getScopeEndLine()));
				comment.setNewComment(getCodeList(newCode, newComment.getStartLine(), newComment.getEndLine()));
				comment.setOldComment(getCodeList(oldCode, oldComment.getStartLine(), oldComment.getEndLine()));
				comment.setDiffList(commentDiffList);
				comment.setChange(
						StringTools.computeSimilarity(comment.getNewComment(), comment.getOldComment()) < 0.95);

				commentRepository.insert(comment);
			}

		}
		System.out.println("error class num :" + errorClassNum);

	}

	private static void removeMethod(List<MethodDeclaration> methodList,
			CompilationUnit unit, int startLine) {

		for (int i = 0, n = methodList.size(); i < n; i++) {
			MethodDeclaration method = methodList.get(i);
			int methodStartLine = unit.getLineNumber(method.getStartPosition());
			if (methodStartLine == startLine) {
				methodList.remove(i);
				break;
			}
		}

	}

	private static List<String> getCodeList(List<Line> newCode, int startLine, int endLine) {

		List<String> codeList = new ArrayList<String>();
		if (startLine > endLine) {
			return codeList;
		}
		for (int i = startLine - 1; i < endLine && i < newCode.size(); i++) {
			codeList.add(newCode.get(i).getLine());
		}
		return codeList;
	}

	public static void main(String[] args) {

		String[] projects = new String[] { "jhotdraw"};
		DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		for (String project : projects) {

			BlockExtractor.extract(project);
			System.out.println(project + " is done.");
			System.out.println(sdf2.format(new Date()));
		}
	}

}
