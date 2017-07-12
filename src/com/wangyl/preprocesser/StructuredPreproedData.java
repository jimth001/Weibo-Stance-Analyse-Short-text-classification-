package com.wangyl.preprocesser;

import java.util.ArrayList;

import com.wangyl.config.Config;

public class StructuredPreproedData {
	public String className="";
	public ArrayList<String> favor=null;
	public ArrayList<String> infavor=null;
	public ArrayList<String> none=null;
	public ArrayList<String> org_favor=null;
	public ArrayList<String> org_infavor=null;
	public ArrayList<String> org_none=null;
	public ArrayList<String> org_unknown=null;
	public boolean isTrainData;//
	public ArrayList<String> unknown=null;
	/**
	 * 
	 */
	public StructuredPreproedData(boolean istraindata) {
		this.isTrainData=istraindata;
		favor=new ArrayList<String>();
		infavor=new ArrayList<String>();
		none=new ArrayList<String>();
		unknown=new ArrayList<String>();
		org_favor=new ArrayList<String>();
		org_infavor=new ArrayList<String>();
		org_none=new ArrayList<String>();
		org_unknown=new ArrayList<String>();
	}
	public void AddData(String sen,String stance) throws Exception{
		if(stance.equals(Config.favor)) {
			favor.add(sen);
		}
		else if(stance.equals(Config.infavor)) {
			infavor.add(sen);
		}
		else if(stance.equals(Config.none)) {
			none.add(sen);
		}
		else if(stance.equals(Config.unknown)) {
			unknown.add(sen);
		}
		else {
			throw new Exception("原始数据立场标签错误！原始数据："+sen+"\n"+"立场标签："+stance+"\n");
		}
	}
	public void AddOrgData(String sen,String stance) throws Exception {
		if(stance.equals(Config.favor)) {
			org_favor.add(sen);
		}
		else if(stance.equals(Config.infavor)) {
			org_infavor.add(sen);
		}
		else if(stance.equals(Config.none)) {
			org_none.add(sen);
		}
		else if(stance.equals(Config.unknown)) {
			org_unknown.add(sen);
		}
		else {
			throw new Exception("原始数据立场标签错误！原始数据："+sen+"\n"+"立场标签："+stance+"\n");
		}
	}
}
