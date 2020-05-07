package com.j256.simplemagic.pattern;

import com.j256.simplemagic.error.MagicPatternException;
import org.junit.Test;

import static org.junit.Assert.*;

public class MagicPatternTest {

	private void assertNoException(String rawLine) {
		try {
			MagicPattern.parse(rawLine);
		} catch (MagicPatternException ex) {
			fail("No Exception should have been caused!");
		}
	}

	private void assertException(String rawLine, String expectedMessage) {
		MagicPatternException exception = null;
		// no previous line
		try {
			MagicPattern.parse(rawLine);
		} catch (MagicPatternException ex) {
			assertEquals(expectedMessage, ex.getMessage());
			exception = ex;
		}
		assertNotNull(exception);
	}

	@Test
	public void testCoverage() {
		// no previous line
		assertException("!:stuff", "Magic pattern level and offset not found.");

		// no non-whitespace
		assertException("            ", "Magic pattern line is empty.");

		// 0 level
		assertException(">0   ", "Magic pattern type not found.");

		// no whitespace
		assertException("100", "Magic pattern level and offset not found.");

		// no pattern
		assertException(">1   ", "Magic pattern type not found.");

		// no type
		assertException(">1   wow", "Magic pattern is incomplete and ending after type definition.");

		// no value
		assertException(">1   wow     ", "unknown magic type string: wow");
	}

	@Test
	public void testBadLevel() {
		// no level number
		assertException(">   string   SONG   Format", "Invalid/unknown offset pattern: 'string'");
		// level not a number
		assertException(">a   string   SONG   Format", "Invalid/unknown offset pattern: 'a'");
	}

	@Test
	public void testTypeString() {
		// & part not a number
		assertException(">1   short&a    Format", "Invalid type AND-number: a");

		// no type string
		assertException(">1   &0    Format", "Blank type string.");

		// unknown matcher
		assertException(">1   unknowntype    Format", "unknown magic type string: unknowntype");
	}

	@Test
	public void testValue() {
		// value not a number
		assertException(">0    byte     =z     format", "Could not parse number from: 'z'");
	}

	@Test
	public void testSpecial() {
		assertNoException("0   string   SONG   Format");

		// no whitespace
		assertException("!:", "Magic pattern level and offset not found.");

		// no value after whitespace
		assertException("!:    ", "Invalid/unknown offset pattern: '!:'");
	}

	@Test
	public void testBigIntegerHexValues() {
		//Ensure usage of BigInteger does not fail for Type AND-number.
		assertNoException(">0x01C\tulequad&0xFFFFFFFCFFFFFFFC\t=0x0000000000000000");

		//Ensure usage of BigInteger does not fail for testValue parsing.
		assertNoException(">>5\tlequad\t\t!0xffffffffffffffff\tnon-streamed, size %lld");
	}

	@Test
	public void testTestValueContainsWhitespace() {
		//EQUALS operator followed by whitespace and operand shall be valid
		assertNoException(">>>>>>0\tubyte\t\t\t= 10\tInfocom (Z-machine %d");

		//NOT-EQUALS operator followed by whitespace and operand shall be valid
		assertNoException(">>>>>>0\tubyte\t\t\t! 10\tInfocom (Z-machine %d");

		//GREATER-THAN operator followed by whitespace and operand shall be valid
		assertNoException(">>>>>>0\tubyte\t\t\t> 10\tInfocom (Z-machine %d");

		//LESS-THAN operator followed by whitespace and operand shall be valid
		assertNoException(">>>>>>0\tubyte\t\t\t< 10\tInfocom (Z-machine %d");

		//AND-ALL-SET operator followed by whitespace and operand shall be valid
		assertNoException(">>>>>>0\tubyte\t\t\t& 10\tInfocom (Z-machine %d");

		//AND-ALL-CLEARED operator followed by whitespace and operand shall be valid
		assertNoException(">>>>>>0\tubyte\t\t\t^ 10\tInfocom (Z-machine %d");

		//NEGATE operator followed by whitespace and operand shall be valid
		assertNoException(">>>>>>0\tubyte\t\t\t~ 10\tInfocom (Z-machine %d");

		//UNKNOWN operator followed by whitespace and operand shall be invalid
		assertException(">>>>>>0\tubyte\t\t\t. 10\tInfocom (Z-machine %d", "Could not parse number from: '.'");
	}

	@Test
	public void testParseXCriterium() {
		assertNoException(">4 long x");
	}

	@Test
	public void testParseOffsetWhitespace() {
		assertNoException("> 4 long 4");
	}
}
