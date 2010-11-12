package com.secureast.passpicture.basic;

public class RandomLine {
	
	static int[] oneDimen(int min, int max, int length){
		int[] iArray = new int[length];
		
		for(int i=0; i<length; i++){
			iArray[i]=min+ (int)(Math.random()*((max-min)+1));
		}
		
		return iArray;
	}
	
	
	static public void main(String[] argc){
		
		int[] test = RandomLine.oneDimen(20, 32, 40);
		
		for (int i=0; i<test.length; i++){
			System.out.print("["+test[i]+"]");
		}
		
		
	}

}
