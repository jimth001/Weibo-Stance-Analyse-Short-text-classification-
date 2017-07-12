package com.wangyl.lsa;

import java.io.IOException;
import java.util.ArrayList;

import com.wangyl.config.Config;
import com.wangyl.laplacianEigenmaps.LaplacianEigenmaps;
import com.wangyl.log.Log;
import com.wangyl.preprocesser.RawDataPreprocesser;
import com.wangyl.preprocesser.StructuredPreproedData;
import com.wangyl.svmAPI.svm_result;
import com.wangyl.tools.ResultAnalyser;


public class TestLSA {
	public static void main(String []args) throws Exception {
		//extractLsaFeturesForSVM(1);
		//svm_classify();
		runlsagvm(0.2,20,25);
	}
	public static void svm_classify(double c) {
		ArrayList<StructuredPreproedData> testData;
		try {
			testData = RawDataPreprocesser.LoadTestDataFromPreproedFile();
			for(int i=0;i<testData.size();i++) {
				StructuredPreproedData tmpData=testData.get(i);
				LsaSvmClassifier.classify(Config.lsaSvmFeature_train+tmpData.className+"_lsafeature.txt", Config.lsaSvmFeature_test+tmpData.className+"_lsafeature.txt", tmpData.className,c);
				
			}
			double f_avg=0;
			for(int i=0;i<testData.size();i++) {
				StructuredPreproedData tmpData=testData.get(i);
				ResultAnalyser rstAnalyser=ResultAnalyser.GetResult(svm_result.GetSVMResultFromFile(Config.lsaSvmResult+tmpData.className+"_result.txt", tmpData.className), tmpData.className);
				rstAnalyser.print();
				f_avg+=rstAnalyser.f_avg;
			}
			f_avg=f_avg/testData.size();
			System.out.printf("%s%.4f\n","total f_avg:",f_avg);
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
	}
	public static void extractLsaFeturesForSVM(int groupnum) {
		ArrayList<StructuredPreproedData> testData;
		try {
			testData = RawDataPreprocesser.LoadTestDataFromPreproedFile();
			ExtractFeatureBaseLsa.GenerateFeatureFile(groupnum, 0, true, Config.lsaSvmFeature_train, null, true);
			ExtractFeatureBaseLsa.GenerateFeatureFile(groupnum, 0, true, Config.lsaSvmFeature_test, testData, false);
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
	}
	public static void runlsasvm() throws Exception {
		
		
		
	}
	public static void runlsagvm(double p,int k,double len) {
		try {
			//Log.EndLog();
			//Config.isDebugMode=false;
			/*for(int k=7;k<=9;k++) {
				for(int j=20;j<=100;j+=30) {
					System.out.println("k,j="+k+" "+j+" "+LsaGroupVoteModel.Classify_fixedVoteNum(k,j));
				}
				//选取了789三种分组，20,50.80三种投票数量，生成9个结果
			}*/
			for(int j=(int)(5+p*len/2);j<(int)(5+len-len*p/2);j++) {
				System.out.println(LsaGroupVoteModel.Classify_fixedVoteNum(j,k));
			}
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		System.out.println("finish");
	}
}

//TODO Auto-generated method stub
		/*LsiLsa m = new LsiLsa(); 
		String string="今年 过节 不 放 鞭炮 过 春节 放 鞭炮 虽然 可以 增加 过年 气氛 但是 每年 的 鞭炮 伤 人 的 事件 仍旧 不 少 而且 污染 环境 制造 噪音 浪费 金钱 今年 过节 不 放炮  ";
		WordSegment.splitWordwithTag(string);
		string=WordSegment.TermsNameToString(" ");
		//m.querySimilarity("入室 盗窃案 蓬莱市 实验中学 2002 年 10 月 12 日 凌晨 蓬莱市 实验中学 报警器 报警 有人 入室 盗窃");
		m.querySimilarity(string);*/
		//RawDataPreprocesser rawDataPreprocesser=new RawDataPreprocesser(Config.rawDataSrc+Config.testDataName, Config.preprocessedTestDataOutputSrc, Config.encodingType);
		//rawDataPreprocesser.PreprocessTestData();
		//LsaGroupVoteModel.Classify(17, 0.6);
		/*RawDataPreprocesser rawDataPreprocesser=new RawDataPreprocesser("", "", Config.encodingType);
		try {
			rawDataPreprocesser.PreproForLSA("IphoneSE");
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}*/
		/*ExtractFeatureBaseLsa.GenerateFeatureFile(10, 10, true, Config.lsaSvmFeature_train, null,true);
		try {
			ExtractFeatureBaseLsa.GenerateFeatureFile(10, 10, true, Config.lsaSvmFeature_test, new RawDataPreprocesser("", "", Config.encodingType).LoadTestDataFromPreproedFile(),false);
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		System.out.println("TestLSA.main()");*/


/*ArrayList<Integer> stanceIdArrayList=new ArrayList<Integer>();
double [][]features=LaplacianEigenmaps.LapEig(100, 15, LsaSvmClassifier.LoadFeatures(Config.lsaSvmFeature_train+"深圳禁摩限电_lsafeature.txt",stanceIdArrayList));
LaplacianEigenmaps.SaveFeaturesForSVM(Config.lsaSvmFeature_train+"深圳禁摩限电_lsalefeature.txt", features,stanceIdArrayList);

features=LaplacianEigenmaps.LapEig(100, 15, LsaSvmClassifier.LoadFeatures(Config.lsaSvmFeature_test+"深圳禁摩限电_lsafeature.txt",stanceIdArrayList));
LaplacianEigenmaps.SaveFeaturesForSVM(Config.lsaSvmFeature_test+"深圳禁摩限电_lsalefeature.txt", features,stanceIdArrayList);

LsaSvmClassifier.classify(Config.lsaSvmFeature_train+"深圳禁摩限电_lsalefeature.txt", Config.lsaSvmFeature_test+"深圳禁摩限电_lsalefeature.txt", "深圳禁摩限电");
*/