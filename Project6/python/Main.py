import sys

import Parser, Code, SymbolTable

'''Assembler for the Hack computer
    Assemble the given .asm file into a .hack file
    Usage: python Main.py file.asm
'''

def main():
    #check if the correct number of arguments are passed
    if len(sys.argv) != 2:
        print("Usage: python Main.py file.asm")
        sys.exit(1)
    #open the input file
    file_path = sys.argv[1]
    output_file = file_path.replace(".asm", ".hack")

    #initialize the symbol table
    symbol_table = SymbolTable.SymbolTable()

    #first pass : build the symbol table 
    parser = Parser.Parser(file_path)
    address = 0
    while parser.hasMoreCommands():
        parser.advance()
        if parser.commandType() == "L_COMMAND":
            if not symbol_table.contains(parser.symbol()):
                symbol_table.addEntry(parser.symbol(), address)
        else:
            address += 1

    #second pass : generate the binary code
    parser = Parser(file_path)
    binary_code = [] #list to store the binary code
    while parser.hasMoreCommands():
        parser.advance()
        command_type = parser.commandType()
        if command_type == "A_COMMAND":
            symbol = parser.symbol()
            #determine if its a number or variable
            if symbol.isdigit():
                address = int(symbol)
            else:
                if symbol_table.contains(symbol):
                    address = symbol_table.getAddress(symbol)
                else:
                    address = symbol_table.next_available_address
                    symbol_table.addEntry(symbol, address)
                    symbol_table.next_available_address += 1
            binary_code.append(f"{address:016b}")
        elif command_type == "C_COMMAND":
            d = parser.dest()
            c = parser.comp()
            j = parser.jump()
            binary_code.append(f"111{Code.comp(c)}{Code.dest(d)}{Code.jump(j)}")
         #write the binary code to the output file
    with open(output_file, 'w') as f:
        f.write("\n".join(binary_code)) #the binary code commands are joined by newline
        print(f"Assembly complete. Binary code written to {output_file}")

   
    

if __name__ == "__main__":
    main()