import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException{
        if (args.length != 1) {
            System.err.println("Usage: java VMTranslator <file.vm>");
            System.exit(1);
        }

        String inputFile = args[0];
    
        Parser parser = new Parser(inputFile);
        CodeWriter writer = new CodeWriter(inputFile);

        //process the file 
        while(parser.hasMoreLines()){
            parser.advance();
            if (parser.commandType().equals("C_ARITHMETIC")){
                writer.writeArithmetic(parser.arg1());
            } else if (parser.commandType().equals("C_PUSH") || parser.commandType().equals("C_POP")){
                writer.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
            }
        }
        parser.close();
        writer.close();
        
        System.out.println("Translation complete.");
    }
    
}
