// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/4/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)
// The algorithm is based on repetitive addition.

@R0         //Load R0 (multiplicand)
D=M         //store it in D-> D=RAM[0]
@R2         //Point to the result 
M=0         // set it equal to 0
@R1         //Load R1 (multiplier)
D=M
@MULTLOOP 
D;JEQ       //if R1 is zero we skip the loop otherwise we go to it

(LOOPSTART)
@R0
D=M         //store the multiplicand 
@R2
M=D+M       //add it to the result 
@R1
M=M-1       //reduce the size of R1 by 1
D=M         //store it to check if its 0. we continue adding until it's 0
@LOOPSTART
D;JGT       //continue the loop as long as R1 is bigger then 0

(END)
@END
0;JMP //halts the loop