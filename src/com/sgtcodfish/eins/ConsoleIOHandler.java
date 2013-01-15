package com.sgtcodfish.eins;

import java.util.Scanner;

/**
 * An implementation of {@link EINSIOHandler} for console IO. Prints to stdout for regular output, stderr for errors. Uses Scanner(System.in) for input.
 * Note that error output is not buffered, while regular output is and needs a call to flush().
 * Note that input methods or deDelay WILL CAUSE A FLUSH.
 * Note that calling readString(true) can cause unintended behaviour with console input.
 * @author Ashley Davis (SgtCoDFish)
 */
public class ConsoleIOHandler implements EINSIOHandler {
	StringBuffer outputBuffer = null;
	Scanner scanner = new Scanner(System.in);
	
	public ConsoleIOHandler() {
		outputBuffer = new StringBuffer();
	}
	
	@Override
	public void finalize() {
		scanner.close();
		scanner = null;
	}
	
	@Override
	public void println(String str) {
		print(str + "\n");
	}

	@Override
	public void print(String str) {
		outputBuffer.append(str);
	}

	@Override
	public void printErrorln(String err) {
		System.err.println(err);
	}

	@Override
	public void printError(String err) {
		System.err.print(err);
	}

	@Override
	public void flush() {
		System.out.print(outputBuffer.toString());
		
		outputBuffer = null;
		outputBuffer = new StringBuffer();
	}

	@Override
	public String readString() {
		return readString(false);
	}
	
	@Override
	public String readString(boolean wsOK) {
		flush();
		System.out.flush();
		
		String retVal = new String();
		
		if(wsOK) {
			retVal = scanner.nextLine();
		} else {
			do {
				retVal = scanner.nextLine();
			} while(retVal.compareTo("") == 0);
		}
		
//		System.out.println("INPUT = " + retVal);
		return retVal;
	}

	@Override
	public int readInt() {
		flush();
		System.out.flush();
		
		int retVal = -1;
		while(!scanner.hasNextInt()) {
			scanner.next();
		}
		retVal = scanner.nextInt();
		
//		System.out.println("INPUT = " + retVal);
		return retVal;
	}

	@Override
	public void doDelay(int delay) {
		flush();
		
		int shorterDelay = delay/5;
		
		for(int i = 0; i < 5; i++) {
			try {
				System.out.print(".");
				Thread.sleep(shorterDelay);
			} catch(InterruptedException ie) {}
		}
		
		System.out.println();
	}
}
