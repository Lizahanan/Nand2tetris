''' Reads the input assembly file, cleans it (removes whitespace and comments), 
    and provides methods to identify and parse each command. 
    Notice: In the course what i call command is called instruction
    This is just something i find intuitively easier and better

'''

class Parser:

    def __init__(self, file_path):
        self.commands = [] #list of commands
        #open the file and process line by line 
        with open(file_path, 'r') as f:
            for line in f:
                #remove whitespace and comments
                line = line.split("//")[0].strip()
                if line:
                    self.commands.append(line)
        self.current_command = None
        self.current_command_index = -1

    
    def hasMoreCommands(self):
        ''' Check if there are more commands '''
        #boolean returns true or false
        return self.current_command_index + 1 < len(self.commands)
    
    def advance(self):
        ''' Get the next command if it exists'''
        if self.hasMoreCommands():
            self.current_command_index += 1
            self.current_command = self.commands[self.current_command_index]


    ''' Now that we got the current instruction we can move onto parsing the current imstruction '''

    def commandType(self):
        ''' Returns the type of the current command
            A_COMMAND for @Xxx where Xxx is either a symbol or a decimal number
            C_COMMAND for dest=comp;jump
            L_COMMAND for (Xxx) where Xxx is a symbol
        '''
        if self.current_command.startswith("@"):
            return "A_COMMAND"
        elif self.current_command.startswith("("):
            return "L_COMMAND" 
        else:
            return "C_COMMAND"
        
    
    def symbol(self):
        ''' Returns the symbol of the current command 
            Only called when commandType is A_COMMAND or L_COMMAND
            for A_COMMAND returns Xxx where @Xxx
            for L_COMMAND returns Xxx where (Xxx)
        '''
        if self.commandType() == "A_COMMAND":
            return self.current_command[1:] #remove the @ using slicing 
        elif self.commandType() == "L_COMMAND":
            return self.current_command[1:-1] #from first index to second last index using slicing, remove the parenthesis
        else:
            raise ValueError("Command type is not A_COMMAND or L_COMMAND, cannot return symbol")
        
    '''The following 3 methods are only called for C_COMMAND
    They are used to parse the current command into its components
    dest, comp, jump
    dest=comp;jump
    '''

    def dest(self):
        ''' Returns the dest mnemonic for the current 
        command if the current command is C_COMMAND
        '''
        #check if command is C_COMMAND
        if self.commandType() == "C_COMMAND":
            if '=' in self.current_command:
                return self.current_command.split("=")[0].strip()
            else:
                return "" #if there is no dest return empty string
        else:
            raise ValueError("Command type is not C_COMMAND, cannot return dest")
        
    def comp(self):
        '''Returns the comp mnemonic for the current command
        if the current command is C_COMMAND
        '''
        if self.commandType()== "C_COMMAND":
            parts = self.current_command.split("=")
            #if there is no dest, the comp is the whole command
            comp_jump = parts[1] if len(parts) == 2 else parts[0] #if there is no dest, the comp is the whole command
            if ';' in comp_jump:
                return comp_jump.split(";")[0].strip() #return the comp part if jump is present
            else:
                return comp_jump.strip() #return the comp part if jump is not present
        else:   
            raise ValueError("Command type is not C_COMMAND, cannot return comp")


    def jump(self):
        '''Returns the jump mnemonic for the current command
        if the current command is C_COMMAND
        '''
        if self.commandType() == "C_COMMAND":
            if ';' in self.current_command:
                return self.current_command.split(";")[1].strip()
            else:
                return ""
        else:
            raise ValueError("Command type is not C_COMMAND, cannot return jump")   


            
        
        
        
