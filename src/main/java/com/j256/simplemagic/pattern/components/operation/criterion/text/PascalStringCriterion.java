package com.j256.simplemagic.pattern.components.operation.criterion.text;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.components.operation.criterion.MagicCriterion;
import com.j256.simplemagic.pattern.components.operation.criterion.MagicCriterionResult;

/**
 * <b>Represents a Pascal String criterion from a line in magic (5) format.</b>
 * <p>
 * As defined in the Magic(5) Manpage:
 * </p>
 * <p>
 * <i>
 * The format of the source fragment files that are used to build this database is as follows: Each line of a fragment
 * file specifies a test to be performed. A test compares the data starting at a particular offset in the file with a
 * byte value, a string or a numeric value. If the test succeeds, a message is printed.
 * </i>
 * </p>
 * <p>
 * This criterion represents such a test. This test checks:
 * </p>
 * <p>
 * <i>
 * A Pascal-style string where the first byte/short/int is interpreted as the unsigned length. The length defaults to
 * byte and can be specified as a modifier. The following modifiers are supported:
 * <i>
 * </p>
 * <ul>
 * <li>B - A byte length (default).</li>
 * <li>H - A 2 byte big endian length.</li>
 * <li>h - A 2 byte little endian length.</li>
 * <li>L - A 4 byte big endian length.</li>
 * <li>l - A 4 byte little endian length.</li>
 * <li>J - The length includes itself in its count.</li>
 * </ul>
 * <p>
 * <i>
 * The string is not NUL terminated. “J” is used rather than the more valuable “I” because this type of length is a feature of the JPEG format.
 * </i>
 * </p>
 */
public class PascalStringCriterion extends StringCriterion {

	/**
	 * Creates a new {@link PascalStringCriterion} as found in a {@link MagicPattern}. The criterion shall define one
	 * Search evaluation contained by a pattern.
	 * <p>
	 * If the criterion is met for given binary data, this indicates the patterns type assumption to be at least
	 * partially correct. ({@link MagicCriterion#isMatch(byte[], int, boolean)})
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
	 * @param invertEndianness  True, if the endianness of extracted data shall be inverted for this test.
	 * @return A {@link MagicCriterionResult} summarizing the evaluation results.
	 */
	@Override
	public MagicCriterionResult<String> isMatch(byte[] data, int currentReadOffset, boolean invertEndianness) {
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
