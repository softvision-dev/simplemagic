package com.j256.simplemagic.pattern.components.operation.criterion.text;

import com.j256.simplemagic.endian.EndianType;
import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.components.operation.criterion.MagicCriterion;
import com.j256.simplemagic.pattern.components.operation.criterion.MagicCriterionResult;
import com.j256.simplemagic.pattern.extractor.MagicExtractor;
import com.j256.simplemagic.pattern.extractor.types.String16Extractor;

/**
 * <b>Represents a two-byte unicode (UCS16) String criterion from a line in magic (5) format.</b>
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
 * A two-byte unicode (UCS16) string [...].
 * </i>
 * </p>
 */
public class String16Criterion extends StringCriterion {

	private final EndianType endianness;

	/**
	 * Creates a new {@link String16Criterion} as found in a {@link MagicPattern}. The criterion shall define one String
	 * evaluation contained by a pattern.
	 * <p>
	 * If the criterion is met for given binary data, this indicates the patterns type assumption to be at least
	 * partially correct. ({@link MagicCriterion#isMatch(byte[], int, boolean)})
	 * </p>
	 *
	 * @param endianness The expected endianness of compared binary data.
	 * @throws MagicPatternException Shall be thrown, if an invalid default operator has been set.
	 */
	public String16Criterion(EndianType endianness) throws MagicPatternException {
		this.endianness = endianness;
	}

	/**
	 * Returns the expected endianness of read binary data.
	 *
	 * @return The expected endianness of read binary data.
	 */
	public EndianType getEndianness() {
		return endianness;
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
	public MagicCriterionResult<String> isMatch(byte[] data, int currentReadOffset, boolean invertEndianness)
			throws MagicPatternException {
		char[] extractedValue = new char[0];
		MagicExtractor<?> extractor = getMagicPattern().getType().getExtractor();
		if (extractor instanceof String16Extractor) {
			extractedValue = ((String16Extractor) extractor).extractValue(data, currentReadOffset, invertEndianness);
		}
		MagicCriterionResult<String> result = super.findOffsetMatch(
				null, extractedValue, 0, extractedValue.length
		);
		if (result.isMatch()) {
			return new MagicCriterionResult<String>(
					this, currentReadOffset + result.getNextReadOffset(), result.getMatchingValue()
			);
		} else {
			return new MagicCriterionResult<String>(this, currentReadOffset);
		}
	}
}
