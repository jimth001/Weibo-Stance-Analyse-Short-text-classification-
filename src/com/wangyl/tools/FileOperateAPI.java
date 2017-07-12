package com.wangyl.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.wangyl.config.Config;
import com.wangyl.log.Log;

public class FileOperateAPI {
	/**
     * 复制文件到目录 <功能详细描述>
     * 
     * @param srcPath 文件绝对路径
     * @param destDirPath 目标文件夹绝对路径
     * @throws Exception 
     * @see [类、类#方法、类#成员]
     */
    public static void CopyFile(String srcPath, String destDirPath) throws Exception
    {
        File srcfile = new File(srcPath);
        File destDir = new File(destDirPath);
        InputStream is = null;
        OutputStream os = null;
        int ret = 0;
        if(!destDir.exists()){  
		    destDir.mkdirs();
		}
        // 源文件存在
        if (srcfile.exists() && destDir.exists() && destDir.isDirectory())
        {
            try
            {
                is = new FileInputStream(srcfile);
                String destFile = destDirPath + File.separator + srcfile.getName();
                os = new FileOutputStream(new File(destFile));
                byte[] buffer = new byte[1024];
                while ((ret = is.read(buffer)) != -1) {
                	os.write(buffer, 0, ret); // 此处不能用os.write(buffer),当读取最后的字节小于1024时，会多写;
                	// ret是读取的字节数
                }
                os.flush();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new Exception("");
            }
            catch (IOException e) {
                e.printStackTrace();
                throw new Exception("");
            }
            finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (is != null) {
                        is.close();
                    }
                }
                catch (Exception e) {
                }
            }
        }
        else {
            throw new Exception("源文件不存在或目标路径不存在！源文件："+srcPath+"，目标路径："+destDirPath);
        }
    }
    /**
     * 列出文件夹下的所有文件，使用递归。 <功能详细描述>
     * 
     * @param dirPath 文件夹绝对路径
     * @see [类、类#方法、类#成员]
     */
    public static void GetFileList(String dirPath)
    {
        File rootDir = new File(dirPath);
        if (rootDir.exists())
        {
            File[] files = rootDir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    System.out.println("目录" + file.getName());
                    // 递归调用
                    GetFileList(file.getPath());
                }
                else {
                    System.out.println("文件" + file.getName());
                }
            }
        }
    }
    /**
     * <一句话功能简述>复制文件夹 <功能详细描述>
     * 
     * @param srcDir 文件夹的绝对路径
     * @param destDir 目标绝对路径
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public static void CopyFolder(String srcDir, String destDir) throws Exception
    {
        File srcFile = new File(srcDir);
        // 在目标路径建立文件夹
        String name = srcFile.getName();
        File destFile = new File(destDir + File.separator + name);
        if (!destFile.exists()) {
            destFile.mkdir();
        }
        if (srcFile.exists() && destFile.isDirectory()) {
            File[] files = srcFile.listFiles();
            String src = srcDir;
            String dest = destFile.getAbsolutePath();
            for (File temp : files) {
                // 复制目录
                if (temp.isDirectory()) {
                    String tempSrc = src + File.separator + temp.getName();
                    String tempDest = dest + File.separator + temp.getName();
                    File tempFile = new File(tempDest);
                    tempFile.mkdir();
                    // 当子目录不为空时
                    if (temp.listFiles().length != 0) {
                        // 递归调用
                        CopyFolder(tempSrc, tempDest);
                    }
                }
                else {
                // 复制文件
                	String tempPath = src + File.separator + temp.getName();
                    CopyFile(tempPath, dest);
                }
            }
        }
    }
    /**
     * 删除文件夹或文件 <功能详细描述>
     * 要先删除子内容，再删除父内容
     * @param dirPath 要删除的文件夹
     * @see [类、类#方法、类#成员]
     */
    public static void DeleteFolder(String dirPath)
    {
        File folder = new File(dirPath);
        File[] files = folder.listFiles();
        if(files!=null) {
        	for (File file : files)
            {
                if (file.isDirectory()) {
                    String tempFilePath = dirPath + File.separator + file.getName();
                    DeleteFolder(tempFilePath);
                }
                else {
                    file.delete();
                }
            }
        }
        folder.delete();
    }
    /**
     * 新建目录
     * @param folderPath String 如 c:/fqf
     * @return boolean
     */
    public void newFolder(String folderPath) {
      try {
    	File myFilePath = new java.io.File(folderPath);
        if (!myFilePath.exists()) {
          myFilePath.mkdir();
        }
      }
      catch (Exception e) {
        Log.LogInf(Timer.GetNowTimeToMillisecends()+"新建目录操作出错");
        e.printStackTrace();
      }
    }
    /** 遍历目录
     * 
     * @param dir
     * @param fileList
     * @throws IOException
     */
 	public static void visitDirsAllFiles(File dir, List<File> fileList) throws IOException {
 		if (dir.isDirectory()) {
 			// 过滤点Linux 点文件
 			FilenameFilter filter = new FilenameFilter() {
 				public boolean accept(File dir, String name) {
 					return !name.startsWith(".");
 				}
 			};
 			String[] children = dir.list(filter);

 			for (int i = 0; i < children.length; i++) {
 				visitDirsAllFiles(new File(dir, children[i]), fileList);
 			}
 		} else {
 			fileList.add(dir);
 		}
 	}
 	public static int HowManyFileInDir(String dir) throws IOException {
 		List<File> filelist=new ArrayList<File>();
 		visitDirsAllFiles(dir, filelist);
 		return filelist.size();
 	}
 	public static void visitDirsAllFiles(String dir, List<File> fileList) throws IOException {
 		visitDirsAllFiles(new File(dir), fileList);
 	}
    public static void main(String[] args) {
    	try {
			Log.LogInf(HowManyFileInDir(Config.lsaCorpusDir)+"\n");
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
    }
}
