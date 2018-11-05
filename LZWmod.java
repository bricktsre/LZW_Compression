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
    private static final int L = 4096;       // number of codewords = 2^W
    private static final int W = 12;         // codeword width
    //private static int buffer =0;
    //private static int blen = 0;
    //private static BufferedReader scan;
    //private static BufferedOutputStream bout;
    private static BinaryIn infile;
    private static BinaryOut outfile;

    public static void compress() throws IOException { 
        RTrie<Integer> st = new RTrie<Integer>();
        for (int i = 0; i < R; i++)
            st.put(new StringBuilder("" + (char) i), i);
        int code = R+1;  // R is codeword for EOF
        
        StringBuilder input = new StringBuilder();
        input.append(infile.readChar());//BinaryStdIn.readChar());
        while (!infile.isEmpty()) {
            input = nextStringBuilder(input,st);
            StringBuilder s = st.longestPrefixOf(input);
        	//write(st.get(s));
        	//BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
            outfile.write(st.get(s),W);
            int t = s.length();
            if (t < input.length() && code < L)    // Add s to symbol table.
                st.put(input, code++);
            input.delete(0, input.length()-1);            // Scan past s in input.
        }
        outfile.write(R,W);
        outfile.close();
        //BinaryStdOut.write(R, W);
        //BinaryStdOut.close();
    } 
    
    private static StringBuilder nextStringBuilder(StringBuilder input, RTrie<Integer> st) {
    	StringBuilder s = input;
        boolean first = true;
    	while(s.length()==input.length()) {
    		if(!first) {	
    			try {
    				input.append(infile.readChar());	//BinaryStdIn.readChar());
    			}catch(NoSuchElementException e) {
    				return input;
    			}
    		}else
    			first=false;
    		s= st.longestPrefixOf(input);
    	}
    	return input;
    }

    public static void expand() {
        String[] st = new String[L];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = BinaryStdIn.readInt(W);
        String val = st[codeword];

        while (true) {
            BinaryStdOut.write(val);
            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            if (i < L) st[i++] = val + s.charAt(0);
            val = s;
        }
        BinaryStdOut.close();
    }

    /*private static void write(int a) throws IOException {
    	if(blen<W) {
    		buffer |= a;
    		blen+=W;
    	}else {
    		buffer= buffer<< W;
    		buffer |= a;
    		for(int i=0;i<3;i++) {
    			byte temp = (byte) buffer;
    			bout.write(temp);
    			buffer = buffer >>8;
    		}
    		blen=0;
    	}
    }
    
    private static void close() throws IOException {
    	if(blen!=0) {
    		bout.write((byte)buffer);
    		bout.write((byte)buffer>>8);		
    	}
    	bout.close();
    }*/
    
    public static void main(String[] args) throws IOException {
    	/*if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new RuntimeException("Illegal command line argument");
        */
        /*try {
			scan= new BufferedReader(new FileReader("wacky.bmp"));
        	FileOutputStream fout = new FileOutputStream("wacky.lzwm"); 
	        bout = new BufferedOutputStream(fout);     	
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
        infile = new BinaryIn("frosty.jpg");
    	outfile = new BinaryOut("frosty.lzwm");
        compress();
    }

}
