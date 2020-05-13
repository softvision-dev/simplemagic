package com.j256.simplemagic.pattern.components;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.components.criterion.types.CriterionType;
import com.j256.simplemagic.pattern.extractor.MagicExtractorFactory;
import com.j256.simplemagic.pattern.extractor.MagicExtractor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>An instance of this class represents a type definition from a line in magic (5) format.</b>
 * <p>
 * As defined in the Magic(5) Manpage:
 * </p>
 * <p>
 * <i>
 * The type of the data to be tested.
 * </i>
 * </p>
 * <p>
 * The supported types are enumerated in: {@link CriterionType}
 * </p>
 * <p>
 * <i>
 * Each top-level magic pattern [...] is classified as text or binary according to the types used. Types ``regex'' and
 * ``search'' are classified as text tests, unless non-printable characters are used in the pattern. All other tests
 * are classified as binary. A top-level pattern is considered to be a test text when all its patterns are text
 * patterns; otherwise, it is considered to be a binary pattern. When matching a file, binary patterns are tried first;
 * if no match is found, and the file looks like text, then its encoding is determined and the text patterns are tried.
 * </i>
 * </p>
 * <p>
 * <i>
 * Prepending a u to the type indicates that ordered comparisons should be unsigned.
 * </i>
 * </p>
 * <p>
 * A type definition may be followed by flags and modifiers, those are strongly type specific and will be discussed
 * in the type specific implementations of {@link MagicCriterion}. For the purposes of this class, it is sufficient to
 * claim, that the type name must consist of alphabetical letters and numbers and might be followed by a character,
 * that is neither. This marker may be a flag marker like '/' or an operator like '&' - and an operand. Such
 * flags/modifiers shall be collected and shall be parsed type specific elsewhere.
 * </p>
 */
public class MagicType {

	private static final Pattern TYPE_PATTERN = Pattern.compile("^(u)?([a-zA-Z0-9]+)(.*)?");

	private final CriterionType criterionType;
	private final MagicExtractor<?> extractor;
	private final boolean unsigned;
	private final String flagsAndModifiers;

	/**
	 * Creates a new {@link MagicType} as found in a {@link MagicPattern}. The type shall influence the evaluation
	 * and processing of following criteria and the extraction of compared values from compared data.
	 * <p>
	 * The binary data must contain a matching value of the hereby defined type at a specific position, to match the pattern.
	 * </p>
	 *
	 * @param criterionType     The type of criterion, that is defined by the pattern. A 'null' value will be treated as
	 *                          invalid.
	 * @param extractor         The extractor, that must be used to extract values from data, that shall be checked for a
	 *                          match with the current pattern. A 'null' value will be treated as invalid.
	 * @param unsigned          A value of 'true' will define compared values as unsigned.
	 * @param flagsAndModifiers Possibly appended flags and modifiers. A 'null' value will be treated as invalid.
	 * @throws MagicPatternException Invalid parameters shall cause this.
	 */
	public MagicType(CriterionType criterionType, MagicExtractor<?> extractor, boolean unsigned,
			String flagsAndModifiers) throws MagicPatternException {
		if (criterionType == null || extractor == null || flagsAndModifiers == null) {
			throw new MagicPatternException("Invalid magic type initialization.");
		}
		this.criterionType = criterionType;
		this.extractor = extractor;
		this.unsigned = unsigned;
		this.flagsAndModifiers = flagsAndModifiers;
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
	 * Returns possibly appended flags and modifiers.
	 *
	 * @return Possibly appended flags and modifiers.
	 */
	public String getFlagsAndModifiers() {
		return flagsAndModifiers;
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

		Matcher matcher = TYPE_PATTERN.matcher(rawDefinition);
		if (!matcher.matches()) {
			throw new MagicPatternException(String.format("Invalid/unknown magic type: %s", rawDefinition));
		}
		boolean unsigned = matcher.group(1) != null;
		String typeString = matcher.group(2);
		String flags = matcher.group(3) == null ? "" : matcher.group(3);

		// process the type string
		CriterionType criterionType = CriterionType.forName(typeString);
		if (criterionType == null) {
			throw new MagicPatternException(String.format("Invalid/unknown magic type: %s", rawDefinition));
		}

		MagicExtractor<?> extractor = MagicExtractorFactory.createExtractor(criterionType);

		return new MagicType(criterionType, extractor, unsigned, flags);
	}
}
