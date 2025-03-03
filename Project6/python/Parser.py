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
        
            