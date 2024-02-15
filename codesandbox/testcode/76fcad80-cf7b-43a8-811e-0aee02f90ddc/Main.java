public class SleepError{
    public static void main(String[] args){
        int a = Integer.parseInt(args[0]);
        int b = Integer.parseInt(args[1]);
        Thread.sleep(10000L);
        System.out.println(a + b);
    }
}
