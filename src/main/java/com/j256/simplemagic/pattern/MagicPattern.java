package com.j256.simplemagic.pattern;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.components.MagicFormat;
import com.j256.simplemagic.pattern.components.MagicOffset;
import com.j256.simplemagic.pattern.components.MagicType;
import com.j256.simplemagic.pattern.components.criterion.MagicCriterionFactory;
import com.j256.simplemagic.pattern.components.criterion.MagicCriterionResult;
import com.j256.simplemagic.pattern.components.criterion.MagicCriterion;
import com.j256.simplemagic.pattern.components.criterion.operator.NumericOperator;
import com.j256.simplemagic.pattern.components.criterion.operator.StringOperator;
import com.j256.simplemagic.pattern.matching.MatchingResult;
import com.j256.simplemagic.pattern.matching.MatchingState;
import com.j256.simplemagic.logger.Logger;
import com.j256.simplemagic.logger.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.j256.simplemagic.pattern.PatternUtils.*;

/**
 * Representation of a line of information of the magic (5) format.
 */
public class MagicPattern {

	private static final Logger LOGGER = LoggerFactory.getLogger(MagicPattern.class);

	private final int level;
	private final MagicOffset offset;
	private final MagicType type;
	private final MagicFormat format;
	private final MagicCriterion<?, ?> criterion;
	private final List<MagicPattern> children = new ArrayList<MagicPattern>();

	private String mimeType = null;
	private boolean optional = false;

	/**
	 * An instance of this class stores and handles all information found in such a line, collects following higher
	 * level patterns as children and offers the evaluation of the hereby defined criteria.
	 * <p>
	 * The compared binary data must (for most patterns, except No-op patterns) contain a specific value, of a specific
	 * type, at a specific offset, to match ({@link MagicPattern#isMatch(byte[])}) this pattern.
	 * </p>
	 * <p>
	 * If the criterion is met for given binary data, this indicates the patterns type assumption to be at least
	 * partially correct. ({@link MagicCriterion#isMatch(byte[], int)})
	 * </p>
	 * <p>
	 * If a consecutive chain of child patterns of said pattern and therefore all their criteria are also met, then the
	 * pattern is a full match and indeed a description of the type of binary data, that has been evaluated.
	 * </p>
	 * <p>
	 * The type information of said met patterns (as defined via their {@link MagicFormat}) can therefore be collected
	 * and used as the {@link MatchingResult} of {@link MagicPattern#isMatch(byte[])}.
	 * </p>
	 * <p>
	 * The String representation of a {@link MatchingResult} of such an evaluation will be formatted according to the
	 * formatting instructions given by such a line (if such instructions are present).
	 * </p>
	 *
	 * @param level     The level of this pattern. 0 means, that this is a top level pattern, higher levels define
	 *                  children of patterns with that level-1. Negative values are treated as invalid.
	 * @param offset    The {@link MagicOffset} from which information, that must be evaluated by this pattern, shall
	 *                  be read from processed binary data. A 'null' value will be treated as invalid.
	 * @param type      The {@link MagicType} of information, that shall be read from processed data.
	 *                  A 'null' value will be treated as invalid.
	 * @param criterion The {@link MagicCriterion}, that shall be used to evaluate this pattern.
	 *                  A 'null' value will be treated as invalid.
	 * @param format    The {@link MagicFormat} of information, that shall be applied to visualize extracted data.
	 *                  A 'null' value will be treated as invalid.
	 * @throws MagicPatternException Invalid parameters shall cause this.
	 */
	public MagicPattern(int level, MagicOffset offset, MagicType type, MagicCriterion<?, ?> criterion, MagicFormat format)
			throws MagicPatternException {
		if (level < 0 || offset == null || type == null || criterion == null || format == null) {
			throw new MagicPatternException("Invalid magic pattern initialization.");
		}
		this.level = level;
		this.offset = offset;
		this.type = type;
		this.format = format;
		this.criterion = criterion;
	}

	/**
	 * Returns the definition level of this {@link MagicPattern}, determining the hierarchical position of this pattern.
	 * (0 means, that this is a top level pattern, higher levels define children of patterns with that level-1.)
	 *
	 * @return The definition level of this {@link MagicPattern} (Must return a value >=0)
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Returns the {@link MagicOffset} of this {@link MagicPattern}, that determines the offset from which information,
	 * that must be evaluated by this pattern, shall be read from processed binary data.
	 *
	 * @return The {@link MagicOffset} of this {@link MagicPattern} (Must never return 'null')
	 */
	public MagicOffset getOffset() {
		return offset;
	}

