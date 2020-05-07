package com.j256.simplemagic.pattern.components.criterion.operator;

import com.j256.simplemagic.pattern.components.criterion.MagicCriterion;

/**
 * Represents an operator for Number based {@link MagicCriterion} types.
 */
public enum NumericOperator implements Operator {

	EQUALS('='),
	NOT_EQUALS('!'),
	GREATER_THAN('>'),
	LESS_THAN('<'),
	AND_ALL_SET('&'),
	AND_ALL_CLEARED('^'),
	NEGATE('~');

	private final char operator;

	/**
	 * Instantiates an enum {@link NumericOperator} value.
	 *
	 * @param operator The magic pattern name for this {@link NumericOperator}.
	 */
	NumericOperator(char operator) {
		this.operator = operator;
	}

	/**
	 * Returns the operator if the first character is an operator.
	 *
	 * @param name The name, that shall be checked.
	 * @return A matching operator or null.
	 */
	public static NumericOperator forOperator(char name) {
		for (NumericOperator operator : values()) {
			if (operator.operator == name) {
				return operator;
			}
		}
		return null;
	}
}
