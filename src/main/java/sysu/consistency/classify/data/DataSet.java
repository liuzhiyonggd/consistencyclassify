package sysu.consistency.classify.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.ClassMessage;
import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.bean.CommentWord;
import sysu.consistency.db.bean.DiffType;
import sysu.consistency.db.bean.Token;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.ClassMessageRepository;
import sysu.consistency.db.repository.CommentRepository;
import sysu.consistency.db.repository.CommentWordRepository;

public class DataSet {
	

	public static void generateDataSet(String idFile,String savePath) throws IOException {

		List<List<Integer>> data = new ArrayList<List<Integer>>();
		CommentRepository commentRepository = RepositoryFactory.getCommentRepository();
		
		
		List<String> idList = FileUtils.readLines(new File(idFile), "UTF-8");
		int count = 0;
		for(String id:idList){
			
			int i=Integer.parseInt(id.split(",")[0]);
			count++;
			if(count%1000==0){
				System.out.println(i +" is done.");
			}
			CommentEntry comment = commentRepository.findASingleByCommentID(i);
			if(comment==null){
				System.out.println(i +" is null.");
				continue;
			}
			if(comment.getDiffList().size()<=0) {
				continue;
			}
			
//			if(comment.getOldCode().size()>30) {
//				continue;
//			}
//			int newWordCount = 0;
//			int oldWordCount = 0;
//			for(String str:comment.getNewComment()) {
//				newWordCount += str.split(" ").length;
//			}
//			for(String str:comment.getOldComment()) {
//				oldWordCount += str.split(" ").length;
//			}
//			if(newWordCount>20||newWordCount<3) {
//				continue;
//			}
//			if(oldWordCount>20||oldWordCount<3) {
//				continue;
//			}
//			
			List<Integer> vector = extractLineVector(comment);
			
			data.add(vector);
		}
		
		writeFile(data, savePath+"/data9"+".txt");
		System.out.println("write data file is done.");
		}
		

