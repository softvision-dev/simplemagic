package com.j256.simplemagic.pattern.components.criterion.types.numeric;

import com.j256.simplemagic.endian.EndianConverterFactory;
import com.j256.simplemagic.endian.EndianType;
import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.MagicOperator;
import com.j256.simplemagic.pattern.PatternUtils;
import com.j256.simplemagic.pattern.components.MagicType;
import com.j256.simplemagic.pattern.components.criterion.AbstractMagicCriterion;
import com.j256.simplemagic.pattern.components.MagicCriterion;
import com.j256.simplemagic.pattern.components.criterion.MagicCriterionResult;
import com.j256.simplemagic.pattern.extractor.MagicExtractor;
import com.j256.simplemagic.pattern.extractor.types.NumberExtractor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a numeric criterion from a line in magic (5) format.
 *
 * @author graywatson
 */
public abstract class AbstractNumberCriterion extends AbstractMagicCriterion<Number> {

	public static final MagicOperator[] NUMERIC_OPERATORS = new MagicOperator[]{
			MagicOperator.EQUALS, MagicOperator.NOT_EQUALS, MagicOperator.GREATER_THAN, MagicOperator.LESS_THAN,
			MagicOperator.AND, MagicOperator.XOR, MagicOperator.COMPLEMENT
	};
	public static final MagicOperator[] TYPE_MODIFIERS = new MagicOperator[]{
			MagicOperator.AND, MagicOperator.OR,MagicOperator.XOR, MagicOperator.COMPLEMENT, MagicOperator.ADD,
			MagicOperator.SUBTRACT,MagicOperator.MULTIPLY, MagicOperator.DIVIDE, MagicOperator.MODULO
	};
	private static final Pattern MODIFIER_PATTERN = Pattern.compile("^([~&|^+\\-*%/])(?:(-?[0-9a-fA-FxX]+)|(-?\\d+))?$");

	private final EndianType endianness;
	private Number testValue;
	private MagicOperator modificatonOperator;
	private Number modificationOperand;

