package com.j256.simplemagic.pattern.components.criterion.types.text;

import com.j256.simplemagic.endian.EndianType;
import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.components.MagicCriterion;
import com.j256.simplemagic.pattern.components.criterion.MagicCriterionResult;
import com.j256.simplemagic.pattern.extractor.MagicExtractor;
import com.j256.simplemagic.pattern.extractor.types.String16Extractor;

/**
 * Represents a two-byte unicode (UCS16) String criterion from a line in magic (5) format.
 *
 * @author graywatson
 */
public class String16Criterion extends StringCriterion {

	private final EndianType endianness;

	/**
	 * Creates a new {@link String16Criterion} as found in a {@link MagicPattern}. The criterion shall define one String
	 * evaluation contained by a pattern.
	 * <p>
	 * If the criterion is met for given binary data, this indicates the patterns type assumption to be at least
	 * partially correct. ({@link MagicCriterion#isMatch(byte[], int)})
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
	 * @return A {@link MagicCriterionResult} summarizing the evaluation results.
	 * @throws MagicPatternException Shall be thrown, if the evaluation failed (possibly due to a malformed criterion.)
	 */
	@Override
	public MagicCriterionResult<String> isMatch(byte[] data, int currentReadOffset) throws MagicPatternException {
		char[] extractedValue = new char[0];
		MagicExtractor<?> extractor = getMagicPattern().getType().getExtractor();
		if (extractor instanceof String16Extractor) {
			extractedValue = ((String16Extractor) extractor).extractValue(data, currentReadOffset);
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
