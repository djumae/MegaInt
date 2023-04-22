/**
 * @author Daniel Jiang
 * @version April 15, 2023
 */
public final class MegaInt {
    private final boolean isNegative;
    private final byte[] digits;

    /**
     *
     * @param digitStr a string that stores the number that is to be converted to a MegaInt object
     */
    public MegaInt(String digitStr){
        boolean isNegative1;
        if ((digitStr == null) || (digitStr.equals('-')) || (!digitStr.matches(".*\\d.*")) || (digitStr.equals(""))){ // if input string is null, is empty, only has negative sign or doesnt have numbers
            throw new IllegalArgumentException("enter a string with an actual integer");
        }
        else if(!digitStr.matches("-?[0-9]+")){ // if input string has anything other than numbers
            throw new IllegalArgumentException("input string contains invalid characters");
        }
        isNegative1 = digitStr.charAt(0) == '-'; // if negative sign is present, store negation
        if (isNegative1) {
            digitStr = digitStr.substring(1); // exclude negative symbol in order to fit digits in byte[] array
        }
        digitStr = digitStr.replaceFirst("^0+(?!$)", ""); //remove leading zeros
        digits = new byte[digitStr.length()]; //store number as digits in byte[] array, from most to least significant
        for(int i = digitStr.length()-1; i>=0;i--){
            for(int j= 0; j<digitStr.length(); j++){
                if(i==j){
                    digits[j] = (byte) Character.getNumericValue(digitStr.charAt(i));
                }
            }
        }
        //if the result is 0, set isNegative to false since 0 is neither positive nor negative
        boolean isZero = true;
        for (int digit : digits){
            if (digit != 0){
                isZero = false;
                break;
            }
        }
        if (isZero) {
            isNegative1 = false;
        }

        isNegative = isNegative1; // deferring assignment until the end to avoid compiler complaint
    }

    /**
     *
     * @param inDigits an array that is already in the format of which a MegaInt object would appear
     * @param isNeg determines whether the number is negative or not
     */
    public MegaInt(byte[] inDigits, boolean isNeg){
        if((inDigits == null) || (inDigits.length == 0)){ //if the array argument is null or is empty
            throw new IllegalArgumentException("input array cannot be null or empty");
        }
        for (byte digit : inDigits){
            if (digit > 9 || digit < 0){ //if a digit is not a number
                throw new IllegalArgumentException("input array contains invalid digits");
            }
        }
        // stores the amount of zeros to be removed later
        int zeroCount = 0;
        for (int i = inDigits.length - 1; i >=1; i--){
            if (inDigits[i] == 0){
                zeroCount ++;
            }
            else{
                break;
            }
        }
        //if there is more than one zero, create a trimmed array
        if (zeroCount > 0){
            byte[] newDigits = new byte[inDigits.length - zeroCount]; // exclude zeros by their positions
            System.arraycopy(inDigits, 0, newDigits, 0, newDigits.length);
            inDigits = newDigits;
            if (inDigits.length == 0) {
                inDigits = new byte[] {0};
                isNeg = false;
            }
        }
        isNegative = isNeg;
        digits = new byte[inDigits.length];
        for (int i = inDigits.length - 1, j= 0; i >= 0; i--, j++){
            digits[j] = inDigits[i];
        }
    }

    /**
     * overrides Object's toString method to parse a MegaInt's number into a string
     * @return string form of MegaInt's number
     */
    public String toString(){
        //cannot directly parse byte into String, StringBuilder solves that
        StringBuilder megaIntString = new StringBuilder();
        int i = digits.length -1;
        //exclude zeros using index
        while (i >= 1 && digits[i] == 0){
            i--;
        }
        megaIntString.append(digits[i]);
        for (int j= i-1; j>=0; j--){
            megaIntString.append(digits[j]);
        }
        return megaIntString.toString();
    }

    /**
     *
     * @param other another MegaInt object for comparison
     * @return the number that determines whether the two MegaInt objects are equal. -1 means this object is less than other, vice versa for 1, and 0 if they are equal.
     */
    public int compareAbs(MegaInt other){
        if(other == null){
            throw new IllegalArgumentException("argument cannot be null");
        }
        int index = digits.length -1;
        while(digits[index] == 0 && index >=0){
            index--; // skips all leading zeros
        }
        int otherIndex = other.digits.length -1;
        while(otherIndex >= 0 && other.digits[otherIndex] ==0){
            otherIndex--; //skip all leading zeros;
        }
        if (index < otherIndex){
            return -1;
        }
        else if(index > otherIndex){
            return 1;
        }
        else{
            while(index>=0){
                byte digit = digits[index]; //extract each digit to compare
                byte otherDigit = other.digits[index]; // same extraction
                if(digit > otherDigit){
                    return 1;
                }
                else if (digit < otherDigit){
                    return -1;
                }
                index--;
            }
            return 0; //if it didnt return by now, they are equal;
        }

    }

