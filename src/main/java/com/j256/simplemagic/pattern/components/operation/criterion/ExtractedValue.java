package com.j256.simplemagic.pattern.components.operation.criterion;

/**
 * This class is used to wrap extracted values and store additional extraction information.
 *
 * @param <T_VALUE> The type of value, that is extracted.
 */
public class ExtractedValue<T_VALUE> {
	private final T_VALUE value;
	private final int suggestedNextReadOffset;

	/**
	 * Wraps an extracted value with a specific read byte length.
	 *
	 * @param value      The extracted value.
	 * @param suggestedNextReadOffset The suggested next read offset.
	 */
	public ExtractedValue(T_VALUE value, int suggestedNextReadOffset) {
		this.value = value;
		this.suggestedNextReadOffset = suggestedNextReadOffset;
	}

	/**
	 * Return the wrapped extracted value.
	 *
	 * @return The extracted value.
	 */
	public T_VALUE getValue() {
		return value;
	}

	/**
	 * Returns the suggested next read offset.
	 *
	 * @return The suggested next read offset.
	 */
	public int getSuggestedNextReadOffset() {
		return suggestedNextReadOffset;
	}
}