	public static List<Integer> extractLineVector(CommentEntry comment) throws IOException {

		List<String> headerList = new ArrayList<String>();
		
		CommentWordRepository wordRepository = RepositoryFactory.getCommentWordRepository();
		ClassMessageRepository classRepository = RepositoryFactory.getClassRepository();
		
		Map<String,Integer> projects = new HashMap<String,Integer>();
		projects.put("jhotdraw", 1);
		projects.put("jedit", 2);
		projects.put("ejbca", 3);
		projects.put("htmlunit", 4);
		projects.put("opennms", 5);
		projects.put("jamwiki", 6);
		projects.put("kablink", 7);
		projects.put("freecol", 8);

			List<Integer> vector = new ArrayList<Integer>();
			
			//1. commentID 
			vector.add(comment.getCommentID());
			headerList.add("@attribute 'commentID' numeric");
			
			//2. comment project
//			vector.add(projects.get(comment.getProject()));
//			headerList.add("@attribute 'project' numeric");
			
			ClassMessage clazz = classRepository.findASingleByProjectAndCommitIDAndClassName(comment.getProject(), comment.getCommitID(),comment.getClassName());
			List<Token> newTokenList = clazz.getNewTokenList();
			List<Token> oldTokenList = clazz.getOldTokenList();
			
			List<Token> commentNewTokenList = new ArrayList<Token>();
			List<Token> commentOldTokenList = new ArrayList<Token>();
			
			for(Token token : newTokenList){
				if(token.getStartLine()>=comment.getNew_scope_startLine()&&token.getEndLine()<=comment.getNew_scope_endLine()){
					commentNewTokenList.add(token);
				}
			}
			
			for(Token token : oldTokenList){
				if(token.getStartLine()>=comment.getOld_scope_startLine()&&token.getEndLine()<=comment.getOld_scope_endLine()){
					commentOldTokenList.add(token);
				}
			}
			
			int insertStatement = 0;
			int deleteStatement = 0;
			int updateStatement = 0;
			int parentChange = 0;
			int orderingChange = 0;
			int conditionChange = 0;
			int insertAlternative = 0;
			int deleteAlternative = 0;

			int diffSize = 0;
			int changeLine = 0;
			int newAllLine = comment.getNew_scope_endLine()-comment.getNew_comment_endLine();
			int oldAllLine = comment.getOld_scope_endLine()-comment.getOld_comment_endLine();
			if(newAllLine <= 0){
				newAllLine = 1;
			}
			if(oldAllLine<=0){
				oldAllLine = 1;
			}
			
			int firstDiffLocation = 0;
			
			
			String diffString = "";
			for (DiffType diff : comment.getDiffList()) {

				if(diff.getType().indexOf("COMMENT")<0){
				    diffString += diff.getType()+";";
				}
				
				if (diff.getType().equals("STATEMENT_INSERT")) {
					insertStatement++;
					changeLine += diff.getNewEndLine()-diff.getNewStartLine()+1;
					if(firstDiffLocation==0){
						firstDiffLocation = diff.getNewStartLine()-comment.getNew_comment_endLine();
					}
					continue;
				}
				if (diff.getType().equals("STATEMENT_DELETE")) {
					deleteStatement++;
					changeLine += diff.getOldEndLine()-diff.getOldStartLine()+1;
					if(firstDiffLocation==0){
						firstDiffLocation = diff.getOldStartLine()-comment.getOld_comment_endLine();
					}
					continue;
				}
				if (diff.getType().equals("STATEMENT_UPDATE")) {
					updateStatement++;
					changeLine += diff.getNewEndLine()-diff.getNewStartLine()+1;
					if(firstDiffLocation==0){
						firstDiffLocation = diff.getNewStartLine()-comment.getNew_comment_endLine();
					}
					continue;
				}
				if (diff.getType().equals("STATEMENT_PARENT_CHANGE")) {
					parentChange++;
					continue;
				} 
				if (diff.getType().equals("STATEMENT_ORDERING_CHANGE")) {
					orderingChange++;
					continue;
				}
				if (diff.getType().equals("CONDITION_EXPRESSION_CHANGE")) {
					conditionChange++;
					changeLine++;
					if(firstDiffLocation==0){
						firstDiffLocation = diff.getNewStartLine()-comment.getNew_comment_endLine();
					}
					continue;
				} 
//				if(diff.getType().equals("COMMENT_INSERT")){
//					insertComment++;
////					changeLine++;
//					continue;
//				}
//				if(diff.getType().equals("COMMENT_DELETE")){
//					deleteComment++;
////					changeLine++;
//					continue;
//				}
//				if(diff.getType().equals("COMMENT_UPDATE")){
//					updateComment++;
////					changeLine++;
//					continue;
//				}
//				if(diff.getType().equals("COMMENT_MOVE")){
//					moveComment++;
////					changeLine++;
//					continue;
//				}
				if(diff.getType().equals("ALTERNATIVE_PART_INSERT")){
					insertAlternative++;
					changeLine += diff.getNewEndLine() - diff.getNewStartLine()+1;
					if(firstDiffLocation==0){
						firstDiffLocation = diff.getNewStartLine()-comment.getNew_comment_endLine();
					}
					continue;
				}
				if(diff.getType().equals("ALTERNATIVE_PART_DELETE")){
					deleteAlternative++;
					changeLine += diff.getOldEndLine() - diff.getOldStartLine()+1;
					if(firstDiffLocation==0){
						firstDiffLocation = diff.getOldStartLine()-comment.getOld_comment_endLine();
					}
					continue;
				}
			}
			diffSize = insertStatement + deleteStatement + updateStatement + conditionChange + insertAlternative + deleteAlternative;
			
			//3. diffSize
			vector.add(discridizeNum(diffSize));
			headerList.add("@attribute 'diffSize' numeric");			
			
			//4. insertStatement
			vector.add(discridizeNum(insertStatement));
			headerList.add("@attribute 'insertStatement' numeric");
			
			//5. insertStatementWeight
			vector.add(discridizeWeight(insertStatement*100/(diffSize+1)));
			headerList.add("@attribute 'insertStatementWeight' numeric");
			
			//6. deleteStatement
			vector.add(discridizeNum(deleteStatement));
			headerList.add("@attribute 'deleteStatement' numeric");
			
			//7. deleteStatementWeight
			vector.add(discridizeWeight(deleteStatement*100/(diffSize+1)));
			headerList.add("@attribute 'deleteStatementWeight' numeric");
			
			//8. updateStatement
			vector.add(discridizeNum(updateStatement));
			headerList.add("@attribute 'updateStatement' numeric");
			
			//9. updateStatementWeight
			vector.add(discridizeWeight(updateStatement*100/(diffSize+1)));
			headerList.add("@attribute 'updateStatementWeight' numeric");
			
			//10. parentChange
			vector.add(discridizeNum(parentChange));
			headerList.add("@attribute 'parentChange' numeric");
			
			//11. parentChangeWeight
			vector.add(discridizeWeight(parentChange*100/(diffSize+1)));
			headerList.add("@attribute 'parentChangeWeight' numeric");
			
			//12. orderingChange
			vector.add(discridizeNum(orderingChange));
			headerList.add("@attribute 'orderingChange' numeric");
			
			//13. orderingChangeWeight
			vector.add(discridizeWeight(orderingChange*100/(diffSize+1)));
			headerList.add("@attribute 'orderingChangeWeight' numeric");
			
			//14. conditionChange
			vector.add(discridizeNum(conditionChange));
			headerList.add("@attribute 'conditionChange' numeric");
			
			//15. conditionChangeWeight
			vector.add(discridizeWeight(conditionChange*100/(diffSize+1)));
			headerList.add("@attribute 'conditionChangeWeight' numeric");
			
//			//. insertComment
//			vector.add(discridizeNum(insertComment));
//			
//			//. insertCommentWeight
//			vector.add(discridizeWeight(insertComment*100/(diffSize+1)));
//			
//			//. deleteComment
//			vector.add(discridizeNum(deleteComment));
//			
//			//. deleteCommentWeight
//			vector.add(discridizeWeight(deleteComment*100/(diffSize+1)));
//			
//			//. updateComment
//			vector.add(discridizeNum(updateComment));
//			
//			//. updateCommentWeight
//			vector.add(discridizeWeight(updateComment*100/(diffSize+1)));
//			
//			//. moveComment
//			vector.add(discridizeNum(moveComment));
//			
//			//. moveCommentWeight
//			vector.add(discridizeWeight(moveComment*100/(diffSize+1)));
			
			//16. insertAlternative
			vector.add(discridizeNum(insertAlternative));
			headerList.add("@attribute 'insertAlternative' numeric");
			
			//17. insertAlternativeWeight
			vector.add(discridizeWeight(insertAlternative*100/(diffSize+1)));
			headerList.add("@attribute 'insertAlternativeWeight' numeric");
			
			//18. deleteAlternative
			vector.add(discridizeNum(deleteAlternative));
			headerList.add("@attribute 'deleteAlternative' numeric");
			
			//19. deleteAlternativeWeight
			vector.add(discridizeWeight(deleteAlternative*100/(diffSize+1)));
			headerList.add("@attribute 'deleteAlternativeWeight' numeric");
			
			//20. newAllLine
			vector.add(discridizeNum(newAllLine));
			headerList.add("@attribute 'newAllLine' numeric");
			
			//21. oldAllLine
			vector.add(discridizeNum(oldAllLine));
			headerList.add("@attribute 'oldAllLine' numeric");
			
			//22. subLine
			vector.add(discridizeNum(Math.abs(newAllLine-oldAllLine)));
			headerList.add("@attribute 'subLine' numeric");
			
			//23. changeLine
			vector.add(discridizeNum(changeLine));
			headerList.add("@attribute 'changeLine' numeric");
			
			//24. changeLineWeight
			vector.add(discridizeWeight(changeLine*100/newAllLine));
			headerList.add("@attribute 'changeLineWeight' numeric");
			
			//25. firstDiffLocation
			vector.add(discridizeNum(firstDiffLocation));
			headerList.add("@attribute 'firstDiffLocation' numeric");
			
			//26. refactor1
//			vector.add(comment.isRefactor1()?1:0);
			
			//27. refactor2
//			int refactor2 = 0;
//			List<String> refactor2List = FileUtils.readLines(new File("D:/work/4.20/refactor.txt"), "UTF-8");
//			Set<Integer> refactor2Set = new HashSet<Integer>();
//			for(String str:refactor2List){
//				refactor2Set.add(Integer.parseInt(str));
//			}
//			if(refactor2Set.contains(comment.getCommentID())){
//				refactor2 = 1;
//			}
//			vector.add(refactor2);
			
			//28. tokenChange
			boolean tokenChange = true;
			if(commentNewTokenList.size()!=commentOldTokenList.size()){
				tokenChange = false;
			}else{
				for(int i=0,n=commentNewTokenList.size();i<n;i++){
					if(!commentNewTokenList.get(i).getTokenName().equals(commentOldTokenList.get(i).getTokenName())){
						tokenChange = false;
						break;
					}
				}
			}
			vector.add(tokenChange?1:0);
			headerList.add("@attribute 'tokenChange' numeric");
			
//			//. diffTypes 
////			List<String> d1 = FileUtils.readLines(new File("token/1.txt"),"UTF-8");
//			List<String> d2 = FileUtils.readLines(new File("token/2.txt"),"UTF-8");
//			List<String> d3 = FileUtils.readLines(new File("token/3.txt"),"UTF-8");
//			List<String> d4 = FileUtils.readLines(new File("token/4.txt"),"UTF-8");
//			List<String> d5 = FileUtils.readLines(new File("token/5.txt"),"UTF-8");
//			List<String> d6 = FileUtils.readLines(new File("token/6.txt"),"UTF-8");
//			List<String> d7 = FileUtils.readLines(new File("token/7.txt"),"UTF-8");
//			List<String> d8 = FileUtils.readLines(new File("token/8.txt"),"UTF-8");
//			List<String> d9 = FileUtils.readLines(new File("token/9.txt"),"UTF-8");
//			List<String> d10 = FileUtils.readLines(new File("token/10.txt"),"UTF-8");
////			List<String> d11 = FileUtils.readLines(new File("token/11.txt"),"UTF-8");
////			
////			if(d1.contains(diffString)){
////				vector.add(1);
//			if(d2.contains(diffString)){
//				vector.add(2);
//			}else if(d3.contains(diffString)){
//				vector.add(3);
//			}else if(d4.contains(diffString)){
//				vector.add(4);
//			}else if(d5.contains(diffString)){
//				vector.add(5);
//			}else if(d6.contains(diffString)){
//				vector.add(6);
//			}else if(d7.contains(diffString)){
//				vector.add(7);
//			}else if(d8.contains(diffString)){
//				vector.add(8);
//			}else if(d9.contains(diffString)){
//				vector.add(9);
//			}else if(d10.contains(diffString)){
//				vector.add(10);
////			}else if(d11.contains(diffString)){
////				vector.add(11);
//			}else{
//				vector.add(0);
//			}
			
			// 锟斤拷同锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�
//			vector.addAll(keywordMap.get(comment.getCommentID()));


			CommentWord commentWord = wordRepository.findASingleByCommentID(comment.getCommentID());
			
			/**
			 * 锟斤拷注锟斤拷锟斤拷锟斤拷
			 */
			
			List<String> oldCommentWordList = commentWord.getOldCommentWords();
			
			//29. wordSize
			vector.add(discridizeNum(oldCommentWordList.size()));
			headerList.add("@attribute 'wordSize' numeric");
			
			//30. 锟角凤拷锟斤拷锟絋odos锟截硷拷锟斤拷
			StringBuilder sb = new StringBuilder();
			for(String str:comment.getOldComment()){
				sb.append(str+" ");
			}
			for(String str:comment.getOldCode()){
				sb.append(str+" ");
			}
//			String codes = sb.toString().toLowerCase();
//			boolean flag = codes.contains("todo")||codes.contains("fixme")||codes.contains("xxx");
//			vector.add(flag?1:0);
//			headerList.add("@attribute 'todos' numeric");
			
			/**
			 * 锟斤拷锟斤拷锟斤拷锟斤拷
			 */
			int codeSimilarity = (int)(commentWord.getCodeSimilarity()*100);
			int oldCodeOldCommentSimilarity = (int)(commentWord.getOldCodeOldCommentSimilarity()*100);
			int newCodeOldCommentSimilarity = (int)(commentWord.getNewCodeOldCommentSimilarity()*100);
			int addWordNumber = commentWord.getAddWords().size();
			int deleteWordNumber = commentWord.getDeleteWords().size();
			
			//31. 锟铰旧达拷锟斤拷锟斤拷锟狡讹拷
			vector.add(discridizeWeight(codeSimilarity));
			headerList.add("@attribute 'codeSimilarity' numeric");
			
			//32. 锟缴达拷锟斤拷途锟阶拷锟斤拷锟斤拷贫锟�
			vector.add(discridizeWeight(oldCodeOldCommentSimilarity));
			headerList.add("@attribute 'oldCodeOldCommentSimilarity' numeric");
			
			//33. 锟铰达拷锟斤拷途锟阶拷锟斤拷锟斤拷贫锟�
			vector.add(discridizeWeight(newCodeOldCommentSimilarity));
			headerList.add("@attribute 'newCodeOldCommentSimilarity' numeric");

			
			//34. 锟铰旧达拷锟斤拷锟斤拷注锟斤拷锟斤拷锟狡讹拷之锟斤拷
			vector.add(Math.abs(oldCodeOldCommentSimilarity-newCodeOldCommentSimilarity));
			headerList.add("@attribute 'subSimilarity' numeric");
			
			//35. 锟铰达拷锟斤拷锟斤拷锟斤拷锟接碉拷锟绞革拷锟斤拷
			vector.add(discridizeNum(addWordNumber));
			headerList.add("@attribute 'addWordNumber' numeric");
			
			//36. 锟铰达拷锟斤拷锟斤拷删锟斤拷锟斤拷锟绞革拷锟斤拷
			vector.add(discridizeNum(deleteWordNumber));			
			headerList.add("@attribute 'deleteWordNumber' numeric");

			FileUtils.writeLines(new File("file/commonhead.txt"), headerList);
			
			//37. ischange
//			vector.add(comment.isChange2()?1:0);
			
			return vector;
	}
	
