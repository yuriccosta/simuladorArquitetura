package components;

public class Bus {
	
	private int data;
	
	
	
	public Bus() {
		data = 0;
	}

	/**
	 * This method implements the storing of a data into the bus
	 * @param data
	 */
	public void put(int data){
		this.data = data;
	}
	
	/**
	 * This methos implements the retrieving of a data from the bus
	 * @return
	 */
	public int get() {
		return this.data;
		
	}

}
