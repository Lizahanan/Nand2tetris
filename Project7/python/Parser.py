import re
from enum import Enum


class COMMAND_TYPE(Enum):
    ''' Enum represeting the command types implemented in project 7 (there are more implemented in project 8)'''
    C_ARITHMETIC = 0
    C_PUSH = 1
    C_POP = 2


class Parser:

    def __init__(self,file_name):
        '''opens the file and prepares to parse it'''
        self.file = open(file_name, 'r')
        self.current_command = None
        self.next_line = None



    def has_more_commands(self):
        '''checks if there are more commands in the file'''
        if self.next_line is not None:
            return True
        while True:
            self.next_line = self.file.readline()
            if not self.next_line:
                return False
            self.next_line = self.next_line.strip()
            if self.next_line  and not self.next_line.startswith('//'): # if the line is not empty or a comment we contunie
                return True
                

    def advance(self):
        '''reads the next command from the input and makes it the current command'''
        if self.next_line:
            self.current_command = self.next_line
            self.next_line = None



    def command_type(self):
        '''returns the type of the current command'''
        if self.current_command is None:
            raise Exception('No command to parse')
        if self.current_command.startswith('push'):
            return COMMAND_TYPE.C_PUSH
        if self.current_command.startswith('pop'):
            return COMMAND_TYPE.C_POP
        return COMMAND_TYPE.C_ARITHMETIC
        



    def arg1(self):
        '''returns the first argument of the current command'''
        if self.current_command is None:
            raise Exception('No command to parse')
        parts = self.current_command.split() # a list of the parts of the command -> list is always ordered
        if self.command_type() == COMMAND_TYPE.C_ARITHMETIC:
            return parts[0] # the command itself 
        if len(parts) < 2:
            raise Exception('Not enough arguments')
        return parts[1] # the first argument
        
        



    def arg2(self):
        '''returns the second argument of the current command only in case of push and pop commands'''
        if self.current_command is None:
            raise Exception('No command to parse')
        parts = self.current_command.split()
        if self.command_type() in (COMMAND_TYPE.C_PUSH, COMMAND_TYPE.C_POP):
            if len(parts) < 3:
                raise Exception('Not enough arguments')
            return int(parts[2]) # convert second argument to integer and return it 
        raise Exception('No second argument for this command type')
    
    def close(self):    
        '''closes the file'''
        self.file.close()





