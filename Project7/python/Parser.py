'''
Parser Module for the VM translator.
This module reads a .vm file, removes comments and whitespace, 
and returns an iterable of commands.
'''

class Parser:
    #constants for command types

    def __init__(self,file):
        '''
        Opens the input file/stream and gets ready to parse it.
        '''
        pass

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