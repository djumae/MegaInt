public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        MegaInt test = new MegaInt("000123");
        byte[] testArray = {3,4,1};
        boolean testNegative = false;
        MegaInt test2 = new MegaInt(testArray, testNegative);
        MegaInt num1 = new MegaInt("230");
        MegaInt num2 = new MegaInt("450");
        MegaInt result = num1.multiply(num2);
        System.out.println(result);


    }
}