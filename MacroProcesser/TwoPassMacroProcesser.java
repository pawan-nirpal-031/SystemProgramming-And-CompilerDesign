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
    StringIntPair[] ArgumentList; // ArgumentList(Arg, ptr_to_the_host_macro)
    int mdtc =0;
    int mntc = 0;
    int args_list =0;
    int databasesize = 20;

    public String PassOneMacroProcesser() throws Exception{
        String CurrSourceLine="";
        boolean macro_dfn = false; // indicates if we are currently wihtin MACRO defination body
        while((CurrSourceLine=buffer_read.readLine())!=null){
            String token[] = CurrSourceLine.split("[ \t]+");
            if(token[0].equals("MACRO")) macro_dfn= true;
            if(!macro_dfn){
                file_write.write(CurrSourceLine+'\n');
            }else{
                if(mdtc==databasesize) return "Error : Macro Defination Table size exceded";
                if(mdtc>0 && MacroDefinationTable[mdtc-1].substring(0,5).equals("MACRO")){ // here (mdtc) we find the defination of macro with actual args 
                    for(int i =0;i<token.length;i++){
                        if(token[i].charAt(0)!='&'){// meaning this is macro name
                            MacroNametable[mntc].key = token[i];
                            MacroNametable[mntc].value = mdtc;
                            mntc+=1;
                        }else{// Arguments go heres
                            ArgumentList[args_list].key = token[i];
                            ArgumentList[args_list].value = mdtc;
                            args_list+=1;
                        }
                    }
                }
                MacroDefinationTable[mdtc] = CurrSourceLine;
                mdtc+=1;
                if(token[0].equals("MEND")) macro_dfn = false;
            }
        }
        return "Pass one successful";
    }

    public String PassTwoMacroProcesser() throws Exception{

        return "Pass two successful";
    }

    TwoPassMacroProcesser(){
       MacroDefinationTable = new String[databasesize];
       ArgumentList = new StringIntPair[databasesize];
       MacroNametable = new StringIntPair[databasesize];
       for(int i =0;i<databasesize;i++){
            MacroDefinationTable[i] ="";
            ArgumentList[i]  = new StringIntPair();
            MacroNametable[i] = new StringIntPair();
       }
       mdtc = mntc = args_list = 0;
    }


    public static void main(String[] args) throws Exception{
        
            String Source_File = "macro_processer_pass_one_input.txt"; // source assembly file
            String Target_file = "macro_processer_pass_one_output.txt"; // intermidiate code
            file_read = new FileReader(Source_File);
            buffer_read = new BufferedReader(file_read);
            file_write = new FileWriter(Target_file);
            buff_write = new BufferedWriter(file_write);
            TwoPassMacroProcesser system = new TwoPassMacroProcesser();
            String logs = system.PassOneMacroProcesser();
            if(!logs.equals("Pass one successful")){
                System.out.println(logs);
            }
            buff_write.close();
            buffer_read.close();


        // }catch(Exception e){
        //     System.out.println(" Failure : "+e.getMessage());
        // }
    }
}
