import java.util.*;

public class UnitTesting {
   static String[] Trim(String pat,char c){
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
    public static void main(String[] args) {
        // String token[] = "READ =5".split("[ \t]+");
        // System.out.println(token[1].substring(1));
        String pat = "L1+30";
        String token[] = Trim(pat,'+');
        for(String t : token) System.out.println(t);
        // int x = 2;
        // System.out.println((x==2?"YES":"NO"));
    }
}
