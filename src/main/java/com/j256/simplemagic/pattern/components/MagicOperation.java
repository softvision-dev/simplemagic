package com.j256.simplemagic.pattern.components;

import com.j256.simplemagic.MagicEntries;
import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.components.operation.OperationType;
import com.j256.simplemagic.pattern.components.operation.criterion.MagicCriterion;
import com.j256.simplemagic.pattern.components.operation.instruction.MagicInstruction;

/**
 * <b>An implementing class represents a test definition or instruction from a line in magic (5) format.</b>
 * <p>
 * An implementing class either represents a {@link MagicCriterion}, that is capable of evaluating values from
 * data for a specific {@link OperationType} and therefore provides one evaluation result, that influences the
 * outcome of {@link MagicPattern#isMatch(byte[], int, MagicEntries)}.
 * </p>
 * <p>
 * Or it represents a {@link MagicInstruction}, that is defining a more complex instruction, such as the creation or
 * indirect call of a named pattern.
 * </p>
 * <p>
 * Both types shall be summarized as: {@link MagicOperation}
 * </p>
 */
public interface MagicOperation {

	/**
	 * Returns true, if the current operation does not define a test criterion and instead shall be skipped during
	 * the evaluation. (This represents the criterion: 'x')
	 *
	 * @return True, if this operation is not defining a testable criterion.
	 */
	boolean isNoopCriterion();

	/**
	 * Parse the given raw definition to initialize this {@link MagicOperation} instance.
	 *
	 * @param magicPattern  The pattern, this operation is defined for. (For reflective access.) A 'null' value shall
	 *                      be treated as invalid.
	 * @param rawDefinition The raw definition of the {@link MagicOperation} as a String.
	 * @throws MagicPatternException Shall be thrown, if the parsing failed.
	 */
	void parse(MagicPattern magicPattern, String rawDefinition) throws MagicPatternException;

	/**
	 * Returns the characteristic starting bytes of this {@link MagicOperation}. Allows for faster selection of
	 * relevant patterns.
	 *
	 * @return An array of the characteristic starting bytes of this operation, or null if such bytes can not be determined.
	 * @throws MagicPatternException Shall be thrown, when accessing pattern information failed.
	 */
	byte[] getStartingBytes() throws MagicPatternException;
}
