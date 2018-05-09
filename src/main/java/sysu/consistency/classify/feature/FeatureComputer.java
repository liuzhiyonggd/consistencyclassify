package sysu.consistency.classify.feature;

import static org.hamcrest.CoreMatchers.containsString;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.WhileStatement;

import sysu.consistency.db.bean.ClassMessage;
import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.bean.DiffType;
import sysu.consistency.db.bean.Line;
import sysu.consistency.db.bean.Token;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.ClassMessageRepository;
import sysu.consistency.db.repository.CommentRepository;

public class FeatureComputer {
	private static CommentRepository commentRepo = RepositoryFactory.getCommentRepository();
	private static ClassMessageRepository classRepo = RepositoryFactory.getClassRepository();
	private static Map<Integer, String> nodeTypeMap;

	public static void computeClassAttributeChangeFeature() throws IOException {
		List<String> idLines = FileUtils.readLines(new File("file/features/comment_id.txt"), "UTF-8");

		List<String> output = new ArrayList<String>();
		for (String str : idLines) {
			int commentID = Integer.parseInt(str);
			CommentEntry comment = commentRepo.findASingleByCommentID(commentID);
			ClassMessage clazz = classRepo.findASingleByProjectAndCommitIDAndClassName(comment.getProject(),
					comment.getCommitID(), comment.getClassName());
			if (clazz == null) {
				System.out.println(commentID + "");
			}
			List<DiffType> diffList = clazz.getDiffList();
			String temp = commentID + ",";
			for (DiffType diff : diffList) {

				if (diff.getType().equals("ADDING_ATTRIBUTE_MODIFIABILITY")
						|| diff.getType().equals("REMOVING_ATTRIBUTE_MODIFIABILITY")
						|| diff.getType().equals("ATTRIBUTE_TYPE_CHANGE")
						|| diff.getType().equals("ATTRIBUTE_RENAMING")) {
					temp += diff.getType() + ",";
					for (String str2 : diff.getOldKeywordList()) {
						temp += str2 + " ";
					}
					temp += ",";
					for (String str2 : diff.getNewKeywordList()) {
						temp += str2 + " ";
					}
					temp += ",";
				}
			}
			output.add(temp);
		}
		FileUtils.writeLines(new File("file/features/class_attribute_change.csv"), output);
	}

	public static void computeClassRelationChangeFeature() throws IOException {
		List<String> classChangeList = FileUtils.readLines(new File("file/features/class_attribute_change.csv"),
				"UTF-8");
		List<String> output = new ArrayList<String>();
		for (String str : classChangeList) {
			str = str.trim();
			String[] temps = str.split(",");
			if (temps.length > 2) {
				int commentID = Integer.parseInt(temps[0]);
				List<String> attributeNameList = new ArrayList<String>();
				for (int i = 1; i < temps.length; i++) {
					String[] temps2 = temps[i].split(" ");
					for (String str2 : temps2) {
						String attributeName = str2;
						if (attributeName.length() > 0) {
							char firstChar = attributeName.charAt(0);
							if (firstChar < 'A' || firstChar > 'Z') {
								attributeNameList.add(attributeName);
							}
						}
					}
				}

				if (attributeNameList.size() > 0) {
					CommentEntry comment = commentRepo.findASingleByCommentID(commentID);
					List<String> relationVariableList = new ArrayList<String>();
					List<Token> newTokenList = comment.getNewToken();
					for (Token token : newTokenList) {
						String keyword = token.getKeyword();
						if (keyword != null) {
							if (attributeNameList.contains(keyword)) {
								relationVariableList.add(keyword);
							}
						}
					}
					List<Token> oldTokenList = comment.getOldToken();
					for (Token token : oldTokenList) {
						String keyword = token.getKeyword();
						if (keyword != null) {
							if (attributeNameList.contains(keyword)) {
								relationVariableList.add(keyword);
							}
						}
					}

					if (relationVariableList.size() > 0) {
						String temp = commentID + ",";
						for (String var : relationVariableList) {
							temp += var + " ";
						}
						output.add(temp);
					}
				}
			}
		}
		FileUtils.writeLines(new File("file/features/class_attribute_relation.csv"), output);
	}

	public static void computeMethodParameterReturnChangeFeature() throws IOException {
		List<String> idLines = FileUtils.readLines(new File("file/features/comment_id.txt"), "UTF-8");

		List<String> output = new ArrayList<String>();
		int count = 0;
		for (String str : idLines) {
			count++;
			if (count % 1000 == 0) {
				System.out.println(count + " is done.");
			}
			int commentID = Integer.parseInt(str);
			CommentEntry comment = commentRepo.findASingleByCommentID(commentID);
			ClassMessage clazz = classRepo.findASingleByProjectAndCommitIDAndClassName(comment.getProject(),
					comment.getCommitID(), comment.getClassName());
			if (clazz == null) {
				System.out.println(commentID + "");
			}
			List<DiffType> diffList = clazz.getDiffList();
			String temp = commentID + ",";
			for (DiffType diff : diffList) {

				if (diff.getType().equals("PARAMETER_ORDERING_CHANGE") || diff.getType().equals("PARAMETER_RENAMING")
						|| diff.getType().equals("PARAMETER_TYPE_CHANGE") || diff.getType().equals("PARAMETER_DELETE")
						|| diff.getType().equals("PARAMETER_INSERT") || diff.getType().equals("RETURN_TYPE_INSERT")
						|| diff.getType().equals("RETURN_TYPE_CHANGE") || diff.getType().equals("RETURN_TYPE_DELETE")) {
					temp += diff.getType() + ",";
					for (String str2 : diff.getOldKeywordList()) {
						temp += str2 + " ";
					}
					temp += ",";
					for (String str2 : diff.getNewKeywordList()) {
						temp += str2 + " ";
					}
					temp += ",";
				}
			}
			output.add(temp);
		}
		FileUtils.writeLines(new File("file/features/method_parameter_return_change.csv"), output);
	}

	public static void computeMethodRelationChangeFeature() throws IOException {
		List<String> methodChangeList = FileUtils
				.readLines(new File("file/features/method_parameter_return_change.csv"), "UTF-8");
		List<String> output = new ArrayList<String>();
		for (String str : methodChangeList) {
			str = str.trim();
			String[] temps = str.split(",");
			if (temps.length > 2) {
				int commentID = Integer.parseInt(temps[0]);
				boolean returnTypeChange = false;
				List<String> parameterNameList = new ArrayList<String>();
				for (int i = 1; i < temps.length; i++) {
					String[] temps2 = temps[i].split(" ");
					for (String str2 : temps2) {
						String attributeName = str2;
						if (attributeName.length() > 0) {
							char firstChar = attributeName.charAt(0);
							if (firstChar < 'A' || firstChar > 'Z') {
								parameterNameList.add(attributeName);
							}
						}

						if (str2.equals("RETRUN_TYPE_INSERT") || str2.equals("RETURN_TYPE_CHANGE")
								|| str2.equals("RETURN_TYPE_DELETE")) {
							returnTypeChange = true;
						}
					}
				}

				if (parameterNameList.size() > 0) {
					CommentEntry comment = commentRepo.findASingleByCommentID(commentID);
					List<String> relationVariableList = new ArrayList<String>();
					List<Token> newTokenList = comment.getNewToken();
					for (Token token : newTokenList) {
						String keyword = token.getKeyword();
						if (keyword != null) {
							if (parameterNameList.contains(keyword)) {
								relationVariableList.add(keyword);
							}
							if (returnTypeChange && keyword.equals("return")) {
								relationVariableList.add(keyword);
							}
						}
					}
					List<Token> oldTokenList = comment.getOldToken();
					for (Token token : oldTokenList) {
						String keyword = token.getKeyword();
						if (keyword != null) {
							if (parameterNameList.contains(keyword)) {
								relationVariableList.add(keyword);
							}
							if (returnTypeChange && keyword.equals("return")) {
								relationVariableList.add(keyword);
							}
						}
					}

					if (relationVariableList.size() > 0) {
						String temp = commentID + ",";
						for (String var : relationVariableList) {
							temp += var + " ";
						}
						output.add(temp);
					}
				}
			}
		}
		FileUtils.writeLines(new File("file/features/method_parameter_return_relation.csv"), output);
	}

