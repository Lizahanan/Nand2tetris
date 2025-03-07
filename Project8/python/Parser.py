'''
Parser Module for the VM translator.
This module reads a .vm file, removes comments and whitespace, 
and returns an iterable of commands.
'''

class Parser:
    #constants for command types
    C_ARITHMETIC = "C_ARITHMETIC"
    C_PUSH = "C_PUSH"
    C_POP = "C_POP"
    #extension for project 8
    C_LABEL = "C_LABEL"
    C_GOTO = "C_GOTO"
    C_IF = "C_IF"
    C_FUNCTION = "C_FUNCTION"
    C_RETURN = "C_RETURN"
    C_CALL = "C_CALL"


    def __init__(self,file):
        '''
        Opens the input file/stream and gets ready to parse it.
        '''
        self.commands = [] #list of commands
        #open the file 
        with open(file, 'r') as f:
            for line in f:
                #remove whitespace and inline comments
                line = line.split("//")[0].strip()
                if line:
                    self.commands.append(line)
        self.current_command = None
        self.current_index = 0


    def hasMoreCommands(self):
        '''
        Returns true if there are more commands in the input.
        '''
        return self.current_index < len(self.commands)

    def advance(self):
        '''
        Reads the next command from the input and makes it the current command.
        '''
        #check if there are more commands
        if self.hasMoreCommands():
            self.current_command = self.commands[self.current_index]
            self.current_index += 1
        

    def commandType(self):
        '''
        Returns the type of the current command.
        '''
        if self.current_command in ["add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not"]:
            return self.C_ARITHMETIC
        elif self.current_command is None:
            return None
        elif self.current_command.startswith("push"):
            return self.C_PUSH
        elif self.current_command.startswith("pop"):
            return self.C_POP
        #extension for project 8
        elif self.current_command.startswith("label"):
            return self.C_LABEL
        elif self.current_command.startswith("goto"):
            return self.C_GOTO
        elif self.current_command.startswith("if-goto"):
            return self.C_IF
        elif self.current_command.startswith("function"):
            return self.C_FUNCTION
        elif self.current_command.startswith("return"):
            return self.C_RETURN
        elif self.current_command.startswith("call"):
            return self.C_CALL
        else:
            raise ValueError("Invalid command: {}".format(self.current_command))

    def arg1(self):
        '''returns first argument of the current command'''
        #if the command is arithmetic, return the command itself
        if self.commandType() == self.C_ARITHMETIC:
            return self.current_command
        #the function shouldnt be called if the command type is return -> extension for project 8
        if self.commandType() == self.C_RETURN:
            raise Exception("The function shouldnt be called for return command")
        #otherwise, return the first argument
        return self.current_command.split()[1] 

    def arg2(self):
        '''
        Returns the second argument of the current command.
        '''
        #only push and pop commands have a second argument + extension for project 8
        if self.commandType() in [self.C_PUSH, self.C_POP, self.C_FUNCTION, self.C_CALL]:
            return int(self.current_command.split()[2])
        else:
            raise ValueError(f"Current command {self.current_command} of type {self.commandType()} does not have a second argument")

    