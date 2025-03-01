import sys 
from Parser import Parser, COMMAND_TYPE
from CodeWriter import CodeWriter

def main():
    if len(sys.argv) != 2:
        raise Exception('Usage: python Main.py file_name.vm')
    file_name = sys.argv[1]
    if not file_name.endswith(".vm"):
        print("Error: Input file must be a .vm file")
        sys.exit(1)


    parser = Parser(file_name)
    code_writer = CodeWriter(file_name.replace('.vm', '.asm'))

    while parser.has_more_commands():
        parser.advance()
        command_type = parser.command_type()
        if command_type == COMMAND_TYPE.C_ARITHMETIC:
            code_writer.write_arithmetic(parser.arg1())
        elif command_type in {COMMAND_TYPE.C_PUSH, COMMAND_TYPE.C_POP}:
            code_writer.write_push_pop(command_type, parser.arg1(), parser.arg2())
        else:
            raise Exception('Invalid command type')
        
    code_writer.close()
    parser.close()

    print(f"Translation of {file_name} is done")

if __name__ == "__main__":
    main()
