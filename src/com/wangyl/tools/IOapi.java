package com.wangyl.tools;
/**
 * @author WangYunli
 *
 */ 
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
/**
 * @author WangYunli
 * IOapi Usage:
 * @IOapi(int maxStreamNum)
 * maxStreamNum指该类可以创建多少个Stream同时读写文件，会创建maxStreamNum个读Stream和maxStreamNum个写Stream
 * @startRead(String filepath,String encodingString,int Readernum)
 * 开始读取，指定文件路径，编码格式，Stream编号
 * @endRead(int Readernum)
 * 关闭读取Stream，释放资源，Readernum指要关闭的Stream编号
 * @String readOneSentence(int Readernum)
 * 读取一行
 * @int readint(int readernum)
 * 读取一个整型数
 * @startWrite(String filepath,String encodingString,int Writernum)
 * 初始化一个写Stream
 * void endWrite(int Writernum)
 * 结束写入，释放写Stream
 * void writeOneString(String s,int Writernum)
 * 写一个String
 * void writeStringBufferIntoTXT(StringBuffer buf,int Writernum)
 * 将StringBuffer的内容写到txt
 * void writechars(char []a,int writernum)
 * 将char数组写入文件
 * static boolean isFileExist(String path)
 * 判断文件是否存在，返回boolean类型
 * public static String GetCurrentDir()
 * 获取当前路径
 * */
public class IOapi {
	private InputStreamReader read[];
	private BufferedReader bufread[];
	private OutputStreamWriter writer[];
	private BufferedWriter bufwriter[];
	private boolean isAppendMode[];
	public IOapi(int maxStreamNum)
	{
		read=new InputStreamReader[maxStreamNum];
		bufread=new BufferedReader[maxStreamNum];
		writer=new OutputStreamWriter[maxStreamNum];
		bufwriter=new BufferedWriter[maxStreamNum];
		isAppendMode=new boolean[maxStreamNum];
		int i;
		for(i=0;i<maxStreamNum;i++)
		{
			read[i]=null;
			bufread[i]=null;
			writer[i]=null;
			bufwriter[i]=null;
			isAppendMode[i]=false;
		}
	}
	public void startRead(String filepath,String encodingString,int Readernum)
	{
		try {
			read[Readernum] = new InputStreamReader(new FileInputStream(filepath), encodingString);
			bufread[Readernum] = new BufferedReader(read[Readernum]);
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
	}
	public void startRead(File file,String encodingString,int Readernum)
	{
		try {
			read[Readernum] = new InputStreamReader(new FileInputStream(file), encodingString);
			bufread[Readernum] = new BufferedReader(read[Readernum]);
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
	}
	public void endRead(int Readernum)
	{
		if(bufread!=null)
			try {
				bufread[Readernum].close();
				bufread[Readernum]=null;
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		if(read!=null)
			try {
				read[Readernum].close();
				read[Readernum]=null;
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
	}
	public static void makeSureDirExists(String dir) {
		File file = new File(dir);
		if(!file.exists()){  
		    file.mkdirs();  
		}
	}
	public void startWrite(String filepath,String encodingString,int Writernum)
	{
		try {  
			File file = new File(filepath);  
			File fileParent = file.getParentFile();  
			if(!fileParent.exists()){  
			    fileParent.mkdirs();  
			}  
			writer[Writernum]=new OutputStreamWriter(new FileOutputStream(filepath),encodingString);
			bufwriter[Writernum]=new BufferedWriter(writer[Writernum]);
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	public void startWrite(File file,String encodingString,int Writernum)
	{
		try {
			writer[Writernum]=new OutputStreamWriter(new FileOutputStream(file),encodingString);
			bufwriter[Writernum]=new BufferedWriter(writer[Writernum]);
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	public boolean IsAppendMode(int Writernum) {
		return isAppendMode[Writernum];
	}
	public void startWrite(String filepath,String encodingString,int Writernum,boolean isAppend)
	{
		if(isAppend) {
			isAppendMode[Writernum]=true;
			try {  
				File file = new File(filepath);  
				File fileParent = file.getParentFile();  
				if(!fileParent.exists()){  
				    fileParent.mkdirs();  
				}  
				writer[Writernum]=new OutputStreamWriter(new FileOutputStream(filepath,isAppend),encodingString);
				bufwriter[Writernum]=new BufferedWriter(writer[Writernum]);
			} catch (UnsupportedEncodingException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
		else {
			try {  
				File file = new File(filepath);  
				File fileParent = file.getParentFile();  
				if(!fileParent.exists()){  
				    fileParent.mkdirs();  
				}  
				writer[Writernum]=new OutputStreamWriter(new FileOutputStream(filepath),encodingString);
				bufwriter[Writernum]=new BufferedWriter(writer[Writernum]);
			} catch (UnsupportedEncodingException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
	}
	public void endWrite(int Writernum)
	{
		try {
			if(bufwriter!=null)
			{
				if(bufwriter[Writernum]!=null) {
					bufwriter[Writernum].close();
					bufwriter[Writernum]=null;
				}
			}
			if(writer!=null)
			{
				if(writer[Writernum]!=null) {
					writer[Writernum].close();
					writer[Writernum]=null;
				}
			}
			
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		isAppendMode[Writernum]=false;
	}
	/**
	 * 读取完文件后会返回null，不是""
	 */
	public String readOneSentence(int Readernum)
	{
		try {
			return bufread[Readernum].readLine();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			System.out.println("文件未正确打开，读取出错");
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 如果isAppendMode[writernum]=false是清除写，否则是追加写
	 * @param s
	 * @param Writernum
	 */
	public void writeOneString(String s,int Writernum)
	{
		
		try {
			bufwriter[Writernum].write(s);
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			System.out.println("写入string出错");
		}
	}
	public void writeStringBufferIntoTXT(StringBuffer buf,int Writernum)//将StringBuffer写入txt
	{
		
			try {
				bufwriter[Writernum].write(buf.toString());
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
	}
	public static boolean isFileExist(String path)
	{
		File file=new File(path);
		return file.exists();
	}
	public int readint(int readernum)
	{
		int rst=-1;
		try {
			rst=bufread[readernum].read();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return rst;
	}
	public void writechars(char []a,int writernum)
 	{
 		try {
			bufwriter[writernum].write(a);
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			System.out.println("Wrong in IOforHMM.writechars");
		}
 	}
	public static String GetCurrentDir() {
		return System.getProperty("user.dir");
	}
	public static String StringArrayList2String(ArrayList<String> str_array_list){
		String sparator=" ";
		StringBuffer tmpStrBuf=new StringBuffer("");
		for(String word:str_array_list) {
			tmpStrBuf.append(word);
			tmpStrBuf.append(sparator);
		}
		return tmpStrBuf.toString().trim();
	}
	public static String StringArrayList2String(ArrayList<String> str_array_list,String sparator){
		StringBuffer tmpStrBuf=new StringBuffer("");
		for(String word:str_array_list) {
			tmpStrBuf.append(word);
			tmpStrBuf.append(sparator);
		}
		return tmpStrBuf.toString().trim();
	}
}
