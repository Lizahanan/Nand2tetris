
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private Map<String,Integer> table;

    //initializer creates an empty symbol table 
    public SymbolTable(){
        table = new HashMap<>();

        //we add predefined symbols 
        addEntry("SCREEN", 16384);
        addEntry("KBD", 24576);
        addEntry("SP", 0);
        addEntry("LCL", 1);
        addEntry("ARG", 2);
        addEntry("THIS", 3);
        addEntry("THAT", 4);  

        
        //register R0 to R15 usiing for loop
        for (int i = 0; i<=15; i++){
            addEntry("R"+i, i); 
        }
    }

    public void  addEntry(String symbol, int address){
        table.put(symbol, address);
    }

    public boolean contains(String symbol) {
        return table.containsKey(symbol);
    }

    public int getAddress(String symbol){
        return table.get(symbol);
    }

}
