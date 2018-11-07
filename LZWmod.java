import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/*************************************************************************
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *
 *************************************************************************/

public class LZWmod {
    private static final int R = 256;        // number of input chars
    private static int L = 512;       // number of codewords = 2^W
    private static int W = 9;         // codeword width

    public static void compress(boolean reset) throws IOException { 
    	if(reset) BinaryStdOut.write(1,1);
    	else BinaryStdOut.write(0,1);
    	
    	RTrie<Integer> st = initializeCodebook();
        int code = R+1;  // R is codeword for EOF
        
        StringBuilder input = new StringBuilder();
        input.append(BinaryStdIn.readChar());
        while (input.length() > 0) {
            input = nextStringBuilder(input,st);
            StringBuilder s = st.longestPrefixOf(input);
        	
            BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
           
            if(code==L && W<16) resizeCodebook();
            if(reset && W==16 && code ==L) {
            	st = initializeCodebook();
            	code=R+1;
            }
            int t = s.length();
            if (t < input.length() && code < L)    // Add s to symbol table.
                st.put(input, code++);
            if(input.length()==s.length()) input.delete(0, input.length()); 
            else input.delete(0, input.length()-1);            // Scan past s in input.
        }
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    } 
    
    private static StringBuilder nextStringBuilder(StringBuilder input, RTrie<Integer> st) {
    	StringBuilder s = input;
        boolean first = true;
    	while(s.length()==input.length()) {
    		if(!first) {	
    			try {
    					input.append(BinaryStdIn.readChar()); //input.append(infile.readChar());
    			}catch(NoSuchElementException e) {
    				return input;
    			}
    		}else
    			first=false;
    		s= st.longestPrefixOf(input);
    	}
    	return input;
    }

    private static void resizeCodebook() {
    	if(W==16) return;
    	else {
    		W++;
    		L=L*2;
    	}
    }
    
    private static RTrie<Integer> initializeCodebook(){
    	RTrie<Integer> st = new RTrie<Integer>();
    	for (int i = 0; i < R; i++)
            st.put(new StringBuilder("" + (char) i), i);
    	return st;
    }
    
    public static void expand() {
        boolean reset = BinaryStdIn.readInt(1)==1;
    	String[] st = new String[65536];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = 	BinaryStdIn.readInt(W);
        String val = st[codeword];

        while (true) {
            BinaryStdOut.write(val);
            if(i==L) resizeCodebook();
            if(reset && W==16 && i ==L) {
            	st = new String[65536];
                for (i = 0; i < R; i++)
                    st[i] = "" + (char) i;
                st[i++] = "";
            }
            codeword = 	BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            if (i < L) st[i++] = val + s.charAt(0);
            val = s;
        }
        BinaryStdOut.close();
    }
    
    public static void main(String[] args) throws IOException {    	
    	if      (args[0].equals("-")) compress(args[1].equals("r"));
        else if (args[0].equals("+")) expand();
        else throw new RuntimeException("Illegal command line argument");
    }

}
