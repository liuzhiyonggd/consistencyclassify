package sysu.consistency.classify.feature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.bean.ClassMessage;
import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.bean.CommentWord;
import sysu.consistency.db.bean.DiffType;
import sysu.consistency.db.bean.Token;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentRepository;

public class DiffFeature implements Feature {

	public Map<String, Double> feature(CommentEntry comment, CommentWord commentWord, ClassMessage clazz) {

		Map<String, Double> features = new LinkedHashMap<String, Double>();

		features.put("diff_size", comment.getDiffList().size() * 1.0d);

		String[] diffTypeFeatures = new String[] { "STATEMENT_INSERT", "STATEMENT_DELETE", "STATEMENT_UPDATE",
				"STATEMENT_PARENT_CHANGE", "STATEMENT_ORDERING_CHANGE",
				// "ADDITIONAL_FUNCTIONALITY",
				"CONDITION_EXPRESSION_CHANGE",
				// "ADDITIONAL_OBJECT_STATE",
				// "REMOVED_FUNCTIONALITY",
				// "REMOVED_OBJECT_STATE",
				// "ALTERNATIVE_PART_INSERT",
				// "PARAMETER_INSERT",
				// "ALTERNATIVE_PART_DELETE",
				// "METHOD_RENAMING",
				// "PARAMETER_DELETE",
				// "PARAMETER_TYPE_CHANGE",
				"RETURN_TYPE_CHANGE",
				// "DECREASING_ACCESSIBILITY_CHANGE",
				// "INCREASING_ACCESSIBILITY_CHANGE",
				// "ATTRIBUTE_RENAMING",
				// "ATTRIBUTE_TYPE_CHANGE",
				// "PARAMETER_RENAMING",
				// "REMOVING_ATTRIBUTE_MODIFIABILITY",
				// "PARENT_INTERFACE_INSERT",
				// "ADDITIONAL_CLASS",
				// "PARENT_CLASS_CHANGE",
				// "RETURN_TYPE_DELETE",
				// "PARENT_INTERFACE_DELETE",
				// "REMOVED_CLASS",
				// "RETURN_TYPE_INSERT",
				// "PARAMETER_ORDERING_CHANGE",
				// "PARENT_CLASS_DELETE",
				// "ADDING_METHOD_OVERRIDABILITY",
				// "PARENT_CLASS_INSERT",
				// "ADDING_ATTRIBUTE_MODIFIABILITY",
				// "REMOVING_METHOD_OVERRIDABILITY",
				// "PARENT_INTERFACE_CHANGE",
				// "CLASS_RENAMING",
				// "REMOVING_CLASS_DERIVABILITY",
				// "ADDING_CLASS_DERIVABILITY",
				// "UNCLASSIFIED_CHANGE",

				// detail type
				"IfStatement_Insert", "IfStatement_Update", "IfStatement_Delete", "WhileStatement_Insert",
				"WhileStatement_Update", "WhileStatement_Delete", "ForStatement_Insert", "ForStatement_Update",
				"ForStatement_Delete", "ReturnStatement_Insert", "ReturnStatement_Update", "ReturnStatement_Delete",
				"AssertStatement_Insert", "AssertStatement_Update", "AssertStatement_Delete", "LogStatement_Insert",
				"LogStatement_Update", "LogStatement_Delete"

		};

		for (String str : diffTypeFeatures) {
			features.put(str, 0.0d);
		}

		List<DiffType> diffList = comment.getDiffList();
		for (DiffType diff : diffList) {
			if (features.containsKey(diff.getType())) {
				features.put(diff.getType(), features.get(diff.getType()) + 1);
			}
		}

		for (DiffType diff : diffList) {
			if (diff.getType().indexOf("INSERT") > 0) {
				for (Token token : comment.getNewToken()) {
					if (token.getStartLine() >= diff.getNewStartLine() && token.getEndLine() <= diff.getNewEndLine()) {
						if (token.getTokenName().equals("IfStatement")) {
							features.put("IfStatement_Insert", features.get("IfStatement_Insert") + 1);
						}
						if (token.getTokenName().equals("ForStatement")) {
							features.put("ForStatement_Insert", features.get("ForStatement_Insert") + 1);
						}
						if (token.getTokenName().equals("WhileStatement")) {
							features.put("WhileStatement_Insert", features.get("WhileStatement_Insert") + 1);
						}
						if (token.getTokenName().equals("ReturnStatement")) {
							features.put("ReturnStatement_Insert", features.get("ReturnStatement_Insert") + 1);
						}
						if (token.getKeyword() != null && token.getKeyword().toLowerCase().contains("assert")) {
							features.put("AssertStatement_Insert", features.get("AssertStatement_Insert") + 1);
						}
						if (token.getKeyword() != null && token.getKeyword().toLowerCase().contains("log")) {
							features.put("LogStatement_Insert", features.get("LogStatement_Insert") + 1);
						}
					}
				}
			}
			if (diff.getType().indexOf("DELETE") > 0) {
				for (Token token : comment.getOldToken()) {
					if (token.getStartLine() >= diff.getOldStartLine() && token.getEndLine() <= diff.getOldEndLine()) {
						if (token.getTokenName().equals("IfStatement")) {
							features.put("IfStatement_Delete", features.get("IfStatement_Delete") + 1);
						}
						if (token.getTokenName().equals("ForStatement")) {
							features.put("ForStatement_Delete", features.get("ForStatement_Delete") + 1);
						}
						if (token.getTokenName().equals("WhileStatement")) {
							features.put("WhileStatement_Delete", features.get("WhileStatement_Delete") + 1);
						}
						if (token.getTokenName().equals("ReturnStatement")) {
							features.put("ReturnStatement_Delete", features.get("ReturnStatement_Delete") + 1);
						}
						if (token.getKeyword() != null && token.getKeyword().toLowerCase().contains("assert")) {
							features.put("AssertStatement_Delete", features.get("AssertStatement_Delete") + 1);
						}
						if (token.getKeyword() != null && token.getKeyword().toLowerCase().contains("log")) {
							features.put("LogStatement_Delete", features.get("LogStatement_Delete") + 1);
						}
					}
				}
			}

			if (diff.getType().indexOf("UPDATE") > 0) {
				for (Token token : comment.getOldToken()) {
					if (token.getStartLine() >= diff.getOldStartLine() && token.getEndLine() <= diff.getOldEndLine()) {
						if (token.getTokenName().equals("IfStatement")) {
							features.put("IfStatement_Update", features.get("IfStatement_Update") + 1);
						}
						if (token.getTokenName().equals("ForStatement")) {
							features.put("ForStatement_Update", features.get("ForStatement_Update") + 1);
						}
						if (token.getTokenName().equals("WhileStatement")) {
							features.put("WhileStatement_Update", features.get("WhileStatement_Update") + 1);
						}
						if (token.getTokenName().equals("ReturnStatement")) {
							features.put("ReturnStatement_Update", features.get("ReturnStatement_Update") + 1);
						}
						if (token.getKeyword() != null && token.getKeyword().toLowerCase().contains("assert")) {
							features.put("AssertStatement_Update", features.get("AssertStatement_Update") + 1);
						}
						if (token.getKeyword() != null && token.getKeyword().toLowerCase().contains("log")) {
							features.put("LogStatement_Update", features.get("LogStatement_Update") + 1);
						}
					}
				}
			}
		}

		int firstDiffLocation = 0;
		for (DiffType diff : diffList) {
			if (diff.getType().equals("STATEMENT_INSERT") || diff.getType().equals("STATEMENT_UPDATE")
					|| diff.getType().equals("STATEMENT_DELETE")
					|| diff.getType().equals("CONDITION_EXPRESSION_CHANGE")) {
				if (diff.getNewStartLine() > 0) {
					firstDiffLocation = diff.getNewStartLine() - comment.getNew_comment_endLine();
				} else {
					firstDiffLocation = diff.getOldStartLine() - comment.getOld_comment_endLine();
				}
				break;
			}
		}

		Map<String, Double> weightFeatures = new LinkedHashMap<String, Double>();
		int diffSize = diffList.size();
		for (Map.Entry<String, Double> entry : features.entrySet()) {
			String key = entry.getKey() + "_Weight";
			Double value = entry.getValue() / (diffSize+0.01);
			weightFeatures.put(key, value);
		}
		features.putAll(weightFeatures);

		features.put("first_diff_location", firstDiffLocation * 1.0d);

		// token change
		double tokenChange = 0d;
		if (comment.getNewToken().size() != comment.getOldToken().size()) {
			tokenChange = 1d;
		} else {
			for (int i = 0; i < comment.getNewToken().size(); i++) {
				Token newToken = comment.getNewToken().get(i);
				Token oldToken = comment.getOldToken().get(i);
				if (!newToken.getTokenName().equals(oldToken.getTokenName())) {
					tokenChange = 1d;
					break;
				}
			}
		}
		features.put("tokenChange", tokenChange);
		
		return features;
	}

