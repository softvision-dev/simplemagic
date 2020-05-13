package com.j256.simplemagic.pattern.components.criterion;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.MagicOperator;
import com.j256.simplemagic.pattern.components.MagicCriterion;

/**
 * Extending classes represent a criterion definition from a line in magic (5) format.
 */
public abstract class AbstractMagicCriterion<VALUE_TYPE>
		implements MagicCriterion<VALUE_TYPE> {

	private MagicPattern magicPattern;
	private MagicOperator operator;

	/**
	 * Creates a new {@link MagicCriterion} as found in a {@link MagicPattern}. The criterion shall define one evaluation
	 * contained by a pattern.
	 * <p>
	 * If the criterion is met for given binary data, this indicates the patterns type assumption to be at least
	 * partially correct. ({@link MagicCriterion#isMatch(byte[], int)})
	 * </p>
	 *
	 * @param defaultOperator The default {@link MagicOperator} defining the operation, that must be successful, for this
	 *                        criterion	to be met. (might be replaced during parsing and must never be 'null'.)
	 * @throws MagicPatternException Shall be thrown, if an invalid default operator has been set.
	 */
	public AbstractMagicCriterion(MagicOperator defaultOperator) throws MagicPatternException {
		if (defaultOperator == null) {
			throw new MagicPatternException("Invalid criterion initialization.");
		}
		this.operator = defaultOperator;
	}

	/**
	 * Returns the {@link MagicOperator} defining the operation, that must be successful, for this criterion	to be met.
	 *
	 * @return The {@link MagicOperator} defining the operation, that must be successful, for this criterion	to be met.
	 * (Must never return 'null'.)
	 */
	@Override
	public MagicOperator getOperator() {
		return operator;
	}

	/**
	 * Returns true, if this criterion does not represent a matchable operation and should be skipped during evaluation.
	 *
	 * @return True, this criterion does not represent a matchable operation.
	 */
	@Override
	public boolean isNoopCriterion() {
		return false;
	}

	/**
	 * Sets the {@link MagicOperator} defining the operation, that must be successful, for this criterion to be met.
	 *
	 * @param operator The {@link MagicOperator} defining the operation, that must be successful, for this criterion to be met.
	 * @throws MagicPatternException Shall be thrown, if an invalid default operator has been set.
	 */
	public void setOperator(MagicOperator operator) throws MagicPatternException {
		if (operator == null) {
			throw new MagicPatternException("Invalid criterion initialization.");
		}
		this.operator = operator;
	}

	/**
	 * Returns the {@link MagicPattern}, that is defining this {@link MagicCriterion} for reflective access.
	 *
	 * @return The {@link MagicPattern}, that is defining this {@link MagicCriterion} for reflective access.
	 */
	public MagicPattern getMagicPattern() throws MagicPatternException {
		if (this.magicPattern == null) {
			throw new MagicPatternException("missing magic pattern for criterion.");
		}
		return magicPattern;
	}

	/**
	 * Sets the {@link MagicPattern}, that is defining this {@link MagicCriterion}.
	 *
	 * @param magicPattern The {@link MagicPattern}, that is defining this {@link MagicCriterion}. A 'null' value will
	 *                     be treated as invalid.
	 * @throws MagicPatternException Shall be thrown for invalid parameters.
	 */
	protected void setMagicPattern(MagicPattern magicPattern) throws MagicPatternException {
		if (magicPattern == null) {
			throw new MagicPatternException("Invalid criterion initialization.");
		}
		this.magicPattern = magicPattern;
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
		setMagicPattern(magicPattern);
	}
}
