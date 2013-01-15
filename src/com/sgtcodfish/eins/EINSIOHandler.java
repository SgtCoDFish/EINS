package com.sgtcodfish.eins;

/**
 * Used to define a method of getting input/showing output from the game. Can be any kind of method of IO as long as display is instantaneous after calling flush.
 * @author Ashley Davis (SgtCoDFish)
 *
 */
public interface EINSIOHandler {
	
	/**
	 * Print a string str and append a newline to the end.
	 * @param str The string to print.
	 */
	public void println(String str);
	
	/**
	 * Print a string str and append nothing.
	 * @param str The string to print.
	 */
	public void print(String str);
	
	/**
	 * Print an error message and append a newline.
	 * @param err The error message to print.
	 */
	public void printErrorln(String err);
	
	/**
	 * Print an error message.
	 * @param err The error message to print.
	 */
	public void printError(String err);
	
	/**
	 * Force all output to occur when this function is called.
	 */
	public void flush();
	
	/**
	 * Get a string input from the user, assuming that inputting pure whitespace is not allowed. Same as calling readString(false).
	 * @return The string the user.
	 */
	public String readString();
	
	/**
	 * Get a string input from the user.
	 * @return wsOK If true, whitespace input (such as '\n') is OK and accepted as valid input, otherwise it is skipped over.
	 */
	public String readString(boolean wsOK);
	
	/**
	 * Get an integer from the user.
	 * @return An integer inputted by the user.
	 */
	public int readInt();
	
	/**
	 * Flush the output then delay for delay milliseconds. This could involve showing an hourglass, printing dots, etc.
	 * @param delay The time, in milliseconds, to delay, total.
	 */
	public void doDelay(int delay);
}
