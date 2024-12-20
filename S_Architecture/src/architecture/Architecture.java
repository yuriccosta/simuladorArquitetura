package architecture;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.print.DocFlavor.READER;
import javax.print.DocFlavor.READER;

import components.Bus;
import components.Demux;
import components.Memory;
import components.Register;
import components.Ula;

public class Architecture {

	private boolean simulation; // this boolean indicates if the execution is done in simulation mode.
								// simulation mode shows the components' status after each instruction

	private boolean halt;
	private Bus extbus1;
	private Bus intbus1;
	private Bus intbus2;
	private Bus flagsBus;
	private Memory memory;
	private Memory statusMemory;
	private int memorySize;
	private Register PC;
	private Register IR;
	private Register REG0;
	private Register REG1;
	private Register REG2;
	private Register REG3;
	private Register StackTop;
	private Register StackBottom;
	private Register Flags;
	private Ula ula;
	private Demux demux; // only for multiple register purposes

	private ArrayList<String> commandsList;
	private ArrayList<Register> registersList;

	/**
	 * Instanciates all components in this architecture
	 */
	private void componentsInstances() {
		// don't forget the instantiation order
		// buses -> registers -> ula -> memory
		extbus1 = new Bus();
		intbus1 = new Bus();
		intbus2 = new Bus();
		PC = new Register("PC", extbus1, intbus1);
		IR = new Register("IR", intbus1, intbus1);
		REG0 = new Register("REG0", extbus1, intbus2);
		REG1 = new Register("REG1", extbus1, intbus2);
		REG2 = new Register("REG2", extbus1, intbus2);
		REG3 = new Register("REG3", extbus1, intbus2);
		StackTop = new Register("StackTop", intbus1, intbus1);
		StackBottom = new Register("StackBottom", intbus1, intbus1);
		Flags = new Register(2, flagsBus);
		fillRegistersList();
		ula = new Ula(intbus1, intbus2);
		statusMemory = new Memory(2, extbus1);
		memorySize = 136;
		memory = new Memory(memorySize, extbus1);
		demux = new Demux(); // this bus is used only for multiple register operations

		fillCommandsList();
	}

	/**
	 * This method fills the registers list inserting into them all the registers we
	 * have.
	 * IMPORTANT!
	 * The first register to be inserted must be the default RPG
	 */
	private void fillRegistersList() {
		registersList = new ArrayList<Register>();
		registersList.add(REG0);
		registersList.add(REG1);
		registersList.add(REG2);
		registersList.add(REG3);
		registersList.add(PC);
		registersList.add(IR);
		registersList.add(Flags);
		registersList.add(StackTop);
		registersList.add(StackBottom);
	}

	/**
	 * Constructor that instanciates all components according the architecture
	 * diagram
	 */
	public Architecture() {
		componentsInstances();

		// by default, the execution method is never simulation mode
		simulation = false;
	}

	public Architecture(boolean sim) {
		componentsInstances();

		// in this constructor we can set the simoualtion mode on or off
		simulation = sim;
	}

	// getters

	protected Bus getExtbus1() {
		return extbus1;
	}

	protected Bus getIntbus1() {
		return intbus1;
	}

	protected Bus getIntbus2() {
		return intbus2;
	}

	protected Memory getMemory() {
		return memory;
	}

	protected Register getPC() {
		return PC;
	}

	protected Register getIR() {
		return IR;
	}

	protected Register getREG0() {
		return REG0;
	}

	protected Register getREG1() {
		return REG1;
	}

	protected Register getREG2() {
		return REG2;
	}

	protected Register getREG3() {
		return REG3;
	}

	protected Register getFlags() {
		return Flags;
	}

	protected Ula getUla() {
		return ula;
	}

	protected Demux getDemux() {
		return demux;
	}

	public ArrayList<String> getCommandsList() {
		return commandsList;
	}

	// all the microprograms must be impemented here
	// the instructions table is
	/*
	 *
	 * add addr (rpg <- rpg + addr)
	 * sub addr (rpg <- rpg - addr)
	 * jmp addr (pc <- addr)
	 * jz addr (se bitZero pc <- addr)
	 * jn addr (se bitneg pc <- addr)
	 * read addr (rpg <- addr)
	 * store addr (addr <- rpg)
	 * ldi x (rpg <- x. x must be an integer)
	 * inc (rpg++)
	 * move regA regB (regA <- regB)
	 */

	/**
	 * This method fills the commands list arraylist with all commands used in this
	 * architecture
	 */
	protected void fillCommandsList() {
		commandsList = new ArrayList<String>();
		commandsList.add("addRegReg"); // 0
		commandsList.add("addMemReg"); // 1
		commandsList.add("addRegMem"); // 2
		commandsList.add("subRegReg"); // 3
		commandsList.add("subMemReg"); // 4
		commandsList.add("subRegMem"); // 5
		commandsList.add("imulMemReg"); // 6
		commandsList.add("imulRegMem"); // 7
		commandsList.add("imulRegReg"); // 8
		commandsList.add("moveMemReg"); // 9
		commandsList.add("moveRegMem"); // 10
		commandsList.add("moveRegReg"); // 11
		commandsList.add("moveImmReg"); // 12
		commandsList.add("incReg"); // 13
		commandsList.add("incMem"); // 14
		commandsList.add("jmp"); // 15
		commandsList.add("jn"); // 16
		commandsList.add("jz"); // 17
		commandsList.add("jnz"); // 18
		commandsList.add("jeq"); // 19
		commandsList.add("jgt"); // 20
		commandsList.add("jlw"); // 21
	}

	/**
	 * This method is used after some ULA operations, setting the flags bits
	 * according the result.
	 * 
	 * @param result is the result of the operation
	 *               NOT TESTED!!!!!!!
	 */
	public void setStatusFlags(int result) {
		Flags.setBit(0, 0);
		Flags.setBit(1, 0);
		if (result == 0) { // bit 0 in flags must be 1 in this case
			Flags.setBit(0, 1);
		}
		if (result < 0) { // bit 1 in flags must be 1 in this case
			Flags.setBit(1, 1);
		}
	}

	/**
	 * This method implements imulMemReg microprogram
	 */
	public void imulMemReg() {
		
		// PC++
		PC.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore(); // now PC points to the parameter address

		// Gravar os valores dos registradores na memória
		memory.getDataList()[80] = REG0.getData();
		memory.getDataList()[81] = REG1.getData();
		memory.getDataList()[82] = REG2.getData();
		memory.getDataList()[83] = REG3.getData();

		
		// Move Mem Reg0
		PC.read();
		memory.read();
		memory.getDataList()[87] = 9; // move
		memory.getDataList()[88] = extbus1.get(); // mem
		memory.getDataList()[89] = 0; // reg0
				
		//PC++
		PC.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore(); // now PC points to the parameter address

		//Move RegA Reg2
		PC.read();
		memory.getDataList()[77] = extbus1.get(); // regA
		memory.read();
		memory.getDataList()[84] = 11; // move
		memory.getDataList()[85] = extbus1.get(); // regA
		memory.getDataList()[86] = 2; // reg2


		//MOVE 0 REG3
		memory.getDataList()[90] = 12; // move
		memory.getDataList()[91] = 0; // immediate
		memory.getDataList()[92] = 3; // reg3

		// MOVE 1 REG1
		memory.getDataList()[93] = 12; // move
		memory.getDataList()[94] = 1; // immediate
		memory.getDataList()[95] = 1; // reg1

		//ADD REG0 REG3
		memory.getDataList()[96] = 0; // add
		memory.getDataList()[97] = 0; // reg0
		memory.getDataList()[98] = 3; // reg3

		//SUB REG2 REG1
		memory.getDataList()[99] = 3; // sub
		memory.getDataList()[100] = 2; // reg2
		memory.getDataList()[101] = 1; // reg1

		//MOVE REG1 REG2
		memory.getDataList()[102] = 11; // move
		memory.getDataList()[103] = 1; // reg1
		memory.getDataList()[104] = 2; // reg2

		//JZ LOOP
		memory.getDataList()[105] = 17; // jz
		memory.getDataList()[106] = 109; // Guardando o endereço da memória

		//JMP LOOP
		memory.getDataList()[107] = 15; // jmp
		memory.getDataList()[108] = 93; // laço

		//Guarda resultado da multiplicação na memória
		memory.getDataList()[109] = 10; // move
		memory.getDataList()[110] = 3; // reg3
		memory.getDataList()[111] = 79; // mem

		//Recuperando os dados dos registradores
		memory.getDataList()[112] = 9; // move
		memory.getDataList()[113] = 80; // mem
		memory.getDataList()[114] = 0; // reg0

		memory.getDataList()[115] = 9; // move
		memory.getDataList()[116] = 81; // mem
		memory.getDataList()[117] = 1; // reg1

		memory.getDataList()[118] = 9; // move
		memory.getDataList()[119] = 82; // mem
		memory.getDataList()[120] = 2; // reg2

		memory.getDataList()[121] = 9; // move
		memory.getDataList()[122] = 83; // mem
		memory.getDataList()[123] = 3; // reg3

		//Recuperamos o resultado da multiplicação no registrador certo
		memory.getDataList()[124] = 9; // move
		memory.getDataList()[125] = 79; // mem
		extbus1.put(77);
		memory.read();
		memory.read();
		memory.getDataList()[126] = extbus1.get() ; // regA

		// PC++
		PC.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore(); // now PC points to the parameter address
		PC.read();

		// JMP para a próxima instrução
		memory.getDataList()[127] = 15; // jmp
		memory.getDataList()[128] = extbus1.get(); // Para a próxima instrução

		// Guarda o endereço da próxima instrução
		extbus1.put(84);
		PC.store();
		
	}