	public static void computeStatements() throws IOException {
		List<String> idLines = FileUtils.readLines(new File("file/features/comment_id.txt"), "UTF-8");
		List<String> output = new ArrayList<String>();
		int count = 0;
		for (String str : idLines) {
			count++;
			if (count % 1000 == 0) {
				System.out.println(count + " is done.");
			}

			int commentID = Integer.parseInt(str);
			CommentEntry comment = commentRepo.findASingleByCommentID(commentID);
			List<Token> tokenList = comment.getOldToken();
			String tokenString = "";
			int statements = 0;
			for (Token token : tokenList) {
				if (token.getTokenName().endsWith("Invocation") || token.getTokenName().endsWith("Declaration")
						|| token.getTokenName().endsWith("Statement")) {
					tokenString += token.getTokenName() + " ";
					statements++;
				}
			}
			output.add(str + "," + statements + "," + tokenString);
		}
		FileUtils.writeLines(new File("file/features/statementcount.csv"), output);
	}

	public static void computeChangeStatements() throws IOException {
		List<String> fileLines = FileUtils.readLines(new File("file/features/changecodeline.csv"), "UTF-8");
		List<String> output = new ArrayList<String>();
		for (String line : fileLines) {
			String[] temps = line.split(",");

			String commentID = temps[0];
			String addString = temps[1];
			String deleteString = temps[2];
			String changeString = temps[3];

			int addStatements = addString.split(" ").length - 1;
			int deleteStatements = deleteString.split(" ").length - 1;
			int changeStatements = changeString.split(" ").length - 1;

			output.add(commentID + "," + addStatements + "," + deleteStatements + "," + changeStatements);
		}
		FileUtils.writeLines(new File("file/features/changestatementcount.csv"), output);
	}

	public static void computeCodeLines() throws IOException {
		List<String> idLines = FileUtils.readLines(new File("file/features/comment_id.txt"), "UTF-8");
		List<String> output = new ArrayList<String>();
		int count = 0;
		for (String str : idLines) {
			count++;
			if (count % 1000 == 0) {
				System.out.println(count + " is done.");
			}
			int commentID = Integer.parseInt(str);
			CommentEntry comment = commentRepo.findASingleByCommentID(commentID);
			int totalLine = comment.getOld_comment_endLine() - comment.getOld_comment_startLine()
					+ comment.getOldCode().size() + 1;
			int commentLine = comment.getOldComment().size();
			int codeLine = totalLine - commentLine;

			output.add(str + "," + totalLine + "," + commentLine + "," + codeLine);
		}
		FileUtils.writeLines(new File("file/features/codeline.csv"), output);
	}

	public static void computeChangeCodeLines() throws IOException {
		List<String> idLines = FileUtils.readLines(new File("file/features/comment_id.txt"), "UTF-8");
		List<String> output = new ArrayList<String>();
		int count = 0;
		for (String str : idLines) {
			count++;
			if (count % 1000 == 0) {
				System.out.println(count + " is done.");
			}
			int commentID = Integer.parseInt(str);
			CommentEntry comment = commentRepo.findASingleByCommentID(commentID);
			List<DiffType> diffList = comment.getDiffList();

			int changeLine = 0;
			int deleteLine = 0;
			int addLine = 0;

			String changeString = "";
			String deleteString = "";
			String addString = "";
			for (DiffType diff : diffList) {
				if (diff.getType().equals("STATEMENT_INSERT")) {
					addString += diff.getType() + ":" + diff.getNewStartLine() + "-" + diff.getNewEndLine() + " ";
					addLine += diff.getNewEndLine() - diff.getNewStartLine() + 1;
					continue;
				}
				if (diff.getType().equals("STATEMENT_DELETE")) {
					deleteString += diff.getType() + ":" + diff.getOldStartLine() + "-" + diff.getOldEndLine() + " ";
					deleteLine += diff.getOldEndLine() - diff.getOldStartLine() + 1;
					continue;
				}
				if (diff.getType().equals("STATEMENT_UPDATE")) {
					changeString += diff.getType() + ":" + diff.getOldStartLine() + "-" + diff.getOldEndLine() + "-"
							+ diff.getNewStartLine() + "-" + diff.getNewEndLine() + " ";
					changeLine += diff.getOldEndLine() - diff.getOldStartLine() + 1;
					continue;
				}

				if (diff.getType().equals("CONDITION_EXPRESSION_CHANGE")) {
					changeString += diff.getType() + ":" + diff.getOldStartLine() + "-" + diff.getOldEndLine() + "-"
							+ diff.getNewStartLine() + "-" + diff.getNewEndLine() + " ";
					changeLine++;
					continue;
				}
				if (diff.getType().equals("ALTERNATIVE_PART_INSERT")) {
					addString += diff.getType() + ":" + diff.getNewStartLine() + "-" + diff.getNewEndLine() + " ";
					addLine += diff.getNewEndLine() - diff.getNewStartLine() + 1;
					continue;
				}
				if (diff.getType().equals("ALTERNATIVE_PART_DELETE")) {
					deleteString += diff.getType() + ":" + diff.getOldStartLine() + "-" + diff.getOldEndLine() + " ";
					deleteLine += diff.getOldEndLine() - diff.getOldStartLine() + 1;
					continue;
				}
			}
			output.add(str + "," + addLine + " " + addString + "," + deleteLine + " " + deleteString + "," + changeLine
					+ " " + changeString);

		}
		FileUtils.writeLines(new File("file/features/changecodeline.csv"), output);
	}

