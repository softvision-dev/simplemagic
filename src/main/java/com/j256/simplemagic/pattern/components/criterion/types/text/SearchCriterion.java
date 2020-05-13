package com.j256.simplemagic.pattern.components.criterion.types.text;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.components.MagicCriterion;
import com.j256.simplemagic.pattern.components.criterion.MagicCriterionResult;

/**
 * Represents a Search criterion from a line in magic (5) format.
 * <p>
 * From the magic(5) man page: A literal string search starting at the given line offset. The same modifier flags can be
 * used as for string patterns. The modifier flags (if any) must be followed by /number range, that is, the number of
 * positions at which the match will be attempted, starting from the start offset. This is suitable for searching larger
 * binary expressions with variable offsets, using \ escapes for special characters. The offset works as for regex.
 * </p>
 * <p>
 * <b>NOTE:</b> in our experience, the /number is _before_ the flags in 99% of the lines so that is how we implemented
 * it.
 * </p>
 *
 * @author graywatson
 */
public class SearchCriterion extends StringCriterion {

	/**
	 * Creates a new {@link SearchCriterion} as found in a {@link MagicPattern}. The criterion shall define one Search
	 * evaluation contained by a pattern.
	 * <p>
	 * If the criterion is met for given binary data, this indicates the patterns type assumption to be at least
	 * partially correct. ({@link MagicCriterion#isMatch(byte[], int)})
	 * </p>
	 *
	 * @throws MagicPatternException Shall be thrown, if an invalid default operator has been set.
	 */
	public SearchCriterion() throws MagicPatternException {
	}

	/**
	 * Shall evaluate the {@link MagicCriterion} for the given data and offset and shall return a {@link MagicCriterionResult}
	 * summarizing the evaluation results.
	 *
	 * @param data              The binary data, that shall be checked whether they match this criterion.
	 * @param currentReadOffset The initial offset in the given data.
	 * @return A {@link MagicCriterionResult} summarizing the evaluation results.
	 */
	@Override
	public MagicCriterionResult<String> isMatch(byte[] data, int currentReadOffset) {
		int maxSearchOffset = getMaxSearchOffset();
		if (isOptionalWhiteSpace()) {
			// we have to look at all of the bytes unfortunately
			maxSearchOffset = data.length;
		}
		// if offset is 1 then we need to pre-read 1 char
		int end = currentReadOffset + maxSearchOffset + getTestValue().length();
		if (end > data.length) {
			end = data.length;
		}
		for (int offset = currentReadOffset; offset < end; offset++) {
			MagicCriterionResult<String> match = findOffsetMatch(data, null, offset, data.length);
			if (match.isMatch()) {
				return match;
			}
		}
		return new MagicCriterionResult<String>(this, currentReadOffset);
	}
}
