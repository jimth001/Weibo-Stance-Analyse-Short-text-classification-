package com.wangyl.ensembleLearning;

import com.wangyl.config.Config;
import com.wangyl.svmAPI.svm_result;
import com.wangyl.tools.ResultAnalyser;

public class EnsembleFramework {
	public static double [][]weight=new double[3][5];
	public static void SetWeight() {
		weight[0][0]=0.5313;//lda iphone
		weight[0][1]=0.5245;//反恐
		weight[0][2]=0.6758;//开放二胎
		weight[0][3]=0.751;//春节
		weight[0][4]=0.5985;//深圳
		weight[1][0]=0.59145;//lsasvm
		weight[1][1]=0.5415;
		weight[1][2]=0.7651;
		weight[1][3]=0.77605;
		weight[1][4]=0.69505;
		weight[2][0]=0.5855;//lsagvm
		weight[2][1]=0.5408;
		weight[2][2]=0.7634;
		weight[2][3]=0.7395;
		weight[2][4]=0.7419;
	}
	public static void vote() throws Exception {
		SetWeight();
		String []targets=Config.topics;
		for(int i=0;i<targets.length;i++) {
			int [][]ldasvm=svm_result.GetSVMResultFromFile(Config.ldasvmPredictResultDir+targets[i]+"_result.txt", targets[i]);
			int [][]lsasvm=svm_result.GetSVMResultFromFile(Config.lsaSvmResult+targets[i]+"_result.txt", targets[i]);
			int [][]lsagvm=ResultAnalyser.LoadLSAGVMResultINTARRAY(Config.lsaGroupVoteModelOutputSrc+targets[i]+"_15_20.txt", targets[i]);
			int [][]predictResult=new int[ldasvm.length][ldasvm[0].length];
			for(int j=0;j<ldasvm[0].length;j++) {
				double []rst={0,0,0};
				rst[ldasvm[0][j]]+=weight[0][i];
				rst[lsasvm[0][j]]+=weight[1][i];
				rst[lsagvm[0][j]]+=weight[2][i];
				if(rst[0]>rst[1]&&rst[0]>rst[2]) {
					predictResult[0][j]=0;
				}
				else if(rst[1]>rst[0]&&rst[1]>rst[2]) {
					predictResult[0][j]=1;
				}
				else {
					predictResult[0][j]=2;
				}
				predictResult[1][j]=ldasvm[1][j];
			}
			ResultAnalyser resultAnalyser=ResultAnalyser.GetResult(predictResult, targets[i]);
			resultAnalyser.print();
		}
		
	}
	public static void main(String []args) {
		try {
			vote();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	private static double sigmoidBasedVoteValue(double a) {
		return 1/(1+(Math.pow(Math.E, 6-10*a)));
		//return a;
	}
}
