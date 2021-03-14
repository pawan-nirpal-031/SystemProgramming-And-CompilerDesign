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




public class TwoPassAssembler {
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


    TwoPassAssembler(){
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

    public String PassTwo() throws Exception{
        String source_inter_file  = "pass_one_output.txt";
        String object_file = "object_code.txt";
        FileReader file_rd = new FileReader(source_inter_file);
        BufferedReader buf_read = new BufferedReader(file_rd);
        FileWriter file_wrte = new FileWriter(object_file);
        BufferedWriter buf_wrte = new BufferedWriter(file_wrte);
        String CurrSourceLine = "";
        if(file_rd==null) return "Error : Passone output file not found";
        while((CurrSourceLine=buf_read.readLine())!=null){
            String token[] = CurrSourceLine.split("[ \t]+");
            if(token[0].length()==0){
                 file_wrte.write("    -1 -1 -1 ");
            }else{
               file_wrte.write(token[0]+" "); // PC value 
               if(token[1].equals("AD")){
                    file_wrte.write("-1 -1 ");
                    file_wrte.write(token[4]);
               }else if(token[1].equals("IS")){
                    file_wrte.write(token[2]+" "); // Instruction number
                    file_wrte.write(token[3]+" "); // Register number if present else -1
                    if(token.length==6 &&( token[4].equals("S") || token[4].equals("L"))){ // Symbol address if symbol
                    int val = (token[4].equals("S")?Symboltable[Integer.parseInt(token[5])].address:Literaltable[Integer.parseInt(token[5])].address);  
                    file_wrte.write(String.valueOf(val));
                    }
                }else if(token[1].equals("DL")){
                    file_wrte.write("-1 -1 ");
                    if(token[2].equals("1")) file_wrte.write(token[4]);
                    else file_wrte.write("-1 ");
                }
            }
            file_wrte.write('\n');
        }
        file_wrte.close();
        file_rd.close();
        buf_read.close();
        return "Object code succesfully genrated";
    }

    public String PassOne() {
       try {
            String CurrSourceLine= "";
            Pooltable[PoolPtr] =0;
            while((CurrSourceLine=buffer_read.readLine())!=null){
                String token[] = CurrSourceLine.split("[ \t]+");
                if((token.length==3 && token[1].equals("STORE")) || ((token.length==3 || token.length==2) && MachineOpcodeTable.containsKey(token[0])) || ( token.length==4 && MachineOpcodeTable.containsKey(token[1]))){
                    int numeric_code = MachineOpcodeTable.get(((token.length==4 || (token.length==3 && token[1].equals("STORE")))?token[1]:token[0]));  // numeric code of this opcode
                    int reg_code = -1;
                    int cond_code = -1;
                    if(token.length>=3){
                        if(token.length==4){// has label - L1 MOVER AREG B
                            int symbol_indx = SearchSymbolTable(token[0]);
                            if(symbol_indx==-1){
                                Symboltable[SymbolPtr].symbol_name = token[0];
                                Symboltable[SymbolPtr].address = ProgramCounter;
                                if(Symboltable[SymbolPtr].length==0) Symboltable[SymbolPtr].length = 1;
                                SymbolPtr+=1;    
                                for(int i =0;i<3;i++) token[i] = token[i+1];
                            }else if(symbol_indx>-1 && Symboltable[symbol_indx].address==-1){ // forward refrenced label
                                Symboltable[symbol_indx].address = ProgramCounter;
                                if(Symboltable[symbol_indx].length==0) Symboltable[symbol_indx].length = 1;
                            } else return "Duplicate label";
                        }
                        if(token[1].equals("STORE") && token.length==3){// ex : LOOP STORE X or LOOP STORE =4
                            ProcessSymbol(token[0], reg_code, cond_code, numeric_code);
                            if(token[2].charAt(0)=='='){// LOOP STORE =4
                                ProcessLiteral(token[2].substring(1), numeric_code,-1,-1);
                            }else{// LOOP STORE X
                                ProcessSymbol(token[2], reg_code, cond_code, numeric_code);
                            }
                            int symbol_indx = SearchSymbolTable(token[0]);
                            if(symbol_indx==-1) return "Error : trying to access unavilable symbol";
                            Symboltable[symbol_indx].address = ProgramCounter;
                            continue;
                        }
                        //by now - MOVER AREG B or SUB CREG =5
                        if(RegisterTable.containsKey(token[1])) reg_code = RegisterTable.get(token[1]);
                        if(ConditionCodeTable.containsKey(token[1])) cond_code = ConditionCodeTable.get(token[1]);
                        if(token[2].charAt(0)=='='){// literal not symbol
                            ProcessLiteral(token[2].substring(1), numeric_code,cond_code,reg_code);
                        }else{ // symbol
                            ProcessSymbol(token[2], reg_code, cond_code, numeric_code);
                        }
                    }else if(token.length==2){ // READ =5 or PRINT B 
                        numeric_code = MachineOpcodeTable.get(token[0]);
                        if(token[1].charAt(0)=='='){ // means litreal read/print
                            ProcessLiteral(token[1].substring(1), numeric_code,-1,-1);
                            // PoolPtr holds the end index of previous pool
                        }else{  
                            ProcessSymbol(token[1], reg_code, cond_code, numeric_code);
                        }
                    }
                    ProgramCounter+=1;
                } 
                else if(token.length==3 && (token[1].equals("DS") || token[1].equals("DC")) ) { // Symbol_name DS/DC size/const 
                    int symbol_indx = SearchSymbolTable(token[0]); // DS -> 0 , DC -> 1
                    if(symbol_indx==-1){ // not added yet add now
                        Symboltable[SymbolPtr].symbol_name = token[0];
                        Symboltable[SymbolPtr].address = ProgramCounter;
                        Symboltable[SymbolPtr].length = (token[1].equals("DS")?Integer.parseInt(token[2]):1);
                        SymbolPtr+=1;
                    }else{
                        Symboltable[symbol_indx].address = ProgramCounter;
                        Symboltable[symbol_indx].length = (token[1].equals("DS")?Integer.parseInt(token[2]):1);
                    }
                    file_write.write(ProgramCounter + " "+" DL "+(token[1].equals("DS")?0:1)+" -1 "+ token[2]+"\n");
                    System.out.println(ProgramCounter + " "+" DL "+ (token[1].equals("DS")?0:1) +" -1 "+ token[2]);
                    ProgramCounter+=((token[1].equals("DS"))?Integer.parseInt(token[2]):1);
                }
                else if(AssemblerDirectivetable.containsKey(token[0])){
                    int numeric_code = AssemblerDirectivetable.get(token[0]);
                    if(token[0].equals("START")){ // start directive config
                        if(token.length==2) ProgramCounter = Integer.parseInt(token[1]);
                        else ProgramCounter = 0;
                        file_write.write("     AD "+numeric_code + " -1 " + " C " + ProgramCounter+"\n");
                        System.out.println("     AD "+numeric_code + " -1 " + " C " + ProgramCounter);
                    }else if(token[0].equals("LTORG") || token[0].equals("END")){
                        if(Literaltable[Pooltable[PoolPtr]].literal.length()>0){ // 1st LTORG, PoolPtr is last pool's index
                            String writline = "";
                            for(int i = Pooltable[PoolPtr];i<LiteralPtr;i++){
                                writline = ProgramCounter + " " + " AD " + numeric_code +" -1 " + Literaltable[i].literal;
                                Literaltable[i].address = ProgramCounter;
                                file_write.write(writline+'\n');
                                System.out.println(writline);
                                ProgramCounter+=1;
                            }
                            Pooltable[++PoolPtr] = LiteralPtr;
                        }
                        if(token[0].equals("END")){
                            file_write.write(ProgramCounter+" " + " AD "+numeric_code+" -1 -1 \n");
                            System.out.println(ProgramCounter+" " + " AD "+numeric_code+" -1 -1 ");
                        } 
                    }else if(token[0].equals("ORIGIN")){
                        String labels[] = Trim(token[1], '+');
                        int offset = Integer.parseInt(labels[1]);
                        int lblindx = SearchSymbolTable(labels[0]);
                        if(lblindx==-1) return "Invalid label";
                        int label_addr = Symboltable[lblindx].address;
                        ProgramCounter = label_addr+offset;
                    }else{ // EQU statement 
                        
                    }
                }
            }
            PopulateDataBases();   
            file_write.close();
            return "Build Successful";
       } catch (Exception e) {
            return e.getMessage();
       }
    }

    public static void main(String[] args) {
        try{
            String Source_File = "pass_one_input.txt";
            String Target_file = "pass_one_output.txt";
            file_read = new FileReader(Source_File);
            buffer_read = new BufferedReader(file_read);
            file_write = new FileWriter(Target_file);
            buff_write = new BufferedWriter(file_write);
            TwoPassAssembler assm = new TwoPassAssembler();
            String logs = assm.PassOne();
            buff_write.close();
            buffer_read.close();
            file_write.close();
            file_read.close();
          //  System.out.println("\n\n"+logs);
            String logsPass2 =  assm.PassTwo();
            System.out.println(logsPass2);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
       
    }

    
}
