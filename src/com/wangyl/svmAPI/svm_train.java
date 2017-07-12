package com.wangyl.svmAPI;

import java.io.PrintStream;
import libsvm.svm_parameter;
import libsvm.svm_problem;
public class svm_train
{
  private svm_parameter param;
  private svm_problem prob;
  private libsvm.svm_model model;
  private String input_file_name;
  private String model_file_name;
  private String error_msg;
  private int cross_validation;
  private int nr_fold;
  private static libsvm.svm_print_interface svm_print_null = new libsvm.svm_print_interface() {
    public void print(String paramAnonymousString) {}
  };
  
  svm_train() {}
  
  private static void exit_with_help() {
    System.out.print("Usage: svm_train [options] training_set_file [model_file]\noptions:\n-s svm_type : set type of SVM (default 0)\n\t0 -- C-SVC\t\t(multi-class classification)\n\t1 -- nu-SVC\t\t(multi-class classification)\n\t2 -- one-class SVM\n\t3 -- epsilon-SVR\t(regression)\n\t4 -- nu-SVR\t\t(regression)\n-t kernel_type : set type of kernel function (default 2)\n\t0 -- linear: u'*v\n\t1 -- polynomial: (gamma*u'*v + coef0)^degree\n\t2 -- radial basis function: exp(-gamma*|u-v|^2)\n\t3 -- sigmoid: tanh(gamma*u'*v + coef0)\n\t4 -- precomputed kernel (kernel values in training_set_file)\n-d degree : set degree in kernel function (default 3)\n-g gamma : set gamma in kernel function (default 1/num_features)\n-r coef0 : set coef0 in kernel function (default 0)\n-c cost : set the parameter C of C-SVC, epsilon-SVR, and nu-SVR (default 1)\n-n nu : set the parameter nu of nu-SVC, one-class SVM, and nu-SVR (default 0.5)\n-p epsilon : set the epsilon in loss function of epsilon-SVR (default 0.1)\n-m cachesize : set cache memory size in MB (default 100)\n-e epsilon : set tolerance of termination criterion (default 0.001)\n-h shrinking : whether to use the shrinking heuristics, 0 or 1 (default 1)\n-b probability_estimates : whether to train a SVC or SVR model for probability estimates, 0 or 1 (default 0)\n-wi weight : set the parameter C of class i to weight*C, for C-SVC (default 1)\n-v n : n-fold cross validation mode\n-q : quiet mode (no outputs)\n");
    System.exit(1);
  }
  
  private void do_cross_validation()
  {
    int j = 0;
    double d1 = 0.0D;
    double d2 = 0.0D;double d3 = 0.0D;double d4 = 0.0D;double d5 = 0.0D;double d6 = 0.0D;
    double[] arrayOfDouble = new double[this.prob.l];
    
    libsvm.svm.svm_cross_validation(this.prob, this.param, this.nr_fold, arrayOfDouble);
    int i; if ((this.param.svm_type == 3) || (this.param.svm_type == 4))
    {

      for (i = 0; i < this.prob.l; i++)
      {
        double d7 = this.prob.y[i];
        double d8 = arrayOfDouble[i];
        d1 += (d8 - d7) * (d8 - d7);
        d2 += d8;
        d3 += d7;
        d4 += d8 * d8;
        d5 += d7 * d7;
        d6 += d8 * d7;
      }
      System.out.print("Cross Validation Mean squared error = " + d1 / this.prob.l + "\n");
      System.out.print("Cross Validation Squared correlation coefficient = " + (this.prob.l * d6 - d2 * d3) * (this.prob.l * d6 - d2 * d3) / ((this.prob.l * d4 - d2 * d2) * (this.prob.l * d5 - d3 * d3)) + "\n");


    }
    else
    {

      for (i = 0; i < this.prob.l; i++)
        if (arrayOfDouble[i] == this.prob.y[i])
          j++;
      System.out.print("Cross Validation Accuracy = " + 100.0D * j / this.prob.l + "%\n");
    }
  }
  
