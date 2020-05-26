package com.j256.simplemagic.pattern;

import com.j256.simplemagic.MagicEntries;
import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.components.*;
import com.j256.simplemagic.pattern.components.operation.MagicOperationFactory;
import com.j256.simplemagic.pattern.components.operation.criterion.MagicCriterionResult;
import com.j256.simplemagic.pattern.components.operation.criterion.MagicCriterion;
import com.j256.simplemagic.pattern.components.operation.instruction.types.DefaultInstruction;
import com.j256.simplemagic.pattern.components.operation.instruction.types.IndirectInstruction;
import com.j256.simplemagic.pattern.components.operation.instruction.MagicInstruction;
import com.j256.simplemagic.pattern.components.operation.instruction.types.UseInstruction;
import com.j256.simplemagic.pattern.matching.MatchingResult;
import com.j256.simplemagic.pattern.matching.MatchingState;
import com.j256.simplemagic.logger.Logger;
import com.j256.simplemagic.logger.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.j256.simplemagic.pattern.PatternUtils.*;
import static com.j256.simplemagic.pattern.components.operation.criterion.numeric.AbstractNumberCriterion.NUMERIC_OPERATORS;

/**
 * <b>An instance of this class represents a line in magic pattern format.</b>
 * <p>
 * As defined in the Magic(5) Manpage:
 * </p>
 * <p>
 * <i>
 * This manual page documents the format of magic files as used by the file(1) command, version 5.38. The file(1)
 * command identifies the type of a file using, among other tests, a test for whether the file contains certain
 * “magic patterns”. The database of these “magic patterns” is usually located in a binary file in
 * /usr/share/misc/magic.mgc or a directory of source text magic pattern fragment files in /usr/share/misc/magic.
 * The database specifies what patterns are to be tested for, what message or MIME type to print if a particular
 * pattern is found, and additional information to extract from the file.
 * </i>
 * </p>
 * <p>
 * <i>
 * The format of the source fragment files that are used to build this database is as follows: Each line of a fragment
 * file specifies a test to be performed. A test compares the data starting at a particular offset in the file with a
 * byte value, a string or a numeric value. If the test succeeds, a message is printed.
 * </i>
 * <ul>
 * <li>offset: {@link MagicOffset}</li>
 * <li>type: {@link MagicType}</li>
 * <li>test: {@link MagicOperation}</li>
 * <li>message: {@link MagicMessage}</li>
 * </ul>
 * </p>
 * <p>
 * Attention: "test" shall be named "Operation" for the purposes of this library.
 * </p>
 * <p>
 * <i>
 * Some file formats contain additional information which is to be printed along with the file type or need additional
 * tests to determine the true file type. These additional tests are introduced by one or more > characters preceding
 * the offset. The number of > on the line indicates the level of the test; a line with no > at the beginning is
 * considered to be at level 0. Tests are arranged in a tree-like hierarchy: if the test on a line at level n succeeds,
 * all following tests at level n+1 are performed, and the messages printed if the tests succeed, until a line with
 * level n (or less) appears. For more complex files, one can use empty messages to get just the "if/then" effect.
 * </i>
 * </p>
 */
public class MagicPattern {

	private static final Logger LOGGER = LoggerFactory.getLogger(MagicPattern.class);

	private final int level;
	private final MagicOffset offset;
	private final MagicType type;
	private final MagicOperation operation;
	private final MagicMessage message;

	private MagicPattern parent;
	private final List<MagicPattern> children = new ArrayList<MagicPattern>();

	private String mimeType = null;
	private boolean optional = false;

