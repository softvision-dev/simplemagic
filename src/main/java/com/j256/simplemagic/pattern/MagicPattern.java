package com.j256.simplemagic.pattern;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.components.MagicMessage;
import com.j256.simplemagic.pattern.components.MagicOffset;
import com.j256.simplemagic.pattern.components.MagicType;
import com.j256.simplemagic.pattern.components.criterion.MagicCriterionFactory;
import com.j256.simplemagic.pattern.components.criterion.MagicCriterionResult;
import com.j256.simplemagic.pattern.components.MagicCriterion;
import com.j256.simplemagic.pattern.matching.MatchingResult;
import com.j256.simplemagic.pattern.matching.MatchingState;
import com.j256.simplemagic.logger.Logger;
import com.j256.simplemagic.logger.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.j256.simplemagic.pattern.PatternUtils.*;
import static com.j256.simplemagic.pattern.components.criterion.types.numeric.AbstractNumberCriterion.NUMERIC_OPERATORS;

/**
 * <b>An instance of this class represents a line in magic (5) format.</b>
 * <p>
 * As defined in the Magic(5) Manpage:
 * </p>
 * <p>
 * <i>
 * This manual page documents the format of magic files as used by the file(1) command, version 5.32. The file(1)
 * command identifies the type of a file using, among other tests, a test for whether the file contains certain
 * ``magic patterns'' The database of these ``magic patterns'' is usually located in a binary file in
 * /usr/share/misc/magic.mgc or a directory of source text magic pattern fragment files in /usr/share/misc/magic
 * The database specifies what patterns are to be tested for, what message or MIME type to print if a particular
 * pattern is found, and additional information to extract from the file.
 * </i>
 * </p>
 * <p>
 * <i>
 * The format of the source fragment files that are used to build this database is as follows: Each line of a fragment
 * file specifies a test to be performed. A test compares the data starting at a particular offset in the file with a
 * byte value, a string or a numeric value. If the test succeeds, a message is printed. The line consists of the
 * following fields:
 * </i>
 * <ul>
 * <li>offset: {@link MagicOffset}</li>
 * <li>type: {@link MagicType}</li>
 * <li>test: {@link MagicCriterion}</li>
 * <li>message: {@link MagicMessage}</li>
 * </ul>
 * </p>
 * <p>
 * Attention: "test" shall be named "Criterion" for the purposes of this library.
 * </p>
 * <p>
 * Some file formats contain additional information which is to be printed along with the file type or need additional
 * tests to determine the true file type. These additional tests are introduced by one or more > characters preceding
 * the offset. The number of > on the line indicates the level of the test; a line with no > at the beginning is
 * considered to be at level 0. Tests are arranged in a tree-like hierarchy: if the test on a line at level n succeeds,
 * all following tests at level n+1 are performed, and the messages printed if the tests succeed, until a line with
 * level n (or less) appears. For more complex files, one can use empty messages to get just the "if/then" effect.
 * </p>
 */
public class MagicPattern {

	private static final Logger LOGGER = LoggerFactory.getLogger(MagicPattern.class);

	private final int level;
	private final MagicOffset offset;
	private final MagicType type;
	private final MagicCriterion<?> criterion;
	private final MagicMessage message;
	private final List<MagicPattern> children = new ArrayList<MagicPattern>();

	private String mimeType = null;
	private boolean optional = false;

	/**
	 * An instance of this class stores and handles all information found in a Magic(5) line, collects following higher
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
	 * The type information of said met patterns (as defined via their {@link MagicMessage}) can therefore be collected
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
	 * @param message   The {@link MagicMessage} contains information, that shall be appended to the {@link MatchingResult}.
	 *                  A 'null' value will be treated as invalid.
	 * @throws MagicPatternException Invalid parameters shall cause this.
	 */
	public MagicPattern(int level, MagicOffset offset, MagicType type, MagicCriterion<?> criterion, MagicMessage message)
			throws MagicPatternException {
		if (level < 0 || offset == null || type == null || criterion == null || message == null) {
			throw new MagicPatternException("Invalid magic pattern initialization.");
		}
		this.level = level;
		this.offset = offset;
		this.type = type;
		this.message = message;
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
	public MagicCriterion<?> getCriterion() {
		return criterion;
	}

	/**
	 * Returns the {@link MagicMessage} of this {@link MagicPattern}, containing information, that shall be appended to a
	 * {@link MatchingResult}.
	 *
	 * @return The {@link MagicMessage} of this {@link MagicPattern} (Must never return 'null')
	 */
	public MagicMessage getMessage() {
		return message;
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
				isMatch(data, 0, 0, new MatchingResult(getMessage().getMessage(), getMimeType()));
	}

	/**
	 * Will evaluate this pattern for the given data and shall always return a {@link MatchingResult}, that summarizes
	 * the evaluation's results.
	 * This Method assumes to be initially run from a top-level pattern and also assumes to then be called recursively
	 * for possibly contained child patterns.
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

		MagicCriterionResult<?> result = null;
		MagicCriterion<?> criterion = getCriterion();
		if (!criterion.isNoopCriterion()) {
			result = criterion.isMatch(data, offset);
			if (!result.isMatch()) {
				return patternResult;
			} else if (MatchingState.NO_MATCH.equals(patternResult.getMatchingState())) {
				patternResult.setMatchingState(MatchingState.PARTIAL_MATCH);
			}
			offset = result.getNextReadOffset();
		}

		if (getMessage().getFormatter() != null) {
			if (getMessage().isClearPreviousMessages()) {
				patternResult.clear();
			}
			// if we are appending and need a space then prepend one
			if (getMessage().isFormatSpacePrefix() && patternResult.length() > 0) {
				patternResult.append(" ");
			}

			patternResult.append(getMessage().getFormatter().format(
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
		if (!UNKNOWN_TYPE_NAME.equals(getMessage().getMessage()) && UNKNOWN_TYPE_NAME.equals(patternResult.getMessage())) {
			patternResult.setMessage(getMessage().getMessage());
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
		if (getMessage().getMessage() != null) {
			sb.append(",name '").append(getMessage().getMessage()).append('\'');
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
		if (getMessage().getFormatter() != null) {
			sb.append(",format '").append(getMessage().getFormatter()).append('\'');
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

		// Search the missing operand for isolated numeric operators.
		if (type.getCriterionType().isNumeric() && criterionString.length() == 1 &&
				(MagicOperator.forOperator(criterionString.charAt(0), NUMERIC_OPERATORS) != null)) {
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
		MagicCriterion<?> criterion = MagicCriterionFactory.createCriterion(type.getCriterionType(), criterionString);

		// Any remaining characters are the MagicFormat.
		startPos = findNonWhitespace(rawLine, endPos + 1);
		MagicMessage format;
		if (startPos >= 0) {
			format = MagicMessage.parse(type.getCriterionType(), rawLine.substring(startPos));
		} else {
			format = MagicMessage.parse(type.getCriterionType(), "");
		}

		MagicPattern magicPattern = new MagicPattern(level, offset, type, criterion, format);
		criterion.parse(magicPattern, criterionString);
		return magicPattern;
	}
}
