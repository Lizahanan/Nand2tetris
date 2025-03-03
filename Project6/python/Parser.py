''' Reads the input assembly file, cleans it (removes whitespace and comments), 
    and provides methods to identify and parse each command. 
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