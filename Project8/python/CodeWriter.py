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
        self.current_function="" #extension for projeect 8
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
                self.write_line('M=D+M') #add the top two elements and store the result in the second element
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
            self.write_line('@' + "index")
            self.write_line('D=A')
        elif segment in ['local', 'argument', 'this', 'that']:
            self.write_line('@' + self.segment_base[segment])
            self.write_line('D=M')
            self.write_line('@' + "index")
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
            self.write_line('@' + "index")
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

    #all of the following are extension for project 8
    '''
    WriteLabel is called with an argument "label"
    the command marks the destination of the goto command 
    in hack code (LABEL)

    '''
    def writeLabel(self,label):
        full_label = self.get_Label(label)
        self.file.write(f"({full_label})\n") 

    
    def get_Label(self, label):
        if self.current_function:
            return f"{self.current_function}${label}"
        else:
            return label 

    '''
    Writes Assembly code for goto command
    the command includes uncoditional "jump
    '''
    def writeGoto(self,label):
        full_label = self.get_Label(label)
        self.file.write(f"@{label}\n0;JMP\n")

    '''
    Assembly code for if-goto command
    '''
    def writeIf(self,label,):
        full_label = self.get_Label(label)
        #we have to pop the condition 
        #pop the top stack value 
        self.pop_to_D() #pop the value
        self.file.write(f"@{full_label}\nD;JNE\n")


    def writeFunction(self,funName,nVars):
        self.current_function = funName
        #we write the label for the function 
        self.file.write(f"({funName})\n")
        #now we need set all locals to 0 -> we push 0 nvars times
        for _ in range(nVars):
            self.write_line("@0")
            self.write_line("D=A")#store 0 in data 
            self.write_line('@SP') #get the stack pointer
            self.write_line("A=M") #save address of the pointer
            self.write_line("M=D") #set to 0 at that address
            self.increment_SP #intxrement the pointer 
        

    '''
    When handling call we need to take care of the following:
    -Save return address
    -save callers segment pointer 
    -repositon arg 
    -reposition lcl 
    -goto execute the callee's code'''
    def writeCall(self,funName,nArgs):
        #generate return label 
        return_label = f"{funName}$ret{self.label_count}"
        self.label_count+=1

        #push the return address 
        self.file.write(f"@{return_label}\nD=A\n@SP\nA=M\nM=D") 
        self.increment_SP

        #Now we need to push LCL, ARG , THIS, THAT 
        for segment in ["LCL", "ARG" , "THIS", "THAT"]:
            self.file.write(f"@{segment}\nD=M\n@SP\nA=M\nM=D")
            self.increment_SP
        
        #now we need to repositon LCL and ARG
        self.file.write(f"@SP\nD=M\n@{nArgs + 5}\nD=D-A\n@ARG\nM=D\n")
        self.file.write("@SP\nD=M\n@LCL\nM=D")

        #now we transfer control to the callee
        self.file.write(f"@{funName}\n0;JMP\n")

        #finally declare return label 
        self.file.write(f"{return_label}")


    def writeReturn(self):

        #endframe = LCL 
        self.file.write("@LCL\nD=M\n@R13\nM=D\n")
        #store return address in endframe-5
        self.file.write("@5\nA=D-A\nD=M\n@R14\nM=D")
        #*ARG=pop()
        self.file.write("@SP\nAM=M-1\nD=M\n@ARG\nA=M\nM=D\n")
        #SP = ARG + 1
        self.file.write("@ARG\nD=M+1\n@SP\nM=D\n")
        '''
        THAT = *(endFrame – 1) 
        THIS = *(endFrame – 2) 
        ARG = *(endFrame – 3) 
        LCL = *(endFrame – 4) 
        '''
        for offset,segment in zip(range(1,5),["THAT","THIS","ARG","LCL"]):
            self.file.write(f"@R13\nAM=M-1\nD=M\n@{segment}\nM=D\n")
        #goto return
            self.file.write("@R14\nA=M\n0;JMP\n")




        

    