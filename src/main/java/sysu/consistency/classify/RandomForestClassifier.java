package sysu.consistency.classify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import weka.attributeSelection.ASSearch;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.CostMatrix;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Debug.Random;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ConverterUtils.DataSink;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.supervised.attribute.Discretize;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.Standardize;

public class RandomForestClassifier {
	private RandomForest classifier;
	private Instances instancesTrain;
	private Instances instancesTest;
	private CostSensitiveClassifier costClassifier;
	
	List<String> output = new ArrayList<String>();
	public List<String> getOutput(){
		return output;
	}

	public RandomForestClassifier(String trainPath,String testPath) throws FileNotFoundException, Exception {
		
		
		File inputFile = new File(trainPath);
		ArffLoader atf = new ArffLoader();
		
		File testFile = new File(testPath);
		ArffLoader atf2 = new ArffLoader();
		
		try {
			atf.setFile(inputFile);
			instancesTrain = atf.getDataSet();
			instancesTrain.setClassIndex(instancesTrain.numAttributes() - 1);
			
			atf2.setFile(testFile);
			instancesTest = atf2.getDataSet();
			instancesTest.setClassIndex(instancesTest.numAttributes() - 1);
			
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("init error.\n"+e.getMessage());
		}
		
        classifier = new RandomForest();
        classifier.setClassifier(new J48());
//        classifier.setClassifier();
//        classifier.buildClassifier(instancesTrain);
		
		costClassifier = new CostSensitiveClassifier();
		CostMatrix matrix = new CostMatrix(new BufferedReader(new FileReader("file/matrix.txt")));
		matrix.applyCostMatrix(instancesTrain, new Random());
		
		System.out.println(matrix.toString());
		costClassifier.setCostMatrix(matrix);
		costClassifier.setClassifier(classifier);
		
	}
	
	public void run(int iterate,int p,int k,String testFile,String savename){
		init(iterate,p,k);
		removeID();
		train();
		getPrecision(testFile,savename);
	}

	public void init(int iterate,int percentage,int featuresNum) {
//		classifier.setBagSizePercent(percentage);
//		classifier.setNumFeatures(featuresNum);
		classifier.setNumIterations(iterate);
	}
	
