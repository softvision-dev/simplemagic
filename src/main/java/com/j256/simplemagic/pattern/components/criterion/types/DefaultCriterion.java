package com.j256.simplemagic.pattern.components.criterion.types;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.MagicOperator;
import com.j256.simplemagic.pattern.components.criterion.AbstractMagicCriterion;
import com.j256.simplemagic.pattern.components.MagicCriterion;
import com.j256.simplemagic.pattern.components.criterion.MagicCriterionResult;

/**
 * Represents a Default criterion from a line in magic (5) format.
 * <p>
 * This is the default criterion, which is always returning true.
 * </p>
 * <p>
 * <b>WARNING:</b> The type "default" <b>is</b> used in the magic files.
 * </p>
 *
 * @author graywatson
 */
public class DefaultCriterion extends AbstractMagicCriterion<String> {

	private final boolean noopCriterion;

	/**
	 * Creates a new {@link DefaultCriterion} as found in a {@link MagicPattern}. The criterion shall define one Default
	 * evaluation contained by a pattern.
	 * <p>
	 * If the criterion is met for given binary data, this indicates the patterns type assumption to be at least
	 * partially correct. ({@link MagicCriterion#isMatch(byte[], int)})
	 * </p>
	 *
	 * @param noopCriterion Shall be set to true, if this Criterion is representing the "x" criterion and therefore
	 *                      should be skipped during evaluation.
	 * @throws MagicPatternException Shall be thrown, if an invalid default operator has been set.
	 */
	public DefaultCriterion(boolean noopCriterion) throws MagicPatternException {
		super(MagicOperator.EQUALS);
		this.noopCriterion = noopCriterion;
	}

	/**
	 * The test value of a default criterion is unused and always defaults to an empty String.
	 *
	 * @return An empty String for the default criterion.
	 */
	@Override
	public String getTestValue() {
		return "";
	}

	/**
	 * Returns true, if this Criterion is representing the "x" criterion and therefore should be skipped during
	 * evaluation.
	 *
	 * @return True if this Criterion is representing the no-op "x" criterion
	 */
	@Override
	public boolean isNoopCriterion() {
		return noopCriterion;
	}

	/**
	 * Always returns a successful {@link MagicCriterionResult} for the {@link DefaultCriterion}.
	 *
	 * @param data              The binary data, that shall be checked whether they match this criterion.
	 * @param currentReadOffset The initial offset in the given data.
	 * @return A {@link MagicCriterionResult} summarizing the evaluation results.
	 */
	@Override
	public MagicCriterionResult<String> isMatch(byte[] data, int currentReadOffset) {
		return new MagicCriterionResult<String>(this, currentReadOffset, "");
	}

	/**
	 * Parse the given raw definition to initialize this {@link MagicCriterion} instance.
	 *
	 * @param magicPattern  The pattern, this criterion is defined for. (For reflective access.) A 'null' value shall
	 *                      be treated as invalid.
	 * @param rawDefinition The raw definition of the {@link MagicCriterion} as a String.
	 * @throws MagicPatternException Shall be thrown, if the parsing failed.
	 */
	@Override
	public void parse(MagicPattern magicPattern, String rawDefinition) throws MagicPatternException {
		super.parse(magicPattern, rawDefinition);
	}

	/**
	 * Returns the characteristic starting bytes of this {@link MagicCriterion}. Allows for faster selection of
	 * relevant patterns.
	 *
	 * @return An array of the characteristic starting bytes of criterion, or null if such bytes can not be determined.
	 */
	@Override
	public byte[] getStartingBytes() {
		return null;
	}
}
