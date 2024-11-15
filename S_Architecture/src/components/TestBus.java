package components;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestBus {

	@Test
	public void testPutGet() {
		Bus bus = new Bus();
		assertEquals(0, bus.get());
		bus.put(1);
		assertEquals(1, bus.get());
		bus.put(2);
		assertEquals(2, bus.get());
	}

}
