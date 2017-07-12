package com.wangyl.tools;

public class StringAnalyzer {

    //提取发送时间
	private static String extractSendTime(String ss) {
		String[] tokens = ss.split("{$}");
		return tokens[0];
	}
	private static String removeShortTerm(String ss){
		StringBuffer sb = new StringBuffer();
		String[] tokens = ss.split(" ");
		for(int i = 0;i<tokens.length;i++)
		{
			if(tokens[i].length()>1)
			{
				sb.append(tokens[i]);
				sb.append(" ");
			}
		}
		return sb.toString();
	}
	//提取中文字符，不带空格分割
	public static String extractChineseCharacterWithoutSpace(String ss) {
		StringBuffer str = new StringBuffer();
		char[] ch = ss.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			if(CharacterAnalyzer.isChinese(ch[i]))
			{
				str.append(ch[i]);
			}
		}
		return str.toString();
	}
	/**
	 * 用作文本预处理，提取出字符流中的中文，数字，英文；去除标点，分隔符，罗马数字，特殊字符等等
	 * @param ss 要处理的字符串
	 * @return 处理后的字符串
	 */
	public static String extractGoodCharacter(String ss){  
		if(ss == null)
			return null;
		StringBuffer str = new StringBuffer();
		char[] ch = ss.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			if(CharacterAnalyzer.isGoodCharacter(ch[i])){
				str.append(ch[i]);
			}else {
				str.append(' ');
			}
		}
		//trim()去掉字符串首尾的空格
		//replaceAll("\\s+", " ")将一个或多个空格替换为一个空格
		return str.toString().replaceAll("\\s+", " ").trim();
	}
	
	public static String extractChineseCharacter(String ss) {
		Boolean lastCharTag = true;
		StringBuffer str = new StringBuffer();
		char[] ch = ss.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			if(CharacterAnalyzer.isChinese(ch[i]))
			{
				if(lastCharTag)
				{
					str.append(ch[i]);
				}
				else
				{
					str.append(" ");
					str.append(ch[i]);
					lastCharTag = true;
				}
			}
			else
			{
				lastCharTag = false;
			}
		}
		//return removeShortTerm(str.toString());
		if(str.toString().length() == 0)
		{
			return "";
		}
		
		return str.toString().toLowerCase().trim();
	}
	
	public static String extractEnglishCharacter(String ss){
		Boolean lastCharTag = true;
		StringBuffer str = new StringBuffer();
		char[] ch = ss.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			if(CharacterAnalyzer.isEnglish(ch[i])){
				if(lastCharTag){
					str.append(ch[i]);
				}
				else{
					str.append(" ");
					str.append(ch[i]);
					lastCharTag = true;
				}
			}
			else{
				lastCharTag = false;
			}
		}
		if(str.toString().length() == 0){
			return null;
		}
		
		return str.toString().toLowerCase().trim();
	}
	
	public static Boolean isNumberString(String ss){
		char[] ch = ss.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			if(!CharacterAnalyzer.isNumber(ch[i]))
				return false;
		}
//		int telephoneNumberLength = 14;
//		if(ss.length()> telephoneNumberLength || ss.length() < 10)//TODO
//			return false;
//		
		return true;
	}

	public static Boolean isNumberString2(String ss){
		char[] ch = ss.toCharArray();
		for (int i = 1; i < ch.length-1; i++) {
			if(!CharacterAnalyzer.isNumber(ch[i]))
				return false;
		}
		int telephoneNumberLength = 14;
		if(ss.length()> telephoneNumberLength || ss.length() < 10)
			return false;
		
		return true;
	}
	
	
	
	public static String extractNumberCharacter(String ss){
		Boolean lastCharTag = true;
		StringBuffer str = new StringBuffer();
		char[] ch = ss.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			//if(characterAnalyzer.isNumber(ch[i])||characterAnalyzer.isSymbol(ch[i]))
			if(CharacterAnalyzer.isNumber(ch[i])){
				if(lastCharTag){
					str.append(ch[i]);
				}
				else{
					str.append(" ");
					str.append(ch[i]);
					lastCharTag = true;
				}
			}
			else{
				lastCharTag = false;
			}
		}
		if(str.toString().length() == 0){
			return null;
		}
		return str.toString();
	}
	
	public static void main(String args[]){
		String testString = "丨~~@昨天喝12了[酒]，今天|丨血压高。 大事没办了1 6，good woods小-事耽误了。 横批是：他阿了吊嬳!!!";
		testString="以前没觉悟，我反对放鞭炮。经被反复说教我改为支持：①壮国威，春节鞭炮的声/味/药俱全，且规模超任何战争，叙利亚/巴以/海湾…等战争弱爆了。②浓民味，放炮者可关门窗屋里放，年味更浓。③利经济，全民放炮增商机，避开经济升级压力：造鞭炮/治空污/灭火灾/清垃圾/医死伤…生意更多了，齐活啊！";
		testString="#感情不毕业# 长亭长，短亭短，一叹再一叹。      凉风凉，暖阳暖，明日又重欢。💕早上送你走 心情很难受 愿你一切安好 希望春节会见面❤️@Claire__隋敏加油 你的头像还是一如既往的丑！";
		System.out.println(extractGoodCharacter(testString));
		System.out.println(extractChineseCharacter(testString));
		System.out.println(extractEnglishCharacter(testString));
		System.out.println(extractNumberCharacter(testString));
	}
}
