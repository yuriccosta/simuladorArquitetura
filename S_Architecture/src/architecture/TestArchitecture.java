package architecture;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

import components.Memory;
import components.Register;
import java.util.concurrent.ArrayBlockingQueue;

public class TestArchitecture {
	
	//uncomment the anotation below to run the architecture showing components status
	//@Test
	public void testShowComponentes() {

		//a complete test (for visual purposes only).
		//a single code as follows
//		ldi 2
//		store 40
//		ldi -4
//		point:
//		store 41  //mem[41]=-4 (then -3, -2, -1, 0)
//		read 40
//		add 40    //mem[40] + mem[40]
//		store 40  //result must be in 40
//		read 41
//		inc
//		jn point
//		end
		
		Architecture arch = new Architecture(true);
		arch.getMemory().getDataList()[0]=7;
		arch.getMemory().getDataList()[1]=2;
		arch.getMemory().getDataList()[2]=6;
		arch.getMemory().getDataList()[3]=40;
		arch.getMemory().getDataList()[4]=7;
		arch.getMemory().getDataList()[5]=-4;
		arch.getMemory().getDataList()[6]=6;
		arch.getMemory().getDataList()[7]=41;
		arch.getMemory().getDataList()[8]=5;
		arch.getMemory().getDataList()[9]=40;
		arch.getMemory().getDataList()[10]=0;
		arch.getMemory().getDataList()[11]=40;
		arch.getMemory().getDataList()[12]=6;
		arch.getMemory().getDataList()[13]=40;
		arch.getMemory().getDataList()[14]=5;
		arch.getMemory().getDataList()[15]=41;
		arch.getMemory().getDataList()[16]=8;
		arch.getMemory().getDataList()[17]=4;
		arch.getMemory().getDataList()[18]=6;
		arch.getMemory().getDataList()[19]=-1;
		arch.getMemory().getDataList()[40]=0;
		arch.getMemory().getDataList()[41]=0;
		//now the program and the variables are stored. we can run
		arch.controlUnitEexec();
		
	}

	@Test
	public void testImulRegMem() {
		Architecture arch = new Architecture();
		//in this test, PC will point to 40
		arch.getExtbus1().put(40);
		arch.getPC().store();      //PC points to position 40
		
		// Precisamos que reg0 tenha um valor
		arch.getExtbus1().put(4);
		arch.getREG0().store();

		// Agora precisamos que logo após o comando add tenhamos o id do registrador
		// Registador 0 na posição 41
		arch.getExtbus1().put(41);
		arch.getMemory().store();
		arch.getExtbus1().put(0);
		arch.getMemory().store();
		
		//Agora precisamos que tenha um endereço na memória logo após o que guardava o id do registrador
		arch.getExtbus1().put(42);
		arch.getMemory().store();
		arch.getExtbus1().put(70);
		arch.getMemory().store();

		// Precisamos de um valor em mem[80]
		arch.getExtbus1().put(70);
		arch.getMemory().store();
		arch.getExtbus1().put(3);
		arch.getMemory().store();

		// Vamos colocar um jmp pro final do programa na posição 43
		// Para o controlUnitExec não se perder
		arch.getExtbus1().put(43);
		arch.getMemory().store();
		arch.getExtbus1().put(15);
		arch.getMemory().store();

		arch.getExtbus1().put(44);
		arch.getMemory().store();
		arch.getExtbus1().put(126);
		arch.getMemory().store();
		
		arch.getExtbus1().put(126);
		arch.getMemory().store();
		arch.getExtbus1().put(-1);
		arch.getMemory().store();


		//result must be into mem[70]
		//pc must be three positions ahead the original one
		arch.imulRegMem();
		arch.controlUnitEexec();
		
		arch.getExtbus1().put(70);
		arch.getMemory().read();
		
		//the bus must contains the number 20
		assertEquals(12, arch.getExtbus1().get());
		//the flags bits 0 and 1 must be 0
		assertEquals(0, arch.getFlags().getBit(0));
		assertEquals(0, arch.getFlags().getBit(1));
		//PC must be pointing to 13
		arch.getPC().read();
		assertEquals(43, arch.getExtbus1().get());
	}

	@Test
	public void testAddRegReg() {
		Architecture arch = new Architecture();
		//in this test, PC will point to 10
		arch.getExtbus1().put(10);
		arch.getPC().store();      //PC points to position 10

		// Precisamos que reg0 e reg1 tenham um valor cada
		arch.getExtbus1().put(20);
		arch.getREG0().store();
		arch.getExtbus1().put(5);
		arch.getREG1().store();
		
		// Agora precisamos que logo após o comando add tenhamos os ids dos registradores
		// Registrador 0 na posição 11
		arch.getExtbus1().put(11);
		arch.getMemory().store();
		arch.getExtbus1().put(0);
		arch.getMemory().store();

		// Registrador 1 na posição 12
		arch.getExtbus1().put(12);
		arch.getMemory().store();
		arch.getExtbus1().put(1);
		arch.getMemory().store();

		//result must be into reg1
		//pc must be three positions ahead the original one
		arch.addRegReg();
		arch.getREG1().read();
		//the bus must contains the number 25
		assertEquals(25, arch.getExtbus1().get());
		//the flags bits 0 and 1 must be 0
		assertEquals(0, arch.getFlags().getBit(0));
		assertEquals(0, arch.getFlags().getBit(1));
		//PC must be pointing to 13
		arch.getPC().read();
		assertEquals(13, arch.getExtbus1().get());
	}

	@Test
	public void testAddMemReg() {
		Architecture arch = new Architecture();
		//in this test, PC will point to 10
		arch.getExtbus1().put(20);
		arch.getPC().store();      //PC points to position 10
		
		// Precisamos que tenha um endereço na memória logo após o add
		arch.getExtbus1().put(21);
		arch.getMemory().store();
		arch.getExtbus1().put(127);
		arch.getMemory().store();

		// Precisamos que mem[127] tenha um valor
		arch.getExtbus1().put(127);
		arch.getMemory().store();
		arch.getExtbus1().put(37);
		arch.getMemory().store();

		// Precisamos que reg0 tenha um valor
		arch.getExtbus1().put(17);
		arch.getREG0().store();

		// Agora precisamos que logo após o mem[21] tenhamos o id do registrador
		// Registrador 0 na posição 22
		arch.getExtbus1().put(22);
		arch.getMemory().store();
		arch.getExtbus1().put(0);
		arch.getMemory().store();

		//result must be into reg[0]
		//pc must be three positions ahead the original one
		arch.addMemReg();
		arch.getREG0().read();

		//the bus must contains the number 20
		assertEquals(54, arch.getExtbus1().get());
		//the flags bits 0 and 1 must be 0
		assertEquals(0, arch.getFlags().getBit(0));
		assertEquals(0, arch.getFlags().getBit(1));
		//PC must be pointing to 13
		arch.getPC().read();
		assertEquals(23, arch.getExtbus1().get());
	}

