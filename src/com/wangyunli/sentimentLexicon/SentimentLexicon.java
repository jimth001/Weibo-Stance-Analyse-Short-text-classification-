package com.wangyunli.sentimentLexicon;

import java.util.HashMap;

import com.wangyl.config.Config;
import com.wangyl.tools.IOapi;

public class SentimentLexicon {
	public final static int neg=-1;
	public final static int pos=1;
	public final static int none=0;
	private static HashMap<String, Integer> sentimentLex=null;
	private static HashMap<String, Integer> evaluateLex=null;
	public static int GetSentiment(String word) {
		if(sentimentLex==null) {
			sentimentLex=LoadLexicon(Config.sentimentLexiconPosSrc_Tsinghua,Config.sentimentLexiconNegSrc_Tsinghua);
		}
		if(sentimentLex.containsKey(word)) {
			if(sentimentLex.get(word)==neg) {
				return neg;
			}
			else {
				return pos;
			}
		}
		else {
			return none;
		}
	}
	public static int GetEvaluation(String word) {
		if(evaluateLex==null) {
			evaluateLex=LoadLexicon(Config.sentimentLexiconPosSrc_Hownet,Config.sentimentLexiconNegSrc_Hownet);
		}
		if(evaluateLex.containsKey(word)) {
			if(evaluateLex.get(word)==neg) {
				return neg;
			}
			else {
				return pos;
			}
		}
		else {
			return none;
		}
	}
	private static HashMap<String, Integer> LoadLexicon(String posSrc,String negSrc) {
		HashMap<String, Integer> lexHashMap=new HashMap<String, Integer>();
		IOapi tmpIO=new IOapi(1);
		LoadOneFile(negSrc, tmpIO, lexHashMap, neg);
		LoadOneFile(posSrc, tmpIO, lexHashMap, pos);
		return lexHashMap;
	}
	private static void LoadOneFile(String src,IOapi tmpIO,HashMap<String, Integer> lexMap,int sentiment) {
		tmpIO.startRead(src, Config.encodingType, 0);
		String line=tmpIO.readOneSentence(0);
		while(line!=null) {
			if(!lexMap.containsKey(line.replace("\n", ""))) {
				lexMap.put(line.replace("\n", ""), sentiment);
			}
			line=tmpIO.readOneSentence(0);
		}
		tmpIO.endRead(0);
	}
}
