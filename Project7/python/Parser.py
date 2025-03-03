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
        pass

    def advance(self):
        '''
        Reads the next command from the input and makes it the current command.
        '''
        pass

    def commandType(self):
        '''
        Returns the type of the current command.
        '''
        pass

    def arg1(self):
        '''returns first argument of the current command'''
        pass

    def arg2(self):
        '''
        Returns the second argument of the current command.
        '''
        pass

    def close(self):
        ''' closes the file'''
        pass