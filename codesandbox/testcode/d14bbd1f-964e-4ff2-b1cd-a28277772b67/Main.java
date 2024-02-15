// SleepError

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;

public class Main{
    public static void main(String[] args){
        blackCodeError(args);
    }


    public static void sleepError(String[] args){
        int a = Integer.parseInt(args[0]);
        int b = Integer.parseInt(args[1]);
        try{

            Thread.sleep(10000L);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(a + b);
    }
    public static void memoryError(String[] args){
        List<byte[]> list = new ArrayList<>();
        while(true){
            list.add(new byte[10000]);
        }
    }

    public static void blackCodeError(String[] args){
        System.out.println(Files.readAllLines("/Users/lili/Desktop/OJ-coding/OJ-backend/codesandbox/src/main/resources/application.yml"));
    }

    public static void ok(String[] args){
    }

}
