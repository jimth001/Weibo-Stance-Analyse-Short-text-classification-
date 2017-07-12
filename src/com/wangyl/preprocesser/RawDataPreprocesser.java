package com.wangyl.preprocesser;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.wangyl.config.Config;
import com.wangyl.log.Log;
import com.wangyl.tools.FileOperateAPI;
import com.wangyl.tools.IOapi;
import com.wangyl.tools.StopWordsFilter;
import com.wangyl.tools.StringAnalyzer;
import com.wangyl.tools.WordSegment;
import com.wangyl.tools.WordSegment_Ansj;

import TypeTrans.Full2Half;

/**
 * show 
 * @author WangYunli
 * 
 */ 
public class RawDataPreprocesser {
	private static String rawDataSrc;//原始语料路径
	private static String outputSrc;//预处理后的文件输出路径
	private static String encodingType;//文件的编码方式
	private HashMap<String, Integer> topicIdMap=null;
	private static IOapi myIO=new IOapi(1);
	public static ArrayList<String> GetTopicsFromFiles(ArrayList<File> files,HashMap<String, Integer> rst) {
		rst.clear();
		ArrayList<String> arr=new ArrayList<String>();
		int id=0;
		for(File file:files) {
			if(rst.containsKey(GetClassNameFromFileName(file.getName()))) {
				continue;
			}
			else {
				rst.put(GetClassNameFromFileName(file.getName()), id);
				arr.add(GetClassNameFromFileName(file.getName()));
				id++;
			}
		}
		return arr;
	}
	public static String GetClassNameFromFileName(String filename) {
		return filename.split("_")[0];
	}
	public static String GetStanceFromFileName(String filename) {
		String []strs=filename.split("_");
		String []strs2=strs[1].split(".txt");
		return strs2[0];
	}
	public static String GetClassNameFromFileName(File file) {
		return GetClassNameFromFileName(file.getName());
	}
	public static String GetStanceFromFileName(File file) {
		return GetStanceFromFileName(file.getName());
	}
	public RawDataPreprocesser(String in_src,String out_src,String encodingString) {
		RawDataPreprocesser.rawDataSrc=in_src;
		RawDataPreprocesser.outputSrc=out_src;
		RawDataPreprocesser.encodingType=encodingString;
	}
	public void setOutputSrc(String outputSrc) {
		RawDataPreprocesser.outputSrc = outputSrc;
	}
	public void setRawDataSrc(String rawDataSrc) {
		RawDataPreprocesser.rawDataSrc = rawDataSrc;
	}
	public String getOutputSrc() {
		return outputSrc;
	}
	public String getRawDataSrc() {
		return rawDataSrc;
	}
	public ArrayList<StructuredPreproedData> GenerateValidationCorpus(String trainsrc,String testsrc,String outdir) {
		IOapi tmpIO=new IOapi(1);
		tmpIO.startRead(trainsrc, encodingType, 0);
		String line=tmpIO.readOneSentence(0);//要求第一行开始就是正式数据
		String []splitStrings=null;
		HashMap<String, Integer> classMap=new HashMap<String, Integer>();
		ArrayList<StructuredPreproedData> preproedatas=new ArrayList<StructuredPreproedData>();
		//加载训练数据：
		while(line!=null) {
			splitStrings=line.split("\t");
			if(splitStrings.length==4) {
				String rst=PreprocessOneLine(splitStrings[2], splitStrings[1]);
				String tmptopic=splitStrings[1].replace("iPhone SE", "IphoneSE").replace("俄罗斯叙利亚反恐行动", "俄罗斯在叙利亚的反恐行动");
				if(!classMap.containsKey(tmptopic)) {//如果该分类没出现过
					classMap.put(splitStrings[1], preproedatas.size());
					StructuredPreproedData tmpPreproedData=new StructuredPreproedData(true);
					tmpPreproedData.className=splitStrings[1].replace("iPhone SE", "IphoneSE").replace("俄罗斯叙利亚反恐行动", "俄罗斯在叙利亚的反恐行动");
					try {
						tmpPreproedData.AddData(rst, splitStrings[3]);
						tmpPreproedData.AddOrgData(splitStrings[2], splitStrings[3]);
					} catch (Exception e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
					preproedatas.add(tmpPreproedData);
				}
				else {
					try {
						preproedatas.get(classMap.get(tmptopic).intValue()).AddData(rst, splitStrings[3]);
						preproedatas.get(classMap.get(tmptopic).intValue()).AddOrgData(splitStrings[2], splitStrings[3]);
					} catch (Exception e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
				}
			}
			else if(splitStrings.length==3) {//没有类别标签，认为是UNKNOWN
				String rst=PreprocessOneLine(splitStrings[2], splitStrings[1]);
				String tmptopic=splitStrings[1].replace("iPhone SE", "IphoneSE").replace("俄罗斯叙利亚反恐行动", "俄罗斯在叙利亚的反恐行动");
				if(!classMap.containsKey(tmptopic)) {//如果该分类没出现过
					classMap.put(splitStrings[1], preproedatas.size());
					StructuredPreproedData tmpPreproedData=new StructuredPreproedData(true);
					tmpPreproedData.className=splitStrings[1].replace("iPhone SE", "IphoneSE").replace("俄罗斯叙利亚反恐行动", "俄罗斯在叙利亚的反恐行动");
					try {
						tmpPreproedData.AddData(rst, Config.unknown);
						tmpPreproedData.AddOrgData(splitStrings[2], Config.unknown);
					} catch (Exception e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
					preproedatas.add(tmpPreproedData);
				}
				else {
					try {
						preproedatas.get(classMap.get(tmptopic).intValue()).AddData(rst, Config.unknown);
						preproedatas.get(classMap.get(tmptopic).intValue()).AddOrgData(splitStrings[2], Config.unknown);
					} catch (Exception e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
				}
			}
			else {//长度error，说明微博文本中也出现了tab
				Log.LogInf("error 10002:分割长度错误："+splitStrings.length+";原句为："+line);
			}
			line=tmpIO.readOneSentence(0);
		}
		tmpIO.endRead(0);
		//加载测试数据：
		tmpIO.startRead(testsrc, encodingType, 0);
		line=tmpIO.readOneSentence(0);
		while(line!=null) {
			splitStrings=line.split("	");
			if(splitStrings.length==4) {
				String rst=PreprocessOneLine(splitStrings[2], splitStrings[1]);
				String tmptopic=splitStrings[1].replace("iPhone SE", "IphoneSE").replace("俄罗斯叙利亚反恐行动", "俄罗斯在叙利亚的反恐行动");
				if(!classMap.containsKey(tmptopic)) {//如果该分类没出现过
					classMap.put(splitStrings[1], preproedatas.size());
					StructuredPreproedData tmpPreproedData=new StructuredPreproedData(true);
					tmpPreproedData.className=splitStrings[1].replace("iPhone SE", "IphoneSE").replace("俄罗斯叙利亚反恐行动", "俄罗斯在叙利亚的反恐行动");
					try {
						tmpPreproedData.AddData(rst, splitStrings[3]);
						tmpPreproedData.AddOrgData(splitStrings[2], splitStrings[3]);
					} catch (Exception e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
					preproedatas.add(tmpPreproedData);
				}
				else {
					try {
						preproedatas.get(classMap.get(tmptopic).intValue()).AddData(rst, splitStrings[3]);
						preproedatas.get(classMap.get(tmptopic).intValue()).AddOrgData(splitStrings[2], splitStrings[3]);
					} catch (Exception e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
				}
			}
			else if(splitStrings.length==3) {//没有类别标签，认为是UNKNOWN
				String rst=PreprocessOneLine(splitStrings[2], splitStrings[1]);
				String tmptopic=splitStrings[1].replace("iPhone SE", "IphoneSE").replace("俄罗斯叙利亚反恐行动", "俄罗斯在叙利亚的反恐行动");
				if(!classMap.containsKey(tmptopic)) {//如果该分类没出现过
					classMap.put(splitStrings[1], preproedatas.size());
					StructuredPreproedData tmpPreproedData=new StructuredPreproedData(true);
					tmpPreproedData.className=splitStrings[1].replace("iPhone SE", "IphoneSE").replace("俄罗斯叙利亚反恐行动", "俄罗斯在叙利亚的反恐行动");
					try {
						tmpPreproedData.AddData(rst, Config.unknown);
						tmpPreproedData.AddOrgData(splitStrings[2], Config.unknown);
					} catch (Exception e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
					preproedatas.add(tmpPreproedData);
				}
				else {
					try {
						preproedatas.get(classMap.get(tmptopic).intValue()).AddData(rst, Config.unknown);
						preproedatas.get(classMap.get(tmptopic).intValue()).AddOrgData(splitStrings[2], Config.unknown);
					} catch (Exception e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
				}
			}
			else {//长度error，说明微博文本中也出现了tab
				Log.LogInf("error 10002:分割长度错误："+splitStrings.length+";原句为："+line);
			}
			line=tmpIO.readOneSentence(0);
		}
		tmpIO.endRead(0);
		//将数据写入validationset下：
		for(int i=0;i<preproedatas.size();i++) {
			StructuredPreproedData tmppreprodata=preproedatas.get(i);
			if(tmppreprodata.favor.size()>0) {
				tmpIO.startWrite(outdir+tmppreprodata.className+"_"+Config.favor+".txt", encodingType, 0,false);
				int size=tmppreprodata.favor.size();
				for(int j=0;j<size;j++) {
					tmpIO.writeOneString(tmppreprodata.favor.get(j)+"\n", 0);
				}
				tmpIO.endWrite(0);
			}
			if(tmppreprodata.infavor.size()>0) {
				tmpIO.startWrite(outdir+tmppreprodata.className+"_"+Config.infavor+".txt", encodingType, 0,false);
				int size=tmppreprodata.infavor.size();
				for(int j=0;j<size;j++) {
					tmpIO.writeOneString(tmppreprodata.infavor.get(j)+"\n", 0);
				}
				tmpIO.endWrite(0);
			}
			if(tmppreprodata.none.size()>0) {
				tmpIO.startWrite(outdir+tmppreprodata.className+"_"+Config.none+".txt", encodingType, 0,false);
				int size=tmppreprodata.none.size();
				for(int j=0;j<size;j++) {
					tmpIO.writeOneString(tmppreprodata.none.get(j)+"\n", 0);
				}
				tmpIO.endWrite(0);
			}
			if(tmppreprodata.unknown.size()>0) {
				tmpIO.startWrite(outdir+tmppreprodata.className+"_"+Config.unknown+".txt", encodingType, 0,false);
				int size=tmppreprodata.unknown.size();
				for(int j=0;j<size;j++) {
					tmpIO.writeOneString(tmppreprodata.unknown.get(j)+"\n", 0);
				}
				tmpIO.endWrite(0);
			}
		}
		topicIdMap=classMap;
		return preproedatas;
	}
	/**
	 * @description 训练集预处理
	 */
	public ArrayList<StructuredPreproedData> PreprocessTrainData(){
		IOapi tmpIO=new IOapi(1);
		tmpIO.startRead(rawDataSrc, encodingType, 0);
		String line=tmpIO.readOneSentence(0);//要求第一行开始就是正式数据
		String []splitStrings=null;
		HashMap<String, Integer> classMap=new HashMap<String, Integer>();
		ArrayList<StructuredPreproedData> preproedatas=new ArrayList<StructuredPreproedData>();
		while(line!=null) {
			splitStrings=line.split("	");
			if(splitStrings.length==4) {
				String rst=PreprocessOneLine(splitStrings[2], splitStrings[1]);
				if(!classMap.containsKey(splitStrings[1])) {//如果该分类没出现过
					classMap.put(splitStrings[1], preproedatas.size());
					StructuredPreproedData tmpPreproedData=new StructuredPreproedData(true);
					tmpPreproedData.className=splitStrings[1].replace("iPhone SE", "IphoneSE").replace("俄罗斯叙利亚反恐行动", "俄罗斯在叙利亚的反恐行动");
					try {
						tmpPreproedData.AddData(rst, splitStrings[3]);
						tmpPreproedData.AddOrgData(splitStrings[2], splitStrings[3]);
					} catch (Exception e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
					preproedatas.add(tmpPreproedData);
				}
				else {
					try {
						preproedatas.get(classMap.get(splitStrings[1]).intValue()).AddData(rst, splitStrings[3]);
						preproedatas.get(classMap.get(splitStrings[1]).intValue()).AddOrgData(splitStrings[2], splitStrings[3]);
					} catch (Exception e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
				}
			}
			else if(splitStrings.length==3) {//没有类别标签，认为是UNKNOWN
				String rst=PreprocessOneLine(splitStrings[2], splitStrings[1]);
				if(!classMap.containsKey(splitStrings[1])) {//如果该分类没出现过
					classMap.put(splitStrings[1], preproedatas.size());
					StructuredPreproedData tmpPreproedData=new StructuredPreproedData(true);
					tmpPreproedData.className=splitStrings[1].replace("iPhone SE", "IphoneSE").replace("俄罗斯叙利亚反恐行动", "俄罗斯在叙利亚的反恐行动");
					try {
						tmpPreproedData.AddData(rst, Config.unknown);
						tmpPreproedData.AddOrgData(splitStrings[2], Config.unknown);
					} catch (Exception e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
					preproedatas.add(tmpPreproedData);
				}
				else {
					try {
						preproedatas.get(classMap.get(splitStrings[1]).intValue()).AddData(rst, Config.unknown);
						preproedatas.get(classMap.get(splitStrings[1]).intValue()).AddOrgData(splitStrings[2], Config.unknown);
					} catch (Exception e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
				}
			}
			else {//长度error，说明微博文本中也出现了tab
				Log.LogInf("error 10002:分割长度错误："+splitStrings.length+";原句为："+line);
			}
			line=tmpIO.readOneSentence(0);
		}
		tmpIO.endRead(0);
		for(int i=0;i<preproedatas.size();i++) {
			StructuredPreproedData tmppreprodata=preproedatas.get(i);
			if(tmppreprodata.favor.size()>0) {
				tmpIO.startWrite(outputSrc+tmppreprodata.className+"_"+Config.favor+".txt", encodingType, 0,true);
				int size=tmppreprodata.favor.size();
				for(int j=0;j<size;j++) {
					tmpIO.writeOneString(tmppreprodata.favor.get(j)+"\n", 0);
				}
				tmpIO.endWrite(0);
			}
			if(tmppreprodata.infavor.size()>0) {
				tmpIO.startWrite(outputSrc+tmppreprodata.className+"_"+Config.infavor+".txt", encodingType, 0,true);
				int size=tmppreprodata.infavor.size();
				for(int j=0;j<size;j++) {
					tmpIO.writeOneString(tmppreprodata.infavor.get(j)+"\n", 0);
				}
				tmpIO.endWrite(0);
			}
			if(tmppreprodata.none.size()>0) {
				tmpIO.startWrite(outputSrc+tmppreprodata.className+"_"+Config.none+".txt", encodingType, 0,true);
				int size=tmppreprodata.none.size();
				for(int j=0;j<size;j++) {
					tmpIO.writeOneString(tmppreprodata.none.get(j)+"\n", 0);
				}
				tmpIO.endWrite(0);
			}
			if(tmppreprodata.unknown.size()>0) {
				tmpIO.startWrite(outputSrc+tmppreprodata.className+"_"+Config.unknown+".txt", encodingType, 0,true);
				int size=tmppreprodata.unknown.size();
				for(int j=0;j<size;j++) {
					tmpIO.writeOneString(tmppreprodata.unknown.get(j)+"\n", 0);
				}
				tmpIO.endWrite(0);
			}
		}
		topicIdMap=classMap;
		return preproedatas;
	}
	/**
	 * @description 测试集预处理
	 */
	public ArrayList<StructuredPreproedData> PreprocessTestData() {
		return PreprocessTrainData();
	}
	public static ArrayList<StructuredPreproedData> LoadOrgData(String rawdatasrc,HashMap<String, Integer> classidmap) {
		IOapi tmpIO=new IOapi(1);
		tmpIO.startRead(rawdatasrc, encodingType, 0);
		String line=tmpIO.readOneSentence(0);//要求第一行开始就是正式数据
		String []splitStrings=null;
		HashMap<String, Integer> classMap=new HashMap<String, Integer>();
		ArrayList<StructuredPreproedData> preproedatas=new ArrayList<StructuredPreproedData>();
		while(line!=null) {
			splitStrings=line.split("	");
			if(splitStrings.length==4) {
				if(!classMap.containsKey(splitStrings[1])) {//如果该分类没出现过
					classMap.put(splitStrings[1], preproedatas.size());
					StructuredPreproedData tmpPreproedData=new StructuredPreproedData(true);
					tmpPreproedData.className=splitStrings[1].replace("iPhone SE", "IphoneSE").replace("俄罗斯叙利亚反恐行动", "俄罗斯在叙利亚的反恐行动");
					try {
						tmpPreproedData.AddOrgData(splitStrings[2], splitStrings[3]);
					} catch (Exception e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
					preproedatas.add(tmpPreproedData);
				}
				else {
					try {
						preproedatas.get(classMap.get(splitStrings[1]).intValue()).AddOrgData(splitStrings[2], splitStrings[3]);
					} catch (Exception e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
				}
			}
			else if(splitStrings.length==3) {//没有类别标签，认为是UNKNOWN
				if(!classMap.containsKey(splitStrings[1])) {//如果该分类没出现过
					classMap.put(splitStrings[1], preproedatas.size());
					StructuredPreproedData tmpPreproedData=new StructuredPreproedData(true);
					tmpPreproedData.className=splitStrings[1].replace("iPhone SE", "IphoneSE").replace("俄罗斯叙利亚反恐行动", "俄罗斯在叙利亚的反恐行动");
					try {
						tmpPreproedData.AddOrgData(splitStrings[2], Config.unknown);
					} catch (Exception e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
					preproedatas.add(tmpPreproedData);
				}
				else {
					try {
						preproedatas.get(classMap.get(splitStrings[1]).intValue()).AddOrgData(splitStrings[2], Config.unknown);
					} catch (Exception e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
				}
			}
			else {//长度error，说明微博文本中也出现了tab
				Log.LogInf("error 10002:分割长度错误："+splitStrings.length+";原句为："+line);
			}
			line=tmpIO.readOneSentence(0);
		}
		tmpIO.endRead(0);
		if(classidmap!=null) {
			classidmap=classMap;
		}
		return preproedatas;
	}
	private String PreprocessOneLine(String line,String topic) {
		try {
			return PreProcessText.preProcess4NLPCC2016(line, topic);
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			return "";
		}
	}
	/**
	 * @description 生成好preprocesseddata之后，使用该函数，在lsaCorpusSrc路径下生成指定topic的lsa训练语料
	 * @param topic
	 * @param senNumOfCorpus
	 * @throws Exception
	 */
	public static void PreproForLSA(String topic,int senNumOfCorpus) throws Exception {
		//先清空lsa输入文件夹下的原有文件：
		FileOperateAPI.DeleteFolder(Config.lsaCorpusDir);
		//再把指定topic的输入文件生成到lsaCorpusSrc路径下：
		String fname=topic+"_"+Config.favor+".txt";
		String iname=topic+"_"+Config.infavor+".txt";
		String nname=topic+"_"+Config.none+".txt";
		//把favor文件拆写到指定位置：
		myIO.startRead(Config.preprocessedTrainDataOutputSrc+fname, Config.encodingType, 0);
		String line=myIO.readOneSentence(0);
		int docCounter=0;
		while(line!=null) {
			myIO.startWrite(Config.lsaCorpusDir+topic+"_"+Config.favor+"_"+docCounter+".txt", Config.encodingType, 0);
			for(int i=0;i<senNumOfCorpus&&line!=null;i++) {
				myIO.writeOneString(line+"\n", 0);
				line=myIO.readOneSentence(0);
			}
			myIO.endWrite(0);
			docCounter++;
		}
		myIO.endRead(0);
		//把infavor文件拆写到指定位置：
		myIO.startRead(Config.preprocessedTrainDataOutputSrc+iname, Config.encodingType, 0);
		line=myIO.readOneSentence(0);
		docCounter=0;
		while(line!=null) {
			myIO.startWrite(Config.lsaCorpusDir+topic+"_"+Config.infavor+"_"+docCounter+".txt", Config.encodingType, 0);
			for(int i=0;i<senNumOfCorpus&&line!=null;i++) {
				myIO.writeOneString(line+"\n", 0);
				line=myIO.readOneSentence(0);
			}
			myIO.endWrite(0);
			docCounter++;
		}
		myIO.endRead(0);
		//把none文件拆写到指定位置：
		myIO.startRead(Config.preprocessedTrainDataOutputSrc+nname, Config.encodingType, 0);
		line=myIO.readOneSentence(0);
		docCounter=0;
		while(line!=null) {
			myIO.startWrite(Config.lsaCorpusDir+topic+"_"+Config.none+"_"+docCounter+".txt", Config.encodingType, 0);
			for(int i=0;i<senNumOfCorpus&&line!=null;i++) {
				myIO.writeOneString(line+"\n", 0);
				line=myIO.readOneSentence(0);
			}
			myIO.endWrite(0);
			docCounter++;
		}
		myIO.endRead(0);
	}
	
	public static void PreproForLSA(String topic) throws Exception {
		PreproForLSA(topic,1);
	}
	public static void PreproForLDAGVM(String topic,int groupnum) throws IOException {
		List<File> fileList = new ArrayList<File>();
		FileOperateAPI.visitDirsAllFiles(Config.preprocessedTrainDataOutputSrc, fileList);
		ArrayList<String> favorStrList=new ArrayList<String>();
		ArrayList<String> infavorStrList=new ArrayList<String>();
		ArrayList<String> noneStrList=new ArrayList<String>();
		ArrayList<String> tmpStrList;
		for (File file : fileList) {
			if(!file.getName().contains(topic)) {//如果文件名不包含topic,跳过。注意要保证文件命名符合规范。
				continue;
			}
			if(GetStanceFromFileName(file.getName()).equals(Config.favor)) {
				tmpStrList=favorStrList;
			}
			else if(GetStanceFromFileName(file.getName()).equals(Config.infavor)) {
				tmpStrList=infavorStrList;
			}
			else {
				tmpStrList=noneStrList;
			}
			myIO.startRead(file, Config.encodingType, 0);
			String line=myIO.readOneSentence(0);
			while(line!=null) {
				tmpStrList.add(StopWordsFilter.filterSW(line));
				line=myIO.readOneSentence(0);
			}
			myIO.endRead(0);
		}
		myIO.startWrite(Config.ldaGVMCorpusDir+"\\"+topic+"\\"+topic+".txt", Config.encodingType, 0);
		int rows=0;
		if(favorStrList.size()%groupnum==0) {
			rows+=(favorStrList.size()/groupnum);
		}
		else {
			rows+=(favorStrList.size()/groupnum+1);
		}
		if(infavorStrList.size()%groupnum==0) {
			rows+=(infavorStrList.size()/groupnum);
		}
		else {
			rows+=(infavorStrList.size()/groupnum+1);
		}
		if(noneStrList.size()%groupnum==0) {
			rows+=(noneStrList.size()/groupnum);
		}
		else {
			rows+=(noneStrList.size()/groupnum+1);
		}
		myIO.writeOneString(rows+"\n", 0);
		int counter=0;
		//先写favor部分：
		counter=0;
		for(String s:favorStrList) {
			myIO.writeOneString(s+" ", 0);
			counter++;
			if(counter==groupnum) {
				counter=0;
				myIO.writeOneString("\n", 0);
			}
		}
		if(favorStrList.size()%groupnum!=0) {
			myIO.writeOneString("\n", 0);
		}
		//再写infavor部分：
		counter=0;
		for(String s:infavorStrList) {
			myIO.writeOneString(s+" ", 0);
			counter++;
			if(counter==groupnum) {
				counter=0;
				myIO.writeOneString("\n", 0);
			}
		}
		if(infavorStrList.size()%groupnum!=0) {
			myIO.writeOneString("\n", 0);
		}
		//再写none部分：
		counter=0;
		for(String s:noneStrList) {
			myIO.writeOneString(s+" ", 0);
			counter++;
			if(counter==groupnum) {
				counter=0;
				myIO.writeOneString("\n", 0);
			}
		}
		if(noneStrList.size()%groupnum!=0) {
			myIO.writeOneString("\n", 0);
		}
		myIO.endWrite(0);
		//在preproedTrainDataForLDAGVM下生成相关数据：
		//写favor：
		myIO.startWrite(Config.preproedTrainDataForLDAGVM+topic+"_"+Config.favor+".txt", Config.encodingType, 0);
		counter=0;
		for(String s:favorStrList) {
			myIO.writeOneString(s+" ", 0);
			counter++;
			if(counter==groupnum) {
				counter=0;
				myIO.writeOneString("\n", 0);
			}
		}
		if(favorStrList.size()%groupnum!=0) {
			myIO.writeOneString("\n", 0);
		}
		myIO.endWrite(0);
		//写infavor：
		myIO.startWrite(Config.preproedTrainDataForLDAGVM+topic+"_"+Config.infavor+".txt", Config.encodingType, 0);
		counter=0;
		for(String s:infavorStrList) {
			myIO.writeOneString(s+" ", 0);
			counter++;
			if(counter==groupnum) {
				counter=0;
				myIO.writeOneString("\n", 0);
			}
		}
		if(infavorStrList.size()%groupnum!=0) {
			myIO.writeOneString("\n", 0);
		}
		myIO.endWrite(0);
		//写none：
		myIO.startWrite(Config.preproedTrainDataForLDAGVM+topic+"_"+Config.none+".txt", Config.encodingType, 0);
		counter=0;
		for(String s:noneStrList) {
			myIO.writeOneString(s+" ", 0);
			counter++;
			if(counter==groupnum) {
				counter=0;
				myIO.writeOneString("\n", 0);
			}
		}
		if(noneStrList.size()%groupnum!=0) {
			myIO.writeOneString("\n", 0);
		}
		myIO.endWrite(0);
	}
	/**
	 * @description topic mixed，在LDAinputDir下生产lda训练统一输入文件LDAInput.txt
	 * @throws IOException
	 */
	public static void PreproForLDA() throws IOException {
		List<File> fileList = new ArrayList<File>();
		FileOperateAPI.visitDirsAllFiles(Config.preprocessedTrainDataOutputSrc, fileList);
		ArrayList<String> strlist=new ArrayList<String>();
		for (File file : fileList) {
			myIO.startRead(file, Config.encodingType, 0);
			String line=myIO.readOneSentence(0);
			while(line!=null) {
				strlist.add(StopWordsFilter.filterSW(line)+"\n");
				line=myIO.readOneSentence(0);
			}
			myIO.endRead(0);
		}
		myIO.startWrite(Config.LDAInputSrc, Config.encodingType, 0);
		myIO.writeOneString(strlist.size()+"\n", 0);
		for(String s:strlist) {
			myIO.writeOneString(s, 0);
		}
		myIO.endWrite(0);
	}
	/**
	 * @description only one topic,为指定的topic生成lda训练输入文件
	 * @param topic
	 * @throws IOException 
	 */
	public static void PreproForLDA(String topic) throws IOException {
		List<File> fileList = new ArrayList<File>();
		FileOperateAPI.visitDirsAllFiles(Config.preprocessedTrainDataOutputSrc, fileList);
		ArrayList<String> strlist=new ArrayList<String>();
		for (File file : fileList) {
			if(!file.getName().contains(topic)) {//如果文件名不包含topic,跳过。注意要保证文件命名符合规范。
				continue;
			}
			myIO.startRead(file, Config.encodingType, 0);
			String line=myIO.readOneSentence(0);
			while(line!=null) {
				strlist.add(StopWordsFilter.filterSW(line)+"\n");
				line=myIO.readOneSentence(0);
			}
			myIO.endRead(0);
		}
		myIO.startWrite(Config.LDAInputDir+"\\"+topic+"\\"+topic+".txt", Config.encodingType, 0);
		myIO.writeOneString(strlist.size()+"\n", 0);
		for(String s:strlist) {
			myIO.writeOneString(s, 0);
		}
		myIO.endWrite(0);
	}
	public static ArrayList<StructuredPreproedData> LoadTrainDataFromPreproedFile() throws Exception {
		return LoadTrainDataFromPreproedFile(new HashMap<String, Integer>());
	}
	public static ArrayList<StructuredPreproedData> LoadTrainDataFromPreproedFile(HashMap<String, Integer> topicidmap) throws Exception {
		List<File> fileList = new ArrayList<File>();
		HashMap<String, Integer> topicIdMap=topicidmap;
		ArrayList<StructuredPreproedData> rst=new ArrayList<StructuredPreproedData>();
		FileOperateAPI.visitDirsAllFiles(Config.preprocessedTrainDataOutputSrc, fileList);
		int counter=0;
		for(File file:fileList) {
			String []strs=file.getName().replace(".txt", "").split("_");
			if(strs.length!=2) {
				throw new IOException("从预处理后的文件解析训练数据时出错，错误的文件名格式!文件名："+file.getName());
			}
			if(topicIdMap.containsKey(strs[0])) {
				StructuredPreproedData spd=rst.get(topicIdMap.get(strs[0]).intValue());
				myIO.startRead(file, Config.encodingType, 0);
				String line=myIO.readOneSentence(0);
				while(line!=null) {
					spd.AddData(line, strs[1]);
					line=myIO.readOneSentence(0);
				}
				myIO.endRead(0);
			}
			else {
				StructuredPreproedData spd=new StructuredPreproedData(false);
				rst.add(spd);
				topicIdMap.put(strs[0], counter);
				counter++;
				spd.className=strs[0];
				myIO.startRead(file, Config.encodingType, 0);
				String line=myIO.readOneSentence(0);
				while(line!=null) {
					spd.AddData(line, strs[1]);
					line=myIO.readOneSentence(0);
				}
				myIO.endRead(0);
			}
		}
		return rst;
	}
	public static ArrayList<StructuredPreproedData> LoadTestDataFromPreproedFile() throws Exception {
		return LoadTestDataFromPreproedFile(new HashMap<String, Integer>());
	}
	public static ArrayList<StructuredPreproedData> LoadTestDataFromPreproedFile(HashMap<String, Integer> topicidmap) throws Exception {
		List<File> fileList = new ArrayList<File>();
		HashMap<String, Integer> topicIdMap=topicidmap;
		ArrayList<StructuredPreproedData> rst=new ArrayList<StructuredPreproedData>();
		FileOperateAPI.visitDirsAllFiles(Config.preprocessedTestDataOutputSrc, fileList);
		int counter=0;
		for(File file:fileList) {
			String []strs=file.getName().replace(".txt", "").split("_");
			if(strs.length!=2) {
				throw new IOException("从预处理后的文件解析测试数据时出错，错误的文件名格式!文件名："+file.getName());
			}
			if(topicIdMap.containsKey(strs[0])) {
				StructuredPreproedData spd=rst.get(topicIdMap.get(strs[0]).intValue());
				myIO.startRead(file, Config.encodingType, 0);
				String line=myIO.readOneSentence(0);
				while(line!=null) {
					spd.AddData(line, strs[1]);
					line=myIO.readOneSentence(0);
				}
				myIO.endRead(0);
			}
			else {
				StructuredPreproedData spd=new StructuredPreproedData(false);
				rst.add(spd);
				topicIdMap.put(strs[0], counter);
				counter++;
				spd.className=strs[0];
				myIO.startRead(file, Config.encodingType, 0);
				String line=myIO.readOneSentence(0);
				while(line!=null) {
					spd.AddData(line, strs[1]);
					line=myIO.readOneSentence(0);
				}
				myIO.endRead(0);
			}
		}
		return rst;
	}
}

class PreProcessText {
	static public String preProcess4Task1(String inputStr, String tmpRelationP, String tmpEntityS, String tmpEntityO) throws IOException{
		if (inputStr.length()<1) return inputStr;
		//强制限制了实体词分开
		if (tmpRelationP!=null) {
			if (tmpRelationP.length()==4) { //传闻不和、同为校花、昔日情敌、绯闻女友 等关系 重心词在后面
				if (!inputStr.contains(tmpRelationP)) {
					tmpRelationP = tmpRelationP.substring(2);
				}
			} 
			inputStr = inputStr.replaceAll(tmpRelationP, " "+tmpRelationP+" ");
		}
		inputStr = inputStr.replaceAll(tmpEntityS, " "+tmpEntityS+" ");
		inputStr = inputStr.replaceAll(tmpEntityO, " "+tmpEntityO+" ");
		inputStr=Full2Half.ToDBC(inputStr);//全角转半角						
		inputStr=inputStr.toLowerCase();//字母全部小写
		inputStr=inputStr.replaceAll("\\s+", " ");//多个空格缩成单个空格
		inputStr = StringAnalyzer.extractGoodCharacter(inputStr); //去除所有特殊字符
		//                           无词性                                                                       带词性
		inputStr = WordSegment_Ansj.splitWord(inputStr)+"\t"+WordSegment_Ansj.splitWordwithTag(inputStr);//进行分词
		
		return inputStr;
	} 
	static public String preProcess4Task2(String inputStr) throws IOException{
		if (inputStr.length()<1) return inputStr;
		inputStr=Full2Half.ToDBC(inputStr);//全角转半角						
		inputStr=inputStr.toLowerCase();//字母全部小写
		inputStr=inputStr.replaceAll("\\s+", " ");//多个空格缩成单个空格
		inputStr = StringAnalyzer.extractGoodCharacter(inputStr); //去除所有特殊字符
		//                           无词性                                                                       带词性
		inputStr = WordSegment_Ansj.splitWordwithOutTag4Task2(inputStr);//进行分词
		
		return inputStr.trim();
	}
	/**
	 * 文本预处理顺序：去除带标题的HashTag；去除url；去除分享标识；去除@标识；
	 * 全角转半角；字母转小写；将文中的多个连续空格转换为一个空格；去除所有特殊字符；
	 * 分词（使用thulac API）
	 * @author WangYunli
	 * @date 2017-03-28
	 */
	static public String preProcess4NLPCC2016(String inputStr, String topic) throws IOException{
		if (inputStr.length()<1) return inputStr;
		inputStr=inputStr.replaceAll("#"+topic+"#", " ");//（1）过滤掉HashTag的标识
		//inputStr=inputStr.replaceAll("http://t.cn/(.{7})", " ");错误的正则表达式
		inputStr=inputStr.replaceAll("[a-zA-z]+://[^\\s()（）\\t]*", " ");//（2）过滤掉http://t.cn/[7个字符]
		//（3）过滤掉一些特殊的分享标识，如：
		inputStr=inputStr.replaceAll("（分享[^）]*）", " ");
		inputStr=inputStr.replaceAll("\\(分享[^\\)]*\\)", " ");
		inputStr=inputStr.replaceAll("【分享[^】]*】", " ");
		inputStr=inputStr.replaceAll("（来自[^）]*）", " ");
		inputStr=inputStr.replaceAll("\\(来自[^\\)]*\\)", " ");
		inputStr=inputStr.replaceAll("【来自[^】]*】", " ");
		//（4）过滤掉所有的@标识，如@腾讯新闻客户端 @[10个字符以内]后接另一个@、空格或换行符
		String[] inputStr_sub = inputStr.split("\\s+");
		StringBuffer inputStr_bf = new StringBuffer();
		for (String tmpinputStr_sub:inputStr_sub) {
			tmpinputStr_sub = tmpinputStr_sub+"<eos>";
			tmpinputStr_sub = tmpinputStr_sub.replaceAll("@(.{0,9})@", "@");	
			tmpinputStr_sub = tmpinputStr_sub.replaceAll("@(.{0,9}) ", " ");
			tmpinputStr_sub = tmpinputStr_sub.replaceAll("@(.{0,9})<eos>", " ");
			tmpinputStr_sub = tmpinputStr_sub.replaceAll("<eos>", "");
			inputStr_bf.append(tmpinputStr_sub);
			inputStr_bf.append(" ");
		}
		inputStr = inputStr_bf.toString().trim();
		inputStr_bf = null;	
		inputStr=Full2Half.ToDBC(inputStr);//（5）全角转半角					
		inputStr=inputStr.toLowerCase();//（6）字母全部小写
		inputStr=inputStr.replaceAll("\\s+", " ");//（7）多个空格缩成单个空格
		inputStr = StringAnalyzer.extractGoodCharacter(inputStr); //（8）去除所有特殊字符
		inputStr=WordSegment.splitWordwithTag(inputStr);//（9）进行分词
		inputStr=WordSegment.TermsNameToString(" ");//（9）进行分词
		//inputStr=WordSegment_Ansj.splitWord(inputStr);
		
		return inputStr.trim();
	} 	

	private static boolean isITSuffixSpamInfo(String tmpQuerySnippet, String tmpEntityS, String tmpEntityO) {
		if ((tmpQuerySnippet.contains(tmpEntityS)||tmpQuerySnippet.contains(tmpEntityO))
				&&tmpQuerySnippet.length()>4) {
			return false;
		}else {
			return true;
		}
	}
}