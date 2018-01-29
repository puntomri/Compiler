import java.util.ArrayList;
import java.util.Hashtable;

public class SymbolTable extends ArrayList {


Hashtable<String,Var> ctable;
Hashtable<String,Var> mtable;

int fc;
int sc;
int vc;
int ac;

SymbolTable(){

    ctable = new Hashtable();
    mtable = new Hashtable();
    fc=0;
    sc=0;
    vc=0;
    ac=0;
    this.add(ctable);
    this.add(mtable);

}


public void startMethod(){
    mtable.clear();
    vc=0;
    ac=0;

}

public void insert(String n,String t,String k){
    int i=0;
    boolean b=false;
    if (k.equals("static")) {i=sc; b= true; sc++;}
    if (k.equals("field")) {i=fc; b = true;fc++;}
    if (k.equals("var")) {i=vc; vc++;}
    if (k.equals("arg")) {i=ac; ac++;}

    Var var = new Var(i,t,k);
    if (b) ctable.put(n,var);
    else mtable.put(n,var);

}

    public int varcount(String k){
        int i=0;

        if (k.equals("static")) i=sc;
        if (k.equals("field")) i=fc;
        if (k.equals("var")) i=vc;
        if (k.equals("arg")) i=ac;

       return i;

    }

    public String kind(String n){
        String k = "none";

        if (mtable.containsKey(n)) {
            k=mtable.get(n).kind;
            if(k.equals("var")) return "local";
            if(k.equals("arg")) return "argument";
        }
        else if (ctable.containsKey(n)) {
            k=ctable.get(n).kind;
            if(k.equals("static")) return "static";
            if(k.equals("field")) return "this";

        }  return k;

    }

    public String type(String n){
        String t = "";

        if (mtable.containsKey(n)) return mtable.get(n).type;
        if (ctable.containsKey(n)) return ctable.get(n).type;

        return t;

    }

    public int index(String n){
        int i=0;

        if (mtable.containsKey(n)) return mtable.get(n).index;
        else if (ctable.containsKey(n)) return ctable.get(n).index;
        else return i;

    }


}
