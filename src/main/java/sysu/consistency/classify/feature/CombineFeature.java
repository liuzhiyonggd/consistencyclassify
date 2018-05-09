package sysu.consistency.classify.feature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.ClassMessage;
import sysu.consistency.db.bean.CodeComment;
import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.bean.CommentWord;
import sysu.consistency.db.bean.DiffType;
import sysu.consistency.db.bean.Line;
import sysu.consistency.tools.splitword.WordSpliter;

public class CombineFeature implements Feature {

	private static Map<String, Integer> wordIndexMap;
	private static Map<String, List<Double>> wordVectorMap;

	static {
		// 读取词向量
		List<String> lines = new ArrayList<String>();
		try {
			lines = FileUtils.readLines(new File("f:/file/wordVector_50.txt"), "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		wordVectorMap = new HashMap<String, List<Double>>();
		wordIndexMap = new HashMap<String, Integer>();
		int index = 0;
		for (String str : lines) {
			String word = str.split(",")[0];
			List<Double> vector = new ArrayList<Double>();

			wordIndexMap.put(word, index);
			index++;

			for (String str2 : str.split(",")[1].split(" ")) {
				vector.add(Double.parseDouble(str2));
			}
			wordVectorMap.put(word, vector);
		}
	}

	public Map<String, Double> feature2(CommentWord commentWord) {
		Map<String, Double> features = new LinkedHashMap<String, Double>();

		List<String> commentSentence = commentWord.getOldCommentWords();
		List<String> oldCodeSentence = commentWord.getOldCodeWords();
		List<String> newCodeSentence = commentWord.getNewCodeWords();

		// 旧注释和旧代码的相似度
		double oldCodeCommentSim = (computeSentenceSimilarity(commentSentence, oldCodeSentence)
				+ computeSentenceSimilarity(oldCodeSentence, commentSentence)) / 2;
		features.put("oldcode_comment_sim", oldCodeCommentSim);

		// 旧注释和新代码的相似度
		double newCodeCommentSim = (computeSentenceSimilarity(commentSentence, newCodeSentence)
				+ computeSentenceSimilarity(newCodeSentence, commentSentence)) / 2;
		features.put("newcode_comment_sim", newCodeCommentSim);

		// sub
		features.put("sub_code_comment_sim", oldCodeCommentSim - newCodeCommentSim);
		return features;
	}

	public Map<String, Double> feature(CommentEntry comment, CommentWord commentWord, ClassMessage clazz) {

		Map<String, Double> features = new LinkedHashMap<String, Double>();

		int commentStartLine = comment.getOld_comment_startLine();
		int commentEndLine = comment.getOld_scope_endLine();

		List<CodeComment> classOldCommentList = clazz.getOldComment();
		List<Line> classOldCodeList = clazz.getOldCode();
		List<Line> classNewCodeList = clazz.getNewCode();

		List<String> commentList = new ArrayList<String>();
		Set<Integer> oldCommentLineSet = new LinkedHashSet<Integer>();
		for (CodeComment codeComment : classOldCommentList) {
			if (codeComment.getStartLine() >= commentStartLine && codeComment.getEndLine() <= commentEndLine) {
				for (int i = codeComment.getStartLine(); i <= codeComment.getEndLine(); i++) {
					commentList.add(classOldCodeList.get(i - 1).getLine());
					oldCommentLineSet.add(i);
				}
			}
		}
		List<String> commentSentence = new ArrayList<String>();
		for (String str : commentList) {
			commentSentence.addAll(WordSpliter.split(str));
		}

		List<String> newDiffList = new ArrayList<String>();
		List<String> oldDiffList = new ArrayList<String>();
		Set<Integer> newDiffLineSet = new LinkedHashSet<Integer>();
		Set<Integer> oldDiffLineSet = new LinkedHashSet<Integer>();
		for (DiffType diff : comment.getDiffList()) {
			if (diff.getType().equals("STATEMENT_INSERT") || diff.getType().equals("STATEMENT_UPDATE")
					|| diff.getType().equals("STATEMENT_DELETE") || diff.getType().equals("CONDITION_EXPRESSION_CHANGE")
					|| diff.getType().equals("ALTERNATIVE_PART_INSERT")
					|| diff.getType().equals("ALTERNATIVE_PART_DELETE")) {

				if (diff.getNewStartLine() > 0) {
					for (int i = diff.getNewStartLine(); i <= diff.getNewEndLine(); i++) {
						newDiffLineSet.add(i);
					}
				}
				if (diff.getOldStartLine() > 0) {
					for (int i = diff.getOldStartLine(); i <= diff.getOldEndLine(); i++) {
						oldDiffLineSet.add(i);
					}
				}
			}
		}

		for (Integer line : newDiffLineSet) {
			newDiffList.add(classNewCodeList.get(line - 1).getLine());
		}
		List<String> newDiffSentence = new ArrayList<String>();
		for (String str : newDiffList) {
			newDiffSentence.addAll(WordSpliter.split(str));
		}

		for (Integer line : oldDiffLineSet) {
			oldDiffList.add(classOldCodeList.get(line - 1).getLine());
		}
		List<String> oldDiffSentence = new ArrayList<String>();
		for (String str : oldDiffList) {
			oldDiffSentence.addAll(WordSpliter.split(str));
		}

		Map<String, Double> featureMap2 = feature2(commentWord);
		features.putAll(featureMap2);

		// 旧注释和旧变化代码的相似度
		double oldDiffCommentSim = (computeSentenceSimilarity(commentSentence, oldDiffSentence)
				+ computeSentenceSimilarity(oldDiffSentence, commentSentence)) / 2;
		features.put("olddiff_comment_sim", oldDiffCommentSim);

		// 旧注释和新变化代码的相似度
		double newDiffCommentSim = (computeSentenceSimilarity(commentSentence, newDiffSentence)
				+ computeSentenceSimilarity(newDiffSentence, commentSentence)) / 2;
		features.put("newdiff_comment_sim", newDiffCommentSim);

		// sub
		features.put("sub_diff_comment_sim", oldDiffCommentSim - newDiffCommentSim);

		// 变化前和变化后的相似度
		double diffSim = (computeSentenceSimilarity(oldDiffSentence, newDiffSentence)
				+ computeSentenceSimilarity(newDiffSentence, oldDiffSentence)) / 2;
		features.put("diff_sim", diffSim);
		return features;
	}

	private double computeVectorSimilarity(String word1, String word2) {

		if (!wordIndexMap.containsKey(word1) || !wordIndexMap.containsKey(word2)) {
			return 0d;
		}
		List<Double> vector1 = wordVectorMap.get(word1);
		List<Double> vector2 = wordVectorMap.get(word2);

		double multiply = 0;
		double vector1Mod = 0;
		double vector2Mod = 0;
		for (int i = 0; i < vector1.size(); i++) {
			multiply += vector1.get(i) * vector2.get(i);
			vector1Mod += vector1.get(i) * vector1.get(i);
			vector2Mod += vector2.get(i) * vector2.get(i);
		}
		vector1Mod = Math.sqrt(vector1Mod);
		vector2Mod = Math.sqrt(vector2Mod);

		if (vector1Mod > 0 && vector2Mod > 0) {
			return multiply / (vector1Mod * vector2Mod);

		} else {
			return 0;
		}

	}

	private double computeSentenceSimilarity(List<String> sentence1, List<String> sentence2) {

		if (sentence1.size() == 0 || sentence2.size() == 0) {
			return 0d;
		}

		double sim = 0d;
		for (String str : sentence1) {

			double maxSim = 0.0d;
			for (String str2 : sentence2) {
				if (str.equals(str2)) {
					maxSim = 1.0d;
					break;
				}
				double tmp = computeVectorSimilarity(str, str2);

				if (tmp > maxSim) {
					maxSim = tmp;
				}
			}
			sim += maxSim;
		}
		sim = sim / sentence1.size();
		return sim;
	}

}
