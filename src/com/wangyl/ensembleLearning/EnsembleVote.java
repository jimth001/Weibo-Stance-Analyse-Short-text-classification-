package com.wangyl.ensembleLearning;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.wangyl.config.Config;
import com.wangyl.log.Log;
import com.wangyl.preprocesser.StructuredPreproedData;
import com.wangyl.tools.FileOperateAPI;
import com.wangyl.tools.IOapi;
class SenVoteCounter {
	public int favor;
	public int infavor;
	public int none;
	public String sen;
	public String ans;
	public SenVoteCounter(int favor,int infavor,int none,String sen,String ans) {
		this.favor=favor;
		this.infavor=infavor;
		this.none=none;
		this.sen=sen;
		this.ans=ans;
	}
	/**
	 * @description 加载svm的分类结果，通用于lda-svm，lsa-svm，sf-svm，lsa-le-svm
	 * @param resultSrc
	 * @return 返回一个arraylist<Integer>,0-favor,1-infavor,2-none
	 * @date 20170507
	 */
	public ArrayList<Integer> LoadSvmResult(String resultSrc) {
		IOapi tmpIO=new IOapi(1);
		ArrayList<Integer> rstArrayList=new ArrayList<Integer>();
		tmpIO.startRead(resultSrc, Config.encodingType, 0);
		String line=tmpIO.readOneSentence(0);
		while(line!=null) {
			rstArrayList.add((int)Double.parseDouble(line));
			line=tmpIO.readOneSentence(0);
		}
		return rstArrayList;
	}
	public SenVoteCounter(String stance,String sen,String ans) {
		this.sen=sen;
		this.favor=0;
		this.none=0;
		this.infavor=0;
		this.ans=ans;
		if(stance.equals(Config.favor)) {
			this.favor=1;
		}
		else if(stance.equals(Config.infavor)) {
			this.infavor=1;
		}
		else if(stance.equals(Config.none)) {
			this.none=1;
		}
	}
	public void AddVote(String stance) {
		if(stance.equals(Config.favor)) {
			this.favor++;
		}
		else if(stance.equals(Config.infavor)) {
			this.infavor++;
		}
		else if(stance.equals(Config.none)) {
			this.none++;
		}
	}
	public String GetStance() throws Exception{
		if(favor>infavor&&favor>none) {
			return Config.favor;
		}
		if(infavor>favor&&infavor>none) {
			return Config.infavor;
		}
		if(none>infavor&&none>favor) {
			return Config.none;
		}
		throw new Exception("错误的总票数："+(favor+infavor+none));
	}
}
public class EnsembleVote {
	public static void vote(ArrayList<String> topics,String inputDir,String outputsrc) {
		List<File> filelist=new ArrayList<File>();
		IOapi tmpIO=new IOapi(1);
		try {
			FileOperateAPI.visitDirsAllFiles(inputDir, filelist);
			double all_f_avg=0;
			for(int i=0;i<topics.size();i++) {
				ArrayList<SenVoteCounter> senVoteCounters=new ArrayList<SenVoteCounter>();
				boolean firstread=true;
				for(File file:filelist) {
					if(file.getName().contains(topics.get(i))) {
						tmpIO.startRead(file, Config.encodingType, 0);
						int linenum=0;
						String line=tmpIO.readOneSentence(0);
						while(line!=null) {
							String []strs=line.split("\t");
							if(firstread) {//读第一个投票文件的时候
								senVoteCounters.add(new SenVoteCounter(strs[1], strs[0],strs[2]));
							}
							else {//读其他分类器投票文件的时候
								senVoteCounters.get(linenum).AddVote(strs[1]);
								linenum++;
							}
							line=tmpIO.readOneSentence(0);
						}
						tmpIO.endRead(0);
					}
				}
				tmpIO.startWrite(outputsrc+topics.get(i)+".txt", Config.encodingType, 0);
				for(int j=0;j<senVoteCounters.size();j++) {
					try {
						tmpIO.writeOneString(senVoteCounters.get(j).sen+"\t"+senVoteCounters.get(j).GetStance()+"\t"+senVoteCounters.get(j).ans+"\n", 0);
					} catch (Exception e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
				}
				tmpIO.endWrite(0);
				all_f_avg+=PrintResult(outputsrc+topics.get(i)+".txt");
			}
			Log.LogInf("all_f_avg："+all_f_avg/topics.size());
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
	}
	public static double PrintResult(String resultsrc) {
		IOapi tmpIO=new IOapi(1);
		tmpIO.startRead(resultsrc, Config.encodingType, 0);
		String line=tmpIO.readOneSentence(0);
		String[] strs;
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
		double realfavornum=0;
		double realinfavornum=0;
		double totalnum=0;
		while(line!=null) {
			strs=line.split("\t");
			if(strs[1].equals(strs[2])) {//right
				rightNum++;
				if(strs[1].equals(Config.favor)) {
					rightFavorNum++;
					realfavornum++;
					preFavorNum++;
				}
				if(strs[1].equals(Config.infavor)) {
					rightInfavorNum++;
					realinfavornum++;
					preInfavorNum++;
				}
			}
			else {//wrong
				if(strs[1].equals(Config.favor)) {
					preFavorNum++;
				}
				if(strs[1].equals(Config.infavor)) {
					preInfavorNum++;
				}
				if(strs[2].equals(Config.favor)) {
					realfavornum++;
				}
				if(strs[2].equals(Config.infavor)) {
					realinfavornum++;
				}
			}
			line=tmpIO.readOneSentence(0);
			totalnum++;
		}
		Log.LogInf(resultsrc);
		Log.LogInf("准确率："+rightNum/totalnum);
		p_infavor=rightInfavorNum/preInfavorNum;
		r_infavor=rightInfavorNum/realinfavornum;
		f_infavor=2*p_infavor*r_infavor/(p_infavor+r_infavor);
		p_favor=rightFavorNum/preFavorNum;
		r_favor=rightFavorNum/realfavornum;
		f_favor=2*p_favor*r_favor/(p_favor+r_favor);
		f_avg=(f_favor+f_infavor)/2;
		Log.LogInf("F-score："+f_avg);
		return f_avg;
	}
	public static void main(String[] args) {
		ArrayList<String> topics=new ArrayList<String>();
		topics.add("IphoneSE");
		topics.add("春节放鞭炮");
		topics.add("俄罗斯在叙利亚的反恐行动");
		topics.add("开放二胎");
		topics.add("深圳禁摩限电");
		vote(topics, Config.lsaGroupVoteModelOutputSrc, Config.ensemblelsaGVMOutputSrc);
	}
	public static void run(ArrayList<StructuredPreproedData> testdata) {
		ArrayList<String> topics=new ArrayList<String>();
		for(int i=0;i<testdata.size();i++) {
			topics.add(testdata.get(i).className);
		}
		vote(topics, Config.lsaGroupVoteModelOutputSrc, Config.ensemblelsaGVMOutputSrc);
	}
}
