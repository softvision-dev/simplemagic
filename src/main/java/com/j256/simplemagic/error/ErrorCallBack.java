package com.j256.simplemagic.error;

/**
 * Optional call-back which will be made whenever we discover an error while parsing the magic configuration files.
 * There are usually tons of badly formed lines and other errors.
 */
public interface ErrorCallBack {

	/**
	 * An error was generated while processing the line.
	 *
	 * @param line    Line where the error happened.
	 * @param details Specific information about the error.
	 * @param ex      Exception that was thrown trying to parse the line or null if none.
	 */
	void error(String line, String details, Exception ex);
}
