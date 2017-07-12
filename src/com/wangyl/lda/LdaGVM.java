package com.wangyl.lda;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import com.wangyl.config.Config;
import com.wangyl.log.Log;
import com.wangyl.preprocesser.RawDataPreprocesser;
import com.wangyl.svmAPI.svm_result;
import com.wangyl.tools.FileOperateAPI;
//import com.wangyl.laplacianEigenmaps.VectorR;
import com.wangyl.tools.IOapi;
import com.wangyl.tools.QuickSort;
import com.wangyl.tools.ResultAnalyser;
class SenFeature {
	int stance;
	double[] vector;
	public SenFeature(int stance,double[] vec) {
		this.stance=stance;
		this.vector=vec;
	}
}
class SimilarityAndStance implements Comparable<SimilarityAndStance>{
	public int stance;
	public double similarity;
	public SimilarityAndStance(int sta,double sim) {
		this.stance=sta;
		this.similarity=sim;
	}
	public int compareTo(SimilarityAndStance p)//quicksort的compareTo接口，降序排列 
	{
		// TODO 自动生成的方法存根
		
		int n=0;
		if(this.similarity>p.similarity){
			n=-1;
		}
		if(this.similarity<p.similarity) {
			n=1;
		}
		if(Math.abs(this.similarity-p.similarity)<0.00001) {
			n=0;
		}
		return n;
	}
}
public class LdaGVM {
	public static void GenerateModel(boolean isLdaModelExist,int n) throws Exception {
		HashMap<String, Integer> traintopicidmap=new HashMap<String, Integer>();
		ArrayList<File> trainfilelist=new ArrayList<File>();
		FileOperateAPI.visitDirsAllFiles(Config.preprocessedTrainDataOutputSrc, trainfilelist);
		ArrayList<String> traintopicList=RawDataPreprocesser.GetTopicsFromFiles(trainfilelist, traintopicidmap);
		for(int i=0;i<traintopicList.size();i++) {
			RawDataPreprocesser.PreproForLDAGVM(traintopicList.get(i), 30);
		}
		if(!isLdaModelExist) {
			LDA_api.GenerateModelsForEachTagert(Config.ldaGVMCorpusDir,n);
		}
		ExtractFeatureBaseLda.ExtractLDAFeatures(Config.ldaGVMFeature_train, Config.ldaGVMFeature_test, Config.preproedTrainDataForLDAGVM, Config.preprocessedTestDataOutputSrc, Config.ldaGVMCorpusDir,n);
	}
	public static void run(int n) throws Exception {
		GenerateModel(true,n);
		for(int i=0;i<Config.topics.length;i++) {
			for(int k=50;k<600;k+=5000) {
				Log.StartLog();
				Log.LogInf("K:"+k);
				Classify(Config.ldaGVMFeature_train+Config.topics[i]+"_feature.txt", Config.ldaGVMFeature_test+Config.topics[i]+"_feature.txt", "", k,Config.ldaGVMResultDir+Config.topics[i]+"_result.txt");
				//acc_avg+=Classify(Config.senFeatureDir_train+Config.topics[i]+"_fea", Config.senFeatureDir_test+Config.topics[i]+"_fea", "", k,Config.ldaGVMResultDir+Config.topics[i]+"_result.txt");
				//Log.LogInf("acc_avg:"+acc_avg/5);
				Log.EndLog();
				ResultAnalyser.GetResult(svm_result.GetSVMResultFromFile(Config.ldaGVMResultDir+Config.topics[i]+"_result.txt", Config.topics[i]), Config.topics[i]).print();
			}
		}
		System.out.println("all works have finished");
	}
	public static void main(String []args) throws Exception {
		
		//ExtractFeatureBaseLda.ExtractLDAFeatures(Config.ldaGVMFeature_train, Config.ldaGVMFeature_test, Config.preprocessedTrainDataOutputSrc, Config.preprocessedTestDataOutputSrc, Config.ldaGVMCorpusDir);
		/*for(int i=0;i<Config.topics.length;i++) {
			for(int k=3;k<20;k+=2) {
				Log.StartLog();
				Log.LogInf("K:"+k);
				Classify(Config.ldaGVMFeature_train+Config.topics[i]+"_feature.txt", Config.ldaGVMFeature_test+Config.topics[i]+"_feature.txt", "", k,Config.ldaGVMResultDir+Config.topics[i]+"_result.txt");
				//acc_avg+=Classify(Config.senFeatureDir_train+Config.topics[i]+"_fea", Config.senFeatureDir_test+Config.topics[i]+"_fea", "", k,Config.ldaGVMResultDir+Config.topics[i]+"_result.txt");
				//Log.LogInf("acc_avg:"+acc_avg/5);
				Log.EndLog();
				ResultAnalyser.GetResult(svm_result.GetSVMResultFromFile(Config.ldaGVMResultDir+Config.topics[i]+"_result.txt", Config.topics[i]), Config.topics[i]).print();
			}
		}*/
		//run();
		
		System.out.println("all works have finished");
		//run();
	}
	public static double Classify(String trainDataSrc,String testDataSrc,String resultSrc,int votingNum,String ouputSrc) {
		IOapi tmpIO=new IOapi(1);
		tmpIO.startRead(trainDataSrc, Config.encodingType, 0);
		String line=tmpIO.readOneSentence(0);
		//加载训练集特征向量
		ArrayList<SenFeature> trainFeatureList=new ArrayList<SenFeature>();
		while(line!=null) {
			String []strs=line.split(" ");
			double []tmpVector=new double[strs.length-1];
			for(int i=1;i<strs.length;i++) {
				String []tmpstrs=strs[i].split(":");
				tmpVector[i-1]=Double.parseDouble(tmpstrs[1]);
			}
			trainFeatureList.add(new SenFeature(Integer.parseInt(strs[0]), tmpVector));
			line=tmpIO.readOneSentence(0);
		}
		tmpIO.endRead(0);
		//加载测试集特征向量
		tmpIO.startRead(testDataSrc, Config.encodingType, 0);
		line=tmpIO.readOneSentence(0);
		ArrayList<SenFeature> testFeatureList=new ArrayList<SenFeature>();
		while(line!=null) {
			String []strs=line.split(" ");
			double []tmpVector=new double[strs.length-1];
			for(int i=1;i<strs.length;i++) {
				tmpVector[i-1]= Double.parseDouble((strs[i].split(":")[1]));
			}
			testFeatureList.add(new SenFeature(Integer.parseInt(strs[0]), tmpVector));
			line=tmpIO.readOneSentence(0);
		}
		tmpIO.endRead(0);
		//计算相似度矩阵并排序：201705040033
		SimilarityAndStance [][]simAndStancesMatrix=new SimilarityAndStance[testFeatureList.size()][trainFeatureList.size()];
		int []ResultArray=new int[testFeatureList.size()];
		int rightnum=0;
		for(int i=0;i<testFeatureList.size();i++) {
			for(int j=0;j<trainFeatureList.size();j++) {
				//simAndStancesMatrix[i][j]=new SimilarityAndStance(trainFeatureList.get(j).stance, CalculateSimilarity(testFeatureList.get(i).vector, trainFeatureList.get(j).vector, testFeatureList.get(i).vector.length));
				simAndStancesMatrix[i][j]=new SimilarityAndStance(trainFeatureList.get(j).stance, CompareVector(testFeatureList.get(i).vector, trainFeatureList.get(j).vector, testFeatureList.get(i).vector.length));
				//simAndStancesMatrix[i][j]=new SimilarityAndStance(trainFeatureList.get(j).stance, JensenShannonDis(testFeatureList.get(i).vector, trainFeatureList.get(j).vector));
			}
			QuickSort.sort(simAndStancesMatrix[i]);
			double favor=0;
			double infavor=0;
			double none=0;
			for(int j=0;j<trainFeatureList.size()&&j<votingNum;j++) {
				
				if(simAndStancesMatrix[i][j].stance==Config.favor_int) {
					favor+=Sigmoid_mod(simAndStancesMatrix[i][j].similarity);
				}
				else if(simAndStancesMatrix[i][j].stance==Config.infavor_int) {
					infavor+=Sigmoid_mod(simAndStancesMatrix[i][j].similarity);
				}
				else {
					none+=Sigmoid_mod(simAndStancesMatrix[i][j].similarity);
				}
			}
			if(favor>infavor&&favor>none) {
				ResultArray[i]=0;
			}
			else if(infavor>favor&&infavor>none) {
				ResultArray[i]=1;
			}
			else {
				ResultArray[i]=2;
			}
			if(testFeatureList.get(i).stance==ResultArray[i]) {
				rightnum++;
			}
		}
		tmpIO.startWrite(ouputSrc, Config.encodingType, 0);
		for(int i=0;i<ResultArray.length;i++) {
			tmpIO.writeOneString(ResultArray[i]+"\n", 0);
		}
		tmpIO.endWrite(0);
		double acc=(double)rightnum/(double)testFeatureList.size();
		//Log.LogInf("准确率:"+acc+" "+rightnum+"/"+testFeatureList.size());
		return acc;
	}
	//计算余弦夹角
  	private static double CompareVector(double[] v1,double[] v2,int size)
  	{
  		// A(dot)B = |A||B|Cos(theta)	
  		// so Cos(theta) = A(dot)B / |A||B|
  		double a_dot_b=0;
  		for (int i=0;i<size;i++)
  		{
  			a_dot_b += v1[i] * v2[i];
  		}
  		double A=0;
  		for (int j=0;j<size;j++)
  		{
  			A += v1[j] * v1[j];
  		}
  		A = Math.sqrt(A);
  		double B=0;
  		for (int k=0;k<size;k++)
  		{
  			B +=v2[k]*v2[k];
  		}
  		B = Math.sqrt(B);	
  		return a_dot_b/(A*B);
  	}
    //一种新的相似度
  	private static double CalculateSimilarity(double[] v1,double[] v2,int size)
  	{
  		// A(dot)B = |A||B|Cos(theta)	
  		// so Cos(theta) = A(dot)B / |A||B|
  		double sim=0;
  		for(int i=0;i<size;i++) {
  			sim+=Sigmoid_sim(Math.abs(2*(v1[i]-v2[i])/((v1[i]+v2[i])*(v1[i]+v2[i]))));
  		}
  		return sim/size;
  	}
  	private static double KullbackLerblerDis(double p[],double q[]) {
  		double rst=0;
  		for(int i=0;i<p.length;i++) {
  			rst+=p[i]*Math.log(p[i]/q[i]);
  		}
  		return rst;
  	}
  	private static double JensenShannonDis(double p[],double []q) {
  		double []tmpd=new double[p.length];
  		for(int i=0;i<p.length;i++) {
  			tmpd[i]=(p[i]+q[i])/2;
  		}
  		return (KullbackLerblerDis(p, tmpd)+KullbackLerblerDis(q, tmpd))/2;
  	}
  	private static double Sigmoid_sim(double x) {
  		return 1/(1+Math.pow(Math.E, (4*x-2)));
  	}
  	//x>=0
  	private static double Sigmoid_mod(double x) {
  		return 1/(1+Math.pow(Math.E, (6-10*x)));
  		//return x;
  	}
}