	public static void main(String[] args) throws IOException {
		CommentRepository commRepo = RepositoryFactory.getCommentRepository();

		List<Integer> commentIDList = new ArrayList<Integer>();
		List<String> file = FileUtils.readLines(new File("dataset/commentIDList.txt"), "UTF-8");

		for (int index = 0; index < file.size(); index++) {
			int commentID = Integer.parseInt(file.get(index));
			commentIDList.add(commentID);
		}

		List<String> output = new ArrayList<String>();
		int count = 0;
		for (int commentID : commentIDList) {

			count++;
			if (count % 1000 == 0) {
				System.out.println(count + " is done.");
			}

			CommentEntry comment = commRepo.findASingleByCommentID(commentID);
			DiffFeature diffFeature = new DiffFeature();
			Map<String, Double> features = diffFeature.feature(comment, null, null);
			String temp = commentID + " ";
			for (Map.Entry<String, Double> entry : features.entrySet()) {
				temp += entry.getValue() + " ";
			}
			output.add(temp);
		}
		FileUtils.writeLines(new File("dataset/filter_diffFeature.csv"), output);
	}

	// 生成comment id 列表
	// public static void main(String[] args) throws IOException {
	// List<String> file = FileUtils.readLines(new
	// File("dataset/data12-2.arff"),"UTF-8");
	//
	// List<String> output = new ArrayList<String>();
	//
	// int index = 0;
	// for(;index<file.size();index++) {
	// if(file.get(index).equals("@data")) {
	// index ++;
	// break;
	// }
	// }
	//
	// for(;index<file.size();index++) {
	// int commentID = Integer.parseInt(file.get(index).split(" ")[0]);
	// output.add(commentID+"");
	// }
	//
	// FileUtils.writeLines(new File("dataset/commentIDList.txt"), output);
	// }

}
