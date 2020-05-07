package com.j256.simplemagic.pattern.components;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.PatternUtils;
import com.j256.simplemagic.pattern.components.criterion.types.CriterionType;
import com.j256.simplemagic.pattern.components.extractor.MagicExtractorFactory;
import com.j256.simplemagic.pattern.components.extractor.MagicExtractor;

import java.math.BigInteger;

/**
 * The type definition from a line in magic (5) format.
 */
public class MagicType {

	private final CriterionType criterionType;
	private final MagicExtractor<?> extractor;
	private final BigInteger andValue;
	private final boolean unsigned;
	private final String typeString;

	/**
	 * Creates a new {@link MagicType} as found in a {@link MagicPattern}. The type shall influence the evaluation
	 * and processing of following criteria and the extraction of compared values from compared data.
	 * <p>
	 * The binary data must contain a matching value of the hereby defined type at a specific position, to match the pattern.
	 * </p>
	 *
	 * @param criterionType The type of criterion, that is defined by the pattern. A 'null' value will be treated as
	 *                      invalid.
	 * @param extractor     The extractor, that must be used to extract values from data, that shall be checked for a
	 *                      match with the current pattern. A 'null' value will be treated as invalid.
	 * @param andValue      A value, that shall be ANDed with the extracted value of a test criterion, pre-evaluation.
	 *                      This may be set to null, to disable the 'ANDing'.
	 * @param unsigned      A value of 'true' will define compared values as unsigned.
	 * @param typeString    The raw type string including possible additional appended flags and modifiers. A 'null'
	 *                      value will be treated as invalid.
	 * @throws MagicPatternException Invalid parameters shall cause this.
	 */
	public MagicType(CriterionType criterionType, MagicExtractor<?> extractor, BigInteger andValue, boolean unsigned,
			String typeString) throws MagicPatternException {
		if (criterionType == null || extractor == null || typeString == null) {
			throw new MagicPatternException("Invalid magic type initialization.");
		}
		this.criterionType = criterionType;
		this.extractor = extractor;
		this.andValue = andValue;
		this.unsigned = unsigned;
		this.typeString = typeString;
	}

	/**
	 * Returns the value, that shall be ANDed with the compared value of a test criterion, pre-evaluation.
	 *
	 * @return The value, that shall be ANDed with the compared value of a test criterion, pre-evaluation. This may
	 * return 'null' if such an AND value has not been set in the pattern.
	 */
	public BigInteger getAndValue() {
		return andValue;
	}

	/**
	 * Returns true, if the compared value, is unsigned.
	 *
	 * @return True, if the compared value, is unsinged.
	 */
	public boolean isUnsigned() {
		return unsigned;
	}

	/**
	 * Returns the {@link CriterionType}, that shall be used as a criterion.
	 *
	 * @return The {@link CriterionType}, that shall be used as a criterion.
	 */
	public CriterionType getCriterionType() {
		return criterionType;
	}

	/**
	 * Returns the raw type string including possible appended flags and modifiers.
	 *
	 * @return The raw type string including possible appended flags and modifiers.
	 */
	public String getTypeString() {
		return typeString;
	}

	/**
	 * Returns the extractor, that must be used to extract values from data, that shall be checked for a match with the
	 * current pattern.
	 *
	 * @return The extractor to use, to extract comparable values from data. (Must never return 'null')
	 */
	public MagicExtractor<?> getExtractor() {
		return extractor;
	}

	/**
	 * Returns the byte length of values of this type.
	 *
	 * @return The byte length of values of the type.
	 */
	public int getByteLength() {
		return this.extractor.getByteLength();
	}

	/**
	 * Parse the given raw definition to initialize a {@link MagicType} instance.
	 *
	 * @param rawDefinition The raw definition of the {@link MagicType} as a String.
	 * @throws MagicPatternException Shall be thrown, if the parsing failed.
	 */
	public static MagicType parse(String rawDefinition) throws MagicPatternException {
		if (rawDefinition == null || rawDefinition.isEmpty()) {
			throw new MagicPatternException("Magic pattern type is empty.");
		}

		String typeString = rawDefinition;
		BigInteger andValue = null;
		// process the AND (&) part of the type
		int andIndex = rawDefinition.indexOf('&');
		// we use Long because of overlaps
		if (andIndex >= 0) {
			String andString = rawDefinition.substring(andIndex + 1);
			try {
				andValue = PatternUtils.parseNumericValue(andString);
			} catch (MagicPatternException ex) {
				throw new MagicPatternException(String.format("Invalid type AND-number: %s", andString), ex);
			}
			typeString = typeString.substring(0, andIndex);
		}
		if (typeString.length() == 0) {
			throw new MagicPatternException("Blank type string.");
		}

		// process the type string
		CriterionType criterionType = CriterionType.forName(typeString);
		boolean unsigned = false;
		if (criterionType == null) {
			if (typeString.charAt(0) == 'u') {
				criterionType = CriterionType.forName(typeString.substring(1));
				unsigned = true;
			} else {
				int index = typeString.indexOf('/');
				if (index > 0) {
					criterionType = CriterionType.forName(typeString.substring(0, index));
				}
			}
			if (criterionType == null) {
				throw new MagicPatternException(String.format("unknown magic type string: %s", typeString));
			}
		}

		MagicExtractor<?> extractor = MagicExtractorFactory.createExtractor(criterionType);

		return new MagicType(criterionType, extractor, andValue, unsigned, typeString);
	}
}
