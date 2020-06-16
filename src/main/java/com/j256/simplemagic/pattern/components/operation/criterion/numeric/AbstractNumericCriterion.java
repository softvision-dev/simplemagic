package com.j256.simplemagic.pattern.components.operation.criterion.numeric;

import com.j256.simplemagic.endian.EndianConverterFactory;
import com.j256.simplemagic.endian.EndianType;
import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.MagicOperator;
import com.j256.simplemagic.pattern.PatternUtils;
import com.j256.simplemagic.pattern.components.operation.criterion.AbstractMagicCriterion;
import com.j256.simplemagic.pattern.components.operation.criterion.ExtractedValue;
import com.j256.simplemagic.pattern.components.operation.criterion.MagicCriterion;
import com.j256.simplemagic.pattern.components.operation.criterion.MagicCriterionResult;
import com.j256.simplemagic.pattern.components.operation.criterion.text.AbstractTextCriterion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>Represents a numeric criterion from a line in magic (5) format.</b>
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
 * <i>
 * Each top-level magic pattern (see below for an explanation of levels) is classified as text or binary according to
 * the types used. Types “regex” and “search” are classified as text tests, unless non-printable characters are used
 * in the pattern. All other tests are classified as binary. A top-level pattern is considered to be a test text when
 * all its patterns are text patterns; otherwise, it is considered to be a binary pattern. When matching a file, binary
 * patterns are tried first; if no match is found, and the file looks like text, then its encoding is determined and the
 * text patterns are tried.
 * </i>
 * </p>
 * <p>
 * <i>
 * The numeric types may optionally be followed by & and a numeric value, to specify that the value is to be AND'ed with
 * the numeric value before any comparisons are done. Prepending a u to the type indicates that ordered comparisons
 * should be unsigned.
 * </i>
 * </p>
 * <p>
 * <i>
 * Numeric values may be preceded by a character indicating the operation to be performed. It may be =, to specify that
 * the value from the file must equal the specified value, <, to specify that the value from the file must be less than
 * the specified value, >, to specify that the value from the file must be greater than the specified value, &, to
 * specify that the value from the file must have set all of the bits that are set in the specified value, ^, to
 * specify that the value from the file must have clear any of the bits that are set in the specified value, or ~,
 * the value specified after is negated before tested. x, to specify that any value will match. If the character is
 * omitted, it is assumed to be =. Operators &, ^, and ~ don't work with floats and doubles. The operator ! specifies
 * that the line matches if the test does not succeed.
 * </i>
 * </p>
 * <p>
 * <i>
 * Numeric values are specified in C form; e.g. 13 is decimal, 013 is octal, and 0x13 is hexadecimal.
 * </i>
 * </p>
 * <p>
 * <i>
 * Numeric operations are not performed on date types, instead the numeric value is interpreted as an offset.
 * </i>
 * </p>
 * <p>
 * <i>
 * Dates are treated as numerical values in the respective internal representation.
 * </i>
 * </p>
 * <p>
 * Attention: The hereby defined "binary" test types shall be named "numeric" criteria for the purposes of this library
 * ({@link AbstractNumericCriterion}), additionally String, PascalString and String16 criteria are also counted as "text"
 * based criteria. ({@link AbstractTextCriterion})
 * </p>
 * <p>
 * When later on "binary" criteria shall be executed first to optimize performance - as suggested by the manpage - then
 * this shall be represented by a boolean field: "binaryCriterion".
 * </p>
 */
public abstract class AbstractNumericCriterion extends AbstractMagicCriterion<Number> {

	public static final MagicOperator[] NUMERIC_OPERATORS = new MagicOperator[]{
			MagicOperator.EQUALS, MagicOperator.NOT_EQUALS, MagicOperator.GREATER_THAN, MagicOperator.LESS_THAN,
			MagicOperator.CONJUNCTION, MagicOperator.CONTRAVALENCE, MagicOperator.COMPLEMENT
	};
	// TODO: The "subtract" modifier can be found in magic patterns, but is not documented in the magic(5) manpage,
	// it may be part of the file(1) "data arithmetic" extension?
	public static final MagicOperator[] TYPE_MODIFIERS = new MagicOperator[]{
			MagicOperator.CONJUNCTION, MagicOperator.SUBTRACT
	};
	private static final Pattern MODIFIER_PATTERN = Pattern.compile("^([&-])(?:(-?[0-9a-fA-FxX]+)|(-?\\d+))?$");

	private final EndianType endianness;
	private Number expectedValue;
	private MagicOperator modificationOperator;
	private Number modificationOperand;

