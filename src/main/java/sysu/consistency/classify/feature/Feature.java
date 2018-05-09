package sysu.consistency.classify.feature;

import java.util.Map;

import sysu.consistency.db.bean.ClassMessage;
import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.bean.CommentWord;

public interface Feature {
	Map<String,Double> feature(CommentEntry comment,CommentWord commentWord,ClassMessage clazz);
}
