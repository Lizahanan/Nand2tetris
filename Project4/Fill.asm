// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/4/Fill.asm

// Runs an infinite loop that listens to the keyboard input. 
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel. When no key is pressed, 
// the screen should be cleared.

//The main loop listens to the key press
(LOOP) //the start of the loop
    @KBD
    D=M     //D = RAM[KBD_Address]
    @BLACK 
    D;JNE //jump to (BLACK) if D is not 0
    @WHITE
    0;JMP //else we just go to white 
(BLACK)
    @SCREEN 
    D=A //store the address of the start of the screen memmory 
    @R0
    M=D //store the start of the screen memmory is R[0]
(B_LOOP)
//we start the black loop 
    @R0
    A=M //we store the address that is loaded in RAM[0] -> it basically serves as a container or pointer
    M=-1 //at that address we colour the screen black 
    @R0
    //at R0 we now need to increment the pointer 
    M=M+1
    //now we want to make sure that we dont go overboad 
    @24576 
    //the address of the end of the screen memorry 
    D=A //store it in data 
    @R0
    A=M
    D=D-A //we basically subtract the current pointer from the end of screen memorry
    @B_LOOP
    D;JGT // if D is bigger then zero we keep colouring
    @LOOP
    0;JMP // otherwise we jump back to the main loop

(WHITE)
    @SCREEN 
    D=A //store the address of the start of the screen memmory 
    @R0
    M=D //store the start of the screen memmory is R[0]
(W_LOOP)
//we start the white loop 
    @R0
    A=M //we store the address that is loaded in RAM[0] -> it basically serves as a container or pointer
    M=0 //at that address we colour the screen white 
    @R0
    //at R0 we now need to increment the pointer 
    M=M+1
    //now we want to make sure that we dont go overboad 
    @24576 
    //the address of the end of the screen memorry 
    D=A //store it in data 
    @R0
    A=M
    D=D-A //we basically subtract the current pointer from the end of screen memorry
    @W_LOOP
    D;JGT // if D is bigger then zero we keep colouring
    @LOOP
    0;JMP // otherwise we jump back to the main loop