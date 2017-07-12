package com.wangyl.config;

import com.wangyl.tools.IOapi;

public class Config {
	
	/**
	 * favor标签对应的字符串
	 */
	public static String favor="FAVOR";
	public static String infavor="AGAINST";
	public static String none="NONE";
	public static String unknown="UNKNOWN";
	public static int favor_int=0;
	public static int infavor_int=1;
	public static int none_int=2;
	public static int unknown_int=3;
	public static String encodingType="utf-8";
	/**
	 * 预处理训练集的输出路径
	 */
	public static String preprocessedTrainDataOutputSrc=IOapi.GetCurrentDir()+"\\generatedData\\preprocessedTraindata\\";
	/**
	 * 原始语料路径
	 */
	public static String rawDataDir=IOapi.GetCurrentDir()+"\\data\\";
	public static String labeledTrainDataName="evasampledata4-TaskAA.txt";
	public static String notLabeledTrainDataName="evasampledata4-TaskAR.txt";
	public static String testDataName="NLPCC_2016_Stance_Detection_Task_A_gold.txt";
	/**
	 * 预处理测试集的输出路径
	 */
	public static String preprocessedTestDataOutputSrc=IOapi.GetCurrentDir()+"\\generatedData\\preprocessedTestdata\\";
	/**
	 * debug模式开启时，errorlog的信息将同时在控制台输出
	 */
	public static boolean isDebugMode=true;
	/**
	 * lsa输入语料的路径
	 */
	public static String lsaCorpusDir=IOapi.GetCurrentDir()+"\\generatedData\\corpus\\";
	/**
	 * log文件的路径
	 */
	public static String logsrc=IOapi.GetCurrentDir()+"\\errorlog\\log.txt";
	/**
	 * 最终结果的路径
	 */
	public static String finalResultSrc=IOapi.GetCurrentDir()+"\\generatedData\\finalResult.txt";
	/**
	 * 英文停止词的路径
	 */
	public static String EnStopwordsSrc=IOapi.GetCurrentDir()+"\\models\\english_stopword.txt";
	/**
	 * 中文停止词的路径
	 */
	public static String ChStopwordsSrcString=IOapi.GetCurrentDir()+"\\models\\chinese_stopwords.txt";
	public static String stopDict2Src=IOapi.GetCurrentDir()+"\\models\\stopDict.dic";
	/**
	 * 用户自定义停止词的路径
	 */
	public static String UserStopwordSrc=IOapi.GetCurrentDir()+"\\models\\userstopwords.txt";
	/**
	 * lda统一输入文件路径
	 */
	public static String LDAInputSrc=IOapi.GetCurrentDir()+"\\generatedData\\LDAInput\\ldainput.txt";
	/**
	 * lda分主题输入的文件的路径
	 */
	public static String LDAInputDir=IOapi.GetCurrentDir()+"\\generatedData\\LDAInput\\";
	public static String LDAInputFileName="ldainput.txt";
	public static String lsaGroupVoteModelOutputSrc=IOapi.GetCurrentDir()+"\\generatedData\\classifier\\lsaGVM_ensemble\\";
	public static String ensemblelsaGVMOutputSrc=IOapi.GetCurrentDir()+"\\generatedData\\classifier\\ensemble\\lsagvm\\";
	public static String ldasvmModelDir=IOapi.GetCurrentDir()+"\\generatedData\\ldasvmmodel\\";
	public static String ldasvmPredictResultDir=IOapi.GetCurrentDir()+"\\generatedData\\ldasvmresult\\";
	public static String ldaSvmFeaturesDir_train=IOapi.GetCurrentDir()+"\\generatedData\\ldasvmfea\\traindata\\";
	public static String ldaSvmFeaturesDir_test=IOapi.GetCurrentDir()+"\\generatedData\\ldasvmfea\\testdata\\";
	public static String lsaSvmFeature_train=IOapi.GetCurrentDir()+"\\generatedData\\lsasvmfea\\traindata\\";
	public static String lsaSvmFeature_test=IOapi.GetCurrentDir()+"\\generatedData\\lsasvmfea\\testdata\\";
	public static String lsaSvmResult=IOapi.GetCurrentDir()+"\\generatedData\\lsasvmresult\\";
	public static String lsasvmModelDir=IOapi.GetCurrentDir()+"\\generatedData\\lsasvmmodel\\";
	
	public static String ldaGVMResultDir=IOapi.GetCurrentDir()+"\\generatedData\\ldaGVMResult\\";
	public static String ldaGVMCorpusDir=IOapi.GetCurrentDir()+"\\generatedData\\ldaGVMCorpus\\";
	public static String ldaGVMFeature_train=IOapi.GetCurrentDir()+"\\generatedData\\ldaGVMFeature\\traindata\\";
	public static String ldaGVMFeature_test=IOapi.GetCurrentDir()+"\\generatedData\\ldaGVMFeature\\testdata\\";
	public static String preproedTrainDataForLDAGVM=IOapi.GetCurrentDir()+"\\generatedData\\preproedTrainDataForLDAGVM\\";
	//情感词路径：
	public static String sentimentLexiconPosSrc_Hownet=IOapi.GetCurrentDir()+"\\lexicon\\Hownet\\pos_opinion.txt";
	public static String sentimentLexiconPosSrc_Tsinghua=IOapi.GetCurrentDir()+"\\lexicon\\Tsinghua\\tsinghua.positive.gb.txt";
	public static String sentimentLexiconNegSrc_Hownet=IOapi.GetCurrentDir()+"\\lexicon\\Hownet\\neg_opinion.txt";
	public static String sentimentLexiconNegSrc_Tsinghua=IOapi.GetCurrentDir()+"\\lexicon\\Tsinghua\\tsinghua.negative.gb.txt";
	public static String senFeatureDir=IOapi.GetCurrentDir()+"\\generatedData\\senFeature\\";
	public static String senFeatureDir_train=IOapi.GetCurrentDir()+"\\generatedData\\senFeature\\train\\";
	public static String senFeatureDir_test=IOapi.GetCurrentDir()+"\\generatedData\\senFeature\\test\\";
	public static String senFeatureSvmModelDir=IOapi.GetCurrentDir()+"\\generatedData\\senFeatureSvmModel\\";
	public static String senFeaSvmResultDir=IOapi.GetCurrentDir()+"\\generatedData\\senFeaSvmResult\\";
	public static String []topics={"IphoneSE","俄罗斯在叙利亚的反恐行动","开放二胎","春节放鞭炮","深圳禁摩限电"};
	//交叉验证
	public static String validationCorpusDir=IOapi.GetCurrentDir()+"\\generatedData\\validationCorpus\\";
}
