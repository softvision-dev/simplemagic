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
import com.j256.simplemagic.pattern.components.operation.criterion.modifiers.TextCriterionModifiers;

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
 * <li>h - A 2 byte little endian length.</li>
 * <li>H - A 2 byte big endian length.</li>
 * <li>l - A 4 byte little endian length.</li>
 * <li>L - A 4 byte big endian length.</li>
 * <li>J - The length includes itself in its count.</li>
 * </ul>
 * <p>
 * <i>
 * The string is not NUL terminated. “J” is used rather than the more valuable “I” because this type of length is a feature of the JPEG format.
 * </i>
 * </p>
 */
public class PascalStringCriterion extends AbstractTextCriterion {

	private int lengthValueByteCount = 1;
	private EndianType endianType = EndianType.NATIVE;
	private boolean lengthBytesCounted = false;

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
		super(MagicOperator.EQUALS);
	}

	/**
	 * Return the number of bytes indicating the length of the expected string.
	 *
	 * @return The number of bytes indicating the length of the expected string.
	 */
	public int getLengthValueByteCount() {
		return lengthValueByteCount;
	}

	/**
	 * Returns the endianness of the expected String.
	 *
	 * @return The endianness of the expected String.
	 */
	public EndianType getEndianness() {
		return endianType;
	}

	/**
	 * Returns true, if the length bytes are included in the string length.
	 *
	 * @return True if the length bytes are included in the string length.
	 */
	public boolean isLengthBytesCounted() {
		return lengthBytesCounted;
	}

	/**
	 * Returns the value, that is actually found in the data at the expected position. For Pascal Strings the length
	 * and offset shall be given after resolving the pascal length bytes. The length shall determine the pure value
	 * length and the offset shall point to the terminal offset of the length bytes. May not return null directly,
	 * wrap 'null' value using {@link ExtractedValue} instead.
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
		return new ExtractedValue<String>(endianConverter.convertUTF8String(data, currentReadOffset, length),
				currentReadOffset + length);
	}

	/**
	 * Returns the actual pure value length as encoded in a pascal String's length bytes.
	 *
	 * @param data              The binary data, that shall be checked whether they match this criterion.
	 * @param currentReadOffset The initial offset in the given data.
	 * @param invertEndianness  Whether the currently determined endianness shall be inverted.
	 * @return The length of the value following the pascal String length bytes.
	 */
	public int getActualValueByteLength(byte[] data, int currentReadOffset, boolean invertEndianness) {
		EndianConverter endianConverter = EndianConverterFactory.createEndianConverter(
				invertEndianness ? getEndianness().getInvertedEndianType() : getEndianness()
		);

		// Read length bytes to number.
		int readLength = endianConverter.convertNumber(data, currentReadOffset, getLengthValueByteCount()).intValue();
		return isLengthBytesCounted() ? readLength - getLengthValueByteCount() : readLength;
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
		if (currentReadOffset >= data.length) {
			return new MagicCriterionResult<String>(false, this, currentReadOffset);
		}

		// Collect actual and expected value.
		int actualValueByteLength = getActualValueByteLength(data, currentReadOffset, invertEndianness);
		int lengthValueTerminalOffset = currentReadOffset + getLengthValueByteCount();
		ExtractedValue<String> actualValue = getActualValue(
				data, lengthValueTerminalOffset, actualValueByteLength, invertEndianness
		);
		String expectedValue = getExpectedValue();
		if (actualValue.getValue() == null || expectedValue == null ||
				actualValue.getValue().length() != expectedValue.length()) {
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
		TextCriterionModifiers textFlagsAndModifiers = new TextCriterionModifiers(modifiers);
		if (textFlagsAndModifiers.isEmpty()) {
			return;
		}

		// A Pascal-style string where the first byte/short/int is interpreted as the unsigned length. The length
		// defaults to byte and can be specified as a modifier. The following modifiers are supported:
		for (char ch : textFlagsAndModifiers.getCharacterModifiers()) {
			switch (ch) {
				// B - A byte length (default).
				case 'B':
					this.endianType = EndianType.NATIVE;
					this.lengthValueByteCount = 1;
					break;
				// A 2 byte little endian length.
				case 'h':
					this.endianType = EndianType.LITTLE;
					this.lengthValueByteCount = 2;
					break;
				// A 2 byte big endian length.
				case 'H':
					this.endianType = EndianType.BIG;
					this.lengthValueByteCount = 2;
					break;
				// A 4 byte little endian length.
				case 'l':
					this.endianType = EndianType.LITTLE;
					this.lengthValueByteCount = 4;
					break;
				// A 4 byte big endian length.
				case 'L':
					this.endianType = EndianType.BIG;
					this.lengthValueByteCount = 4;
					break;
				// The length includes itself in its count.
				case 'J':
					this.lengthBytesCounted = true;
					break;
			}
		}
	}

	/**
	 * Returns the characteristic starting bytes of this {@link MagicCriterion}. Allows for faster selection of
	 * relevant patterns.
	 *
	 * @return An array of the characteristic starting bytes of criterion, or null if such bytes can not be determined.
	 */
	@Override
	public byte[] getStartingBytes() {
		return new byte[0];
	}
}
