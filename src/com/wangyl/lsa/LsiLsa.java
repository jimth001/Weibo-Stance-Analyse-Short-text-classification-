package com.wangyl.lsa;



import java.util.ArrayList;
import java.util.Vector;

import com.wangyl.tools.ResultAnalyser;

import Jama.Matrix;
import Jama.SingularValueDecomposition;


/**
 * @author yuwc 
 * @date 2015.7.2
 * 
 * @author WangYunli
 * @data 2017.4.7
 * @description 要求corpus路径下至少有x份预料（x=LSD），计算得到的矩阵给出了测试语句与每份文件的相似度
 */
public class LsiLsa {
	public int iCol ;
	public int iRow ;
	public static int LSD = 30 ;
    private boolean isLsaFunExeced;
	public CreateMatrix crtMatrix;
	Vector<Double> d_vct;
	Vector<Double> temp_d_vct;
	Vector<Integer> q_vct;
	public LsiLsa(){
		crtMatrix  = new CreateMatrix();
		iCol = 0;
		iRow = 0;
		isLsaFunExeced=false;
	}
	
	public class Doc_cor{
		int doc_id;
		double correlation; //calculate by CompareVector()
	};
	Vector<Doc_cor> cor = new Vector<Doc_cor>();
	
	public Matrix M ;
    public Matrix u ;
    public Matrix s ;
    public Matrix v ;
    /**
     * @description 计算输入字符串与各个文档的语义相似度，打印结果。宜用于展示。
     * @param query
     */
    public void querySimilarity(String query){
		if(!isLsaFunExeced) {
			LSA();
			isLsaFunExeced=true;
		}
		Query(query); 
		//打印结果
		for (int i=0;i<cor.size();i++)//cor.size是文档的数量
		{
			System.out.print("Document "+crtMatrix.FindDocName(cor.get(i).doc_id)+" : "+cor.get(i).correlation+"\n");
		}
    }
    public void Reset() {
    	isLsaFunExeced=false;
    }
    /**
     * @author WangYunli
     * @param query
     * @return
     */
    public LsaResultAnalyser[] getOneSenSimilarity(String query) {
    	if(!isLsaFunExeced) {
    		LSA();//LSA初始化
    		isLsaFunExeced=true;
    	}
    	Query(query);
    	LsaResultAnalyser[] rstAnalyser=new LsaResultAnalyser[cor.size()];
    	for(int i=0;i<cor.size();i++) {
    		rstAnalyser[i]=new LsaResultAnalyser(crtMatrix.FindDocName(cor.get(i).doc_id), cor.get(i).correlation);
    	}
    	return rstAnalyser;
    }
    public LsaResultAnalyser[][] getQuerysSimilarity(ArrayList<String> querys) {
    	LsaResultAnalyser[][] rstAnalysers=new LsaResultAnalyser[querys.size()][];
    	for(int i=0;i<querys.size();i++) {
    		rstAnalysers[i]=getOneSenSimilarity(querys.get(i));
    	}
    	return rstAnalysers;
    }
    /**
     * 
     * @param keep
     */
    //降维
  	public void ReduceDim(int keep)
  	{
  		for (int i=keep;i<s.getColumnDimension();i++)
  		{
  			 s.set(i,i,0.0);
  		}
  		//System.out.print("S matrix 降维 到3！\n");
  		//MyStaticValue.PrintMatrix(s); 
  	}