	public static List<List<Integer>> combineVectors(List<List<Integer>> vectorList1,List<List<Integer>> vectorList2){
		List<List<Integer>> resultVectors = new ArrayList<List<Integer>>();
		if(vectorList1.size()!=vectorList2.size()) {
			System.out.println("size not equal.");
			return new ArrayList<List<Integer>>();
			
		}
		for(int i=0,n=vectorList1.size();i<n;i++) {
			List<Integer> vector1 = vectorList1.get(i);
			List<Integer> vector2 = vectorList2.get(i);
			if(!vector1.get(0).equals(vector2.get(0))) {
				System.out.println("id is not equal.");
				System.out.println("vector1:"+vector1.get(0)+" vector2:"+vector2.get(0));
				return new ArrayList<List<Integer>>();
			}
			
			List<Integer> vector = new ArrayList<Integer>();
			for(int j=0;j<vector1.size()-1;j++) {
				vector.add(vector1.get(j));
			}
			for(int j=1;j<vector2.size();j++) {
				vector.add(vector2.get(j));
			}
			resultVectors.add(vector);
		}
		
		return resultVectors;
	}
	
	public static void writeFile(List<List<Integer>> vectorList, String file) throws IOException {
		File dataFile = new File(file);
		List<String> fileLines = new ArrayList<String>();
		fileLines.add("@relation commentData");
		
		List<String> heads1 = new ArrayList<String>();
		heads1 = FileUtils.readLines(new File("file/commonhead.txt"),"UTF-8");
		
		List<String> heads2 = new ArrayList<String>();
		heads2 =FileUtils.readLines(new File("file/refactorhead.txt"),"UTF-8");
		

		fileLines.addAll(heads1);
//		fileLines.addAll(heads2);
		
		fileLines.add("@attribute 'class' {0,1}");
		fileLines.add("@data");

		for (int j = 0; j < vectorList.size(); j++) {
			List<Integer> vector = vectorList.get(j);
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < vector.size() - 1; i++) {
				buffer.append(vector.get(i) + " ");
			}
			buffer.append(vector.get(vector.size() - 1));
			fileLines.add(buffer.toString());
		}

