package com.j256.simplemagic.pattern.components.operation.instruction.types;

import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.components.operation.instruction.AbstractMagicInstruction;
import com.j256.simplemagic.pattern.components.operation.instruction.MagicInstruction;

/**
 * <b>Represents an "indirect" instruction from a line in magic (5) format.</b>
 * <p>
 * As defined in the Magic(5) Manpage:
 * </p>
 * <i>
 * Starting at the given offset, consult the magic database again. The offset of the indirect magic is by default
 * absolute in the file, but one can specify /r to indicate that the offset is relative from the beginning of the
 * entry.
 * </i>
 * </p>
 */
public class IndirectInstruction extends AbstractMagicInstruction {
	//TODO implement /r

	private String operand;

	/**
	 * Returns the instruction operand.
	 *
	 * @return The instruction operand, or null if such an operand is not given.
	 */
	@Override
	public String getOperand() {
		return operand;
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
		this.operand = rawDefinition;
	}
}
