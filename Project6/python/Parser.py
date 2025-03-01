class Parser :


    class Instruction_type:
        '''defines the different types of hack instructions '''
        A_INSTRUCTION = "A_INSTRUCTION" # @value
        C_INSTRUCTION = "C_INSTRUCTION" # dest=comp;jump
        L_INSTRUCTION = "L_INSTRUCTION" # (label)
    
    def __init__(self, file_path):
        '''initializes the parser and opens the source tect file'''
        self.file = open(file_path, 'r')
        self.current_instruction = None

    def has_more_instructions(self):
        '''checks if there are more lines to read in the file'''
        for line in self.file:
            line = line.split('//')[0].strip() # remove comments
            if line:
                self.current_instruction = line
                return True
        return False
    
    def advance(self):
        '''reads the next instruction from the file'''
        return self.current_instruction 
    
    def instruction_type(self):
        '''returns the type of the current instruction'''
        if self.current_instruction.startswith('@'):
            return self.Instruction_type.A_INSTRUCTION
        elif self.current_instruction.startswith('('):
            return self.Instruction_type.L_INSTRUCTION
        else:
            return self.Instruction_type.C_INSTRUCTION
        
    
    def get_current_instruction(self):
        '''returns the current instruction'''
        return self.current_instruction
    
    def close(self):
        '''closes the file'''
        self.file.close()