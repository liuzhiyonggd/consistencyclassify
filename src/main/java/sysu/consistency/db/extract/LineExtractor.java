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

public class LineExtractor {
	
	private static int commentId = 1;

	@SuppressWarnings("unchecked")
	public static void extract() {
		int errorClassNum = 0;

		ClassMessageRepository classRepository = RepositoryFactory.getClassRepository();
		CommentRepository commentRepository = RepositoryFactory.getCommentRepository();

		int count = 0;
		for (int classID=1;classID<172438;classID++) {
			count++;
			if(count%10000==0) {
				System.out.println(count + " is done.");
			}
			ClassMessage clazz = classRepository.findASingleByClassID(classID);
			if(clazz==null) {
				continue;
			}
			// 只关注change类型的类变化
			if (clazz.getType().equals("new") || clazz.getType().equals("delete")) {
				continue;
			}

			// 如果无代码层次的变化，则跳过
			if (clazz.getDiffList().isEmpty()) {
				continue;
			}

			

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

			// 获取新旧类的AST解析器
			ASTParser parser = ASTParser.newParser(AST.JLS8);
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
			
			List<MethodDeclaration> tmp_newMethodList = new ArrayList<MethodDeclaration>();
			List<MethodDeclaration> tmp_oldMethodList = new ArrayList<MethodDeclaration>();
			for(MethodDeclaration newMethod:newMethodList) {
				for(MethodDeclaration oldMethod:oldMethodList) {
					if(compareMethod(newMethod, oldMethod)) {
						tmp_newMethodList.add(newMethod);
						tmp_oldMethodList.add(oldMethod);
						break;
					}
				}
			}
			newMethodList = tmp_newMethodList;
			oldMethodList = tmp_oldMethodList;
			

			
			// 去除新增的和删除的方法
//			for (DiffType diff : diffList) {
//				if (diff.getType().equals("ADDITIONAL_FUNCTIONALITY")) {
//					removeMethod(newMethodList, newUnit, diff.getNewStartLine());
//				} else if (diff.getType().equals("REMOVED_FUNCTIONALITY")) {
//					removeMethod(oldMethodList, oldUnit, diff.getOldStartLine());
//				}
//			}

			List<CodeComment> newCommentList_temp = clazz.getNewComment();
			List<CodeComment> oldCommentList_temp = clazz.getOldComment();

			// 去除与代码在同一行的注释
			List<Token> newTokenList = clazz.getNewTokenList();
			for (int i = 0, n = newCommentList_temp.size(); i < n; i++) {
				CodeComment newComment = newCommentList_temp.get(i);
				for (Token token : newTokenList) {
					if (token.getStartLine() == newComment.getStartLine()
							&& token.getEndLine() == newComment.getEndLine()) {
						newCommentList_temp.remove(i);
						i--;
						n--;
						break;
					}
				}
			}

			List<Token> oldTokenList = clazz.getOldTokenList();
			for (int i = 0, n = oldCommentList_temp.size(); i < n; i++) {
				CodeComment oldComment = oldCommentList_temp.get(i);
				for (Token token : oldTokenList) {
					if (token.getStartLine() == oldComment.getStartLine()
							&& token.getEndLine() == oldComment.getEndLine()) {
						oldCommentList_temp.remove(i);
						i--;
						n--;
						break;
					}
				}
			}

			List<CodeComment> newCommentList = new ArrayList<CodeComment>();
			List<CodeComment> oldCommentList = new ArrayList<CodeComment>();

			if (newMethodList.size() != oldMethodList.size()) {
				
				System.out.println("project:" + clazz.getProject() + " commit:" + clazz.getCommitID() + " class:"
						+ clazz.getClassName() + " method list not equal. old size:"+oldMethodList.size()+" new size:"+newMethodList.size());
				
				for(MethodDeclaration method:newMethodList) {
					System.out.println(method.getName().toString());
				}
				System.out.println("-------------------");
				for(MethodDeclaration method:oldMethodList) {
					System.out.println(method.getName().toString());
				}
				errorClassNum++;
				continue;
			}

			// 以方法为划分界限，获取方法内部的注释
			for (int i = 0, n = newMethodList.size(); i < n; i++) {
				MethodDeclaration newMethod = newMethodList.get(i);
				MethodDeclaration oldMethod = oldMethodList.get(i);

				int newMethodStartLine = newUnit.getLineNumber(newMethod.getStartPosition());
				int newMethodEndLine = newUnit.getLineNumber(newMethod.getStartPosition() + newMethod.getLength() - 1);

				int oldMethodStartLine = oldUnit.getLineNumber(oldMethod.getStartPosition());
				int oldMethodEndLine = oldUnit.getLineNumber(oldMethod.getStartPosition() + oldMethod.getLength() - 1);

				List<CodeComment> newMethodCommentList = new ArrayList<CodeComment>();
				List<CodeComment> oldMethodCommentList = new ArrayList<CodeComment>();

				List<CodeComment> newMoveCommentList = new ArrayList<CodeComment>();
				List<CodeComment> oldMoveCommentList = new ArrayList<CodeComment>();

				for (CodeComment newComment : newCommentList_temp) {
					if (newComment.getStartLine() > newMethodStartLine && newComment.getEndLine() <= newMethodEndLine) {
						newMethodCommentList.add(newComment);
					}
				}

				for (CodeComment oldComment : oldCommentList_temp) {
					if (oldComment.getStartLine() > oldMethodStartLine && oldComment.getEndLine() <= oldMethodEndLine) {
						oldMethodCommentList.add(oldComment);
					}
				}

				// 合并多行注释
				for (int j = 0, m = newMethodCommentList.size(); j < m - 1; j++) {
					CodeComment currentComment = newMethodCommentList.get(j);
					CodeComment nextComment = newMethodCommentList.get(j + 1);
					if (currentComment.getEndLine() + 1 == nextComment.getStartLine()) {
						nextComment.setStartLine(currentComment.getStartLine());
						nextComment.setScopeStartLine(nextComment.getStartLine());
						newMethodCommentList.remove(j);
						j--;
						m--;
					}
				}
				for (int j = 0, m = oldMethodCommentList.size(); j < m - 1; j++) {
					CodeComment currentComment = oldMethodCommentList.get(j);
					CodeComment nextComment = oldMethodCommentList.get(j + 1);
					if (currentComment.getEndLine() + 1 == nextComment.getStartLine()) {
						nextComment.setStartLine(currentComment.getStartLine());
						nextComment.setScopeStartLine(nextComment.getStartLine());
						oldMethodCommentList.remove(j);
						j--;
						m--;
					}
				}

				for (int j = 0, m = newMethodCommentList.size(); j < m; j++) {
					CodeComment newComment = newMethodCommentList.get(j);
					List<String> newCommentMessage = getCodeList(newCode, newComment.getStartLine(),
							newComment.getEndLine());

					// 去除代码注释
					if (StringTools.isCode(newCommentMessage)) {
						newMethodCommentList.remove(j);
						j--;
						m--;
					} else {

						// 在新版本中去除新增的和移动的注释
						for (DiffType diff : diffList) {
							if (diff.getNewStartLine() == newComment.getStartLine()
									&& diff.getNewEndLine() == newComment.getEndLine()
									&& (diff.getType().equals("COMMENT_INSERT")
											|| diff.getType().equals("COMMENT_MOVE"))) {
								newMethodCommentList.remove(j);
								j--;
								m--;
								if (diff.getType().equals("COMMENT_MOVE")) {
									newMoveCommentList.add(newComment);
								}
								break;
							}
						}
					}
				}

				// 与新版本的处理类似
				for (int j = 0, m = oldMethodCommentList.size(); j < m; j++) {
					CodeComment oldComment = oldMethodCommentList.get(j);
					List<String> oldCommentMessage = getCodeList(oldCode, oldComment.getStartLine(),
							oldComment.getEndLine());

					// 去除代码注释
					if (StringTools.isCode(oldCommentMessage)) {
						oldMethodCommentList.remove(j);
						j--;
						m--;
					} else {

						// 在旧版中去除删除的和移动的注释
						for (DiffType diff : diffList) {
							if (diff.getOldStartLine() == oldComment.getStartLine()
									&& diff.getOldEndLine() == oldComment.getEndLine()
									&& (diff.getType().equals("COMMENT_DELETE")
											|| diff.getType().equals("COMMENT_MOVE"))) {
								oldMethodCommentList.remove(j);
								j--;
								m--;
								if (diff.getType().equals("COMMENT_MOVE")) {
									oldMoveCommentList.add(oldComment);
								}
								break;
							}
						}
					}
				}

				if (newMoveCommentList.size() == oldMoveCommentList.size()) {
					// 将移动的注释加到注释列表的尾部
					for (CodeComment comment : newMoveCommentList) {
						newMethodCommentList.add(comment);
					}

					for (CodeComment comment : oldMoveCommentList) {
						oldMethodCommentList.add(comment);
					}
				} else {
					System.out.println("move comment error.");
				}

				if (newMethodCommentList.size() == oldMethodCommentList.size()) {
					newCommentList.addAll(newMethodCommentList);
					oldCommentList.addAll(oldMethodCommentList);
				} else {
					System.out.println("project:" + clazz.getProject() + " commit:" + clazz.getCommitID() + " class:"
							+ clazz.getClassName() + " method:" + newMethod.getName().getFullyQualifiedName() + " "
							+ oldMethod.getName().getFullyQualifiedName() + " comment size not equal.");
					System.out.println("new:" + newMethodCommentList.size() + " old:" + oldMethodCommentList.size());
				}

			}


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
						newStatementList, newUnit, methodEndLine));
			}
			for (int i = 0, n = oldCommentList.size(); i < n; i++) {
				CodeComment oldComment = oldCommentList.get(i);
				int methodEndLine = -1;
				for (MethodDeclaration method : oldMethodList) {
					if (oldComment.getStartLine() > oldUnit.getLineNumber(method.getStartPosition())
							&& oldComment.getStartLine() < oldUnit
									.getLineNumber(method.getStartPosition() + method.getLength() - 1)) {
						methodEndLine = oldUnit.getLineNumber(method.getStartPosition() + method.getLength() - 1);
						break;
					}
				}
				oldComment.setScopeEndLine(CommentScopeTool.computeCommentScope(oldComment, i, oldCommentList,
						oldStatementList, oldUnit, methodEndLine));
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

				// 将注释插入到数据库中
				CommentEntry comment = new CommentEntry();
				comment.setCommentID(commentId);
				commentId ++;
				
				
				comment.setProject(clazz.getProject());
				comment.setCommitID(clazz.getCommitID());
				comment.setClassName(clazz.getClassName());
				comment.setClassID(clazz.getClassID());
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
				System.out.println("insert.");
			}

		}
		System.out.println("error class num :" + errorClassNum);

	}

	private static void removeMethod(List<MethodDeclaration> methodList, CompilationUnit unit, int startLine) {

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
	
	private static boolean compareMethod(MethodDeclaration method1,MethodDeclaration method2) {
		if(!method1.getName().toString().equals(method2.getName().toString())) {
			return false;
		}
		List parameters1 = method1.parameters();
		List parameters2 = method2.parameters();
		
		if(parameters1.size()!=parameters2.size()) {
			return false;
		}
		
		for(int i=0;i<parameters1.size();i++) {
			String[] tmp = parameters1.get(i).toString().split(" ");
			String type1 = tmp[0];
			tmp = parameters2.get(i).toString().split(" ");
			String type2 = tmp[0];
			if(!type1.equals(type2)) {
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) {

//		String[] projects = new String[] { "jgit","hibernate","spring","struts","commons-csv","commons-io","elasticsearch","strman-java","tablesaw" };
		LineExtractor.extract();
	}

}
