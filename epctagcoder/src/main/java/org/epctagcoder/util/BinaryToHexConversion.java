package org.epctagcoder.util;

import java.util.HashMap;

public class BinaryToHexConversion {  
    static final HashMap<String, Character > binaryToHexMap = new HashMap<String, Character >();

    /**
     * load binaryToHexMap values
     */
    static {
         binaryToHexMap.put( "0000", '0');
         binaryToHexMap.put( "0001", '1');          
         binaryToHexMap.put( "0010", '2');
         binaryToHexMap.put( "0011", '3');          
         binaryToHexMap.put( "0100", '4');
         binaryToHexMap.put( "0101", '5');
         binaryToHexMap.put( "0110", '6');
         binaryToHexMap.put( "0111", '7');
         binaryToHexMap.put( "1000", '8');
         binaryToHexMap.put( "1001", '9');
         binaryToHexMap.put( "1010", 'A');
         binaryToHexMap.put( "1011", 'B');
         binaryToHexMap.put( "1100", 'C');
         binaryToHexMap.put( "1101", 'D');
         binaryToHexMap.put( "1110", 'E');
         binaryToHexMap.put( "1111", 'F');     
    }
    
    public static char convert( String binaryString ) {
         Character returnChar = binaryToHexMap.get( binaryString );
         if ( returnChar != null ) {
              return returnChar;
         } else {
              throw new IllegalArgumentException( "Method convert requires 4 character binary String to convert binary to hexadecimal" );
         }
    }
    
    
    
//    public static String convertBinaryToHex(String binInPut) {
//        int chunkLength = binInPut.length() / 4, startIndex = 0, endIndex = 4;
//        String chunkVal = null;
//        for (int i = 0; i < chunkLength; i++) {
//            chunkVal = binInPut.substring(startIndex, endIndex);
//             //System.out.println(Integer.toHexString(Integer.parseInt(chunkVal, 2)));
//            startIndex = endIndex;
//            endIndex = endIndex + 4;
//        }
//
//        return binInPut;
//    }
    
}