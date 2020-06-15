package com.j256.simplemagic.pattern.components.operation.criterion.modifiers;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents and identifies type appended numeric and character based modifiers for text criteria.
 */
public class TextCriterionModifiers {

	private static final char SEPARATOR = '/';
	private final List<Integer> numericModifiers = new ArrayList<Integer>();
	private final List<Character> characterFlags = new ArrayList<Character>();

	/**
	 * Identifies and splits a type appended modifier String to the individual numeric and character based modifiers
	 * for a Text criterion.
	 *
	 * @param flagsAndModifiers The String, that shall be analyzed.
	 */
	public TextCriterionModifiers(String flagsAndModifiers) {
		String trimmed;
		if (flagsAndModifiers == null || (trimmed = flagsAndModifiers.trim()).isEmpty()) {
			return;
		}
		StringBuilder collectedNumber = new StringBuilder();
		for (char ch : trimmed.toCharArray()) {
			if (Character.isDigit(ch)) {
				collectedNumber.append(ch);
			} else {
				if (collectedNumber.length() != 0) {
					numericModifiers.add(Integer.parseInt(collectedNumber.toString()));
					collectedNumber = new StringBuilder();
				}
				if (ch == SEPARATOR) {
					continue;
				}
				characterFlags.add(ch);
			}
		}
		if (collectedNumber.length() != 0) {
			numericModifiers.add(Integer.parseInt(collectedNumber.toString()));
		}
	}

	/**
	 * Returns the numeric modifiers.
	 *
	 * @return The numeric modifiers.
	 */
	public List<Integer> getNumericModifiers() {
		return numericModifiers;
	}

	/**
	 * Returns the character based modifiers.
	 *
	 * @return The character based modifiers.
	 */
	public List<Character> getCharacterModifiers() {
		return characterFlags;
	}

	/**
	 * Returns true, if the type appended String did not contain modifiers.
	 *
	 * @return True, if the type appended String did not contain modifiers.
	 */
	public boolean isEmpty() {
		return numericModifiers.isEmpty() && characterFlags.isEmpty();
	}
}