	/**
	 * Creates a new {@link AbstractNumberCriterion} as found in a {@link MagicPattern}. The criterion shall define one
	 * numeric evaluation contained by a pattern.
	 * <p>
	 * If the criterion is met for given binary data, this indicates the patterns type assumption to be at least
	 * partially correct. ({@link MagicCriterion#isMatch(byte[], int)})
	 * </p>
	 *
	 * @param endianness The expected endianness of compared binary data.
	 * @throws MagicPatternException Shall be thrown, if an invalid default operator has been set.
	 */
	public AbstractNumberCriterion(EndianType endianness) throws MagicPatternException {
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
	public Number getTestValue() {
		return testValue;
	}

	/**
	 * Evaluates a {@link MagicOperator#AND} modifier for this criterion.
	 *
	 * @param extractedValue The extracted value that shall be modified.
	 * @param operand        The operand, that shall be applied.
	 * @return The resulting value.
	 */
	protected abstract Number applyConjunction(Number extractedValue, Number operand);

	/**
	 * Evaluates a {@link MagicOperator#OR} modifier for this criterion.
	 *
	 * @param extractedValue The extracted value that shall be modified.
	 * @param operand        The operand, that shall be applied.
	 * @return The resulting value.
	 */
	protected abstract Number applyDisjunction(Number extractedValue, Number operand);

	/**
	 * Evaluates a {@link MagicOperator#XOR} modifier for this criterion.
	 *
	 * @param extractedValue The extracted value that shall be modified.
	 * @param operand        The operand, that shall be applied.
	 * @return The resulting value.
	 */
	protected abstract Number applyContravalence(Number extractedValue, Number operand);

	/**
	 * Evaluates a {@link MagicOperator#COMPLEMENT} modifier for this criterion.
	 *
	 * @param extractedValue The extracted value that shall be modified.
	 * @return The resulting value.
	 */
	protected abstract Number applyComplement(Number extractedValue);

	/**
	 * Evaluates a {@link MagicOperator#ADD} modifier for this criterion.
	 *
	 * @param extractedValue The extracted value that shall be modified.
	 * @param operand        The operand, that shall be applied.
	 * @return The resulting value.
	 */
	protected abstract Number applyAddition(Number extractedValue, Number operand);

	/**
	 * Evaluates a {@link MagicOperator#SUBTRACT} modifier for this criterion.
	 *
	 * @param extractedValue The extracted value that shall be modified.
	 * @param operand        The operand, that shall be applied.
	 * @return The resulting value.
	 */
	protected abstract Number applySubtraction(Number extractedValue, Number operand);

	/**
	 * Evaluates a {@link MagicOperator#MULTIPLY} modifier for this criterion.
	 *
	 * @param extractedValue The extracted value that shall be modified.
	 * @param operand        The operand, that shall be applied.
	 * @return The resulting value.
	 */
	protected abstract Number applyMultiplication(Number extractedValue, Number operand);

	/**
	 * Evaluates a {@link MagicOperator#DIVIDE} modifier for this criterion.
	 *
	 * @param extractedValue The extracted value that shall be modified.
	 * @param operand        The operand, that shall be applied.
	 * @return The resulting value.
	 */
	protected abstract Number applyDivision(Number extractedValue, Number operand);

	/**
	 * Evaluates a {@link MagicOperator#MODULO} modifier for this criterion.
	 *
	 * @param extractedValue The extracted value that shall be modified.
	 * @param operand        The operand, that shall be applied.
	 * @return The resulting value.
	 */
	protected abstract Number applyModulo(Number extractedValue, Number operand);

	/**
	 * Evaluates a {@link MagicOperator#EQUALS} for this criterion.
	 *
	 * @param extractedValue The value, that shall be equal to the expected test value.
	 * @return True, if the given value is equal to the expected test value.
	 * @throws MagicPatternException Shall be thrown, if the evaluation failed with an exception.
	 */
	protected abstract boolean testEqual(Number extractedValue) throws MagicPatternException;

	/**
	 * Evaluates a {@link MagicOperator#NOT_EQUALS} for this criterion.
	 *
	 * @param extractedValue The value, that shall not be equal to the expected test value.
	 * @return True, if the given value is not equal to the expected test value.
	 * @throws MagicPatternException Shall be thrown, if the evaluation failed with an exception.
	 */
	protected abstract boolean testNotEqual(Number extractedValue) throws MagicPatternException;

	/**
	 * Evaluates a {@link MagicOperator#GREATER_THAN} for this criterion.
	 *
	 * @param extractedValue The value, that shall be great than the expected test value.
	 * @return True, if the given value is greater than the expected test value.
	 * @throws MagicPatternException Shall be thrown, if the evaluation failed with an exception.
	 */
	protected abstract boolean testGreaterThan(Number extractedValue) throws MagicPatternException;

	/**
	 * Evaluates a {@link MagicOperator#LESS_THAN} for this criterion.
	 *
	 * @param extractedValue The value, that shall be less than the expected test value.
	 * @return True, if the given value is less than the expected test value.
	 * @throws MagicPatternException Shall be thrown, if the evaluation failed with an exception.
	 */
	protected abstract boolean testLessThan(Number extractedValue) throws MagicPatternException;

	/**
	 * Evaluates a {@link MagicOperator#AND} for this criterion.
	 *
	 * @param extractedValue The value, that shall have set all the same bits as the expected test value.
	 * @return True, if the given value has the same bits set, as the expected test value.
	 * @throws MagicPatternException Shall be thrown, if the evaluation failed with an exception.
	 */
	protected abstract boolean testAnd(Number extractedValue) throws MagicPatternException;

	/**
	 * Evaluates a {@link MagicOperator#XOR} for this criterion.
	 *
	 * @param extractedValue The value, that shall have cleared all bits set in the expected test value.
	 * @return True, if the given value has cleared all bits set in the expected test value.
	 * @throws MagicPatternException Shall be thrown, if the evaluation failed with an exception.
	 */
	protected abstract boolean testXor(Number extractedValue) throws MagicPatternException;

	/**
	 * Evaluates a {@link MagicOperator#COMPLEMENT} for this criterion.
	 *
	 * @param extractedValue The value, that shall have set all the same bits as the expected test value, after being
	 *                       negated.
	 * @return True, if the given value has the same bits set, as the expected test value, after being negated.
	 * @throws MagicPatternException Shall be thrown, if the evaluation failed with an exception.
	 */
	protected abstract boolean testComplement(Number extractedValue) throws MagicPatternException;

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
	public MagicCriterionResult<Number> isMatch(byte[] data, int currentReadOffset)
			throws MagicPatternException {
		boolean matches;
		Number extractedValue = 0;
		MagicExtractor<?> extractor = getMagicPattern().getType().getExtractor();
		if (extractor instanceof NumberExtractor) {
			extractedValue = ((NumberExtractor) extractor).extractValue(data, currentReadOffset);
		}
		if (extractedValue == null) {
			return new MagicCriterionResult<Number>(this, currentReadOffset);
		}

		// Evaluate flags and modifiers
		extractedValue = applyModifier(extractedValue);
		if (extractedValue == null) {
			return new MagicCriterionResult<Number>(this, currentReadOffset);
		}

		// Test the value for a match.
		switch (getOperator()) {
			case NOT_EQUALS:
				matches = testNotEqual(extractedValue);
				break;
			case GREATER_THAN:
				matches = testGreaterThan(extractedValue);
				break;
			case LESS_THAN:
				matches = testLessThan(extractedValue);
				break;
			case AND:
				matches = testAnd(extractedValue);
				break;
			case XOR:
				matches = testXor(extractedValue);
				break;
			case COMPLEMENT:
				matches = testComplement(extractedValue);
				break;
			case EQUALS:
			default:
				matches = testEqual(extractedValue);
				break;
		}

		if (matches) {
			return new MagicCriterionResult<Number>(this,
					currentReadOffset + getMagicPattern().getType().getByteLength(),
					extractedValue);
		} else {
			return new MagicCriterionResult<Number>(this, currentReadOffset);
		}
	}

	/**
	 * Applies flags and modifiers, that are defined by the {@link MagicType} to the given extracted value and returns
	 * the modified value.
	 *
	 * @param extractedValue The value, that shall be modified.
	 * @return The modified value.
	 */
	private Number applyModifier(Number extractedValue) {
		if(this.modificatonOperator == null){
			return extractedValue;
		}

		//Apply modifier.
		switch (this.modificatonOperator) {
			case OR:
				return applyDisjunction(extractedValue, this.modificationOperand);
			case XOR:
				return applyContravalence(extractedValue, this.modificationOperand);
			case COMPLEMENT:
				return applyComplement(extractedValue);
			case ADD:
				return applyAddition(extractedValue, this.modificationOperand);
			case SUBTRACT:
				return applySubtraction(extractedValue, this.modificationOperand);
			case MULTIPLY:
				return applyMultiplication(extractedValue, this.modificationOperand);
			case DIVIDE:
				return applyDivision(extractedValue, this.modificationOperand);
			case MODULO:
				return applyModulo(extractedValue, this.modificationOperand);
			case AND:
			default:
				return applyConjunction(extractedValue, this.modificationOperand);
		}
	}

	/**
	 * Parses the test value of a {@link AbstractNumberCriterion} to the according numeric type {@link Number}.
	 * Override in extending classes to return a more specific numeric type.
	 *
	 * @param valueString The value, that shall be parsed.
	 * @return A typed numeric value, according to {@link AbstractNumberCriterion}.
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
	public void parse(MagicPattern magicPattern, String rawDefinition) throws MagicPatternException {
		super.parse(magicPattern, rawDefinition);
		if (rawDefinition == null || rawDefinition.length() == 0) {
			throw new MagicPatternException("Criterion definition is empty.");
		}

		MagicOperator operator = MagicOperator.forOperator(rawDefinition.charAt(0), NUMERIC_OPERATORS);
		String valueString;
		if (operator == null) {
			valueString = rawDefinition;
		} else {
			setOperator(operator);
			valueString = rawDefinition.substring(1).trim();
		}

		this.testValue = decodeValueString(valueString);

		// Parse type appended numeric modifier (if any).
		String flagsAndModifiers = getMagicPattern().getType().getFlagsAndModifiers();
		if (flagsAndModifiers == null || flagsAndModifiers.trim().isEmpty()) {
			return;
		}
		Matcher matcher = MODIFIER_PATTERN.matcher(flagsAndModifiers.trim());
		if (!matcher.matches()) {
			throw new MagicPatternException(String.format("Unknown modifier: %s", flagsAndModifiers));
		}
		// An operator marks the modification - it must be present!
		this.modificatonOperator = MagicOperator.forPattern(matcher.group(1), TYPE_MODIFIERS);
		if (this.modificatonOperator == null) {
			throw new MagicPatternException(String.format("Unknown modification operator: %s", flagsAndModifiers));
		}
		// Find modification operand. Only complement is valid without operand.
		if (matcher.group(2) == null && matcher.group(3) == null) {
			if (!MagicOperator.COMPLEMENT.equals(operator)) {
				throw new MagicPatternException(String.format("Empty/unknown modification operand: %s", flagsAndModifiers));
			}
			this.modificationOperand = 0;
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
	 */
	@Override
	public byte[] getStartingBytes() throws MagicPatternException {
		return EndianConverterFactory.createEndianConverter(getEndianness()).convertToByteArray(
				getTestValue().longValue(), getMagicPattern().getType().getByteLength()
		);
	}
}
