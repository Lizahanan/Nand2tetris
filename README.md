# Nand2Tetris Projects

This repository contains my implementations of projects from the **Nand2Tetris** course, where I build a computer from the ground up, starting with logic gates and progressing to an operating system and high-level programming.


## Course Overview
The **Nand2Tetris** course takes a bottom-up approach to computer science, covering hardware, assembly, compilers, and operating systems. The projects in this repository follow the Reichman University course Digital Systems by Shimon Schocken.
In addition the course has been designed by Shimon Shocken and Noam Nisan

## Projects Breakdown

### **Project 1: Boolean Logic**
- Implements fundamental logic gates using only NAND gates.
- Includes AND, OR, NOT, XOR, MUX, and DEMUX and others
- Built using the Hardware Description Language (HDL).

### **Project 2: ALU (Arithmetic Logic Unit)**
- Constructs a 16-bit ALU capable of arithmetic and logical operations.
- Supports addition, bitwise operations, and negation.
- Basis for all future computations in the system.

### **Project 3: Sequential Logic**
- Introduces memory elements such as flip-flops and registers.
- Implements a 16-bit Register, RAM8, RAM64, and RAM16K.
- Constructs a Program Counter (PC) to control execution flow.

### **Project 4: Machine Language**
- Develops an assembly language for the Hack computer.
- Writes low-level programs such as loops and conditionals.
- Assembles code manually for execution in the Hack CPU.

### **Project 5: Computer Architecture**
- Builds the Hack CPU using the ALU and registers.
- Implements instruction execution (arithmetic, jumps, memory access).
- Combines components to form a fully functional computer.

### **Project 6: Assembler**
- Writes a program to translate Hack assembly code into binary machine code.
- Implements a two-pass assembler to handle labels and symbols.
- Written in a high-level language 

### **Project 7: Virtual Machine - Part I**
- Develops a translator for a simple stack-based virtual machine (VM).
- Implements arithmetic, memory access, and stack operations.
- Translates VM code into Hack assembly language.

### **Project 8: Virtual Machine - Part II**
- Adds support for program control flow (if statements, loops, function calls).
- Implements the call stack and function return mechanisms.
- Translates complex VM code into Hack assembly.

### **Project 9: Building a Game**
- Uses the Jack language to develop a complete software project.
- Demonstrates understanding of the full hardware-software stack.
- Showcases creativity and problem-solving skills.

### **Project 10: Compiler**
- Constructs a tokenizer and parser for the Jack programming language.
- Converts Jack source code into an abstract syntax tree (AST).
- Implements code generation for the Jack language.
- Translates parsed Jack programs into VM instructions.
- Supports variable allocation, function calls, and control flow.

## How to Use
Each project contains:
- **HDL files** for hardware design.
- **Assembly files** for low-level programming.
- **VM and Jack files** for higher-level implementation.
- **Python/Java/C++ scripts** for software-based tools (Assembler, VM Translator, Compiler).

To run the projects, use the provided tools in the Nand2Tetris software suite.

## Acknowledgments
This project follows the **Nand2Tetris** curriculum, created by Noam Nisan and Shimon Schocken. More details can be found at [nand2tetris.org](https://www.nand2tetris.org/).

Projects 1 - 6 have been done solely by me 
Projects 7 - 11 have been done together with Noam Atzmon
During the course all high-level language projects have been completed in Java. After that i have also decided to complete it in Python. 

