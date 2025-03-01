import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HackParser{
    private BufferedReader reader;
    private String currentLine;
    private String currentInstruction;

    //instruction types 
    public enum InstructionType {
        A_INSTRUCTION, //@value
        C_INSTRUCTION, // dest = comp;jump
        L_INSTRUCTION  //(label)
    }


    //initializer: creates a parser and opens the source file
    public HackParser(String filePath) throws IOException{
        reader = new BufferedReader(new FileReader(filePath));
        currentLine = null;
        currentInstruction = null;
    }

    //checks if there's more work to do
    public boolean hasMoreLines() throws IOException {
        while ((currentLine = reader.readLine()) != null){
            //trim whitespace and ignore empty lines 
            currentLine = currentLine.trim();
            //we read only the part with no comment
            if (!currentLine.isEmpty() && !currentLine.startsWith("//")){
                //remove inline comment
                int commentInd = currentLine.indexOf("//");
                if (commentInd != -1){
                    currentLine = currentLine.substring(0, commentInd).trim();
                }
                return true; //theres a valid line to process
            }
        }
        return false; //no valid line to process was found in the while loop
    }

    public void advance(){
        currentInstruction = currentLine; //the current line is now storred as current instruction
        //we move from previous instruction to new valid line
    }

    public InstructionType instructionType(){
        if (currentInstruction.startsWith("@")){
            return InstructionType.A_INSTRUCTION;

        } else if (currentInstruction.startsWith("(")){
            return InstructionType.L_INSTRUCTION;
        } else {
            return InstructionType.C_INSTRUCTION;
        }

    }
    //returns current instruction
    public String getCurrInstruction(){
        return currentInstruction;
    }

    //closes the file 

    public void close() throws IOException{
        if (reader != null){
            reader.close();
        }
    }



}