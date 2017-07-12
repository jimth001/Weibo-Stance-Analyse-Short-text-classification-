package com.wangyl.lsa;

import java.io.IOException;
import java.util.ArrayList;

import com.wangyl.config.Config;
import com.wangyl.svmAPI.svm_predict;
import com.wangyl.svmAPI.svm_train;
import com.wangyl.tools.IOapi;

public class LsaSvmClassifier {
	public static void classify(String trainData,String testData,String []trainCmdline,String []predictCmdline) throws IOException {
		//String []paramArrayOfString=trainCmdline.split(" ");
		svm_train.TrainModel(trainCmdline);
		//paramArrayOfString=predictCmdline.split(" ");
		svm_predict.Predict(predictCmdline);
	}
	//从feature文件中加载原始lsa feature，返回double[][]，用于LE降维
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
	//使用svm分类
	/**
	 * 
	 * @param trainData 训练数据特征文件路径
	 * @param testData 测试数据特征文件路径
	 * @param target 主题，目标
	 * @throws IOException
	 */
	public static void classify(String trainData,String testData,String target,double c) throws IOException {
		String trainCmdline="-s 0 -t 0 -c "+c;//+trainData+" "+Config.svmModelSrc;
		String []tmp=trainCmdline.split(" ");
		String []trainCmd=new String[tmp.length+2];
		int i=0;
		for(i=0;i<tmp.length;i++) {
			trainCmd[i]=tmp[i];
		}
		trainCmd[i]=trainData;
		trainCmd[i+1]=Config.lsasvmModelDir+"model";
		String []predictCmd=new String[3];//testData+" "+Config.svmModelSrc+".model "+Config.svmPredictResultSrc;
		predictCmd[0]=testData;
		predictCmd[1]=Config.lsasvmModelDir+"model";
		predictCmd[2]=Config.lsaSvmResult+target+"_result.txt";
		IOapi.makeSureDirExists(Config.lsasvmModelDir);
		IOapi.makeSureDirExists(Config.lsaSvmResult);
		classify(trainData, testData, trainCmd, predictCmd);
	}
}