	/**
	 * This method implements imulRegMem microprogram
	 */
	public void imulRegMem() {
		
		// PC++
		PC.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore(); // now PC points to the parameter address

		// Gravar os valores dos registradores na memória
		memory.getDataList()[80] = REG0.getData();
		memory.getDataList()[81] = REG1.getData();
		memory.getDataList()[82] = REG2.getData();
		memory.getDataList()[83] = REG3.getData();

		// Move RegA Reg0
		// Agora colocamos moveRegAReg na memória, onde RegA é o que esta no parametro
		memory.getDataList()[84] = 11; // move
		// Pegamos o id do Registrador do parametro e colocamos na memória
		PC.read();
		memory.read();
		memory.getDataList()[85] = extbus1.get(); // regA
		memory.getDataList()[86] = 0; // reg0

		//MOVE 0 REG3
		memory.getDataList()[87] = 12; // move
		memory.getDataList()[88] = 0; // immediate
		memory.getDataList()[89] = 3; // reg3

		// ADD REG0 REG3
		// Agora o endereço do laço que começa no addRegReg
		memory.getDataList()[90] = 0; // add
		memory.getDataList()[91] = 0; // reg0
		memory.getDataList()[92] = 3; // reg3

		// MOVE 1 REG1
		memory.getDataList()[93] = 12; // move
		memory.getDataList()[94] = 1; // immediate
		memory.getDataList()[95] = 1; // reg1

		// PC++
		PC.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore(); // now PC points to the parameter address

		// SUB REG2 REG1
		// Agora colocamos o valor de Mem no reg2
		PC.read();
		memory.read();
		memory.getDataList()[78] = extbus1.get(); // Guardando o endereço da memória
		demux.setValue(2);
		memory.read();
		registersStore();

		System.out.println("REG1: " + REG1.getData());
		System.out.println("REG2: " + REG2.getData());
		// Fazemos o sub reg2 reg1, onde reg1 = 1
		memory.getDataList()[96] = 3; // sub
		memory.getDataList()[97] = 2; // reg2
		memory.getDataList()[98] = 1; // reg1

		// MOVE REG1 REG2
		memory.getDataList()[99] = 11; // move
		memory.getDataList()[100] = 1; // reg1
		memory.getDataList()[101] = 2; // reg2


		memory.getDataList()[102] = 17; // jz
		memory.getDataList()[103] = 106; // Guardando o endereço da memória

		// Agora colocamos o jmp para o laço
		memory.getDataList()[104] = 15; // jmp
		memory.getDataList()[105] = 90; // laço

		// Guarda resultado da multiplicação na memória
		memory.getDataList()[106] = 10; // move
		memory.getDataList()[107] = 3; // reg3
		memory.getDataList()[108] = memory.getDataList()[78]; // mem

		// Recuperando os dados dos registradores
		memory.getDataList()[109] = 9; // move
		memory.getDataList()[110] = 80; // mem
		memory.getDataList()[111] = 0; // reg0

		memory.getDataList()[112] = 9; // move
		memory.getDataList()[113] = 81; // mem
		memory.getDataList()[114] = 1; // reg1

		memory.getDataList()[115] = 9; // move
		memory.getDataList()[116] = 82; // mem
		memory.getDataList()[117] = 2; // reg2

		memory.getDataList()[118] = 9; // move
		memory.getDataList()[119] = 83; // mem
		memory.getDataList()[120] = 3; // reg3

		// PC++
		PC.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore(); // now PC points to the parameter address
		PC.read();

		memory.getDataList()[121] = 15; // jmp
		memory.getDataList()[122] = extbus1.get(); // Para a próxima instrução

		extbus1.put(84);
		PC.store();
	}

	public void imulRegReg() {
		// PC++
		PC.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore(); // now PC points to the parameter address
		// IR.internalStore(); // Guardamos o PC no IR

		// Gravar os valores dos registradores na memória
		memory.getDataList()[80] = REG0.getData();
		memory.getDataList()[81] = REG1.getData();
		memory.getDataList()[82] = REG2.getData();
		memory.getDataList()[83] = REG3.getData();

		// Move RegA Reg0
		// Agora colocamos moveRegAReg na memória, onde RegA é o que esta no parametro
		memory.getDataList()[84] = 11; // move
		// Pegamos o id do Registrador do parametro e colocamos na memória
		PC.read();
		memory.read();
		memory.getDataList()[85] = extbus1.get(); // regA
		memory.getDataList()[86] = 0; // reg0

		// PC++
		PC.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore(); // now PC points to the parameter address

		// Move RegB Reg2
		// Agora colocamos moveRegBReg na memória, onde RegB é o que esta no parametro
		PC.read();
		memory.read();
		memory.getDataList()[87] = 11; // move
		memory.getDataList()[78] = extbus1.get(); // regB // Guarda qual registrador é
		memory.getDataList()[88] = extbus1.get(); // regB
		memory.getDataList()[89] = 2; // reg2

		// MOVE 0 REG3
		memory.getDataList()[90] = 12; // move
		memory.getDataList()[91] = 0; // immediate
		memory.getDataList()[92] = 3; // reg1

		// MOVE 1 REG1
		memory.getDataList()[93] = 12; // move // endereço do LOOP
		memory.getDataList()[94] = 1; // immediate
		memory.getDataList()[95] = 1; // reg1

		// ADD REG0 REG3
		memory.getDataList()[96] = 0; // add
		memory.getDataList()[97] = 0; // reg0
		memory.getDataList()[98] = 3; // reg3

		// SUB REG2 REG1
		memory.getDataList()[99] = 3; // sub
		memory.getDataList()[100] = 2; // reg2
		memory.getDataList()[101] = 1; // reg1

		// MOVE REG1 REG2
		memory.getDataList()[102] = 11; // move
		memory.getDataList()[103] = 1; // reg1
		memory.getDataList()[104] = 2; // reg2

		// JZ LOOP
		memory.getDataList()[105] = 17; // jz
		memory.getDataList()[106] = 109; // Guardando o endereço da memória

		// JMP LOOP
		memory.getDataList()[107] = 15; // jmp
		memory.getDataList()[108] = 93; // laço

		// Guarda resultado da multiplicação na memória
		memory.getDataList()[109] = 10; // move
		memory.getDataList()[110] = 3; // reg3
		memory.getDataList()[111] = 77; // mem

		// Recupera os dados dos registradores
		memory.getDataList()[112] = 9; // move
		memory.getDataList()[113] = 80; // mem
		memory.getDataList()[114] = 0; // reg0

		memory.getDataList()[115] = 9; // move
		memory.getDataList()[116] = 81; // mem
		memory.getDataList()[117] = 1; // reg1

		memory.getDataList()[118] = 9; // move
		memory.getDataList()[119] = 82; // mem
		memory.getDataList()[120] = 2; // reg2

		memory.getDataList()[121] = 9; // move
		memory.getDataList()[122] = 83; // mem
		memory.getDataList()[123] = 3; // reg3

		// Recuperamos o resultado da multiplicação no registrador certo
		memory.getDataList()[124] = 9; // move
		memory.getDataList()[125] = 77; // mem
		memory.getDataList()[126] = memory.getDataList()[78]; // regB

		// PC++
		PC.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore(); // now PC points to the parameter address
		PC.read();

		memory.getDataList()[127] = 15; // jmp
		memory.getDataList()[128] = extbus1.get(); // Para a próxima instrução

		extbus1.put(84);
		PC.store();
	}

