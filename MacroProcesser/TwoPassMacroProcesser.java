import java.util.*;
import java.io.*;

class StringIntPair{
    String key;
    int value;
    StringIntPair(){
        key = "";
        value = -1;
    }
    StringIntPair(String k,int v){
        key = k;
        value = v;
    }
}
public class TwoPassMacroProcesser {
    static BufferedReader buffer_read = null;
    static FileWriter file_write = null;
    static BufferedWriter buff_write = null;
    static FileReader file_read = null;
    StringIntPair[] MacroNametable; // MacroNametable(Macro Name, MDT index)
    String[] MacroDefinationTable;// MacroDefinationTable(token)
    String[] ArgumentList;
    int mdtc =0;
    int mntc = 0;
    int databasesize = 20;

    public String PassOneMacroProcesser() throws Exception{
        String CurrSourceLine="";
        boolean macro_dfn = false;
        while((CurrSourceLine=buffer_read.readLine())!=null){
            String token[] = CurrSourceLine.split("[ \t]+");
            if(token[0].equals("MACRO")) macro_dfn= true;
            if(!macro_dfn){
                for(String t : token) file_write.write(t+' ');
                file_write.write('\n');
            }else{
                if(mdtc==databasesize-1) return "Error : Macro Defination Table size exceded";
                if(token.length>=2){
                    boolean is_macro_name = true;
                    for(int i =0;i<token.length;i++){
                        if(i!=1 && (token[i].charAt(0)!='&')) is_macro_name = false;
                    }
                    if(is_macro_name){
                        MacroNametable[mntc].key = token[1];
                        MacroNametable[mntc].value = mdtc;
                    }
                }
                MacroDefinationTable[mdtc] = CurrSourceLine;
                mdtc+=1;
                if(token[0].equals("MEND")) macro_dfn = false;
            }
        }
        for(String t : MacroDefinationTable) System.out.println(t);
        return "Pass one ok";
    }


    TwoPassMacroProcesser(){
       MacroDefinationTable = new String[databasesize];
       ArgumentList = new String[databasesize];
       MacroNametable = new StringIntPair[databasesize];
       for(int i =0;i<databasesize;i++) MacroDefinationTable[i] = ArgumentList[i] = "";
       mdtc = mntc = 0;
    }


    public static void main(String[] args) {
        try{
            String Source_File = "macro_processer_pass_one_input.txt"; // source assembly file
            String Target_file = "macro_processer_pass_one_output.txt"; // intermidiate code
            file_read = new FileReader(Source_File);
            buffer_read = new BufferedReader(file_read);
            file_write = new FileWriter(Target_file);
            buff_write = new BufferedWriter(file_write);
            TwoPassMacroProcesser system = new TwoPassMacroProcesser();
            String logs = system.PassOneMacroProcesser();
            if(!logs.equals("Pass one ok")){
                System.out.println(logs);
            }
            buff_write.close();
            buffer_read.close();


        }catch(Exception e){
            System.out.println("Error : "+e.getMessage());
        }
    }
}
