package com.j256.simplemagic.pattern.components.operation.criterion.types;

import static org.junit.Assert.assertEquals;

import com.j256.simplemagic.MagicEntries;
import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.matching.MatchingResult;
import com.j256.simplemagic.pattern.matching.MatchingState;
import org.junit.Test;

import java.io.IOException;

public class UtcDateTypeTest {

	@Test
	public void testBasic() throws MagicPatternException, IOException {
		MagicPattern magicPattern = MagicPattern.parse("0 beldate >0 %s");
		int secs = 1367982937;
		MatchingResult result = magicPattern.isMatch(integerToBytes(secs), 0, new MagicEntries());
		assertEquals(MatchingState.FULL_MATCH, result.getMatchingState());
		assertEquals("2013-05-08 05:15:37 +0200", result.toString());
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