	private static List<Block> getBlock(Statement statement, List<Statement> statementList) {

		List<Block> blockList = new ArrayList<Block>();
		if (statement instanceof IfStatement) {
			IfStatement ifStatement = (IfStatement) statement;
			Statement thenStatement = ifStatement.getThenStatement();
			if (thenStatement instanceof Block) {
				Block block = (Block) thenStatement;
				blockList.add(block);
			} else if (thenStatement != null) {
				statementList.add(thenStatement);
			}
			Statement elseStatement = ifStatement.getElseStatement();

			if (elseStatement instanceof Block) {
				Block block = (Block) elseStatement;
				blockList.add(block);
			}
			if (elseStatement != null) {
				statementList.add(elseStatement);
			}

			return blockList;
		}

		if (statement instanceof WhileStatement) {
			WhileStatement whileStatement = (WhileStatement) statement;
			Statement whileBody = whileStatement.getBody();
			if (whileBody instanceof Block) {
				Block block = (Block) whileBody;
				blockList.add(block);
			}
			return blockList;
		}

		if (statement instanceof ForStatement) {
			ForStatement forStatement = (ForStatement) statement;
			Statement forBody = forStatement.getBody();
			if (forBody instanceof Block) {
				Block block = (Block) forBody;
				blockList.add(block);
			}
			return blockList;
		}

		if (statement instanceof EnhancedForStatement) {
			EnhancedForStatement forStatement = (EnhancedForStatement) statement;
			Statement forBody = forStatement.getBody();
			if (forBody instanceof Block) {
				Block block = (Block) forBody;
				blockList.add(block);
			}
			return blockList;
		}

		if (statement instanceof TryStatement) {
			TryStatement tryStatement = (TryStatement) statement;
			Statement tryBody = tryStatement.getBody();
			if (tryBody instanceof Block) {
				Block block = (Block) tryBody;
				blockList.add(block);
			}
			List<CatchClause> catchClauses = tryStatement.catchClauses();
			if (catchClauses != null && catchClauses.size() > 0) {
				for (CatchClause clause : catchClauses) {
					Statement sta = clause.getBody();
					if (sta instanceof Block) {
						Block block = (Block) sta;
						blockList.add(block);
					}
				}
			}

			Statement finallyBody = tryStatement.getFinally();
			if (finallyBody instanceof Block) {
				Block block = (Block) finallyBody;
				blockList.add(block);
			}
			if (finallyBody != null) {
				statementList.add(finallyBody);
			}
			return blockList;
		}

		if (statement instanceof SwitchStatement) {
			SwitchStatement switchStatement = (SwitchStatement) statement;
			List<Statement> switchStatementList = switchStatement.statements();
			statementList.addAll(switchStatementList);
			for (Statement sta : switchStatementList) {
				List<Block> bList = getBlock(sta, statementList);
				blockList.addAll(bList);
			}
			return blockList;
		}

		if (statement instanceof Block) {
			blockList.add((Block) statement);
			return blockList;
		}
		return blockList;
	}

