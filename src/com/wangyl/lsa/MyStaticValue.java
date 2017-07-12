package com.wangyl.lsa;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class MyStaticValue {
	public static final String SEPERATOR_C_BLANK = " ";
	public static final String SEPERATOR_E_BLANK = " ";
	
	public static void PrintVector(Vector<Double> m, int d) {
		for (int i = 0; i < d; i++) {
			System.out.printf("%g ", m.get(i));
		}
		System.out.printf("\n");
	}

	// 初始化向量
	public static <T> void initVector(Vector<T> vec, int num, T t) {
		vec.clear();
		for (int i = 0; i < num; i++) {
			vec.add(t);
		}
	}
}



/**
 * 这个类储存一些公用变�?.
 * @description 好像没有用到。wangyl。2017.4.12
 */
/*public class MyStaticValue {

	public static final Logger LIBRARYLOG = Logger.getLogger("DICLOG");

	// 是否�?启人名识�?
	public static boolean isNameRecognition = true;

	// 是否�?启数字识�?
	public static boolean isNumRecognition = true;

	// 是否数字和量词合�?
	public static boolean isQuantifierRecognition = true;

	public static boolean isRealName = false;

	/**
	 * 用户自定义词典的加载,如果是路径就扫描路径下的dic文件
	 * /
	public static String userLibrary = "library/userLibrary/default.dic";

	public static String ambiguityLibrary = "library/ambiguity.dic";

	/**
	 * 是否用户辞典不加载相同的�?
	 * /
	public static boolean isSkipUserDefine = false;

	// 分词放法
	public static String method = "TO";

	// core字典加载类型
	public static String loadType = "core";

	// sourceFile 分词源文件目�?
	public static String sourceFile = "sourcefile/";

	// ---数据库信�?
	// 数据库用户名
	public static String userName = "root";
	// 数据库密�?
	public static String passWord = "root";
	// 驱动信息
	public static final String driver = "com.mysql.jdbc.Driver";
	// 数据库地�?
	public static String url = "jdbc:mysql://10.71.14.232:3306/WORD_CLOUD";

	// begin add by ywc
	public static final String SEPERATOR_C_SENTENCE = "。！？：；�??";
	public static final String SEPERATOR_C_SUB_SENTENCE = "、，（）“�?��?��??";
	public static final String SEPERATOR_E_SENTENCE = "!?:;";
	public static final String SEPERATOR_E_SUB_SENTENCE = ",()\042'";
	public static final String SEPERATOR_LINK = "\n";// System.getProperty("line.separator");

	public static final String versionNumber = "1.0.0";
	public static final String mathNumber = "1234567890";
	public static final String posString = "名动副连助代介数量叹";
	public static final String SpecialNumber = "①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮⑯";
	public static final String MathNumber = "1234567890";
	public static final String shutDownFile = IOapi.GetCurrentDir()+"\\a.shutdown";
	public static final String SEPERATOR_C_BLANK = " ";
	public static final String SEPERATOR_E_BLANK = " ";

	public static Map<String, String> mapPos;
	static {

		// TODO Auto-generated constructor stub
		mapPos = new HashMap<String, String>();
		mapPos.put("�?", "n");
		mapPos.put("�?", "v");
		mapPos.put("�?", "ag");
		mapPos.put("�?", "ad");
		mapPos.put("�?", "c");
		mapPos.put("�?", "u");
		mapPos.put("�?", "r");
		mapPos.put("�?", "p");
		mapPos.put("�?", "m");
		mapPos.put("�?", "q");
		mapPos.put("�?", "e");
		mapPos.put("拟声", "o");
	}

	/*
	 * 是否是汉�?
	 * /
	public static boolean isChineseChar(String str) {
		boolean temp = false;
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(str);
		if (m.find()) {
			temp = true;
		}
		return temp;
	}

	// 是否是汉�?
	public static boolean isChinese(char a) {
		int v = (int) a;
		return (v >= 19968 && v <= 171941);
	}

	// 是否包含汉字
	public static boolean chontainsChinese(String s) {
		if (null == s || "".equals(s.trim()))
			return false;
		for (int i = 0; i < s.length(); i++) {
			if (isChinese(s.charAt(i)))
				return true;
		}
		return false;
	}

	// 词�?�映射函�? 汉字转字�?
	public static String ChinesePosToEnglishPos(String strPos) {
		String temp = mapPos.get(strPos);
		return temp;
	}

	// 段落分句
	public static List<String> segSentenceByPunctuation(String Line) {
		List<String> senList = new ArrayList<String>();
		String[] strJ = new String[] {};
		// 先根据句号分
		strJ = Line.split("�?");
		for (int i = 0; i < strJ.length; i++) {
			String tmp = strJ[i];
			if (!tmp.endsWith("�?") && !tmp.endsWith("�?")) {
				tmp += "�?";
			}
			String[] strW = tmp.split("�?");
			for (int j = 0; j < strW.length; j++) {
				String tt = strW[j];
				if (!tt.endsWith("�?") && !tt.endsWith("�?")) {
					tt += "�?";
				}
				String[] strT = tt.split("�?");
				for (int k = 0; k < strT.length; k++) {
					if (!strT[k].endsWith("�?") && !strT[k].endsWith("�?")) {
						strT[k] += "�?";
					}
					senList.add(strT[k]);
				}
				strT = null;
			}
			strW = null;
		}
		return senList;
	}

	// 遍历目录
	public static void visitDirsAllFiles(File dir, List<File> fileList)
			throws IOException {

		if (dir.isDirectory()) {
			// 过滤点linux 点文�?
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

	// 判断是字�?
	public static boolean Letter(char ch) {
		if ((ch > 'a' && ch < 'z') || (ch > 'A' && ch < 'Z')) {
			return true;
		} else {
			return false;
		}
	}

	// 打印矩阵
	public static void PrintMatrix(Matrix mtx) {
		for (int i = 0; i < mtx.getRowDimension(); i++) {
			for (int j = 0; j < mtx.getColumnDimension(); j++) {
				System.out.printf("m(%d,%d) = %g\t", i, j, mtx.get(i, j));
			}
			System.out.printf("\n");
		}
	}

	public static void PrintVector(Vector<Double> m, int d) {
		for (int i = 0; i < d; i++) {
			System.out.printf("%g ", m.get(i));
		}
		System.out.printf("\n");
	}

	// 初始�? 向量
	public static <T> void initVector(Vector<T> vec, int num, T t) {
		for (int i = 0; i < num; i++) {
			vec.add(t);
		}
	}
	// end add
}*/

