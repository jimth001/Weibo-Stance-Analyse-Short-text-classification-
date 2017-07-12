package com.wangyl.lsa;

import java.util.ArrayList;
import java.util.HashMap;

import com.wangyl.config.Config;
import com.wangyl.log.Log;
import com.wangyl.preprocesser.RawDataPreprocesser;
import com.wangyl.preprocesser.StructuredPreproedData;
import com.wangyl.tools.FileOperateAPI;
import com.wangyl.tools.IOapi;

public class ExtractFeatureBaseLsa {
	/**
	 * 
	 * @param groupnum
	 * @param dims
	 * @param maxdims
	 * @param outputDir
	 * @param testDatas 该参数为训练数据时，此参数可为null，生成的是svm训练数据；为测试数据时，生成的是svm测试数据，数据类型为arraylist<StructuredPreproedData>
	 * @param isTrainFeature 是否为训练数据
	 * @param 
	 */
	public static void GenerateFeatureFile(int groupnum,int dims,boolean maxdims,String outputDir,ArrayList<StructuredPreproedData> testDatas,boolean isTrainFeature) {
		try {
			HashMap<String, Integer> topicIdMapInTrainData=new HashMap<String, Integer>();
			ArrayList<StructuredPreproedData> traindata=RawDataPreprocesser.LoadTrainDataFromPreproedFile(topicIdMapInTrainData);
			if(isTrainFeature) {
				testDatas=traindata;
			}
			IOapi tmpIO=new IOapi(1);
			for(int i=0;i<testDatas.size();i++) {//一次循环处理一种话题
				
				StructuredPreproedData preTestData=testDatas.get(i);
				LsiLsa lsa=new LsiLsa();
				if(topicIdMapInTrainData.containsKey(preTestData.className)) {
					int index=topicIdMapInTrainData.get(preTestData.className).intValue();
					StructuredPreproedData preTrainData=traindata.get(index);
					//检测训练集是否包含全部分类：
					if(preTrainData.favor.size()==0||preTrainData.infavor.size()==0||preTrainData.none.size()==0) {
						try {
							Log.LogInf("error:训练集未包含全部分类！");
						} catch (Exception e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}
						continue;
					}
					tmpIO.startWrite(outputDir+preTestData.className+"_lsafeature.txt", Config.encodingType, 0);
					//准备与preTestData.className相应的lsa训练数据（corpus文件夹下）
					RawDataPreprocesser.PreproForLSA(preTestData.className, groupnum);
					//设置维数：
					if(maxdims) {
						LsiLsa.LSD=FileOperateAPI.HowManyFileInDir(Config.lsaCorpusDir);
					}
					else {
						LsiLsa.LSD=Math.min(dims, FileOperateAPI.HowManyFileInDir(Config.lsaCorpusDir));
					}
					//重置lsa状态：
					lsa.Reset();
					//为testdata的favor数据生成特征：
					if(preTestData.favor.size()>0) {
						LsaResultAnalyser[][]rstAnalysers=lsa.GetFeatureDq_groupdata(preTestData.favor,false);
						for(int k=0;k<rstAnalysers.length;k++) {
							tmpIO.writeOneString(LsaResultToString(rstAnalysers[k], Config.favor)+"\n", 0);
						}
					}
					//为testdata的infavor数据生成特征：
					if(preTestData.infavor.size()>0) {
						LsaResultAnalyser[][]rstAnalysers=lsa.GetFeatureDq_groupdata(preTestData.infavor,false);
						//把特征写到feature文件中:
						for(int k=0;k<rstAnalysers.length;k++) {
							tmpIO.writeOneString(LsaResultToString(rstAnalysers[k], Config.infavor)+"\n", 0);
						}
					}
					//为testdata的none数据生成特征：
					if(preTestData.none.size()>0) {
						LsaResultAnalyser[][]rstAnalysers=lsa.GetFeatureDq_groupdata(preTestData.none,false);
						for(int k=0;k<rstAnalysers.length;k++) {
							tmpIO.writeOneString(LsaResultToString(rstAnalysers[k], Config.none)+"\n", 0);
						}
					}
					tmpIO.endWrite(0);
				}
				else {
					try {
						Log.LogInf("error:训练集中未出现的类别！:"+preTestData.className);
					} catch (Exception e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
				}
			}
		}
		catch (Exception e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		}
	}
	private static String LsaResultToString(LsaResultAnalyser[] lra,String stance) throws Exception {
		StringBuffer strBuf=new StringBuffer("");
		if(stance.equals(Config.favor)) {
			strBuf.append("0");
		}
		else if(stance.equals(Config.infavor)) {
			strBuf.append("1");
		}
		else if(stance.equals(Config.none)) {
			strBuf.append("2");
		}
		else {
			throw new Exception("错误的立场标签！");
		}
		for(int i=0;i<lra.length;i++) {
			//lra[i].ConfirmStanceAndTopic();
			strBuf.append(" "+(i+1)+":"+lra[i].getSimilarity());
		}
		return strBuf.toString();
	}
}