	private static Map<Integer, String> getNodeTypeMap() {
		if (nodeTypeMap != null) {
			return nodeTypeMap;
		}

		nodeTypeMap = new HashMap<Integer, String>();
		nodeTypeMap.put(Statement.ANNOTATION_TYPE_DECLARATION, "ANNOTATION_TYPE_DECLARATION");
		nodeTypeMap.put(Statement.ANNOTATION_TYPE_MEMBER_DECLARATION, "ANNOTATION_TYPE_MEMBER_DECLARATION");
		nodeTypeMap.put(Statement.ANONYMOUS_CLASS_DECLARATION, "ANONYMOUS_CLASS_DECLARATION");
		nodeTypeMap.put(Statement.ARRAY_ACCESS, "ARRAY_ACCESS");
		nodeTypeMap.put(Statement.ARRAY_CREATION, "ARRAY_CREATION");
		nodeTypeMap.put(Statement.ARRAY_INITIALIZER, "ARRAY_INITIALIZER");
		nodeTypeMap.put(Statement.ARRAY_TYPE, "ARRAY_TYPE");
		nodeTypeMap.put(Statement.ASSERT_STATEMENT, "ASSERT_STATEMENT");
		nodeTypeMap.put(Statement.ASSIGNMENT, "ASSIGNMENT");
		nodeTypeMap.put(Statement.BLOCK, "BLOCK");
		nodeTypeMap.put(Statement.BLOCK_COMMENT, "BLOCK_COMMENT");
		nodeTypeMap.put(Statement.BOOLEAN_LITERAL, "BOOLEAN_LITERAL");
		nodeTypeMap.put(Statement.BREAK_STATEMENT, "BREAK_STATEMENT");
		nodeTypeMap.put(Statement.CAST_EXPRESSION, "CAST_EXPRESSION");
		nodeTypeMap.put(Statement.CATCH_CLAUSE, "CATCH_CLAUSE");
		nodeTypeMap.put(Statement.CHARACTER_LITERAL, "CHARACTER_LITERAL");
		nodeTypeMap.put(Statement.CLASS_INSTANCE_CREATION, "CLASS_INSTANCE_CREATION");
		nodeTypeMap.put(Statement.COMPILATION_UNIT, "COMPILATION_UNIT");
		nodeTypeMap.put(Statement.CONDITIONAL_EXPRESSION, "CONDITIONAL_EXPRESSION");
		nodeTypeMap.put(Statement.CONSTRUCTOR_INVOCATION, "CONSTRUCTOR_INVOCATION");
		nodeTypeMap.put(Statement.CONTINUE_STATEMENT, "CONTINUE_STATEMENT");
		nodeTypeMap.put(Statement.CREATION_REFERENCE, "CREATION_REFERENCE");
		nodeTypeMap.put(Statement.DIMENSION, "DIMENSION");
		nodeTypeMap.put(Statement.DO_STATEMENT, "DO_STATEMENT");
		nodeTypeMap.put(Statement.EMPTY_STATEMENT, "EMPTY_STATEMENT");
		nodeTypeMap.put(Statement.ENHANCED_FOR_STATEMENT, "ENHANCED_FOR_STATEMENT");
		nodeTypeMap.put(Statement.ENUM_CONSTANT_DECLARATION, "ENUM_CONSTANT_DECLARATION");
		nodeTypeMap.put(Statement.ENUM_DECLARATION, "ENUM_DECLARATION");
		nodeTypeMap.put(Statement.EXPRESSION_METHOD_REFERENCE, "EXPRESSION_METHOD_REFERENCE");
		nodeTypeMap.put(Statement.EXPRESSION_STATEMENT, "EXPRESSION_STATEMENT");
		nodeTypeMap.put(Statement.FIELD_ACCESS, "FIELD_ACCESS");
		nodeTypeMap.put(Statement.FIELD_DECLARATION, "FIELD_DECLARATION");
		nodeTypeMap.put(Statement.FOR_STATEMENT, "FOR_STATEMENT");
		nodeTypeMap.put(Statement.IF_STATEMENT, "IF_STATEMENT");
		nodeTypeMap.put(Statement.IMPORT_DECLARATION, "IMPORT_DECLARATION");
		nodeTypeMap.put(Statement.INFIX_EXPRESSION, "INFIX_EXPRESSION");
		nodeTypeMap.put(Statement.INITIALIZER, "INITIALIZER");
		nodeTypeMap.put(Statement.INSTANCEOF_EXPRESSION, "INSTANCEOF_EXPRESSION");
		nodeTypeMap.put(Statement.INTERSECTION_TYPE, "INTERSECTION_TYPE");
		nodeTypeMap.put(Statement.JAVADOC, "JAVADOC");
		nodeTypeMap.put(Statement.LABELED_STATEMENT, "LABELED_STATEMENT");
		nodeTypeMap.put(Statement.LAMBDA_EXPRESSION, "LAMBDA_EXPRESSION");
		nodeTypeMap.put(Statement.LINE_COMMENT, "LINE_COMMENT");
		nodeTypeMap.put(Statement.MALFORMED, "MALFORMED");
		nodeTypeMap.put(Statement.MARKER_ANNOTATION, "MARKER_ANNOTATION");
		nodeTypeMap.put(Statement.MEMBER_REF, "MEMBER_REF");
		nodeTypeMap.put(Statement.MEMBER_VALUE_PAIR, "MEMBER_VALUE_PAIR");
		nodeTypeMap.put(Statement.METHOD_DECLARATION, "METHOD_DECLARATION");
		nodeTypeMap.put(Statement.METHOD_INVOCATION, "METHOD_INVOCATION");
		nodeTypeMap.put(Statement.METHOD_REF, "METHOD_REF");
		nodeTypeMap.put(Statement.METHOD_REF_PARAMETER, "METHOD_REF_PARAMETER");
		nodeTypeMap.put(Statement.MODIFIER, "MODIFIER");
		nodeTypeMap.put(Statement.NAME_QUALIFIED_TYPE, "NAME_QUALIFIED_TYPE");
		nodeTypeMap.put(Statement.NORMAL_ANNOTATION, "NORMAL_ANNOTATION");
		nodeTypeMap.put(Statement.NULL_LITERAL, "NULL_LITERAL");
		nodeTypeMap.put(Statement.NUMBER_LITERAL, "NUMBER_LITERAL");
		nodeTypeMap.put(Statement.ORIGINAL, "ORIGINAL");
		nodeTypeMap.put(Statement.PACKAGE_DECLARATION, "PACKAGE_DECLARATION");
		nodeTypeMap.put(Statement.PARAMETERIZED_TYPE, "PARAMETERIZED_TYPE");
		nodeTypeMap.put(Statement.PARENTHESIZED_EXPRESSION, "PARENTHESIZED_EXPRESSION");
		nodeTypeMap.put(Statement.POSTFIX_EXPRESSION, "POSTFIX_EXPRESSION");
		nodeTypeMap.put(Statement.PREFIX_EXPRESSION, "PREFIX_EXPRESSION");
		nodeTypeMap.put(Statement.PRIMITIVE_TYPE, "PRIMITIVE_TYPE");
		nodeTypeMap.put(Statement.PROTECT, "PROTECT");
		nodeTypeMap.put(Statement.QUALIFIED_NAME, "QUALIFIED_NAME");
		nodeTypeMap.put(Statement.QUALIFIED_TYPE, "QUALIFIED_TYPE");
		nodeTypeMap.put(Statement.RECOVERED, "RECOVERED");
		nodeTypeMap.put(Statement.RETURN_STATEMENT, "RETURN_STATEMENT");
		nodeTypeMap.put(Statement.SIMPLE_NAME, "SIMPLE_NAME");
		nodeTypeMap.put(Statement.SIMPLE_TYPE, "SIMPLE_TYPE");
		nodeTypeMap.put(Statement.SINGLE_MEMBER_ANNOTATION, "SINGLE_MEMBER_ANNOTATION");
		nodeTypeMap.put(Statement.SINGLE_VARIABLE_DECLARATION, "SINGLE_VARIABLE_DECLARATION");
		nodeTypeMap.put(Statement.STRING_LITERAL, "STRING_LITERAL");
		nodeTypeMap.put(Statement.SUPER_CONSTRUCTOR_INVOCATION, "SUPER_CONSTRUCTOR_INVOCATION");
		nodeTypeMap.put(Statement.SUPER_FIELD_ACCESS, "SUPER_FIELD_ACCESS");
		nodeTypeMap.put(Statement.SUPER_METHOD_INVOCATION, "SUPER_METHOD_INVOCATION");
		nodeTypeMap.put(Statement.SUPER_METHOD_REFERENCE, "SUPER_METHOD_REFERENCE");
		nodeTypeMap.put(Statement.SWITCH_CASE, "SWITCH_CASE");
		nodeTypeMap.put(Statement.SWITCH_STATEMENT, "SWITCH_STATEMENT");
		nodeTypeMap.put(Statement.SYNCHRONIZED_STATEMENT, "SYNCHRONIZED_STATEMENT");
		nodeTypeMap.put(Statement.TAG_ELEMENT, "TAG_ELEMENT");
		nodeTypeMap.put(Statement.TEXT_ELEMENT, "TEXT_ELEMENT");
		nodeTypeMap.put(Statement.THIS_EXPRESSION, "THIS_EXPRESSION");
		nodeTypeMap.put(Statement.THROW_STATEMENT, "THROW_STATEMENT");
		nodeTypeMap.put(Statement.TRY_STATEMENT, "TRY_STATEMENT");
		nodeTypeMap.put(Statement.TYPE_DECLARATION, "TYPE_DECLARATION");
		nodeTypeMap.put(Statement.TYPE_DECLARATION_STATEMENT, "TYPE_DECLARATION_STATEMENT");
		nodeTypeMap.put(Statement.TYPE_LITERAL, "TYPE_LITERAL");
		nodeTypeMap.put(Statement.TYPE_METHOD_REFERENCE, "TYPE_METHOD_REFERENCE");
		nodeTypeMap.put(Statement.TYPE_PARAMETER, "TYPE_PARAMETER");
		nodeTypeMap.put(Statement.UNION_TYPE, "UNION_TYPE");
		nodeTypeMap.put(Statement.VARIABLE_DECLARATION_EXPRESSION, "VARIABLE_DECLARATION_EXPRESSION");
		nodeTypeMap.put(Statement.VARIABLE_DECLARATION_FRAGMENT, "VARIABLE_DECLARATION_FRAGMENT");
		nodeTypeMap.put(Statement.VARIABLE_DECLARATION_STATEMENT, "VARIABLE_DECLARATION_STATEMENT");
		nodeTypeMap.put(Statement.WHILE_STATEMENT, "WHILE_STATEMENT");
		nodeTypeMap.put(Statement.WILDCARD_TYPE, "WILDCARD_TYPE");
		return nodeTypeMap;
	}

