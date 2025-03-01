from enum import Enum

class COMMAND_TYPE(Enum):
    ''' Enum represeting the command types implemented in project 7 (there are more implemented in project 8)'''
    C_ARITHMETIC = "C_ARITHMETIC"
    C_PUSH = "C_PUSH"
    C_POP = "C_POP"

class CodeWriter:
    def __init__(self, file_name):
        '''opens the file and gets ready to write into it'''
        self.file_name = file_name.replace('.vm', '')
        self.file = open(file_name.replace('.vm', '.asm'), 'w')
        self.label_counter = 0 #counter for uniqie labels 

    def write_arithmetic(self, command):
        '''writes the assembly code that is the translation of the given arithmetic command'''
        if command in {'add' , 'sub', 'and', 'or'}:
            self.pop_stack_to_d()
            self.decrement_sp()
            self.file.write("A=M\n")
            operations = {
                "add": "M=M+D\n",
                "sub": "M=M-D\n",
                "and": "M=D&M\n",
                "or": "M=D|M\n"
            }
            self.file.write(operations[command]+ "\n")
        elif command in {'neg', 'not'}:
            self.decrement_sp()
            self.file.write("A=M\n")
            operations = {
                "neg": "M=-M\n",
                "not": "M=!M\n"
            }
            self.file.write(operations[command]+ "\n")
        elif command in {'eq', 'gt', 'lt'}:
            self.pop_stack_to_d()
            self.decrement_sp()
            self.file.write("A=M\n")
            self.file.write("D=M-D\n")
            self.file.write(f"@BOOL{self.label_counter}\n")
            jump = {
                "eq": "JEQ",
                "gt": "JGT",
                "lt": "JLT"
            } [command]
            self.file.write(f"D;{jump}\n")
            self.set_a_to_stack()
            self.file.write("M=0\n")
            self.file.write(f"@END{self.label_counter}\n")
            self.file.write(f"0;JMP\n")
            self.file.write(f"(BOOL{self.label_counter})\n")
            self.set_a_to_stack()
            self.file.write("M=-1\n")
            self.file.write(f"(ENDBOOL{self.label_counter})\n")
            self.label_counter += 1
        self.increment_sp()
        
    def write_push_pop(self, command, segment, index):
        '''writes assembly given the command, segment and index'''
        if command == COMMAND_TYPE.C_PUSH:
            if segment == "constant":
                self.file.write(f"@{index}\n")
                self.file.write("D=A\n")
            else:
                self.translate_segment(segment, index)
                self.file.write("D=M\n")
            self.push_d_to_stack()
        elif command == COMMAND_TYPE.C_POP:
            self.translate_segment(segment, index)
            self.file.write("D=A\n")
            self.file.write("@R13\n")   #store the address in R13
            self.file.write("M=D\n")
            self.pop_stack_to_d()
            self.file.write("@R13\n")
            self.file.write("A=M\n")
            self.file.write("M=D\n") #store the value that has been popped 


    def close (self):
        '''closes the file'''
        self.file.close()

    def translate_segment(self, segment, index):
        '''handles the translation of the memorry segment'''
        segment_dict = {
            "local": "LCL",
            "argument": "ARG",
            "this": "THIS",
            "that": "THAT",
            
        }
        if segment in segment_dict:
            self.file.write(f"@{segment_dict[segment]}\n")
            self.file.write(f"D=M\n")
            self.file.write(f"@{index}\n")
            self.file.write("A=D+A\n")
        elif segment == "pointer":
            self.file.write(f"@{'THIS' if index == 0 else 'THAT'}\n")
        elif segment == "temp":
            self.file.write(f"@{5 + index}\n")
        elif segment == "static":
            self.file.write(f"@{self.file_name}.{index}\n")
        elif segment == "constant":
            self.file.write(f"@{index}\n")
            self.file.write("D=A\n")
        else:
            raise Exception(f"Invalid segment {segment}")
        

    def push_d_to_stack(self):
        '''pushes the value of D register into the stack'''
        self.file.write("@SP\nA=M\nM=D\n")
        self.increment_sp()

    def pop_stack_to_d(self):
        '''pops the value from the stack and stores it in the D register'''
        self.decrement_sp()
        self.set_a_to_stack()
        self.file.write("D=M\n")

    def decrement_sp(self):
        '''decrements the stack pointer'''
        self.file.write("@SP\nM=M-1\n")
    
    def increment_sp(self):
        '''increments the stack pointer'''
        self.file.write("@SP\nM=M+1\n")

    def set_a_to_stack(self):
        '''sets the A register to the stack pointer'''
        self.file.write("@SP\nA=M\n")

        






