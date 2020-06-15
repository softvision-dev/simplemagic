package com.j256.simplemagic.pattern.components.operation.criterion;

/**
 * The result summary of a {@link MagicCriterion#isMatch(byte[], int, boolean)} call.
 *
 * @param <VALUE_TYPE> The extracted value's type.
 */
public class MagicCriterionResult<VALUE_TYPE> {

	private final MagicCriterion<VALUE_TYPE> criterion;
	private final int nextReadOffset;
	private final VALUE_TYPE matchingValue;
	private final boolean match;

	/**
	 * Constructor to initialize a successfull {@link MagicCriterionResult} as a result of
	 * {@link MagicCriterion#isMatch(byte[], int, boolean)}.
	 *
	 * @param match          True, if the criterion is met.
	 * @param criterion      The {@link MagicCriterion}, that has been evaluated.
	 * @param nextReadOffset The suggested next read offset for following criterion evaluations.
	 */
	public MagicCriterionResult(boolean match, MagicCriterion<VALUE_TYPE> criterion, int nextReadOffset) {
		this.match = match;
		this.criterion = criterion;
		this.nextReadOffset = nextReadOffset;
		this.matchingValue = null;
	}

	/**
	 * Constructor to initialize a successfull {@link MagicCriterionResult} as a result of
	 * {@link MagicCriterion#isMatch(byte[], int, boolean)}.
	 *
	 * @param match          True, if the criterion is met.
	 * @param criterion      The {@link MagicCriterion}, that has been evaluated.
	 * @param nextReadOffset The suggested next read offset for following criterion evaluations.
	 * @param matchingValue  The value, that met the criterion.
	 */
	public MagicCriterionResult(boolean match, MagicCriterion<VALUE_TYPE> criterion, int nextReadOffset,
			VALUE_TYPE matchingValue) {
		this.match = match;
		this.criterion = criterion;
		this.nextReadOffset = nextReadOffset;
		this.matchingValue = matchingValue;
	}

	/**
	 * Returns the criterion, that caused this {@link MagicCriterionResult}.
	 *
	 * @return The criterion, that caused this {@link MagicCriterionResult}.
	 */
	public MagicCriterion<VALUE_TYPE> getCriterion() {
		return criterion;
	}

	/**
	 * Returns the value, that met the {@link MagicCriterion}.
	 *
	 * @return The value, that met the {@link MagicCriterion}. Returns null, if the criterion has not been met.
	 */
	public VALUE_TYPE getMatchingValue() {
		return matchingValue;
	}

	/**
	 * Suggests the next offset, that should be used to read further data. This assumes, that the read offset should
	 * skip all bytes, that have already been read and matched by a {@link MagicCriterion}.
	 *
	 * @return The next offset, that should be used to read further data.
	 */
	public int getNextReadOffset() {
		return nextReadOffset;
	}

	/**
	 * Returns true, if the causing {@link MagicCriterion} has been met.
	 *
	 * @return True, if the causing {@link MagicCriterion} has been met.
	 */
	public boolean isMatch() {
		return match;
	}
}
