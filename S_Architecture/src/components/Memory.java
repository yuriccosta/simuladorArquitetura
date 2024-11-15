package components;

public class Memory {
	
	private Bus bus;
	private int storePosition; //this value indicates that the memory has read an
					// address and is waiting for a data to be storesd in this position
	private int size;
	private int dataList[];
	
	public Memory(int size, Bus bus) {
		storePosition = -1; //negative values indicates the memory is not storing
		this.size = size;
		dataList = new int[size];
		this.bus = bus;
		for (int i=0;i<size;i++) {
			dataList[i] = 0;
		}
	}

	/**
	 * This method is used for TDD and Simulation purposes only
	 * NOT TESTED
	 * @return
	 */
	public int[] getDataList() {
		return dataList;
	}

	/**
	 * This method stores into position the data found in the bus
	 * @param position
	 */
	public void store() {
		if (storePosition < 0) { //the storing is just starting
			this.storePosition = bus.get();
		}
		else {//the storing was initiated, in the bus is the data
			this.dataList[storePosition] = bus.get();
			storePosition = -1; //no storing is being performed anymore
		}
	}
	
	/**
	 * This method gets the data from the position and stores it into the bus
	 * @param position
	 */
	public void read() {
		if ((bus.get() < size)&&(bus.get() >=0))
			bus.put(dataList[bus.get()]);
	}
	
	/**
	 * Special method used in statusm memory to store the data in the position 0
	 */
	public void storeIn0() { 
		this.dataList[0] = bus.get();
	}

	/**
	 * Special method used in statusm memory to store the data in the position 1
	 */
	public void storeIn1() { 
		this.dataList[1] = bus.get();
	}

}