	private void removeID(){
		Remove remove = new Remove();
//		String removeKeyword = "";
//		for(int i :removeList){
//			removeKeyword += ","+i;
//		}
//		output.add(removeKeyword);
//		System.out.println(removeKeyword);
		String[] options = new String[]{"-R","1,2"};
		try {
			remove.setOptions(options);
			remove.setInputFormat(instancesTrain);
			instancesTrain = Filter.useFilter(instancesTrain, remove);
			instancesTest = Filter.useFilter(instancesTest, remove);
		} catch (Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void selectAttribute(){
		InfoGainAttributeEval evaluator = new InfoGainAttributeEval();
		        ASSearch search = new Ranker();
		        AttributeSelection eval = null;
		
		        eval = new AttributeSelection();
		        eval.setEvaluator(evaluator);
		        eval.setSearch(search);
		        
		        try {
		        	eval.setInputFormat(instancesTrain);
					instancesTrain = Filter.useFilter(instancesTrain, eval);
					instancesTest = Filter.useFilter(instancesTest, eval);
				} catch (Exception e) {
					System.out.println("select error.\n"+e.getMessage());
				}
		        
		    }
	
	private void removeInstance(int radio){
		int num = 0;
		System.out.println("train instance num(before):"+instancesTrain.numInstances());
		for(int i=0,n=instancesTrain.numInstances();i<n;i++){
			Instance instance = instancesTrain.get(i);
			if(instance.classValue()==0d){
				num++;
				if(num%radio!=0){
					instancesTrain.remove(i);
					i--;
					n--;
				}
			}
		}
		System.out.println("train instance num(after):"+instancesTrain.numInstances());
	}
	private void standardize(){
		Standardize standardize = new Standardize();
		try {
			standardize.setInputFormat(instancesTrain);
			instancesTrain = Filter.useFilter(instancesTrain, standardize);
			
			DataSink.write("D:/work/5.9/strain.arff", instancesTrain);
			
			instancesTest = Filter.useFilter(instancesTest,standardize);
			DataSink.write("D:/work/5.9/stest.arff", instancesTest);
//			System.out.println(instancesTrain);
		} catch (Exception e) {
			System.out.println("standardize error.\n"+e.getMessage());
		}
	}

	private void smoteSample(){
		 SMOTE convert = new SMOTE();
	        int seed = (int) (Math.random() * 10);
	        String[] options = {"-S", String.valueOf(seed), "-P", "100.0", "-K", "5"};
	        try {
	            convert.setOptions(options);
	            convert.setInputFormat(instancesTrain);
	            instancesTrain = Filter.useFilter(instancesTrain, convert);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	}
	
	private void discretize(){
		Discretize discretize = new Discretize();
		int[] indicesArray = new int[instancesTrain.numAttributes()-1];
		for(int index=0;index<instancesTrain.numAttributes()-1;index++){
			indicesArray[index] = index;
		}
		
		discretize.setAttributeIndicesArray(indicesArray);
		try {
			discretize.setInputFormat(instancesTrain);
			instancesTrain = Filter.useFilter(instancesTrain, discretize);
			DataSink.write("D:/temp/discretizeTrain.arff", instancesTrain);
			instancesTest = Filter.useFilter(instancesTest, discretize);
			DataSink.write("D:/temp/discretizeTest.arff", instancesTest);
			System.out.println(instancesTrain);
		} catch (Exception e) {
			System.out.println("discretize error.\n"+e.getMessage());
		}
		
	}
	
	private void train() {
		try {
			costClassifier.buildClassifier(instancesTrain);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("train error.\n"+e.getMessage());
		}
	}
	
	private void evaluate(){
		Evaluation eval;
		try {
			eval = new Evaluation(instancesTrain);
			eval.evaluateModel(costClassifier, instancesTest);
			System.out.println(eval.toSummaryString("\nResults\n======\n",false));
			output.add(eval.toSummaryString("\nResults\n======\n",false));
			System.out.println("0 recall:"+eval.recall(0));
			System.out.println("0 predition:"+eval.precision(0));
			System.out.println("1 recall:"+eval.recall(1));
			System.out.println("1 predition:"+eval.precision(1));
		} catch (Exception e) {
			System.out.println("evaluate error.\n"+e.getMessage());
		}
		
	}

	public List<Double> classify(String testPath) {

		List<Double> result = new ArrayList<Double>();
		File testFile = new File(testPath);

		ArffLoader atf = new ArffLoader();

		try {
			atf.setFile(testFile);
			Instances instancesTest = atf.getDataSet();
			
			int num = instancesTest.numInstances();
			for (int i = 0; i < num; i++) {
				Double d = costClassifier.classifyInstance(instancesTest.instance(i));
				result.add(d);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("C45Classifier 锟斤拷锟斤拷实锟斤拷锟斤拷锟斤拷锟斤拷锟缴癸拷.");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("C45Classifier 锟斤拷锟斤拷失锟斤拷.");
		}

		return result;

	}

	public double getPrecision(String testPath,String savename) {
		File testFile = new File(testPath);
		ArffLoader atf = new ArffLoader();
		
		List<Integer> commentIDList = new ArrayList<Integer>();
		List<String> false1CommentIDList = new ArrayList<String>();
		List<String> false0CommentIDList = new ArrayList<String>();
		double right = 0.0d;
		double result = 0.0d;
		int right_0 = 0;
		int right_1 = 0;
		int class_1 = 0;
		int class_0 = 0;
		int false_1 = 0;
		int false_0 = 0;
		try {
			atf.setFile(testFile);
			Instances instancesTest2 = atf.getDataSet();
			for(int i=0,n=instancesTest2.numInstances();i<n;i++){
				commentIDList.add((int)instancesTest2.get(i).value(0));
			}
			instancesTest2.deleteAttributeAt(0);
			instancesTest2.deleteAttributeAt(0);
			instancesTest2.setClassIndex(instancesTest2.numAttributes() - 1);
			int num = instancesTest2.numInstances();

			for(int i=0;i<num;i++){
//				System.out.println("classify:"+classifier.classifyInstance(instancesTest.instance(i))+" classValue:"+instancesTest.instance(i).classValue());
				double classifyValue = costClassifier.classifyInstance(instancesTest2.instance(i));
				double classValue = instancesTest2.instance(i).classValue();
				int commentID = commentIDList.get(i);
				if(classValue==1.0d){
					class_1++;
				}else{
					class_0++;
				}
				if((classifyValue>=0.5&&classValue==1.0d)||(classifyValue<0.5&&classValue==0.0d))// 锟斤拷锟皆わ拷锟街碉拷痛锟街碉拷锟饺ｏ拷锟斤拷锟斤拷锟斤拷锟斤拷锟叫的凤拷锟斤拷锟斤拷锟结供锟斤拷锟斤拷为锟斤拷确锟金案ｏ拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷澹�
	            {
	              right++;//锟斤拷确值锟斤拷1
	              if(classValue==1.0d){
	            	  right_1++;
	              }else{
	            	  right_0++;
	              }
	            }else{
	            	
	            	
	            	
	            	if(classValue==1.0d){
	            		false_0++;
	            		false0CommentIDList.add(commentID+","+classifyValue);
	            	}else{
	            		false_1++;
//	            		if(instancesTest.instance(i).value(1)==1.0d){
	            			false1CommentIDList.add(commentID+","+classifyValue);
//	            		}
	            	}
	            }
			}
			result = right/num;
			System.out.println("total:"+num);
			output.add("total:"+num);
			System.out.println("class_1:"+class_1);
			output.add("class_1:"+class_1);
			System.out.println("class_0:"+class_0);
			output.add("class_0:"+class_0);
			System.out.println("right_1:"+right_1);
			output.add("right_1:"+right_1);
			System.out.println("right_0:"+right_0);
			output.add("right_0:"+right_0);
			System.out.println("false_1:"+false_1);
			output.add("false_1:"+false_1);
			System.out.println("false_0:"+false_0);
			output.add("false_0:"+false_0);
			
			System.out.println("class_1 recall:"+right_1*1.0d/class_1);
			output.add("class_1 recall:"+right_1*1.0d/class_1);
			System.out.println("class_0 recall:"+right_0*1.0d/class_0);
			output.add("class_0 recall:"+right_0*1.0d/class_0);
			System.out.println("class_1 precision:"+right_1*1.0d/(right_1+false_1));
			output.add("class_1 precision:"+right_1*1.0d/(right_1+false_1));
			System.out.println("class_0 precision:"+right_0*1.0d/(right_0+false_0));
			output.add("class_0 precision:"+right_0*1.0d/(right_0+false_0));
			
			System.out.println("class_1 F1:"+2*right_1*1.0d/class_1*right_1*1.0d/(right_1+false_1)/(right_1*1.0d/class_1+right_1*1.0d/(right_1+false_1)));
			output.add("class_1 F1:"+2*right_1*1.0d/class_1*right_1*1.0d/(right_1+false_1)/(right_1*1.0d/class_1+right_1*1.0d/(right_1+false_1)));
			System.out.println("class_0 F1:"+2*right_0*1.0d/class_0*right_0*1.0d/(right_0+false_0)/(right_0*1.0d/class_0+right_0*1.0d/(right_0+false_0)));
			output.add("class_0 F1:"+2*right_0*1.0d/class_0*right_0*1.0d/(right_0+false_0)/(right_0*1.0d/class_0+right_0*1.0d/(right_0+false_0)));
			System.out.println(false1CommentIDList.size());
			FileUtils.writeLines(new File("file/result/false1_"+savename+".csv"), false1CommentIDList);
			FileUtils.writeLines(new File("file/result/false0_"+savename+".csv"), false0CommentIDList);
			FileUtils.writeLines(new File("file/result/"+savename+".txt"), output);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("C45Classifier 锟斤拷锟斤拷实锟斤拷锟斤拷锟斤拷锟斤拷锟缴癸拷.");
		} catch (Exception e) {
			// 

			e.printStackTrace();
			System.out.println("C45Classifier 锟斤拷锟斤拷失锟斤拷.");
		}
		return result;
	}
	

	public static void main(String[] args) throws Exception {
		
		RandomForestClassifier rf = new RandomForestClassifier("file/train.arff","file/verify.arff");
		rf.run(100, 100, 10, "file/verify.arff","result2" );
		
		
		
//		RandomForestClassifier rs = new RandomForestClassifier("d:/temp/jhotdraw_lineTrain.arff","d:/temp/jhotdraw_lineTest.arff");
//		System.out.println("jhotdraw:========================");
//		rs.run();
//		RandomForestClassifier rs2 = new RandomForestClassifier("d:/temp/jedit_lineTrain.arff","d:/temp/jedit_lineTest.arff");
//		System.out.println("jedit:========================");
//		rs2.run();
//		RandomForestClassifier rs3 = new RandomForestClassifier("d:/temp/htmlunit_lineTrain.arff","d:/temp/htmlunit_lineTest.arff");
//		System.out.println("htmlunit:========================");
//		rs3.run();
		
		
//		Questionare4.main(args);

	}

}
