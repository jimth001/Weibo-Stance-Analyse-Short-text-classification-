package com.wangyl.lda;



import java.util.ArrayList;

import com.wangyl.config.Config;
import com.wangyl.preprocesser.RawDataPreprocesser;
import com.wangyl.preprocesser.StructuredPreproedData;

import jgibblda.Estimator;
import jgibblda.Inferencer;
import jgibblda.LDACmdOption;
import jgibblda.Model;

/**
 *@Option(name="-est", usage="Specify whether we want to estimate model from scratch")
 @Default est = false;是否从零开始估计模型？
 @Option(name="-estc", usage="Specify whether we want to continue the last estimation")
 @Default estc = false; 是否继续上一次的估计？
 *@Option(name="-inf", usage="Specify whether we want to do inference")
 *@Default inf = true;是否要做推断？
 *@Option(name="-dir", usage="Specify directory")
 *@Default dir = "";目录
 *@Option(name="-dfile", usage="Specify data file")
 *@Default dfile = "";数据文件名称
 *@Option(name="-model", usage="Specify the model name")
 *@Default modelName = "";选择使用哪一个迭代的模型结果来进行推断,一般是model-final
 *@Option(name="-alpha", usage="Specify alpha")
 *@Default alpha = -1.0D;LDA中的alpha参数
 *@Option(name="-beta", usage="Specify beta")
 *@Default beta = -1.0D;LDA中的beta参数
 @Option(name="-ntopics", usage="Specify the number of topics")
 @Default K = 100;话题的数目
 @Option(name="-niters", usage="Specify the number of iterations")
 @Default niters = 1000;迭代次数
 @Option(name="-savestep", usage="Specify the number of steps to save the model since the last save")
 @Default savestep = 100;指定多少步骤数之后保存一次模型？
 @Option(name="-twords", usage="Specify the number of most likely words to be printed for each topic")
 @Default twords = 100;每个主题列出多少个最可能的词语数量（概率top n）
 @Option(name="-withrawdata", usage="Specify whether we include raw data in the input")
 @Default withrawdata = false;训练输入是否加入raw data
 @Option(name="-wordmap", usage="Specify the wordmap file")
 @Default wordMapFileName = "wordmap.txt";指定wordmap文件
 @Detest 数据集要求：第一行是total num
 */

public class LDA_api
{
	public LDACmdOption option = new LDACmdOption();
	public LDA_api() {}
	
	public void Analyse()
	{
		if ((option.est) || (option.estc)) {
			Estimator estimator = new Estimator();
			estimator.init(option);
			estimator.estimate();
		}
		else if (option.inf) {
			Inferencer inferencer = new Inferencer();
			inferencer.init(option);
			Model newModel = inferencer.inference(); 
			/*for (int i = 0; i < newModel.phi.length; i++)
			{
				System.out.println("-----------------------\ntopic" + i + " : ");
				for (int j = 0; j < 10; j++) {
					System.out.println((String)inferencer.globalDict.id2word.get(Integer.valueOf(j)) + "\t" + newModel.phi[i][j]);
				}
			}*/
		}
	}
	public static void Infer(String dir,String inputfilename,int n) {
		LDA_api mylda=new LDA_api();
		mylda.option.dir=dir;
		mylda.option.dfile=inputfilename;
		mylda.option.est=false;
		mylda.option.estc=false;
		mylda.option.inf=true;
		mylda.option.modelName="model-final";
		mylda.option.K=n;
		mylda.option.savestep=1000;
		//mylda.option.alpha=0.1;
		mylda.option.beta=0.01;
		mylda.option.twords=50;
		mylda.Analyse();
	}
	private static void GenerateModelUseDefaultPara(String dir,String inputfilename,int n) {
		LDA_api mylda=new LDA_api();
		mylda.option.dir=dir;
		mylda.option.dfile=inputfilename;
		mylda.option.est=true;
		mylda.option.estc=false;
		mylda.option.inf=false;
		mylda.option.modelName="model-final";
		mylda.option.K=n;
		mylda.option.savestep=1000;
		//mylda.option.alpha=0.1;
		mylda.option.beta=0.01;
		mylda.option.twords=50;
		mylda.Analyse();
	}
	/**
	 * @description 为每个主题生成一个lda模型，包含了LDA预处理操作，只需完成raw data的预处理即可
	 */
	public static void GenerateModelsForEachTagert(String modelDir,int n) {
		ArrayList<StructuredPreproedData> spd;
		try {
			spd = RawDataPreprocesser.LoadTrainDataFromPreproedFile();
			for(int i=0;i<spd.size();i++) {
				RawDataPreprocesser.PreproForLDA(spd.get(i).className);
				GenerateModelUseDefaultPara(modelDir+spd.get(i).className+"\\",spd.get(i).className+".txt",n);
			}
			
			//Test(Config.LDAInputDir+"深圳禁摩限电\\", "深圳禁摩限电_AGAINST_0.txt");
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	public static void GenerateUniversalModel(int n) {
		GenerateModelUseDefaultPara(Config.LDAInputDir, Config.LDAInputFileName,n);
	}
}