	@Test
	public void testAddRegMem() {
		Architecture arch = new Architecture();
		//in this test, PC will point to 40
		arch.getExtbus1().put(40);
		arch.getPC().store();      //PC points to position 40
		
		// Precisamos que reg0 tenha um valor
		arch.getExtbus1().put(7);
		arch.getREG0().store();

		// Agora precisamos que logo após o comando add tenhamos o id do registrador
		// Registador 0 na posição 41
		arch.getExtbus1().put(41);
		arch.getMemory().store();
		arch.getExtbus1().put(0);
		arch.getMemory().store();
		
		//Agora precisamos que tenha um endereço na memória logo após o que guardava o id do registrador
		arch.getExtbus1().put(42);
		arch.getMemory().store();
		arch.getExtbus1().put(111);
		arch.getMemory().store();

		// Precisamos de um valor em mem[111]
		arch.getExtbus1().put(111);
		arch.getMemory().store();
		arch.getExtbus1().put(3);
		arch.getMemory().store();


		//result must be into mem[111]
		//pc must be three positions ahead the original one
		arch.addRegMem();
		arch.getExtbus1().put(111);
		arch.getMemory().read();
		
		//the bus must contains the number 20
		assertEquals(10, arch.getExtbus1().get());
		//the flags bits 0 and 1 must be 0
		assertEquals(0, arch.getFlags().getBit(0));
		assertEquals(0, arch.getFlags().getBit(1));
		//PC must be pointing to 13
		arch.getPC().read();
		assertEquals(43, arch.getExtbus1().get());
	}

	@Test
	public void testsubRegReg() {
		Architecture arch = new Architecture();
		//in this test, PC will point to 10
		arch.getExtbus1().put(10);
		arch.getPC().store();      //PC points to position 10

		// Precisamos que reg0 e reg1 tenham um valor cada
		arch.getExtbus1().put(20);
		arch.getREG0().store();
		arch.getExtbus1().put(5);
		arch.getREG1().store();

		// Agora precisamos que logo após o comando add tenhamos os ids dos registradores
		// Registrador 0 na posição 11
		arch.getExtbus1().put(11);
		arch.getMemory().store();
		arch.getExtbus1().put(0);
		arch.getMemory().store();

		// Registrador 1 na posição 12
		arch.getExtbus1().put(12);
		arch.getMemory().store();
		arch.getExtbus1().put(1);
		arch.getMemory().store();

		//result must be into reg1
		//pc must be three positions ahead the original one
		arch.subRegReg();
		arch.getREG1().read();
		//the bus must contains the number 15
		assertEquals(15, arch.getExtbus1().get());
		//the flags bits 0 and 1 must be 0
		assertEquals(0, arch.getFlags().getBit(0));
		assertEquals(0, arch.getFlags().getBit(1));
		//PC must be pointing to 13
		arch.getPC().read();
		assertEquals(13, arch.getExtbus1().get());
	}

	@Test
	public void testsubMemReg() {
		Architecture arch = new Architecture();
		//in this test, PC will point to 10
		arch.getExtbus1().put(20);
		arch.getPC().store();      //PC points to position 10
		
		// Precisamos que tenha um endereço na memória logo após o add
		arch.getExtbus1().put(21);
		arch.getMemory().store();
		arch.getExtbus1().put(127);
		arch.getMemory().store();

		// Precisamos que mem[127] tenha um valor
		arch.getExtbus1().put(127);
		arch.getMemory().store();
		arch.getExtbus1().put(37);
		arch.getMemory().store();

		// Precisamos que reg0 tenha um valor
		arch.getExtbus1().put(37);
		arch.getREG0().store();

		// Agora precisamos que logo após o mem[21] tenhamos o id do registrador
		// Registrador 0 na posição 22
		arch.getExtbus1().put(22);
		arch.getMemory().store();


		//result must be into reg[0]
		//pc must be three positions ahead the original one
		arch.subMemReg();
		arch.getREG0().read();

		//the bus must contains the number 20
		assertEquals(0, arch.getExtbus1().get());
		//the flags bits 0 and 1 must be 0
		assertEquals(1, arch.getFlags().getBit(0));
		assertEquals(0, arch.getFlags().getBit(1));
		//PC must be pointing to 23
		arch.getPC().read();
		assertEquals(23, arch.getExtbus1().get());
	}

	@Test
	public void testsubRegMem() {
		Architecture arch = new Architecture();
		//in this test, PC will point to 40
		arch.getExtbus1().put(40);
		arch.getPC().store();      //PC points to position 40
		
		// Precisamos que reg0 tenha um valor
		arch.getExtbus1().put(7);
		arch.getREG0().store();

		// Agora precisamos que logo após o comando add tenhamos o id do registrador
		// Registador 0 na posição 41
		arch.getExtbus1().put(41);
		arch.getMemory().store();
		arch.getExtbus1().put(0);
		arch.getMemory().store();
		
		//Agora precisamos que tenha um endereço na memória logo após o que guardava o id do registrador
		arch.getExtbus1().put(42);
		arch.getMemory().store();
		arch.getExtbus1().put(111);
		arch.getMemory().store();

		// Precisamos de um valor em mem[111]
		arch.getExtbus1().put(111);
		arch.getMemory().store();
		arch.getExtbus1().put(3);
		arch.getMemory().store();

		//result must be into mem[11]
		//pc must be three positions ahead the original one
		arch.subRegMem();
		arch.getExtbus1().put(111);
		arch.getMemory().read();
		
		//the bus must contains the number 4
		assertEquals(4, arch.getExtbus1().get());
		//the flags bits 0 and 1 must be 0
		assertEquals(0, arch.getFlags().getBit(0));
		assertEquals(0, arch.getFlags().getBit(1));
		//PC must be pointing to 43
		arch.getPC().read();
		assertEquals(43, arch.getExtbus1().get());
	}