	/**
	 * Returns the {@link MagicType} of this {@link MagicPattern}, determining the required type of information, that
	 * shall be read from processed data.
	 *
	 * @return The {@link MagicType} of this {@link MagicPattern} (Must never return 'null')
	 */
	public MagicType getType() {
		return type;
	}

	/**
	 * Returns the {@link MagicCriterion} of this {@link MagicPattern}, that shall be used to evaluate this pattern.
	 *
	 * @return The {@link MagicCriterion} of this {@link MagicPattern} (Must never return 'null')
	 */
	public MagicCriterion<?, ?> getCriterion() {
		return criterion;
	}

	/**
	 * Returns the {@link MagicFormat} of this {@link MagicPattern}, adding a format, that shall be applied to visualize
	 * extracted data.
	 *
	 * @return The {@link MagicFormat} of this {@link MagicPattern} (Must never return 'null')
	 */
	public MagicFormat getFormat() {
		return format;
	}

	/**
	 * Returns the determined MIME type. (or null, if a MIME type has not been set via an extension pattern.)
	 *
	 * @return The determined MIME type or null.
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * Sets the MIME type to the given value.
	 *
	 * @param mimeType The MIME type, that shall be set.
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * Returns true, if the current pattern has been marked as optional.
	 *
	 * @return True, if the current pattern has been marked as optional.
	 */
	public boolean isOptional() {
		return optional;
	}

	/**
	 * Set to 'true', to mark this pattern as optional.
	 *
	 * @param optional The intended optional state of this pattern.
	 */
	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	/**
	 * Returns all children of the current pattern.
	 *
	 * @return The children of the current pattern.
	 */
	public List<MagicPattern> getChildren() {
		return children;
	}

	/**
	 * Adds the given pattern as a child of the current pattern.
	 *
	 * @param child The pattern, that shall be added as a child of the current pattern. (Adding 'null' will be ignored.)
	 */
	public void addChild(MagicPattern child) {
		if (child != null) {
			this.children.add(child);
		}
	}

	/**
	 * Returns the characteristic starting bytes of this pattern (as defined by it's {@link MagicCriterion}. Allows for
	 * faster selection of relevant patterns.
	 *
	 * @return An array of the characteristic starting bytes of this pattern, or null if such bytes can not be determined.
	 * @throws MagicPatternException Shall be thrown, if the the {@link MagicCriterion} is malformed and does not allow
	 *                               to read the starting bytes.
	 */
	public byte[] getStartingBytes() throws MagicPatternException {
		if ((getOffset().getBaseOffset() != 0 ||
				(getOffset().isIndirect() && getOffset().getIndirectOffset().getOffset() != 0))
				|| getCriterion() == null) {
			return null;
		} else {
			return getCriterion().getStartingBytes();
		}
	}

	/**
	 * Will evaluate this pattern for the given data and shall always return a {@link MatchingResult}, that summarizes
	 * the evaluation's results.
	 *
	 * @param data The data, that shall be checked, whether they match this pattern.
	 * @return A {@link MatchingResult} that summarizes the evaluation's results. (Must never return 'null')
	 * @throws IOException           Shall be thrown if the given data can not be accessed.
	 * @throws MagicPatternException Shall be thrown if this {@link MagicPattern} is malformed and the evaluation
	 *                               could not be evaluated.
	 */
	public MatchingResult isMatch(byte[] data) throws IOException, MagicPatternException {
		return data == null ? null :
				isMatch(data, 0, 0, new MatchingResult(getFormat().getName(), getMimeType()));
	}

