package sysu.consistency.classify.data;

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
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.CommentWord;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentWordRepository;

public class WordSet {
	
	public static List<String> getDocumentSet() throws IOException {
		CommentWordRepository wordRepo = RepositoryFactory.getCommentWordRepository();
		List<String> documents = new ArrayList<String>();
		Set<String> frequencyWords = new HashSet<String>();
		List<String> frequencyFile = FileUtils.readLines(new File("E:\\注释一致性实验\\数据\\word.txt"),"UTF-8");
		frequencyWords.addAll(frequencyFile);
		for(int commentID=1;commentID<=48170;commentID++) {
			
			CommentWord commentWord = wordRepo.findASingleByCommentID(commentID);
			
			List<String> newCommentWords = filterWords(commentWord.getNewCommentWords(),frequencyWords);
			List<String> oldCommentWords = filterWords(commentWord.getOldCommentWords(),frequencyWords);
			List<String> newCodeWords = filterWords(commentWord.getNewCodeWords(),frequencyWords);
			List<String> oldCodeWords = filterWords(commentWord.getOldCodeWords(),frequencyWords);
			
			    String newCommentSentence = getEmbeddingSentence(newCommentWords, newCodeWords);
			    String newCodeSentence = getEmbeddingSentence( newCodeWords,newCommentWords);
			    documents.add(newCommentSentence);
			    documents.add(newCodeSentence);
			
			    String oldCommentSentence = getEmbeddingSentence(oldCommentWords, oldCodeWords);
			    String oldCodeSentence = getEmbeddingSentence(oldCodeWords,oldCommentWords);
			    documents.add(oldCommentSentence);
			    documents.add(oldCodeSentence);
			
		}
		return documents;
	}
	
	private static String getEmbeddingSentence(List<String> list1,List<String> list2){
		List<String> wordList = new ArrayList<String>();
		if(list2.size()<=0) {
			wordList = list1;
		}else {
		Random random = new Random();
		for(String str:list1) {
			int randomIndex1 = random.nextInt(list2.size());
			int randomIndex2 = random.nextInt(list2.size());
			wordList.add(str);
			wordList.add(list2.get(randomIndex1));
			wordList.add(list2.get(randomIndex2));
		}
		}
		
		StringBuilder sb = new StringBuilder();
		for(String str:wordList) {
			sb.append(str).append(" ");
		}
		return sb.toString();
	}
	
	private static List<String> filterWords(List<String> wordList,Set<String> dict){
		List<String> result = new ArrayList<String>();
		for(String str:wordList) {
			if(dict.contains(str)) {
				result.add(str);
			}
		}
		return result;
	}
	/**
	 * 词频统计
	 * @return
	 * @throws IOException
	 */
	public static Map<String,Integer> getWordFrequency() throws IOException{
		CommentWordRepository wordRepo = RepositoryFactory.getCommentWordRepository();
		Map<String,Integer> wordFrequency = new HashMap<String,Integer>();
		
		for(int commentID=1;commentID<=48170;commentID++) {
			if(commentID%1000==0) {
				System.out.println(commentID+" is done.");
			}
		    CommentWord commentWord = wordRepo.findASingleByCommentID(commentID);
		    List<String> newCommentList = commentWord.getNewCommentWords();
		    List<String> oldCommentList = commentWord.getOldCommentWords();
		    List<String> newCodeList = commentWord.getNewCodeWords();
		    List<String> oldCodeList = commentWord.getOldCodeWords();
		    List<String> tempList = new ArrayList<String>();
		    tempList.addAll(newCommentList);
		    tempList.addAll(oldCommentList);
		    tempList.addAll(newCodeList);
		    tempList.addAll(oldCodeList);
		    
		    for(String str:tempList) {
		    	if(wordFrequency.containsKey(str)) {
		    		wordFrequency.put(str, wordFrequency.get(str)+1);
		    	}else {
		    		wordFrequency.put(str, 1);
		    	}
		    }
		}
		List<Map.Entry<String,Integer>> list = new ArrayList<Map.Entry<String,Integer>>(wordFrequency.entrySet());
        //然后通过比较器来实现排序
        Collections.sort(list,new Comparator<Map.Entry<String,Integer>>() {
            //降序排序
            public int compare(Entry<String, Integer> o1,
                    Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
            
        });
        
        List<String> output = new ArrayList<String>();
        for(Map.Entry<String,Integer> mapping:list){ 
               output.add(mapping.getKey()+","+mapping.getValue()); 
          } 
        FileUtils.writeLines(new File("E:\\注释一致性实验\\数据\\wordfrequency.txt"), output);
		return wordFrequency;
	}
	
	public static void main(String[] args) throws IOException {
		List<String> documents = getDocumentSet();
		FileUtils.writeLines(new File("E:\\注释一致性实验\\数据\\documents2.txt"), documents);
	}

}
