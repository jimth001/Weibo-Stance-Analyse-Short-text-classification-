package com.wangyl.lsa;

import java.util.ArrayList;
import java.util.HashMap;

import com.wangyl.config.Config;
import com.wangyl.log.Log;
import com.wangyl.preprocesser.RawDataPreprocesser;
import com.wangyl.preprocesser.StructuredPreproedData;
import com.wangyl.tools.FileOperateAPI;
import com.wangyl.tools.IOapi;

public class LsaGroupVoteModel {
	
	public static double Classify(int groupnum,int dims,double voteproportion,boolean maxdims,boolean fixedVoteNum,double vote_num) {
		try {
			HashMap<String, Integer> topicIdMapInTrainData=new HashMap<String, Integer>();
			ArrayList<StructuredPreproedData> traindata=RawDataPreprocesser.LoadTrainDataFromPreproedFile(topicIdMapInTrainData);
			ArrayList<StructuredPreproedData> testDatas = RawDataPreprocesser.LoadTestDataFromPreproedFile();
			double all_f_avg=0;
			IOapi tmpIO=new IOapi(1);
			double voteNum=vote_num;
			if(fixedVoteNum==false) {
				voteNum=(groupnum*voteproportion);
			}
			for(int i=0;i<testDatas.size();i++) {//一次循环处理一种话题
				double rightNum=0;
				double rightFavorNum=0;
				double rightInfavorNum=0;
				double preInfavorNum=0;
				double preFavorNum=0;
				double p_favor=0;
				double p_infavor=0;
				double r_favor=0;
				double r_infavor=0;
				double f_infavor=0;
				double f_favor=0;
				double f_avg=0;
				StructuredPreproedData preTestData=testDatas.get(i);
				LsiLsa lsa=new LsiLsa();
				if(topicIdMapInTrainData.containsKey(preTestData.className)) {
					int index=topicIdMapInTrainData.get(preTestData.className).intValue();
					StructuredPreproedData preTrainData=traindata.get(index);
					//检查训练集数据：
					if(preTrainData.favor.size()==0||preTrainData.infavor.size()==0||preTrainData.none.size()==0) {
						try {
							Log.LogInf("error:训练集未包含全部分类！");
						} catch (Exception e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}
						continue;
					}
					//打开要写入的文件：
					tmpIO.startWrite(Config.lsaGroupVoteModelOutputSrc+preTestData.className+"_"+groupnum+"_"+(int)(voteNum)+".txt", Config.encodingType, 0);
					//为LSA做数据预处理：
					RawDataPreprocesser.PreproForLSA(preTestData.className, groupnum);
					//设置维数：
					if(maxdims) {
						LsiLsa.LSD=FileOperateAPI.HowManyFileInDir(Config.lsaCorpusDir);
					}
					else {
						LsiLsa.LSD=Math.min(dims, FileOperateAPI.HowManyFileInDir(Config.lsaCorpusDir));
					}
					//lsa状态初始化（重置）：
					lsa.Reset();
					//处理测试集中favor部分的数据（分类）：
					if(preTestData.favor.size()>0) {
						LsaResultAnalyser[][]rstAnalysers=lsa.getQuerysSimilarity(preTestData.favor);
						for(int x=0;x<rstAnalysers.length;x++) {
							String predictStance=LsaResultAnalyser.VoteForMostSimilarityEntity(rstAnalysers[x],voteNum);
							if(predictStance.equals(Config.favor)) {
								rightNum++;
								preFavorNum++;
								rightFavorNum++;
							}
							else {
								if(predictStance.equals(Config.infavor)) {
									preInfavorNum++;
								}
							}
							tmpIO.writeOneString(preTestData.favor.get(x)+"\t"+predictStance+"\t"+Config.favor+"\n", 0);
						}
					}
					//处理测试集中infavor部分的数据（分类）：
					if(preTestData.infavor.size()>0) {
						LsaResultAnalyser[][]rstAnalysers=lsa.getQuerysSimilarity(preTestData.infavor);
						for(int x=0;x<rstAnalysers.length;x++) {
							String predictStance=LsaResultAnalyser.VoteForMostSimilarityEntity(rstAnalysers[x],voteNum);
							if(predictStance.equals(Config.infavor)) {
								rightNum++;
								preInfavorNum++;
								rightInfavorNum++;
							}
							else {
								if(predictStance.equals(Config.favor)) {
									preFavorNum++;
								}
								//Log.LogInf("话题："+preTestData.className+"\t"+"内容："+preTestData.infavor.get(x)+"\t"+"预测："+predictStance+"\t"+"答案："+Config.infavor);
							}
							tmpIO.writeOneString(preTestData.infavor.get(x)+"\t"+predictStance+"\t"+Config.infavor+"\n", 0);
						}
					}
					//处理测试集中none部分的数据（分类）：
					if(preTestData.none.size()>0) {
						LsaResultAnalyser[][]rstAnalysers=lsa.getQuerysSimilarity(preTestData.none);
						for(int x=0;x<rstAnalysers.length;x++) {
							String predictStance=LsaResultAnalyser.VoteForMostSimilarityEntity(rstAnalysers[x],voteNum);
							if(predictStance.equals(Config.none)) {
								rightNum++;
							}
							else {
								if(predictStance.equals(Config.infavor)) {
									preInfavorNum++;
								}
								if(predictStance.equals(Config.favor)) {
									preFavorNum++;
								}
							}
							tmpIO.writeOneString(preTestData.none.get(x)+"\t"+predictStance+"\t"+Config.none+"\n", 0);
						}
					}
					//写入完毕，关闭output文件，释放资源
					tmpIO.endWrite(0);
					//计算相关评价指标：
					double totalNum=preTestData.infavor.size()+preTestData.favor.size()+preTestData.none.size();
					Log.LogInf(preTestData.className+" 准确率:"+rightNum/totalNum);
					p_infavor=rightInfavorNum/preInfavorNum;
					r_infavor=rightInfavorNum/preTestData.infavor.size();
					f_infavor=2*p_infavor*r_infavor/(p_infavor+r_infavor);
					p_favor=rightFavorNum/preFavorNum;
					r_favor=rightFavorNum/preTestData.favor.size();
					f_favor=2*p_favor*r_favor/(p_favor+r_favor);
					f_avg=(f_favor+f_infavor)/2;
					//输出F值：
					Log.LogInf("F-score:"+f_avg);
					all_f_avg+=f_avg;
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
			//计算所有topic的F值均值：
			all_f_avg=all_f_avg/testDatas.size();
			Log.LogInf("all f avg:"+all_f_avg);
			return all_f_avg;
		} 
		catch (Exception e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
			return Double.NEGATIVE_INFINITY;
		}
	}
	public static double Classify(int groupnum,int dims,double voteproportion) {
		return Classify(groupnum, dims, voteproportion, false,false,0);
	}
	public static double Classify(int groupnum,double voteproportion) {
		return Classify(groupnum, 3, voteproportion, true,false,0);
	}
	public static double Classify_fixedVoteNum(int groupnum,int dims,double votenum) {
		return Classify(groupnum, dims, 0, false, true, votenum);
	}
	public static double Classify_fixedVoteNum(int groupnum,double votenum) {
		return Classify(groupnum,0,0,true,true,votenum);
	}
}
