package com.wangyl.lda;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.thunlp.thulac.main.Main;

import com.wangyl.config.Config;
import com.wangyl.laplacianEigenmaps.LaplacianEigenmaps;
import com.wangyl.preprocesser.RawDataPreprocesser;
import com.wangyl.preprocesser.StructuredPreproedData;
import com.wangyl.svmAPI.svm_result;
import com.wangyl.tools.FileOperateAPI;
import com.wangyl.tools.IOapi;
import com.wangyl.tools.ResultAnalyser;

public class TestLDA {
	public static void runLdaSvm(int n,double c) throws Exception {
		LDA_api.GenerateModelsForEachTagert(Config.LDAInputDir,n);
		ArrayList<StructuredPreproedData> testData=ExtractFeatureBaseLda.ExtractLDAFeatures(Config.ldaSvmFeaturesDir_train, Config.ldaSvmFeaturesDir_test, Config.preprocessedTrainDataOutputSrc, Config.preprocessedTestDataOutputSrc, Config.LDAInputDir,n);
		//ArrayList<StructuredPreproedData> testData=RawDataPreprocesser.LoadTestDataFromPreproedFile();
		for(int i=0;i<testData.size();i++) {
			StructuredPreproedData tmpdata=testData.get(i);
			LdaSvmClassifier.classify(Config.ldaSvmFeaturesDir_train+tmpdata.className+"_feature.txt", Config.ldaSvmFeaturesDir_test+tmpdata.className+"_feature.txt", tmpdata.className,c);
		}
		for(int i=0;i<Config.topics.length;i++) {
			//int [][]result;
			ResultAnalyser rstAnalyser;
			rstAnalyser=ResultAnalyser.GetResult(svm_result.GetSVMResultFromFile(Config.ldasvmPredictResultDir+Config.topics[i]+"_result.txt", Config.topics[i]), Config.topics[i]);
			rstAnalyser.print();
		}
		System.out.println("all works have finished");
	}
	public static void main(String[] args) throws Exception {
		
		//LDA_api.GenerateModelsForEachTagert();
		/**/
		//runLdaSvm();
		
		
		
		//System.out.println(r/t);
		//FileOperateAPI.CopyFolder("D:\\工程项目\\java\\Weibo Text Stance Detection\\generatedData\\preprocessedTestdata\\", Config.ldaSvmFeaturesDir_train);
		//FileOperateAPI.CopyFolder(Config.preprocessedTestDataOutputSrc, Config.ldaSvmFeaturesDir_test);
		//LDA_api.Infer(Config.LDAInputDir+"开放二胎\\", "开放二胎.txt");
		/*//LDA_api.GenerateModelsForEachTagert();
		ExtractFeatureBaseLda.LoadLDAModel(Config.LDAInputDir+"IphoneSE\\"+"model-final.twords");
		ExtractFeatureBaseLda.GenerateFeatureFile(Config.preprocessedTrainDataOutputSrc,"IphoneSE", Config.ldaSvmFeaturesDir_train);
		ExtractFeatureBaseLda.GenerateFeatureFile(Config.preprocessedTestDataOutputSrc, "IphoneSE", Config.ldaSvmFeaturesDir_test);
		System.out.println("特征文件生成完毕");
		
		ArrayList<Integer> stanceId=new ArrayList<Integer>();
		//train
		double [][]fea=LdaSvmClassifier.LoadFeatures(Config.ldaSvmFeaturesDir_train+"IphoneSE_features.txt", stanceId);
		fea=LaplacianEigenmaps.LapEig(100, 35, fea);
		LaplacianEigenmaps.SaveFeaturesForSVM(Config.ldaSvmFeaturesDir_train+"IphoneSE_lda_le_features.txt", fea, stanceId);
		//test
		fea=LdaSvmClassifier.LoadFeatures(Config.ldaSvmFeaturesDir_test+"IphoneSE_features.txt", stanceId);
		fea=LaplacianEigenmaps.LapEig(100, 35, fea);
		LaplacianEigenmaps.SaveFeaturesForSVM(Config.ldaSvmFeaturesDir_test+"IphoneSE_lda_le_features.txt", fea, stanceId);
		
		try {
			LdaSvmClassifier.classify(Config.ldaSvmFeaturesDir_train+"IphoneSE_lda_le_features.txt", Config.ldaSvmFeaturesDir_test+"IphoneSE_lda_le_features.txt","IphoneSE");
			//LdaSvmClassifier.classify(Config.ldaSvmFeaturesDir_train+"IphoneSE_features.txt", Config.ldaSvmFeaturesDir_test+"IphoneSE_features.txt","IphoneSE");
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}*/
		
		
		
	}
}
