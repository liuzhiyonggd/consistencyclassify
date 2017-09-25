package sysu.consistency.datamining;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.bean.CommentWord;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentRepository;
import sysu.consistency.db.repository.CommentWordRepository;

public class WordMapping {
	
	public static void mapping(String type) throws IOException{
		Map<String,Integer> wordMap = new HashMap<String,Integer>();
		Map<String,Integer> changeWordMap = new HashMap<String,Integer>();
		CommentWordRepository commentWordRepo = RepositoryFactory.getCommentWordRepository();
		CommentRepository commentRepo = RepositoryFactory.getCommentRepository();
		String[] projects = new String[]{"jhotdraw","jedit","ejbca","htmlunit","freecol","kablink","jamwiki","opennms"};
		for(String project:projects){
			
			List<CommentWord> commentWordList = commentWordRepo.findByProject(project);
			for(CommentWord commentWord:commentWordList){
				
				//�ų����Լ���ֻ��ѵ���������ھ�
				if(commentWord.getCommentID()%3==0){
					continue;
				}
				
				CommentEntry comment = commentRepo.findASingleByCommentID(commentWord.getCommentID());
				if(comment.isChange2()){
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
				Set<String> wordSet = new HashSet<String>();
				for(String word:words){
					wordSet.add(word);
				}
				for(String word:wordSet){
					if(wordMap.containsKey(word)){
						wordMap.put(word, wordMap.get(word)+1);
						if(commentWord.isIschange()){
							changeWordMap.put(word, changeWordMap.get(word)+1);
						}
					}else{
						wordMap.put(word, 1);
						if(commentWord.isIschange()){
							changeWordMap.put(word,1);
						}else{
							changeWordMap.put(word,0);
						}
					}
				}
			}
			System.out.println(project+" is done.");
		}
		
		Comparator<Map.Entry<String, Integer>> comparator = new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> mapping1, Map.Entry<String, Integer> mapping2) {
				return mapping2.getValue().compareTo(mapping1.getValue());
			}
		};

		List<Map.Entry<String, Integer>> keywordMappingList = new ArrayList<Map.Entry<String, Integer>>(
				wordMap.entrySet());
		Collections.sort(keywordMappingList, comparator);
		
		List<String> dataLine = new ArrayList<String>();
		List<String> dataLine1 = new ArrayList<String>();
		
		for(Entry<String,Integer> entry:keywordMappingList){
			dataLine.add(entry.getKey()+","+entry.getValue()+","+changeWordMap.get(entry.getKey()));
			dataLine1.add(entry.getKey());
		}
		
		FileUtils.writeLines(new File("D:/work/4.8/"+type+"/wordstatistic.csv"), dataLine);
		FileUtils.writeLines(new File("D:/work/4.8/"+type+"/wordlist.csv"),dataLine1);

	}

}
