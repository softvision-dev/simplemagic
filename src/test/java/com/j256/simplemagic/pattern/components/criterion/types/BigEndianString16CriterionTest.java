package com.j256.simplemagic.pattern.components.criterion.types;

import com.j256.simplemagic.endian.EndianType;
import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.components.extractor.types.String16Extractor;
import com.j256.simplemagic.pattern.matching.MatchingResult;
import com.j256.simplemagic.pattern.matching.MatchingState;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class BigEndianString16CriterionTest {

	@Test
	public void testStuff() {
		String16Extractor extractor = new String16Extractor(EndianType.BIG);
		byte[] bytes = new byte[]{1, 'a', 2, 'b'};
		char[] value = extractor.extractValue(bytes, 0);
		assertEquals("šɢ", new String(value));
	}

	@Test
	public void testMatch() throws MagicPatternException, IOException {
		MagicPattern magicPattern = MagicPattern.parse(">0 bestring16 šɢ");
		byte[] data = new byte[]{1, 'a', 2, 'b'};
		MatchingResult result = magicPattern.isMatch(data);
		assertEquals(MatchingState.FULL_MATCH, result.getMatchingState());
	}
}
