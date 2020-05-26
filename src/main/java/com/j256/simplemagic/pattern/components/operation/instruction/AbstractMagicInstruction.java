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
public abstract class AbstractMagicInstruction implements MagicInstruction {

	/**
	 * Returns true, if the current operation does not define a test criterion and instead shall be skipped during
	 * the evaluation. This shall always return true for instructions, as none of them are testable criteria.
	 *
	 * @return True, if this operation is not defining a testable criterion.
	 */
	@Override
	public boolean isNoopCriterion() {
		return true;
	}

	/**
	 * Returns the characteristic starting bytes of this {@link MagicOperation}. Allows for faster selection of
	 * relevant patterns. This will always evaluate to 'null' for instructions.
	 *
	 * @return An array of the characteristic starting bytes of this operation, or null if such bytes can not be determined.
	 */
	@Override
	public byte[] getStartingBytes() {
		return null;
	}
}
