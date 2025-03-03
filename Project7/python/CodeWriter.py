'''Generates Assembly code from the parsed VM command'''


class CodeWriter:

    def __init__(self, file):
        '''Opens the output file/stream and gets ready to write into it.'''
        pass

    def writeArithmetic(self, command):
        '''Writes the assembly code that is the translation of the given arithmetic command.'''
        pass

    def writePushPop(self, command, segment, index):
        '''Writes the assembly code that is the translation of the given command, where command is either C_PUSH or C_POP.'''
        pass

    def close(self):
        '''Closes the output file.'''
        pass