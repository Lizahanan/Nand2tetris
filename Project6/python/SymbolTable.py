'''
    Symbol Table 
    Manages symbols, such as predefined, labels and variables 

    
'''

class SymbolTable:

    def __init__(self):
        #predefined symbols for the hack computer 
        self.table ={
            "SP": 0,
            "LCL": 1,
            "ARG": 2,
            "THIS": 3,
            "THAT": 4,
            "SCREEN": 16384,
            "KBD": 24576,
            #register R0 to R15 using dictionary comprehension
        }
        #register R0 to R15 using dictionary comprehension
        self.table.update({f"R{i}": i for i in range(16)})

        #set the next available address to 16
        self.next_available_address = 16


    def addEntry (self, symbol , address):
        ''' Add a new entry to the table '''
        self.table[symbol] = address

    def contains(self, symbol):
        '''Check if the symbol is in the table '''
        return symbol in self.table
    
    def getAddress(self, symbol):
        '''Returns the address asossiated with the symbol'''
        return self.table[symbol]

    
