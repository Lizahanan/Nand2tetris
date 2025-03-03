# Nand2Tetris Projects

This repository contains my implementations of projects from the **Nand2Tetris** course, where I build a computer from the ground up—starting with logic gates and progressing all the way to an operating system and high-level programming.

---

## Table of Contents

- [Course Overview](#course-overview)
- [Projects Breakdown](#projects-breakdown)
  - [Project 1: Boolean Logic](#project-1-boolean-logic)
  - [Project 2: ALU (Arithmetic Logic Unit)](#project-2-alu-arithmetic-logic-unit)
  - [Project 3: Sequential Logic](#project-3-sequential-logic)
  - [Project 4: Machine Language](#project-4-machine-language)
  - [Project 5: Computer Architecture](#project-5-computer-architecture)
  - [Project 6: Assembler](#project-6-assembler)
  - [Project 7: Virtual Machine - Part I](#project-7-virtual-machine---part-i)
  - [Project 8: Virtual Machine - Part II](#project-8-virtual-machine---part-ii)
  - [Project 9: Building a Game](#project-9-building-a-game)
  - [Project 11: Compiler](#project-10-compiler)
- [How to Use](#how-to-use)
- [Acknowledgments](#acknowledgments)
- [Credits and Contributions](#credits-and-contributions)

---

## Course Overview

The **Nand2Tetris** course takes a bottom-up approach to computer science, starting from hardware design and moving through assembly language, virtual machines, and high-level programming. The projects in this repository follow the Digital Systems curriculum from Reichman University, designed by Shimon Schocken and Noam Nisan, lectured by Shimon Schocken.

---

## Projects Breakdown

### Project 1: Boolean Logic
- **Focus:** Implementing fundamental logic gates using only NAND gates.
- **Key Components:** AND, OR, NOT, XOR, MUX, DEMUX, and more.
- **Implementation:** Built using the Hardware Description Language (HDL).

### Project 2: ALU (Arithmetic Logic Unit)
- **Focus:** Constructing a 16-bit ALU capable of arithmetic and logical operations.
- **Key Operations:** Addition, bitwise operations, and negation.
- **Importance:** Serves as the basis for all future computations in the system.

### Project 3: Sequential Logic
- **Focus:** Building memory elements such as flip-flops and registers.
- **Key Components:** 16-bit Register, RAM8, RAM64, RAM16K, and Program Counter (PC).

### Project 4: Machine Language
- **Focus:** Developing an assembly language for the Hack computer.
- **Key Skills:** Writing low-level programs (loops, conditionals) and manually assembling code.
- **Outcome:** Understanding the fundamentals of machine-level programming.

### Project 5: Computer Architecture
- **Focus:** Building the Hack CPU using components from previous projects.
- **Key Components:** ALU, registers, instruction execution (arithmetic, jumps, memory access).
- **Outcome:** Combining all hardware elements to form a functional computer.

### Project 6: Assembler
- **Focus:** Writing a program to translate Hack assembly code into binary machine code.
- **Key Features:** Implements a two-pass assembler for handling labels and symbols.
- **Languages:** Developed in both Java (as per course requirements) and Python (personal implementation).

### Project 7: Virtual Machine - Part I
- **Focus:** Developing a translator for a simple stack-based virtual machine (VM).
- **Key Operations:** Arithmetic, memory access, and stack manipulation.
- **Outcome:** Translating VM code into Hack assembly language.

### Project 8: Virtual Machine - Part II
- **Focus:** Extending the VM translator to support program control flow.
- **Key Components:** If-statements, loops, function calls, call stack, and function returns.

### Project 9: Building a Game
- **Focus:** Using the Jack language to develop a complete software project.
- **Outcome:** Demonstrates a full hardware-software stack understanding, showcasing creativity and problem-solving skills.

### Project 11: Compiler
- **Focus:** Building a tokenizer and parser for the Jack programming language.
- **Key Components:** 
  - Conversion of Jack source code into an abstract syntax tree (AST).
  - Code generation from the AST into VM instructions.
  - Support for variable allocation, function calls, and control flow.

---

## How to Use

Each project in this repository contains:
- **HDL files** for hardware design.
- **Assembly files** for low-level programming.
- **VM and Jack files** for higher-level implementation.
- **Python/Java scripts** for software-based tools such as the Assembler, VM Translator, and Compiler.

To run a specific project, use the provided tools in the Nand2Tetris software suite. Detailed instructions for each project are available in their respective directories.

---

## Acknowledgments

This repository follows the **Nand2Tetris** curriculum created by Noam Nisan and Shimon Schocken. More details can be found on the [Nand2Tetris website](https://www.nand2tetris.org/).

---

## Credits and Contributions

- **Projects 1 - 6:** Implemented solely by me.
- **Projects 7 - 11:** Developed in collaboration with Noam Atzmon.
- **High-Level Language Projects:** Completed in Java as part of the course requirements, with personal Python implementations for additional learning and testing.

---

Feel free to explore the projects and reach out if you have any questions or suggestions. Happy building!