	@Test
	public void testjmp(){
		Architecture arch = new Architecture();
		arch.getExtbus1().put(50);
		arch.getPC().store();      //PC aponta para posição 50

		// Precisamos que mem[51] tenha um valor
		arch.getExtbus1().put(51);
		arch.getMemory().store();
		arch.getExtbus1().put(105);
		arch.getMemory().store();

		// Agora basta chamar o método jmp
		arch.jmp();
		arch.getPC().read();
		assertEquals(105, arch.getExtbus1().get());
	}

	@Test
	public void testjz(){
		Architecture arch = new Architecture();

		//storing the number 66 in PC
		arch.getExtbus1().put(66);
		arch.getPC().store();      

		//storing the number 110 into the memory, in position 67, the position just after PC
		arch.getExtbus1().put(67);
		arch.getMemory().store();
		arch.getExtbus1().put(110);
		arch.getMemory().store();

		//now we can perform the jz method. 

		//CASE 1.
		//Bit ZERO is equals to 1

		arch.getFlags().setBit(0,1);

		//So, we will move the the number 110 (stored in the 67th position in the memory) 
		//into the PC

		//testing if PC stores the number 66
		arch.getPC().read();
		assertEquals(66, arch.getExtbus1().get());	

		// calling the jz function
		arch.jz();

		//PC must be pointing to 110
		arch.getPC().read();
		assertEquals(110, arch.getExtbus1().get());

		//CASE 2.
		//Bit ZERO is equals to 0

		arch.getFlags().setBit(0,0);

		//PC must have the number 66 initially
		arch.getExtbus1().put(66);
		arch.getPC().store();      

		//testing if PC stores the number 66
		arch.getPC().read();
		assertEquals(66, arch.getExtbus1().get());	

		//Note that the memory was not changed. So, in position 67 we have the number 110
		
		//Once the ZERO bit is 0, we WILL NOT move the number 110 (stored in the 67th position in the memory)
		//into the PC.
		//The original PC position was 66. The parameter is in position 67. So, now PC must be pointing to 68

		// calling the jz function
		arch.jz();

		//PC contains the number 68
		arch.getPC().read();
		assertEquals(68, arch.getExtbus1().get());

	}

	@Test
	public void testjnz(){
		Architecture arch = new Architecture();

		//storing the number 70 in PC
		arch.getExtbus1().put(70);
		arch.getPC().store();      

		//storing the number 105 into the memory, in position 71, the position just after PC
		arch.getExtbus1().put(71);
		arch.getMemory().store();
		arch.getExtbus1().put(105);
		arch.getMemory().store();

		//now we can perform the jnz method. 

		//CASE 1.
		//Bit ZERO is equals to 1

		arch.getFlags().setBit(0,1);

		//So, we will NOT move the the number 105 (stored in the 71th position in the memory) 
		//into the PC

		//testing if PC stores the number 70
		arch.getPC().read();
		assertEquals(70, arch.getExtbus1().get());	

		// calling the jz function
		arch.jnz();

		//PC must be pointing to 72
		arch.getPC().read();
		assertEquals(72, arch.getExtbus1().get());

		//CASE 2.
		//Bit ZERO is equals to 0

		arch.getFlags().setBit(0,0);

		//PC must have the number 70 initially
		arch.getExtbus1().put(70);
		arch.getPC().store();      

		//testing if PC stores the number 70
		arch.getPC().read();
		assertEquals(70, arch.getExtbus1().get());	

		//Note that the memory was not changed. So, in position 71 we have the number 105
		
		//Once the ZERO bit is 0, we WILL move the number 105 (stored in the 71th position in the memory)
		//into the PC.
		//The original PC position was 70. The parameter is in position 71. So, now PC must be pointing to 105

		arch.jnz();
		//PC contains the number 68
		arch.getPC().read();
		assertEquals(105, arch.getExtbus1().get());

	}

	@Test
	public void testjn(){
		Architecture arch = new Architecture();

		//storing the number 75 in PC
		arch.getExtbus1().put(75);
		arch.getPC().store();      

		//storing the number 108 into the memory, in position 76, the position just after PC
		arch.getExtbus1().put(76);
		arch.getMemory().store();
		arch.getExtbus1().put(108);
		arch.getMemory().store();

		//now we can perform the jn method. 

		//CASE 1.
		//Bit NEGATIVE is equals to 1

		arch.getFlags().setBit(1,1);

		//So, we will move the the number 108 (stored in the 76th position in the memory) 
		//into the PC

		//testing if PC stores the number 75
		arch.getPC().read();
		assertEquals(75, arch.getExtbus1().get());	

		// calling the jn function
		arch.jn();

		//the flag bit 1 must be 1
		assertEquals(1, arch.getFlags().getBit(1));

		//PC must be pointing to 108
		arch.getPC().read();
		assertEquals(108, arch.getExtbus1().get());

		//CASE 2.
		//Bit NEGATIVE is equals to 0

		arch.getFlags().setBit(1,0);

		//PC must have the number 75 initially
		arch.getExtbus1().put(75);
		arch.getPC().store();      

		//testing if PC stores the number 75
		arch.getPC().read();
		assertEquals(75, arch.getExtbus1().get());	

		//Note that the memory was not changed. So, in position 76 we have the number 108
		
		//Once the NEGATIVE bit is 0, we WILL NOT move the number 108 (stored in the 76th position in the memory)
		//into the PC.
		//The original PC position was 75. The parameter is in position 76. So, now PC must be pointing to 77

		// calling the jn function
		arch.jn();

		//PC must contain the number 77
		arch.getPC().read();
		assertEquals(77, arch.getExtbus1().get());

	}

