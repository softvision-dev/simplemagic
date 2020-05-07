package com.j256.simplemagic.pattern.components.criterion.operator;

import com.j256.simplemagic.pattern.components.criterion.MagicCriterion;

/**
 * Represents an operator for String based {@link MagicCriterion} types.
 */
public enum StringOperator implements Operator {
	EQUALS('='),
	NOT_EQUALS('!'),
	GREATER_THAN('>'),
	LESS_THAN('<');

	private final char operator;

	/**
	 * Instantiates an enum {@link StringOperator} value.
	 *
	 * @param operator The magic pattern name for this {@link StringOperator}.
	 */
	StringOperator(char operator) {
		this.operator = operator;
	}

	/**
	 * Returns the operator if the first character is an operator.
	 *
	 * @param name The name, that shall be checked.
	 * @return A matching operator or null.
	 */
	public static StringOperator forOperator(char name) {
		for (StringOperator operator : values()) {
			if (operator.operator == name) {
				return operator;
			}
		}
		return null;
	}
}
