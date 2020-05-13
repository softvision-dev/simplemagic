package com.j256.simplemagic.pattern.formatter.types;

import com.j256.simplemagic.pattern.formatter.AbstractMagicFormatter;

/**
 * Formatter for basic formatting instructions.
 */
public class DefaultFormatter extends AbstractMagicFormatter {

	/**
	 * Applies basic formatting instructions, such as replacing placeholders with dynamic values.
	 *
	 * @param formatString The formatting instructions of this formatter.
	 */
	public DefaultFormatter(String formatString) {
		super(formatString);
	}
}
