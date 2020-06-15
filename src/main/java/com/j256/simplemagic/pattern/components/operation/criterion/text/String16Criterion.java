package com.j256.simplemagic.pattern.components.operation.criterion.text;

import com.j256.simplemagic.endian.EndianConverter;
import com.j256.simplemagic.endian.EndianConverterFactory;
import com.j256.simplemagic.endian.EndianType;
import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicOperator;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.components.operation.criterion.ExtractedValue;
import com.j256.simplemagic.pattern.components.operation.criterion.MagicCriterion;
import com.j256.simplemagic.pattern.components.operation.criterion.MagicCriterionResult;

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
public class String16Criterion extends AbstractTextCriterion {

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
		super(MagicOperator.EQUALS);
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
	 * Returns the value, that is actually found in the data at the expected position.
	 *
	 * @param data              The binary data, that shall be checked whether they match this criterion.
	 * @param currentReadOffset The initial offset in the given data.
	 * @param length            The value length in bytes. (-1 if no length shall be given.)
	 * @param invertEndianness  Whether the currently determined endianness shall be inverted.
	 * @return The value, that shall match the criterion.
	 */
	@Override
	public ExtractedValue<String> getActualValue(byte[] data, int currentReadOffset, int length, boolean invertEndianness) {
		if (currentReadOffset + length > data.length) {
			return new ExtractedValue<String>(null, currentReadOffset);
		}

		// Read actual value to String.
		EndianConverter endianConverter = EndianConverterFactory.createEndianConverter(
				invertEndianness ? getEndianness().getInvertedEndianType() : getEndianness()
		);
		return new ExtractedValue<String>(endianConverter.convertUTF16String(data, currentReadOffset, length),
				currentReadOffset + length);
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
	@SuppressWarnings("DuplicatedCode")
	@Override
	public MagicCriterionResult<String> isMatch(byte[] data, int currentReadOffset, boolean invertEndianness) {
		// verify the starting offset
		if (currentReadOffset < 0 || (data == null)) {
			return new MagicCriterionResult<String>(false, this, currentReadOffset);
		}

		// Collect actual and expected value.
		String expectedValue = getExpectedValue();
		if (expectedValue == null) {
			return new MagicCriterionResult<String>(false, this, currentReadOffset);
		}
		ExtractedValue<String> actualValue = getActualValue(
				data, currentReadOffset, expectedValue.length() * 2, invertEndianness
		);
		if (actualValue.getValue() == null || actualValue.getValue().length() != expectedValue.length()) {
			return new MagicCriterionResult<String>(false, this, currentReadOffset);
		}

		// Compare expected and actual value.
		for (int index = 0; index < expectedValue.length(); index++) {
			// Collect next actual and expected character.
			char actualCharacter = actualValue.getValue().charAt(index);
			char expectedCharacter = expectedValue.charAt(index);
			boolean lastCharacter = (index == expectedValue.length() - 1);

			// If it does not match return with failure.
			if (!isCharacterMatching(actualCharacter, getOperator(), expectedCharacter,
					lastCharacter, false, false)) {
				return new MagicCriterionResult<String>(false, this, currentReadOffset);
			}
		}

		// When reaching this, everything matches. Collect The matching characters and return the result.
		return new MagicCriterionResult<String>(true, this,
				actualValue.getSuggestedNextReadOffset(), actualValue.getValue());
	}

	/**
	 * Parse the type appended modifiers of this {@link MagicCriterion}.
	 *
	 * @param modifiers The type appended modifiers.
	 */
	@Override
	protected void parseTypeAppendedModifiers(String modifiers) {
		// There are no known modifiers for the String16Criterion.
	}

	/**
	 * Returns the characteristic starting bytes of this {@link MagicCriterion}. Allows for faster selection of
	 * relevant patterns.
	 *
	 * @return An array of the characteristic starting bytes of criterion, or null if such bytes can not be determined.
	 */
	@Override
	public byte[] getStartingBytes() {
		return null;
	}
}
