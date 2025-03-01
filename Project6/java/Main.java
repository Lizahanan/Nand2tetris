import java.io.*;

public class Main {

    public static void main(String[] args){
        if (args.length != 1) {
            System.err.println("Usage: java HackAssembler <source file>");
            return;
        }
        String inputFile = args[0];
        String outputFile = inputFile.replace(".asm", ".hack");
        try {
            // Step 1: Initialize components
            SymbolTable symbolTable = new SymbolTable();
            HackParser parser = new HackParser(inputFile);
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

            // Step 2: First pass to handle labels
            firstPass(parser, symbolTable);

            // Reset parser for the second pass
            parser = new HackParser(inputFile);

            // Step 3: Second pass to generate binary code
            secondPass(parser, symbolTable, writer);

            // Close resources
            parser.close();
            writer.close();

            System.out.println("Assembling completed. Output written to " + outputFile);

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }


}       //during first pass we add lables to the table
    // First pass to handle labels
    private static void firstPass(HackParser parser, SymbolTable symbolTable) throws IOException {
        int lineNumber = 0;  // We keep track of the line number to assign addresses to labels
        while (parser.hasMoreLines()) {
            parser.advance();
            HackParser.InstructionType type = parser.instructionType();

            if (type == HackParser.InstructionType.L_INSTRUCTION) { 
                String label = parser.getCurrInstruction().substring(1, parser.getCurrInstruction().length() - 1);  // Extract label without parentheses
                symbolTable.addEntry(label, lineNumber);  // Add the label to the symbol table with its address (line number)
            } else {
                lineNumber++;  // Increment the line number for non-label instructions
            }
    }
}

        //during second pass we add variables to the table 
    // Second pass to generate binary code
private static void secondPass(HackParser parser, SymbolTable symbolTable, BufferedWriter writer) throws IOException {
    int nextFreeAddress = 16; // Start with address 16 for variables
    while (parser.hasMoreLines()) {
        parser.advance();
        HackParser.InstructionType type = parser.instructionType();

        if (type == HackParser.InstructionType.A_INSTRUCTION) {
            String symbol = parser.getCurrInstruction().substring(1);  // Remove the "@" symbol
            int address;

            if (isNumeric(symbol)) {
                address = Integer.parseInt(symbol); // If it's a number, parse it
            } else {
                if (!symbolTable.contains(symbol)) {
                    // If the symbol is not in the symbol table, add it with the next available address
                    symbolTable.addEntry(symbol, nextFreeAddress);
                    nextFreeAddress++;
                }
                
                address = symbolTable.getAddress(symbol);  // Get address from the symbol table
            }

            // Convert address to 16-bit binary with leading zeros
            writer.write(String.format("%16s", Integer.toBinaryString(address)).replace(' ', '0'));
            writer.newLine();

        } else if (type == HackParser.InstructionType.C_INSTRUCTION) {
            // Extract dest, comp, and jump parts (similar to your current logic)
            String[] parts = parser.getCurrInstruction().split("[=;]");
            String dest = parts.length == 3 ? parts[0] : (parser.getCurrInstruction().contains("=") ? parts[0] : null);
            String comp = parts.length == 3 ? parts[1] : (parser.getCurrInstruction().contains("=") ? parts[1] : parts[0]);
            String jump = parser.getCurrInstruction().contains(";") ? parts[parts.length - 1] : null;

            // Construct the binary C-instruction
            String binaryCode = "111" + HackCode.comp(comp) + HackCode.dest(dest) + HackCode.jump(jump);
            writer.write(binaryCode);
            writer.newLine();
        }
    }
}

             // Helper method to check if a string represents a numeric value
    private static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false; // Null or empty input is not numeric
        }
        try {
            Integer.parseInt(str); // Attempt to parse the string as an integer
            return true; // If successful, it is numeric
        } catch (NumberFormatException e) {
            return false; // Parsing failed, it's not numeric
        }
    }


        
}

