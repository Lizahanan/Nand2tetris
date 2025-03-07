'''The driving script for the program. 
This script will be used to run the program and call the necessary functions to run the program.
In simple words the engine of our VM translator.
'''
import sys
from Parser import Parser
from CodeWriter import CodeWriter
import os

def main():
    #check if the user has provided the correct number of arguments
    if len(sys.argv) != 2:
        print("Usage: python Main.py <file_name>")
        sys.exit(1)

    #get the file name from the command line argument
    input_file = sys.argv[1]
    output_file = input_file.replace('.vm', '.asm') #create the output file name
    file_name = input_file.split('/')[-1].replace('.vm', '') #get the file name without the path and extension

    parser = Parser(input_file) #create a parser object
    code_writer = CodeWriter(output_file, file_name) #create a code writer object

    while parser.hasMoreCommands():
        parser.advance()
        command_type = parser.commandType()
        if command_type == 'C_ARITHMETIC':
            command = parser.arg1()
            code_writer.writeArithmetic(command)
        elif command_type in ['C_PUSH', 'C_POP']:
            segment = parser.arg1()
            index = parser.arg2()
            code_writer.writePushPop(command_type, segment, index)
        #the other commands are implemented in the next project
        code_writer.close() #close the output file

    

if __name__ == "__main__":
    main()