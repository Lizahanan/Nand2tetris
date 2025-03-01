import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Parser{
    private BufferedReader reader; //reader to read the input file
    public String currentCommand; //current VM command
    private String nextLine; //tracks the next line in the input file

    //Constructor: Opens the file and prepares to parse it
    public Parser(String fileName) throws FileNotFoundException{
        reader = new BufferedReader(new FileReader(fileName));
        currentCommand = null;
        nextLine = null;
    }

    //Checks if there are more commands in the file

    public boolean hasMoreLines() throws IOException{
        if(nextLine != null){
            return true; //there's a pre-fetched line
        }
        nextLine = reader.readLine(); //try to read the next line
        while (nextLine != null && (nextLine.trim().isEmpty() || nextLine.startsWith("//"))) {
            nextLine = reader.readLine(); // Skip empty or comment lines  
        } return nextLine != null;

    }

    //advance: Reads the next command and makes it the current instruction

    public void advance(){
        if (nextLine != null){
            currentCommand = nextLine.trim(); //set next line as the current command 
            nextLine = null; //clear pre-fetched lines
        }
    }

    //Returns the type of the current VM command
    public String commandType(){
        if(currentCommand == null){
            throw new IllegalStateException("No command to process");
        }
        if (currentCommand.startsWith("push")){
            return "C_PUSH";
        }
        if (currentCommand.startsWith("pop")){
            return "C_POP";
        } else {
            return "C_ARITHMETIC";
        }
    }

    // arg1: Returns the first argument of the current command
    public String arg1(){
        if(currentCommand == null){
            throw new IllegalStateException("No command to process");
        }
        String[] parts = currentCommand.split("\\s+"); //splits the command into parts using any whitespace as delimiter
        if (commandType().equals("C_ARITHMETIC")) {
            return parts[0]; // The command itself is the argument for arithmetic commands
        }
        if (parts.length < 2) {
            throw new IllegalArgumentException("Command does not have enough arguments"); //push and pop require at least 2 arguments
        }
        return parts[1]; // for push and pop commands this returns the first part for example "constant" in "push constant 7"
    }

    // arg2: Returns the second argument of the current command
    public int arg2(){
        if (currentCommand == null) {
            throw new IllegalStateException("No current command to process");
        }
        String[] parts = currentCommand.split("\\s+"); //split the command into parts using any whitespace as delimiter
        if (commandType().equals("C_PUSH") || commandType().equals("C_POP")){
            if (parts.length < 3){
                throw new IllegalArgumentException("Command does not have a second argument");  
            }
            return Integer.parseInt(parts[2]); //return the second part of the command for example 7 in "push constant 7"
        }
        throw new UnsupportedOperationException("arg2 is not applicable for this command type"); // for example if it's an arithmetic operation we will not have second argument
    }

    // Close the reader when done
    public void close() throws IOException {
        reader.close();
    }

}