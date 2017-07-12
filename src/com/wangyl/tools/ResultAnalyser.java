package com.wangyl.tools;

import java.util.ArrayList;

import com.wangyl.config.Config;

public class ResultAnalyser {
	public static ResultAnalyser LoadLSAGVMResult(String resultFileSrc,String topic) {
		ArrayList<String> preStance=new ArrayList<String>();
		ArrayList<String> realStance=new ArrayList<String>();
		IOapi tmpIO=new IOapi(1);
		tmpIO.startRead(resultFileSrc, Config.encodingType, 0);
		String line=tmpIO.readOneSentence(0);
		while(line!=null) {
			String []strs=line.split("\t");
			preStance.add(strs[1]);
			realStance.add(strs[2]);
			line=tmpIO.readOneSentence(0);
		}
		tmpIO.endRead(0);
		int [][]a=new int[2][realStance.size()];
		for(int i=0;i<realStance.size();i++) {
			if(Config.favor.equals(preStance.get(i))) {
				a[0][i]=Config.favor_int;
			}
			else if(Config.infavor.equals(preStance.get(i))) {
				a[0][i]=Config.infavor_int;
			}
			else {
				a[0][i]=Config.none_int;
			}
			if(Config.favor.equals(realStance.get(i))) {
				a[1][i]=Config.favor_int;
			}
			else if(Config.infavor.equals(realStance.get(i))) {
				a[1][i]=Config.infavor_int;
			}
			else {
				a[1][i]=Config.none_int;
			}
		}
		return GetResult(a, topic);
	}
	public static int[][] LoadLSAGVMResultINTARRAY(String resultFileSrc,String topic) {
		ArrayList<String> preStance=new ArrayList<String>();
		ArrayList<String> realStance=new ArrayList<String>();
		IOapi tmpIO=new IOapi(1);
		tmpIO.startRead(resultFileSrc, Config.encodingType, 0);
		String line=tmpIO.readOneSentence(0);
		while(line!=null) {
			String []strs=line.split("\t");
			preStance.add(strs[1]);
			realStance.add(strs[2]);
			line=tmpIO.readOneSentence(0);
		}
		tmpIO.endRead(0);
		int [][]a=new int[2][realStance.size()];
		for(int i=0;i<realStance.size();i++) {
			if(Config.favor.equals(preStance.get(i))) {
				a[0][i]=Config.favor_int;
			}
			else if(Config.infavor.equals(preStance.get(i))) {
				a[0][i]=Config.infavor_int;
			}
			else {
				a[0][i]=Config.none_int;
			}
			if(Config.favor.equals(realStance.get(i))) {
				a[1][i]=Config.favor_int;
			}
			else if(Config.infavor.equals(realStance.get(i))) {
				a[1][i]=Config.infavor_int;
			}
			else {
				a[1][i]=Config.none_int;
			}
		}
		return a;
	}
	public static ResultAnalyser GetResult(int [][]a,String topic) {
		ResultAnalyser rstAnalyser=new ResultAnalyser(topic);
		rstAnalyser.total_num=a[0].length;
		for(int i=0;i<a[0].length;i++) {
			if(a[0][i]==a[1][i]) {//正确的
				rstAnalyser.rightNum++;
				if(a[0][i]==Config.favor_int) {
					rstAnalyser.rightFavorNum++;
					rstAnalyser.preFavorNum++;
					rstAnalyser.realFavorNum++;
				}
				else if(a[0][i]==Config.infavor_int) {
					rstAnalyser.rightInfavorNum++;
					rstAnalyser.preInfavorNum++;
					rstAnalyser.realInfavorNum++;
				}
				else {
					
				}
			}
			else {//错误的
				if(a[0][i]==Config.favor_int) {
					rstAnalyser.preFavorNum++;
					if(a[1][i]==Config.infavor_int) {//预测为favor，答案为infavor
						rstAnalyser.realInfavorNum++;
					}
					else if(a[1][i]==Config.none_int) {
						
					}
				}
				else if(a[0][i]==Config.infavor_int) {
					rstAnalyser.preInfavorNum++;
					if(a[1][i]==Config.favor_int) {
						rstAnalyser.realFavorNum++;
					}
					else if(a[1][i]==Config.none_int){
						
					}
				}
				else {
					if(a[1][i]==Config.favor_int) {
						rstAnalyser.realFavorNum++;
					}
					else if(a[1][i]==Config.infavor_int) {
						rstAnalyser.realInfavorNum++;
					}
				}
			}
		}
		rstAnalyser.p_favor=rstAnalyser.rightFavorNum/rstAnalyser.preFavorNum;
		rstAnalyser.r_favor=rstAnalyser.rightFavorNum/rstAnalyser.realFavorNum;
		rstAnalyser.p_infavor=rstAnalyser.rightInfavorNum/rstAnalyser.preInfavorNum;
		rstAnalyser.r_infavor=rstAnalyser.rightInfavorNum/rstAnalyser.realInfavorNum;
		rstAnalyser.f_favor=2*rstAnalyser.p_favor*rstAnalyser.r_favor/(rstAnalyser.p_favor+rstAnalyser.r_favor);
		rstAnalyser.f_infavor=2*rstAnalyser.p_infavor*rstAnalyser.r_infavor/(rstAnalyser.p_infavor+rstAnalyser.r_infavor);
		rstAnalyser.f_avg=(rstAnalyser.f_favor+rstAnalyser.f_infavor)/2;
		rstAnalyser.acc=rstAnalyser.rightNum/rstAnalyser.total_num;
		return rstAnalyser;
	}
	public String topic;
	public double rightNum=0;
	public double rightFavorNum=0;
	public double rightInfavorNum=0;
	public double preInfavorNum=0;
	public double preFavorNum=0;
	public double realFavorNum=0;
	public double realInfavorNum=0;
	public double p_favor=0;
	public double p_infavor=0;
	public double r_favor=0;
	public double r_infavor=0;
	public double f_infavor=0;
	public double f_favor=0;
	public double f_avg=0;
	public double total_num;
	public double acc;
	public ResultAnalyser(String topicStr) {
		topic=topicStr;
		rightNum=0;
		rightFavorNum=0;
		rightInfavorNum=0;
		preInfavorNum=0;
		preFavorNum=0;
		p_favor=0;
		p_infavor=0;
		r_favor=0;
		r_infavor=0;
		f_infavor=0;
		f_favor=0;
		f_avg=0;
		realFavorNum=0;
		realInfavorNum=0;
		total_num=0;
		acc=0;
	}
	public void print() {
		System.out.printf("%s%s\n","Topic:",topic);
		System.out.printf("%s%.4f\n","f-favor:",f_favor);
		System.out.printf("%s%.4f\n","f-infavor:",f_infavor);
		System.out.printf("%s%.4f\n","f-avg:",f_avg);
		System.out.printf("%s%.4f\n","acc:",acc);
	}
	public static void main(String [] args) {
		double a=0.12345678;
		System.out.printf("%s%.4f\n","sd",a);
		System.out.printf("%.4f\n",a);
	}
}
