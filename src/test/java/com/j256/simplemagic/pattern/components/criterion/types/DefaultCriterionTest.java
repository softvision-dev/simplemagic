package com.j256.simplemagic.pattern.components.criterion.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.extractor.types.DefaultExtractor;
import com.j256.simplemagic.pattern.formatter.types.DefaultFormatter;
import org.junit.Test;

public class DefaultCriterionTest {

	@Test
	public void testCoverage() throws MagicPatternException {
		DefaultCriterion defaultCriterion = new DefaultCriterion(false);
		defaultCriterion.parse(MagicPattern.parse("0 default x"), null);
		DefaultExtractor defaultExtractor = new DefaultExtractor();
		assertEquals("", defaultExtractor.extractValue(null, 0));
		assertTrue(defaultCriterion.isMatch(null, 0).isMatch());
		String str = "weofjwepfj";
		assertEquals(str, new DefaultFormatter("%s").format(str));
	}
}