	/**
	 * Creates a new {@link AbstractNumericCriterion} as found in a {@link MagicPattern}. The criterion shall define one
	 * numeric evaluation contained by a pattern.
	 * <p>
	 * If the criterion is met for given binary data, this indicates the patterns type assumption to be at least
	 * partially correct. ({@link MagicCriterion#isMatch(byte[], int, boolean)})
	 * </p>
	 *
	 * @param endianness The expected endianness of compared binary data.
	 * @throws MagicPatternException Shall be thrown, if an invalid default operator has been set.
	 */
	public AbstractNumericCriterion(EndianType endianness) throws MagicPatternException {
		super(MagicOperator.EQUALS);
		if (endianness == null) {
			throw new MagicPatternException("Invalid criterion initialization.");
		}
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
	 * Returns a numeric value, that must be found in binary data to match this criterion.
	 *
	 * @return The numeric value, that must be matched.
	 */
	@Override
	public Number getExpectedValue() {
		return expectedValue;
	}

	/**
	 * Returns the optional modification operator, that must be applied (if present) to the extracted value, before
	 * evaluating this criterion.
	 *
	 * @return The current modification operator, or null if such an operator has not been set.
	 */
	public MagicOperator getModificationOperator() {
		return modificationOperator;
	}

	/**
	 * Returns the optional modification operand, that must be applied (if present) to the extracted value, before
	 * evaluating this criterion.
	 *
	 * @return The current modification operand, or null if such an operator has not been set.
	 */
	public Number getModificationOperand() {
		return modificationOperand;
	}

	/**
	 * Shall evaluate the {@link MagicCriterion} for the given data and offset and shall return a {@link MagicCriterionResult}
	 * summarizing the evaluation results.
	 *
	 * @param data              The binary data, that shall be checked whether they match this criterion.
	 * @param currentReadOffset The initial offset in the given data.
	 * @param invertEndianness  Whether the currently determined endianness shall be inverted.
	 * @return A {@link MagicCriterionResult} summarizing the evaluation results.
	 * @throws MagicPatternException Shall be thrown, if the evaluation failed (possibly due to a malformed criterion.)
	 */
	@Override
	public MagicCriterionResult<Number> isMatch(byte[] data, int currentReadOffset, boolean invertEndianness)
			throws MagicPatternException {
		// Extract the numeric value from data.
		ExtractedValue<Number> actualValue = getActualValue(
				data, currentReadOffset, getMagicPattern().getType().getByteLength(), invertEndianness
		);
		if (actualValue.getValue() == null) {
			return new MagicCriterionResult<Number>(false, this, currentReadOffset);
		}

		Number actual = actualValue.getValue();
		// Apply numeric modifiers.
		if(modificationOperator != null && getModificationOperand() != null) {
			actual = applyModifier(
					actualValue.getValue(), getModificationOperator(), getModificationOperand()
			);
			if (actual == null) {
				return new MagicCriterionResult<Number>(false, this, currentReadOffset);
			}
		}

		// Test the value for a match.
		if (evaluate(actual, getOperator(), getExpectedValue())) {
			return new MagicCriterionResult<Number>(true, this,
					actualValue.getSuggestedNextReadOffset(), actual);
		} else {
			return new MagicCriterionResult<Number>(false, this, currentReadOffset);
		}
	}

	/**
	 * Applies the found type appended modifier to an extracted value, using the given operator.
	 * <p>
	 * According to the Magic(5) Manpage:
	 * </p>
	 *
	 * @param extractedValue The first operand of the current modification operation.
	 * @param operator       The operator of the current modification operation.
	 * @param modifier       The second operand of the current modification operation.
	 * @return The resulting modified {@link Number}.
	 * @throws MagicPatternException Shall be thrown, if adapting the modifier failed. Always fail for unknown/failed
	 *                               modification operations, instead of risking to evaluate erroneous values,
	 *                               or worse : To not report definition gaps/syntax errors.
	 */
	protected abstract Number applyModifier(Number extractedValue, MagicOperator operator, Number modifier)
			throws MagicPatternException;

	/**
	 * Evaluates the current {@link AbstractNumericCriterion}, comparing the current test value to the given extracted
	 * value, using the given operator.
	 *
	 * @param extractedValue The first operand of the current comparison operation.
	 * @param operator       The operator of the current comparison operation.
	 * @param testValue      The second operand of the current comparison operation.
	 * @return True, if the comparison operation is matching.
	 * @throws MagicPatternException Shall be thrown, if the evaluation failed or the operator is unknown. Always Fail
	 *                               for unknown/failed evaluation operations, instead of risking to create false
	 *                               positives, or worse : To not report definition gaps/syntax errors.
	 */
	protected abstract boolean evaluate(Number extractedValue, MagicOperator operator, Number testValue)
			throws MagicPatternException;

	// TODO: Check whether this does already support HEX and octal number formats.
	/**
	 * Parses the test value of a {@link AbstractNumericCriterion} to the according numeric type {@link Number}.
	 * Override in extending classes to return a more specific numeric type.
	 *
	 * @param valueString The value, that shall be parsed.
	 * @return A typed numeric value, according to {@link AbstractNumericCriterion}.
	 * @throws MagicPatternException Shall be thrown, if the test value could not be parsed.
	 */
	protected Number decodeValueString(String valueString) throws MagicPatternException {
		return PatternUtils.parseNumericValue(valueString);
	}

	/**
	 * Parse the given raw definition to initialize this {@link MagicCriterion} instance.
	 *
	 * @param magicPattern  The pattern, this criterion is defined for. (For reflective access.) A 'null' value will
	 *                      be treated as invalid.
	 * @param rawDefinition The raw definition of the {@link MagicCriterion} as a String.
	 * @throws MagicPatternException Shall be thrown, if the parsing failed.
	 */
	@Override
	public void doParse(MagicPattern magicPattern, String rawDefinition) throws MagicPatternException {
		if (rawDefinition == null || rawDefinition.isEmpty()) {
			throw new MagicPatternException("Criterion definition is empty.");
		}

		// Parse type appended numeric modifier (if any).
		parseTypeAppendedModifiers(getMagicPattern().getType().getModifiers());

		// Parse operator and test value.
		MagicOperator operator = MagicOperator.forOperator(rawDefinition.charAt(0), NUMERIC_OPERATORS);
		setOperator(operator);
		this.expectedValue = decodeValueString(operator != null ? rawDefinition.substring(1) : rawDefinition);
	}

	/* TODO: For Magic(5) the type appended modifier is a simple & mask, but file(1) seems to implement further
	 * modifiers, such as found in the "macintosh" magic file from the file(1) "Magdir":
	 * >0x402 beldate-0x7C25B080 x created: %s
	 * An author of the "macintosh" magic file explains:
	 * # From "Tom N Harris" <telliamed@mac.com>
	 * # Fixed HFS+ and Partition map magic: Ethan Benson <erbenson@alaska.net>
	 * # The MacOS epoch begins on 1 Jan 1904 instead of 1 Jan 1970, so these
	 * # entries depend on the data arithmetic added after v.35
	 * When assuming, he talks about file(1) v.35 here, then said "data arithmetic" could not be found publicly
	 * documented yet.
	 * It is assumed, that we require an implementation for arithmetic modifiers here, but currently we can only ignore
	 * those patterns and will refuse their evaluation with an Exception.
	 */

	/**
	 * Parse the type appended modifiers of this {@link MagicCriterion}.
	 *
	 * @param modifiers The type appended modifiers.
	 * @throws MagicPatternException Shall be thrown if an error occurred during evaluation.
	 */
	@Override
	protected void parseTypeAppendedModifiers(String modifiers) throws MagicPatternException {
		String trimmed;
		// When there are no flags or modifiers, there is nothing to do here. Flags and modifiers must not be set.
		if (modifiers == null || (trimmed = modifiers.trim()).isEmpty()) {
			return;
		}

		// When there are flags or modifiers set, then they must be known, else we risk to not evaluate the criterion
		// correctly.
		Matcher matcher = MODIFIER_PATTERN.matcher(trimmed);
		if (!matcher.matches()) {
			throw new MagicPatternException(String.format("Unknown modifier: %s", modifiers));
		}

		// An operator defines the type of modification operation - it must be present - no defaults can be assumed here!
		this.modificationOperator = MagicOperator.forPattern(matcher.group(1), TYPE_MODIFIERS);
		if (this.modificationOperator == null) {
			throw new MagicPatternException(String.format("Unknown modification operator: %s", modifiers));
		}

		// Find modification operand - we strictly require a valid operand for comparison operations.
		if (matcher.group(2) == null && matcher.group(3) == null) {
			throw new MagicPatternException(String.format("Empty/unknown modification operand: %s", modifiers));
		} else {
			this.modificationOperand = matcher.group(2) != null ?
					decodeValueString(matcher.group(2)) : decodeValueString(matcher.group(3));
		}
	}

	/**
	 * Returns the characteristic starting bytes of this {@link MagicCriterion}. Allows for faster selection of
	 * relevant patterns.
	 *
	 * @return An array of the characteristic starting bytes of criterion, or null if such bytes can not be determined.
	 * @throws MagicPatternException Shall be thrown, when accessing pattern information failed.
	 */
	@Override
	public byte[] getStartingBytes() throws MagicPatternException {
		return isNoopCriterion() ? null :
				EndianConverterFactory.createEndianConverter(getEndianness()).convertToByteArray(
						getExpectedValue().longValue(), getMagicPattern().getType().getByteLength()
				);
	}
}