    /**
     *
     * @param other added with this MegaInt object to get sum
     * @return  sum of the addition of two MegaInt objects
     */
    public MegaInt add(MegaInt other){
        byte[] sum = new byte[Math.max(other.digits.length, digits.length) +1];
        int i = digits.length -1;
        int j = other.digits.length -1;
        int index = sum.length-1;
        int carry = 0;

        if(this.digits.length == 0 || this.digits == null){
            throw new IllegalArgumentException("this MegaInt object does not have a valid number");
        }
        if(other.digits.length == 0 || other.digits== null){
            throw new IllegalArgumentException("the other MegaInt object does not have a valid number");
        }

        if (this.isNegative != other.isNegative) {
            MegaInt negativeNumber;
            MegaInt positiveNumber;
            if (this.isNegative) {
                negativeNumber = new MegaInt(this.digits, false);
                positiveNumber = new MegaInt(other.digits, false);
            } else {
                negativeNumber = new MegaInt(other.digits, false);
                positiveNumber = new MegaInt(this.digits, false);
            }
            return positiveNumber.subtract(negativeNumber);
        }

        while(i >=0 || j>=0){
            if(i>=0){
                carry += digits[i];
            }
            if(j >=0){
                carry +=other.digits[j];
            }
            sum[index] = (byte)(carry%10);
            carry /= 10;
            i--;
            j--;
            index--;
        }
        if(carry == 1){
            sum[0] =1;
        }
        else{
            byte[] trimSum = new byte[sum.length -1]; //sounded like dim sum, removes empty space of array
            System.arraycopy(sum, 1, trimSum, 0, trimSum.length);
        }
        return new MegaInt(sum, isNegative);
    }

    /**
     *
     * @param other to be subtracted from this MegaInt object to get difference
     * @return difference between the two MegaInt objects.
     */
    public MegaInt subtract(MegaInt other){
        if(this.digits.length == 0 || this.digits == null){
            throw new IllegalArgumentException("this MegaInt object does not have a valid number");
        }
        if(other.digits.length == 0 || other.digits== null){
            throw new IllegalArgumentException("the other MegaInt object does not have a valid number");
        }
        // determine the sign of the result
        boolean isDifferenceNegative = false;
        if (isNegative != other.isNegative) {
            // if the numbers have different sign , take the sign of larger number
            isDifferenceNegative = (digits.length < other.digits.length || (digits.length == other.digits.length && digits[digits.length - 1] < other.digits[other.digits.length - 1]));
        }
        else {
            // if both numbers have the same sign, subtract their magnitudes and keep the sign
            isDifferenceNegative = isNegative;
        }

        MegaInt a = abs(this);
        MegaInt b = abs(other);

        // compare the magnitudes of the numbers
        if ((a.digits.length == b.digits.length && a.digits[a.digits.length - 1] < b.digits[b.digits.length - 1]) || (a.digits.length < b.digits.length )) {
            // swap places so that a is always larger than b
            MegaInt temp = a;
            a = b;
            b = temp;
            isDifferenceNegative = !isDifferenceNegative; // flip the sign of the result

        }
        // subtract the two magnitudes, one digit at a time
        byte[] result = new byte[digits.length];
        int borrow = 0;
        for (int i = 0; i < a.digits.length; i++) {
            int diff = a.digits[i] - borrow;
            if (i < b.digits.length) {
                diff -= b.digits[i];
            }
            if (diff < 0) {
                diff += 10;
                borrow = 1;
            } else {
                borrow = 0;
            }
            result[i] = (byte) diff;
        }


        //return the result
        return new MegaInt(result, isDifferenceNegative);
    }

    /**
     *
     * @param other is multiplied with this object and returns a product
     * @return product of the two object's numbers
     */
    public MegaInt multiply(MegaInt other){
        boolean isProductNegative;
        // checks if both objects are valid
        if(this.digits.length == 0 || this.digits == null){
            throw new IllegalArgumentException("this MegaInt object does not have a valid number");
        }
        if(other.digits.length == 0 || other.digits== null){
            throw new IllegalArgumentException("the other MegaInt object does not have a valid number");
        }
        // if either number is negative, product will be negative
        MegaInt absOfThis = abs(this);
        MegaInt absOfOther = abs(other);
        isProductNegative = (this.isNegative != other.isNegative);

        byte[] result = new byte[absOfThis.digits.length + absOfOther.digits.length];
        //multiply digits, one place value at a time, from right to left
        for (int i = absOfThis.digits.length - 1; i >= 0; i--) {
            for (int j = absOfOther.digits.length - 1; j >= 0; j--) {
                int digit1 = absOfThis.digits[i];
                int digit2 = absOfOther.digits[j];
                int product = digit1 * digit2;
                int index1 = result.length - (i + j) - 1;
                int index2 = index1 - 1;
                int sum = product + result[index2];
                result[index1] += sum / 10;
                result[index2] = (byte) (sum % 10);
            }
        }

        int i = result.length - 1;
        while(i > 0 && result[i] == 0){
            i--;
        }

        byte[] trimProduct = new byte[i + 1];
        System.arraycopy(result, 0, trimProduct, 0, trimProduct.length);

        return new MegaInt(trimProduct, isProductNegative);
    }

    /**
     *
     * @param input the MegaInt object
     * @return the absolute value of the number that the MegaInt object holds
     */
    private MegaInt abs(MegaInt input) {
        MegaInt temp = new MegaInt(input.digits, input.isNegative);
        return new MegaInt(temp.digits, false);

    }


}