		FileUtils.writeLines(dataFile, fileLines);

	}
	
	private static int discridizeNum(int num){
		if(num==0){
			return 0;
		}
		if(num==1){
			return 1;
		}
		if(num==2){
			return 2;
		}
		if(num==3){
			return 3;
		}
		if(num==4){
			return 4;
		}
		if(num==5){
			return 5;
		}
		if(num>5&&num<=10){
			return 6;
		}
		if(num>10&&num<=20){
			return 7;
		}
		if(num>20){
			return 8;
		}
		
		return 0;
		
	}
	
	private static int discridizeWeight(int num){
		if(num==0){
			return 0;
		}
		if(num>0&&num<=10){
			return 1;
		}
		if(num>10&&num<=20){
			return 2;
		}
		if(num>20&&num<=30){
			return 3;
		}
		if(num>30&&num<=40){
			return 4;
		}
		if(num>40&&num<=50){
			return 5;
		}
		if(num>50&&num<=60){
			return 6;
		}
		if(num>60&&num<=70){
			return 7;
		}
		if(num>70&&num<=80){
			return 8;
		}
		if(num>80&&num<=90){
			return 9;
		}
		if(num>90&&num<100){
			return 10;
		}
		if(num==100){
			return 11;
		}
		return 0;
	}
	
	public static List<List<Integer>> getVectorList4File(String path) throws IOException{
		List<List<Integer>> vectorList = new ArrayList<List<Integer>>();
		List<String> lines = FileUtils.readLines(new File(path),"UTF-8");
		for(String line:lines) {
			List<Integer> vector = new ArrayList<Integer>();
			String[] temps = line.split(" ");
			for(int i=0;i<temps.length;i++) {
				vector.add(Integer.parseInt(temps[i]));
			}
			vectorList.add(vector);
		}
		return vectorList;
	}
	
	public static void main(String[] args) throws IOException{
		DataSet.generateDataSet("file/data_1_2_5_6_3.csv","dataset");
//		List<List<Integer>> vectorList1 = getVectorList4File("dataset/data.txt");
//		List<List<Integer>> vectorList2 = getVectorList4File("dataset/refactorHibernate.txt");
//		List<List<Integer>> vectorList = combineVectors(vectorList1, vectorList2);
//		writeFile(vectorList, "file/data3.arff");
//		writeFile(vectorList1,"file/data.arff");
		
		
	}

}
