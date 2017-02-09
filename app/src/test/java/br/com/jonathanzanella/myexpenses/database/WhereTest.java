package br.com.jonathanzanella.myexpenses.database;

import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

/**
 * Created by jzanella on 11/12/16.
 */
public class WhereTest {

	@Test
	public void generate_simple_query_successfully() throws Exception {
		Where where = new Where(Fields.ID).eq(1L);
		Select expected = new Select("id = ?", new String[]{"1"});
		Select query = where.query();
		assertEquals(query, expected);
	}

	@Test
	public void generate_query_with_and_successfully() throws Exception {
		Where where = new Where(Fields.ID).eq(1L).and(Fields.NAME).eq("John Doe");
		Select expected = new Select("id = ? and name = ?", new String[] {"1", "John Doe"});
		Select query = where.query();
		assertEquals(query, expected);
	}

	@Test
	public void generate_query_with_in() throws Exception {
		Where where = new Where(Fields.NAME).queryIn(Arrays.asList("John Doe", "John Doe2"));
		Select expected = new Select("name in (?,?)", new String[] {"John Doe", "John Doe2"});
		Select query = where.query();
		assertEquals(query, expected);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void throw_exception_with_two_fields_added() throws Exception {
		new Where(Fields.ID).and(Fields.NAME);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void throw_exception_with_two_values_added() throws Exception {
		new Where(Fields.ID).eq("").eq("");
	}

	@Test(expected = UnsupportedOperationException.class)
	public void throw_exception_without_values_added() throws Exception {
		new Where(Fields.ID).query();
	}
}