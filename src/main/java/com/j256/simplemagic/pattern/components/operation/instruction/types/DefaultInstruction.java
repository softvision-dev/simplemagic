package com.j256.simplemagic.pattern.components.operation.instruction.types;

import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.components.operation.instruction.AbstractMagicInstruction;
import com.j256.simplemagic.pattern.components.operation.instruction.MagicInstruction;

/**
 * <b>Represents a "default" instruction from a line in magic (5) format.</b>
 * <p>
 * As defined in the Magic(5) Manpage:
 * </p>
 * <i>This is intended to be used with the test x (which is always true) and it has no type. It matches when no other
 * test at that continuation level has matched before. Clearing that matched tests for a continuation level, can be
 * done using the clear test.</i>
 * <p>
 * This is behaving equivalent to the "default" case in a switch-case.
 * </p>
 */
public class DefaultInstruction extends AbstractMagicInstruction {

	/**
	 * Returns the instruction operand. (The default instruction does not require additional operands.)
	 *
	 * @return The instruction operand, or null if such an operand is not given.
	 */
	@Override
	public String getOperand() {
		return null;
	}

	/**
	 * Parse the given raw definition to initialize this {@link MagicInstruction} instance.
	 *
	 * @param magicPattern  The pattern, this operation is defined for. (For reflective access.) A 'null' value shall
	 *                      be treated as invalid.
	 * @param rawDefinition The raw definition of the {@link MagicInstruction} as a String.
	 */
	@Override
	public void parse(MagicPattern magicPattern, String rawDefinition) {
		//NO PARSING REQUIRED
	}
}