	@Test
	public void testjeq(){
		Architecture arch = new Architecture();

		//storing the number 80 in PC
		arch.getExtbus1().put(80);
		arch.getPC().store();

		// seting values to Reg0 and Reg1
		arch.getExtbus1().put(30);
		arch.getREG0().store();
		arch.getExtbus1().put(30);
		arch.getREG1().store();

		// We need the id of the reg
		// That's why we set the reg0 into the 81th position
		arch.getExtbus1().put(81);
		arch.getMemory().store();
		arch.getExtbus1().put(0);
		arch.getMemory().store();

		// We need the id of the reg
		// That's why we set the reg0 into the 82th position
		arch.getExtbus1().put(82);
		arch.getMemory().store();
		arch.getExtbus1().put(1);
		arch.getMemory().store();

		//storing the number 100 into the memory, in position 83, the position just after PC
		arch.getExtbus1().put(83);
		arch.getMemory().store();
		arch.getExtbus1().put(100);
		arch.getMemory().store();

		//now we can perform the jeq method. 

		//CASE 1.
		//Reg0 equals to Reg1

		//So, we will move the the number 100 (stored in the 83th position in the memory) 
		//into the PC

		//testing if PC stores the number 80
		arch.getPC().read();
		assertEquals(80, arch.getExtbus1().get());	

		// calling the jeq function
		arch.jeq();

		//PC must be pointing to 100
		arch.getPC().read();
		assertEquals(100, arch.getExtbus1().get());

		//CASE 2.
		//Reg0 is not equal to Reg1

		// we need to change the value in one of the regs
		arch.getExtbus1().put(3);
		arch.getREG0().store();

		//PC must have the number 80 initially
		arch.getExtbus1().put(80);
		arch.getPC().store();      

		//testing if PC stores the number 80
		arch.getPC().read();
		assertEquals(80, arch.getExtbus1().get());	

		//Note that the memory was not changed. So, in position 83 we have the number 100
		
		//Once Reg0 is not equal to Reg1, we WILL NOT move the number 100 (stored in the 83th position in the memory)
		//into the PC.
		//The original PC position was 80. The parameter is in position 83. So, now PC must be pointing to 84

		// calling the jeq function
		arch.jeq();

		//PC must be pointing to 84
		arch.getPC().read();
		assertEquals(84, arch.getExtbus1().get());
	}

	@Test
	public void testjgt(){
		Architecture arch = new Architecture();

		//storing the number 85 in PC
		arch.getExtbus1().put(85);
		arch.getPC().store();

		// we need reg0 and reg1 to have one value each
		arch.getExtbus1().put(60);
		arch.getREG0().store();
		arch.getExtbus1().put(30);
		arch.getREG1().store();

        // We need the id of the reg
		// That's why we set the reg0 into the 86th position
		arch.getExtbus1().put(86);
		arch.getMemory().store();
		arch.getExtbus1().put(0);
		arch.getMemory().store();

		// We need the id of the reg
		// That's why we set the reg1 into the 87th position
		arch.getExtbus1().put(87);
		arch.getMemory().store();
		arch.getExtbus1().put(1);
		arch.getMemory().store();

		//storing the number 106 into the memory, in position 88, the position just after PC
		arch.getExtbus1().put(88);
		arch.getMemory().store();
		arch.getExtbus1().put(106);
		arch.getMemory().store();

		//now we can perform the jgt method. 

		//CASE 1.
		//Reg0 is greater than Reg1 (Reg0 > Reg1)

		//So, we will move the the number 106 (stored in the 88th position in the memory) 
		//into the PC

		//testing if PC stores the number 85
		arch.getPC().read();
		assertEquals(85, arch.getExtbus1().get());

		// calling the jgt function
		arch.jgt();

		//PC must be pointing to 106
		arch.getPC().read();
		assertEquals(106, arch.getExtbus1().get());

		//CASE 2.
		//Reg0 is NOT greater than Reg1 (Reg0 < Reg1)

		// we need to change the value in one of the regs
		arch.getExtbus1().put(10);
		arch.getREG0().store();

		//PC must have the number 85 initially
		arch.getExtbus1().put(85);
		arch.getPC().store();      

		//testing if PC stores the number 85
		arch.getPC().read();
		assertEquals(85, arch.getExtbus1().get());	

		//Note that the memory was not changed. So, in position 88 we have the number 106
		
		//Once Reg0 is not greater than Reg1, we WILL NOT move the number 106 (stored in the 88th position in the memory)
		//into the PC.
		//The original PC position was 85. The parameter is in position 88. So, now PC must be pointing to 89

		// calling the jgt function
		arch.jgt();

		//PC must be pointing to 89
		arch.getPC().read();
		assertEquals(89, arch.getExtbus1().get());
	}

	@Test
	public void testjlw(){
		Architecture arch = new Architecture();

		//storing the number 90 in PC
		arch.getExtbus1().put(90);
		arch.getPC().store();

		// we need reg0 and reg1 to have one value each
		arch.getExtbus1().put(30);
		arch.getREG0().store();
		arch.getExtbus1().put(60);
		arch.getREG1().store();

        // We need the id of the reg
		// That's why we set the reg0 into the 91th position
		arch.getExtbus1().put(91);
		arch.getMemory().store();
		arch.getExtbus1().put(0);
		arch.getMemory().store();

		// We need the id of the reg
		// That's why we set the reg1 into the 92th position
		arch.getExtbus1().put(92);
		arch.getMemory().store();
		arch.getExtbus1().put(1);
		arch.getMemory().store();

		//storing the number 109 into the memory, in position 93, the position just after PC
		arch.getExtbus1().put(93);
		arch.getMemory().store();
		arch.getExtbus1().put(109);
		arch.getMemory().store();

		//now we can perform the jlw method. 

		//CASE 1.
		//Reg0 is less than Reg1 (Reg0 < Reg1)

		//So, we will move the the number 109 (stored in the 93th position in the memory) 
		//into the PC

		//testing if PC stores the number 90
		arch.getPC().read();
		assertEquals(90, arch.getExtbus1().get());

		// calling the jlw function
		arch.jlw();

		//PC must be pointing to 109
		arch.getPC().read();
		assertEquals(109, arch.getExtbus1().get());

		//CASE 2.
		//Reg0 is NOT less than Reg1 (Reg0 > Reg1)

		// we need to change the value in one of the regs
		arch.getExtbus1().put(90);
		arch.getREG0().store();

		//PC must have the number 90 initially
		arch.getExtbus1().put(90);
		arch.getPC().store();      

		//testing if PC stores the number 90
		arch.getPC().read();
		assertEquals(90, arch.getExtbus1().get());	

		//Note that the memory was not changed. So, in position 93 we have the number 109
		
		//Once Reg0 is not less than Reg1, we WILL NOT move the number 109 (stored in the 93th position in the memory)
		//into the PC.
		//The original PC position was 90. The parameter is in position 93. So, now PC must be pointing to 94

		// calling the jlw function
		arch.jlw();

		//PC must be pointing to 94
		arch.getPC().read();
		assertEquals(94, arch.getExtbus1().get());

	}
	
