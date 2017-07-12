package com.wangyl.tools;

import java.util.ArrayList;

import org.thunlp.thulac.Split;
/**
 * show 词性标签说明：
 * n/名词 np/人名 ns/地名 ni/机构名 nz/其它专名
 * m/数词 q/量词 mq/数量词 t/时间词 f/方位词 s/处所词
 * v/动词 a/形容词 d/副词 h/前接成分 k/后接成分 i/习语 
 * j/简称 r/代词 c/连词 p/介词 u/助词 y/语气助词
 * e/叹词 o/拟声词 g/语素 w/标点 x/其它
 * 本类包含两个API，一个使用带词性标注的方法分词，一个使用不带词性标注的方法分词；
 * 两种方法可以混用，即一部分用第一个api分词，一部分用第二个api分词，但是每次切换api会重置模型参数，重新初始化模型;
 * 因此如无必要建议不要切换使用api;
 * 建议使用带词性标注的api，可配合其他api单独提取分词序列和词性序列;
 * @author WangYunli
 * 
 */
public class WordSegment {
	private static char separator='/';//默认的单词与词性之间的分隔符
	private static ArrayList<Term> terms=new ArrayList<Term>();//一个单词对应一个term，term中包括单词和词性标签
	private static Split sp=new Split();
	private static ArrayList<String> splitStreams=null;
	private static String nowString="";
	private static boolean methodWithTag=false;//是不是使用的带词性的方法分词的
	private static int idCounter=0;//记录nowString对应的id，同时也是一共分了多少句话
	public static void setSeparator(char separator) {
		WordSegment.separator = separator;
		sp.setSeparator(WordSegment.separator);
	}
	public static char getSeparator() {
		return separator;
	}
	public static ArrayList<Term> getTerms() {
		return terms;
	}
	public static String getNowString() {
		return nowString;
	}
	/**
	 * 不使用词性标注，只分词，分词效果可能比词性标注略差
	 */
	public static String splitWordwithOutTag(String str) {
	
		if((methodWithTag==true&&idCounter>0)||idCounter==0) {
			sp.setSeparator(' ');
			sp.setSegOnly(true);
			sp.setUseFilter(false);
		}
		idCounter++;
		methodWithTag=false;
		nowString=str;
		splitStreams=sp.split(str);
		return IOapi.StringArrayList2String(splitStreams, "").trim();
	}
	public static String splitWordwithTag(String str) {
		if((methodWithTag==false&&idCounter>0)||idCounter==0) {
			sp.setSeparator(separator);
			if(idCounter>0) {
				sp.setSegOnly(false);
				sp.setUseFilter(false);
			}
		}
		idCounter++;
		methodWithTag=true;
		terms.clear();
		nowString=str;
		splitStreams=sp.split(str);
		String []tmpStrArray=null;
		for(String ss:splitStreams) {
			tmpStrArray=ss.split(String.valueOf(separator));
			if(tmpStrArray.length!=2) {
				System.out.println("Error 10001:分词结果格式错误："+ss);
				continue;
			}
			else {
				terms.add(new Term(tmpStrArray[0],tmpStrArray[1]));
			}
		}
		return IOapi.StringArrayList2String(splitStreams, " ").trim();
	}
	public static String TermsNameToString(String separator) {
		StringBuffer strBuf=new StringBuffer("");
		if(idCounter<1||methodWithTag==false) {
			return "";
		}
		else {
			for(Term term:terms) {
				strBuf.append(term.name);
				strBuf.append(separator);
			}
			return strBuf.toString().trim();
		}
	}
	public static String TermsTagToString(String separator) {
		StringBuffer strBuf=new StringBuffer("");
		if(idCounter<1||methodWithTag==false) {
			return "";
		}
		else {
			for(Term term:terms) {
				strBuf.append(term.tag);
				strBuf.append(separator);
			}
			return strBuf.toString().trim();
		}
	}
}
class Term {
	public String name;
	public String tag;
	public Term() {
		this.name="";
		this.tag="";
	}
	public Term(String name,String tag) {
		this.name=name;
		this.tag=tag;
	}
}
