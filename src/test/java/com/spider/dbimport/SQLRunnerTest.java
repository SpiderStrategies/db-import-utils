package com.spider.dbimport;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SQLRunnerTest {

	@Test
	public void test() throws Exception {
		String[] output = SQLRunner.determineSQLToRun("src/test/java/cleanDataTest.sql");
		String sql1 = "UPDATE sometable SET something = replace(something,'Mister ','Mr ') WHERE something LIKE 'Mister %'";
		assertEquals(sql1, output[0].trim());

		String sql2 = "UPDATE sometable\n" +
			"SET something = 'Brownstone'\n" +
			"WHERE something\n" +
			"LIKE 'Brwnstn %'";
		assertEquals(sql2, output[1].trim());
	}

}