	@Test
	public void testIncReg() {
		Architecture arch = new Architecture();
		//in this test, PC will point to 15
		arch.getExtbus1().put(15);
		arch.getPC().store();      //PC points to position 15

		// Precisamos que reg0 tenha um valor 
		arch.getExtbus1().put(20);
		arch.getREG0().store();

		// Agora precisamos que logo após o comando inc tenhamos o id do registrador
		// Registrador 0 na posição 16
		arch.getExtbus1().put(16);
		arch.getMemory().store();
		arch.getExtbus1().put(0);
		arch.getMemory().store();

		//result must be into reg0
		//pc must be three positions ahead the original one
		arch.incReg();
		arch.getREG0().read();
		//the bus must contains the number 21
		assertEquals(21, arch.getExtbus1().get());
		//the flags bits 0 and 1 must be 0
		assertEquals(0, arch.getFlags().getBit(0));
		assertEquals(0, arch.getFlags().getBit(1));
		//PC must be pointing to 17
		arch.getPC().read();
		assertEquals(17, arch.getExtbus1().get());
	}

	@Test
	public void testIncMem() {
		Architecture arch = new Architecture();
		//in this test, PC will point to 30
		arch.getExtbus1().put(30);
		arch.getPC().store();      //PC points to position 30

		// Precisamos que tenha um endereço na memória logo após o inc
		arch.getExtbus1().put(31);
		arch.getMemory().store();
		arch.getExtbus1().put(55);
		arch.getMemory().store();

		// Precisamos que mem[55] tenha um valor
		arch.getExtbus1().put(55);
		arch.getMemory().store();
		arch.getExtbus1().put(11);
		arch.getMemory().store();

		//result must be into memory[55]
		//pc must be three positions ahead the original one
		arch.incMem();
		arch.getExtbus1().put(55);
		arch.getMemory().read();
		//the bus must contains the number 12
		assertEquals(12, arch.getExtbus1().get());
		//the flags bits 0 and 1 must be 0
		assertEquals(0, arch.getFlags().getBit(0));
		assertEquals(0, arch.getFlags().getBit(1));
		//PC must be pointing to 32
		arch.getPC().read();
		assertEquals(32, arch.getExtbus1().get());
	}

	@Test
	public void testmoveMemReg() {
		Architecture arch = new Architecture();
		//in this test, PC will point to 33
		arch.getExtbus1().put(33);
		arch.getPC().store();      //PC points to position 33
		
		// Precisamos que tenha um endereço na memória 
		arch.getExtbus1().put(34);
		arch.getMemory().store();
		arch.getExtbus1().put(77);
		arch.getMemory().store();

		// Precisamos que mem[77] tenha um valor
		arch.getExtbus1().put(77);
		arch.getMemory().store();
		arch.getExtbus1().put(17);
		arch.getMemory().store();

		// Agora precisamos do id do registrador
		// Registrador 0 na posição 22
		arch.getExtbus1().put(35);
		arch.getMemory().store();
		arch.getExtbus1().put(0);
		arch.getMemory().store();

		//result must be into reg[0]
		//pc must be three positions ahead the original one
		arch.moveMemReg();
		arch.getREG0().read();

		//the bus must contains the number 17
		assertEquals(17, arch.getExtbus1().get());
		//PC must be pointing to 36
		arch.getPC().read();
		assertEquals(36, arch.getExtbus1().get());
	}


	@Test
	public void testmoveRegMem() {
		Architecture arch = new Architecture();
		//in this test, PC will point to 27
		arch.getExtbus1().put(27);
		arch.getPC().store();      //PC points to position 27
		
		// Precisamos que reg0 tenha um valor
		arch.getExtbus1().put(7);
		arch.getREG0().store();

		// Agora precisamos que logo após o comando move tenhamos o id do registrador
		// Registador 0 na posição 41
		arch.getExtbus1().put(28);
		arch.getMemory().store();
		arch.getExtbus1().put(0);
		arch.getMemory().store();
		
		//Agora precisamos que tenha um endereço na memória logo após o que guardava o id do registrador
		arch.getExtbus1().put(29);
		arch.getMemory().store();
		arch.getExtbus1().put(66);
		arch.getMemory().store();

		//result must be into mem[111]
		//pc must be three positions ahead the original one
		arch.moveRegMem();
		arch.getExtbus1().put(66);
		arch.getMemory().read();

		//the bus must contains the number 10
		assertEquals(7, arch.getExtbus1().get());
		//PC must be pointing to 22
		arch.getPC().read();
		assertEquals(30, arch.getExtbus1().get());
	}

	@Test
	public void testmoveRegReg() {
		Architecture arch = new Architecture();
		//in this test, PC will point to 19
		arch.getExtbus1().put(19);
		arch.getPC().store();      //PC points to position 19

		// Precisamos que reg0 tenha um valor
		arch.getExtbus1().put(10);
		arch.getREG0().store();

		// Agora precisamos que logo após o comando move tenhamos os ids dos registradores
		// Registrador 0 na posição 20
		arch.getExtbus1().put(20);
		arch.getMemory().store();
		arch.getExtbus1().put(0);
		arch.getMemory().store();

		// Registrador 1 na posição 21
		arch.getExtbus1().put(21);
		arch.getMemory().store();
		arch.getExtbus1().put(1);
		arch.getMemory().store();

		//result must be into reg1
		//pc must be three positions ahead the original one
		arch.moveRegReg();
		arch.getREG1().read();
		//the bus must contains the number 10
		assertEquals(10, arch.getExtbus1().get());
		//PC must be pointing to 22
		arch.getPC().read();
		assertEquals(22, arch.getExtbus1().get());
	}

	@Test
	public void testImmRegReg() {
		Architecture arch = new Architecture();
		//in this test, PC will point to 19
		arch.getExtbus1().put(19);
		arch.getPC().store();      //PC points to position 19

		// Precisamos que reg0 tenha um valor
		arch.getExtbus1().put(10);
		arch.getREG0().store();

		// Agora precisamos que logo após o comando move tenhamos os ids dos registradores
		// Registrador 0 na posição 20
		arch.getExtbus1().put(20);
		arch.getMemory().store();
		arch.getExtbus1().put(0);
		arch.getMemory().store();

		// Registrador 1 na posição 21
		arch.getExtbus1().put(21);
		arch.getMemory().store();
		arch.getExtbus1().put(1);
		arch.getMemory().store();

		//result must be into reg1
		//pc must be three positions ahead the original one
		arch.moveRegReg();
		arch.getREG1().read();
		//the bus must contains the number 10
		assertEquals(10, arch.getExtbus1().get());
		//PC must be pointing to 22
		arch.getPC().read();
		assertEquals(22, arch.getExtbus1().get());
	}
	
	// @Test
	// public void testSub() {
	// 	Architecture arch = new Architecture();
		
	// 	/*************************
	// 	 * first test: 5 (rpg) - 8 (mem-40) = -3 (rpg)
	// 	 ***********************************************/
		
