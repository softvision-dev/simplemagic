package com.j256.simplemagic.pattern.components.criterion.operator;

import com.j256.simplemagic.pattern.components.criterion.MagicCriterion;

/**
 * Represents an operator for {@link MagicCriterion} types.
 */
public interface Operator {
	/**
	 * Returns the name of this operator, exactly as declared in its
	 * enum declaration.
	 *
	 * @return the name of this enum constant
	 */
	String name();
}
