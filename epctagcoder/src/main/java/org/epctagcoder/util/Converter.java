//http://www.exploringbinary.com/decimal-binary-conversion-using-deci-binary/

package org.epctagcoder.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Converter {
	static final Map<String, String> hexToBinMap = new HashMap<String, String>();
	static final Map<String, String> binToHexMap = new HashMap<String, String>();
	
	static {
		hexToBinMap.put("0", "0000");
		hexToBinMap.put("1", "0001");
		hexToBinMap.put("2", "0010");
		hexToBinMap.put("3", "0011");
		hexToBinMap.put("4", "0100");
		hexToBinMap.put("5", "0101");
		hexToBinMap.put("6", "0110");
		hexToBinMap.put("7", "0111");
		hexToBinMap.put("8", "1000");
		hexToBinMap.put("9", "1001");
		hexToBinMap.put("A", "1010");
		hexToBinMap.put("B", "1011");
		hexToBinMap.put("C", "1100");
		hexToBinMap.put("D", "1101");
		hexToBinMap.put("E", "1110");
		hexToBinMap.put("F", "1111");
		
		binToHexMap.put("0000", "0");
		binToHexMap.put("0001", "1");
		binToHexMap.put("0010", "2");
		binToHexMap.put("0011", "3");
		binToHexMap.put("0100", "4");
		binToHexMap.put("0101", "5");
		binToHexMap.put("0110", "6");
		binToHexMap.put("0111", "7");
		binToHexMap.put("1000", "8");
		binToHexMap.put("1001", "9");
		binToHexMap.put("1010", "A");
		binToHexMap.put("1011", "B");
		binToHexMap.put("1100", "C");
		binToHexMap.put("1101", "D");
		binToHexMap.put("1110", "E");
		binToHexMap.put("1111", "F");
	}
	
	
	public static String hexToBin(String hex) {
		int len = hex.length();
		StringBuilder bin = new StringBuilder();
		
		hex = hex.toUpperCase();
		
		for (int i=0; i<len; i++) {
			bin.append(hexToBinMap.get(hex.substring(i, i+1)));
		}
		
		return bin.toString();
	}
	
	
	public static String binToHex(String bin) {
		int endIndex = 4;
		int len = bin.length() / endIndex;
		int startIndex = 0;

		StringBuilder hex = new StringBuilder();
	    for (int i = 0; i < len; i++) {
	    	hex.append(  binToHexMap.get(bin.substring(startIndex, endIndex)) );
	        startIndex = endIndex;
	        endIndex = endIndex+4;
	    } 		

		return hex.toString();
	}	
	
	
	public static String binToString(String s) {
		StringBuilder bin = new StringBuilder();
		
		for(int i = 0; i <= s.length() - 8; i+=8) {
		    int k = Integer.parseInt(s.substring(i, i+8), 2);
		    bin.append( (char) k );
		}
		
		return bin.toString().trim();
	}	
	
	// funciona, substituir?
	public static String decToBin(String dec, int bits) {
		return strZero( new BigInteger(dec).toString(2).toString(), bits);
	}
	public static String decToBin(Integer dec, int bits) {
		return strZero( BigInteger.valueOf(dec.longValue()).toString(2).toString(), bits);
	}	
	
	
	// funciona, substituir?
	public static String binToDec(String bin) {
		return new BigInteger(bin,2).toString();
	}
	
	
	// montei esse, dá dó descartar
	public static String binToDec2(String bin) {
		int len = bin.length();
		int rev = len - 1;
		BigDecimal d = new BigDecimal("0");

		StringBuilder dec = new StringBuilder();
		for (int i = 0; i < len; i++) {
			String pos = bin.substring(i, i + 1);
			d = d.add( new BigDecimal(pos).multiply( new BigDecimal("2").pow(rev) ) );
			rev--;
		}
		dec.append(d);
		return dec.toString();
	}	
	

	
	
//	http://stackoverflow.com/questions/4211705/binary-to-text-in-java?noredirect=1&lq=1
	public static String convertBinToBit(String s, int fromBit, int toBit) {
		StringBuilder bin = new StringBuilder();
		StringBuilder regex = new StringBuilder().append("(?<=\\G.{").append(fromBit).append("})");
		
		String[] split = s.split(regex.toString());
		for (String a : split) {
			bin.append( lPadZero(Integer.parseInt(a), toBit) );
		}
		
		return bin.toString();
	}
	
	

	
    public static String fill(String text, int size) {
        StringBuilder builder = new StringBuilder(text);
        while (builder.length() < size) {
            builder.append('0');
        }
        return builder.toString();
    }	
	
    
 
   
   //http://stackoverflow.com/questions/917163/convert-a-string-like-testing123-to-binary-in-java
   public static String StringtoBinary(String str, int bits) {
	    String result = "";
	    String tmpStr;
	    int tmpInt;
	    char[] messChar = str.toCharArray();

	    for (int i = 0; i < messChar.length; i++) {
	        tmpStr = Integer.toBinaryString(messChar[i]);
	        tmpInt = tmpStr.length();
	        if(tmpInt != bits) {
	            tmpInt = bits - tmpInt;
	            if (tmpInt == bits) {
	                result += tmpStr;
	            } else if (tmpInt > 0) {
	                for (int j = 0; j < tmpInt; j++) {
	                    result += "0";
	                }
	                result += tmpStr;
	            } else {
	                System.err.println("argument 'bits' is too small");
	            }
	        } else {
	            result += tmpStr;
	        }
//	        result += " "; // separator
	    }

	    return result;
	}   
   
   
   
   public static String lPadZero(int in, int fill){

	    boolean negative = false;
	    int value, len = 0;

	    if(in >= 0){
	        value = in;
	    } else {
	        negative = true;
	        value = - in;
	        in = - in;
	        len ++;
	    }

	    if(value == 0){
	        len = 1;
	    } else{         
	        for(; value != 0; len ++){
	            value /= 10;
	        }
	    }

	    StringBuilder sb = new StringBuilder();

	    if(negative){
	        sb.append('-');
	    }

	    for(int i = fill; i > len; i--){
	        sb.append('0');
	    }

	    sb.append(in);

	    return sb.toString();       
	}   
   
  
   
   
   public static String strZero(String str, int len) {
	   
		StringBuilder sb = new StringBuilder();

		for (int toPrepend=len-str.length(); toPrepend>0; toPrepend--) {
		    sb.append('0');
		}
		
		sb.append(str);			
		return sb.toString();	   
   }
   
   
   public static boolean isNumeric(String str) {
	   return str.chars().allMatch( Character::isDigit );
   }
   
   
   public static List<String> splitEqually(String text, int size) {
	   List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);

	   for (int start = 0; start < text.length(); start += size) {
		   ret.add(text.substring(start, Math.min(text.length(), start + size)));
	   }
	   return ret;
	}   
   

}