	// 	//storing the number 8 i the memory, in position 40
	// 	arch.getExtbus1().put(40);
	// 	arch.getMemory().store();
	// 	arch.getExtbus1().put(8);
	// 	arch.getMemory().store();
	// 	//storing the number 5 in the external bus
	// 	arch.getExtbus1().put(5);
	// 	//moving this number 5 into the rpg
	// 	arch.getRPG().store();
		
	// 	//moreover, the number 40 must be in the position next to PC. (to perform sub 40)
	// 	//in this test, PC will point to 10 and the 40 will be in the position 11
	// 	arch.getExtbus1().put(11);
	// 	arch.getMemory().store();
	// 	arch.getExtbus1().put(40); //40 is in position 11
	// 	arch.getMemory().store();
	// 	arch.getExtbus1().put(10);
	// 	arch.getPC().store();      //PC points to position 10
		
	// 	//now we can perform the sub method. 
	// 	//we will sub, from the number 5 (stored in the rpg) 
	// 	//the number 8 (stored in the memory, position 40)
	// 	//result must be into rpg
	// 	arch.sub();
	// 	arch.getRPG().read();
	// 	//the bus must contains the number -3
	// 	assertEquals(-3, arch.getExtbus1().get());
		
	// 	//flags bits must be 0 (bit zero) and 1 (bit negative)
	// 	assertEquals(0, arch.getFlags().getBit(0));
	// 	assertEquals(1, arch.getFlags().getBit(1));
		
		
	// 	//PC must be pointing to 12
	// 	arch.getPC().read();
	// 	assertEquals(12, arch.getExtbus1().get());

	// 	/*************************
	// 	 * second test: 5 (rpg) - 5 (mem-50) = 0 (rpg)
	// 	 ***********************************************/
		
	// 	//storing the number 5 in the memory, in position 50
	// 	arch.getExtbus1().put(50);
	// 	arch.getMemory().store();
	// 	arch.getExtbus1().put(5);
	// 	arch.getMemory().store();
	// 	//storing the number 5 in the external bus
	// 	arch.getExtbus1().put(5);
	// 	//moving this number 5 into the rpg
	// 	arch.getRPG().store();
		
	// 	//moreover, the number 50 must be in the position next to PC. (to perform sub 50)
	// 	//in this test, PC will point to 12 and the 50 will be in the position 13
	// 	arch.getExtbus1().put(13);
	// 	arch.getMemory().store();
	// 	arch.getExtbus1().put(50); //50 is in position 13
	// 	arch.getMemory().store();
	// 	arch.getExtbus1().put(12);
	// 	arch.getPC().store();      //PC points to position 12
		
		
	// 	//now we can perform the sub method. 
	// 	//we will sub, from the number 5 (stored in the rpg) 
	// 	//the number 5 (already stored in the memory, position 50)
	// 	//result must be into rpg
	// 	arch.sub();
	// 	arch.getRPG().read();
	// 	//the bus must contains the number 0
	// 	assertEquals(0, arch.getExtbus1().get());
		
	// 	//flags bits must be 1 (bit zero) and 0 (bit negative)
	// 	assertEquals(1, arch.getFlags().getBit(0));
	// 	assertEquals(0, arch.getFlags().getBit(1));
		
	// 	//PC must be pointing to 14
	// 	arch.getPC().read();
	// 	assertEquals(14, arch.getExtbus1().get());
		
	// 	/*************************
	// 	 * third test: 5 (rpg) - 3 (mem-60) = 2 (rpg)
	// 	 ***********************************************/
		
	// 	//storing the number 3 in the memory, in position 60
	// 	arch.getExtbus1().put(60);
	// 	arch.getMemory().store();
	// 	arch.getExtbus1().put(3);
	// 	arch.getMemory().store();
	// 	//storing the number 5 in the external bus
	// 	arch.getExtbus1().put(5);
	// 	//moving this number 5 into the rpg
	// 	arch.getRPG().store();
		
	// 	//moreover, the number 60 must be in the position next to PC. (to perform sub 60)
	// 	//in this test, PC will point to 14 and the 60 will be in the position 15
	// 	arch.getExtbus1().put(15);
	// 	arch.getMemory().store();
	// 	arch.getExtbus1().put(60); //60 is in position 15
	// 	arch.getMemory().store();
	// 	arch.getExtbus1().put(14);
	// 	arch.getPC().store();      //PC points to position 14
		
		
	// 	//now we can perform the sub method. 
	// 	//we will sub, from the number 5 (stored in the rpg) 
	// 	//the number 3 (already stored in the memory, position 60)
	// 	//result must be into rpg
	// 	arch.sub();
	// 	arch.getRPG().read();
	// 	//the bus must contains the number 2
	// 	assertEquals(2, arch.getExtbus1().get());
		
	// 	//flags bits must be 0 (bit zero) and 0 (bit negative)
	// 	assertEquals(0, arch.getFlags().getBit(0));
	// 	assertEquals(0, arch.getFlags().getBit(1));
		
	// 	//PC must be pointing to 16
	// 	arch.getPC().read();
	// 	assertEquals(16, arch.getExtbus1().get());
	// }
	
	// @Test
	// public void testJmp() {
	// 	Architecture arch = new Architecture();
	// 	//storing the number 10 in PC
	// 	arch.getIntbus2().put(10);
	// 	arch.getPC().internalStore();

	// 	//storing the number 25 in the memory, in the position just before that one adressed by PC
	// 	arch.getExtbus1().put(11); //the position is 11, once PC points to 10
	// 	arch.getMemory().store();
	// 	arch.getExtbus1().put(25);
	// 	arch.getMemory().store();
		
		
	// 	//testing if PC stores the number 10
	// 	arch.getPC().read();
	// 	assertEquals(10, arch.getExtbus1().get());
		
	// 	//now we can perform the jmp method. 
	// 	//we will move the the number 25 (stored in the 31th position in the memory) 
	// 	//into the PC
	// 	arch.jmp();
	// 	arch.getPC().internalRead();;
	// 	//the internalbus2 must contains the number 25
	// 	assertEquals(25, arch.getIntbus2().get());

	// }
	
	// @Test
	// public void testJz() {
	// 	Architecture arch = new Architecture();
		
	// 	//storing the number 30 in PC
	// 	arch.getIntbus2().put(30);
	// 	arch.getPC().internalStore();
		
	// 	//storing the number 25 in the into the memory, in position 31, the position just after PC
	// 	arch.getExtbus1().put(31);
	// 	arch.getMemory().store();
	// 	arch.getExtbus1().put(25);
	// 	arch.getMemory().store();

