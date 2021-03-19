import java.util.*;
import java.io.*;


public class TwoPassMacroProcesser {
    static BufferedReader buffer_read = null;
    static FileWriter file_write = null;
    static BufferedWriter buff_write = null;
    static FileReader file_read = null;
    Hashtable<String,Integer>MacroNametable; // MacroNametable(Macro Name, MDT index)
    String[] MacroDefinationTable;// MacroDefinationTable(token)
    String[] ArgumentList;
    int mdtc =0;
    int mntc = 0;
    int databasesize = 20;

    public String PassOneMacroProcesser() throws Exception{
        String CurrSourceLine="";
        while((CurrSourceLine=buffer_read.readLine())!=null){
            String token[] = CurrSourceLine.split("[ \t]+");
            
        }
        return "Pass one successful";
    }


    TwoPassMacroProcesser(){
       MacroDefinationTable = new String[databasesize];
       ArgumentList = new String[databasesize];
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
            system.PassOneMacroProcesser();
            
            buff_write.close();
            buffer_read.close();


        }catch(Exception e){
            System.out.println("Error : "+e.getMessage());
        }
    }
}