	/**
	 * Will evaluate this pattern for the given data and shall always return a {@link MatchingResult}, that summarizes
	 * the evaluation's results.
	 * This Method assumes to be run from a top-level pattern and also assumes to then be called recursively for possibly
	 * contained child patterns.
	 *
	 * @param data              The data, that shall be checked, whether they match this pattern.
	 * @param currentReadOffset The current offset in the given data. (resulting from recursions on this method.
	 *                          Should always be set to 0 for the initial matching call.)
	 * @param level             The current level of the pattern, that is evaluated. (resulting from recursions on this
	 *                          method. Should always be set to 0 for the initial matching call of a top level pattern.)
	 * @param patternResult     The currently stored result, that shall be translated to a {@link MatchingResult} in the
	 *                          end. (Allows to store the results of recursive calls of this method for child patterns.)
	 * @return A {@link MatchingResult} that summarizes the evaluation's results.
	 * @throws IOException           Shall be thrown if the given data can not be accessed.
	 * @throws MagicPatternException Shall be thrown if this {@link MagicPattern} is malformed and the evaluation
	 *                               could not be processed.
	 */
	protected MatchingResult isMatch(byte[] data, int currentReadOffset, int level, MatchingResult patternResult)
			throws IOException, MagicPatternException {
		int offset = (int) getOffset().getReadOffset(data, currentReadOffset);

		MagicCriterionResult<?, ?> result = null;
		MagicCriterion<?, ?> criterion = getCriterion();
		if (!criterion.isNoopCriterion()) {
			result = criterion.isMatch(data, offset);
			if (!result.isMatch()) {
				return patternResult;
			} else if (MatchingState.NO_MATCH.equals(patternResult.getMatchingState())) {
				patternResult.setMatchingState(MatchingState.PARTIAL_MATCH);
			}
			offset = result.getNextReadOffset();
		}

		if (getFormat().getFormatter() != null) {
			if (getFormat().isClearFormat()) {
				patternResult.clear();
			}
			// if we are appending and need a space then prepend one
			if (getFormat().isFormatSpacePrefix() && patternResult.length() > 0) {
				patternResult.append(" ");
			}

			patternResult.append(getFormat().getFormatter().format(
					result != null ? result.getMatchingValue() :
							getType().getExtractor().extractValue(data, offset))
			);
		}
		LOGGER.trace("matched data: {}: {}", this, patternResult);

		if (!getChildren().isEmpty()) {
			boolean allOptional = true;
			// run through the children to add more content-type details
			for (MagicPattern entry : getChildren()) {
				if (!entry.isOptional()) {
					allOptional = false;
				}
				// goes recursive here
				entry.isMatch(data, offset, level + 1, patternResult);
				// we continue to match to see if we can add additional children info to the name

				if (allOptional) {
					patternResult.setMatchingState(MatchingState.FULL_MATCH);
				}
			}
		} else {
			patternResult.setMatchingState(MatchingState.FULL_MATCH);
		}

		/*
		 * Now that we have processed this entry (either with or without children), see if we still need to annotate the
		 * content information.
		 *
		 * NOTE: the children will have the first opportunity to set this which makes sense since they are the most
		 * specific.
		 */
		if (!UNKNOWN_TYPE_NAME.equals(getFormat().getName()) && UNKNOWN_TYPE_NAME.equals(patternResult.getTypeName())) {
			patternResult.setTypeName(getFormat().getName());
		}
		/*
		 * Set the mime-type if it is not set already or if we've gotten more specific in the processing of a pattern
		 * and determine that it's actually a different type so we can override the previous mime-type. Example of this
		 * is Adobe Illustrator which looks like a PDF but has extra stuff in it.
		 */
		if (getMimeType() != null && (patternResult.getMimeType() == null || level > patternResult.getMatchingLevel())) {
			patternResult.setMimeType(getMimeType());
			patternResult.setMatchingLevel(level);
		}
		return patternResult;
	}

