'''The driving script for the program. 
This script will be used to run the program and call the necessary functions to run the program.
In simple words the engine of our VM translator.
'''
import sys
import os
from Parser import Parser
from CodeWriter import CodeWriter

def main():
    if len(sys.argv) != 2:
        print("Usage: VMTranslator <inputfile.vm or directory>")
        sys.exit(1)

    input_path = sys.argv[1]
    # Derive the file_name (without extension) for static variable handling
    file_name = os.path.splitext(os.path.basename(input_path))[0]
    output_file = input_path.replace('.vm', '.asm')

    parser = Parser(input_path)
    code_writer = CodeWriter(output_file, file_name)

    while parser.hasMoreCommands():
        command_type = parser.commandType()
        if command_type == Parser.C_ARITHMETIC:
            code_writer.writeArithmetic(parser.arg1())
        # Instead of a combined writePushPop, we assume separate push and pop methods
        elif command_type == Parser.C_PUSH:
            code_writer.writePush(parser.arg1(), parser.arg2())
        elif command_type == Parser.C_POP:
            code_writer.writePop(parser.arg1(), parser.arg2())
        elif command_type == Parser.C_LABEL:
            code_writer.writeLabel(parser.arg1())
        elif command_type == Parser.C_GOTO:
            code_writer.writeGoto(parser.arg1())
        elif command_type == Parser.C_IF:
            code_writer.writeIf(parser.arg1())
        elif command_type == Parser.C_FUNCTION:
            code_writer.writeFunction(parser.arg1(), parser.arg2())
        elif command_type == Parser.C_CALL:
            code_writer.writeCall(parser.arg1(), parser.arg2())
        elif command_type == Parser.C_RETURN:
            code_writer.writeReturn()
        parser.advance()

    code_writer.close()

if __name__ == "__main__":
    main()
