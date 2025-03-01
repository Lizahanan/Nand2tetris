
import java.util.HashMap;
import java.io.*;

public class CodeWriter {
    private FileWriter writer;
    private int boolCount;
    private String fileName;

    private static final HashMap<String, String> SEGMENT_BASE = new HashMap<>() {{
        put("argument", "ARG");
        put("local", "LCL");
        put("this", "THIS");
        put("that", "THAT");
    }};

    public CodeWriter(String fileName) throws IOException {
        initializeFile(fileName);
        boolCount = 0;
    }
    private void initializeFile(String fileName) throws IOException {
        String path = fileName.replace(".vm", ".asm");
        File outputFile = new File(path);
        if (outputFile.createNewFile()) {
            System.out.println("File created: " + outputFile.getName());
        }
        writer = new FileWriter(path);
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    // writeArithmetic: Translates an arithmetic and logic command into Hack assembly
    public void writeArithmetic(String command) throws IOException{
        if (requiresBinaryOperation(command)) {
            popStackToD();
        }
        decrementSP();
        setAToStack();
        handleArithmeticCommand(command);
        incrementSP();
    }
    private boolean requiresBinaryOperation(String command) {
        return !command.equals("neg") && !command.equals("not");
    }
    private void handleArithmeticCommand(String command) throws IOException {
        switch (command) {
            case "add":
                writeArithmeticOperation("M=D+M");
                break;
            case "sub":
                writeArithmeticOperation("M=M-D");
                break;
            case "and":
                writeArithmeticOperation("M=D&M");
                break;
            case "or":
                writeArithmeticOperation("M=D|M");
                break;
            case "neg":
                writeArithmeticOperation("M=-M");
                break;
            case "not":
                writeArithmeticOperation("M=!M");
                break;
            case "eq":
            case "gt":
            case "lt":
                handleBooleanCommand(command);
                break;
            default:
                throw new IllegalArgumentException("Invalid arithmetic command: " + command);
        }
    }
    private void writeArithmeticOperation(String operation) throws IOException {
        writer.write(operation + "\n");
    }

    private void handleBooleanCommand(String command) throws IOException {
        writer.write("D=M-D\n");
        writer.write("@BOOL" + boolCount + "\n");

        String jumpCommand = switch (command) {
            case "eq" -> "JEQ";
            case "gt" -> "JGT";
            case "lt" -> "JLT";
            default -> throw new IllegalArgumentException("Invalid boolean command: " + command);
        };
        writer.write("D;" + jumpCommand + "\n");
        setAToStack();
        writer.write("M=0\n");
        writer.write("@ENDBOOL" + boolCount + "\n");
        writer.write("0;JMP\n");
        writer.write("(BOOL" + boolCount + ")\n");
        setAToStack();
        writer.write("M=-1\n");
        writer.write("(ENDBOOL" + boolCount + ")\n");
        boolCount++;
    }


    // writePushPop: Translates push or pop commands into Hack assembly
    public void writePushPop(String commandType , String segment , int index) throws IOException{
        translateSegment(segment, index);
        if (commandType.equals("C_PUSH")) {
            handlePushCommand(segment);
        } else if (commandType.equals("C_POP")) {
            handlePopCommand();
        } else {
            throw new IllegalArgumentException("Invalid command type: " + commandType);
        }
    }
    private void handlePushCommand(String segment) throws IOException {
        if (segment.equals("constant")) {
            writer.write("D=A\n");
        } else {
            writer.write("D=M\n");
        }
        pushDToStack();
    }
    private void handlePopCommand() throws IOException {
        writer.write("D=A\n");
        writer.write("@R13\n");
        writer.write("M=D\n");
        popStackToD();
        writer.write("@R13\n");
        writer.write("A=M\n");
        writer.write("M=D\n");
    }


    public void close() throws IOException{
        writer.close();
    }
    private void translateSegment(String segment, int index) throws IOException {
        switch (segment) {
            case "constant":
                writer.write("@" + index + "\n");
                break;
            case "static":
                writer.write("@" + fileName + "." + index + "\n");
                break;
            case "temp":
                writer.write("@R" + (5 + index) + "\n");
                break;
            case "pointer":
                writer.write("@" + (index == 0 ? "THIS" : "THAT") + "\n");
                break;
            default:
                translateBaseSegment(segment, index);
        }
    }

    private void translateBaseSegment(String segment, int index) throws IOException {
        if (SEGMENT_BASE.containsKey(segment)) {
            writer.write("@" + SEGMENT_BASE.get(segment) + "\n");
            writer.write("D=M\n");
            writer.write("@" + index + "\n");
            writer.write("A=D+A\n");
        } else {
            throw new IllegalArgumentException("Invalid segment: " + segment);
        }
    }

    private void pushDToStack() throws IOException {
        writer.write("@SP\n");
        writer.write("A=M\n");
        writer.write("M=D\n");
        writer.write("@SP\n");
        writer.write("M=M+1\n");
    }

    private void popStackToD() throws IOException {
        writer.write("@SP\n");
        writer.write("AM=M-1\n");
        writer.write("D=M\n");
    }

    private void decrementSP() throws IOException {
        writer.write("@SP\n");
        writer.write("M=M-1\n");
    }

    private void incrementSP() throws IOException {
        writer.write("@SP\n");
        writer.write("M=M+1\n");
    }
    private void setAToStack() throws IOException {
        writer.write("@SP\n");
        writer.write("A=M\n");
    }
}