import java.util.*;
import java.io.*;


class Symbol{
    String symbol_name;
    int address;
    int length;

    Symbol(){
        address = -1;
        length = 0;
        symbol_name = "";
    }

    Symbol(String name){
        this.symbol_name = name;
    }

    Symbol(String name,int addr){
        this.symbol_name = name;
        this.address  = addr;
    }

    void SetSize(int size){
        this.length = size;
    }

    int GetAddress(String symbol){
        return this.address;
    }

    void SetSymbol(String sy,int addr){
        this.address = addr;
        this.symbol_name = sy;
    }
}


class Literal{
    String literal;
    int address;
    
    Literal(){
        literal = "";
        address = -1;
    }

    void SetLiteral(String lit,int addr){
        this.address = addr;
        this.literal = lit;
    }
}




public class SinglePassAssembler{
    static BufferedReader buffer_read = null;
    static FileWriter file_write = null;
    static BufferedWriter buff_write = null;
    static FileReader file_read = null;
    static int Symboltablesize;
    Hashtable<String,Integer>MachineOpcodeTable = new Hashtable<>();
    Hashtable<String,Integer>AssemblerDirectivetable = new Hashtable<>();
    Hashtable<String,Integer>ConditionCodeTable = new Hashtable<>();
    Hashtable<String,Integer>RegisterTable = new Hashtable<>();
    Symbol Symboltable[];
    Literal Literaltable[];
    int Pooltable[];
    int ProgramCounter;
    int LiteralPtr;
    int SymbolPtr;
    int PoolPtr; 

    SinglePassAssembler(){
        Symboltablesize = 10;
        Pooltable = new int[Symboltablesize];
        ProgramCounter = LiteralPtr = SymbolPtr = 0;
        Pooltable[0] = PoolPtr = 0;
        Symboltable = new Symbol[Symboltablesize];
        Literaltable = new Literal[Symboltablesize];
        String opcodes[] = {"MOVER","MOVEM","ADD","SUB","MUL","DIV","PRINT","READ","BC","STORE"};
        String directives[] = {"START","END","ORIGIN","EQU","LTORG"};
        String condtion[] = {"LT","LE","GT","GE","EQ","ANY"};
        String registers[] = {"AREG","BREG","CREG","DREG"};
        int numeric_code = 1;
        for(String opcode : opcodes){
            MachineOpcodeTable.put(opcode,numeric_code);
            numeric_code+=1;
        }
        numeric_code = 1;
        for(String dir : directives){
            AssemblerDirectivetable.put(dir,numeric_code);
            numeric_code+=1;
        }
        numeric_code = 1;
        for(String reg : registers){
            RegisterTable.put(reg,numeric_code);
            numeric_code+=1;
        }
        numeric_code = 1;
        for(String cond : condtion){
            ConditionCodeTable.put(cond,numeric_code);
            numeric_code+=1;
        }
        for(int i =0;i<Symboltablesize;i++){
            Symboltable[i] = new Symbol();
            Literaltable[i] = new Literal();
            Pooltable[i] = -1;
        }
    }

    private int SearchSymbolTable(String label){
        for(int i =0;i<Symboltablesize;i++) if(Symboltable[i].symbol_name.equals(label)) return i;
        return -1;
    }

    private void PrintSymboltable(){
        for(Symbol s : Symboltable) System.out.println(s.symbol_name+" "+s.address);
    }

    private void PrintLiteralTable(){
        for(Literal l : Literaltable) System.out.println(l.literal+" "+l.address);
    }

    private void PrintDataBases(Hashtable<String,Integer>Table){
        Table.forEach((code,code_num) -> System.out.println(code+" : "+code_num));
    }

    private void PopulateDataBases(){
        try {
            FileWriter file = new FileWriter("SymbolTable.txt");  
            file.write("Symbol  address  length\n");
            for(int i =0;i<Symboltablesize;i++){
                if(Symboltable[i].symbol_name.length()==0) break;
                file.write(Symboltable[i].symbol_name+ " " + Symboltable[i].address+ " " + Symboltable[i].length+'\n');
            }
            file.close();
            file = new FileWriter("LiteralTable.txt");
            file.write("Literal  address\n");
            for(int i =0;i<Symboltablesize;i++){
                if(Literaltable[i].literal.length()==0) break;
                file.write(Literaltable[i].literal+" "+Literaltable[i].address+'\n');
            }
            file.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }
    }

    private void ProcessLiteral(String lit,int numeric_code,int cond_code,int reg_code) throws IOException{
        String writeline="";
        Literaltable[LiteralPtr].literal = lit;
        LiteralPtr+=1;
        int code = -1;
        if(reg_code>-1) code = reg_code;
        if(cond_code>-1) code = cond_code;
        writeline =  ProgramCounter + " " + " IS " + numeric_code + " "+ code + " L "+ (LiteralPtr-1);
        System.out.println(writeline);
        file_write.write(writeline+'\n');
    }

    private static String[] Trim(String pat,char c){
        String token[]={"",""};
        for(int i=0;i<pat.length();i++){
            if(pat.charAt(i)==c){
               token[0] =  pat.substring(0, i);
               token[1] = pat.substring(i+1);
               return token;
            }
        }
        return token;
    }

    private void ProcessSymbol(String sym,int reg_code,int cond_code,int numeric_code) throws IOException{
        int symbol_indx = SearchSymbolTable(sym);
        if(symbol_indx==-1){
            Symboltable[SymbolPtr].symbol_name = sym;
            SymbolPtr+=1;
            symbol_indx = SymbolPtr-1;
        }
        int code = -1;
        if(reg_code>-1) code = reg_code;
        else if(cond_code>-1) code = cond_code;
        Symboltable[symbol_indx].length = 1;
        System.out.println(ProgramCounter + " " + " IS " + numeric_code + " "+ code + " S "+(symbol_indx)  );
        file_write.write( ProgramCounter + " " + " IS " + numeric_code + " "+ code + " S "+(symbol_indx) +"\n");  
    }


    public static void main(String[] args) {
        try {
            String Source_File = "source_program.txt";
            String Target_file = "target_object_code.txt";
            file_read = new FileReader(Source_File);
            buffer_read = new BufferedReader(file_read);
            file_write = new FileWriter(Target_file);
            buff_write = new BufferedWriter(file_write);
            SinglePassAssembler assm = new SinglePassAssembler();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
} 