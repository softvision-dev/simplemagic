package com.j256.simplemagic.pattern.components.operation.criterion.types;

import com.j256.simplemagic.MagicEntries;
import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.matching.MatchingResult;
import com.j256.simplemagic.pattern.matching.MatchingState;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class BigEndianString16CriterionTest {

	@Test
	public void testMatch() throws MagicPatternException, IOException {
		MagicPattern magicPattern = MagicPattern.parse(">0 bestring16 šɢ");
		byte[] data = new byte[]{1, 'a', 2, 'b'};
		MatchingResult result = magicPattern.isMatch(data, 0, new MagicEntries());
		assertEquals(MatchingState.FULL_MATCH, result.getMatchingState());
	}
}
