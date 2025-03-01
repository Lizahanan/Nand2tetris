/**
 * this file translates jack code to vm code
 * we later run that code on VMEmulator
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;

public class CodeWriter {

    //enums to store different segments 
    public static enum Segment{
        CONST, ARG, LOCAL, STATIC, THIS, THAT, POINTER, TEMP, NONE
    };
    //enum to store different arithmetic commands
    public static enum Command{
        ADD, SUB, NEG, EQ, GT, LT, AND, OR, NOT
    };

    //hashmap to store the segment strings
    private static HashMap<Segment, String> segmentStringHashMap = new HashMap<Segment, String>();
    //hashmap to store the command strings
    private static HashMap<Command, String> commandStringHashMap = new HashMap<Command, String>();
    private PrintWriter printWriter;

    static{
        segmentStringHashMap.put(Segment.CONST, "constant");
        segmentStringHashMap.put(Segment.ARG, "argument");
        segmentStringHashMap.put(Segment.LOCAL, "local");
        segmentStringHashMap.put(Segment.STATIC, "static");
        segmentStringHashMap.put(Segment.THIS, "this");
        segmentStringHashMap.put(Segment.THAT, "that");
        segmentStringHashMap.put(Segment.POINTER, "pointer");
        segmentStringHashMap.put(Segment.TEMP, "temp");

        commandStringHashMap.put(Command.ADD, "add");
        commandStringHashMap.put(Command.SUB, "sub");
        commandStringHashMap.put(Command.NEG, "neg");
        commandStringHashMap.put(Command.EQ, "eq");
        commandStringHashMap.put(Command.GT, "gt");
        commandStringHashMap.put(Command.LT, "lt");
        commandStringHashMap.put(Command.AND, "and");
        commandStringHashMap.put(Command.OR, "or");
        commandStringHashMap.put(Command.NOT, "not");
    }

    /**
     * creates new file and prepares to write to it
     * @param fileOut
     */
    
    public CodeWriter(File fileOut) {
        try {
            printWriter = new PrintWriter(fileOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes a VM push command
     * @param segment
     * @param index
     */

    public void writePush(Segment segment, int index) {
        writeCommand("push", segmentStringHashMap.get(segment), String.valueOf(index));
        
    }

    /**
     * Writes a VM pop command
     * @param segment
     * @param index
     */
    public void writePop(Segment segment, int index) {
        writeCommand("pop", segmentStringHashMap.get(segment), String.valueOf(index));
    }

    /**
     * writes a VM arithmetic command
     * @param command
     */
    public void writeArithmetic(Command command) {
        writeCommand(commandStringHashMap.get(command), "", "");
    }

    
    /**
     * writes a VM label command
     * @param label
     */
    public void writeLabel(String label){
        writeCommand("label",label,"");
    }

    /**
     * writes a VM goto command
     * @param label
     */
    public void writeGoto(String label){
        writeCommand("goto",label,"");
    }
    

    /**
     * writes a VM if-goto command
     * @param label
     */
    public void writeIf(String label){
        writeCommand("if-goto",label,"");
    }

     /**
     * writes a VM call command
     * @param name
     * @param nArgs
     */
    public void writeCall(String name, int nArgs){
        writeCommand("call",name,String.valueOf(nArgs));
    }

    /**
     * writes a VM function command
     * @param name
     * @param nLocals
     */
    public void writeFunction(String name, int nLocals){
        writeCommand("function",name,String.valueOf(nLocals));
    }

    /**
     * writes a VM  command
     * @param cmd
     * @param arg1
     * @param arg2
     */
    public void writeCommand(String cmd, String arg1, String arg2){

        printWriter.print(cmd + " " + arg1 + " " + arg2 + "\n");

    }
    /**
     * writes a VM return command
     */
    public void writeReturn(){
        writeCommand("return","","");
    }

     /**
     * close the output file
     */

     public void close(){
        printWriter.close();
    }

}