	// commentID,classChange,classChangeRelation,methodChange,methodChangeRelation,
	// codeLine,changeCodeLine,changeCodeLine/codeLine,statements,changeStatements,changeStatements/statements,
	// insertStatements,insertStatements/changeStatements,updateStatements,updateStatements/changeStatements,
	// deleteStatements,deleteStatements/changeStatements,
	public static void combineFeatures() throws IOException {

		List<String> output = new ArrayList<String>();

		List<String> classChangeLines = FileUtils.readLines(new File("file/features/class_attribute_change.csv"),
				"UTF-8");
		Set<Integer> classChangeSet = new HashSet<Integer>();
		for (String str : classChangeLines) {
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			if (temps.length > 1) {
				classChangeSet.add(commentID);
			}
		}

		List<String> classRelationLines = FileUtils.readLines(new File("file/features/class_attribute_relation.csv"),
				"UTF-8");
		Set<Integer> classRelationSet = new HashSet<Integer>();
		for (String str : classRelationLines) {
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			classRelationSet.add(commentID);
		}

		List<String> methodChangeLines = FileUtils
				.readLines(new File("file/features/method_parameter_return_change.csv"), "UTF-8");
		Set<Integer> methodChangeSet = new HashSet<Integer>();
		for (String str : methodChangeLines) {
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			if (temps.length > 1) {
				methodChangeSet.add(commentID);
			}
		}

		List<String> methodRelationLines = FileUtils
				.readLines(new File("file/features/method_parameter_return_relation.csv"), "UTF-8");
		Set<Integer> methodRelationSet = new HashSet<Integer>();
		for (String str : methodRelationLines) {
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			methodRelationSet.add(commentID);
		}

		List<String> codeLines = FileUtils.readLines(new File("file/features/codeline.csv"), "UTF-8");
		Map<Integer, Integer> codeLineMap = new HashMap<Integer, Integer>();
		for (String str : codeLines) {
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			int codeLine = Integer.parseInt(temps[3]);
			codeLineMap.put(commentID, codeLine);
		}

		List<String> changeCodeLines = FileUtils.readLines(new File("file/features/changecodeline.csv"), "UTF-8");
		Map<Integer, Integer> changeCodeLineMap = new HashMap<Integer, Integer>();
		for (String str : changeCodeLines) {
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			int changeCodeLine = Integer.parseInt(temps[1].split(" ")[0]) + Integer.parseInt(temps[2].split(" ")[0])
					+ Integer.parseInt(temps[3].split(" ")[0]);
			changeCodeLineMap.put(commentID, changeCodeLine);
		}

		List<String> changeStatementLines = FileUtils.readLines(new File("file/features/changestatementcount.csv"),
				"UTF-8");
		Map<Integer, Integer> changeStatementMap = new HashMap<Integer, Integer>();
		Map<Integer, Integer> addStatementMap = new HashMap<Integer, Integer>();
		Map<Integer, Integer> deleteStatementMap = new HashMap<Integer, Integer>();
		Map<Integer, Integer> updateStatementMap = new HashMap<Integer, Integer>();
		for (String str : changeStatementLines) {
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			int addStatements = Integer.parseInt(temps[1]);
			int deleteStatements = Integer.parseInt(temps[2]);
			int updateStatements = Integer.parseInt(temps[3]);
			int changeStatements = addStatements + deleteStatements + updateStatements;

			changeStatementMap.put(commentID, changeStatements);
			addStatementMap.put(commentID, addStatements);
			deleteStatementMap.put(commentID, deleteStatements);
			updateStatementMap.put(commentID, updateStatements);

		}

		List<String> statementCountLines = FileUtils.readLines(new File("file/features/statementcount.csv"), "UTF-8");
		for (String str : statementCountLines) {
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			int statements = Integer.parseInt(temps[1]);
			if (statements > 0) {
				String temp = "";
				temp += commentID + ",";

				if (classChangeSet.contains(commentID)) {
					temp += "1,";
				} else {
					temp += "0,";
				}

				if (classRelationSet.contains(commentID)) {
					temp += "1,";
				} else {
					temp += "0,";
				}

				if (methodChangeSet.contains(commentID)) {
					temp += "1,";
				} else {
					temp += "0,";
				}

				if (methodRelationSet.contains(commentID)) {
					temp += "1,";
				} else {
					temp += "0,";
				}

				temp += codeLineMap.get(commentID) + ",";
				temp += changeCodeLineMap.get(commentID) + ",";
				temp += changeCodeLineMap.get(commentID) * 1.0d / codeLineMap.get(commentID) + ",";
				temp += statements + ",";
				temp += changeStatementMap.get(commentID) + ",";
				temp += changeStatementMap.get(commentID) * 1.0d / statements + ",";
				temp += addStatementMap.get(commentID) + ",";
				temp += addStatementMap.get(commentID) * 1.0d / changeStatementMap.get(commentID) + ",";
				temp += deleteStatementMap.get(commentID) + ",";
				temp += deleteStatementMap.get(commentID) * 1.0d / changeStatementMap.get(commentID) + ",";
				temp += updateStatementMap.get(commentID) + ",";
				temp += updateStatementMap.get(commentID) * 1.0d / changeStatementMap.get(commentID);
				output.add(temp);
			}
		}
		FileUtils.writeLines(new File("file/features/data.csv"), output);
	}

	public static void combineFeatures2() throws IOException {

		List<String> output = new ArrayList<String>();

		List<String> dataLines1 = FileUtils.readLines(new File("file/features/data.csv"), "UTF-8");
		List<String> dataLines2 = FileUtils.readLines(new File("file/features/data2.csv"), "UTF-8");

		Map<Integer, String> dataMap1 = new HashMap<Integer, String>();
		for (String str : dataLines1) {
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			String value = "";
			for (int i = 1; i < temps.length - 1; i++) {
				value += temps[i] + ",";
			}
			value += temps[temps.length - 1];
			dataMap1.put(commentID, value);
		}

		for (String str : dataLines2) {
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			String temp = temps[0] + "," + temps[1] + ",";
			if (dataMap1.containsKey(commentID)) {
				temp += dataMap1.get(commentID) + ",";
				for (int i = 25; i < temps.length - 1; i++) {
					temp += temps[i] + ",";
				}
				temp += temps[temps.length - 1];
				output.add(temp);
			}

		}
		FileUtils.writeLines(new File("file/features/data3.csv"), output);

	}

