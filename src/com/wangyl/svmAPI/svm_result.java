package com.wangyl.svmAPI;

import java.util.ArrayList;
import java.util.HashMap;

import javax.sound.sampled.Line;

import com.wangyl.config.Config;
import com.wangyl.preprocesser.RawDataPreprocesser;
import com.wangyl.preprocesser.StructuredPreproedData;
import com.wangyl.tools.IOapi;

public class svm_result {
	
	public static int[][] GetSVMResultFromFile(String svm_result_src,String target) throws Exception {
		HashMap<String, Integer> topicidmap=new HashMap<String, Integer>();
		ArrayList<StructuredPreproedData> testDatas=RawDataPreprocesser.LoadTestDataFromPreproedFile(topicidmap);
		StructuredPreproedData targetData=testDatas.get(topicidmap.get(target));
		IOapi tmpIO=new IOapi(1);
		tmpIO.startRead(svm_result_src, Config.encodingType, 0);
		String line=tmpIO.readOneSentence(0);
		int i=0;
		int [][]rst=new int[2][targetData.favor.size()+targetData.infavor.size()+targetData.none.size()];
		while(line!=null) {
			rst[0][i]=(int)Double.parseDouble(line);
			if(i<targetData.favor.size()) {
				rst[1][i]=0;
			}
			else if(i<targetData.favor.size()+targetData.infavor.size()) {
				rst[1][i]=1;
			}
			else {
				rst[1][i]=2;
			}
			i++;
			line=tmpIO.readOneSentence(0);
		}
		tmpIO.endRead(0);
		return rst;
	}
}
