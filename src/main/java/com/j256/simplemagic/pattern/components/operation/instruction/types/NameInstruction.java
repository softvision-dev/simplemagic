package com.j256.simplemagic.pattern.components.operation.instruction.types;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.components.operation.instruction.AbstractMagicInstruction;
import com.j256.simplemagic.pattern.components.operation.instruction.MagicInstruction;

/**
 * <b>Represents a "name" instruction from a line in magic (5) format.</b>
 * <p>
 * As defined in the Magic(5) Manpage:
 * </p>
 * <i>
 * Define a “named” magic instance that can be called from another use magic entry, like a subroutine call. Named
 * instance direct magic offsets are relative to the offset of the previous matched entry, but indirect offsets are
 * relative to the beginning of the file as usual. Named magic entries always match.
 * </i>
 * </p>
 */
public class NameInstruction extends AbstractMagicInstruction {

	private String name;

	/**
	 * Returns the instruction operand.
	 *
	 * @return The instruction operand, or null if such an operand is not given.
	 */
	@Override
	public String getOperand() {
		return name;
	}

	/**
	 * Parse the given raw definition to initialize this {@link MagicInstruction} instance.
	 *
	 * @param magicPattern  The pattern, this operation is defined for. (For reflective access.) A 'null' value shall
	 *                      be treated as invalid.
	 * @param rawDefinition The raw definition of the {@link MagicInstruction} as a String.
	 */
	@Override
	public void parse(MagicPattern magicPattern, String rawDefinition) throws MagicPatternException {
		if (rawDefinition == null || rawDefinition.trim().length() == 0) {
			throw new MagicPatternException("Name definition is empty.");
		}
		this.name = rawDefinition.trim();
	}
}
