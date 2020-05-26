package com.j256.simplemagic.pattern.components.operation.criterion.types;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import com.j256.simplemagic.MagicEntries;
import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.matching.MatchingResult;
import com.j256.simplemagic.pattern.matching.MatchingState;
import org.junit.Test;

public class LocalDateCriterionTest {

	@Test
	public void testBasic() throws MagicPatternException, IOException {
		MagicPattern pattern = MagicPattern.parse(">0 ldate >0 %s");
		int secs = 1367982937;
		MatchingResult result = pattern.isMatch(integerToBytes(secs), 0, new MagicEntries());
		assertEquals(MatchingState.FULL_MATCH, result.getMatchingState());
		assertEquals("2017-09-21 11:41:37 +0200", result.toString());
	}

	private byte[] integerToBytes(int val) {
		byte[] bytes = new byte[4];
		for (int i = 0; i < bytes.length; i++) {
			byte b = (byte) (val % 256);
			bytes[bytes.length - 1 - i] = b;
			val /= 256;
		}
		return bytes;
	}
}
