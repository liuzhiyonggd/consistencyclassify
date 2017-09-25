package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.ClassMessage;
import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.bean.DiffType;
import sysu.consistency.db.bean.Token;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.ClassMessageRepository;
import sysu.consistency.db.repository.CommentRepository;

public class ContainsKeyword {

	public static void main(String[] args) throws IOException {
		CommentRepository commentRepo = RepositoryFactory.getCommentRepository();
		ClassMessageRepository classRepo = RepositoryFactory.getClassRepository();
		
		String[] types = new String[]{"IfStatement","Elseif","ForStatement","EnhancedForStatement","DoStatement","WhileStatement",
				"CatchClause","TryStatement","ThrowStatement","MethodInvocation","VariableDeclarationStatement","NumberLiteral","CharacterLiteral",
				"StringLiteral","NullLiteral","BooleanLiteral","AssertStatement"};
		
		List<String> idList = FileUtils.readLines(new File("D:/work/5.3/newid.txt"),"UTF-8");
		List<String> output = new ArrayList<String>();
		String temp = "CommentID,";
		for(String str:types){
			temp+=str+"_Insert,";
		}
		temp +="Print_Insert,Log_Insert,Debug_Insert,";
		for(String str:types){
			temp+=str+"_Update,";
		}
		temp +="Print_Update,Log_Update,Debug_Update,";
		
		for(String str:types){
			temp+=str+"_Delete,";
		}
		temp +="Print_Delete,Log_Delete,Debug_Delete,isChange";
		
		output.add(temp);
		
		for(String id:idList){
			int i = Integer.parseInt(id);
			if(i%1000==0){
			    System.out.println(i+" is done.");
			}
			CommentEntry comment = commentRepo.findASingleByCommentID(i);
			ClassMessage clazz = classRepo.findASingleByProjectAndCommitIDAndClassName(comment.getProject(), comment.getCommitID(), comment.getClassName());
            List<Token> newTokenList = clazz.getNewTokenList();
            List<Token> oldTokenList = clazz.getOldTokenList();
			
			
			List<DiffType> diffList = comment.getDiffList();
			Set<Integer> updateNewLine = new HashSet<Integer>();
			Set<Integer> updateOldLine = new HashSet<Integer>();
			Set<Integer> deleteLine = new HashSet<Integer>();
			Set<Integer> insertLine = new HashSet<Integer>();
			
			for(DiffType diff:diffList){
				if(diff.getType().indexOf("PARENT")<0&&diff.getType().indexOf("ORDER")<0){
					if(diff.getType().indexOf("CONDITION")<0){
					if(diff.getNewStartLine()>0&&diff.getOldStartLine()>0){
						for(int index = diff.getNewStartLine();index<=diff.getNewEndLine();index++){
							updateNewLine.add(index);
						}
						for(int index = diff.getOldStartLine();index<=diff.getOldEndLine();index++){
							updateOldLine.add(index);
						}
					}
					if(diff.getNewStartLine()>0&&diff.getOldStartLine()==0){
						for(int index = diff.getNewStartLine();index<=diff.getNewEndLine();index++){
							insertLine.add(index);
						}
					}
					if(diff.getNewStartLine()==0&&diff.getOldStartLine()>0){
						for(int index = diff.getOldStartLine();index<=diff.getOldEndLine();index++){
							deleteLine.add(index);
						}
					}
					}else{
						if(diff.getNewStartLine()>0&&diff.getOldStartLine()>0){
							updateNewLine.add(diff.getNewStartLine());
							updateOldLine.add(diff.getOldStartLine());
						}
						if(diff.getNewStartLine()>0&&diff.getOldStartLine()==0){
							updateNewLine.add(diff.getNewStartLine());
						}
						if(diff.getNewStartLine()==0&&diff.getOldStartLine()>0){
							updateOldLine.add(diff.getOldStartLine());
						}
					}
				}
			}
			
			List<Token> insertTokenList = new ArrayList<Token>();
			List<Token> deleteTokenList = new ArrayList<Token>();
			List<Token> updateTokenList = new ArrayList<Token>();
			
			for(Token token:newTokenList){
				if(containsToken(token,insertLine)){
					insertTokenList.add(token);
				}
				if(containsToken(token,updateNewLine)){
					updateTokenList.add(token);
				}
			}
			
			for(Token token:oldTokenList){
				if(containsToken(token,deleteLine)){
					deleteTokenList.add(token);
				}
				if(containsToken(token,updateOldLine)){
					updateTokenList.add(token);
				}
			}
			
			// 0:IfStatement      1:Elseif
			// 2:ForStatement     3:EnhancedForStatement     4:DoStatement     5:WhileStatement
			// 6:CatchClause      7:TryStatement             8:ThrowStatement
			// 9:MethodInvocation 10:VariableDeclarationStatement
			// 11:NumberLiteral   12:CharacterLiteral        13:StringLiteral  14:NullLiteral    15:BooleanLiteral
			// 16:AssertStatement 17:PrintStatement          18:LogStatement   19:DebugStatement
			
			
			int[] insertVector = new int[20];
			
			for(Token token:insertTokenList){
				for(int index=0;index<17;index++){
					if(token.getTokenName().equals(types[index])){
						insertVector[index]++;
					}
				}
				if(token.getKeyword()!=null){
				if(token.getKeyword().equals("print")||token.getKeyword().equals("println")){
					insertVector[17]++;
				}
				
				if(token.getKeyword().equals("log")){
					insertVector[18]++;
				}
				
				if(token.getKeyword().equals("debug")){
					insertVector[19]++;
				}
				}
			}
			
			int[] updateVector = new int[20];
			for(Token token:updateTokenList){
				for(int index=0;index<17;index++){
					if(token.getTokenName().equals(types[index])){
						updateVector[index]++;
					}
				}
				if(token.getKeyword()!=null){
				if(token.getKeyword().equals("print")||token.getKeyword().equals("println")){
					updateVector[17]++;
				}
				
				if(token.getKeyword().equals("log")){
					updateVector[18]++;
				}
				
				if(token.getKeyword().equals("debug")){
					updateVector[19]++;
				}
				}
			}
			
			int[] deleteVector = new int[20];
			for(Token token:deleteTokenList){
				for(int index=0;index<17;index++){
					if(token.getTokenName().equals(types[index])){
						deleteVector[index]++;
					}
				}
				if(token.getKeyword()!=null){
				if(token.getKeyword().equals("print")||token.getKeyword().equals("println")){
					deleteVector[17]++;
				}
				
				if(token.getKeyword().equals("log")){
					deleteVector[18]++;
				}
				
				if(token.getKeyword().equals("debug")){
					deleteVector[19]++;
				}
				}
			}
			
			String temp2 = comment.getCommentID()+",";
			for(int v:insertVector){
				temp2+=v+",";
			}
			for(int v:updateVector){
				temp2+=v+",";
			}
			for(int v:deleteVector){
				temp2+=v+",";
			}
			temp2+=comment.isChange();
			
			output.add(temp2);
			
		}
		
		FileUtils.writeLines(new File("D:/work/5.3/containskeyword.csv"), output);
			
		
	}
	
	private static boolean containsToken(Token token,Set<Integer> set){
		boolean result = true;
		int startLine = token.getStartLine();
		int endLine = token.getEndLine();
		
		for(int i=startLine;i<=endLine;i++){
			if(!set.contains(i)){
				result = false;
				break;
			}
		}
		return result;
	}
	
}

