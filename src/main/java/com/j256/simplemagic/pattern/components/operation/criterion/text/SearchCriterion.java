package com.j256.simplemagic.pattern.components.operation.criterion.text;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.components.operation.criterion.MagicCriterion;
import com.j256.simplemagic.pattern.components.operation.criterion.MagicCriterionResult;
import com.j256.simplemagic.pattern.components.operation.criterion.modifiers.TextCriterionModifiers;

/**
 * <b>Represents a Search criterion from a line in magic (5) format.</b>
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
 * A literal string search starting at the given offset. The same modifier flags can be used as for string patterns.
 * The search expression must contain the range in the form /number, that is the number of positions at which the match
 * will be attempted, starting from the start offset. This is suitable for searching larger binary expressions with
 * variable offsets, using \ escapes for special characters. The order of modifier and number is not relevant.
 * </i>
 * </p>
 */
public class SearchCriterion extends StringCriterion {

	private int range = 0;

	/**
	 * Creates a new {@link SearchCriterion} as found in a {@link MagicPattern}. The criterion shall define one Search
	 * evaluation contained by a pattern.
	 * <p>
	 * If the criterion is met for given binary data, this indicates the patterns type assumption to be at least
	 * partially correct. ({@link MagicCriterion#isMatch(byte[], int, boolean)})
	 * </p>
	 *
	 * @throws MagicPatternException Shall be thrown, if an invalid default operator has been set.
	 */
	public SearchCriterion() throws MagicPatternException {
	}

	/**
	 * Returns the range of this search criterion.
	 *
	 * @return The range of this search criterion.
	 */
	public int getRange() {
		return range;
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
	public MagicCriterionResult<String> isMatch(byte[] data, int currentReadOffset, boolean invertEndianness) {
		int range = getRange();

		// verify the starting offset and range
		if (currentReadOffset < 0 || data == null || range <= 0) {
			return new MagicCriterionResult<String>(false, this, currentReadOffset);
		}

		for (int i = 0; i <= range; i++) {
			MagicCriterionResult<String> result = super.isMatch(
					data, currentReadOffset + i, invertEndianness
			);
			if (result.isMatch()) {
				return result;
			}
		}

		return new MagicCriterionResult<String>(false, this, currentReadOffset);
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

		// The search expression must contain the range in the form /number, that is the number of positions at which
		// the match will be attempted, starting from the start offset.
		this.range = textFlagsAndModifiers.getNumericModifiers().isEmpty() ?
				0 : textFlagsAndModifiers.getNumericModifiers().get(0);

		// The same modifier flags can be used as for string patterns.
		if (!textFlagsAndModifiers.getCharacterModifiers().isEmpty()) {
			super.parseTypeAppendedModifiers(modifiers);
		}
	}
}