	// 	//now we can perform the jz method. 

	// 	//CASE 1.
	// 	//Bit ZERO is equals to 1
	// 	arch.getFlags().setBit(0, 1);
		
	// 	//So, we will move the the number 25 (stored in the 31th position in the memory) 
	// 	//into the PC

	// 	//testing if PC stores the number 30
	// 	arch.getPC().read();
	// 	assertEquals(30, arch.getExtbus1().get());		

	// 	arch.jz();
		
	// 	//PC must be storing the number 25
	// 	arch.getPC().internalRead();
	// 	assertEquals(25, arch.getIntbus2().get());
		
	// 	//CASE 2.
	// 	//Bit ZERO is equals to 0
	// 	arch.getFlags().setBit(0, 0);
	// 	//PC must have the number 30 initially (in this time, by using the external bus)
	// 	arch.getExtbus1().put(30);
	// 	arch.getPC().store();
	// 	//destroying the data in external bus
	// 	arch.getExtbus1().put(0);

	// 	//testing if PC stores the number 30
	// 	arch.getPC().read();
	// 	assertEquals(30, arch.getExtbus1().get());	
		
	// 	//Note that the memory was not changed. So, in position 31 we have the number 25
		
	// 	//Once the ZERO bit is 0, we WILL NOT move the number 25 (stored in the 31th position in the memory)
	// 	//into the PC.
	// 	//The original PC position was 30. The parameter is in position 31. So, now PC must be pointing to 32
	// 	arch.jz();
	// 	//PC contains the number 32
	// 	arch.getPC().internalRead();
	// 	assertEquals(32, arch.getIntbus2().get());
	// }
	
	// @Test
	// public void testJn() {
	// 	Architecture arch = new Architecture();
		
	// 	//storing the number 30 in PC
	// 	arch.getIntbus2().put(30);
	// 	arch.getPC().internalStore();
		
	// 	//storing the number 25 in the into the memory, in position 31, the position just after PC
	// 	arch.getExtbus1().put(31);
	// 	arch.getMemory().store();
	// 	arch.getExtbus1().put(25);
	// 	arch.getMemory().store();

	// 	//now we can perform the jn method. 

	// 	//CASE 1.
	// 	//Bit NEGATIVE is equals to 1
	// 	arch.getFlags().setBit(1, 1);
		
	// 	//So, we will move the the number 25 (stored in the 31th position in the memory) 
	// 	//into the PC

	// 	//testing if PC stores the number 30
	// 	arch.getPC().read();
	// 	assertEquals(30, arch.getExtbus1().get());		

	// 	arch.jn();
		
	// 	//PC must be storng the number 25
	// 	arch.getPC().internalRead();
	// 	assertEquals(25, arch.getIntbus2().get());
		
	// 	//CASE 2.
	// 	//Bit NEGATIVE is equals to 0
	// 	arch.getFlags().setBit(1, 0);
	// 	//PC must have the number 30 initially (in this time, by using the external bus)
	// 	arch.getExtbus1().put(30);
	// 	arch.getPC().store();
	// 	//destroying the data in external bus
	// 	arch.getExtbus1().put(0);

	// 	//testing if PC stores the number 30
	// 	arch.getPC().read();
	// 	assertEquals(30, arch.getExtbus1().get());	
		
	// 	//Note that the memory was not changed. So, in position 31 we have the number 25
		
	// 	//Once the ZERO bit is 0, we WILL NOT move the number 25 (stored in the 31th position in the memory)
	// 	//into the PC.
	// 	//The original PC position was 30. The parameter is in position 31. So, now PC must be pointing to 32
	// 	arch.jn();
	// 	//PC contains the number 32
	// 	arch.getPC().internalRead();
	// 	assertEquals(32, arch.getIntbus2().get());
	// }
	
	// @Test
	// public void testRead() {
	// 	Architecture arch = new Architecture();
	// 	//storing the number 10 in RPG
	// 	arch.getIntbus1().put(10);
	// 	arch.getRPG().internalStore();
	// 	//testing if RPG stores the number 10
	// 	arch.getRPG().read();
	// 	assertEquals(10, arch.getExtbus1().get());
		
	// 	//storing the number 25 in the memory, in position 31
	// 	arch.getExtbus1().put(31);
	// 	arch.getMemory().store();
	// 	arch.getExtbus1().put(25);
	// 	arch.getMemory().store();
		
	// 	//storing the number -100 in the memory, in position 25
	// 	arch.getExtbus1().put(25);
	// 	arch.getMemory().store();
	// 	arch.getExtbus1().put(-100);
	// 	arch.getMemory().store();
		
	// 	//PC must be pointing to the address just before the parameter (where is the instruction)
	// 	arch.getExtbus1().put(30);
	// 	arch.getPC().store();

	// 	//now we can perform the read method. 
	// 	//scenery PC=30, mem[31]=25, mem[25]=-100 RPG=10
	// 	//we will move the the number 25 (stored in the 31th position in the memory) 
	// 	//into the RPG by using the move command (move 31)

	// 	arch.read();
	// 	arch.getRPG().internalRead();
	// 	//the internalbus1 must contain the number -100 (that is stored in position 25)
	// 	assertEquals(-100, arch.getRPG().getData());
		
	// 	//PC must be pointing two positions after its original value
	// 	arch.getPC().read();
	// 	assertEquals(32, arch.getExtbus1().get());

	// }
	
	// @Test
	// public void testStore() {
	// 	Architecture arch = new Architecture();

	// 	//storing the number 25 in the memory, in position 31
	// 	arch.getMemory().getDataList()[31]=25;
		

	// 	//now we can perform the store method. 
	// 	//store X stores, in the position X, the data that is currently in RPG
		
	// 	//let's put PC pointing to the position 11
	// 	arch.getExtbus1().put(11);
	// 	arch.getPC().store();
	// 	//now lets put the parameter (the position where the data will be stored) into the position next to PC
	// 	arch.getMemory().getDataList()[12]=31;
		
	// 	//storing the number 155 in RPG
	// 	arch.getIntbus1().put(155);
	// 	arch.getRPG().internalStore();
		
	// 	//testing if memory contains the number 25 in the 31th position
	// 	arch.getExtbus1().put(31);
	// 	arch.getMemory().read();
	// 	assertEquals(25, arch.getExtbus1().get());
		
	// 	//So, PC is pointing to memory[11], memory[12] has 31, memory [31] has 25 and RPG has 155
		
