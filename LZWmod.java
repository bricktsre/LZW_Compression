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
    //private static int buffer =0;
    //private static int blen = 0;
    //private static BufferedReader scan;
    //private static BufferedOutputStream bout;
    private static BinaryIn infile;
    private static BinaryOut outfile;

    public static void compress(boolean reset) throws IOException { 
    	if(reset) outfile.write(1, 1); //BinarayStdOut.write(1,1);
    	else outfile.write(0, 1);		//BinarayStdOut.write(0,1);
    	
    	RTrie<Integer> st = initializeCodebook();
        int code = R+1;  // R is codeword for EOF
        
        StringBuilder input = new StringBuilder();
        input.append(infile.readChar());//BinaryStdIn.readChar());
        while (!infile.isEmpty()) {
            input = nextStringBuilder(input,st);
            StringBuilder s = st.longestPrefixOf(input);
        	
            //write(st.get(s));
        	//BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
            outfile.write(st.get(s),W);
           
            if(code==L && W<16) resizeCodebook();
            if(reset && W==16 && code ==L) {
            	st = initializeCodebook();
            	code=R+1;
            }
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
        boolean reset = infile.readInt(1)==1; //BinarayStdIn.readInt(1);
    	String[] st = new String[65536];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = 	infile.readInt(W);	//BinaryStdIn.readInt(W);
        String val = st[codeword];

        while (true) {
            outfile.write(val);		//BinaryStdOut.write(val);
            if(i==L) resizeCodebook();
            if(reset && W==16 && i ==L) {
            	st = new String[65536];
                for (i = 0; i < R; i++)
                    st[i] = "" + (char) i;
                st[i++] = "";
            }
            codeword = 	infile.readInt(W);	//BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            if (i < L) st[i++] = val + s.charAt(0);
            val = s;
        }
        outfile.close();		//BinaryStdOut.close();
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
    	/*if      (args[0].equals("-")) compress(args[1].equals("r"));
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
        //infile = new BinaryIn("wacky.bmp");
    	//outfile = new BinaryOut("wacky.lzwm");
        //compress(true);
        
       infile = new BinaryIn("wacky.lzwm");
       outfile = new BinaryOut("wacky2.bmp");
        expand();
    }

}
