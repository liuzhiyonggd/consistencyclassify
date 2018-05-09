package sysu.consistency.classify.feature;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import sysu.consistency.db.bean.ClassMessage;
import sysu.consistency.db.bean.CodeComment;
import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.bean.CommentWord;

public class TextualFeature implements Feature {

	public Map<String, Double> feature(CommentEntry comment, CommentWord commentWord, ClassMessage clazz) {
		Map<String, Double> features = new LinkedHashMap<String, Double>();

		features.put("old_comment_start_line", comment.getOld_comment_startLine() * 1.0d);

		features.put("old_comment_end_line", comment.getOld_comment_endLine() * 1.0d);

		features.put("new_code_line", comment.getNewCode().size() * 1.0d);

		features.put("old_code_line", comment.getOldCode().size() * 1.0d);

		features.put("sub_code_line", (comment.getNewCode().size() - comment.getOldCode().size()) * 1.0d);
		
		features.put("sub_code_line_weight", (comment.getNewCode().size() - comment.getOldCode().size()) * 1.0d/(comment.getNewCode().size()+0.01));

		features.put("new_scope_end_line", comment.getNew_scope_endLine() * 1.0d);

		features.put("old_scope_end_line", comment.getOld_scope_endLine() * 1.0d);

		features.put("old_comment_word_count", commentWord.getOldCommentWords().size() * 1.0d);

		int commentLines = 0;
		int classCommentLines = 0;
		List<CodeComment> commentList = clazz.getOldComment();
		for (CodeComment codeComment : commentList) {
			if (codeComment.getStartLine() >= comment.getOld_comment_startLine()
					&& codeComment.getEndLine() <= comment.getOld_scope_endLine()) {
				commentLines += codeComment.getEndLine() - codeComment.getStartLine() + 1;
			}
			classCommentLines += codeComment.getEndLine() - codeComment.getStartLine() + 1;
		}
		double commentDensity = commentLines * 1.0d
				/ (comment.getOld_scope_endLine() - comment.getOld_comment_startLine() + 1);
		features.put("comment_density", commentDensity);

		double classCommentDensity = classCommentLines * 1.0d / clazz.getOldCode().size();
		features.put("class_comment_density", classCommentDensity);

		// todo
		double todo = 0d;
		List<String> commentWordList = commentWord.getOldCommentWords();
		for (String str : commentWordList) {
			if (str.equals("todo") || str.equals("fixme") || str.equals("xxx")) {
				todo = 1.0d;
			}
		}
		features.put("todo", todo);

		return features;
	}

}
