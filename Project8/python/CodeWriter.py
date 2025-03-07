'''Generates Assembly code from the parsed VM command'''


class CodeWriter:

    #dictionary to store the segment base addresses
    segment_base = {
        'local' : 'LCL',
        'argument' : 'ARG',
        'this' : 'THIS',
        'that' : 'THAT',
        'temp': 5,
        'pointer' : 3,
        'static' : 16,
        'constant' : 'CONSTANT'
    }



    def __init__(self, output_file, file_name):
        '''Opens the output file/stream and gets ready to write into it.'''
        self.file = open(output_file, 'w') #open the file in write mode
        self.file_name = file_name #name of the file used for static variables 
        self.label_count = 0 #used to generate unique labels


    def write_line(self, line):
        '''Writes a line to the output file'''
        self.file.write(line + '\n')

    def pop_to_D(self):
        '''Pops the top element of the stack and stores it in D'''
        self.write_line('@SP') #load the stack pointer
        self.write_line('M=M-1') #decrement the stack pointer
        self.write_line('A=M') #load the address of the top element
        self.write_line('D=M') #store the top element in D

    def decrement_SP(self):
        '''Decrements the stack pointer'''
        self.write_line('@SP') #load the stack pointer
        self.write_line('M=M-1') #decrement the stack pointer

    def set_A_to_SP(self):
        '''Sets the A register to the value stored in the stack pointer'''
        self.write_line('@SP')
        self.write_line('A=M')
    
    def increment_SP(self):
        '''Increments the stack pointer'''
        self.write_line('@SP')
        self.write_line('M=M+1')


    def writeArithmetic(self, command):
        '''Writes the assembly code that is the translation of the given arithmetic command.'''
        #for arithmetic and boolean commands which perform operations on the top two elements of the stack
        if command in ['add' , 'sub' , 'and' , 'or']:
            self.pop_to_D() #pop the top element and store it in D
            self.decrement_SP() #decrement the stack pointer
            self.write_line('A=M') #load the address of the second element
            if command == 'add':
                self.write_line('M=M+D') #add the top two elements and store the result in the second element
            elif command == 'sub':
                self.write_line('M=M-D') #subtract the top element from the second element and store the result in the second element
            elif command == 'and':
                self.write_line('M=M&D') #perform bitwise AND operation on the top two elements and store the result in the second element
            elif command == 'or':
                self.write_line('M=M|D') #perform bitwise OR operation on the top two elements and store the result in the second element
        #for unary arithmetic commands which perform operations on the top element of the stack
        elif command in ['neg' , 'not']:
            self.decrement_SP() #decrement the stack pointer
            self.set_A_to_SP() #set the A register to the value stored in the stack pointer
            if command == 'neg':
                self.write_line('M=-M') #negate the top element
            elif command == 'not':
                self.write_line('M=!M') #perform bitwise NOT operation on the top element
        #for comparison commands which perform operations on the top two elements of the stack
        elif command in ['eq' , 'gt' , 'lt']:
            self.pop_to_D()
            self.decrement_SP()
            self.write_line('A=M')
            self.write_line('D=M-D')
            self.write_line('@TRUE' + str(self.label_count))
            if command == 'eq':
                self.write_line('D;JEQ')
            elif command == 'gt':
                self.write_line('D;JGT')
            elif command == 'lt':
                self.write_line('D;JLT')
            #false is 0 and true is -1
            self.write_line('M=0') #store false in the second element
            self.write_line('@END' + str(self.label_count))
            self.write_line('0;JMP')
            self.write_line('(TRUE' + str(self.label_count) + ')')
            self.write_line('M=-1')
            self.write_line('(END' + str(self.label_count) + ')')
            self.label_count += 1
        else:
            raise ValueError('Invalid command')
        self.increment_SP()

            

        

    def writePush(self, segment, index):
        '''Writes the assembly code that is the translation of the given command, where command is either C_PUSH or C_POP.'''
        if segment == 'constant':
            self.write_line('@' + index)
            self.write_line('D=A')
        elif segment in ['local', 'argument', 'this', 'that']:
            self.write_line('@' + self.segment_base[segment])
            self.write_line('D=M')
            self.write_line('@' + index)
            self.write_line('A=D+A')
            self.write_line('D=M')
        elif segment == 'temp':
            self.write_line('@' + str(self.segment_base[segment] + index))
            self.write_line('D=M')
        elif segment == 'pointer':
            self.write_line('@' + str(self.segment_base[segment] + index))
            self.write_line('D=M')
        elif segment == 'static':
            self.write_line('@' + self.file_name + '.' + index)
            self.write_line('D=M')
        self.write_line('@SP')
        self.write_line('A=M')
        self.write_line('M=D')
        self.increment_SP()

    def writePop(self, segment, index):
        if segment == 'constant':
            raise ValueError('Cannot pop to constant segment')
        elif segment in ['local', 'argument', 'this', 'that']:
            self.write_line('@' + self.segment_base[segment])
            self.write_line('D=M')
            self.write_line('@' + index)
            self.write_line('D=D+A')
            self.write_line('@R13') #store the address in R13 which holds the target address
            self.write_line('M=D')
            self.pop_to_D()
            self.write_line('@R13')
            self.write_line('A=M')
            self.write_line('M=D')
        elif segment == 'temp':
            self.pop_to_D()
            self.write_line('@' + str(self.segment_base[segment] + index))
            self.write_line('M=D')
        elif segment == 'pointer':
            self.pop_to_D()
            self.write_line('@' + str(self.segment_base[segment] + index))
            self.write_line('M=D')
        elif segment == 'static':
            self.pop_to_D()
            self.write_line('@' + self.file_name + '.' + index)
            self.write_line('M=D')
       
    def close(self):
        '''Closes the output file'''
        self.file.close()


        

    