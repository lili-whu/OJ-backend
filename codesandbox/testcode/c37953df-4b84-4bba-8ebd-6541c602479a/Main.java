// SleepError

public class Main{
    public static void main(String[] args){
        sleepError(args);
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
}
