'''The driving script for the program. 
This script will be used to run the program and call the necessary functions to run the program.
In simple words the engine of our VM translator.
'''
import sys
from Parser import Parser
from CodeWriter import CodeWriter

def main():
    if len(sys.argv) != 2:
        print("Usage: VMTranslator <inputfile.vm or directory>")
        sys.exit(1)
    
    input_path = sys.argv[1]
    
    # For simplicity, assume the input is a single .vm file.
    # (You can add directory handling to process multiple files.)
    parser = Parser(input_path)
    output_file = input_path.replace('.vm', '.asm')
    code_writer = CodeWriter(output_file)
    
    while parser.hasMoreCommands():
        ctype = parser.commandType()
        if ctype == Parser.C_ARITHMETIC:
            code_writer.writeArithmetic(parser.arg1())
        elif ctype in (Parser.C_PUSH, Parser.C_POP):
            code_writer.writePushPop(ctype, parser.arg1(), parser.arg2())
        elif ctype == Parser.C_LABEL:
            code_writer.writeLabel(parser.arg1())
        elif ctype == Parser.C_GOTO:
            code_writer.writeGoto(parser.arg1())
        elif ctype == Parser.C_IF:
            code_writer.writeIf(parser.arg1())
        elif ctype == Parser.C_FUNCTION:
            code_writer.writeFunction(parser.arg1(), parser.arg2())
        elif ctype == Parser.C_CALL:
            code_writer.writeCall(parser.arg1(), parser.arg2())
        elif ctype == Parser.C_RETURN:
            code_writer.writeReturn()
        parser.advance()

    code_writer.close()

if __name__ == "__main__":
    main()
