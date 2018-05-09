package sysu.consistency.classify.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import sysu.consistency.classify.feature.CombineFeature;
import sysu.consistency.classify.feature.DiffFeature;
import sysu.consistency.classify.feature.Feature;
import sysu.consistency.classify.feature.OtherFeature;
import sysu.consistency.classify.feature.TextualFeature;
import sysu.consistency.db.bean.ClassMessage;
import sysu.consistency.db.bean.CommentEntry;
import sysu.consistency.db.bean.CommentWord;
import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.ClassMessageRepository;
import sysu.consistency.db.repository.CommentRepository;
import sysu.consistency.db.repository.CommentWordRepository;

public class NewDataSet {

	public static List<Map<String, Double>> getDatas(String idFile) {
		List<Map<String, Double>> datas = new ArrayList<Map<String, Double>>();
		
		List<Feature> featureDetectList = new ArrayList<Feature>();
		featureDetectList.add(new TextualFeature());
		featureDetectList.add(new DiffFeature());
		featureDetectList.add(new CombineFeature());
		featureDetectList.add(new OtherFeature());
		
		CommentRepository commentRepository = RepositoryFactory.getCommentRepository();
		ClassMessageRepository classRepository = RepositoryFactory.getClassRepository();
		CommentWordRepository commentWordRepository = RepositoryFactory.getCommentWordRepository();
		
		List<String> idList = new ArrayList<String>();
		try {
			idList = FileUtils.readLines(new File(idFile), "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<String> labelFile = new ArrayList<String>();
		try {
			// 提前生成好label标签，作用域检测前为label.csv 文件，检测后为label2.csv 文件
			labelFile = FileUtils.readLines(new File("dataset/label.csv"),"UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<Integer,Double> labelMap = new HashMap<Integer,Double>();
		for(String str:labelFile) {
			String[] temps = str.split(",");
			int commentID = Integer.parseInt(temps[0]);
			Double label = Double.parseDouble(temps[1]);
			labelMap.put(commentID, label);
		}
		
		int count = 0;
		for (int index=0;index<idList.size();index++) {
			String id = idList.get(index);
			int i = Integer.parseInt(id);
			
			count++;
			if(count%1000==0) {
			    System.out.println(i + " is done.");
			}
			CommentEntry comment = commentRepository.findASingleByCommentID(i);
			ClassMessage clazz = classRepository.findASingleByProjectAndCommitIDAndClassName(comment.getProject(),comment.getCommitID(),comment.getClassName());
			CommentWord commentWord = commentWordRepository.findASingleByCommentID(i);
			Map<String,Double> features = new LinkedHashMap<String,Double>();
			features.put("comment_id", comment.getCommentID()*1.0d);
			for (Feature featureDetect : featureDetectList) {
				for (Map.Entry<String, Double> entry : featureDetect.feature(comment, commentWord, clazz).entrySet()) {
					features.put(entry.getKey(), entry.getValue());
				}
			}
			features.put("class", labelMap.get(comment.getCommentID()));
			datas.add(features);

		}
		return datas;
	}
	
	public static void writeFile(List<Map<String,Double>> datas,String savePath) {
		List<String> header = new ArrayList<String>();
		List<String> vectors = new ArrayList<String>();
		header.add("@relation comment_consistency");
		for(Map.Entry<String, Double> feature:datas.get(0).entrySet()) {
			header.add("@attribute '"+feature.getKey()+"' numeric");
		}
		header.add("@data");
		
		for(Map<String,Double> features:datas) {
			StringBuilder sb = new StringBuilder();
			for(Map.Entry<String, Double> feature:features.entrySet()) {
				sb.append(feature.getValue()).append(" ");
			}
			vectors.add(sb.toString());
		}
		
		List<String> output = new ArrayList<String>();
		output.addAll(header);
		output.addAll(vectors);
		try {
			FileUtils.writeLines(new File(savePath), output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		writeFile(getDatas("dataset/commentIDList.txt"),"dataset/beforeScopeData.arff");
	}
}