  	//打印矩阵
  	public void PrintMatrix(Matrix mtx)
  	{	
  		for (int i = 0; i < mtx.getRowDimension(); i++)
  		{
  			for (int j = 0; j < mtx.getColumnDimension(); j++)
  			{
  			   System.out.printf ("m(%d,%d) = %g\t", i, j, mtx.get ( i, j));
  			}
  			System.out.printf("\n");
  		}
  	}
  	public LsaResultAnalyser[][] GetFeatureDq_groupdata(ArrayList<String> querys,boolean whetherCoordinate) {
  		double [][]rst=new double[querys.size()][];
  		for(int i=0;i<querys.size();i++) {
  			rst[i]=GetFeatureDq_single(querys.get(i), whetherCoordinate);
  		}
  		return transferdoubleArrayToResultAnalysers(rst);
  	}
  	public LsaResultAnalyser[][] transferdoubleArrayToResultAnalysers(double [][] a) {
  		LsaResultAnalyser [][]rstAnalyser=new LsaResultAnalyser[a.length][a[0].length];
  		for(int i=0;i<a.length;i++) {
  			for(int j=0;j<a[0].length;j++) {
  				rstAnalyser[i][j]=new LsaResultAnalyser(a[i][j]);
  			}
  		}
  		return rstAnalyser;
  	}
  	/**
  	 * 
  	 * @param query
  	 * @param whetherCoordinate 是否将特征坐标化
  	 * @return
  	 */
  	public double[] GetFeatureDq_single(String query,boolean whetherCoordinate) {
  		if(!isLsaFunExeced) {
    		LSA();//LSA初始化
    		isLsaFunExeced=true;
    	}
  		String[] strs = null;
  		if(query.isEmpty()){
  			double []a=new double[LSD];
  			for(int i=0;i<LSD;i++) {
  	  			a[i]=0;
  	  		}
  			return a;
  		}else{
  			strs = query.split(" ");
  		}
  		double []q=new double[iRow];
  		double []d=new double[LSD];
  		for(int i=0;i<iRow;i++) {
  			q[i]=0;
  		}
  		for(int i=0;i<LSD;i++) {
  			d[i]=0;
  		}
  		// first fill Xq
  		for (String word : strs) {
  			//to do : porter stemming 
  			if (crtMatrix.wordList.containsKey(word)) /*word is in the list*/
  			{
  				q[crtMatrix.wordList.get(word)]++;
  			}
  		}
  		// Dq = Xq' T S^-1
  		// second let's calculate Xq' T
  		for (int i = 0; i < LSD; i++)
  		{
  			double sum = 0;
  			int tmp=crtMatrix.wordList.size();
  			for (int j = 0; j < tmp; j++)
  			{
  				sum += q[j] * u.get(j,i); 
  			}
  			d[i]=sum;
  		}
  		if(whetherCoordinate) {
  			for (int k = 0; k < LSD; k++)
  	  		{
  	  			d[k]=d[k]/s.get(k,k);
  	  		}
  		}
  		return d;
  	}
  	//相似查询
  	public void Query(String query){
  		cor.clear();
  		String[] strs = null;
  		if(query.isEmpty()){
  			return ;
  		}else{
  			strs = query.split(" ");
  		}
  		
  		//Vector<Integer> q_vct = new Vector<Integer>(iRow);
  		MyStaticValue.initVector(q_vct, iRow, 0);
  		//Vector<Double> d_vct = new Vector<Double>(LSD);
  		MyStaticValue.initVector(d_vct, LSD, 0.0);
  		
  		// first fill Xq
  		for (String word : strs) {
  	 
  			//to do : porter stemming 
  			if (crtMatrix.wordList.containsKey(word)) /*word is in the list*/
  			{
  				q_vct.set(crtMatrix.wordList.get(word),q_vct.get(crtMatrix.wordList.get(word))+1);
  			}
  		}
  		// Dq = Xq' T S^-1
  		// second let's calculate Xq' T
  		for (int i = 0; i < LSD; i++)
  		{
  			double sum = 0;
  			int tmp=crtMatrix.wordList.size();
  			for (int j = 0; j < tmp; j++)
  			{
  				sum += q_vct.get(j) * u.get(j,i); 
  			}
  			d_vct.set(i,sum);
  		}
  		// then calculate (Xq' T) S^-1
  		for (int k = 0; k < LSD; k++)
  		{
  			d_vct.set(k, d_vct.get(k)* (1/s.get(k,k)));
  		}
  		//MyStaticValue.PrintVector(d_vct, LSD);
  		//compare each document with Dq
  		for (int l=0;l<crtMatrix.docList.size();l++)
  		{
  			//Vector<Double> temp_d_vct = new Vector<Double>(LSD);
  			MyStaticValue.initVector(temp_d_vct, LSD, 0.0);
  			// fill temp document vector
  			for (int m=0;m<LSD;m++)
  			{
  				temp_d_vct.set(m, v.get(l,m)*s.get(m, m)); /*coordinate need to multiply by single value,that is s*/
  			}
  			
  			AddCorrelation(l,CompareVector(d_vct, temp_d_vct, LSD));	
  		}
  		
  	} 
  	public void LSA()//根据corpus下文档初始化LSA，确定TFIDF矩阵，iCol，iRow，这些参数在这之后基于当前语料的推断中都是定值，为了优化内存占用，改写了一些代码
  	{
  		M = crtMatrix.createTfIdfMatrix();
  		iCol = crtMatrix.iCol;
  		iRow = crtMatrix.iRow;
  	    //System.out.print( "M Matrix : ------------------------------------\n");
  		//MyStaticValue.PrintMatrix(mtx);
  		CountSVD();
  	 /* System.out.print( "U Matrix : ------------------------------------\n");
  	    MyStaticValue.PrintMatrix(mtx);
  	    System.out.print( "V Matrix : ------------------------------------\n");
  	    MyStaticValue.PrintMatrix(v_mtx);
  	    System.out.print( "S Matrix : ------------------------------------\n");
  	    MyStaticValue.PrintVector(s_vct);	*/
  		// reduce the dimensions
  		// that is, set N smallest value of vector S to 0
  		// now we keep 3 bigest value
  		ReduceDim(LSD);
  		d_vct = new Vector<Double>(LSD);//new
  		temp_d_vct = new Vector<Double>(LSD);//new
  		q_vct = new Vector<Integer>(iRow);//new
  		System.gc();
  		//PrintVector(s_vct,docList.size());
  	}
  	
