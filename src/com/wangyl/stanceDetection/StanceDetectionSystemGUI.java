package com.wangyl.stanceDetection;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.encog.ensemble.Ensemble;

import com.wangyl.config.Config;
import com.wangyl.ensembleLearning.EnsembleFramework;
import com.wangyl.ensembleLearning.EnsembleVote;
import com.wangyl.lda.LDA_api;
import com.wangyl.lda.TestLDA;
import com.wangyl.log.Log;
import com.wangyl.lsa.LsaGroupVoteModel;
import com.wangyl.lsa.TestLSA;
import com.wangyl.preprocesser.RawDataPreprocesser;
import com.wangyl.preprocesser.StructuredPreproedData;
import com.wangyl.svmAPI.svm_result;
import com.wangyl.tools.*;
import com.wangyunli.sentimentLexicon.SentimentAnalyse;
//import com.e2one.example;
public class StanceDetectionSystemGUI {

	/**
	 * @param args
	 */
	static Thread t=null;
	static RawDataPreprocesser rawDataPreprocesser=new RawDataPreprocesser("", Config.preprocessedTrainDataOutputSrc, Config.encodingType);
	static String trainsrc="";
	static String nltrainsrc="";
	static String testsrc="";
	static int lsagvm_n=8;
	static int lsagvm_k=20;
	static double lsasvm_c=0.1;
	static int ldatopicnum=60;
	static double ldasvm_c=1;
	static double slsvm_l=0.1;
	static double slsvm_c=1;
	static double fp=0.2;
	static int fk=20;
	public static boolean CheckArgs(int type,String argsline) {
		String []strs;
		switch (type) {
		case 1://lsagvm
			strs=argsline.split(",");
			if(strs.length!=2) {
				return false;
			}
			else {
				try {
					lsagvm_n=Integer.parseInt(strs[0]);
					lsagvm_k=Integer.parseInt(strs[1]);
				} catch (Exception e) {
					// TODO: handle exception
					return false;
				}
				return true;
			}
		case 2://lsasvm
			strs=argsline.split(",");
			if(strs.length!=1) {
				return false;
			}
			else {
				try {
					lsasvm_c=Double.parseDouble(strs[0]);
				} catch (Exception e) {
					// TODO: handle exception
					return false;
				}
				return true;
			}
			
		case 3://ldasvm
			strs=argsline.split(",");
			if(strs.length!=2) {
				return false;
			}
			else {
				try {
					ldatopicnum=Integer.parseInt(strs[0]);
					ldasvm_c=Double.parseDouble(strs[1]);
				} catch (Exception e) {
					// TODO: handle exception
					return false;
				}
				
				return true;
			}
		case 4://slsvm
			strs=argsline.split(",");
			if(strs.length!=2) {
				return false;
			}
			else {
				try {
					slsvm_l=Double.parseDouble(strs[0]);
					slsvm_c=Double.parseDouble(strs[1]);
				} catch (Exception e) {
					// TODO: handle exception
					return false;
				}
				
				return true;
			}
		case 5://f e lsagvm
			strs=argsline.split(",");
			if(strs.length!=2) {
				return false;
			}
			else {
				try {
					fp=Double.parseDouble(strs[0]);
					fk=Integer.parseInt(strs[1]);
				} catch (Exception e) {
					// TODO: handle exception
					return false;
				}
				return true;
			}
		default:
			return false;
		}
	}
	public static void main(String[] args) {
		// TODO 自动生成的方法存根
		Display display = new Display();
	    final Shell shell = new Shell(display);
	    shell.setText("微博立场分析工具");
	    GridLayout mainUILayout=new GridLayout(10,false);//2列，不等长
	    //mainUILayout.numColumns = 3;
	    mainUILayout.makeColumnsEqualWidth = false;
	    mainUILayout.marginWidth = 15;
	    mainUILayout.marginHeight = 15;
	    mainUILayout.verticalSpacing = 1;
	    mainUILayout.horizontalSpacing = 1;
	    //标注的训练文件路径label：
	    final Label trainDataSrcLabel=new Label(shell, SWT.BORDER|SWT.SHADOW_NONE);
	    trainDataSrcLabel.setText("已标注训练数据的路径：");
	    trainDataSrcLabel.setAlignment(SWT.CENTER);
	    GridData trainDataSrcLabelData=new GridData(SWT.FILL,SWT.LEFT,false,false);
	    trainDataSrcLabelData.verticalSpan=3;
	    trainDataSrcLabelData.horizontalSpan=3;
	    trainDataSrcLabelData.widthHint=200;
	    trainDataSrcLabelData.heightHint=36;
	    trainDataSrcLabel.setLayoutData(trainDataSrcLabelData);
	    //设置训练文件路径文本框：
	    final Text trainDataSrcText = new Text(shell, SWT.BORDER|SWT.H_SCROLL);
	    trainDataSrcText.setText(Config.rawDataDir+Config.labeledTrainDataName);
	    GridData trainDataSrcTextData=new GridData(SWT.FILL,SWT.LEFT,false,false);
	    trainDataSrcTextData.verticalSpan=3;
	    trainDataSrcTextData.horizontalSpan=6;
	    trainDataSrcTextData.widthHint=500;
	    trainDataSrcText.setLayoutData(trainDataSrcTextData);
	    //设置标注的训练文件路径选择button：
	    final Button chooseltdSrcBtn=new Button(shell, SWT.PUSH|SWT.CENTER);
	    chooseltdSrcBtn.setText("选择路径");
	    chooseltdSrcBtn.setToolTipText("点此选择标注的训练文件，也可直接在左边的文本框内输入路径");
	    chooseltdSrcBtn.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				// TODO 自动生成的方法存根
				JFileChooser chooser=new JFileChooser() ;
				chooser.setCurrentDirectory(new File(Config.rawDataDir)) ;
				if(chooser.showOpenDialog(new JLabel())==JFileChooser.APPROVE_OPTION)
				{
					trainDataSrcText.setText(chooser.getSelectedFile().getPath());
				}
			}
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO 自动生成的方法存根
			}
		});
	    GridData chooseltdSrcBtnData=new GridData(SWT.FILL,SWT.LEFT,false,false);
	    chooseltdSrcBtnData.verticalSpan=3;
	    chooseltdSrcBtnData.horizontalSpan=1;
	    chooseltdSrcBtnData.widthHint=60;
	    chooseltdSrcBtn.setLayoutData(chooseltdSrcBtnData);
	    //未标注的训练文件的路径label：
	    final Label nltrainDataSrcLabel=new Label(shell, SWT.BORDER|SWT.SHADOW_NONE);
	    nltrainDataSrcLabel.setText("未标注训练数据的路径：");
	    nltrainDataSrcLabel.setAlignment(SWT.CENTER);
	    GridData nltrainDataSrcLabelData=new GridData(SWT.FILL,SWT.LEFT,false,false);
	    nltrainDataSrcLabelData.verticalSpan=3;
	    nltrainDataSrcLabelData.horizontalSpan=3;
	    nltrainDataSrcLabelData.widthHint=200;
	    nltrainDataSrcLabelData.heightHint=36;
	    nltrainDataSrcLabel.setLayoutData(nltrainDataSrcLabelData);
	    //未标注的训练文件的路径文本框：
	    final Text nltrainDataSrcText=new Text(shell, SWT.BORDER|SWT.H_SCROLL);
	    nltrainDataSrcText.setText(Config.rawDataDir+Config.notLabeledTrainDataName);
	    GridData nltrainDataSrcTextData=new GridData(SWT.FILL,SWT.LEFT,false,false);
	    nltrainDataSrcTextData.verticalSpan=3;
	    nltrainDataSrcTextData.horizontalSpan=6;
	    nltrainDataSrcTextData.widthHint=500;
	    nltrainDataSrcText.setLayoutData(nltrainDataSrcTextData);
	    //设置未标注的训练文件路径选择button：
	    final Button choosenltdSrcBtn=new Button(shell, SWT.PUSH|SWT.CENTER);
	    choosenltdSrcBtn.setText("选择路径");
	    choosenltdSrcBtn.setToolTipText("点此选择未标注的训练文件（非必须训练文件），也可直接在左边的文本框内输入路径");
	    choosenltdSrcBtn.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				// TODO 自动生成的方法存根
				JFileChooser chooser=new JFileChooser() ;
				chooser.setCurrentDirectory(new File(Config.rawDataDir)) ;
				if(chooser.showOpenDialog(new JLabel())==JFileChooser.APPROVE_OPTION)
				{
					nltrainDataSrcText.setText(chooser.getSelectedFile().getPath());
				}
			}
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO 自动生成的方法存根
			}
		});
	    GridData choosenltdSrcBtnData=new GridData(SWT.FILL,SWT.LEFT,false,false);
	    choosenltdSrcBtnData.verticalSpan=3;
	    choosenltdSrcBtnData.horizontalSpan=1;
	    choosenltdSrcBtnData.widthHint=60;
	    choosenltdSrcBtn.setLayoutData(choosenltdSrcBtnData);
	    //测试文件的路径的label：
	    final Label testDataSrcLabel=new Label(shell, SWT.BORDER|SWT.SHADOW_NONE);
	    testDataSrcLabel.setText("测试数据文件的路径：");
	    testDataSrcLabel.setAlignment(SWT.CENTER);
	    GridData testDataSrcLabelData=new GridData(SWT.FILL,SWT.LEFT,false,false);
	    testDataSrcLabelData.verticalSpan=3;
	    testDataSrcLabelData.horizontalSpan=3;
	    testDataSrcLabelData.widthHint=200;
	    testDataSrcLabelData.heightHint=36;
	    testDataSrcLabel.setLayoutData(testDataSrcLabelData);
	    //测试文件的路径文本框：
	    final Text testDataSrcText=new Text(shell, SWT.BORDER|SWT.H_SCROLL);
	    testDataSrcText.setText(Config.rawDataDir+Config.testDataName);
	    GridData testDataSrcTextData=new GridData(SWT.FILL,SWT.LEFT,false,false);
	    testDataSrcTextData.verticalSpan=3;
	    testDataSrcTextData.horizontalSpan=6;
	    testDataSrcTextData.widthHint=500;
	    testDataSrcText.setLayoutData(testDataSrcTextData);
	    //设置测试文件路径选择button：
	    final Button choosettdSrcBtn=new Button(shell, SWT.PUSH|SWT.CENTER);
	    choosettdSrcBtn.setText("选择路径");
	    choosettdSrcBtn.setToolTipText("点此选择测试文件，也可直接在左边的文本框内输入路径");
	    choosettdSrcBtn.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				// TODO 自动生成的方法存根
				JFileChooser chooser=new JFileChooser() ;
				chooser.setCurrentDirectory(new File(Config.rawDataDir)) ;
				if(chooser.showOpenDialog(new JLabel())==JFileChooser.APPROVE_OPTION)
				{
					testDataSrcText.setText(chooser.getSelectedFile().getPath());
				}
			}
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO 自动生成的方法存根
			}
		});  
	    GridData choosettdSrcBtnData=new GridData(SWT.FILL,SWT.LEFT,false,false);
	    choosettdSrcBtnData.verticalSpan=3;
	    choosettdSrcBtnData.horizontalSpan=1;
	    choosettdSrcBtnData.widthHint=60;
	    choosettdSrcBtn.setLayoutData(choosettdSrcBtnData);
	    
	    
	    
	    //分类方法label：
	    final Label classifyMethodLabel=new Label(shell, SWT.BORDER|SWT.SHADOW_NONE);
	    classifyMethodLabel.setText("分类方法：");
	    classifyMethodLabel.setAlignment(SWT.CENTER);
	    GridData classifyMethodLabelData=new GridData(SWT.FILL,SWT.LEFT,false,false);
	    classifyMethodLabelData.verticalSpan=2;
	    classifyMethodLabelData.horizontalSpan=3;
	    classifyMethodLabelData.widthHint=200;
	    classifyMethodLabelData.heightHint=24;
	    classifyMethodLabel.setLayoutData(classifyMethodLabelData);
	    //设置选择分类方法用的单选框：
	    final Button checkButton= new Button(shell, SWT.RADIO);
	    checkButton.setText("LSA-GVM");
	    GridData checkButtonData=new GridData();
	    checkButtonData.verticalSpan=2;
	    checkButtonData.horizontalSpan=1;
	    checkButtonData.heightHint=24;
	    checkButtonData.widthHint=120;
	    checkButton.setToolTipText("参数:正整数n(建议大于5小于30)，正整数k(建议大于5)。参数填写示例：10,20");
	    checkButton.setLayoutData(checkButtonData);
	    //设置选择分类方法用的单选框：
	    final Button checkButton2= new Button(shell, SWT.RADIO);
	    checkButton2.setText("LSA-SVM");
	    GridData checkButtonData2=new GridData();
	    checkButtonData2.verticalSpan=2;
	    checkButtonData2.horizontalSpan=1;
	    checkButtonData2.heightHint=24;
	    checkButtonData2.widthHint=120;
	    checkButton2.setToolTipText("参数:正实数c。参数填写示例：0.1");
	    checkButton2.setLayoutData(checkButtonData2);
	    //设置选择分类方法用的单选框：
	    final Button checkButton3= new Button(shell, SWT.RADIO);
	    checkButton3.setText("LDA-SVM");
	    GridData checkButtonData3=new GridData();
	    checkButtonData3.verticalSpan=2;
	    checkButtonData3.horizontalSpan=1;
	    checkButtonData3.heightHint=24;
	    checkButtonData3.widthHint=120;
	    checkButton3.setToolTipText("参数：正整数n(建议大于20小于300)，正实数c。参数填写示例:60,1");
	    checkButton3.setLayoutData(checkButtonData3);
	  //设置选择分类方法用的单选框：
	    final Button checkButton4= new Button(shell, SWT.RADIO);
	    checkButton4.setText("SL-SVM");
	    GridData checkButtonData4=new GridData();
	    checkButtonData4.verticalSpan=2;
	    checkButtonData4.horizontalSpan=1;
	    checkButtonData4.heightHint=24;
	    checkButtonData4.widthHint=120;
	    checkButton4.setToolTipText("参数：正实数l，正实数c。参数填写示例：0.1,1");
	    checkButton4.setLayoutData(checkButtonData4);
	  //设置选择分类方法用的单选框：
	    final Button checkButton5= new Button(shell, SWT.RADIO);
	    checkButton5.setText("Fast Ensemble LSA-GVM");
	    GridData checkButtonData5=new GridData();
	    checkButtonData5.verticalSpan=2;
	    checkButtonData5.horizontalSpan=2;
	    checkButtonData5.heightHint=24;
	    checkButtonData5.widthHint=180;
	    checkButton5.setToolTipText("参数：实数p=[0,1),正整数k。参数填写示例：0.2,20");
	    checkButton5.setLayoutData(checkButtonData5);
	    
	    //参数框label：
	    final Label argsLabel=new Label(shell, SWT.BORDER|SWT.SHADOW_NONE);
	    argsLabel.setText("算法参数：");
	    argsLabel.setAlignment(SWT.CENTER);
	    GridData argsLabelData=new GridData(SWT.FILL,SWT.LEFT,false,false);
	    argsLabelData.verticalSpan=2;
	    argsLabelData.horizontalSpan=2;
	    argsLabelData.widthHint=200;
	    argsLabelData.heightHint=36;
	    argsLabel.setToolTipText("在右边的文本框中输入算法中的参数，鼠标悬停在算法选择控件上，可查看参数填写说明");
	    argsLabel.setLayoutData(argsLabelData);
	    //参数框：
	    final Text argsText = new Text(shell, SWT.BORDER|SWT.H_SCROLL);
	    argsText.setText("在此输入算法中的参数，鼠标悬停在算法选择控件上，可查看参数填写说明");
	    GridData argsTextData=new GridData(SWT.FILL,SWT.LEFT,false,false);
	    argsTextData.verticalSpan=2;
	    argsTextData.horizontalSpan=8;
	    argsTextData.widthHint=500;
	    argsLabelData.heightHint=36;
	    argsText.setLayoutData(argsTextData);
	    
	    //设置输出用的文本
	    final Text outputConsoleText = new Text(shell, SWT.WRAP|SWT.BORDER|SWT.V_SCROLL);// 多行文本框，可自动换行 | 垂直滚动条  
	    //outputConsoleText.setBounds(0, 0, 600, 300);// （x, y, width, height）
	    outputConsoleText.setText("console");
	    outputConsoleText.setEditable(false);
	    GridData outputConsoleTextData=new GridData(SWT.FILL,SWT.CENTER,true,true);
	    outputConsoleTextData.verticalSpan=1;//组件占的垂直行数
	    outputConsoleTextData.horizontalSpan=10;//组件占的水平列数
	    outputConsoleTextData.heightHint=220;//组件高度
	    outputConsoleTextData.widthHint=500;//组件宽度
	    outputConsoleText.setLayoutData(outputConsoleTextData);
	    //final org.eclipse.swt.widgets.Label trainDataSrcLabel=new org.eclipse.swt.widgets.Label(shell, style);
	    //执行button：
	    Button runButton= new Button(shell, SWT.PUSH|SWT.CENTER);
	    runButton.setText("START");
	    GridData runButtonData=new GridData();
	    runButtonData.verticalSpan=1;
	    runButtonData.horizontalSpan=5;
	    runButtonData.heightHint=28;
	    runButtonData.widthHint=360;
	    runButtonData.horizontalAlignment=SWT.FILL;
	    runButton.setLayoutData(runButtonData);
	    runButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				if(t!=null&&t.isAlive()) {
					JOptionPane.showMessageDialog(null, "请等待当前的任务执行完毕", "【出错啦】", JOptionPane.ERROR_MESSAGE);
					return;
				}
				// TODO 自动生成的方法存根
				outputConsoleText.setText("");
				if(checkButton.getSelection()) {//LSA-GVM
					if(!CheckArgs(1, argsText.getText())) {
						JOptionPane.showMessageDialog(null, "请按正确格式填写参数", "【出错啦】", JOptionPane.ERROR_MESSAGE);
						argsText.setText("在此输入算法中的参数，鼠标悬停在算法选择控件上，可查看参数填写说明");
						return;
					}
					System.out.println("LSA-GVM:");
					testsrc=testDataSrcText.getText();
					trainsrc=trainDataSrcText.getText();
					t = new Thread(new Runnable(){  
			            public void run(){
			            	try {
			            		System.out.println(Timer.GetNowTimeToMillisecends()+" 训练集预处理中...");
								rawDataPreprocesser.setOutputSrc(Config.preprocessedTrainDataOutputSrc);
								rawDataPreprocesser.setRawDataSrc(trainsrc);
								FileOperateAPI.DeleteFolder(Config.preprocessedTrainDataOutputSrc);
								rawDataPreprocesser.PreprocessTrainData();
								System.out.println(Timer.GetNowTimeToMillisecends()+" 测试集预处理中...");
								rawDataPreprocesser.setOutputSrc(Config.preprocessedTestDataOutputSrc);
								rawDataPreprocesser.setRawDataSrc(testsrc);
								FileOperateAPI.DeleteFolder(Config.preprocessedTestDataOutputSrc);
								ArrayList<StructuredPreproedData> testDatas=rawDataPreprocesser.PreprocessTestData();
								System.out.println(Timer.GetNowTimeToMillisecends()+" Anaylse...");
								System.out.println(LsaGroupVoteModel.Classify_fixedVoteNum(lsagvm_n,lsagvm_k));
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
			            }});  
			        t.start();
				}
				else if(checkButton2.getSelection()) {//LSA-SVM
					if(!CheckArgs(2, argsText.getText())) {
						JOptionPane.showMessageDialog(null, "请按正确格式填写参数", "【出错啦】", JOptionPane.ERROR_MESSAGE);
						argsText.setText("在此输入算法中的参数，鼠标悬停在算法选择控件上，可查看参数填写说明");
						return;
					}
					System.out.println("LSA-SVM:");
					testsrc=testDataSrcText.getText();
					trainsrc=trainDataSrcText.getText();
					t = new Thread(new Runnable(){  
			            public void run(){
			            	try {
			            		System.out.println(Timer.GetNowTimeToMillisecends()+" 训练集预处理中...");
								rawDataPreprocesser.setOutputSrc(Config.preprocessedTrainDataOutputSrc);
								rawDataPreprocesser.setRawDataSrc(trainsrc);
								FileOperateAPI.DeleteFolder(Config.preprocessedTrainDataOutputSrc);
								rawDataPreprocesser.PreprocessTrainData();
								System.out.println(Timer.GetNowTimeToMillisecends()+" 测试集预处理中...");
								rawDataPreprocesser.setOutputSrc(Config.preprocessedTestDataOutputSrc);
								rawDataPreprocesser.setRawDataSrc(testsrc);
								FileOperateAPI.DeleteFolder(Config.preprocessedTestDataOutputSrc);
								ArrayList<StructuredPreproedData> testDatas=rawDataPreprocesser.PreprocessTestData();
								System.out.println(Timer.GetNowTimeToMillisecends()+" Anaylse...");
								TestLSA.extractLsaFeturesForSVM(1);
								TestLSA.svm_classify(lsasvm_c);
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
			            }});  
			        t.start();
				}
				else if(checkButton3.getSelection()) {//LDA-SVM
					if(!CheckArgs(3, argsText.getText())) {
						JOptionPane.showMessageDialog(null, "请按正确格式填写参数", "【出错啦】", JOptionPane.ERROR_MESSAGE);
						argsText.setText("在此输入算法中的参数，鼠标悬停在算法选择控件上，可查看参数填写说明");
						return;
					}
					System.out.println("LDA-SVM:");
					testsrc=testDataSrcText.getText();
					trainsrc=trainDataSrcText.getText();
					t = new Thread(new Runnable(){  
			            public void run(){
			            	try {
			            		System.out.println(Timer.GetNowTimeToMillisecends()+" 训练集预处理中...");
								rawDataPreprocesser.setOutputSrc(Config.preprocessedTrainDataOutputSrc);
								rawDataPreprocesser.setRawDataSrc(trainsrc);
								FileOperateAPI.DeleteFolder(Config.preprocessedTrainDataOutputSrc);
								rawDataPreprocesser.PreprocessTrainData();
								System.out.println(Timer.GetNowTimeToMillisecends()+" 测试集预处理中...");
								rawDataPreprocesser.setOutputSrc(Config.preprocessedTestDataOutputSrc);
								rawDataPreprocesser.setRawDataSrc(testsrc);
								FileOperateAPI.DeleteFolder(Config.preprocessedTestDataOutputSrc);
								ArrayList<StructuredPreproedData> testDatas=rawDataPreprocesser.PreprocessTestData();
								System.out.println(Timer.GetNowTimeToMillisecends()+" Anaylse...");
								TestLDA.runLdaSvm(ldatopicnum, ldasvm_c);
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
							
							
			            }});  
			        t.start();
				}
				else if(checkButton4.getSelection()) {//SL-SVM
					if(!CheckArgs(4, argsText.getText())) {
						JOptionPane.showMessageDialog(null, "请按正确格式填写参数", "【出错啦】", JOptionPane.ERROR_MESSAGE);
						argsText.setText("在此输入算法中的参数，鼠标悬停在算法选择控件上，可查看参数填写说明");
						return;
					}
					System.out.println("SL-SVM:");
					testsrc=testDataSrcText.getText();
					trainsrc=trainDataSrcText.getText();
					t = new Thread(new Runnable(){  
			            public void run(){
			            	//Log.EndLog();
			            	//Config.isDebugMode=false;
			            	try {
			            		System.out.println(Timer.GetNowTimeToMillisecends()+" 训练集预处理中...");
								rawDataPreprocesser.setOutputSrc(Config.preprocessedTrainDataOutputSrc);
								rawDataPreprocesser.setRawDataSrc(trainsrc);
								FileOperateAPI.DeleteFolder(Config.preprocessedTrainDataOutputSrc);
								rawDataPreprocesser.PreprocessTrainData();
								System.out.println(Timer.GetNowTimeToMillisecends()+" 测试集预处理中...");
								rawDataPreprocesser.setOutputSrc(Config.preprocessedTestDataOutputSrc);
								rawDataPreprocesser.setRawDataSrc(testsrc);
								FileOperateAPI.DeleteFolder(Config.preprocessedTestDataOutputSrc);
								ArrayList<StructuredPreproedData> testDatas=rawDataPreprocesser.PreprocessTestData();
								System.out.println(Timer.GetNowTimeToMillisecends()+" Anaylse...");
								SentimentAnalyse.Run(false, slsvm_l, slsvm_c);
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
							
			            }});  
			        t.start(); 
				}
				else if(checkButton5.getSelection()) {//Fast Ensemble LSA-GVM
					if(!CheckArgs(5, argsText.getText())) {
						JOptionPane.showMessageDialog(null, "请按正确格式填写参数", "【出错啦】", JOptionPane.ERROR_MESSAGE);
						argsText.setText("在此输入算法中的参数，鼠标悬停在算法选择控件上，可查看参数填写说明");
						return;
					}
					testsrc=testDataSrcText.getText();
					trainsrc=trainDataSrcText.getText();
					t = new Thread(new Runnable(){  
			            public void run(){
			            	//Log.EndLog();
			            	//Config.isDebugMode=false;
			            	try {
			            		System.out.println("Fast Ensemble LSA-GVM:");
								System.out.println(Timer.GetNowTimeToMillisecends()+" 训练集预处理中...");
								rawDataPreprocesser.setOutputSrc(Config.preprocessedTrainDataOutputSrc);
								rawDataPreprocesser.setRawDataSrc(trainsrc);
								FileOperateAPI.DeleteFolder(Config.preprocessedTrainDataOutputSrc);
								ArrayList<StructuredPreproedData> trainDatas=rawDataPreprocesser.PreprocessTrainData();
								System.out.println(Timer.GetNowTimeToMillisecends()+" 测试集预处理中...");
								rawDataPreprocesser.setOutputSrc(Config.preprocessedTestDataOutputSrc);
								rawDataPreprocesser.setRawDataSrc(testsrc);
								FileOperateAPI.DeleteFolder(Config.preprocessedTestDataOutputSrc);
								ArrayList<StructuredPreproedData> testDatas=rawDataPreprocesser.PreprocessTestData();
								System.out.println(Timer.GetNowTimeToMillisecends()+" LSA-GVM...");
								FileOperateAPI.DeleteFolder(Config.lsaGroupVoteModelOutputSrc);
								double avgdatanum=0;
								for(int i=0;i<trainDatas.size();i++) {
									avgdatanum+=trainDatas.get(i).favor.size();
									avgdatanum+=trainDatas.get(i).infavor.size();
									avgdatanum+=trainDatas.get(i).none.size();
								}
								avgdatanum=avgdatanum/trainDatas.size();
								TestLSA.runlsagvm(fp,fk,1.5*Math.sqrt(avgdatanum/1.5));
								System.out.println(Timer.GetNowTimeToMillisecends()+" Ensemble...");
								EnsembleVote.run(testDatas);
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
			            }});  
			        t.start(); 
					
				}
				else {
					JOptionPane.showMessageDialog(null, "未选择要使用的算法！", "【出错啦】", JOptionPane.ERROR_MESSAGE);
				}
				/*try {
					TongJi_0520_tmp.main(null);
				} catch (Exception e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}*/
			}
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO 自动生成的方法存根
			}
		});
	    //outputConsoleTextData.grabExcessVerticalSpace=true;
	    //outputConsoleTextData.grabExcessHorizontalSpace=true;
	    //
	    Button readButton= new Button(shell, SWT.PUSH|SWT.CENTER);
	    readButton.setText("显示预测结果");
	    GridData readButtonData=new GridData();
	    readButtonData.verticalSpan=1;
	    readButtonData.horizontalSpan=5;
	    readButtonData.heightHint=28;
	    readButtonData.widthHint=360;
	    readButtonData.horizontalAlignment=SWT.FILL;
	    readButton.setLayoutData(readButtonData);
	    readButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				if(t!=null&&t.isAlive()) {
					JOptionPane.showMessageDialog(null, "请等待当前的任务执行完毕", "【出错啦】", JOptionPane.ERROR_MESSAGE);
					return;
				}
				// TODO 自动生成的方法存根
				outputConsoleText.setText("");
				if(checkButton.getSelection()) {//LSA-GVM
					if(!CheckArgs(1, argsText.getText())) {
						JOptionPane.showMessageDialog(null, "本方法需要正确地填写分析时选用的参数才能查看对应的结果，因为存放结果的文件夹下可能保存着不同参数下得到的结果", "【出错啦】", JOptionPane.ERROR_MESSAGE);
						argsText.setText("在此输入算法中的参数，鼠标悬停在算法选择控件上，可查看参数填写说明");
						return;
					}
					System.out.println("LSA-GVM:");
					testsrc=testDataSrcText.getText();
					trainsrc=trainDataSrcText.getText();
					t = new Thread(new Runnable(){  
			            public void run(){
			            	try {
			            		ArrayList<StructuredPreproedData> testOrgData=RawDataPreprocesser.LoadOrgData(testsrc,null);
			            		for(int i=0;i<testOrgData.size();i++) {
			            			StructuredPreproedData tmpdata=testOrgData.get(i);
			            			try {
			            				int [][]rst=ResultAnalyser.LoadLSAGVMResultINTARRAY(Config.lsaGroupVoteModelOutputSrc+testOrgData.get(i).className+"_"+lsagvm_n+"_"+lsagvm_k+".txt", testOrgData.get(i).className);
				            			System.out.println("Target:"+testOrgData.get(i).className);
				            			int k=0;
				            			for(int j=0;j<tmpdata.org_favor.size();j++,k++) {
				            				if(rst[0][k]==Config.favor_int) {
				            					System.out.print(k+".预测："+Config.favor+" ");
				            				}
				            				else if(rst[0][k]==Config.infavor_int) {
				            					System.out.print(k+".预测："+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print(k+".预测："+Config.none+" ");
				            				}
				            				if(rst[1][k]==Config.favor_int) {
				            					System.out.print("正确："+Config.favor+" ");
				            				}
				            				else if(rst[1][k]==Config.infavor_int) {
				            					System.out.print("正确："+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print("正确："+Config.none+" ");
				            				}
				            				System.out.println(tmpdata.org_favor.get(j));
				            			}
				            			for(int j=0;j<tmpdata.org_infavor.size();j++,k++) {
				            				if(rst[0][k]==Config.favor_int) {
				            					System.out.print(k+".预测："+Config.favor+" ");
				            				}
				            				else if(rst[0][k]==Config.infavor_int) {
				            					System.out.print(k+".预测："+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print(k+".预测："+Config.none+" ");
				            				}
				            				if(rst[1][k]==Config.favor_int) {
				            					System.out.print("正确："+Config.favor+" ");
				            				}
				            				else if(rst[1][k]==Config.infavor_int) {
				            					System.out.print("正确："+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print("正确："+Config.none+" ");
				            				}
				            				System.out.println(tmpdata.org_infavor.get(j));
				            			}
				            			for(int j=0;j<tmpdata.org_none.size();j++,k++) {
				            				if(rst[0][k]==Config.favor_int) {
				            					System.out.print(k+".预测："+Config.favor+" ");
				            				}
				            				else if(rst[0][k]==Config.infavor_int) {
				            					System.out.print(k+".预测："+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print(k+".预测："+Config.none+" ");
				            				}
				            				if(rst[1][k]==Config.favor_int) {
				            					System.out.print("正确："+Config.favor+" ");
				            				}
				            				else if(rst[1][k]==Config.infavor_int) {
				            					System.out.print("正确："+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print("正确："+Config.none+" ");
				            				}
				            				System.out.println(tmpdata.org_none.get(j));
				            			}
				            			
									} catch (Exception e) {
										// TODO: handle exception
										e.printStackTrace();
										System.out.println("请检查结果文件是否存在："+Config.lsaGroupVoteModelOutputSrc+testOrgData.get(i).className+"_"+lsagvm_n+"_"+lsagvm_k+".txt");
									}
			            			System.out.println("\n");
			            		}
			            	} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
			            }});  
			        t.start();
				}
				else if(checkButton2.getSelection()) {//LSA-SVM
					
					System.out.println("LSA-SVM:");
					testsrc=testDataSrcText.getText();
					trainsrc=trainDataSrcText.getText();
					t = new Thread(new Runnable(){  
			            public void run(){
			            	try {
			            		ArrayList<StructuredPreproedData> testOrgData=RawDataPreprocesser.LoadOrgData(testsrc,null);
			            		for(int i=0;i<testOrgData.size();i++) {
			            			StructuredPreproedData tmpdata=testOrgData.get(i);
			            			try {
			            				int [][]rst=svm_result.GetSVMResultFromFile(Config.lsaSvmResult+testOrgData.get(i).className+"_result.txt", testOrgData.get(i).className);
				            			System.out.println("Target:"+testOrgData.get(i).className);
				            			int k=0;
				            			for(int j=0;j<tmpdata.org_favor.size();j++,k++) {
				            				if(rst[0][k]==Config.favor_int) {
				            					System.out.print(k+".预测："+Config.favor+" ");
				            				}
				            				else if(rst[0][k]==Config.infavor_int) {
				            					System.out.print(k+".预测："+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print(k+".预测："+Config.none+" ");
				            				}
				            				if(rst[1][k]==Config.favor_int) {
				            					System.out.print("正确："+Config.favor+" ");
				            				}
				            				else if(rst[1][k]==Config.infavor_int) {
				            					System.out.print("正确："+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print("正确："+Config.none+" ");
				            				}
				            				System.out.println(tmpdata.org_favor.get(j));
				            			}
				            			for(int j=0;j<tmpdata.org_infavor.size();j++,k++) {
				            				if(rst[0][k]==Config.favor_int) {
				            					System.out.print(k+".预测："+Config.favor+" ");
				            				}
				            				else if(rst[0][k]==Config.infavor_int) {
				            					System.out.print(k+".预测："+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print(k+".预测："+Config.none+" ");
				            				}
				            				if(rst[1][k]==Config.favor_int) {
				            					System.out.print("正确："+Config.favor+" ");
				            				}
				            				else if(rst[1][k]==Config.infavor_int) {
				            					System.out.print("正确："+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print("正确："+Config.none+" ");
				            				}
				            				System.out.println(tmpdata.org_infavor.get(j));
				            			}
				            			for(int j=0;j<tmpdata.org_none.size();j++,k++) {
				            				if(rst[0][k]==Config.favor_int) {
				            					System.out.print(k+".预测："+Config.favor+" ");
				            				}
				            				else if(rst[0][k]==Config.infavor_int) {
				            					System.out.print(k+".预测："+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print(k+".预测："+Config.none+" ");
				            				}
				            				if(rst[1][k]==Config.favor_int) {
				            					System.out.print("正确："+Config.favor+" ");
				            				}
				            				else if(rst[1][k]==Config.infavor_int) {
				            					System.out.print("正确："+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print("正确："+Config.none+" ");
				            				}
				            				System.out.println(tmpdata.org_none.get(j));
				            			}
				            			
									} catch (Exception e) {
										// TODO: handle exception
										System.out.println("请检查结果文件是否存在："+Config.ldasvmPredictResultDir+testOrgData.get(i).className+"_result.txt"+"\n");
									}
			            			System.out.println("\n");
			            		}
			            		
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
			            }});  
			        t.start();
				}
				else if(checkButton3.getSelection()) {//LDA-SVM
					
					System.out.println("LDA-SVM:");
					testsrc=testDataSrcText.getText();
					trainsrc=trainDataSrcText.getText();
					t = new Thread(new Runnable(){  
			            public void run(){
			            	try {
			            		ArrayList<StructuredPreproedData> testOrgData=RawDataPreprocesser.LoadOrgData(testsrc,null);
			            		for(int i=0;i<testOrgData.size();i++) {
			            			StructuredPreproedData tmpdata=testOrgData.get(i);
			            			try {
			            				int [][]rst=svm_result.GetSVMResultFromFile(Config.ldasvmPredictResultDir+testOrgData.get(i).className+"_result.txt", testOrgData.get(i).className);
				            			System.out.println("Target:"+testOrgData.get(i).className);
				            			int k=0;
				            			for(int j=0;j<tmpdata.org_favor.size();j++,k++) {
				            				if(rst[0][k]==Config.favor_int) {
				            					System.out.print(k+".预测："+Config.favor+" ");
				            				}
				            				else if(rst[0][k]==Config.infavor_int) {
				            					System.out.print(k+".预测："+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print(k+".预测："+Config.none+" ");
				            				}
				            				if(rst[1][k]==Config.favor_int) {
				            					System.out.print("正确："+Config.favor+" ");
				            				}
				            				else if(rst[1][k]==Config.infavor_int) {
				            					System.out.print("正确："+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print("正确："+Config.none+" ");
				            				}
				            				System.out.println(tmpdata.org_favor.get(j));
				            			}
				            			for(int j=0;j<tmpdata.org_infavor.size();j++,k++) {
				            				if(rst[0][k]==Config.favor_int) {
				            					System.out.print(k+".预测："+Config.favor+" ");
				            				}
				            				else if(rst[0][k]==Config.infavor_int) {
				            					System.out.print(k+".预测："+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print(k+".预测："+Config.none+" ");
				            				}
				            				if(rst[1][k]==Config.favor_int) {
				            					System.out.print("正确："+Config.favor+" ");
				            				}
				            				else if(rst[1][k]==Config.infavor_int) {
				            					System.out.print("正确："+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print("正确："+Config.none+" ");
				            				}
				            				System.out.println(tmpdata.org_infavor.get(j));
				            			}
				            			for(int j=0;j<tmpdata.org_none.size();j++,k++) {
				            				if(rst[0][k]==Config.favor_int) {
				            					System.out.print(k+".预测："+Config.favor+" ");
				            				}
				            				else if(rst[0][k]==Config.infavor_int) {
				            					System.out.print(k+".预测："+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print(k+".预测："+Config.none+" ");
				            				}
				            				if(rst[1][k]==Config.favor_int) {
				            					System.out.print("正确："+Config.favor+" ");
				            				}
				            				else if(rst[1][k]==Config.infavor_int) {
				            					System.out.print("正确："+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print("正确："+Config.none+" ");
				            				}
				            				System.out.println(tmpdata.org_none.get(j));
				            			}
				            			
									} catch (Exception e) {
										// TODO: handle exception
										System.out.println("请检查结果文件是否存在："+Config.ldasvmPredictResultDir+testOrgData.get(i).className+"_result.txt"+"\n");
									}
			            			System.out.println("\n");
			            		}
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
			            }});  
			        t.start();
				}
				else if(checkButton4.getSelection()) {//SL-SVM
					
					System.out.println("SL-SVM:");
					testsrc=testDataSrcText.getText();
					trainsrc=trainDataSrcText.getText();
					t = new Thread(new Runnable(){  
			            public void run(){
			            	try {
			            		ArrayList<StructuredPreproedData> testOrgData=RawDataPreprocesser.LoadOrgData(testsrc,null);
			            		for(int i=0;i<testOrgData.size();i++) {
			            			StructuredPreproedData tmpdata=testOrgData.get(i);
			            			try {
			            				int [][]rst=svm_result.GetSVMResultFromFile(Config.senFeaSvmResultDir+testOrgData.get(i).className+"_result.txt", testOrgData.get(i).className);
				            			System.out.println("Target:"+testOrgData.get(i).className);
				            			int k=0;
				            			for(int j=0;j<tmpdata.org_favor.size();j++,k++) {
				            				if(rst[0][k]==Config.favor_int) {
				            					System.out.print(k+".预测："+Config.favor+" ");
				            				}
				            				else if(rst[0][k]==Config.infavor_int) {
				            					System.out.print(k+".预测："+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print(k+".预测："+Config.none+" ");
				            				}
				            				if(rst[1][k]==Config.favor_int) {
				            					System.out.print("正确："+Config.favor+" ");
				            				}
				            				else if(rst[1][k]==Config.infavor_int) {
				            					System.out.print("正确："+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print("正确："+Config.none+" ");
				            				}
				            				System.out.println(tmpdata.org_favor.get(j));
				            			}
				            			for(int j=0;j<tmpdata.org_infavor.size();j++,k++) {
				            				if(rst[0][k]==Config.favor_int) {
				            					System.out.print(k+".预测："+Config.favor+" ");
				            				}
				            				else if(rst[0][k]==Config.infavor_int) {
				            					System.out.print(k+".预测："+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print(k+".预测："+Config.none+" ");
				            				}
				            				if(rst[1][k]==Config.favor_int) {
				            					System.out.print("正确："+Config.favor+" ");
				            				}
				            				else if(rst[1][k]==Config.infavor_int) {
				            					System.out.print("正确："+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print("正确："+Config.none+" ");
				            				}
				            				System.out.println(tmpdata.org_infavor.get(j));
				            			}
				            			for(int j=0;j<tmpdata.org_none.size();j++,k++) {
				            				if(rst[0][k]==Config.favor_int) {
				            					System.out.print(k+".预测："+Config.favor+" ");
				            				}
				            				else if(rst[0][k]==Config.infavor_int) {
				            					System.out.print(k+".预测："+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print(k+".预测："+Config.none+" ");
				            				}
				            				if(rst[1][k]==Config.favor_int) {
				            					System.out.print("正确："+Config.favor+" ");
				            				}
				            				else if(rst[1][k]==Config.infavor_int) {
				            					System.out.print("正确："+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print("正确："+Config.none+" ");
				            				}
				            				System.out.println(tmpdata.org_none.get(j));
				            			}
				            			
									} catch (Exception e) {
										// TODO: handle exception
										System.out.println("请检查结果文件是否存在："+Config.ldasvmPredictResultDir+testOrgData.get(i).className+"_result.txt"+"\n");
									}
			            			System.out.println("\n");
			            		}
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
							
			            }});  
			        t.start(); 
				}
				else if(checkButton5.getSelection()) {//Fast Ensemble LSA-GVM
					System.out.println("Fast Ensemble LSA-GVM:");
					testsrc=testDataSrcText.getText();
					trainsrc=trainDataSrcText.getText();
					t = new Thread(new Runnable(){  
			            public void run(){
			            	try {
			            		ArrayList<StructuredPreproedData> testOrgData=RawDataPreprocesser.LoadOrgData(testsrc,null);
			            		for(int i=0;i<testOrgData.size();i++) {
			            			StructuredPreproedData tmpdata=testOrgData.get(i);
			            			try {
			            				int [][]rst=ResultAnalyser.LoadLSAGVMResultINTARRAY(Config.ensemblelsaGVMOutputSrc+testOrgData.get(i).className+".txt", testOrgData.get(i).className);
				            			System.out.println("Target:"+testOrgData.get(i).className);
				            			int k=0;
				            			for(int j=0;j<tmpdata.org_favor.size();j++,k++) {
				            				if(rst[0][k]==Config.favor_int) {
				            					System.out.print(k+".预测："+Config.favor+" ");
				            				}
				            				else if(rst[0][k]==Config.infavor_int) {
				            					System.out.print(k+".预测："+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print(k+".预测："+Config.none+" ");
				            				}
				            				if(rst[1][k]==Config.favor_int) {
				            					System.out.print("正确："+Config.favor+" ");
				            				}
				            				else if(rst[1][k]==Config.infavor_int) {
				            					System.out.print("正确："+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print("正确："+Config.none+" ");
				            				}
				            				System.out.println(tmpdata.org_favor.get(j));
				            			}
				            			for(int j=0;j<tmpdata.org_infavor.size();j++,k++) {
				            				if(rst[0][k]==Config.favor_int) {
				            					System.out.print(k+".预测："+Config.favor+" ");
				            				}
				            				else if(rst[0][k]==Config.infavor_int) {
				            					System.out.print(k+".预测："+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print(k+".预测："+Config.none+" ");
				            				}
				            				if(rst[1][k]==Config.favor_int) {
				            					System.out.print("正确："+Config.favor+" ");
				            				}
				            				else if(rst[1][k]==Config.infavor_int) {
				            					System.out.print("正确："+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print("正确："+Config.none+" ");
				            				}
				            				System.out.println(tmpdata.org_infavor.get(j));
				            			}
				            			for(int j=0;j<tmpdata.org_none.size();j++,k++) {
				            				if(rst[0][k]==Config.favor_int) {
				            					System.out.print(k+".预测："+Config.favor+" ");
				            				}
				            				else if(rst[0][k]==Config.infavor_int) {
				            					System.out.print(k+".预测："+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print(k+".预测："+Config.none+" ");
				            				}
				            				if(rst[1][k]==Config.favor_int) {
				            					System.out.print("正确："+Config.favor+" ");
				            				}
				            				else if(rst[1][k]==Config.infavor_int) {
				            					System.out.print("正确："+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print("正确："+Config.none+" ");
				            				}
				            				System.out.println(tmpdata.org_none.get(j));
				            			}
				            			
									} catch (Exception e) {
										// TODO: handle exception
										e.printStackTrace();
										System.out.println("请检查结果文件是否存在："+Config.ensemblelsaGVMOutputSrc+testOrgData.get(i).className+".txt");
									}
			            			System.out.println("\n");
			            		}
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
			            }});  
			        t.start(); 
					
				}
				else {
					JOptionPane.showMessageDialog(null, "未选择算法类别！", "【出错啦】", JOptionPane.ERROR_MESSAGE);
				}
				/*try {
					TongJi_0520_tmp.main(null);
				} catch (Exception e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}*/
			}
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO 自动生成的方法存根
			}
		});
	    
	    MyPrintStream mps = new MyPrintStream(System.out, outputConsoleText);
	    System.setOut(mps);
	    System.setErr(mps);
	    shell.setLayout(mainUILayout);
	    shell.open();
	    // 开始事件处理循环，直到用户关闭窗口
	    while (!shell.isDisposed()) {
	        if (!display.readAndDispatch())
	            display.sleep();
	    }
	    display.dispose();
	}

}
class MyPrintStream extends PrintStream {
	private Text text;
	public MyPrintStream(OutputStream out, Text text) {
		super(out);
		this.text = text;
	}
	/** *//**
	 * 在这里重截,所有的打印方法都要调用的方法
	 */
	@Override
	public void write(byte[] buf, int off, int len) {
		final String message = new String(buf, off, len);
		/**//* SWT非界面线程访问组件的方式 */
		Display.getDefault().syncExec(new Thread(){
			public void run(){
				/**//* 在这里把信息添加到组件中 */
				text.append(message);
			}
		});
	}
}
