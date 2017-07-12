package com.wangyl.tools;

import java.util.ArrayList;
import java.util.HashMap;

import com.wangyl.preprocesser.RawDataPreprocesser;
import com.wangyl.preprocesser.StructuredPreproedData;

public class TongJi_0520_tmp {
	public static void tongji(ArrayList<String> strs,HashMap<String, Integer> map) {
		for(int i=0;i<strs.size();i++) {
			String []strarr=strs.get(i).split(" ");
			for(int j=0;j<strarr.length;j++) {
				if(map.containsKey(strarr[j])) {
					continue;
				}
				else {
					map.put(strarr[j], 0);
				}
			}
		}
	}
	public static void main(String []args) throws Exception {
		HashMap<String, Integer> testwordMap=new HashMap<String, Integer>();
		HashMap<String, Integer> trainwordMap=new HashMap<String, Integer>();
		HashMap<String, Integer> totalwordMap=new HashMap<String, Integer>();
		ArrayList<StructuredPreproedData> testdataArrayList=RawDataPreprocesser.LoadTestDataFromPreproedFile();
		ArrayList<StructuredPreproedData> traindataArrayList=RawDataPreprocesser.LoadTrainDataFromPreproedFile();
		for(int i=0;i<testdataArrayList.size();i++) {
			testwordMap.clear();
			trainwordMap.clear();
			totalwordMap.clear();
			StructuredPreproedData tmpData=testdataArrayList.get(i);
			tongji(tmpData.favor, testwordMap);
			tongji(tmpData.infavor, testwordMap);
			tongji(tmpData.none, testwordMap);
			tongji(tmpData.favor, totalwordMap);
			tongji(tmpData.infavor, totalwordMap);
			tongji(tmpData.none, totalwordMap);
			System.out.println("test:"+tmpData.className);
			System.out.println("favor:"+tmpData.favor.size());
			System.out.println("infavor:"+tmpData.infavor.size());
			System.out.println("none:"+tmpData.none.size());
			System.out.println("total size:"+(tmpData.none.size()+tmpData.infavor.size()+tmpData.favor.size()));
			StructuredPreproedData tmpData2=traindataArrayList.get(i);
			tongji(tmpData2.favor, trainwordMap);
			tongji(tmpData2.infavor, trainwordMap);
			tongji(tmpData2.none, trainwordMap);
			tongji(tmpData2.favor, totalwordMap);
			tongji(tmpData2.infavor, totalwordMap);
			tongji(tmpData2.none, totalwordMap);
			System.out.println("train:"+tmpData2.className);
			System.out.println("favor:"+tmpData2.favor.size());
			System.out.println("infavor:"+tmpData2.infavor.size());
			System.out.println("none:"+tmpData2.none.size());
			System.out.println("total size:"+(tmpData2.none.size()+tmpData2.infavor.size()+tmpData2.favor.size()));
			System.out.println("test wordlist size:"+testwordMap.size());
			System.out.println("train wordlist size:"+trainwordMap.size());
			System.out.println("total wordlist size:"+totalwordMap.size());
		}
	}
}
