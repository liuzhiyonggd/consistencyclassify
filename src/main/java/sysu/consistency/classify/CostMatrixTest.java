package sysu.consistency.classify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import weka.classifiers.CostMatrix;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Debug.Random;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class CostMatrixTest {

	public static void main(String[] args) throws FileNotFoundException, Exception {
		File inputFile = new File("file/train.arff");
		File testFile = new File("file/verify.arff");
		
		ArffLoader atf = new ArffLoader();
		atf.setFile(inputFile);
		Instances data = atf.getDataSet();
		data.setClassIndex(data.numAttributes() - 1);
		
		ArffLoader atf2 = new ArffLoader();
		atf2.setFile(testFile);
		Instances test = atf2.getDataSet();
		test.setClassIndex(test.numAttributes() - 1);

		CostMatrix matrix = new CostMatrix(new BufferedReader(new FileReader("file/matrix.txt")));
		Instances train = data;//matrix.applyCostMatrix(data, null);
		RandomForest rf = new RandomForest();
		rf.setNumIterations(100);
		rf.setBagSizePercent(100);
		rf.setNumFeatures(10);
//		rf.buildClassifier(train);
		CostSensitiveClassifier csc = new CostSensitiveClassifier();
		csc.setCostMatrix(matrix);
		csc.setClassifier(rf);
//		csc.buildClassifier(train);
		Evaluation eval = new Evaluation(train);
//		eval.evaluateModel(csc, test);
		eval.crossValidateModel(csc, train, 10, new Random(1L));
		System.out.println(eval.toMatrixString());
		System.out.println(eval.toSummaryString());
		
//		double totalClass0Precision = 0d;
//		double totalClass0Recall = 0d;
//		double totalClass1Precision = 0d;
//		double totalClass1Recall = 0d;
//		for(int i=0;i<10;i++) {
//			Instances train = data.trainCV(10,i);
////			train = matrix.applyCostMatrix(train, new Random(1));
//		    Instances test = data.testCV(10, i);
//		    RandomForest rf = new RandomForest();
//		    Evaluation eval = new Evaluation(test);
//		    rf.buildClassifier(train);
//		    eval.evaluateModel(rf, test);
//		    System.out.println(i+" ----------------------");
		    System.out.println("class 0 precision:"+eval.precision(0));
			System.out.println("class 0 recall:"+eval.recall(0));
			System.out.println("class 1 precision:"+eval.precision(1));
			System.out.println("class 1 recall:"+eval.recall(1));
//			
//			totalClass0Precision += eval.precision(0);
//			totalClass0Recall += eval.recall(0);
//			totalClass1Precision += eval.precision(1);
//			totalClass1Recall += eval.recall(1);
//		}
//		totalClass0Precision /= 10;
//		totalClass0Recall /= 10;
//		totalClass1Precision /= 10;
//		totalClass1Recall /= 10;
//		System.out.println("total class 0 Precision:"+totalClass0Precision);
//		System.out.println("total class 0 Recall:"+totalClass0Recall);
//		System.out.println("total class 1 Precision:"+totalClass1Precision);
//		System.out.println("total class 1 Recall:"+totalClass1Recall);
	}
	
//	public static void main(String[] args) throws Exception {
//		File inputFile = new File("file/test.arff");
//		
//		ArffLoader atf = new ArffLoader();
//		atf.setFile(inputFile);
//		Instances data = atf.getDataSet();
//		data.setClassIndex(data.numAttributes() - 1);
//		
//		System.out.println("ordinal data:");
//		for(int i = 0;i<data.numInstances();i++) {
//			System.out.println(data.instance(i));
//		}
//		
//		CostMatrix matrix = new CostMatrix(new BufferedReader(new FileReader("file/matrix.txt")));
//		data = matrix.applyCostMatrix(data, null);
//		System.out.println("weight data:");
//		for(int i = 0;i<data.numInstances();i++) {
//			System.out.println(data.instance(i));
//		}
//		
//		data = data.resampleWithWeights(new Random(1L));
//		System.out.println("resample data:");
//		for(int i = 0;i<data.numInstances();i++) {
//			System.out.println(data.instance(i));
//		}
//	}

}
