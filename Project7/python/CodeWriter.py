'''Generates Assembly code from the parsed VM command'''


class CodeWriter:

    def __init__(self, output_file, file_name):
        '''Opens the output file/stream and gets ready to write into it.'''
        self.file = open(output_file, 'w') #open the file in write mode
        self.file_name = file_name #name of the file used for static variables 
        self.label_count = 0 #used to generate unique labels

    def writeArithmetic(self, command):
        '''Writes the assembly code that is the translation of the given arithmetic command.'''
        

    def writePushPop(self, command, segment, index):
        '''Writes the assembly code that is the translation of the given command, where command is either C_PUSH or C_POP.'''
        pass

    def close(self):
        '''Closes the output file.'''
        self.file.close() #close the file