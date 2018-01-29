import java.io.IOException;
import java.io.PrintWriter;

public class Writer {
    int labelcounter;

    PrintWriter writer;

    Writer(String f){
        labelcounter=0;
        try{
            writer = new PrintWriter(f, "UTF-8");


        } catch (IOException e){
            System.out.println("problem");
        }

    }

    public void push(String s, int i){
        writer.println("push "+s+" "+i);
    }
    public void pop(String s, int i){
        writer.println("pop "+s+" "+i);
    }

    public void ari(String s){
        if (s.equals("+"))writer.println("add");
        if (s.equals("-"))writer.println("sub");
        if (s.equals("neg"))writer.println("neg");
        if (s.equals("="))writer.println("eq");
        if (s.equals(">"))writer.println("gt");
        if (s.equals("<"))writer.println("lt");
        if (s.equals("&"))writer.println("and");
        if (s.equals("|"))writer.println("or");
        if (s.equals("~"))writer.println("not");
        if (s.equals("*"))call("Math.multiply",2);
        if (s.equals("/"))call("Math.divide",2);
    }

    public void label (String s,int c){
        writer.println("label "+s+c);

    }

    public void gotol (String s,int c){
        writer.println("goto "+s+c);

    }
    public void ifl (String s,int c){
        writer.println("if-goto "+s+c);

    }
    public void call (String s,int i){
        writer.println("call "+s+" " +i);

    }
    public void func (String s,int i){
        writer.println("function "+s+" " +i);

    }
    public void returnl (){
        writer.println("return");

    }
    public void close(){

        writer.close();
    }




}