	public static void computeChangeStatementType() throws IOException {
		List<String> changeCodeLines = FileUtils.readLines(new File("file/features/changecodeline.csv"), "UTF-8");

		String[] types = new String[] { "IfStatement", "Elseif", "ForStatement", "EnhancedForStatement",
				"WhileStatement", "CatchClause", "TryStatement", "ThrowStatement", "MethodInvocation",
				"VariableDeclarationStatement" };

		Map<String, Set<Integer>> addMap = new HashMap<String, Set<Integer>>();
		Map<String, Set<Integer>> deleteMap = new HashMap<String, Set<Integer>>();
		Map<String, Set<Integer>> updateMap = new HashMap<String, Set<Integer>>();

		for (String str : types) {
			addMap.put(str, new LinkedHashSet<Integer>());
			deleteMap.put(str, new LinkedHashSet<Integer>());
			updateMap.put(str, new LinkedHashSet<Integer>());
		}

		addMap.put("log", new LinkedHashSet<Integer>());
		deleteMap.put("log", new LinkedHashSet<Integer>());
		updateMap.put("log", new LinkedHashSet<Integer>());

		addMap.put("print", new LinkedHashSet<Integer>());
		deleteMap.put("print", new LinkedHashSet<Integer>());
		updateMap.put("print", new LinkedHashSet<Integer>());

		addMap.put("assert", new LinkedHashSet<Integer>());
		deleteMap.put("assert", new LinkedHashSet<Integer>());
		updateMap.put("assert", new LinkedHashSet<Integer>());

		for (String str : changeCodeLines) {
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			String[] addStatements = temps[1].split(" ");
			String[] deleteStatements = temps[2].split(" ");
			String[] updateStatements = temps[3].split(" ");

			CommentEntry comment = commentRepo.findASingleByCommentID(commentID);
			List<Token> newToken = comment.getNewToken();
			List<Token> oldToken = comment.getOldToken();

			if (addStatements.length > 1) {
				for (Token token : newToken) {
					for (int i = 1; i < addStatements.length; i++) {
						int startLine = Integer.parseInt(addStatements[i].split(":")[1].split("-")[0]);
						int endLine = Integer.parseInt(addStatements[i].split(":")[1].split("-")[1]);

						if (token.getStartLine() >= startLine && token.getEndLine() <= endLine) {
							if (addMap.containsKey(token.getTokenName())) {
								addMap.get(token.getTokenName()).add(commentID);
							}
							if(token.getKeyword()!=null) {
							if (token.getKeyword().equals("print") || token.getKeyword().equals("println")) {
								addMap.get("print").add(commentID);
							}
							if (token.getKeyword().startsWith("log") || token.getKeyword().startsWith("Log")) {
								addMap.get("log").add(commentID);
							}
							if (token.getKeyword().startsWith("assert") || token.getKeyword().startsWith("Assert")) {
								addMap.get("assert").add(commentID);
							}
							}
						}
					}
				}
			}
			
			if (deleteStatements.length > 1) {
				for (Token token : oldToken) {
					for (int i = 1; i < deleteStatements.length; i++) {
						int startLine = Integer.parseInt(deleteStatements[i].split(":")[1].split("-")[0]);
						int endLine = Integer.parseInt(deleteStatements[i].split(":")[1].split("-")[1]);

						if (token.getStartLine() >= startLine && token.getEndLine() <= endLine) {
							if (deleteMap.containsKey(token.getTokenName())) {
								deleteMap.get(token.getTokenName()).add(commentID);
							}
							if(token.getKeyword()!=null) {
							if (token.getKeyword().equals("print") || token.getKeyword().equals("println")) {
								deleteMap.get("print").add(commentID);
							}
							if (token.getKeyword().startsWith("log") || token.getKeyword().startsWith("Log")) {
								deleteMap.get("log").add(commentID);
							}
							if (token.getKeyword().startsWith("assert") || token.getKeyword().startsWith("Assert")) {
								deleteMap.get("assert").add(commentID);
							}
							}
						}
					}
				}
			}
			
			if (updateStatements.length > 1) {
				for (Token token : oldToken) {
					for (int i = 1; i < updateStatements.length; i++) {
						int startLine = Integer.parseInt(updateStatements[i].split(":")[1].split("-")[0]);
						int endLine = Integer.parseInt(updateStatements[i].split(":")[1].split("-")[1]);

						if (token.getStartLine() >= startLine && token.getEndLine() <= endLine) {
							if (updateMap.containsKey(token.getTokenName())) {
								updateMap.get(token.getTokenName()).add(commentID);
							}
							if(token.getKeyword()!=null) {
							if (token.getKeyword().equals("print") || token.getKeyword().equals("println")) {
								updateMap.get("print").add(commentID);
							}
							if (token.getKeyword().startsWith("log") || token.getKeyword().startsWith("Log")) {
								updateMap.get("log").add(commentID);
							}
							if (token.getKeyword().startsWith("assert") || token.getKeyword().startsWith("Assert")) {
								updateMap.get("assert").add(commentID);
							}
							}
						}
					}
				}
				
				for (Token token : newToken) {
					for (int i = 1; i < updateStatements.length; i++) {
						int startLine = Integer.parseInt(updateStatements[i].split(":")[1].split("-")[2]);
						int endLine = Integer.parseInt(updateStatements[i].split(":")[1].split("-")[3]);

						if (token.getStartLine() >= startLine && token.getEndLine() <= endLine) {
							if (updateMap.containsKey(token.getTokenName())) {
								updateMap.get(token.getTokenName()).add(commentID);
							}
							if(token.getKeyword()!=null) {
							if (token.getKeyword().equals("print") || token.getKeyword().equals("println")) {
								updateMap.get("print").add(commentID);
							}
							if (token.getKeyword().startsWith("log") || token.getKeyword().startsWith("Log")) {
								updateMap.get("log").add(commentID);
							}
							if (token.getKeyword().startsWith("assert") || token.getKeyword().startsWith("Assert")) {
								updateMap.get("assert").add(commentID);
							}
							}
						}
					}
				}
			}
		}
		
		List<String> addOutput = new ArrayList<String>();
		for(Map.Entry<String, Set<Integer>> entry:addMap.entrySet()) {
			String temp = entry.getKey();
			temp += entry.getValue().size()+",";
			for(int id:entry.getValue()) {
				temp += id+" ";
			}
			addOutput.add(temp);
		}
		FileUtils.writeLines(new File("file/features/addtypes.csv"), addOutput);
		
		List<String> deleteOutput = new ArrayList<String>();
		for(Map.Entry<String, Set<Integer>> entry:deleteMap.entrySet()) {
			String temp = entry.getKey();
			temp += entry.getValue().size()+",";
			for(int id:entry.getValue()) {
				temp += id+" ";
			}
			deleteOutput.add(temp);
		}
		FileUtils.writeLines(new File("file/features/deletetypes.csv"), deleteOutput);
		
		List<String> updateOutput = new ArrayList<String>();
		for(Map.Entry<String, Set<Integer>> entry:updateMap.entrySet()) {
			String temp = entry.getKey();
			temp += entry.getValue().size()+",";
			for(int id:entry.getValue()) {
				temp += id+" ";
			}
			updateOutput.add(temp);
		}
		FileUtils.writeLines(new File("file/features/updatetypes.csv"), updateOutput);
	}
	
	public static void statisticChangeType() throws IOException {
		List<String> addStatementTypes = FileUtils.readLines(new File("file/features/addtypes.csv"),"UTF-8");
		List<String> deleteStatementTypes = FileUtils.readLines(new File("file/features/deletetypes.csv"),"UTF-8");
		List<String> updateStatementTypes = FileUtils.readLines(new File("file/features/updatetypes.csv"),"UTF-8");
		List<String> data2Lines = FileUtils.readLines(new File("file/features/data2.csv"),"UTF-8");
		
		Map<Integer,Integer> commentLabelMap = new HashMap<Integer,Integer>();
		for(String str:data2Lines) {
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			int label = Integer.parseInt(temps[temps.length-1]);
			commentLabelMap.put(commentID, label);
		}
		
		List<String> addStaResult = new ArrayList<String>();
		for(String str:addStatementTypes) {
			String[] temps = str.split(",");
			String type = temps[0];
			int totalNum = Integer.parseInt(temps[1]);
			int changedNum = 0;
			int unChangeNum = 0;
			String[] idList = temps[2].split(" ");
			for(String str2:idList) {
			    int commentID = Integer.parseInt(str2);
			    if(commentLabelMap.get(commentID)==1) {
			    	changedNum ++;
			    }else {
			    	unChangeNum ++;
			    }
			}
			addStaResult.add(type+","+totalNum+","+changedNum+","+unChangeNum);
			
		}
		FileUtils.writeLines(new File("file/features/addtypestatistic.csv"), addStaResult);
		
		List<String> deleteStaResult = new ArrayList<String>();
		for(String str:deleteStatementTypes) {
			String[] temps = str.split(",");
			String type = temps[0];
			int totalNum = Integer.parseInt(temps[1]);
			int changedNum = 0;
			int unChangeNum = 0;
			String[] idList = temps[2].split(" ");
			for(String str2:idList) {
			    int commentID = Integer.parseInt(str2);
			    if(commentLabelMap.get(commentID)==1) {
			    	changedNum ++;
			    }else {
			    	unChangeNum ++;
			    }
			}
			deleteStaResult.add(type+","+totalNum+","+changedNum+","+unChangeNum);
			
		}
		FileUtils.writeLines(new File("file/features/deletetypestatistic.csv"), deleteStaResult);
		
		List<String> updateStaResult = new ArrayList<String>();
		for(String str:updateStatementTypes) {
			String[] temps = str.split(",");
			String type = temps[0];
			int totalNum = Integer.parseInt(temps[1]);
			int changedNum = 0;
			int unChangeNum = 0;
			String[] idList = temps[2].split(" ");
			for(String str2:idList) {
			    int commentID = Integer.parseInt(str2);
			    if(commentLabelMap.get(commentID)==1) {
			    	changedNum ++;
			    }else {
			    	unChangeNum ++;
			    }
			}
			updateStaResult.add(type+","+totalNum+","+changedNum+","+unChangeNum);
			
		}
		FileUtils.writeLines(new File("file/features/updatetypestatistic.csv"), updateStaResult);
	}
	
