package com.wangyl.trainModel;

import java.util.ArrayList;
import com.wangyl.config.Config;
import com.wangyl.preprocesser.RawDataPreprocesser;
import com.wangyl.preprocesser.StructuredPreproedData;
import com.wangyl.tools.IOapi;

/*
 * https://www.scipy.org/install.html 这里提供了多种安装方法。
我推荐whl 方法。
whl 文件的下载，http://www.lfd.uci.edu/~gohlke/pythonlibs/#scipy
如果已经安装了非mkl版本的numpy, 建议先卸载。
C:\Users\chenzq>python2 -m pip uninstall numpy
F:\>python2 -m pip install "numpy-1.11.1+mkl-cp27-cp27m-win32.whl"搜索
Processing f:\numpy-1.11.1+mkl-cp27-cp27m-win32.whl
Installing collected packages: numpy
Successfully installed numpy-1.11.1+mkl
F:\>python2 -m pip install "scipy-0.18.0-cp27-cp27m-win32.whl"
Processing f:\scipy-0.18.0-cp27-cp27m-win32.whl
Installing collected packages: scipy
Successfully installed scipy-0.18.0
 */
/**
 * @description mainly for lsa
 * @author a0312
 *
 */
public class GenerateValidationSet {
	public static String rawDataSrc="";//input
	public static String rawTrainDataSetSrc="";//output
	public static String rawValidationSetSrc="";//output
	public static final double rawValidationSetRatio=0.25;//参数
	private static ArrayList<String> sens=new ArrayList<String>();
	public static void GenerateModelForTunning() {
		GenerateModelForTunningByOrder();
	}
	public static void GenerateModelForTunning(String mode,int index) {
		if(mode.equals("random")) {//全随机
			GenerateModelForTunningByRamdom();
		}
		else if(mode.equals("group-half-random")) {//组内半随机
			GenerateModelForTunningByGroupHalfRandomSample();
		}
		else if(mode.equals("group-index")) {//组内索引
			GenerateModelForTunningByGroupIndexSample(index);
		}
		else if(mode.equals("group-random")) {//组内全随机
			GenerateModelForTunningByGroupRandomSample();
		}
		else if(mode.equals("ascending-order")) {
			GenerateModelForTunningByOrder();
		}
	}
	private static void GenerateModelForTunningByRamdom() {
		sens.clear();
		IOapi tmpIO=new IOapi(2);
		tmpIO.startRead(rawDataSrc, Config.encodingType, 0);
		String line=tmpIO.readOneSentence(0);
		while(line!=null) {
			sens.add(line);
			line=tmpIO.readOneSentence(0);
		}
		tmpIO.endRead(0);
		int total_num=sens.size();
		int valsetnum=(int)(total_num*rawValidationSetRatio);
		int[] map=new int[sens.size()];
		for(int i=0;i<map.length;i++) {
			map[i]=0;
		}
		tmpIO.startWrite(rawValidationSetSrc, Config.encodingType, 0);
		tmpIO.startWrite(rawTrainDataSetSrc, Config.encodingType, 1);
		int seed=0;
		for(int i=0;i<valsetnum;i++) {
			seed=((int)(Math.random()*2*total_num))%total_num;
			while(map[seed]==1) {
				seed=((int)(Math.random()*2*total_num))%total_num;
			}
			map[seed]=1;
		}
		for(int i=0;i<map.length;i++) {
			if(map[i]==1) {
				tmpIO.writeOneString(sens.get(i)+"\n", 0);
			}
			else {
				tmpIO.writeOneString(sens.get(i)+"\n", 1);
			}
		}
		tmpIO.endWrite(0);
		tmpIO.endWrite(1);
	}
	private static void GenerateModelForTunningByOrder() {
		sens.clear();
		IOapi tmpIO=new IOapi(2);
		tmpIO.startRead(rawDataSrc, Config.encodingType, 0);
		String line=tmpIO.readOneSentence(0);
		while(line!=null) {
			sens.add(line);
			line=tmpIO.readOneSentence(0);
		}
		tmpIO.endRead(0);
		int total_num=sens.size();
		int valsetnum=(int)(total_num*rawValidationSetRatio);
		int[] map=new int[sens.size()];
		for(int i=0;i<map.length;i++) {
			map[i]=0;
		}
		tmpIO.startWrite(rawValidationSetSrc, Config.encodingType, 0);
		tmpIO.startWrite(rawTrainDataSetSrc, Config.encodingType, 1);
		int i=0;
		for(i=0;i<valsetnum;i++) {
			tmpIO.writeOneString(sens.get(i)+"\n", 0);
		}
		while(i<total_num) {
			tmpIO.writeOneString(sens.get(i)+"\n", 1);
			i++;
		}
		tmpIO.endWrite(0);
		tmpIO.endWrite(1);
	}
	private static void GenerateModelForTunningByGroupHalfRandomSample() {
		sens.clear();
		IOapi tmpIO=new IOapi(2);
		tmpIO.startRead(rawDataSrc, Config.encodingType, 0);
		String line=tmpIO.readOneSentence(0);
		while(line!=null) {
			sens.add(line);
			line=tmpIO.readOneSentence(0);
		}
		tmpIO.endRead(0);
		int total_num=sens.size();
		int valsetnum=(int)(total_num*rawValidationSetRatio);
		int sectionlen=total_num/valsetnum;
		if(total_num%valsetnum!=0) {
			sectionlen=total_num/(valsetnum-1);
		}
		int index=0;
		int index_seed=(int)(Math.random()*total_num);//随机出一个索引种子
		tmpIO.startWrite(rawValidationSetSrc, Config.encodingType, 0);
		tmpIO.startWrite(rawTrainDataSetSrc, Config.encodingType, 1);
		for(int i=0;i<total_num;i+=sectionlen) {
			index=index_seed%Math.min(sectionlen, total_num-i);
			tmpIO.writeOneString(sens.get(i+index)+"\n", 0);
			for(int j=i;j<total_num&&j<i+sectionlen;j++) {
				if(j==i+index) {
					continue;
				}
				tmpIO.writeOneString(sens.get(j)+"\n", 1);
			}
		}
		tmpIO.endWrite(0);
		tmpIO.endWrite(1);
	}
	private static void GenerateModelForTunningByGroupRandomSample() {//需要x条数据，则将原始数据按顺序划分为x组，每组中随机选择1条
		sens.clear();
		IOapi tmpIO=new IOapi(2);
		tmpIO.startRead(rawDataSrc, Config.encodingType, 0);
		String line=tmpIO.readOneSentence(0);
		while(line!=null) {
			sens.add(line);
			line=tmpIO.readOneSentence(0);
		}
		tmpIO.endRead(0);
		int total_num=sens.size();
		int valsetnum=(int)(total_num*rawValidationSetRatio);
		int sectionlen=total_num/valsetnum;
		if(total_num%valsetnum!=0) {
			sectionlen=total_num/(valsetnum-1);
		}
		int index=0;
		tmpIO.startWrite(rawValidationSetSrc, Config.encodingType, 0);
		tmpIO.startWrite(rawTrainDataSetSrc, Config.encodingType, 1);
		for(int i=0;i<total_num;i+=sectionlen) {
			index=((int)(Math.random()*2*valsetnum))%Math.min(sectionlen, total_num-i);
			tmpIO.writeOneString(sens.get(i+index)+"\n", 0);
			for(int j=i;j<total_num&&j<i+sectionlen;j++) {
				if(j==i+index) {
					continue;
				}
				tmpIO.writeOneString(sens.get(j)+"\n", 1);
			}
		}
		tmpIO.endWrite(0);
		tmpIO.endWrite(1);
	}
	private static void GenerateModelForTunningByGroupIndexSample(int index_seed) {//需要x条数据，则将原始数据按顺序划分为x组，每组中选择1条，组内索引号为index%组长度
		sens.clear();
		sens.clear();
		IOapi tmpIO=new IOapi(2);
		tmpIO.startRead(rawDataSrc, Config.encodingType, 0);
		String line=tmpIO.readOneSentence(0);
		while(line!=null) {
			sens.add(line);
			line=tmpIO.readOneSentence(0);
		}
		tmpIO.endRead(0);
		int total_num=sens.size();
		int valsetnum=(int)(total_num*rawValidationSetRatio);
		int sectionlen=total_num/valsetnum;
		if(total_num%valsetnum!=0) {
			sectionlen=total_num/(valsetnum-1);
		}
		int index=0;
		tmpIO.startWrite(rawValidationSetSrc, Config.encodingType, 0);
		tmpIO.startWrite(rawTrainDataSetSrc, Config.encodingType, 1);
		for(int i=0;i<total_num;i+=sectionlen) {
			index=index_seed%Math.min(sectionlen, total_num-i);
			tmpIO.writeOneString(sens.get(i+index)+"\n", 0);
			for(int j=i;j<total_num&&j<i+sectionlen;j++) {
				if(j==i+index) {
					continue;
				}
				tmpIO.writeOneString(sens.get(j)+"\n", 1);
			}
		}
		tmpIO.endWrite(0);
		tmpIO.endWrite(1);
	}
	public static void CombineTwoTexts(String src1,String src2,String outputSrc) {
		ArrayList<String> content=new ArrayList<String>();
		IOapi tmpIO=new IOapi(1);
		tmpIO.startRead(src1, Config.encodingType, 0);
		String line=tmpIO.readOneSentence(0);
		while(line!=null) {
			content.add(line);
			line=tmpIO.readOneSentence(0);
		}
		tmpIO.endRead(0);
		tmpIO.startRead(src2, Config.encodingType, 0);
		line=tmpIO.readOneSentence(0);
		while(line!=null) {
			content.add(line);
			tmpIO.readOneSentence(0);
		}
		tmpIO.endRead(0);
		tmpIO.startWrite(outputSrc, Config.encodingType, 0);
		for(int i=0;i<content.size();i++) {
			tmpIO.writeOneString(content.get(i)+"\n", 0);
		}
		tmpIO.endWrite(0);
	}
	public static void main(String []args) {
		RawDataPreprocesser rawDataPreprocesser=new RawDataPreprocesser("", "", Config.encodingType);
		ArrayList<StructuredPreproedData> arrayList=rawDataPreprocesser.GenerateValidationCorpus(Config.rawDataDir+Config.labeledTrainDataName, Config.rawDataDir+Config.testDataName, Config.validationCorpusDir);
		int index=4;
		for(int i=0;i<arrayList.size();i++) {
			StructuredPreproedData tmpData=arrayList.get(i);
			rawDataSrc=Config.validationCorpusDir+tmpData.className+"_FAVOR.txt";
			rawTrainDataSetSrc=Config.preprocessedTrainDataOutputSrc+tmpData.className+"_FAVOR.txt";
			rawValidationSetSrc=Config.preprocessedTestDataOutputSrc+tmpData.className+"_FAVOR.txt";
			GenerateModelForTunning("random",index);
			rawDataSrc=Config.validationCorpusDir+tmpData.className+"_AGAINST.txt";
			rawTrainDataSetSrc=Config.preprocessedTrainDataOutputSrc+tmpData.className+"_AGAINST.txt";
			rawValidationSetSrc=Config.preprocessedTestDataOutputSrc+tmpData.className+"_AGAINST.txt";
			GenerateModelForTunning("random",index);
			rawDataSrc=Config.validationCorpusDir+tmpData.className+"_NONE.txt";
			rawTrainDataSetSrc=Config.preprocessedTrainDataOutputSrc+tmpData.className+"_NONE.txt";
			rawValidationSetSrc=Config.preprocessedTestDataOutputSrc+tmpData.className+"_NONE.txt";
			GenerateModelForTunning("random",index);
		}
		System.out.print("finish");
	}
}