  	//计算余弦夹角
  	double CompareVector(Vector<Double> v1,Vector<Double> v2,int size)
  	{
  		// A(dot)B = |A||B|Cos(theta)	
  		// so Cos(theta) = A(dot)B / |A||B|
  		double a_dot_b=0;
  		for (int i=0;i<size;i++)
  		{
  			a_dot_b += v1.get(i) * v2.get(i);
  		}
  		double A=0;
  		for (int j=0;j<size;j++)
  		{
  			A += v1.get(j) * v1.get(j);
  		}
  		A = Math.sqrt(A);
  		double B=0;
  		for (int k=0;k<size;k++)
  		{
  			B +=v2.get(k)*v2.get(k);
  		}
  		B = Math.sqrt(B);	
  		return a_dot_b/(A*B);
  	}
  	
  	public void AddCorrelation(int docid,double corrlation)
  	{
  		Doc_cor d_cor = new Doc_cor();
  		d_cor.doc_id = docid;
  		d_cor.correlation = corrlation;
  		if (cor.size() == 0)
  		{
  			cor.add(d_cor);
  			return;
  		}
  		 
  		int i=0;
  		boolean isMax = false;
  		for (Doc_cor doc_cor : cor) {
  			if (corrlation > doc_cor.correlation)
  			{
  				cor.insertElementAt(d_cor,i);
  				isMax = true;
  				return;
  			}
  			i++;
  		}
  		
  		if (!isMax)
  		{
  			cor.add(d_cor);
  		}
  	}
  	
  	//SVD
  	public void CountSVD(){
  		// S = U S V^T  so first let's allocate U,S,V these three matrix 
  		SingularValueDecomposition svd = M.svd();
  		u = svd.getU();
  		s = svd.getS();				  /*S is stored in a n-d vector*/
  		v = svd.getV();			      /*V is a N by N matrix*/
  		//MyStaticValue.PrintMatrix(u); 
  		//MyStaticValue.PrintMatrix(s); 
  		//MyStaticValue.PrintMatrix(v);		
  	}
}
