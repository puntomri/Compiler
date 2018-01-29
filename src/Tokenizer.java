import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Tokenizer {

    Scanner scanner;
    ArrayList <String> tokens;
    Writer w;
    boolean comenton;
    SymbolTable st;
    String cls;
    int labelcounter;


    public Tokenizer(File file,Writer writer) {
        labelcounter=0;

        comenton = false;
        String f = file.getName();
        f = f.replace(".jack", ".vm");
        w = new Writer(f);
        tokens = new ArrayList<String>();
        st = new SymbolTable();

        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
        while (scanner.hasNextLine()) {
            String rline = scanner.nextLine();
            breakline(rline);

        }
       // for(int i=0;i<tokens.size();i++) System.out.println(tokens.get(i));
        classcompiler(tokens);



        w.close();

    }



    public void breakline(String cline){
        String[] tr;
        String line;

        if (cline.contains("/**")) {
            comenton = true;

        }
        if (comenton) {
            if (!cline.contains("*/")) return;
            else{
                comenton = false;
                return;
            }
        }

       String token ="";

        if(cline.length()==0||cline.charAt(0)=='/') return;
        tr = cline.split("//");
        line= tr[0];
        line=line.trim();


            String[] splited = line.split(" ");
            int l = splited.length;
            boolean ststring = false;
            for(int i=0; i<l;i++){



                    for (int j = 0; j < splited[i].length(); j++) {
                        char c = splited[i].charAt(j);
                        if (!ststring&&(c == '{' || c == '}' || c == '(' || c == ')' || c == '[' || c == ']' || c == '.' || c == ',' || c == ';' || c == '+'
                                || c == '-' || c == '*' || c == '/' || c == '&' || c == '|' || c == '<' || c == '>' || c == '=' || c == '~')) {

                            if (token.length() > 0) {
                                tokens.add(token);

                                token = "";
                            }


                             tokens.add("" + c);


                        } else {

                            token = token + c;
                            if (c=='"') {

                                if (!ststring) ststring = true;
                                else ststring = false;

                            }

                        }
                    }

                    if (token.length() > 0) {
                        if (!ststring) {
                            tokens.add(token);
                            token = "";
                        }
                    }

                    if (ststring) token = token+" ";
            }


    }


    public String tokentype(String s){

        char c = s.charAt(0);

        if (s.equals("class")||s.equals("method")||s.equals("function")||s.equals("constructor")||s.equals("int")||
               s.equals("boolean")||s.equals("char")||s.equals("void")||s.equals("var")||s.equals("static")||
                s.equals("field")||s.equals("let")||s.equals("do")||s.equals("if")||s.equals("else")||
                s.equals("while")||s.equals("return")||s.equals("true")||s.equals("false")||s.equals("null") ||s.equals("this") ){

                return "keyword";


        } else if (c=='{'||c=='}' ||c== '(' ||c== ')' ||c== '[' ||c== ']' ||c== '.' ||c== ',' || c== ';' ||c== '+'
                ||c== '-' ||c== '*' ||c== '/' ||c== '&' ||c== '|' ||c== '<' ||c== '>' ||c== '=' ||c== '~') {

            return "symbol";
        } else if (c=='"'){

            return "stringConstant";
        } else if (c=='1'||c=='2' ||c== '3' ||c== '4' ||c== '5' ||c== '6' ||c== '7' ||c== '8' || c== '9' ||c== '0' ){
            return "integerConstant";
        } else {
            return "identifier";
        }


    }


    public void classcompiler(ArrayList <String> tokens){

        String tok;

        String classname=tokens.get(1);
        cls = classname;
        while (!tokens.isEmpty()){
              tok = tokens.get(0);

              if (tok.equals("field")||tok.equals("static")){
                 classvarcompiler(tokens);
              } else if (tok.equals("constructor")||tok.equals("method")||tok.equals("function")){

                  subroutinecompiler(tokens,classname);
              } else {

                  tokens.remove(0);
              }
        }


    }


    public void classvarcompiler(ArrayList <String> tokens){

        String kind = tokens.remove(0);
        String  type = tokens.remove(0);
        String   name = tokens.remove(0);

            st.insert(name,type,kind);

            while(!tokens.get(0).equals(";")){
                tokens.remove(0); // ","
                name = tokens.remove(0); // "name"
                st.insert(name,type,kind);

            }


        tokens.remove(0);

    }




    public void subroutinecompiler(ArrayList <String> tokens,String classname){
        st.startMethod();
        String kind = tokens.remove(0); //removes "method"

       tokens.remove(0); //removes "void/type"
        String name = tokens.remove(0); //removes "name"
        tokens.remove(0); //removes "("
        int p =0;

        if (kind.equals("method")) st.insert("this","","arg");

        int c = parametercompiler(tokens);
        tokens.remove(0); //removes ")"

        //if (kind.equals("method")) p++;

        name = classname +"."+name;

        for(int i=0;i<tokens.size();i++){
            if(tokens.get(i).equals("var")||tokens.get(i).equals(",")) p++;

            if (tokens.get(i).equals("do")||tokens.get(i).equals("return")
                    ||tokens.get(i).equals("if")||tokens.get(i).equals("while")
                    ||tokens.get(i).equals("let")) break;

        }



        w.func(name,p);

        if (kind.equals("constructor")) {
            w.push("constant",st.varcount("field"));
            w.writer.println("call Memory.alloc 1");
            w.pop("pointer",0);
        }

        if(kind.equals("method")) {
            w.push("argument", 0);
            w.pop("pointer", 0);

        }
        subrutbodycomp(tokens);// "{"


    }



    public int parametercompiler(ArrayList <String> tokens){
        int counter = 0;
        String tok;
        tok = tokens.get(0);
        if (tok.equals(")")) return 0;
        counter++;


        while (!tokens.isEmpty()){

            tok = tokens.get(0);


            if (tok.equals(")")) break;
            else if (tok.equals(",")){
                counter++;
                tokens.remove(0);
            }
            else{
                String type = tokens.remove(0);
                String name = tokens.remove(0);
                st.insert(name,type,"arg");
            }


        }
        return counter;
    }



    public void subrutbodycomp(ArrayList <String> tokens){

        String tok;

        while (!tokens.isEmpty()){

            tok = tokens.get(0);


            if (tok.equals("var")) {

                varDeccomp(tokens);
            } else if (tok.equals("if")||tok.equals("while")||tok.equals("let")||tok.equals("do")||tok.equals("return")) {

               compilestatements(tokens);

            }
            else {
                tokens.remove(0); //"{}"
                if (tok.equals("}")) break;

            }

        }

    }

    public void varDeccomp(ArrayList <String> tokens){

        String kind = tokens.remove(0);
        String  type = tokens.remove(0);
        String   name = tokens.remove(0);

        st.insert(name,type,kind);

        while(!tokens.get(0).equals(";")){
            tokens.remove(0); // ","
            name = tokens.remove(0); // "name"
            st.insert(name,type,kind);
        }


        tokens.remove(0); //";"
    }

    public void compilestatements(ArrayList <String> tokens){

        String tok;

        while (!tokens.isEmpty()){

            tok = tokens.get(0);

            if (tok.equals("do")){
                docompile(tokens);
            } else if (tok.equals("let")){
               // tokens.remove(0);
                letcompile(tokens);
            } else if (tok.equals("while")){
                whilecompile(tokens);
            } else if (tok.equals("return")){
                returncompile(tokens);
            } else if (tok.equals("if")){
               ifcompile(tokens);
            } else {

                break;
            }

        }


    }

    public void docompile(ArrayList <String> tokens){

            tokens.remove(0); //"do"

            subroutinecallcompiler(tokens);

            w.pop("temp",0);

            tokens.remove(0); //":"

    }

    public void letcompile(ArrayList <String> tokens){


        tokens.remove(0); //"let"

        if (!tokens.get(1).equals("[")) {
            String name = tokens.remove(0); //"var"
            tokens.remove(0); //"="
                expression(tokens);
            tokens.remove(0); //";"
            w.pop(st.kind(name), st.index(name));
        } else{
            String name = tokens.remove(0); //"var"
            w.push(st.kind(name), st.index(name));
            tokens.remove(0); //"["
            expression(tokens);
            tokens.remove(0); //"]"
            w.ari("+");
            tokens.remove(0); //"="
            expression(tokens);

            tokens.remove(0); //";"
            w.pop("temp",0);
            w.pop("pointer",1);
            w.push("temp",0);
            w.pop("that",0);

        }

    }

    public void returncompile(ArrayList <String> tokens){


        tokens.remove(0);//"return"
        String tok=tokens.get(0);//";/result"
        if(!tok.equals(";")) expression(tokens);
        else w.push("constant",0);
        tokens.remove(0);//";"
        w.returnl();

    }




    public void whilecompile(ArrayList <String> tokens){
        int c = labelcounter;
        labelcounter++;

         tokens.remove(0); //"while"
            w.label("W1-",c);
         tokens.remove(0); //"("
               expression(tokens);
               w.ari("~");
         tokens.remove(0); //")"
         tokens.remove(0); //"{"
               w.ifl("W2-",c);
                    compilestatements(tokens);
                w.gotol("W1-",c);
                w.label("W2-",c);

        tokens.remove(0); //"}"



    }




    public void ifcompile(ArrayList <String> tokens){
        int c = labelcounter;
        labelcounter++;


        tokens.remove(0); //"if"

        tokens.remove(0); //"("
        expression(tokens);
        w.ari("~");
        tokens.remove(0); //")"

        tokens.remove(0); //"{"
        w.ifl("L1-",c);
        compilestatements(tokens);

        w.gotol("L2-",c);
        w.label("L1-",c);
        tokens.remove(0); //"}"
        if (tokens.get(0).equals("else")){
            tokens.remove(0); //"else"
            elsecompiler(tokens);
        }

        w.label("L2-",c);

        w.labelcounter++;


    }


    public void elsecompiler(ArrayList <String> tokens){

            tokens.remove(0); //"{
                compilestatements(tokens);
        tokens.remove(0); //"}
    }



    public void expression(ArrayList <String> tokens){

        termcompiler(tokens);

        String tok = tokens.get(0);
        if (!(tok.equals(")") || tok.equals("}") || tok.equals("]") || tok.equals(";") || tok.equals(","))){

            String s = tokens.remove(0); // "->*/"

            termcompiler(tokens);

            w.ari(s);


        }

    }

    public void termcompiler(ArrayList <String> tokens){

        String tok;

        tok = tokens.get(0);

        if (tok.equals("~")||tok.equals("-")) {

            String s= tokens.remove(0);
            if(s.equals("-")) s = "neg";

            termcompiler(tokens);

            w.ari(s);

        } else if (tokens.get(1).equals(".") || (tokens.get(1).equals("(") && (!tokens.get(0).equals("~") && !tokens.get(0).equals("-")&& !tokens.get(0).equals("(")))) {

            subroutinecallcompiler(tokens);

        }  else if (tok.equals("(")) {

                        tokens.remove(0); //"("

                        expression(tokens);

                        tokens.remove(0); //")"

        } else if (tokentype(tok).equals("integerConstant")) {

            w.push("constant",Integer.parseInt(tokens.remove(0)));

        } else if (tokentype(tok).equals("stringConstant")) {

            String tok2 = tok.replace('"',' ');
            tok2 = tok2.trim();
            int a= tok.length()-tok2.length()-2;
            for(int i=0;i<a;i++) tok2 = tok2 +" ";
            tok=tok2;
            w.push("constant",tok.length());
            w.writer.println("call String.new 1");

            for (int i=0;i<tok.length();i++) {

                w.push("constant",(int)tok.charAt(i));
                w.writer.println("call String.appendChar 2");

            }


            tokens.remove(0);


        }else if (tokentype(tok).equals("keyword")) {
            if (tok.equals("true")){
                w.push("constant",1);
                w.ari("neg");
            }
            if (tok.equals("false")) w.push("constant",0);
            if (tok.equals("null")) {
                w.push("constant",0);

            }
            if (tok.equals("this")) w.push("pointer",0);

            tokens.remove(0);


        } else if (tokentype(tok).equals("identifier")){

            String name = tokens.remove(0);


            if (tokens.get(0).equals("[")) {
                tokens.remove(0); //"["
                w.push(st.kind(name), st.index(name));
                expression(tokens);
                w.ari("+");

                w.pop("pointer",1);
                w.push("that",0);

                tokens.remove(0); //"]"

            } else {
                w.push(st.kind(name), st.index(name));
            }
        }

    }

    public void subroutinecallcompiler(ArrayList <String> tokens){

        String name;
        int i=0;
        if (tokens.get(1).equals(".")){
            String arg = tokens.remove(0);
            tokens.remove(0); //"."
            if (!st.kind(arg).equals("none")){

                w.push(st.kind(arg), st.index(arg));

                arg = st.type(arg);

                name = arg+"."+tokens.remove(0);


                i=1;
            } else {

                name = arg + "." + tokens.remove(0);

            }
        } else {

            name = cls + "." + tokens.remove(0);
            w.push("pointer", 0);
            i=1;
        }

            tokens.remove(0); //"("

            i = i+expressionList(tokens);

            tokens.remove(0); //")"

            w.call(name, i);

    }

    public int expressionList(ArrayList <String> tokens){
        int i = 0;
        String tok;

        while (!tokens.isEmpty()){

            tok = tokens.get(0);

            if (tok.equals(","))  tokens.remove(0);
            else {

                if (tok.equals(")"))  break;

                expression(tokens);

                i++;

            }
        }

      return i;
    }


}
