package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.bean.DiffType;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentRepository;

public class Test3 {

	public static void main(String[] args) throws IOException {
		CommentRepository commentRepo = RepositoryFactory.getCommentRepository();
		List<String> result = new ArrayList<String>();
		Map<String,Integer> insertStatementMap = new HashMap<String,Integer>();
		Map<String,Integer> cInsertStatementMap = new HashMap<String,Integer>();
		Map<String,Integer> updateStatementMap = new HashMap<String,Integer>();
		Map<String,Integer> cUpdateStatementMap = new HashMap<String,Integer>();
		Map<String,Integer> deleteStatementMap = new HashMap<String,Integer>();
		Map<String,Integer> cDeleteStatementMap = new HashMap<String,Integer>();
		
		String[] types = new String[]{"if","for","while","method_call","variable_declare","other"};
		for(String str:types){
			insertStatementMap.put(str, 0);
			cInsertStatementMap.put(str, 0);
			updateStatementMap.put(str, 0);
			cUpdateStatementMap.put(str,0);
			deleteStatementMap.put(str, 0);
			cDeleteStatementMap.put(str, 0);
		}
		
		for(int i=1;i<=111180;i++){
			if(i%1000==0){
			    System.out.println(i+" is done.");
			}
			CommentEntry comment = commentRepo.findASingleByCommentID(i);
			
			List<DiffType> diffList = comment.getDiffList();
			Set<Integer> updateNewLine = new HashSet<Integer>();
			Set<Integer> updateOldLine = new HashSet<Integer>();
			Set<Integer> deleteLine = new HashSet<Integer>();
			Set<Integer> insertLine = new HashSet<Integer>();
			
			int newCodeStartLine = comment.getNew_comment_endLine()+1;
			int oldCodeStartLine = comment.getOld_comment_endLine()+1;
			for(DiffType diff:diffList){
				if(diff.getType().indexOf("PARENT")<0&&diff.getType().indexOf("ORDER")<0){
					if(diff.getNewStartLine()>0&&diff.getOldStartLine()>0){
						for(int index = diff.getNewStartLine();index<=diff.getNewEndLine();index++){
							updateNewLine.add(index-newCodeStartLine);
						}
						for(int index = diff.getOldStartLine();index<=diff.getOldEndLine();index++){
							updateOldLine.add(index-oldCodeStartLine);
						}
					}
					if(diff.getNewStartLine()>0&&diff.getOldStartLine()==0){
						for(int index = diff.getNewStartLine();index<=diff.getNewEndLine();index++){
							insertLine.add(index-newCodeStartLine);
						}
					}
					if(diff.getNewStartLine()==0&&diff.getOldStartLine()>0){
						for(int index = diff.getOldStartLine();index<=diff.getOldEndLine();index++){
							deleteLine.add(index-oldCodeStartLine);
						}
					}
				}
			}
			List<String> newCodeList = comment.getNewCode();
			List<String> oldCodeList = comment.getOldCode();
			StringBuilder deleteCodeBuilder = new StringBuilder();
			for(int index:deleteLine){
				if(index>=0)
				deleteCodeBuilder.append(oldCodeList.get(index)+" ");
			}
			StringBuilder insertCodeBuilder = new StringBuilder();
			for(int index:insertLine){
				if(index>=0)
				insertCodeBuilder.append(newCodeList.get(index)+" ");
			}
			StringBuilder updateCodeBuilder = new StringBuilder();
			for(int index:updateNewLine){
				if(index>=0)
				updateCodeBuilder.append(newCodeList.get(index)+" ");
			}
			for(int index:updateOldLine){
				if(index>=0)
				updateCodeBuilder.append(oldCodeList.get(index)+" ");
			}
			
			String deleteCode = deleteCodeBuilder.toString();
			String insertCode = insertCodeBuilder.toString();
			String updateCode = updateCodeBuilder.toString();
			
			statisticDiffType(deleteStatementMap,cDeleteStatementMap,deleteCode,comment.isChange());
			statisticDiffType(insertStatementMap,cInsertStatementMap,insertCode,comment.isChange());
			statisticDiffType(updateStatementMap,cUpdateStatementMap,updateCode,comment.isChange());
		}
		
		List<String> deleteFile = new ArrayList<String>();
		for(Map.Entry<String, Integer> entry:deleteStatementMap.entrySet()){
			deleteFile.add(entry.getKey()+","+entry.getValue()+","+cDeleteStatementMap.get(entry.getKey()));
		}
		

		List<String> insertFile = new ArrayList<String>();
		for(Map.Entry<String, Integer> entry:insertStatementMap.entrySet()){
			insertFile.add(entry.getKey()+","+entry.getValue()+","+cInsertStatementMap.get(entry.getKey()));
		}
		

		List<String> updateFile = new ArrayList<String>();
		for(Map.Entry<String, Integer> entry:updateStatementMap.entrySet()){
			updateFile.add(entry.getKey()+","+entry.getValue()+","+cUpdateStatementMap.get(entry.getKey()));
		}
		
		FileUtils.writeLines(new File("D:/work/4.23/insert.csv"), insertFile);
		FileUtils.writeLines(new File("D:/work/4.23/update.csv"), updateFile);
		FileUtils.writeLines(new File("D:/work/4.23/delete.csv"), deleteFile);
	}
	
	private static void statisticDiffType(Map<String,Integer> map,Map<String,Integer> cMap,String str,boolean ischange){
		boolean flag = false;
		if(str.indexOf("if")>=0){
			flag = true;
			map.put("if", map.get("if")+1);
			if(ischange){
				cMap.put("if", cMap.get("if")+1);
			}
		}
		if(str.indexOf("for")>=0){
			flag = true;
			map.put("for", map.get("for")+1);
			if(ischange){
				cMap.put("for", cMap.get("for")+1);
			}
		}
		if(str.indexOf("while")>=0){
			flag = true;
			map.put("while", map.get("while")+1);
			if(ischange){
				cMap.put("while", cMap.get("while")+1);
			}
		}
		String[] temps = str.split(" ");
		for(String str2:temps){
		if(str2.matches("[a-zA-Z0-9_]+\\.[a-zA-Z0-9_]+\\(.*\\)*")){
			flag = true;
			
			map.put("method_call",map.get("method_call")+1);
			if(ischange){
				cMap.put("method_call", cMap.get("method_call")+1);
			}
			
			break;
		}
		}
		if(str.indexOf("new ")>=0){
			flag = true;
			map.put("variable_declare",map.get("variable_declare")+1);
			if(ischange){
				cMap.put("variable_declare", cMap.get("variable_declare")+1);
			}
		}
		if(!flag){
			map.put("other",map.get("other")+1);
			if(ischange){
				cMap.put("other", cMap.get("other")+1);
			}
		}
	}
	}