	public static void statisticChangeLength() throws IOException {
		List<String> data2Lines = FileUtils.readLines(new File("file/features/data2.csv"), "UTF-8");
	    List<String> changeStatementLines = FileUtils.readLines(new File("file/features/changestatementcount.csv"),"UTF-8");
	    
	    Map<Integer,Integer> commentLabelMap = new HashMap<Integer,Integer>();
		for(String str:data2Lines) {
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			int label = Integer.parseInt(temps[temps.length-1]);
			commentLabelMap.put(commentID, label);
		}
		
		Map<Integer,List<Integer>> changeLengthMap = new HashMap<Integer,List<Integer>>();
		for(String str:changeStatementLines) {
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			int changeLength = Integer.parseInt(temps[1])+Integer.parseInt(temps[2])+Integer.parseInt(temps[3]);
			if(!changeLengthMap.containsKey(changeLength)) {
				changeLengthMap.put(changeLength, new ArrayList<Integer>());
			}
			changeLengthMap.get(changeLength).add(commentID);
		}
		
		List<String> output = new ArrayList<String>();
		for(Map.Entry<Integer, List<Integer>> entry : changeLengthMap.entrySet()) {
			
			int changeCount = 0;
			int unChangeCount = 0;
			for(int id:entry.getValue()) {
				if(commentLabelMap.get(id)==1) {
					changeCount++;
				}else {
					unChangeCount++;
				}
			}
			
			output.add(entry.getKey()+","+entry.getValue().size()+","+changeCount+","+unChangeCount);
		}
		FileUtils.writeLines(new File("file/features/changelengthstatistic.csv"), output);
	}
	
	public static void generateData4() throws IOException {
		List<String> dataLines = FileUtils.readLines(new File("file/features/data.csv"),"UTF-8");
		List<String> data2Lines = FileUtils.readLines(new File("file/features/data2.csv"),"UTF-8");
		List<String> addTypeLines = FileUtils.readLines(new File("file/features/addtypes.csv"),"UTF-8");
		List<String> deleteTypeLines = FileUtils.readLines(new File("file/features/deletetypes.csv"),"UTF-8");
		List<String> updateTypeLines = FileUtils.readLines(new File("file/features/updatetypes.csv"),"UTF-8");
		
		Map<Integer,List<Integer>> dataMap = new HashMap<Integer,List<Integer>>();
		for(String str:dataLines) {
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			dataMap.put(commentID, new ArrayList<Integer>());
			
			//context features
			dataMap.get(commentID).add(Integer.parseInt(temps[1]));
			dataMap.get(commentID).add(Integer.parseInt(temps[2]));
			dataMap.get(commentID).add(Integer.parseInt(temps[3]));
			dataMap.get(commentID).add(Integer.parseInt(temps[4]));
			
			//code change features
			//statements,changed statements,changed statements/statements
			dataMap.get(commentID).add(Integer.parseInt(temps[8]));
			dataMap.get(commentID).add(Integer.parseInt(temps[9]));
			dataMap.get(commentID).add(Integer.parseInt(temps[9])*10/Integer.parseInt(temps[8]));
			
			//inserted statements,deleted statements,updated statements
			dataMap.get(commentID).add(Integer.parseInt(temps[11]));
			dataMap.get(commentID).add(Integer.parseInt(temps[11])*10/Integer.parseInt(temps[8]));
			dataMap.get(commentID).add(Integer.parseInt(temps[13]));
			dataMap.get(commentID).add(Integer.parseInt(temps[13])*10/Integer.parseInt(temps[8]));
			dataMap.get(commentID).add(Integer.parseInt(temps[15]));
			dataMap.get(commentID).add(Integer.parseInt(temps[15])*10/Integer.parseInt(temps[8]));
		}
		
		//changed statement types
		Map<String,Set<Integer>> addMap = new HashMap<String,Set<Integer>>();
		for(String str:addTypeLines) {
			String[] temps = str.split(",");
			addMap.put(temps[0], new HashSet<Integer>());
			for(String str2:temps[2].split(" ")) {
				addMap.get(temps[0]).add(Integer.parseInt(str2));
			}
		}
		
		Map<String,Set<Integer>> deleteMap = new HashMap<String,Set<Integer>>();
		for(String str:deleteTypeLines) {
			String[] temps = str.split(",");
			deleteMap.put(temps[0], new HashSet<Integer>());
			for(String str2:temps[2].split(" ")) {
				deleteMap.get(temps[0]).add(Integer.parseInt(str2));
			}
		}
		
		Map<String,Set<Integer>> updateMap = new HashMap<String,Set<Integer>>();
		for(String str:updateTypeLines) {
			String[] temps = str.split(",");
			updateMap.put(temps[0], new HashSet<Integer>());
			for(String str2:temps[2].split(" ")) {
				updateMap.get(temps[0]).add(Integer.parseInt(str2));
			}
		}
		
		String[] types = new String[] { "IfStatement", "Elseif", "ForStatement", "EnhancedForStatement",
				"WhileStatement", "CatchClause", "TryStatement", "ThrowStatement", "MethodInvocation",
				"VariableDeclarationStatement" };
		
		for(Map.Entry<Integer, List<Integer>> entry:dataMap.entrySet()) {
			for(String str:types) {
				if(addMap.get(str).contains(entry.getKey())) {
					entry.getValue().add(1);
				}else {
					entry.getValue().add(0);
				}
			}
			for(String str:types) {
				if(deleteMap.get(str).contains(entry.getKey())) {
					entry.getValue().add(1);
				}else {
					entry.getValue().add(0);
				}
			}
			for(String str:types) {
				if(updateMap.get(str).contains(entry.getKey())) {
					entry.getValue().add(1);
				}else {
					entry.getValue().add(0);
				}
			}
		}
		
		for(String str:data2Lines) {
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			if(dataMap.containsKey(commentID)) {
			    //project
				int project = Integer.parseInt(temps[1]);
			    dataMap.get(commentID).add(0,project);
			    
			    //refactoring 1,2,3,5,6,7,8,9
			    dataMap.get(commentID).add(Integer.parseInt(temps[59]));
			    dataMap.get(commentID).add(Integer.parseInt(temps[60]));
			    dataMap.get(commentID).add(Integer.parseInt(temps[61]));
			    dataMap.get(commentID).add(Integer.parseInt(temps[62]));
			    dataMap.get(commentID).add(Integer.parseInt(temps[63]));
			    dataMap.get(commentID).add(Integer.parseInt(temps[64]));
			    dataMap.get(commentID).add(Integer.parseInt(temps[65]));
			    dataMap.get(commentID).add(Integer.parseInt(temps[66]));
			    
			    //comment features
			    //comment line,method comment line,class comment line
			    dataMap.get(commentID).add(Integer.parseInt(temps[53]));
			    dataMap.get(commentID).add(Integer.parseInt(temps[54]));
			    dataMap.get(commentID).add(Integer.parseInt(temps[55]));
			    
			    //comment line method comment line class comment line weights
			    dataMap.get(commentID).add(Integer.parseInt(temps[56]));
			    dataMap.get(commentID).add(Integer.parseInt(temps[57]));
			    dataMap.get(commentID).add(Integer.parseInt(temps[58]));
			    
			    //word size,todos
			    dataMap.get(commentID).add(Integer.parseInt(temps[45]));
			    dataMap.get(commentID).add(Integer.parseInt(temps[46]));
			    
			    //relationship features
			    dataMap.get(commentID).add(Integer.parseInt(temps[67]));
			    dataMap.get(commentID).add(Integer.parseInt(temps[68]));
			    dataMap.get(commentID).add(Integer.parseInt(temps[69]));
			    dataMap.get(commentID).add(Integer.parseInt(temps[70]));
			    dataMap.get(commentID).add(Integer.parseInt(temps[71]));
			    dataMap.get(commentID).add(Integer.parseInt(temps[72]));
			    dataMap.get(commentID).add(Integer.parseInt(temps[73]));
			    
			    //label
			    dataMap.get(commentID).add(Integer.parseInt(temps[74]));
			}
		}
		
		List<String> output = new ArrayList<String>();
		for(Map.Entry<Integer, List<Integer>> entry:dataMap.entrySet()) {
			String temp = entry.getKey()+",";
			for(int i=0;i<entry.getValue().size()-1;i++) {
				temp += entry.getValue().get(i)+",";
			}
			temp += entry.getValue().get(entry.getValue().size()-1);
			output.add(temp);
		}
		
		FileUtils.writeLines(new File("file/features/data4.csv"), output);
		
		
	}
	
