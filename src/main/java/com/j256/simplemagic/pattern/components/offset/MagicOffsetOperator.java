package com.j256.simplemagic.pattern.components.offset;

/**
 * <b>This enumeration lists all modification operation types for an offset in magic (5) format.</b>
 * <p>
 * As defined in the Magic(5) Manpage:
 * </p>
 * <p>
 * <i>
 * [...] If this indirect offset cannot be used directly, simple calculations are possible: appending [+-*%/&|^] number
 * inside parentheses allows one to modify the value read from the file before it is used as an offset:
 * </i>
 * </p>
 */
public enum MagicOffsetOperator {

	ADD("+"),
	SUBTRACT("-"),
	MULTIPLY("*"),
	DIVIDE("/"),
	MODULO("%"),
	AND("&"),
	OR("|"),
	XOR("^");

	private final String name;

	/**
	 * Instantiates an enum {@link MagicOffsetOperator} value.
	 *
	 * @param name The magic pattern name for this {@link MagicOffsetOperator}.
	 */
	MagicOffsetOperator(String name) {
		this.name = name;
	}

	/**
	 * Returns the magic pattern name for this {@link MagicOffsetOperator}.
	 *
	 * @return The magic pattern name for this {@link MagicOffsetOperator}.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Determines a matching {@link MagicOffsetOperator} for the given magic pattern operator name.
	 *
	 * @param name The operator name a {@link MagicOffsetOperator} shall be found for.
	 * @return The matching {@link MagicOffsetOperator} or null, if such an operator can not be found.
	 */
	public static MagicOffsetOperator forName(String name) {
		for (MagicOffsetOperator modifier : values()) {
			if (modifier.getName().equals(name)) {
				return modifier;
			}
		}

		return null;
	}
}
