package components;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestUla {


	@Test
	public void testInternalExternalStoreRead() {
		Bus bus = new Bus();
		Bus bus2 = new Bus();
		Ula ula = new Ula(bus, bus2);
	
		//testing if both internal registers were started with zero
		bus.put(-1); //the value into the bus was not zero
		ula.read(0);
		assertEquals(0, bus.get());
		
		//external buses

				//storing data in reg1 and reg2
				bus.put(3);
				ula.store(0); //3 is in reg1
				bus.put(6);
				ula.store(1); //6 is in reg2
				bus.put(100); //the value into the bus is 100
				ula.read(1);
				assertEquals(6, bus.get());
				ula.read(0);
				assertEquals(3, bus.get());
				
		//internal buses
				
				//storing data in reg1 and reg2
				bus2.put(3);
				ula.internalStore(0);; //3 is in reg1
				bus2.put(6);
				ula.internalStore(1); //6 is in reg2
				bus2.put(100); //the value into the bus2 is 100
				ula.internalRead(1);
				assertEquals(6, bus2.get());
				ula.internalRead(0);
				assertEquals(3, bus2.get());
	}


	@Test
	public void testAdd() {
		Bus bus = new Bus();
		Bus bus2 = new Bus();
		Ula ula = new Ula(bus, bus2);
		bus.put(3);
		ula.store(0); //3 is in reg1
		bus.put(6);
		ula.store(1); //6 is in reg2
		ula.add(); //now we must have 9 in reg2
		ula.read(1);
		assertEquals(9, bus.get());
		
		bus.put(-9);
		ula.store(0); //-9 is in reg1
		bus.put(6);
		ula.store(1); //6 is in reg2
		ula.add(); //now we must have -3 in reg2
		ula.read(1);
		assertEquals(-3, bus.get());
		
		bus.put(3);
		ula.store(0); //3 is in reg1
		bus.put(-3);
		ula.store(1); //-3 is in reg2
		ula.add(); //now we must have 0 in reg2
		ula.read(1);
		assertEquals(0, bus.get());
	}

	@Test
	public void testSub() {
		Bus bus = new Bus();
		Bus bus2 = new Bus();
		Ula ula = new Ula(bus, bus2);
		bus.put(9);
		ula.store(0); //9 is in reg1
		bus.put(6);
		ula.store(1); //6 is in reg2
		ula.sub(); //now we must have 9-6 = 3 in reg2
		ula.read(1);
		assertEquals(3, bus.get());
		
		bus.put(6);
		ula.store(0); //6 is in reg1
		bus.put(10);
		ula.store(1); //10 is in reg2
		ula.sub(); //now we must have 6-10 = -4 in reg2
		ula.read(1);
		assertEquals(-4, bus.get());
		
		bus.put(10);
		ula.store(0); //10 is in reg1
		bus.put(10);
		ula.store(1); //10 is in reg2
		ula.sub(); //now we must have 10-10 = 0 in reg2
		ula.read(1);
		assertEquals(0, bus.get());
	}
	
	@Test
	public void testInc() {
		Bus bus = new Bus();
		Bus bus2 = new Bus();
		Ula ula = new Ula(bus, bus2);
		bus.put(9);
		ula.store(1); //9 is in reg2
		ula.inc(); //now we must have 9+1 = 10 in reg2
		ula.read(1);
		assertEquals(10, bus.get());
		
		bus.put(-10);
		ula.store(1); //-10 is in reg2
		ula.inc(); //now we must have -10+1 = -9 in reg2
		ula.read(1);
		assertEquals(-9, bus.get());
		
		bus.put(-1);
		ula.store(1); //-1 is in reg1
		ula.inc(); //now we must have -1+1 = 0 in reg2
		ula.read(1);
		assertEquals(0, bus.get());
	}
}
