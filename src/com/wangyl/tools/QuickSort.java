package com.wangyl.tools;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class QuickSort {
    @SuppressWarnings("unchecked")
    //对上述快排函数原型修改，使其可以对任意对象类型数组进行排序。这个函数为内部使用，外部排序函数接口为sort()，sort函数要求对象必须实现Comparable接口，可以提供编译时类型检测，见后文。
    private static void quickSort(Object[] in,int begin, int end) {
        if( begin == end || begin == (end-1) ) return;
        Object p = in[begin];
        int a = begin +1;
        int b = a;
        for( ; b < end; b++) {
            //该对象类型数组必须实现Comparable接口，这样才能使用compareTo函数进行比较
            if( ((Comparable<Object>)in[b]).compareTo(p) < 0) {
                if(a == b){a++; continue;}
                Object temp = in[a];
                in[a] = in[b];
                in[b] = temp;
                a++;
            }
        }
        in[begin] = in[a-1];
        in[a-1] = p;
        if( a-1 > begin){
            quickSort(in,begin, a);
        } 
        if( end-1 > a ) {
            quickSort(in,a, end);
        } 
        return;
    }
    
    //使用泛型，对任意对象数组排序，该对象类型数组必须实现Comparable接口
    public static <T extends Comparable<? super T>> void sort(T[] input){
        quickSort(input,0,input.length);
    }
    
    //添加对List对象进行排序的功能，参考了Java中的Java.util.Collections类的sort()函数
    public static <T extends Comparable<? super T>> void sort(List<T> list){
        Object[] t = list.toArray();//将列表转换为数组
        quickSort(t,0,t.length); //对数组进行排序
        //数组排序完成后再写回到列表中
        ListIterator<T> i = list.listIterator();
        for (int j=0; j<t.length; j++) {
            i.next();
            i.set((T)t[j]);
        }
    }
    
    //由于Java中原始数据类型（int、double、byte等）无法使用泛型，所以只能使用函数重载机制实现对这些原始类型数组（int[]、double[]、byte[]等）的排序。这里为了共用同一个排序函数，利用原始类型的(AutoBoxing，UnBoxing)机制将其封装为对应对象类型，组成新的对象数组，排序后再解封装，这样的缺点是需要额外的转换步骤、额外的空间保存封装后的数组。另一种方式是将排序代码复制到各个重载函数中，官方API中的Java.util.Arrays这个类中的sort()函数就是使用这种方法，可以从Arrays类的源代码看出。
    public static void sort(int[] input){
        Integer[] t = new Integer[input.length];
        for(int i = 0; i < input.length; i++){
            t[i] = input[i];//封装
        }
        quickSort(t,0,t.length);//排序
        for(int i = 0; i < input.length; i++){
            input[i] = t[i];//解封装
        }
    }
    //double[]数组的重载函数
    public static void sort(double[] input){
        Double[] t = new Double[input.length];
        for(int i = 0; i < input.length; i++){
            t[i] = input[i];
        }
        quickSort(t,0,t.length);
        for(int i = 0; i < input.length; i++){
            input[i] = t[i];
        }
    }
    //byte[]数组的重载函数
    public static void sort(byte[] input){
        Byte[] t = new Byte[input.length];
        for(int i = 0; i < input.length; i++){
            t[i] = input[i];
        }
        quickSort(t,0,t.length);
        for(int i = 0; i < input.length; i++){
            input[i] = t[i];
        }
    }
    //short[]数组的重载函数
    public static void sort(short[] input){
        Short[] t = new Short[input.length];
        for(int i = 0; i < input.length; i++){
            t[i] = input[i];
        }
        quickSort(t,0,t.length);
        for(int i = 0; i < input.length; i++){
            input[i] = t[i];
        }
    }
    //char[]数组的重载函数
    public static void sort(char[] input){
        Character[] t = new Character[input.length];
        for(int i = 0; i < input.length; i++){
            t[i] = input[i];
        }
        quickSort(t,0,t.length);
        for(int i = 0; i < input.length; i++){
            input[i] = t[i];
        }
    }
    //float[]数组的重载函数
    public static void sort(float[] input){
        Float[] t = new Float[input.length];
        for(int i = 0; i < input.length; i++){
            t[i] = input[i];
        }
        quickSort(t,0,t.length);
        for(int i = 0; i < input.length; i++){
            input[i] = t[i];
        }
    }
    
    //测试用的main函数
     public static void main(String[] args) {
        //生产一个随机数组成的int[]数组，用来测试
        int LEN = 1;
        int[] input = {1,2,1,2,1};
        //input[0]=0;
        System.out.print("int[] after sorting: ");
        sort(input);
        for(int i : input) {
          System.out.print(i + " ");
        } 
        System.out.println();
    }
}
