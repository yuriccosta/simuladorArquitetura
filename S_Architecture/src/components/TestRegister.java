package components;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestRegister {

	@Test
	public void testGetSetBit() {
		Bus bus = new Bus();
		Register flags = new Register(5, bus);
		//testing if all the bits were started with 0
		for (int i=0;i<5;i++) {
			flags.getBit(i);
			assertEquals(0, bus.get());
		}
		//setting the sequence 11011 in the bits
		flags.setBit(0,1);
		flags.setBit(1,1);
		flags.setBit(2,0);
		flags.setBit(3,1);
		flags.setBit(4,1);
		//now checking if the bits are the same
		assertEquals(1, flags.getBit(4));
		assertEquals(1, flags.getBit(3));
		assertEquals(0, flags.getBit(2));
		assertEquals(1, flags.getBit(1));
		assertEquals(1, flags.getBit(0));
	}

	@Test
	public void testReadStore() {
		Bus bus = new Bus();
		Bus intbus = new Bus();
		Register reg = new Register("RPG", bus, intbus);
		
		//testing if the register, just after being created, stores 0
		reg.read();
		assertEquals(0, bus.get());
		
		//testing the storing and reading of some numbers
		bus.put(10);
		reg.store();
		bus.put(0); //destroying the old value
		reg.read();
		assertEquals(10, bus.get());
		
		bus.put(20);
		reg.store();
		bus.put(0); //destroying the old value
		reg.read();
		assertEquals(20, bus.get());
		
		bus.put(1);
		reg.store();
		bus.put(0); //destroying the old value
		reg.read();
		assertEquals(1, bus.get());
	}
	
	@Test
	public void testInternalReadStore() {
		Bus bus = new Bus();
		Bus intbus = new Bus();
		Register reg = new Register("RPG", bus, intbus);
		
		//testing if the register, just after being created, stores 0
		reg.internalRead();
		assertEquals(0, intbus.get());
		
		//testing the storing and reading of some numbers
		intbus.put(10);
		reg.internalStore();
		intbus.put(0); //destroying the old value
		reg.internalRead();
		assertEquals(10, intbus.get());
		
		intbus.put(20);
		reg.internalStore();
		intbus.put(0); //destroying the old value
		reg.internalRead();
		assertEquals(20, intbus.get());
		
		intbus.put(1);
		reg.internalStore();
		intbus.put(0); //destroying the old value
		reg.internalRead();
		assertEquals(1, intbus.get());
	}


}
