package com.j256.simplemagic.pattern.components.criterion.types.text;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.components.MagicCriterion;
import com.j256.simplemagic.pattern.components.criterion.MagicCriterionResult;

/**
 * Represents a Pascal String criterion from a line in magic (5) format.
 * <p>
 * A Pascal-style string where the first byte is interpreted as the an unsigned length. The string is not '\0'
 * terminated.
 * </p>
 *
 * @author graywatson
 */
public class PascalStringCriterion extends StringCriterion {

	/**
	 * Creates a new {@link PascalStringCriterion} as found in a {@link MagicPattern}. The criterion shall define one
	 * Search evaluation contained by a pattern.
	 * <p>
	 * If the criterion is met for given binary data, this indicates the patterns type assumption to be at least
	 * partially correct. ({@link MagicCriterion#isMatch(byte[], int)})
	 * </p>
	 *
	 * @throws MagicPatternException Shall be thrown, if an invalid default operator has been set.
	 */
	public PascalStringCriterion() throws MagicPatternException {
		super();
	}

	/**
	 * Shall evaluate the {@link MagicCriterion} for the given data and offset and shall return a {@link MagicCriterionResult}
	 * summarizing the evaluation results.
	 *
	 * @param data              The binary data, that shall be checked whether they match this criterion.
	 * @param currentReadOffset The initial offset in the given data.
	 * @return A {@link MagicCriterionResult} summarizing the evaluation results.
	 */
	@Override
	public MagicCriterionResult<String> isMatch(byte[] data, int currentReadOffset) {
		if (currentReadOffset >= data.length) {
			return new MagicCriterionResult<String>(this, currentReadOffset);
		}
		// our maximum position is +1 to move past the length byte and then add in the length
		int terminalOffset = 1 + (data[currentReadOffset] & 0xFF);
		if (terminalOffset > data.length) {
			terminalOffset = data.length;
		}

		// we start matching past the length byte so the starting offset is +1
		return findOffsetMatch(data, null, currentReadOffset + 1, terminalOffset);
	}
}
