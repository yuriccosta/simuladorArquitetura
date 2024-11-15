package components;

public class Ula {
	
	private Bus intBus;
	private Bus extBus1;
	private Bus extBus2;
	private Register reg1;
	private Register reg2;
	
	
	public Ula(Bus extBus1, Bus extBus2) {
		super();
		this.extBus1 = extBus1;
		this.extBus2 = extBus2;
		intBus = new Bus();
		reg1 = new Register("UlaReg0", extBus1, intBus);
		reg2 = new Register("UlaReg1", extBus1, intBus);
	}

	/**
	 * This method adds the reg1 and reg2 values, storing the result in reg2.
	 */
	public void add() {
		int res=0;
		intBus.put(0);
		reg1.internalRead(); //puts its data into the internal bus
		res = intBus.get(); //stored for operation
		reg2.internalRead(); //puts the internal data into the internal bus
		res += intBus.get(); //the operation was performed
		intBus.put(res);
		reg2.internalStore(); //saves the result into internal store
	}
	
	/**
	 * This method sub the reg2 value from reg1 value, storing the result in reg2
	 * This processing uses a Ula's internal bus
	 */
	public void sub() {
				
		int res=0;
		intBus.put(0);
		reg1.internalRead(); //puts its data into the internal bus
		res = intBus.get(); //stored for operation
		reg2.internalRead(); //puts the internal data into the internal bus
		res -= intBus.get(); //the operation was performed
		intBus.put(res);
		reg2.internalStore(); //saves the result into internal store
		
	}
	
	/**
	 * This method increments by 1 the value stored into reg2
	 */
	public void inc() {
		
		reg2.internalRead();
		int res = intBus.get();
		res ++;
		intBus.put(res);
		reg2.internalStore();
		
	}
	
	/**
	 * This method stores the value found in the external bus into the #reg
	 * @param reg
	 */
	public void store(int reg) {
		if (reg==0)
			reg1.store();
		else
			reg2.store();
	}
	
	/**
	 * This method reads the value from #reg stores it into the external bus
	 * @param reg
	 */
	public void read (int reg) {
		if (reg==0)
			reg1.read();
		else
			reg2.read();
	}
	
	/**
	 * This method stores the value found in the internal bus into the #reg
	 * @param reg
	 */
	public void internalStore(int reg) {
		extBus1.put(extBus2.get()); //moving the data from a bus to another
		//inserting the data in the correct register
		if (reg==0)
			reg1.store();
		else
			reg2.store();
	}
	
	/**
	 * This method reads the value from #reg stores it into the internal bus
	 * @param reg
	 */
	public void internalRead (int reg) {
		if (reg==0)
			reg1.read();
		else
			reg2.read();
		extBus2.put(extBus1.get()); //moving the data from a bus to another
	}
	
	
}
