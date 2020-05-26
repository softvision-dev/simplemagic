package com.j256.simplemagic.pattern.components.operation.instruction;

import com.j256.simplemagic.pattern.components.MagicOperation;

/**
 * <b>Represents an instruction from a line in magic (5) format.</b>
 * <p>
 * Additionally to testable criteria the magic (5) type defines instructions like "name" or "use". Such patterns
 * are not testable criteria but rather instructions for the evaluating component to: define/reference a named magic
 * pattern, default to a specific value, in case none of the other criteria match, etc.
 * </p>
 * <p>
 * Such "instructions" shall implement this interface.
 * </p>
 */
public interface MagicInstruction extends MagicOperation {

	/**
	 * Returns the instruction operand.
	 *
	 * @return The instruction operand, or null if such an operand is not given.
	 */
	String getOperand();
}
