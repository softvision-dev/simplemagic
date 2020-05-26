package com.j256.simplemagic.pattern.components.operation.instruction.types;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.components.operation.instruction.AbstractMagicInstruction;
import com.j256.simplemagic.pattern.components.operation.instruction.MagicInstruction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>Represents a "use" instruction from a line in magic (5) format.</b>
 * <p>
 * As defined in the Magic(5) Manpage:
 * </p>
 * <i>
 * Recursively call the named magic starting from the current offset. If the name of the referenced begins with a
 * ^ then the endianness of the magic is switched; if the magic mentioned leshort for example, it is treated as
 * beshort and vice versa. This is useful to avoid duplicating the rules for different endianness.
 * </i>
 * </p>
 */
public class UseInstruction extends AbstractMagicInstruction {

	private static final Pattern USE_INSTRUCTION = Pattern.compile("(^)?(.*+)");

	private String name = "";
	private boolean invertEndianness = false;

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
	 * Returns true, if the endianness of referenced patterns shall be inverted.
	 *
	 * @return True, if the endianness of referenced patterns shall be inverted.
	 */
	public boolean isInvertEndianness() {
		return invertEndianness;
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
		Matcher matcher = USE_INSTRUCTION.matcher(rawDefinition);
		if (!matcher.matches()) {
			throw new MagicPatternException(String.format("Unknown use instruction pattern: %s", rawDefinition));
		}
		this.invertEndianness = matcher.group(1) != null;
		this.name = matcher.group(2);
		if (this.name == null) {
			throw new MagicPatternException(String.format("Empty referenced name in use instruction: %s", rawDefinition));
		}
	}
}
