package sysu.consistency.datamining;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.CommentWord;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentWordRepository;

public class DataGenerator {
	
	public static void generate(String type) throws IOException{
		List<String> wordList = FileUtils.readLines(new File("D:/work/4.8/"+type+"/wordlist.csv"),"UTF-8");
		List<String> fileLines = new ArrayList<String>();
		
		CommentWordRepository commentWordRepo = RepositoryFactory.getCommentWordRepository();
		
		String[] projects = new String[]{"jhotdraw","jedit","ejbca","htmlunit","freecol","kablink","jamwiki","opennms"};
		for(String project:projects){
			
			List<CommentWord> commentWordList = commentWordRepo.findByProjectAndType(project, type);
			for(CommentWord commentWord:commentWordList){
				
				if(commentWord.getCommentID()%3==0){
					continue;
				}
				List<String> words = null;
				if(type.equals("oldcomment")){
					words = commentWord.getOldCommentWords();
				}
				if(type.equals("newcode")){
					words = commentWord.getNewCodeWords();
				}
				if(type.equals("oldcode")){
				    words = commentWord.getOldCodeWords();
				}
				StringBuffer sb = new StringBuffer();
				Set<String> wordSet = new HashSet<String>();
				for(String word:words){
					wordSet.add(word);
				}
				for(String word:wordSet){
					if(wordList.contains(word)){
						sb.append(wordList.indexOf(word)+" ");
					}
				}
				if(sb.toString().length()>0){
					fileLines.add(sb.toString());
				}
			}
			System.out.println(project +" is done.");
		}
		
		FileUtils.writeLines(new File("D:/work/4.8/"+type+"/data.txt"), fileLines);
	}

}
