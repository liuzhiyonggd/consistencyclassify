package sysu.consistency.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

public class Test2 {
	
	public static void main(String[] args) throws IOException{
		List<String> dataList = FileUtils.readLines(new File("D:/work/4.27/updatedata.txt"),"UTF-8");
		List<String> newDataList = new ArrayList<String>();
		
		List<String> insertLogFile = FileUtils.readLines(new File("D:/work/4.27/keyword/insert_log.txt"),"UTF-8");
		List<String> deleteLogFile = FileUtils.readLines(new File("D:/work/4.27/keyword/delete_log.txt"),"UTF-8");
		List<String> updateLogFile = FileUtils.readLines(new File("D:/work/4.27/keyword/update_log.txt"),"UTF-8");
		
		List<String> insertTryFile = FileUtils.readLines(new File("D:/work/4.27/keyword/insert_try.txt"),"UTF-8");
		List<String> deleteTryFile = FileUtils.readLines(new File("D:/work/4.27/keyword/delete_try.txt"),"UTF-8");
		List<String> updateTryFile = FileUtils.readLines(new File("D:/work/4.27/keyword/update_try.txt"),"UTF-8");
		
		List<String> insertCatchFile = FileUtils.readLines(new File("D:/work/4.27/keyword/insert_catch.txt"),"UTF-8");
		List<String> deleteCatchFile = FileUtils.readLines(new File("D:/work/4.27/keyword/delete_catch.txt"),"UTF-8");
		List<String> updateCatchFile = FileUtils.readLines(new File("D:/work/4.27/keyword/update_catch.txt"),"UTF-8");
		
		List<String> insertFinallyFile = FileUtils.readLines(new File("D:/work/4.27/keyword/insert_finally.txt"),"UTF-8");
		List<String> deleteFinallyFile = FileUtils.readLines(new File("D:/work/4.27/keyword/delete_finally.txt"),"UTF-8");
		List<String> updateFinallyFile = FileUtils.readLines(new File("D:/work/4.27/keyword/update_finally.txt"),"UTF-8");
		
		List<String> insertPrintFile = FileUtils.readLines(new File("D:/work/4.27/keyword/insert_print.txt"),"UTF-8");
		List<String> deletePrintFile = FileUtils.readLines(new File("D:/work/4.27/keyword/delete_print.txt"),"UTF-8");
		List<String> updatePrintFile = FileUtils.readLines(new File("D:/work/4.27/keyword/update_print.txt"),"UTF-8");
		
		List<String> insertIfFile = FileUtils.readLines(new File("D:/work/4.27/keyword/insert_if.txt"),"UTF-8");
		List<String> deleteIfFile = FileUtils.readLines(new File("D:/work/4.27/keyword/delete_if.txt"),"UTF-8");
		List<String> updateIfFile = FileUtils.readLines(new File("D:/work/4.27/keyword/update_if.txt"),"UTF-8");
		
		List<String> insertForFile = FileUtils.readLines(new File("D:/work/4.27/keyword/insert_for.txt"),"UTF-8");
		List<String> deleteForFile = FileUtils.readLines(new File("D:/work/4.27/keyword/delete_for.txt"),"UTF-8");
		List<String> updateForFile = FileUtils.readLines(new File("D:/work/4.27/keyword/update_for.txt"),"UTF-8");
		
		List<String> insertWhileFile = FileUtils.readLines(new File("D:/work/4.27/keyword/insert_while.txt"),"UTF-8");
		List<String> deleteWhileFile = FileUtils.readLines(new File("D:/work/4.27/keyword/delete_while.txt"),"UTF-8");
		List<String> updateWhileFile = FileUtils.readLines(new File("D:/work/4.27/keyword/update_while.txt"),"UTF-8");
		
		Map<Integer,Integer> insertLogMap = getMap(insertLogFile);
		Map<Integer,Integer> deleteLogMap = getMap(deleteLogFile);
		Map<Integer,Integer> updateLogMap = getMap(updateLogFile);
		
		Map<Integer,Integer> insertTryMap = getMap(insertTryFile);
		Map<Integer,Integer> deleteTryMap = getMap(deleteTryFile);
		Map<Integer,Integer> updateTryMap = getMap(updateTryFile);
		
		Map<Integer,Integer> insertCatchMap = getMap(insertCatchFile);
		Map<Integer,Integer> deleteCatchMap = getMap(deleteCatchFile);
		Map<Integer,Integer> updateCatchMap = getMap(updateCatchFile);
		
		Map<Integer,Integer> insertFinallyMap = getMap(insertFinallyFile);
		Map<Integer,Integer> deleteFinallyMap = getMap(deleteFinallyFile);
		Map<Integer,Integer> updateFinallyMap = getMap(updateFinallyFile);
		
		Map<Integer,Integer> insertPrintMap = getMap(insertPrintFile);
		Map<Integer,Integer> deletePrintMap = getMap(deletePrintFile);
		Map<Integer,Integer> updatePrintMap = getMap(updatePrintFile);
		
		Map<Integer,Integer> insertIfMap = getMap(insertIfFile);
		Map<Integer,Integer> deleteIfMap = getMap(deleteIfFile);
		Map<Integer,Integer> updateIfMap = getMap(updateIfFile);
		
		Map<Integer,Integer> insertForMap = getMap(insertForFile);
		Map<Integer,Integer> deleteForMap = getMap(deleteForFile);
		Map<Integer,Integer> updateForMap = getMap(updateForFile);
		
		Map<Integer,Integer> insertWhileMap = getMap(insertWhileFile);
		Map<Integer,Integer> deleteWhileMap = getMap(deleteWhileFile);
		Map<Integer,Integer> updateWhileMap = getMap(updateWhileFile);
		
		for(String str:dataList){
			String[] temps = str.split(" ");
			int commentID = Integer.parseInt(temps[0]);
			
			int insertLog = 0;
			int deleteLog = 0;
			int updateLog = 0;
			int insertTry = 0;
			int deleteTry = 0;
			int updateTry = 0;
			int insertCatch = 0;
			int deleteCatch = 0;
			int updateCatch = 0;
			int insertFinally = 0;
			int deleteFinally = 0;
			int updateFinally = 0;
			int insertPrint = 0;
			int deletePrint = 0;
			int updatePrint = 0;
			int insertIf = 0;
			int deleteIf = 0;
			int updateIf = 0;
			int insertFor = 0;
			int deleteFor = 0;
			int updateFor = 0;
			int insertWhile = 0;
			int deleteWhile = 0;
			int updateWhile = 0;
			
			if(insertLogMap.containsKey(commentID)){
				insertLog = insertLogMap.get(commentID);
			}
			if(deleteLogMap.containsKey(commentID)){
				deleteLog = deleteLogMap.get(commentID);
			}
			if(updateLogMap.containsKey(commentID)){
				updateLog = updateLogMap.get(commentID);
			}
			if(insertTryMap.containsKey(commentID)){
				insertTry = insertTryMap.get(commentID);
			}
			if(deleteTryMap.containsKey(commentID)){
				deleteTry = deleteTryMap.get(commentID);
			}
			if(updateTryMap.containsKey(commentID)){
				updateTry = updateTryMap.get(commentID);
			}
			if(insertCatchMap.containsKey(commentID)){
				insertCatch = insertCatchMap.get(commentID);
			}
			if(deleteCatchMap.containsKey(commentID)){
				deleteCatch = deleteCatchMap.get(commentID);
			}
			if(updateCatchMap.containsKey(commentID)){
				updateCatch = updateCatchMap.get(commentID);
			}
			if(insertFinallyMap.containsKey(commentID)){
				insertFinally = insertFinallyMap.get(commentID);
			}
			if(deleteFinallyMap.containsKey(commentID)){
				deleteFinally = deleteFinallyMap.get(commentID);
			}
			if(updateFinallyMap.containsKey(commentID)){
				updateFinally = updateFinallyMap.get(commentID);
			}
			if(insertPrintMap.containsKey(commentID)){
				insertPrint = insertPrintMap.get(commentID);
			}
			if(deletePrintMap.containsKey(commentID)){
				deletePrint = deletePrintMap.get(commentID);
			}
			if(updatePrintMap.containsKey(commentID)){
				updatePrint = updatePrintMap.get(commentID);
			}
			if(insertIfMap.containsKey(commentID)){
				insertIf = insertIfMap.get(commentID);
			}
			if(deleteIfMap.containsKey(commentID)){
				deleteIf = deleteIfMap.get(commentID);
			}
			if(updateIfMap.containsKey(commentID)){
				updateIf = updateIfMap.get(commentID);
			}
			if(insertForMap.containsKey(commentID)){
				insertFor = insertForMap.get(commentID);
			}
			if(deleteForMap.containsKey(commentID)){
				deleteFor = deleteForMap.get(commentID);
			}
			if(updateForMap.containsKey(commentID)){
				updateFor = updateForMap.get(commentID);
			}
			if(insertWhileMap.containsKey(commentID)){
				insertWhile = insertWhileMap.get(commentID);
			}
			if(deleteWhileMap.containsKey(commentID)){
				deleteWhile = deleteWhileMap.get(commentID);
			}
			if(updateWhileMap.containsKey(commentID)){
				updateWhile = updateWhileMap.get(commentID);
			}
			StringBuilder builder = new StringBuilder();
			for(int i=0;i<temps.length-1;i++){
				builder.append(temps[i]+" ");
			}
			builder.append(insertLog+" "+deleteLog+" "+updateLog+" "+insertTry+" "+deleteTry+" "+updateTry+" "
					+insertCatch+" "+deleteCatch+" "+updateCatch+" "+insertFinally+" "+deleteFinally+" "+updateFinally
					+" "+insertPrint+" "+deletePrint+" "+updatePrint+" "+insertIf+" "+deleteIf+" "+updateIf+" "
					+insertFor+" "+deleteFor+" "+updateFor+" "+insertWhile+" "+deleteWhile+" "+updateWhile+" "+temps[temps.length-1]);
			
			newDataList.add(builder.toString());
		}
		FileUtils.writeLines(new File("D:/work/4.27/newupdatedata.txt"), newDataList);
	}
	
	private static Map<Integer,Integer> getMap(List<String> input){
		Map<Integer,Integer> map = new HashMap<Integer,Integer>();
		for(String str:input){
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			int times = Integer.parseInt(temps[1]);
			map.put(commentID, times);
		}
		return map;
		
	}

}
