package sysu.consistency.classify.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class CombineFeatures {
	
	public static void combine(String file1,String file2,String file3,String file4,String saveFile) throws IOException {
		List<String> file1Lines = FileUtils.readLines(new File(file1),"UTF-8");
		List<String> file2Lines = FileUtils.readLines(new File(file2),"UTF-8");
		List<String> file3Lines = FileUtils.readLines(new File(file3),"UTF-8");
		List<String> file4Lines = FileUtils.readLines(new File(file4),"UTF-8");
		List<String> datas = new ArrayList<String>();
		
		int index1 = 0;
		for(String str:file1Lines) {
			index1++;
			if(str.equals("@data")) {
				break;
			}
		}
		
		for(int i=0;i<index1-2;i++) {
			datas.add(file1Lines.get(i));
		}
		
		for(int i=1;i<10;i++) {
			datas.add("@attribute 'refactor_"+i+"' numeric");
		}
		
		datas.add("@attribute 'old_diff_comment_sim' numeric");
		datas.add("@attribute 'new_diff_comment_sim' numeric");
		datas.add("@attribute 'sub_diff_comment_sim' numeric");
		datas.add("@attribute 'diff_sim' numeric");
		datas.add("@attribute 'old_code_comment_sim' numeric");
		datas.add("@attribute 'new_code_comment_sim' numeric");
		datas.add("@attribute 'sub_code_comment_sim' numeric");
		
		datas.add(file1Lines.get(index1-2));
		datas.add(file1Lines.get(index1-1));
		
		int index2 = 0;
		for(;index1<file1Lines.size();index1++) {
			String[] features1 = file1Lines.get(index1).split(" ");
			String[] features2 = file2Lines.get(index2).split(" ");
            String[] features3 = file3Lines.get(index2).split(" ");
            String[] features4 = file4Lines.get(index2).split(" ");
			index2++;
			
			List<String> features = new ArrayList<String>();
			for(int i=0;i<features1.length-1;i++) {
				features.add(features1[i]);
			}
			for(int i=1;i<features2.length-1;i++) {
				features.add(features2[i]);
			}
			for(int i=1;i<features3.length;i++) {
				features.add(features3[i]);
			}
			for(int i=1;i<features4.length;i++) {
				features.add(features4[i]);
			}
			
			features.add(features1[features1.length-1]);
			StringBuilder sb = new StringBuilder();
			for(String str:features) {
				sb.append(str).append(" ");
			}
			datas.add(sb.toString());
		}
		FileUtils.writeLines(new File(saveFile), datas);
	}
	
	public static void main(String[] args) throws IOException {
		combine("E:\\注释一致性实验\\数据\\old_data\\2\\alltest_ordinal.arff", "E:\\注释一致性实验\\数据\\old_data\\2\\refactoring_alltest.txt","E:\\注释一致性实验\\数据\\old_data\\2\\alltest_discredize_sim.txt","E:\\注释一致性实验\\数据\\old_data\\2\\alltest_discredize_sim2.txt", "E:\\注释一致性实验\\数据\\old_data\\2\\alltest.arff");
//		combine("E:\\注释一致性实验\\数据\\old_data\\1\\train2_ordinal.arff", "E:\\注释一致性实验\\数据\\old_data\\1\\refactoring_train.txt","E:\\注释一致性实验\\数据\\old_data\\1\\train_discredize_sim.txt","E:\\注释一致性实验\\数据\\old_data\\1\\train_discredize_sim2.txt", "E:\\注释一致性实验\\数据\\old_data\\1\\train.arff");	
	}

}