	/**
	 * This method implements the microprogram for
	 * ADD address
	 * In the machine language this command number is 0, and the address is in the
	 * position next to him
	 * 
	 * where address is a valid position in this memory architecture
	 * The method reads the value from memory (position address) and
	 * performs an add with this value and that one stored in the RPG (the first
	 * register in the register list).
	 * The final result must be in RPG (the first register in the register list).
	 * The logic is
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <- intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.store() now pc points to the parameter
	 * 6. rpg -> intbus1 //rpg.read() the current rpg value must go to the ula
	 * 7. ula <- intbus1 //ula.store()
	 * 8. pc -> extbus (pc.read())
	 * 9. memory reads from extbus //this forces memory to write the data position
	 * in the extbus
	 * 10. memory reads from extbus //this forces memory to write the data value in
	 * the extbus
	 * 11. rpg <- extbus (rpg.store())
	 * 12. rpg -> intbus1 (rpg.read())
	 * 13. ula <- intbus1 //ula.store()
	 * 14. Flags <- zero //the status flags are reset
	 * 15. ula adds
	 * 16. ula -> intbus1 //ula.read()
	 * 17. ChangeFlags //informations about flags are set according the result
	 * 18. rpg <- intbus1 //rpg.store() - the add is complete.
	 * 19. pc -> intbus2 //pc.read() now pc must point the next instruction address
	 * 20. ula <- intbus2 //ula.store()
	 * 21. ula incs
	 * 22. ula -> intbus2 //ula.read()
	 * 23. pc <- intbus2 //pc.store()
	 * end
	 * 
	 * @param address
	 */
	public void addRegReg() { // Revisar
		PC.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore(); // now PC points to the parameter address
		PC.read();
		memory.read(); // the first register id is now in the external bus.
		demux.setValue(extbus1.get()); // points to the correct register
		registersInternalRead(); // starts the read from the register identified into demux bus
		ula.internalStore(0);
		ula.inc();
		ula.read(1);
		PC.internalStore(); // now PC points to the second parameter (the second reg id)
		PC.read();
		memory.read(); // the first register id is now in the external bus.
		demux.setValue(extbus1.get()); // points to the correct register
		registersInternalRead(); // starts the read from the register identified into demux bus
		ula.internalStore(1);
		ula.add(); // the result is in the second ula's internal register
		ula.internalRead(1);
		setStatusFlags(intbus2.get()); // changing flags due the end of the operation
		registersInternalStore(); // performs an internal store for the register identified into demux bus
		PC.internalRead(); // we need to make PC points to the next instruction address
		ula.store(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore(); // now PC points to the next instruction. We go back to the FETCH status.
	}

	/**
	 * This method implements the microprogram for
	 * ADD address
	 * In the machine language this command number is 0, and the address is in the
	 * position next to him
	 * 
	 * where address is a valid position in this memory architecture
	 * The method reads the value from memory (position address) and
	 * performs an add with this value and that one stored in the RPG (the first
	 * register in the register list).
	 * The final result must be in RPG (the first register in the register list).
	 * The logic is
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <- intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.store() now pc points to the parameter
	 * 6. rpg -> intbus1 //rpg.read() the current rpg value must go to the ula
	 * 7. ula <- intbus1 //ula.store()
	 * 8. pc -> extbus (pc.read())
	 * 9. memory reads from extbus //this forces memory to write the data position
	 * in the extbus
	 * 10. memory reads from extbus //this forces memory to write the data value in
	 * the extbus
	 * 11. rpg <- extbus (rpg.store())
	 * 12. rpg -> intbus1 (rpg.read())
	 * 13. ula <- intbus1 //ula.store()
	 * 14. Flags <- zero //the status flags are reset
	 * 15. ula adds
	 * 16. ula -> intbus1 //ula.read()
	 * 17. ChangeFlags //informations about flags are set according the result
	 * 18. rpg <- intbus1 //rpg.store() - the add is complete.
	 * 19. pc -> intbus2 //pc.read() now pc must point the next instruction address
	 * 20. ula <- intbus2 //ula.store()
	 * 21. ula incs
	 * 22. ula -> intbus2 //ula.read()
	 * 23. pc <- intbus2 //pc.store()
	 * end
	 * 
	 * @param address
	 */
	public void addMemReg() { // Revisar
		PC.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore(); // now PC points to the parameter address
		PC.read();
		memory.read();
		memory.read();
		PC.store();
		PC.internalRead();
		ula.store(0);
		ula.inc();
		ula.read(1);
		PC.internalStore(); // now PC points to the second parameter (the second reg id)
		PC.read();
		memory.read(); // the first register id is now in the external bus.
		demux.setValue(extbus1.get()); // points to the correct register
		registersInternalRead(); // starts the read from the register identified into demux bus
		ula.internalStore(1);
		ula.add(); // the result is in the second ula's internal register
		ula.internalRead(1);
		setStatusFlags(intbus2.get()); // changing flags due the end of the operation
		registersInternalStore(); // performs an internal store for the register identified into demux bus
		PC.internalRead(); // we need to make PC points to the next instruction address
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore(); // now PC points to the next instruction. We go back to the FETCH status.
	}

	/**
	 * This method implements the microprogram for
	 * ADD address
	 * In the machine language this command number is 0, and the address is in the
	 * position next to him
	 * 
	 * where address is a valid position in this memory architecture
	 * The method reads the value from memory (position address) and
	 * performs an add with this value and that one stored in the RPG (the first
	 * register in the register list).
	 * The final result must be in RPG (the first register in the register list).
	 * The logic is
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <- intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.store() now pc points to the parameter
	 * 6. rpg -> intbus1 //rpg.read() the current rpg value must go to the ula
	 * 7. ula <- intbus1 //ula.store()
	 * 8. pc -> extbus (pc.read())
	 * 9. memory reads from extbus //this forces memory to write the data position
	 * in the extbus
	 * 10. memory reads from extbus //this forces memory to write the data value in
	 * the extbus
	 * 11. rpg <- extbus (rpg.store())
	 * 12. rpg -> intbus1 (rpg.read())
	 * 13. ula <- intbus1 //ula.store()
	 * 14. Flags <- zero //the status flags are reset
	 * 15. ula adds
	 * 16. ula -> intbus1 //ula.read()
	 * 17. ChangeFlags //informations about flags are set according the result
	 * 18. rpg <- intbus1 //rpg.store() - the add is complete.
	 * 19. pc -> intbus2 //pc.read() now pc must point the next instruction address
	 * 20. ula <- intbus2 //ula.store()
	 * 21. ula incs
	 * 22. ula -> intbus2 //ula.read()
	 * 23. pc <- intbus2 //pc.store()
	 * end
	 * 
	 * @param address
	 */
	public void addRegMem() { // Revisar
		PC.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore(); // now PC points to the parameter address
		PC.read();
		memory.read();
		demux.setValue(extbus1.get()); // points to the correct register
		registersInternalRead(); // starts the read from the register identified into demux bus
		ula.internalStore(0);
		ula.inc();
		ula.read(1);
		IR.internalStore();
		PC.internalStore(); // now PC points to the second parameter (the second reg id)
		PC.read();
		memory.read(); // the first register id is now in the external bus.
		memory.store();
		memory.read(); // the first register id is now in the external bus.
		PC.store();
		PC.internalRead();
		ula.store(1);
		ula.add(); // the result is in the second ula's internal register
		ula.read(1);
		setStatusFlags(intbus2.get()); // changing flags due the end of the operation
		PC.internalStore(); // we need to make PC points to the next instruction address
		PC.read();
		memory.store();
		IR.internalRead();
		ula.store(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore(); // now PC points to the next instruction. We go back to the FETCH status.
	}

	/**
	 * This method implements the microprogram for
	 * ADD address
	 * In the machine language this command number is 0, and the address is in the
	 * position next to him
	 * 
	 * where address is a valid position in this memory architecture
	 * The method reads the value from memory (position address) and
	 * performs an add with this value and that one stored in the RPG (the first
	 * register in the register list).
	 * The final result must be in RPG (the first register in the register list).
	 * The logic is
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <- intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.store() now pc points to the parameter
	 * 6. rpg -> intbus1 //rpg.read() the current rpg value must go to the ula
	 * 7. ula <- intbus1 //ula.store()
	 * 8. pc -> extbus (pc.read())
	 * 9. memory reads from extbus //this forces memory to write the data position
	 * in the extbus
	 * 10. memory reads from extbus //this forces memory to write the data value in
	 * the extbus
	 * 11. rpg <- extbus (rpg.store())
	 * 12. rpg -> intbus1 (rpg.read())
	 * 13. ula <- intbus1 //ula.store()
	 * 14. Flags <- zero //the status flags are reset
	 * 15. ula adds
	 * 16. ula -> intbus1 //ula.read()
	 * 17. ChangeFlags //informations about flags are set according the result
	 * 18. rpg <- intbus1 //rpg.store() - the add is complete.
	 * 19. pc -> intbus2 //pc.read() now pc must point the next instruction address
	 * 20. ula <- intbus2 //ula.store()
	 * 21. ula incs
	 * 22. ula -> intbus2 //ula.read()
	 * 23. pc <- intbus2 //pc.store()
	 * end
	 * 
	 * @param address
	 */

	public void subRegReg() { // Revisar
		PC.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore(); // now PC points to the parameter address
		PC.read();
		memory.read(); // the first register id is now in the external bus.
		demux.setValue(extbus1.get()); // points to the correct register
		registersInternalRead(); // starts the read from the register identified into demux bus
		ula.internalStore(0);
		ula.inc();
		ula.read(1);
		PC.internalStore(); // now PC points to the second parameter (the second reg id)
		PC.read();
		memory.read(); // the first register id is now in the external bus.
		demux.setValue(extbus1.get()); // points to the correct register
		registersInternalRead(); // starts the read from the register identified into demux bus
		ula.internalStore(1);
		ula.sub(); // the result is in the second ula's internal register
		ula.internalRead(1);
		setStatusFlags(intbus2.get()); // changing flags due the end of the operation
		registersInternalStore(); // performs an internal store for the register identified into demux bus
		PC.internalRead(); // we need to make PC points to the next instruction address
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore(); // now PC points to the next instruction. We go back to the FETCH status.
	}

	/**
	 * This method implements the microprogram for
	 * ADD address
	 * In the machine language this command number is 0, and the address is in the
	 * position next to him
	 * 
	 * where address is a valid position in this memory architecture
	 * The method reads the value from memory (position address) and
	 * performs an add with this value and that one stored in the RPG (the first
	 * register in the register list).
	 * The final result must be in RPG (the first register in the register list).
	 * The logic is
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <- intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.store() now pc points to the parameter
	 * 6. rpg -> intbus1 //rpg.read() the current rpg value must go to the ula
	 * 7. ula <- intbus1 //ula.store()
	 * 8. pc -> extbus (pc.read())
	 * 9. memory reads from extbus //this forces memory to write the data position
	 * in the extbus
	 * 10. memory reads from extbus //this forces memory to write the data value in
	 * the extbus
	 * 11. rpg <- extbus (rpg.store())
	 * 12. rpg -> intbus1 (rpg.read())
	 * 13. ula <- intbus1 //ula.store()
	 * 14. Flags <- zero //the status flags are reset
	 * 15. ula adds
	 * 16. ula -> intbus1 //ula.read()
	 * 17. ChangeFlags //informations about flags are set according the result
	 * 18. rpg <- intbus1 //rpg.store() - the add is complete.
	 * 19. pc -> intbus2 //pc.read() now pc must point the next instruction address
	 * 20. ula <- intbus2 //ula.store()
	 * 21. ula incs
	 * 22. ula -> intbus2 //ula.read()
	 * 23. pc <- intbus2 //pc.store()
	 * end
	 * 
	 * @param address
	 */
	public void subMemReg() { // Revisar
		PC.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore(); // now PC points to the parameter address
		PC.read();
		memory.read();
		memory.read();
		PC.store();
		PC.internalRead();
		ula.store(0);
		ula.inc();
		ula.read(1);
		PC.internalStore(); // now PC points to the second parameter (the second reg id)
		PC.read();
		memory.read(); // the first register id is now in the external bus.
		demux.setValue(extbus1.get()); // points to the correct register
		registersInternalRead(); // starts the read from the register identified into demux bus
		ula.internalStore(1);
		ula.sub(); // the result is in the second ula's internal register
		ula.internalRead(1);
		setStatusFlags(intbus2.get()); // changing flags due the end of the operation
		registersInternalStore(); // performs an internal store for the register identified into demux bus
		PC.internalRead(); // we need to make PC points to the next instruction address
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore(); // now PC points to the next instruction. We go back to the FETCH status.
	}

	/**
	 * This method implements the microprogram for
	 * ADD address
	 * In the machine language this command number is 0, and the address is in the
	 * position next to him
	 * 
	 * where address is a valid position in this memory architecture
	 * The method reads the value from memory (position address) and
	 * performs an add with this value and that one stored in the RPG (the first
	 * register in the register list).
	 * The final result must be in RPG (the first register in the register list).
	 * The logic is
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <- intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.store() now pc points to the parameter
	 * 6. rpg -> intbus1 //rpg.read() the current rpg value must go to the ula
	 * 7. ula <- intbus1 //ula.store()
	 * 8. pc -> extbus (pc.read())
	 * 9. memory reads from extbus //this forces memory to write the data position
	 * in the extbus
	 * 10. memory reads from extbus //this forces memory to write the data value in
	 * the extbus
	 * 11. rpg <- extbus (rpg.store())
	 * 12. rpg -> intbus1 (rpg.read())
	 * 13. ula <- intbus1 //ula.store()
	 * 14. Flags <- zero //the status flags are reset
	 * 15. ula adds
	 * 16. ula -> intbus1 //ula.read()
	 * 17. ChangeFlags //informations about flags are set according the result
	 * 18. rpg <- intbus1 //rpg.store() - the add is complete.
	 * 19. pc -> intbus2 //pc.read() now pc must point the next instruction address
	 * 20. ula <- intbus2 //ula.store()
	 * 21. ula incs
	 * 22. ula -> intbus2 //ula.read()
	 * 23. pc <- intbus2 //pc.store()
	 * end
	 * 
	 * @param address
	 */
	public void subRegMem() { // Revisar
		PC.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore(); // now PC points to the parameter address
		PC.read();
		memory.read();
		demux.setValue(extbus1.get()); // points to the correct register
		registersInternalRead(); // starts the read from the register identified into demux bus
		ula.internalStore(0);
		ula.inc();
		ula.read(1);
		IR.internalStore();
		PC.internalStore(); // now PC points to the second parameter (the second reg id)
		PC.read();
		memory.read(); // the first register id is now in the external bus.
		memory.store();
		memory.read(); // the first register id is now in the external bus.
		PC.store();
		PC.internalRead();
		ula.store(1);
		ula.sub(); // the result is in the second ula's internal register
		ula.read(1);
		setStatusFlags(intbus2.get()); // changing flags due the end of the operation
		PC.internalStore(); // we need to make PC points to the next instruction address
		PC.read();
		memory.store();
		IR.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore(); // now PC points to the next instruction. We go back to the FETCH status.
	}

	/**
	 * This method implements the microprogram for
	 * JMP address
	 * In the machine language this command number is 2, and the address is in the
	 * position next to him
	 * 
	 * where address is a valid position in this memory architecture (where the PC
	 * is redirecto to)
	 * The method reads the value from memory (position address) and
	 * inserts it into the PC register.
	 * So, the program is deviated
	 * The logic is
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <- intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.store() now pc points to the parameter
	 * 6. pc -> extbus //pc.read()
	 * 7. memory reads from extbus //this forces memory to write the data position
	 * in the extbus
	 * 8. pc <- extbus //pc.store() //pc was pointing to another part of the memory
	 * end
	 * 
	 * @param address
	 */
	public void jmp() {
		PC.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore(); // now PC points to the parameter address
		PC.read();
		memory.read();
		PC.store();
	}

	/**
	 * This method implements the microprogram for
	 * JZ address
	 * In the machine language this command number is 3, and the address is in the
	 * position next to him
	 * 
	 * where address is a valid position in this memory architecture (where
	 * the PC is redirected to, but only in the case the ZERO bit in Flags is 1)
	 * The method reads the value from memory (position address) and
	 * inserts it into the PC register if the ZERO bit in Flags register is setted.
	 * So, the program is deviated conditionally
	 * The logic is
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <- intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.internalstore() now pc points to the parameter
	 * 6. pc -> extbus1 //pc.read() now the parameter address is in the extbus1
	 * 7. Memory -> extbus1 //memory.read() the address (if jn) is in external bus 1
	 * 8. statusMemory(1)<- extbus1 // statusMemory.storeIn1()
	 * 9. ula incs
	 * 10. ula -> intbus2 //ula.read()
	 * 11. PC <- intbus2 // PC.internalStore() PC is now pointing to next
	 * instruction
	 * 12. PC -> extbus1 // PC.read() the next instruction address is in the extbus
	 * 13. statusMemory(0)<- extbus1 // statusMemory.storeIn0()
	 * 14. Flags(bitZero) -> extbus1 //the ZERO bit is in the external bus
	 * 15. statusMemory <- extbus // the status memory returns the correct address
	 * according the ZERO bit
	 * 16. PC <- extbus1 // PC stores the new address where the program is
	 * redirected to
	 * end
	 * 
	 * @param address
	 */
	public void jz() {
		PC.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore();// now PC points to the parameter address
		PC.read();
		memory.read();// now the parameter value (address of the jz) is in the external bus
		statusMemory.storeIn1(); // the address is in position 1 of the status memory
		ula.inc();
		ula.read(1);
		PC.internalStore();// now PC points to the next instruction
		PC.read();// now the bus has the next istruction address
		statusMemory.storeIn0(); // the address is in the position 0 of the status memory
		extbus1.put(Flags.getBit(0)); // the ZERO bit is in the external bus
		statusMemory.read(); // gets the correct address (next instruction or parameter address)
		PC.store(); // stores into PC
	}

	/**
	 * This method implements the microprogram for
	 * JZ address
	 * In the machine language this command number is 3, and the address is in the
	 * position next to him
	 * 
	 * where address is a valid position in this memory architecture (where
	 * the PC is redirected to, but only in the case the ZERO bit in Flags is 1)
	 * The method reads the value from memory (position address) and
	 * inserts it into the PC register if the ZERO bit in Flags register is setted.
	 * So, the program is deviated conditionally
	 * The logic is
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <- intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.internalstore() now pc points to the parameter
	 * 6. pc -> extbus1 //pc.read() now the parameter address is in the extbus1
	 * 7. Memory -> extbus1 //memory.read() the address (if jn) is in external bus 1
	 * 8. statusMemory(1)<- extbus1 // statusMemory.storeIn1()
	 * 9. ula incs
	 * 10. ula -> intbus2 //ula.read()
	 * 11. PC <- intbus2 // PC.internalStore() PC is now pointing to next
	 * instruction
	 * 12. PC -> extbus1 // PC.read() the next instruction address is in the extbus
	 * 13. statusMemory(0)<- extbus1 // statusMemory.storeIn0()
	 * 14. Flags(bitZero) -> extbus1 //the ZERO bit is in the external bus
	 * 15. statusMemory <- extbus // the status memory returns the correct address
	 * according the ZERO bit
	 * 16. PC <- extbus1 // PC stores the new address where the program is
	 * redirected to
	 * end
	 * 
	 * @param address
	 */
	public void jnz() {
		PC.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore();// now PC points to the parameter address
		PC.read();
		memory.read();// now the parameter value (address of the jz) is in the external bus
		statusMemory.storeIn0(); // the address is in position 1 of the status memory
		ula.inc();
		ula.read(1);
		PC.internalStore();// now PC points to the next instruction
		PC.read();// now the bus has the next istruction address
		statusMemory.storeIn1(); // the address is in the position 0 of the status memory
		extbus1.put(Flags.getBit(0)); // the ZERO bit is in the external bus
		statusMemory.read(); // gets the correct address (next instruction or parameter address)
		PC.store(); // stores into PC
	}

	/**
	 * This method implements the microprogram for
	 * jn address
	 * In the machine language this command number is 4, and the address is in the
	 * position next to him
	 * 
	 * where address is a valid position in this memory architecture (where
	 * the PC is redirected to, but only in the case the NEGATIVE bit in Flags is 1)
	 * The method reads the value from memory (position address) and
	 * inserts it into the PC register if the NEG bit in Flags register is setted.
	 * So, the program is deviated conditionally
	 * The logic is
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <- intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.internalstore() now pc points to the parameter
	 * 6. pc -> extbus1 //pc.read() now the parameter address is in the extbus1
	 * 7. Memory -> extbus1 //memory.read() the address (if jn) is in external bus 1
	 * 8. statusMemory(1)<- extbus1 // statusMemory.storeIn1()
	 * 9. ula incs
	 * 10. ula -> intbus2 //ula.read()
	 * 11. PC <- intbus2 // PC.internalStore() PC is now pointing to next
	 * instruction
	 * 12. PC -> extbus1 // PC.read() the next instruction address is in the extbus
	 * 13. statusMemory(0)<- extbus1 // statusMemory.storeIn0()
	 * 14. Flags(bitNEGATIVE) -> extbus1 //the NEGATIVE bit is in the external bus
	 * 15. statusMemory <- extbus // the status memory returns the correct address
	 * according the ZERO bit
	 * 16. PC <- extbus1 // PC stores the new address where the program is
	 * redirected to
	 * end
	 * 
	 * @param address
	 */
	public void jn() {
		PC.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore();// now PC points to the parameter address
		PC.read();
		memory.read();// now the parameter value (address of the jz) is in the external bus
		statusMemory.storeIn1(); // the address is in position 1 of the status memory
		ula.inc();
		ula.read(1);
		PC.internalStore();// now PC points to the next instruction
		PC.read();// now the bus has the next istruction address
		statusMemory.storeIn0(); // the address is in the position 0 of the status memory
		extbus1.put(Flags.getBit(1)); // the ZERO bit is in the external bus
		statusMemory.read(); // gets the correct address (next instruction or parameter address)
		PC.store(); // stores into PC
	}

	/**
	 * This method implements the microprogram for
	 * jn address
	 * In the machine language this command number is 4, and the address is in the
	 * position next to him
	 * 
	 * where address is a valid position in this memory architecture (where
	 * the PC is redirected to, but only in the case the NEGATIVE bit in Flags is 1)
	 * The method reads the value from memory (position address) and
	 * inserts it into the PC register if the NEG bit in Flags register is setted.
	 * So, the program is deviated conditionally
	 * The logic is
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <- intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.internalstore() now pc points to the parameter
	 * 6. pc -> extbus1 //pc.read() now the parameter address is in the extbus1
	 * 7. Memory -> extbus1 //memory.read() the address (if jn) is in external bus 1
	 * 8. statusMemory(1)<- extbus1 // statusMemory.storeIn1()
	 * 9. ula incs
	 * 10. ula -> intbus2 //ula.read()
	 * 11. PC <- intbus2 // PC.internalStore() PC is now pointing to next
	 * instruction
	 * 12. PC -> extbus1 // PC.read() the next instruction address is in the extbus
	 * 13. statusMemory(0)<- extbus1 // statusMemory.storeIn0()
	 * 14. Flags(bitNEGATIVE) -> extbus1 //the NEGATIVE bit is in the external bus
	 * 15. statusMemory <- extbus // the status memory returns the correct address
	 * according the ZERO bit
	 * 16. PC <- extbus1 // PC stores the new address where the program is
	 * redirected to
	 * end
	 * 
	 * @param address
	 */
	public void jeq() {
		PC.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore();// now PC points to the parameter address
		PC.read();
		memory.read();// now the parameter value (address of the jeq) is in the external bus
		demux.setValue(extbus1.get());
		registersInternalRead();
		ula.internalStore(0);
		ula.inc();
		ula.read(1);
		PC.internalStore();
		PC.read();
		memory.read();
		demux.setValue(extbus1.get());
		registersInternalRead();
		ula.inc();
		ula.read(1);
		PC.internalStore();
		PC.read();
		memory.read();
		statusMemory.storeIn1(); // the address is in position 1 of the status memory
		ula.inc();
		ula.read(1);
		PC.internalStore();
		PC.read();
		statusMemory.storeIn0();
		registersInternalRead();
		ula.internalStore(1);
		ula.sub();
		ula.internalRead(1);
		setStatusFlags(intbus2.get()); // changing flags due the end of the operation
		extbus1.put(Flags.getBit(0)); // the ZERO bit is in the external bus
		statusMemory.read(); // gets the correct address (next instruction or parameter address)
		PC.store();
	}

	/**
	 * This method implements the microprogram for
	 * jn address
	 * In the machine language this command number is 4, and the address is in the
	 * position next to him
	 * 
	 * where address is a valid position in this memory architecture (where
	 * the PC is redirected to, but only in the case the NEGATIVE bit in Flags is 1)
	 * The method reads the value from memory (position address) and
	 * inserts it into the PC register if the NEG bit in Flags register is setted.
	 * So, the program is deviated conditionally
	 * The logic is
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <- intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.internalstore() now pc points to the parameter
	 * 6. pc -> extbus1 //pc.read() now the parameter address is in the extbus1
	 * 7. Memory -> extbus1 //memory.read() the address (if jn) is in external bus 1
	 * 8. statusMemory(1)<- extbus1 // statusMemory.storeIn1()
	 * 9. ula incs
	 * 10. ula -> intbus2 //ula.read()
	 * 11. PC <- intbus2 // PC.internalStore() PC is now pointing to next
	 * instruction
	 * 12. PC -> extbus1 // PC.read() the next instruction address is in the extbus
	 * 13. statusMemory(0)<- extbus1 // statusMemory.storeIn0()
	 * 14. Flags(bitNEGATIVE) -> extbus1 //the NEGATIVE bit is in the external bus
	 * 15. statusMemory <- extbus // the status memory returns the correct address
	 * according the ZERO bit
	 * 16. PC <- extbus1 // PC stores the new address where the program is
	 * redirected to
	 * end
	 * 
	 * @param address
	 */
	public void jgt() {
		PC.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore();// now PC points to the parameter address
		PC.read();
		memory.read();// now the parameter value (address of the jz) is in the external bus
		demux.setValue(extbus1.get());
		registersRead();
		PC.store();
		PC.internalRead();
		IR.internalStore();
		ula.inc();
		ula.read(1);
		PC.internalStore();
		PC.read();
		memory.read();
		demux.setValue(extbus1.get());
		ula.inc();
		ula.read(1);
		PC.internalStore();
		PC.read();
		memory.read();
		statusMemory.storeIn1(); // the address is in position 1 of the status memory
		ula.inc();
		ula.read(1);
		PC.internalStore();
		PC.read();
		statusMemory.storeIn0();
		registersInternalRead();
		ula.internalStore(0);
		IR.internalRead();
		ula.store(1);
		ula.sub();
		ula.internalRead(1);
		setStatusFlags(intbus2.get()); // changing flags due the end of the operation
		extbus1.put(Flags.getBit(1)); // the ZERO bit is in the external bus
		statusMemory.read(); // gets the correct address (next instruction or parameter address)
		PC.store();
	}

	/**
	 * This method implements the microprogram for
	 * jn address
	 * In the machine language this command number is 4, and the address is in the
	 * position next to him
	 * 
	 * where address is a valid position in this memory architecture (where
	 * the PC is redirected to, but only in the case the NEGATIVE bit in Flags is 1)
	 * The method reads the value from memory (position address) and
	 * inserts it into the PC register if the NEG bit in Flags register is setted.
	 * So, the program is deviated conditionally
	 * The logic is
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <- intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.internalstore() now pc points to the parameter
	 * 6. pc -> extbus1 //pc.read() now the parameter address is in the extbus1
	 * 7. Memory -> extbus1 //memory.read() the address (if jn) is in external bus 1
	 * 8. statusMemory(1)<- extbus1 // statusMemory.storeIn1()
	 * 9. ula incs
	 * 10. ula -> intbus2 //ula.read()
	 * 11. PC <- intbus2 // PC.internalStore() PC is now pointing to next
	 * instruction
	 * 12. PC -> extbus1 // PC.read() the next instruction address is in the extbus
	 * 13. statusMemory(0)<- extbus1 // statusMemory.storeIn0()
	 * 14. Flags(bitNEGATIVE) -> extbus1 //the NEGATIVE bit is in the external bus
	 * 15. statusMemory <- extbus // the status memory returns the correct address
	 * according the ZERO bit
	 * 16. PC <- extbus1 // PC stores the new address where the program is
	 * redirected to
	 * end
	 * 
	 * @param address
	 */
	public void jlw() {
		PC.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore();// now PC points to the parameter address
		PC.read();
		memory.read();// now the parameter value (address of the jz) is in the external bus
		demux.setValue(extbus1.get());
		registersInternalRead();
		ula.internalStore(0);
		ula.inc();
		ula.read(1);
		PC.internalStore();
		PC.read();
		memory.read();
		demux.setValue(extbus1.get());
		ula.inc();
		ula.read(1);
		System.out.println("ULA(1) -> IntBus1: " + intbus1.get());
		PC.internalStore();
		PC.read();
		memory.read();
		statusMemory.storeIn1(); // the address is in position 1 of the status memory
		ula.inc();
		ula.read(1);
		PC.internalStore();
		PC.read();
		statusMemory.storeIn0();
		registersInternalRead();
		ula.internalStore(1);
		ula.sub();
		ula.internalRead(1);
		setStatusFlags(intbus2.get()); // changing flags due the end of the operation
		extbus1.put(Flags.getBit(1)); // the ZERO bit is in the external bus
		statusMemory.read(); // gets the correct address (next instruction or parameter address)
		PC.store();
	}

	/**
	 * This method implements the microprogram for
	 * read address
	 * In the machine language this command number is 5, and the address is in the
	 * position next to him
	 * 
	 * where address is a valid position in this memory architecture
	 * The method reads the value from memory (position address) and
	 * inserts it into the RPG register (the first register in the register list)
	 * The logic is
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <- intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.store() now pc points to the parameter
	 * 6. pc -> extbus //(pc.read())the address where is the position to be read is
	 * now in the external bus
	 * 7. memory reads from extbus //this forces memory to write the address in the
	 * extbus
	 * 8. memory reads from extbus //this forces memory to write the stored data in
	 * the extbus
	 * 9. RPG <- extbus //the data is read
	 * 10. pc -> intbus2 //pc.read() now pc must point the next instruction address
	 * 11. ula <- intbus2 //ula.store()
	 * 12. ula incs
	 * 13. ula -> intbus2 //ula.read()
	 * 14. pc <- intbus2 //pc.store()
	 * end
	 * 
	 * @param address
	 */
	/*
	 * public void read() {
	 * PC.internalRead();
	 * ula.internalStore(1);
	 * ula.inc();
	 * ula.internalRead(1);
	 * PC.internalStore(); //now PC points to the parameter address
	 * PC.read();
	 * memory.read(); // the address is now in the external bus.
	 * memory.read(); // the data is now in the external bus.
	 * RPG.store();
	 * PC.internalRead(); //we need to make PC points to the next instruction
	 * address
	 * ula.internalStore(1);
	 * ula.inc();
	 * ula.internalRead(1);
	 * PC.internalStore(); //now PC points to the next instruction. We go back to
	 * the FETCH status.
	 * }
	 */
	/**
	 * This method implements the microprogram for
	 * store address
	 * In the machine language this command number is 6, and the address is in the
	 * position next to him
	 * 
	 * where address is a valid position in this memory architecture
	 * The method reads the value from RPG (the first register in the register list)
	 * and
	 * inserts it into the memory (position address)
	 * The logic is
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <- intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.store() now pc points to the parameter
	 * 6. pc -> extbus //(pc.read())the parameter address is the external bus
	 * 7. memory reads // memory reads the data in the parameter address.
	 * // this data is the address where the RPG value must be stores
	 * 8. memory stores //memory reads the address and wait for the value
	 * 9. RPG -> Externalbus //RPG.read()
	 * 10. memory stores //memory receives the value and stores it
	 * 11. pc -> intbus2 //pc.read() now pc must point the next instruction address
	 * 12. ula <- intbus2 //ula.store()
	 * 13. ula incs
	 * 14. ula -> intbus2 //ula.read()
	 * 15. pc <- intbus2 //pc.store()
	 * end
	 * 
	 * @param address
	 */
	/*
	 * public void store() {
	 * PC.internalRead();
	 * ula.internalStore(1);
	 * ula.inc();
	 * ula.internalRead(1);
	 * PC.internalStore(); //now PC points to the parameter address
	 * PC.read();
	 * memory.read(); //the parameter address (pointing to the addres where data
	 * must be stored
	 * //is now in externalbus1
	 * memory.store(); //the address is in the memory. Now we must to send the data
	 * RPG.read();
	 * memory.store(); //the data is now stored
	 * PC.internalRead(); //we need to make PC points to the next instruction
	 * address
	 * ula.internalStore(1);
	 * ula.inc();
	 * ula.internalRead(1);
	 * PC.internalStore(); //now PC points to the next instruction. We go back to
	 * the FETCH status.
	 * }
	 */

	/**
	 * This method implements the microprogram for
	 * ldi immediate
	 * In the machine language this command number is 7, and the immediate value
	 * is in the position next to him
	 * 
	 * The method moves the value (parameter) into the internalbus1 and the RPG
	 * (the first register in the register list) consumes it
	 * The logic is
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <- intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.store() now pc points to the parameter
	 * 6. pc -> extbus //(pc.read())the address where is the position to be read is
	 * now in the external bus
	 * 7. memory reads from extbus //this forces memory to write the stored data in
	 * the extbus
	 * 8. RPG <- extbus //rpg.store()
	 * 9. 10. pc -> intbus2 //pc.read() now pc must point the next instruction
	 * address
	 * 10. ula <- intbus2 //ula.store()
	 * 11. ula incs
	 * 12. ula -> intbus2 //ula.read()
	 * 13. pc <- intbus2 //pc.store()
	 * end
	 * 
	 * @param address
	 */
	/*
	 * public void ldi() {
	 * PC.internalRead();
	 * ula.internalStore(1);
	 * ula.inc();
	 * ula.internalRead(1);
	 * PC.internalStore(); //now PC points to the parameter address
	 * PC.read();
	 * memory.read(); // the immediate is now in the external bus.
	 * RPG.store(); //RPG receives the immediate
	 * PC.internalRead(); //we need to make PC points to the next instruction
	 * address
	 * ula.internalStore(1);
	 * ula.inc();
	 * ula.internalRead(1);
	 * PC.internalStore(); //now PC points to the next instruction. We go back to
	 * the FETCH status.
	 * }
	 */
	/**
	 * This method implements the microprogram for
	 * inc
	 * In the machine language this command number is 8
	 * 
	 * The method moves the value in rpg (the first register in the register list)
	 * into the ula and performs an inc method
	 * -> inc works just like add rpg (the first register in the register list)
	 * with the mumber 1 stored into the memory
	 * -> however, inc consumes lower amount of cycles
	 * 
	 * The logic is
	 * 
	 * 1. rpg -> intbus1 //rpg.read()
	 * 2. ula <- intbus1 //ula.store()
	 * 3. Flags <- zero //the status flags are reset
	 * 4. ula incs
	 * 5. ula -> intbus1 //ula.read()
	 * 6. ChangeFlags //informations about flags are set according the result
	 * 7. rpg <- intbus1 //rpg.store()
	 * 8. pc -> intbus2 //pc.read() now pc must point the next instruction address
	 * 9. ula <- intbus2 //ula.store()
	 * 10. ula incs
	 * 11. ula -> intbus2 //ula.read()
	 * 12. pc <- intbus2 //pc.store()
	 * end
	 * 
	 * @param address
	 */

	public void incReg() {
		PC.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore();
		PC.read();
		memory.read();
		demux.setValue(extbus1.get());
		registersInternalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		registersInternalStore();
		PC.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore();
	}

	/**
	 * This method implements the microprogram for
	 * inc
	 * In the machine language this command number is 8
	 * 
	 * The method moves the value in rpg (the first register in the register list)
	 * into the ula and performs an inc method
	 * -> inc works just like add rpg (the first register in the register list)
	 * with the mumber 1 stored into the memory
	 * -> however, inc consumes lower amount of cycles
	 * 
	 * The logic is
	 * 
	 * 1. rpg -> intbus1 //rpg.read()
	 * 2. ula <- intbus1 //ula.store()
	 * 3. Flags <- zero //the status flags are reset
	 * 4. ula incs
	 * 5. ula -> intbus1 //ula.read()
	 * 6. ChangeFlags //informations about flags are set according the result
	 * 7. rpg <- intbus1 //rpg.store()
	 * 8. pc -> intbus2 //pc.read() now pc must point the next instruction address
	 * 9. ula <- intbus2 //ula.store()
	 * 10. ula incs
	 * 11. ula -> intbus2 //ula.read()
	 * 12. pc <- intbus2 //pc.store()
	 * end
	 * 
	 * @param address
	 */

	public void incMem() {
		PC.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		IR.internalStore();
		PC.internalStore();
		PC.read();
		memory.read();
		memory.store();
		memory.read();
		PC.store();
		PC.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore();
		PC.read();
		memory.store();
		IR.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore();
	}

	/**
	 * This method implements the microprogram for
	 * move <reg1> <reg2>
	 * In the machine language this command number is 9
	 * 
	 * The method reads the two register ids (<reg1> and <reg2>) from the memory, in
	 * positions just after the command, and
	 * copies the value from the <reg1> register to the <reg2> register
	 * 
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <- intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.store() now pc points to the first parameter
	 * 6. pc -> extbus //(pc.read())the address where is the position to be read is
	 * now in the external bus
	 * 7. memory reads from extbus //this forces memory to write the parameter
	 * (first regID) in the extbus
	 * 8. pc -> intbus2 //pc.read() //getting the second parameter
	 * 9. ula <- intbus2 //ula.store()
	 * 10. ula incs
	 * 11. ula -> intbus2 //ula.read()
	 * 12. pc <- intbus2 //pc.store() now pc points to the second parameter
	 * 13. demux <- extbus //now the register to be operated is selected
	 * 14. registers -> intbus1 //this performs the internal reading of the selected
	 * register
	 * 15. PC -> extbus (pc.read())the address where is the position to be read is
	 * now in the external bus
	 * 16. memory reads from extbus //this forces memory to write the parameter
	 * (second regID) in the extbus
	 * 17. demux <- extbus //now the register to be operated is selected
	 * 18. registers <- intbus1 //thid rerforms the external reading of the register
	 * identified in the extbus
	 * 19. 10. pc -> intbus2 //pc.read() now pc must point the next instruction
	 * address
	 * 20. ula <- intbus2 //ula.store()
	 * 21. ula incs
	 * 22. ula -> intbus2 //ula.read()
	 * 23. pc <- intbus2 //pc.store()
	 * 
	 *//* */
	public void moveMemReg() {
		PC.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore(); // now PC points to the first parameter (the first reg id)
		PC.read();
		memory.read(); // the first register id is now in the external bus.
		memory.read(); // the first register id is now in the external bus.
		PC.store();
		PC.internalRead();
		IR.internalStore();
		ula.inc();
		ula.read(1);
		PC.internalStore();
		PC.read();
		memory.read();
		demux.setValue(extbus1.get());
		IR.internalRead();
		PC.internalStore();
		PC.read();
		registersStore();
		ula.inc();
		ula.read(1);
		PC.internalStore();
	}

	/**
	 * This method implements the microprogram for
	 * move <reg1> <reg2>
	 * In the machine language this command number is 9
	 * 
	 * The method reads the two register ids (<reg1> and <reg2>) from the memory, in
	 * positions just after the command, and
	 * copies the value from the <reg1> register to the <reg2> register
	 * 
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <- intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.store() now pc points to the first parameter
	 * 6. pc -> extbus //(pc.read())the address where is the position to be read is
	 * now in the external bus
	 * 7. memory reads from extbus //this forces memory to write the parameter
	 * (first regID) in the extbus
	 * 8. pc -> intbus2 //pc.read() //getting the second parameter
	 * 9. ula <- intbus2 //ula.store()
	 * 10. ula incs
	 * 11. ula -> intbus2 //ula.read()
	 * 12. pc <- intbus2 //pc.store() now pc points to the second parameter
	 * 13. demux <- extbus //now the register to be operated is selected
	 * 14. registers -> intbus1 //this performs the internal reading of the selected
	 * register
	 * 15. PC -> extbus (pc.read())the address where is the position to be read is
	 * now in the external bus
	 * 16. memory reads from extbus //this forces memory to write the parameter
	 * (second regID) in the extbus
	 * 17. demux <- extbus //now the register to be operated is selected
	 * 18. registers <- intbus1 //thid rerforms the external reading of the register
	 * identified in the extbus
	 * 19. 10. pc -> intbus2 //pc.read() now pc must point the next instruction
	 * address
	 * 20. ula <- intbus2 //ula.store()
	 * 21. ula incs
	 * 22. ula -> intbus2 //ula.read()
	 * 23. pc <- intbus2 //pc.store()
	 * 
	 *//* */
	public void moveRegMem() {
		PC.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore(); // now PC points to the first parameter (the first reg id)
		PC.read();
		memory.read(); // the first register id is now in the external bus.
		demux.setValue(extbus1.get());
		ula.inc();
		ula.read(1);
		PC.internalStore();
		PC.read();
		memory.read();
		memory.store();
		registersRead();
		memory.store();
		ula.inc();
		ula.read(1);
		PC.internalStore();
	}

	/**
	 * This method implements the microprogram for
	 * move <reg1> <reg2>
	 * In the machine language this command number is 9
	 * 
	 * The method reads the two register ids (<reg1> and <reg2>) from the memory, in
	 * positions just after the command, and
	 * copies the value from the <reg1> register to the <reg2> register
	 * 
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <- intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.store() now pc points to the first parameter
	 * 6. pc -> extbus //(pc.read())the address where is the position to be read is
	 * now in the external bus
	 * 7. memory reads from extbus //this forces memory to write the parameter
	 * (first regID) in the extbus
	 * 8. pc -> intbus2 //pc.read() //getting the second parameter
	 * 9. ula <- intbus2 //ula.store()
	 * 10. ula incs
	 * 11. ula -> intbus2 //ula.read()
	 * 12. pc <- intbus2 //pc.store() now pc points to the second parameter
	 * 13. demux <- extbus //now the register to be operated is selected
	 * 14. registers -> intbus1 //this performs the internal reading of the selected
	 * register
	 * 15. PC -> extbus (pc.read())the address where is the position to be read is
	 * now in the external bus
	 * 16. memory reads from extbus //this forces memory to write the parameter
	 * (second regID) in the extbus
	 * 17. demux <- extbus //now the register to be operated is selected
	 * 18. registers <- intbus1 //thid rerforms the external reading of the register
	 * identified in the extbus
	 * 19. 10. pc -> intbus2 //pc.read() now pc must point the next instruction
	 * address
	 * 20. ula <- intbus2 //ula.store()
	 * 21. ula incs
	 * 22. ula -> intbus2 //ula.read()
	 * 23. pc <- intbus2 //pc.store()
	 * 
	 *//* */
	public void moveRegReg() {
		PC.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore(); // now PC points to the first parameter (the first reg id)
		PC.read();
		memory.read(); // the first register id is now in the external bus.
		demux.setValue(extbus1.get());
		registersRead();
		PC.store();
		PC.internalRead();
		IR.internalStore();
		ula.inc();
		ula.read(1);
		PC.internalStore();
		PC.read();
		memory.read();
		demux.setValue(extbus1.get());
		IR.internalRead();
		PC.internalStore();
		PC.read();
		registersStore();
		ula.inc();
		ula.read(1);
		PC.internalStore();
	}

	/**
	 * This method implements the microprogram for
	 * move <reg1> <reg2>
	 * In the machine language this command number is 9
	 * 
	 * The method reads the two register ids (<reg1> and <reg2>) from the memory, in
	 * positions just after the command, and
	 * copies the value from the <reg1> register to the <reg2> register
	 * 
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <- intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.store() now pc points to the first parameter
	 * 6. pc -> extbus //(pc.read())the address where is the position to be read is
	 * now in the external bus
	 * 7. memory reads from extbus //this forces memory to write the parameter
	 * (first regID) in the extbus
	 * 8. pc -> intbus2 //pc.read() //getting the second parameter
	 * 9. ula <- intbus2 //ula.store()
	 * 10. ula incs
	 * 11. ula -> intbus2 //ula.read()
	 * 12. pc <- intbus2 //pc.store() now pc points to the second parameter
	 * 13. demux <- extbus //now the register to be operated is selected
	 * 14. registers -> intbus1 //this performs the internal reading of the selected
	 * register
	 * 15. PC -> extbus (pc.read())the address where is the position to be read is
	 * now in the external bus
	 * 16. memory reads from extbus //this forces memory to write the parameter
	 * (second regID) in the extbus
	 * 17. demux <- extbus //now the register to be operated is selected
	 * 18. registers <- intbus1 //thid rerforms the external reading of the register
	 * identified in the extbus
	 * 19. 10. pc -> intbus2 //pc.read() now pc must point the next instruction
	 * address
	 * 20. ula <- intbus2 //ula.store()
	 * 21. ula incs
	 * 22. ula -> intbus2 //ula.read()
	 * 23. pc <- intbus2 //pc.store()
	 * 
	 *//* */
	public void moveImmReg() {
		PC.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		PC.internalStore(); // now PC points to the first parameter (the first reg id)
		PC.read();
		memory.read(); // the first register id is now in the external bus.
		PC.store();
		PC.internalRead();
		IR.internalStore();
		ula.inc();
		ula.read(1);
		PC.internalStore();
		PC.read();
		memory.read();
		demux.setValue(extbus1.get());
		IR.internalRead();
		PC.internalStore();
		PC.read();
		registersStore();
		ula.inc();
		ula.read(1);
		PC.internalStore();
	}

	public ArrayList<Register> getRegistersList() {
		return registersList;
	}

	/**
	 * This method performs an (external) read from a register into the register
	 * list.
	 * The register id must be in the demux bus
	 */
	private void registersRead() {
		registersList.get(demux.getValue()).read();
	}

	/**
	 * This method performs an (internal) read from a register into the register
	 * list.
	 * The register id must be in the demux bus
	 */
	public void registersInternalRead() {
		registersList.get(demux.getValue()).internalRead();
		;
	}

	/**
	 * This method performs an (external) store toa register into the register list.
	 * The register id must be in the demux bus
	 */
	private void registersStore() {
		registersList.get(demux.getValue()).store();
	}

	/**
	 * This method performs an (internal) store toa register into the register list.
	 * The register id must be in the demux bus
	 */
	public void registersInternalStore() {
		registersList.get(demux.getValue()).internalStore();
		;
	}

	/**
	 * This method reads an entire file in machine code and
	 * stores it into the memory
	 * NOT TESTED
	 * 
	 * @param filename
	 * @throws IOException
	 */
	public void readExec(String filename) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename + ".dxf"));
		String linha;
		int i = 0;
		while ((linha = br.readLine()) != null) {
			extbus1.put(i);
			memory.store();
			extbus1.put(Integer.parseInt(linha));
			memory.store();
			i++;
		}
		br.close();
	}

	/**
	 * This method executes a program that is stored in the memory
	 */
	public void controlUnitEexec() {
		halt = false;
		int c = 0;
		while (!halt) {
			if (c > 512) {
				break;
				// this is a security measure to avoid infinite loops
			}
			fetch();
			decodeExecute();
			c++;
		}

	}

	/**
	 * This method implements The decode proccess,
	 * that is to find the correct operation do be executed
	 * according the command.
	 * And the execute proccess, that is the execution itself of the command
	 */
	private void decodeExecute() {
		IR.internalRead(); // the instruction is in the internalbus2
		int command = extbus1.get();
		simulationDecodeExecuteBefore(command);
		switch (command) {

			case 0:
				addRegReg();
				break;

			case 1:
				addMemReg();
				break;
			case 2:
				addRegMem();
				break;
			case 3:
				subRegReg();
				break;
			case 4:
				subMemReg();
				break;
			case 5:
				subRegMem();
				break;
			/*
			 * case 6:
			 * imulMemReg();
			 * break;
			 * case 7:
			 * imulRegMem();
			 * break;
			 * case 8:
			 * imulRegReg();
			 * break;
			 */
			case 9:
				moveMemReg();
				break;
			case 10:
				moveRegMem();
				break;

			case 11:
				moveRegReg();
				break;

			case 12:
				moveImmReg();
				break;
			case 13:
				incReg();
				break;
			case 14:
				incMem();
				break;
			case 15:
				jmp();
				break;
			case 16:
				jn();
				break;
			case 17:
				jz();
				break;
			case 18:
				jnz();
				break;
			case 19:
				jeq();
				break;
			case 20:
				jgt();
				break;
			case 21:
				jlw();
				break;
			default:
				halt = true;
				break;
		}
		if (simulation)
			simulationDecodeExecuteAfter();
	}

	/**
	 * This method is used to show the components status in simulation conditions
	 * NOT TESTED
	 * 
	 * @param command
	 */
	private void simulationDecodeExecuteBefore(int command) {
		System.out.println("----------BEFORE Decode and Execute phases--------------");
		String instruction;
		int parameter = 0;
		for (Register r : registersList) {
			System.out.println(r.getRegisterName() + ": " + r.getData());
		}
		if (command != -1)
			instruction = commandsList.get(command);
		else
			instruction = "END";
		if (hasOperands(instruction)) {
			parameter = memory.getDataList()[PC.getData() + 1];
			System.out.println("Instruction: " + instruction + " " + parameter);
		} else
			System.out.println("Instruction: " + instruction);
		if ("read".equals(instruction))
			System.out.println("memory[" + parameter + "]=" + memory.getDataList()[parameter]);

	}

	/**
	 * This method is used to show the components status in simulation conditions
	 * NOT TESTED
	 */
	private void simulationDecodeExecuteAfter() {
		String instruction;
		System.out.println("-----------AFTER Decode and Execute phases--------------");
		System.out.println("Internal Bus 1: " + intbus1.get());
		System.out.println("Internal Bus 2: " + intbus2.get());
		System.out.println("External Bus 1: " + extbus1.get());
		for (Register r : registersList) {
			System.out.println(r.getRegisterName() + ": " + r.getData());
		}
		Scanner entrada = new Scanner(System.in);
		System.out.println("Press <Enter>");
		String mensagem = entrada.nextLine();
	}

	/**
	 * This method uses PC to find, in the memory,
	 * the command code that must be executed.
	 * This command must be stored in IR
	 * NOT TESTED!
	 */
	private void fetch() {
		PC.internalRead();
		StackTop.internalStore();
		PC.read();
		memory.read();
		PC.store();
		PC.internalRead();
		IR.internalStore();
		StackTop.internalRead();
		PC.internalStore();
		simulationFetch();
	}

	/**
	 * This method is used to show the components status in simulation conditions
	 * NOT TESTED!!!!!!!!!
	 */
	private void simulationFetch() {
		if (simulation) {
			System.out.println("-------Fetch Phase------");
			System.out.println("PC: " + PC.getData());
			System.out.println("IR: " + IR.getData());
		}
	}

	/**
	 * This method is used to show in a correct way the operands (if there is any)
	 * of instruction,
	 * when in simulation mode
	 * NOT TESTED!!!!!
	 * 
	 * @param instruction
	 * @return
	 */
	private boolean hasOperands(String instruction) {
		if ("inc".equals(instruction)) // inc is the only one instruction having no operands
			return false;
		else
			return true;
	}

	/**
	 * This method returns the amount of positions allowed in the memory
	 * of this architecture
	 * NOT TESTED!!!!!!!
	 * 
	 * @return
	 */
	public int getMemorySize() {
		return memorySize;
	}

	public static void main(String[] args) throws IOException {
		Architecture arch = new Architecture(true);
		arch.readExec("program");
		arch.controlUnitEexec();
	}

}
