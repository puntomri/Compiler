import java.io.File;



public class Vmt{

    File file;
    Tokenizer tokenizer;
    Writer writer;


    Vmt(String name){


     file = new File(name+".jack");
     tokenizer = new Tokenizer(file,writer);

    }


    public static void main(String[] args){

        Vmt vmt = new Vmt("Main");
     // Vmt vmt2 = new Vmt("Bat");
      // Vmt vmt3 = new Vmt("Ball");
      //  Vmt vmt4 = new Vmt("PongGame");
    }
}
