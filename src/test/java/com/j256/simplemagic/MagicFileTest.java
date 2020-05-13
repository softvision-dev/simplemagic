package com.j256.simplemagic;

import java.io.IOException;

import com.j256.simplemagic.error.ErrorCallBack;
import org.junit.Ignore;
import org.junit.Test;

public class MagicFileTest {

	@Test
	public void testMagicFileParse() throws IOException {
		new ContentInfoUtil("/magic.gz", new ErrorCallBack() {
			@Override
			public void error(String line, String details, Exception e) {
				throw new RuntimeException("Got this error: '" + details + "', on line: " + line, e);
			}
		});
	}
}