	public static void statisticProject() throws IOException {
		Map<Integer,Set<String>> commitMap = new HashMap<Integer,Set<String>>();
		Map<Integer,Set<Integer>> commentMap = new HashMap<Integer,Set<Integer>>();
		Map<Integer,Set<Integer>> changedCommentMap = new HashMap<Integer,Set<Integer>>();
		Map<Integer,Set<Integer>> unchangedCommentMap = new HashMap<Integer,Set<Integer>>();
		
		List<String> datas = FileUtils.readLines(new File("file/features/data4.csv"),"UTF-8");
		for(String str:datas) {
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			int project = Integer.parseInt(temps[1]);
			int label = Integer.parseInt(temps[temps.length-1]);
			CommentEntry comment = commentRepo.findASingleByCommentID(commentID);
			String commitID = comment.getCommitID();
			
			if(!commitMap.containsKey(project)) {
				commitMap.put(project, new HashSet<String>());
			}
			if(!commentMap.containsKey(project)) {
				commentMap.put(project, new HashSet<Integer>());
			}
			if(!changedCommentMap.containsKey(project)) {
				changedCommentMap.put(project, new HashSet<Integer>());
			}
			if(!unchangedCommentMap.containsKey(project)) {
				unchangedCommentMap.put(project, new HashSet<Integer>());
			}
			
			commitMap.get(project).add(commitID);
			commentMap.get(project).add(commentID);
			if(label==1) {
				changedCommentMap.get(project).add(commentID);
			}else {
				unchangedCommentMap.get(project).add(commentID);
			}
		}
		List<String> output = new ArrayList<String>();
		for(Map.Entry<Integer, Set<String>> entry:commitMap.entrySet()) {
			output.add(entry.getKey()+","+entry.getValue().size()+","+commentMap.get(entry.getKey()).size()+
					","+changedCommentMap.get(entry.getKey()).size()+","+unchangedCommentMap.get(entry.getKey()).size());
			
		}
		FileUtils.writeLines(new File("file/features/commitstatistic.csv"), output);
		
	}
	
	public static void addContextFeature() throws IOException{
		List<String> classChangeLines = FileUtils.readLines(new File("file/features/class_attribute_change.csv"),
				"UTF-8");
		Set<Integer> classChangeSet = new HashSet<Integer>();
		for (String str : classChangeLines) {
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			if (temps.length > 1) {
				classChangeSet.add(commentID);
			}
		}

		List<String> classRelationLines = FileUtils.readLines(new File("file/features/class_attribute_relation.csv"),
				"UTF-8");
		Set<Integer> classRelationSet = new HashSet<Integer>();
		for (String str : classRelationLines) {
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			classRelationSet.add(commentID);
		}

		List<String> methodChangeLines = FileUtils
				.readLines(new File("file/features/method_parameter_return_change.csv"), "UTF-8");
		Set<Integer> methodChangeSet = new HashSet<Integer>();
		for (String str : methodChangeLines) {
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			if (temps.length > 1) {
				methodChangeSet.add(commentID);
			}
		}

		List<String> methodRelationLines = FileUtils
				.readLines(new File("file/features/method_parameter_return_relation.csv"), "UTF-8");
		Set<Integer> methodRelationSet = new HashSet<Integer>();
		for (String str : methodRelationLines) {
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			methodRelationSet.add(commentID);
		}
		
		List<String> output = new ArrayList<String>();
		List<String> dataLines = FileUtils.readLines(new File("file/features/data2.csv"), "UTF-8");
		for(String str:dataLines) {
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			
			String temp = commentID+","+temps[1]+",";
			if(classChangeSet.contains(commentID)) {
				temp += "1,";
			}else {
				temp += "0,";
			}
			if(classRelationSet.contains(commentID)) {
				temp += "1,";
			}else {
				temp += "0,";
			}
			if(methodChangeSet.contains(commentID)) {
				temp += "1,";
			}else {
				temp += "0,";
			}
			if(methodRelationSet.contains(commentID)) {
				temp += "1,";
			}else {
				temp += "0,";
			}
			
			for(int i=2;i<temps.length-1;i++) {
				temp += temps[i]+",";
			}
			temp += temps[temps.length-1];
			output.add(temp);
		}
		FileUtils.writeLines(new File("file/features/data5.csv"), output);
	}

	public static void main(String[] args) throws IOException {
		addContextFeature();
	}

}