	/**
	 * Translates this pattern to it's string representation.
	 * <p>
	 * Attention: The resulting String is not equal to the initial raw definition of the pattern.
	 * The resulting String allows to represent this pattern in a more readable and understandable manner and does not
	 * restore the original pattern definition.
	 * </p>
	 *
	 * @return The string representation of this {@link MagicPattern}
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("level ").append(level);
		if (getFormat().getName() != null) {
			sb.append(",name '").append(getFormat().getName()).append('\'');
		}
		if (getMimeType() != null) {
			sb.append(",mime '").append(getMimeType()).append('\'');
		}
		if (getCriterion().getTestValue() != null) {
			sb.append(",test '")
					.append(getCriterion().getOperator().name())
					.append("', value '").append(getCriterion().getTestValue())
					.append('\'');
		}
		if (getFormat().getFormatter() != null) {
			sb.append(",format '").append(getFormat().getFormatter()).append('\'');
		}
		return sb.toString();
	}

	/**
	 * Parse the given raw definition to initialize a {@link MagicPattern} instance.
	 *
	 * @param rawLine The raw definition of the {@link MagicPattern} as a String.
	 * @throws MagicPatternException Shall be thrown if the parsing failed.
	 */
	public static MagicPattern parse(String rawLine) throws MagicPatternException {
		if (rawLine == null || rawLine.trim().isEmpty()) {
			throw new MagicPatternException("Magic pattern line is empty.");
		}

		// skip whitespaces to find level and MagicOffset.
		int startPos = findNonWhitespace(rawLine, 0);
		if (startPos < 0) {
			throw new MagicPatternException("Magic pattern line is empty.");
		}
		int endPos = findWhitespaceWithoutEscape(rawLine, startPos);
		if (endPos < 0) {
			throw new MagicPatternException("Magic pattern level and offset not found.");
		}

		int level;
		MagicOffset offset;
		String levelAndOffset = rawLine.substring(startPos, endPos).trim();
		int lastLevelIndex = levelAndOffset.lastIndexOf('>');
		// If last occurrence of '>' is the last character of 'levelAndOffset', the offset String is missing.
		// Assume whitespace in between level and offset: Append next element!
		if ((lastLevelIndex < 0 && levelAndOffset.isEmpty()) || lastLevelIndex == levelAndOffset.length() - 1) {
			startPos = findNonWhitespace(rawLine, endPos + 1);
			if (startPos < 0) {
				throw new MagicPatternException("Magic pattern offset not found.");
			}
			endPos = findWhitespaceWithoutEscape(rawLine, startPos);
			if (endPos < 0) {
				throw new MagicPatternException("Magic pattern offset not found.");
			}
			String offsetString = rawLine.substring(startPos, endPos).trim();

			level = lastLevelIndex < 0 ? 0 : lastLevelIndex + 1;
			offset = MagicOffset.parse(offsetString);
		}
		// Split level and MagicOffset.
		else if (lastLevelIndex < 0) {
			level = 0;
			offset = MagicOffset.parse(levelAndOffset);
		} else {
			level = lastLevelIndex + 1;
			offset = MagicOffset.parse(levelAndOffset.substring(lastLevelIndex + 1));
		}

		// skip whitespaces to find the MagicType.
		startPos = findNonWhitespace(rawLine, endPos + 1);
		if (startPos < 0) {
			throw new MagicPatternException("Magic pattern type not found.");
		}
		endPos = findWhitespaceWithoutEscape(rawLine, startPos);
		if (endPos < 0) {
			throw new MagicPatternException("Magic pattern is incomplete and ending after type definition.");
		}
		MagicType type = MagicType.parse(rawLine.substring(startPos, endPos));

		// skip whitespaces to find the MagicCriterion.
		startPos = findNonWhitespace(rawLine, endPos + 1);
		if (startPos < 0) {
			throw new MagicPatternException("Magic pattern criterion not found.");
		}
		endPos = findWhitespaceWithoutEscape(rawLine, startPos);
		if (endPos < 0) {
			endPos = rawLine.length();
		}
		String criterionString = rawLine.substring(startPos, endPos);
		// Search the missing operand for isolated operators.
		if (criterionString.length() == 1 &&
				(NumericOperator.forOperator(criterionString.charAt(0)) != null ||
						StringOperator.forOperator(criterionString.charAt(0)) != null)) {
			// skip any whitespace to find operand
			startPos = findNonWhitespace(rawLine, endPos + 1);
			// if there is no operand, then the operator is isolated and the testString is erroneous.
			if (startPos < 0) {
				throw new MagicPatternException(String.format("No operand found for criterion: '%s'", criterionString));
			}
			endPos = findWhitespaceWithoutEscape(rawLine, startPos);
			if (endPos < 0) {
				endPos = rawLine.length();
			}
			// the following element must be the operand, else the testString is erroneous anyway. Append it!
			criterionString += rawLine.substring(startPos, endPos);
		}
		MagicCriterion<?, ?> criterion = MagicCriterionFactory.createCriterion(type.getCriterionType(), criterionString);

		// Any remaining characters are the MagicFormat.
		startPos = findNonWhitespace(rawLine, endPos + 1);
		MagicFormat format;
		if (startPos >= 0) {
			format = MagicFormat.parse(type.getCriterionType(), rawLine.substring(startPos));
		} else {
			format = MagicFormat.parse(type.getCriterionType(), "");
		}

		MagicPattern magicPattern = new MagicPattern(level, offset, type, criterion, format);
		criterion.parse(magicPattern, criterionString);
		return magicPattern;
	}
}