  private void run(String[] paramArrayOfString) throws java.io.IOException
  {
    parse_command_line(paramArrayOfString);
    read_problem();
    this.error_msg = libsvm.svm.svm_check_parameter(this.prob, this.param);
    
    if (this.error_msg != null)
    {
      System.err.print("ERROR: " + this.error_msg + "\n");
      System.exit(1);
    }
    
    if (this.cross_validation != 0)
    {
      do_cross_validation();
    }
    else
    {
      this.model = libsvm.svm.svm_train(this.prob, this.param);
      libsvm.svm.svm_save_model(this.model_file_name, this.model);
    }
  }
  //调用这个函数，传进命令参数，训练模型
  public static void TrainModel(String[] paramArrayOfString) throws java.io.IOException
  {
    svm_train localSvm_train = new svm_train();
    localSvm_train.run(paramArrayOfString);
  }
  
  private static double atof(String paramString)
  {
    double d = Double.valueOf(paramString).doubleValue();
    if ((Double.isNaN(d)) || (Double.isInfinite(d)))
    {
      System.err.print("NaN or Infinity in input\n");
      System.exit(1);
    }
    return d;
  }
  
  private static int atoi(String paramString)
  {
    return Integer.parseInt(paramString);
  }
  

  private void parse_command_line(String[] paramArrayOfString)
  {
    libsvm.svm_print_interface localSvm_print_interface = null;
    
    this.param = new svm_parameter();
    
    this.param.svm_type = 0;
    this.param.kernel_type = 2;
    this.param.degree = 3;
    this.param.gamma = 0.0D;
    this.param.coef0 = 0.0D;
    this.param.nu = 0.5D;
    this.param.cache_size = 100.0D;
    this.param.C = 1.0D;
    this.param.eps = 0.001D;
    this.param.p = 0.1D;
    this.param.shrinking = 1;
    this.param.probability = 0;
    this.param.nr_weight = 0;
    this.param.weight_label = new int[0];
    this.param.weight = new double[0];
    this.cross_validation = 0;
    
    int i=0;//此处有改动
    for ( i = 0; i < paramArrayOfString.length; i++)
    {
      if (paramArrayOfString[i].charAt(0) != '-') break;
      i++; if (i >= paramArrayOfString.length)
        exit_with_help();
      switch (paramArrayOfString[(i - 1)].charAt(1))
      {
      case 's': 
        this.param.svm_type = atoi(paramArrayOfString[i]);
        break;
      case 't': 
        this.param.kernel_type = atoi(paramArrayOfString[i]);
        break;
      case 'd': 
        this.param.degree = atoi(paramArrayOfString[i]);
        break;
      case 'g': 
        this.param.gamma = atof(paramArrayOfString[i]);
        break;
      case 'r': 
        this.param.coef0 = atof(paramArrayOfString[i]);
        break;
      case 'n': 
        this.param.nu = atof(paramArrayOfString[i]);
        break;
      case 'm': 
        this.param.cache_size = atof(paramArrayOfString[i]);
        break;
      case 'c': 
        this.param.C = atof(paramArrayOfString[i]);
        break;
      case 'e': 
        this.param.eps = atof(paramArrayOfString[i]);
        break;
      case 'p': 
        this.param.p = atof(paramArrayOfString[i]);
        break;
      case 'h': 
        this.param.shrinking = atoi(paramArrayOfString[i]);
        break;
      case 'b': 
        this.param.probability = atoi(paramArrayOfString[i]);
        break;
      case 'q': 
        localSvm_print_interface = svm_print_null;
        i--;
        break;
      case 'v': 
        this.cross_validation = 1;
        this.nr_fold = atoi(paramArrayOfString[i]);
        if (this.nr_fold < 2)
        {
          System.err.print("n-fold cross validation: n must >= 2\n");
          exit_with_help();
        }
        break;
      case 'w': 
        this.param.nr_weight += 1;
        
        Object localObject = this.param.weight_label;
        this.param.weight_label = new int[this.param.nr_weight];
        System.arraycopy(localObject, 0, this.param.weight_label, 0, this.param.nr_weight - 1);
        


        localObject = this.param.weight;
        this.param.weight = new double[this.param.nr_weight];
        System.arraycopy(localObject, 0, this.param.weight, 0, this.param.nr_weight - 1);
        

        this.param.weight_label[(this.param.nr_weight - 1)] = atoi(paramArrayOfString[(i - 1)].substring(2));
        this.param.weight[(this.param.nr_weight - 1)] = atof(paramArrayOfString[i]);
        break;
      case 'f': case 'i': case 'j': case 'k': case 'l': case 'o': case 'u': default: 
        System.err.print("Unknown option: " + paramArrayOfString[(i - 1)] + "\n");
        exit_with_help();
      }
      
    }
    libsvm.svm.svm_set_print_string_function(localSvm_print_interface);
    


    if (i >= paramArrayOfString.length) {
      exit_with_help();
    }
    this.input_file_name = paramArrayOfString[i];
    
    if (i < paramArrayOfString.length - 1) {
      this.model_file_name = paramArrayOfString[(i + 1)];
    }
    else {
      int j = paramArrayOfString[i].lastIndexOf('/');
      j++;
      this.model_file_name = (paramArrayOfString[i].substring(j) + ".model");
    }
  }
  