	/**
	 * An instance of this class stores and handles all information found in a Magic(5) line, collects following higher
	 * level patterns as children and offers the evaluation of the hereby defined criteria.
	 * <p>
	 * The compared binary data must (for most patterns, except No-op patterns) contain a specific value, of a specific
	 * type, at a specific offset, to match ({@link MagicPattern#isMatch(byte[], int, MagicEntries)}) this pattern.
	 * </p>
	 * <p>
	 * If this pattern has a {@link MagicInstruction} for it's {@link MagicOperation} (such as "name" and "use") it must
	 * be evaluated contextually and will most likely influence other pattern executions, instead of being testable
	 * itself.
	 * </p>
	 * <p>
	 * If this pattern has a testable {@link MagicCriterion} for it's {@link MagicOperation} and if the criterion is met
	 * for given binary data, this indicates the patterns type assumption to be at least
	 * partially correct. ({@link MagicCriterion#isMatch(byte[], int, boolean)})
	 * </p>
	 * <p>
	 * If a consecutive chain of child patterns of said pattern and therefore all their criteria are also met, then the
	 * pattern is a full match and indeed a description of the type of binary data, that has been evaluated.
	 * </p>
	 * <p>
	 * The type information of said met patterns (as defined via their {@link MagicMessage}) can therefore be collected
	 * and used as the {@link MatchingResult} of {@link MagicPattern#isMatch(byte[], int, MagicEntries)}.
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
	 * @param operation The {@link MagicOperation}, that shall be used to evaluate this pattern.
	 *                  A 'null' value will be treated as invalid.
	 * @param message   The {@link MagicMessage} contains information, that shall be appended to the {@link MatchingResult}.
	 *                  A 'null' value will be treated as invalid.
	 * @throws MagicPatternException Invalid parameters shall cause this.
	 */
	public MagicPattern(int level, MagicOffset offset, MagicType type, MagicOperation operation, MagicMessage message)
			throws MagicPatternException {
		if (level < 0 || offset == null || type == null || operation == null || message == null) {
			throw new MagicPatternException("Invalid magic pattern initialization.");
		}
		this.level = level;
		this.offset = offset;
		this.type = type;
		this.message = message;
		this.operation = operation;
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
	 * Returns the {@link MagicOperation} of this {@link MagicPattern}, that shall be used to evaluate this pattern.
	 *
	 * @return The {@link MagicOperation} of this {@link MagicPattern} (Must never return 'null')
	 */
	public MagicOperation getOperation() {
		return operation;
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
	 * Returns true, if this pattern contains an instruction (such as "name" or "use") instead of a testable
	 * criterion.
	 *
	 * @return true, if this pattern contains an instruction (such as "name" or "use") instead of a testable
	 * criterion.
	 */
	public boolean isInstruction() {
		return this.operation instanceof MagicInstruction;
	}


	/**
	 * Returns true, if this pattern contains a testable criterion.
	 *
	 * @return true, if this pattern contains a testable criterion.
	 */
	public boolean isTest() {
		return this.operation instanceof MagicCriterion;
	}

	public void setParent(MagicPattern parent) {
		this.parent = parent;
	}

	@SuppressWarnings("unused")
	public MagicPattern getParent() {
		return parent;
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
				|| getOperation() == null) {
			return null;
		} else {
			return getOperation().getStartingBytes();
		}
	}

	/**
	 * Will evaluate this pattern for the given data and shall always return a {@link MatchingResult}, that summarizes
	 * the evaluation's results.
	 *
	 * @param data       The data, that shall be checked, whether they match this pattern.
	 * @param baseOffset The initial offset for indirect pattern calls.
	 * @param entries    The MagicPattern database for named and indirect pattern calls.
	 * @return A {@link MatchingResult} that summarizes the evaluation's results. (Must never return 'null')
	 * @throws IOException           Shall be thrown if the given data can not be accessed.
	 * @throws MagicPatternException Shall be thrown if this {@link MagicPattern} is malformed and the evaluation
	 *                               could not be evaluated.
	 */
	public MatchingResult isMatch(byte[] data, int baseOffset, MagicEntries entries) throws IOException,
			MagicPatternException {
		MatchingResult result = new MatchingResult(getMessage().getMessage(), getMimeType());
		isMatch(data, 0, baseOffset, 0, result, entries, false);
		return data == null || entries == null ? null : result;
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
	 * @param indirectOffset    The initial offset for indirect pattern calls.
	 * @param level             The current level of the pattern, that is evaluated. (resulting from recursions on this
	 *                          method. Should always be set to 0 for the initial matching call of a top level pattern.)
	 * @param patternResult     The currently stored result, that shall be translated to a {@link MatchingResult} in the
	 *                          end. (Allows to store the results of recursive calls of this method for child patterns.)
	 * @param entries           The MagicPattern database for named and indirect pattern calls.
	 * @return A {@link MatchingResult} that summarizes the evaluation's results.
	 * @throws IOException           Shall be thrown if the given data can not be accessed.
	 * @throws MagicPatternException Shall be thrown if this {@link MagicPattern} is malformed and the evaluation
	 *                               could not be processed.
	 */
	protected boolean isMatch(byte[] data, int currentReadOffset, int indirectOffset, int level,
			MatchingResult patternResult, MagicEntries entries, boolean invertEndianness)
			throws IOException, MagicPatternException {
		int offset = (int) getOffset().getReadOffset(data, currentReadOffset, indirectOffset);

		MagicCriterionResult<?> result = null;
		boolean currentMatchingState = false;
		MagicOperation operation = getOperation();

		// If it is a "use" instruction, we shall call the hereby referenced named pattern.
		if (isInstruction() && getOperation() instanceof UseInstruction) {
			UseInstruction useInstruction = (UseInstruction) getOperation();
			MagicPattern usedPattern = entries.getNamedPattern(useInstruction.getOperand());
			if (usedPattern == null) {
				throw new MagicPatternException(String.format("Named pattern not found for: '%s'",
						useInstruction.getOperand()));
			}
			// goes recursive here
			currentMatchingState = usedPattern.isMatch(data, 0, offset, level + 1, patternResult,
					entries, useInstruction.isInvertEndianness());
			if (!patternResult.isMatchingState(MatchingState.FULL_MATCH)) {
				return currentMatchingState;
			}
			if (!getChildren().isEmpty()) {
				patternResult.setMatchingState(MatchingState.PARTIAL_MATCH);
			}
		}
		// If it is an "indirect" instruction, we shall restart the evaluation from the current offset.
		else if (isInstruction() && getOperation() instanceof IndirectInstruction) {
			//TODO implement /r
			for (MagicPattern pattern : entries.getMagicPatterns()) {
				pattern.isMatch(data, 0, offset, level + 1, patternResult, entries,
						invertEndianness);
			}
		}
		// If it is a testable criterion, then we shall evaluate it's results.
		else if (!operation.isNoopCriterion() && operation instanceof MagicCriterion) {
			result = ((MagicCriterion<?>) operation).isMatch(data, offset, invertEndianness);
			if (!result.isMatch()) {
				return currentMatchingState;
			} else if (patternResult.isMatchingState(MatchingState.NO_MATCH)) {
				patternResult.setMatchingState(MatchingState.PARTIAL_MATCH);
			}
			currentMatchingState = true;
			offset = result.getNextReadOffset();
		}
		// If it is some sort of noop criterion, then we shall not evaluate it and default to true.
		// (this is a mere fallback - noop criteria are mostly instructions: An instruction should have been handled above.)
		else if (operation.isNoopCriterion()) {
			currentMatchingState = true;
			patternResult.setMatchingState(MatchingState.PARTIAL_MATCH);
		}

		// If we have reached this, then the current Criterion has matched.
		// If this criterion has a message and a formatter, then format it's message and append it.
		if (!getMessage().isEmpty() && getMessage().getFormatter() != null) {
			if (getMessage().isClearPreviousMessages()) {
				patternResult.clear();
			}
			// if we are appending and need a space then prepend one
			if (getMessage().isFormatSpacePrefix() && patternResult.length() > 0) {
				patternResult.append(" ");
			}

			patternResult.append(getMessage().getFormatter().format(
					result != null ? result.getMatchingValue() :
							getType().getExtractor().extractValue(data, offset, invertEndianness))
			);
		}
		LOGGER.trace("matched data: {}: {}", this, patternResult);

		// If it has children, we must evaluate it's descendants now recursively.
		if (!getChildren().isEmpty()) {
			boolean allOptional = true;
			boolean noneMatches = true;
			MagicPattern defaultCase = null;
			// Run through the children to add more content-type details
			for (MagicPattern child : getChildren()) {
				// If the current child is a "default" instruction it does match, if all it's siblings failed.
				// In exactly that case it must be tested later and shall only be stored for now.
				if (child.getOperation() instanceof DefaultInstruction) {
					defaultCase = child;
					continue;
				}
				if (!child.isOptional()) {
					allOptional = false;
				}

				// goes recursive here
				if (child.isMatch(data, offset, indirectOffset, level + 1, patternResult, entries, invertEndianness)) {
					noneMatches = false;
				}
				// we continue to match to see if we can add additional children info to the name
			}
			// If all those children have been optional, their messages have already been appended accordingly.
			// Their results however do not influence our matching state - reset it to a full match.
			if (allOptional) {
				patternResult.setMatchingState(MatchingState.FULL_MATCH);
			}
			// If none of it's siblings have matched and a default case has been defined, it is evaluated now.
			if (noneMatches && defaultCase != null) {
				defaultCase.isMatch(data, offset, indirectOffset, level + 1, patternResult, entries, invertEndianness);
			}
		}
		// If it does not have children, the pattern chain has reached it's deepest matching descendant and therefore
		// this chain is a full match of the current evaluation.
		else {
			patternResult.setMatchingState(MatchingState.FULL_MATCH);
		}

		/*
		 * Now that we have processed this pattern, see if we still need to annotate the content information.
		 */
		if (!getMessage().isEmpty() && patternResult.isUnknownTypeName()) {
			patternResult.setRawMessage(getMessage().getMessage());
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
		return currentMatchingState;
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
		if (!getMessage().isEmpty()) {
			sb.append(",name '").append(getMessage().getMessage()).append('\'');
		}
		if (getMimeType() != null) {
			sb.append(",mime '").append(getMimeType()).append('\'');
		}
		MagicCriterion<?> criterion;
		sb.append(",type '")
				.append(getType().getOperationType().getName())
				.append("'");
		if (getOperation() instanceof MagicCriterion && (criterion = (MagicCriterion<?>) getOperation()).getTestValue() != null) {
			sb.append(",operator '")
					.append(criterion.getOperator().name())
					.append("', value '").append(criterion.getTestValue())
					.append('\'');
		} else if (getOperation() instanceof MagicInstruction) {
			MagicInstruction instruction = (MagicInstruction) getOperation();
			sb.append(",instruction '")
					.append(getType().getOperationType().getName())
					.append("', operand '").append(instruction.getOperand())
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
		if (type.getOperationType().isNumeric() && criterionString.length() == 1 &&
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
		MagicOperation operation = MagicOperationFactory.createOperation(type.getOperationType(), criterionString);

		// Any remaining characters are the MagicFormat.
		startPos = findNonWhitespace(rawLine, endPos + 1);
		MagicMessage format;
		if (startPos >= 0) {
			format = MagicMessage.parse(type.getOperationType(), rawLine.substring(startPos));
		} else {
			format = MagicMessage.parse(type.getOperationType(), "");
		}

		MagicPattern magicPattern = new MagicPattern(level, offset, type, operation, format);
		operation.parse(magicPattern, criterionString);
		return magicPattern;
	}
}
