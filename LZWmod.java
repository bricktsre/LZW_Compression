import java.io.IOException;
import java.util.NoSuchElementException;

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
    private static int L = 512;       		 // number of codewords = 2^W
    private static int W = 9;        		 // codeword width

    /**
     * LZW compression with variable length codewords and dictionary resetting
     * 
     * @param reset				Should the codebook be reset upon reaching maximum capacity
     * @throws IOException		BinaryStdIn cannot read in or BinaryStdOut write out
     */
    public static void compress(boolean reset) throws IOException { 
    	if(reset) BinaryStdOut.write(1,1);					//Writes one to the file if the dictionary is to be reset upon filing
    	else BinaryStdOut.write(0,1);						//Writes zero if it is not to be reset
    	
    	RTrie<Integer> st = initializeCodebook();			//Radix Trie storing the codebook
        int code = R+1;  									// R is codeword for EOF	
        
        StringBuilder input = new StringBuilder();
        input.append(BinaryStdIn.readChar());
        while (input.length() > 0) {						//While there still is input left
            input = nextStringBuilder(input,st);			//The next input with a longest prefix one less than it
            StringBuilder s = st.longestPrefixOf(input);
        	
            BinaryStdOut.write(st.get(s), W);      			// Print the prefix's encoding.
           
            if(code==L && W<16) resizeCodebook();			//Resize the codebook if reached maximum codewords for codeword width
            if(reset && W==16 && code ==L) {				//Reset the codebook
            	st = initializeCodebook();
            	code=R+1;
            }
            int t = s.length();
            if (t < input.length() && code < L)    			// Add input to codebook
                st.put(input, code++);
            if(input.length()==s.length()) input.delete(0, input.length()); 	//last input of the file
            else input.delete(0, input.length()-1);            					//Delete all but the last character of the input	
        }
        BinaryStdOut.write(R, W);			//Write EOF signal
        BinaryStdOut.close();
    } 
    
    /**
     * Returns the next stringbuilder with a longest prefix one less than it
     * 
     * @param input		current input from the file
     * @param st		codebook
     * @return			stringbuilder
     */
    private static StringBuilder nextStringBuilder(StringBuilder input, RTrie<Integer> st) {
    	StringBuilder s = input;
        boolean first = true;
    	while(s.length()==input.length()) {
    		if(!first) {											//First time around do not add a character
    			try {
    					input.append(BinaryStdIn.readChar()); 		//Read in another character
    			}catch(NoSuchElementException e) {
    				return input;									//EOF
    			}
    		}else
    			first=false;
    		s= st.longestPrefixOf(input);
    	}
    	return input;
    }

    /**
     * Increases the width of the codewords by one
     * Doubles the number of available codewords
     */
    private static void resizeCodebook() {
    	if(W==16) return;
    	else {
    		W++;
    		L=L*2;
    	}
    }
    
    /**
     * Initializes a RadixTrie with extended ASCII
     * 
     * @return	the nex RadixTrie
     */
    private static RTrie<Integer> initializeCodebook(){
    	RTrie<Integer> st = new RTrie<Integer>();
    	for (int i = 0; i < R; i++)
            st.put(new StringBuilder("" + (char) i), i);
    	return st;
    }
    
    /**
     * Decompresses the file
     */
    public static void expand() {
        boolean reset = BinaryStdIn.readInt(1)==1;		//Check if codebook should be reset or not
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
            if(i==L) resizeCodebook();		//Resize codebook
            if(reset && W==16 && i ==L) {	//Reset codebook
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
