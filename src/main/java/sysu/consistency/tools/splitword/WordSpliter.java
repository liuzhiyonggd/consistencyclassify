package sysu.consistency.tools.splitword;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;

public class WordSpliter {
	
	private static WordnetStemmer stemmer = null;
	
	static {
		//WordNet ����ȡ�ʸ�
				String wnhome = "C:/Program Files (x86)/WordNet/2.1"; //��ȡ��������WNHOME
			    String path = wnhome + File.separator+ "dict";
			    URL url = null;
				try {
					url = new URL("file", null, path);
				} catch (MalformedURLException e) {
					System.out.println("wordnet url is error.");
				}  //����һ��URL����ָ��WordNet��ditcĿ¼
				if(url!=null){
					IDictionary dict=new Dictionary(url);
					try {
						dict.open();
					} catch (IOException e) {
						System.out.println("dictionary create is error.");
					} //�򿪴ʵ�
					stemmer = new WordnetStemmer(dict);
				}
	}
	
	public static List<String> split(String sentense){
		
		//��comment1 �� comment2 ���зִ�
//		Pattern pattern = Pattern.compile("\\.\\s*|\\s+|,\\s*|/\\*|\\*/|\\*");
//		String[] words = pattern.split(sentense);
		
		String splitToken = " .,;:/&|`~%+=-*<>$#@!^\\()[]{}''\"\r\n\t";
		StringTokenizer st = new StringTokenizer(sentense,splitToken,false);
		List<String> wordList = new ArrayList<String>();
		
		//�Էִʺõ��ַ������дʸ���ȡ
		while(st.hasMoreTokens()){
			String str = st.nextToken();
			
			char[] charArray = str.toCharArray();
			List<String> tempWordList = new ArrayList<String>();
			int startIndex = 0;
			for(int i=0,n=charArray.length;i<n;i++){
				if(charArray[i]>=65&&charArray[i]<=90&&i-1>=0&&(charArray[i-1]<65||charArray[i-1]>90)&&i>0){
					String word = String.copyValueOf(charArray,startIndex,i-startIndex).toLowerCase();
					startIndex = i;
					tempWordList.add(word);
				}
			}
			tempWordList.add(String.copyValueOf(charArray, startIndex, charArray.length-startIndex).toLowerCase());
			for(String str2:tempWordList){
				
				//ȥ��ͣ�ô�
//				if(Stopwords.isStopword(str2)){
//					continue;
//				}
				
				//���˷Ƿ��ַ�
				Pattern pattern = Pattern.compile("[0-9a-zA-Z]+");
				Matcher matcher = pattern.matcher(str2);
				if(!matcher.matches()) {
					continue;
				}
				
				List<String> stemList = null;
				try{
					stemList = stemmer.findStems(str2, POS.NOUN);
				}catch(Exception e){
					stemList = null;
				}
				if(stemList==null||stemList.isEmpty()||stemList.get(0).equals(str2)){
					try{
						stemList = stemmer.findStems(str2, POS.VERB);
					}catch(Exception e){
						stemList = null;
					}
				}
				if(stemList==null||stemList.isEmpty()){
					wordList.add(str2);
				}else{
					wordList.addAll(stemList);
				}
			}
		}
		
		return wordList;
	}
	
	public static void main(String[] args){
//		try {
//			List<String> test = FileUtils.readLines(new File("d:/stringtest.txt"));
//			for(String str:test){
//				List<String> words = WordSpliter.split(str);
//				for(String word:words){
//					System.out.print(word+"_");
//				}
//				System.out.println();
//			}
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		String str = "fill log configuration data with values from LogEntry constants. Default is true for all events";
		List<String> tempWordList = WordSpliter.split(str);
		for(String word:tempWordList){
			System.out.print("\""+word+"\",");
		}
	}
}
	