	// 	//destroying data in externalbus 1
	// 	arch.getExtbus1().put(0);

	// 	arch.store();

	// 	//now, memory[31] must be updated from 25 to 155
	// 	assertEquals(155, arch.getMemory().getDataList()[31]);
		
	// 	//PC must be pointing two positions after its original value
	// 	arch.getPC().read();
	// 	assertEquals(13, arch.getExtbus1().get());


	// }
	
	// @Test
	// public void testLdi() {
	// 	Architecture arch = new Architecture();
	// 	//storing the number 10 in RPG
	// 	arch.getIntbus1().put(10);
	// 	arch.getRPG().internalStore();
		
	// 	//the scenery: PC points to 50, mem[51] (parameter) is -40
		
	// 	arch.getExtbus1().put(51);
	// 	arch.getMemory().store();
	// 	arch.getExtbus1().put(-40);
	// 	arch.getMemory().store();
		
	// 	arch.getExtbus1().put(50);
	// 	arch.getPC().store();
		
	// 	//destroying data in internalbus 1
	// 	arch.getIntbus1().put(0);

	// 	//now we can perform the ldi method. 
	// 	//we will move the the number -40 (immediate value) 
	// 	//into the rpg
	// 	arch.ldi();
		
	// 	arch.getRPG().read();
	// 	//the externalbus1 must contains the number 44
	// 	assertEquals(-40, arch.getExtbus1().get());
		
	// 	//PC must be pointing two positions after its original value
	// 	arch.getPC().read();
	// 	assertEquals(52, arch.getExtbus1().get());

	// }
	
	// @Test
	// public void testInc() {
	// 	Architecture arch = new Architecture();
	// 	//storing the number 10 in RPG
	// 	arch.getExtbus1().put(10);
	// 	arch.getRPG().store();
	// 	//testing if RPG stores the number 10
	// 	arch.getRPG().read();
	// 	assertEquals(10, arch.getExtbus1().get());

	// 	//destroying data in externalbus 1
	// 	arch.getExtbus1().put(0);
		
	// 	//pc points to 50 (where we suppose the instruction is
	// 	arch.getExtbus1().put(50);
	// 	arch.getPC().store();

	// 	//now we can perform the inc method. 
	// 	arch.inc();
	// 	arch.getRPG().read();
	// 	//the externalbus1 must contains the number 11
	// 	assertEquals(11, arch.getExtbus1().get());
		
	// 	//PC must be pointing ONE position after its original value, because this command has no parameters!
	// 	arch.getPC().read();
	// 	assertEquals(51, arch.getExtbus1().get());

	// }
	
	// @Test
	// public void testMoveRegReg() {
	// 	Architecture arch = new Architecture();

	// 	//storing the number 1 in the memory, in position 31
	// 	arch.getMemory().getDataList()[31]=1;
	// 	//storing the number 0 in the memory, in position 32
	// 	arch.getMemory().getDataList()[32]=0;
	// 	//making PC points to position 30
	// 	arch.getExtbus1().put(30);
	// 	arch.getPC().store();
		
		
	// 	//now setting the registers values
	// 	arch.getExtbus1().put(45);
	// 	arch.getRegistersList().get(0).store(); //RPG0 has 45
	// 	arch.getExtbus1().put(99);
	// 	arch.getRegistersList().get(1).store(); //RPG1 has 99
		
	// 	//executing the command move REG1 REG0.
	// 	arch.moveRegReg();
		
	// 	//testing if both REG1 and REG0 store the same value: 99
	// 	arch.getRegistersList().get(0).read();
	// 	assertEquals(99, arch.getExtbus1().get());
	// 	arch.getRegistersList().get(1).read();
	// 	assertEquals(99, arch.getExtbus1().get());
		
	// 	//Testing if PC points to 3 positions after the original
	// 	//PC was pointing to 30; now it must be pointing to 33
	// 	arch.getPC().read();assertEquals(33, arch.getExtbus1().get());
	// }
		
	@Test
	public void testFillCommandsList() {
		
		//all the instructions must be in Commands List
		/*
		 *
				add addr (rpg <- rpg + addr)
				sub addr (rpg <- rpg - addr)
				jmp addr (pc <- addr)
				jz addr  (se bitZero pc <- addr)
				jn addr  (se bitneg pc <- addr)
				read addr (rpg <- addr)
				store addr  (addr <- rpg)
				ldi x    (rpg <- x. x must be an integer)
				inc    (rpg++)
				move %reg0 %reg1 (reg1 <- Reg0)
		 */

		
		Architecture arch = new Architecture();
		ArrayList<String> commands = arch.getCommandsList();
		assertTrue("addRegReg".equals(commands.get(0)));
        assertTrue("addMemReg".equals(commands.get(1)));
        assertTrue("addRegMem".equals(commands.get(2)));
        assertTrue("subRegReg".equals(commands.get(3)));
        assertTrue("subMemReg".equals(commands.get(4)));
        assertTrue("subRegMem".equals(commands.get(5)));
        assertTrue("imulMemReg".equals(commands.get(6)));
        assertTrue("imulRegMem".equals(commands.get(7)));
        assertTrue("imulRegReg".equals(commands.get(8)));
        assertTrue("moveMemReg".equals(commands.get(9)));
        assertTrue("moveRegMem".equals(commands.get(10)));
        assertTrue("moveRegReg".equals(commands.get(11)));
        assertTrue("moveImmReg".equals(commands.get(12)));
        assertTrue("incReg".equals(commands.get(13)));
        assertTrue("incMem".equals(commands.get(14)));
        assertTrue("jmp".equals(commands.get(15)));
        assertTrue("jn".equals(commands.get(16)));
        assertTrue("jz".equals(commands.get(17)));
        assertTrue("jnz".equals(commands.get(18)));
        assertTrue("jeq".equals(commands.get(19)));
        assertTrue("jgt".equals(commands.get(20)));
        assertTrue("jlw".equals(commands.get(21)));
		
	}
	
	@Test
	public void testReadExec() throws IOException {
		Architecture arch = new Architecture();
		arch.readExec("testFile");
		assertEquals(5, arch.getMemory().getDataList()[0]);
		assertEquals(4, arch.getMemory().getDataList()[1]);
		assertEquals(3, arch.getMemory().getDataList()[2]);
		assertEquals(2, arch.getMemory().getDataList()[3]);
		assertEquals(1, arch.getMemory().getDataList()[4]);
		assertEquals(0, arch.getMemory().getDataList()[5]);
	}

}
