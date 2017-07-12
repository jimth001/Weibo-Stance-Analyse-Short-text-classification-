package com.wangyunli.sentimentLexicon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.wangyl.config.Config;
import com.wangyl.log.Log;
import com.wangyl.preprocesser.RawDataPreprocesser;
import com.wangyl.preprocesser.StructuredPreproedData;
import com.wangyl.svmAPI.svm_predict;
import com.wangyl.svmAPI.svm_result;
import com.wangyl.svmAPI.svm_train;
import com.wangyl.tools.FileOperateAPI;
import com.wangyl.tools.IOapi;
import com.wangyl.tools.ResultAnalyser;

public class SentimentAnalyse {
	public static void ExtractSentimentFeature(String []preprocessedData,String featureFileSrc,int []stance,double l) {
		IOapi tmpIO=new IOapi(1);
		tmpIO.startWrite(featureFileSrc, Config.encodingType, 0);
		double positiveEvaluationNum;
		double negativeEvaluationNum;
		double positiveSentimentNum;
		double negativeSentimentNum;
		double positiveEvaluationValue;
		double negativeEvaluationValue;
		double positiveSentimentValue;
		double negativeSentimentValue;
		double lamata=l;
		for(int i=0;i<preprocessedData.length;i++) {
			tmpIO.startRead(preprocessedData[i], Config.encodingType, 0);
			String line=tmpIO.readOneSentence(0);
			while(line!=null) {
				positiveEvaluationNum=0;
				positiveSentimentNum=0;
				negativeSentimentNum=0;
				negativeEvaluationNum=0;
				String []strs=line.split(" ");
				for(int j=0;j<strs.length;j++) {
					if(SentimentLexicon.GetEvaluation(strs[j])==SentimentLexicon.pos) {
						positiveEvaluationNum++;
					}
					else if(SentimentLexicon.GetEvaluation(strs[j])==SentimentLexicon.neg) {
						negativeEvaluationNum++;
					}
					if(SentimentLexicon.GetSentiment(strs[j])==SentimentLexicon.pos) {
						positiveSentimentNum++;
					}
					else if(SentimentLexicon.GetSentiment(strs[j])==SentimentLexicon.neg) {
						negativeSentimentNum++;
					}
				}
				positiveEvaluationValue=(positiveEvaluationNum+1)/(positiveEvaluationNum+Math.pow(negativeEvaluationNum, lamata)+2);
				negativeEvaluationValue=(Math.pow(negativeEvaluationNum, lamata)+1)/(positiveEvaluationNum+Math.pow(negativeEvaluationNum, lamata)+2);
				positiveSentimentValue=(positiveSentimentNum+1)/(positiveSentimentNum+Math.pow(negativeSentimentNum,lamata)+2);
				negativeSentimentValue=(Math.pow(negativeSentimentNum,lamata)+1)/(positiveSentimentNum+Math.pow(negativeSentimentNum,lamata)+2);
				tmpIO.writeOneString(stance[i]+" 1:"+positiveEvaluationValue+" 2:"+negativeEvaluationValue+" 3:"+positiveSentimentValue+"4:"+negativeSentimentValue+"\n", 0);
				line=tmpIO.readOneSentence(0);
			}
			tmpIO.endRead(0);
		}
		tmpIO.endWrite(0);
	}
	
	
	public static double[][] LoadFeatures(String featureFileSrc,ArrayList<Integer> stanceId) {
		IOapi tmpIO=new IOapi(1);
		tmpIO.startRead(featureFileSrc, Config.encodingType, 0);
		String line=tmpIO.readOneSentence(0);
		ArrayList<String> strList=new ArrayList<String>();
		
		while(line!=null) {
			strList.add(line);
			line=tmpIO.readOneSentence(0);
		}
		tmpIO.endRead(0);
		double [][]rst=new double [strList.size()][strList.get(0).split(" ").length-1];
		String []strs;
		stanceId.clear();
		for(int i=0;i<strList.size();i++) {
			strs=strList.get(i).split(" ");
			stanceId.add(Integer.parseInt(strs[0]));
			for(int j=1;j<strs.length;j++) {
				rst[i][j-1]=Double.parseDouble((strs[j].split(":"))[1]);
			}
		}
		return rst;
	}
	public static void ClassifyBySVM(String testData,String target) throws Exception {
		String []predictCmd=new String[3];//testData+" "+Config.svmModelSrc+".model "+Config.svmPredictResultSrc;
		predictCmd[0]=testData;
		predictCmd[1]=Config.senFeatureSvmModelDir+target+"_model";
		if(!IOapi.isFileExist(predictCmd[1])) {
			Log.LogInf("error:模型文件不存在:"+predictCmd[1]+"\n");
			return;
		}
		predictCmd[2]=Config.senFeaSvmResultDir+target+"_result.txt";
		IOapi.makeSureDirExists(Config.senFeaSvmResultDir);
		try {
			svm_predict.Predict(predictCmd);
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		ResultAnalyser.GetResult(svm_result.GetSVMResultFromFile(Config.senFeaSvmResultDir+target+"_result.txt", target), target).print();
	}
	public static void TrainModel(double l,double c) throws IOException {
		//RawDataPreprocesser rawDataPreprocesser=new RawDataPreprocesser("", "", Config.encodingType);
		//ArrayList<StructuredPreproedData> testDatas=rawDataPreprocesser.LoadTestDataFromPreproedFile();
		ArrayList<File> trainfileList=new ArrayList<File>();
		HashMap<String, Integer> testTopicIdMap=new HashMap<String, Integer>();
		FileOperateAPI.visitDirsAllFiles(Config.preprocessedTrainDataOutputSrc, trainfileList);
		ArrayList<String> topicList=RawDataPreprocesser.GetTopicsFromFiles(trainfileList, testTopicIdMap);
		//生成训练集的特征文件,并训练svm模型
		for(int i=0;i<topicList.size();i++) {
			String []strs={Config.preprocessedTrainDataOutputSrc+topicList.get(i)+"_"+Config.favor+".txt",
					Config.preprocessedTrainDataOutputSrc+topicList.get(i)+"_"+Config.infavor+".txt",
					Config.preprocessedTrainDataOutputSrc+topicList.get(i)+"_"+Config.none+".txt"};
			int []stance={Config.favor_int,Config.infavor_int,Config.none_int};
			ExtractSentimentFeature(strs, Config.senFeatureDir_train+topicList.get(i)+"_fea", stance,l);
			//设定SVM参数：
			String trainCmdline="-s 0 -t 0 -c "+c;
			String []tmp=trainCmdline.split(" ");
			String []trainCmd=new String[tmp.length+2];
			int j=0;
			for(j=0;j<tmp.length;j++) {
				trainCmd[j]=tmp[j];
			}
			trainCmd[j]=Config.senFeatureDir_train+topicList.get(i)+"_fea";
			trainCmd[j+1]=Config.senFeatureSvmModelDir+topicList.get(i)+"_model";
			IOapi.makeSureDirExists(Config.senFeatureSvmModelDir);
			svm_train.TrainModel(trainCmd);
		}
	}
	public static void Run(boolean modelExist,double l,double c) throws Exception {
		if(!modelExist) {
			try {
				TrainModel(c,l);
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
				
			}
		}
		ArrayList<File> testfilelist=new ArrayList<File>();
		try {
			FileOperateAPI.visitDirsAllFiles(Config.preprocessedTestDataOutputSrc, testfilelist);
			HashMap<String, Integer> testTopicIdMap=new HashMap<String, Integer>();
			ArrayList<String> testtopicList=RawDataPreprocesser.GetTopicsFromFiles(testfilelist, testTopicIdMap);
			for(int i=0;i<testtopicList.size();i++) {
				String []strs={Config.preprocessedTestDataOutputSrc+testtopicList.get(i)+"_"+Config.favor+".txt",
						Config.preprocessedTestDataOutputSrc+testtopicList.get(i)+"_"+Config.infavor+".txt",
						Config.preprocessedTestDataOutputSrc+testtopicList.get(i)+"_"+Config.none+".txt"};
				int []stance={Config.favor_int,Config.infavor_int,Config.none_int};
				ExtractSentimentFeature(strs, Config.senFeatureDir_test+testtopicList.get(i)+"_fea", stance,l);
				ClassifyBySVM(Config.senFeatureDir_test+testtopicList.get(i)+"_fea",testtopicList.get(i));
				
			}
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	public static void main(String []args) {
		try {
			//Run(false);
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
}
