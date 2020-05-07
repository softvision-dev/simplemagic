package com.j256.simplemagic.error;

import com.j256.simplemagic.pattern.MagicPattern;

public class MagicPatternException extends Exception {

	/**
	 * Collects information on parsing failures for {@link MagicPattern} parsing.
	 *
	 * @param details A description of the occurred failure.
	 * @param cause   The cause of the failure.
	 */
	public MagicPatternException(String details, Throwable cause) {
		super(details, cause);
	}

	/**
	 * Collects information on parsing failures for {@link MagicPattern} parsing.
	 *
	 * @param details A description of the occurred failure.
	 */
	public MagicPatternException(String details) {
		this(details, null);
	}
}
