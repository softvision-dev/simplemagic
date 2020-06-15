package com.j256.simplemagic.pattern.components.operation.criterion.types;

import static org.junit.Assert.assertEquals;

import com.j256.simplemagic.MagicEntries;
import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.matching.MatchingResult;
import com.j256.simplemagic.pattern.matching.MatchingState;
import org.junit.Test;

import java.io.IOException;

public class LittleEndianString16CriterionTest {
	//TODO: rethink - reactivate
	/*
	@Test
	public void testStuff() {
		String16Extractor extractor = new String16Extractor(EndianType.LITTLE);
		byte[] bytes = new byte[]{1, 'a', 2, 'b'};
		char[] value = extractor.extractValue(bytes, 0, false);
		assertEquals("愁戂", new String(value));
	}*/

	@Test
	public void testMatch() throws MagicPatternException, IOException {
		MagicPattern magicPattern = MagicPattern.parse(">0 lestring16 愁戂");
		byte[] data = new byte[]{1, 'a', 2, 'b'};
		MatchingResult result = magicPattern.isMatch(data, 0, new MagicEntries());
		assertEquals(MatchingState.FULL_MATCH, result.getMatchingState());
	}
}
