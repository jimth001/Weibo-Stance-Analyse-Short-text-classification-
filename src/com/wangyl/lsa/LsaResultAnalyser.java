package com.wangyl.lsa;

import com.wangyl.config.Config;
import com.wangyl.log.Log;
import com.wangyl.tools.Timer;

public class LsaResultAnalyser {
	private String topic="";
	private String stance="";
	private double similarity;
	private String docname;
	public LsaResultAnalyser(String docname,double similarity) {
		this.setDocname(docname);
		this.similarity=similarity;
		this.ConfirmStanceAndTopic();
	}
	public LsaResultAnalyser(double similarity) {
		this.docname="forfeature_none";
		this.similarity=similarity;
		this.ConfirmStanceAndTopic();
	}
	public double getSimilarity() {
		return similarity;
	}
	public String getStance() {
		return stance;
	}
	public String getTopic() {
		return topic;
	}
	public String getDocname() {
		return docname;
	}
	public void setDocname(String docname) {
		this.docname = docname;
	}
	public void ConfirmStanceAndTopic() {
		String string=docname.replace(".txt", "");
		String[] strs=string.split("_");
		if(strs.length<2) {
			Log.LogInf("文件名称格式错误:"+docname);
		}
		else {
			topic=strs[0];
			stance=strs[1];
		}
	}
	public static String VoteForMostSimilarityEntity(LsaResultAnalyser[] lra,double votenum) {
		int len=lra.length;
		double favor=0;
		double infavor=0;
		double none=0;
		for(int i=0;i<Math.min(len, votenum);i++) {
			if(lra[i].stance.equals(Config.favor)) {
				favor+=sigmoidBasedVoteValue(lra[i].similarity);
			}
			else if(lra[i].stance.equals(Config.infavor)) {
				infavor+=sigmoidBasedVoteValue(lra[i].similarity);
			}
			else if(lra[i].stance.equals(Config.none)) {
				none+=sigmoidBasedVoteValue(lra[i].similarity);
			}
			else {
				Log.LogInf(Timer.GetNowTimeToMillisecends()+"立场类别错误:"+lra[i].stance);
			}
		}
		if(favor>infavor&&favor>none) {
			return Config.favor;
		}
		else if(infavor>favor&&infavor>none) {
			return Config.infavor;
		}
		else {
			return Config.none;
		}
	}
	private static double sigmoidBasedVoteValue(double a) {
		return 1/(1+(Math.pow(Math.E, 6-10*a)));
		//return a;
	}
}
