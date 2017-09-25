package sysu.consistency.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class Main {
	
	public static void main(String[] args) {
		String str = " ifa (";
		int ifIndex = str.indexOf("if");
		boolean before = true;
		boolean after = true;
		if(ifIndex>0){
		    String beforeIf = str.substring(ifIndex-1,ifIndex);
		    if(beforeIf.matches("[0-9a-zA-Z_]")){
		    	before = false;
		    }
		}
		if(ifIndex>=0&&ifIndex<str.length()-3){
			String afterIf = str.substring(ifIndex+2,ifIndex+3);
			if(afterIf.matches("[0-9a-zA-Z_]")){
				after = false;
			}
		}
		
		if(ifIndex>=0&&before&&after){
			System.out.println(true);
		}
	}
	
	public static int resolve(String expr) {
	    int result = 0;
	    Stack<Integer> numStack = new Stack<Integer>(); 
	    for(String str:expr.split(" ")){
	    	try{
	    		int num = Integer.parseInt(str);
	    		numStack.push(num);
	    		if(numStack.size()>16){
	    			return -2;
	    		}
	    	}catch(Exception e){
	    		if(str.equals("*")){
	    			if(numStack.size()<2){
	    				return -1;
	    			}
	    			int num1 = numStack.pop();
	    			int num2 = numStack.pop();
	    			
	    			int num = num1 * num2;
	    			
	    		}
	    		if(str.equals("^")){
	    			if(numStack.size()<1){
	    				return -1;
	    			}
	    			int num = numStack.pop();
	    			num++;
	    			numStack.push(num);
	    			
	    		}
	    		if(str.equals("+")){
	    			if(numStack.size()<2){
	    				return -1;
	    			}
	    			int num1 = numStack.pop();
	    			int num2 = numStack.pop();
	    			int num = num1 + num2;
	    			numStack.push(num);
	    		}
	    	}
	    }
	    if(numStack.size()!=1){
			return -1;
		}
	    result = numStack.pop();
	    
	    return result;
    }
	

}
