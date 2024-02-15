// SleepError

import java.io.File;
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
        File file = new File(System.getProperty("user.dir"));
        System.out.println(Arrays.stream(file.list()).forEach(System.out::println);
    }

    public static void ok(String[] args){
    }

}