  private void read_problem()
    throws java.io.IOException
  {
    java.io.BufferedReader localBufferedReader = new java.io.BufferedReader(new java.io.FileReader(this.input_file_name));
    java.util.Vector localVector1 = new java.util.Vector();
    java.util.Vector localVector2 = new java.util.Vector();
    int i = 0;
    
    for (;;)
    {
      String str = localBufferedReader.readLine();
      if (str == null)
        break;
      java.util.StringTokenizer localStringTokenizer = new java.util.StringTokenizer(str, " \t\n\r\f:");
      
      localVector1.addElement(Double.valueOf(atof(localStringTokenizer.nextToken())));
      int k = localStringTokenizer.countTokens() / 2;
      libsvm.svm_node[] arrayOfSvm_node = new libsvm.svm_node[k];
      for (int m = 0; m < k; m++)
      {
        arrayOfSvm_node[m] = new libsvm.svm_node();
        arrayOfSvm_node[m].index = atoi(localStringTokenizer.nextToken());
        arrayOfSvm_node[m].value = atof(localStringTokenizer.nextToken());
      }
      if (k > 0) i = Math.max(i, arrayOfSvm_node[(k - 1)].index);
      localVector2.addElement(arrayOfSvm_node);
    }
    
    this.prob = new svm_problem();
    this.prob.l = localVector1.size();
    this.prob.x = new libsvm.svm_node[this.prob.l][];
    for (int j = 0; j < this.prob.l; j++)
      this.prob.x[j] = ((libsvm.svm_node[])localVector2.elementAt(j));
    this.prob.y = new double[this.prob.l];
    int j=0;//此处有改动
    for (j = 0; j < this.prob.l; j++) {
      this.prob.y[j] = ((Double)localVector1.elementAt(j)).doubleValue();
    }
    if ((this.param.gamma == 0.0D) && (i > 0)) {
      this.param.gamma = (1.0D / i);
    }
    if (this.param.kernel_type == 4) {
      for (j = 0; j < this.prob.l; j++)
      {
        if (this.prob.x[j][0].index != 0)
        {
          System.err.print("Wrong kernel matrix: first column must be 0:sample_serial_number\n");
          System.exit(1);
        }
        if (((int)this.prob.x[j][0].value <= 0) || ((int)this.prob.x[j][0].value > i))
        {
          System.err.print("Wrong input format: sample_serial_number out of range\n");
          System.exit(1);
        }
      }
    }
    localBufferedReader.close();
  }
}
