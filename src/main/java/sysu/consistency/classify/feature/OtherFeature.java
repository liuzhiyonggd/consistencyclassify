package sysu.consistency.classify.feature;

import java.util.LinkedHashMap;
import java.util.Map;

import sysu.consistency.db.bean.ClassMessage;
import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.bean.CommentWord;

public class OtherFeature implements Feature{

	public Map<String, Double> feature(CommentEntry comment, CommentWord commentWord, ClassMessage clazz) {
		Map<String,Double> features = new LinkedHashMap<String, Double>();
		
		
		
		return features;
		
	}

}
