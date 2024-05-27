import java.util.Scanner;

// 1. для чисел в 10-й СС
// 1.1 в нормализованный вид
// 1.2 в денормализованный вид
// 2 перевод в 2-ую
// 3. перевод в 16-ую

public class Main
{
    // вывод числа в экспоненциальном виде в десятичной СС
    private static void exponentNum(String inNum)
    {
        boolean f = true;
        String sign = Double.parseDouble(inNum) >= 0 ? "" : "-";
        inNum = Double.toString(Math.abs(Double.parseDouble(inNum)));
        boolean isNumBiggerThanOne = Float.parseFloat(inNum) >= 1;
        String mantissa = "";

        int count_non_significant_symbols = 0;
        for (int i = 0; i < inNum.length(); i++)
        {
            if (inNum.charAt(i) == '0')
                count_non_significant_symbols++;
            else if (inNum.charAt(i) == '.') {
                count_non_significant_symbols++;
                f = false;
            }
            else
                break;
        }

        int start_index = count_non_significant_symbols;
        int count = count_non_significant_symbols > 0 ? count_non_significant_symbols - 1 : -1;
        for (int i = start_index; i < inNum.length(); i++)
        {
            if (inNum.charAt(i) != '.')
                mantissa += inNum.charAt(i);
            else
                f = false;

            if (f && isNumBiggerThanOne)
                count++;
            else if (f) {
                count--;
            }
        }

        if (isNumBiggerThanOne)
        {
            System.out.println("Normalized: " + sign + mantissa.charAt(0) + "." + mantissa.substring(1) + "E+" + count);
            System.out.println("Denormalized: " + sign + "0." + mantissa + "E+" + (count + 1));
        }
        else
        {
            System.out.println("Normalized: " + sign + mantissa.charAt(0) + "." + mantissa.substring(1) + "E-" + count);
            System.out.println("Denormalized: " + sign + "0." + mantissa + "E-" + (count - 1));
        }
    }

    // перевод дробной части в двоичную СС
    private static String fractBinary(double fract, int integer_len)
    {
        String fract_bin = "";
        int i = 0;
        while (Math.floor(fract) != fract)
        {
            // ограничение на размер дробной части (нужно чтобы мантисса была не больше 23 бит)
            if (i == (23 - integer_len))
                break;

            // когда мы переводили целые числа в двоичную СС то делили на 2
            // и брали остатки и записывали в обратном порядке
            // здесь же мы умножаем на 2 и берем целую часть от получившегося числа
            // до тех пор, пока у нас не получится число у которого дробная часть == .0
            fract_bin += (int) Math.floor(fract * 2) % 2;
            fract *= 2;
            i++;
        }

        if (fract_bin.isEmpty())
            fract_bin = "0";

        return fract_bin;
    }

    // нахождение экспоненты
    private static int findExponenta(String binary_num)
    {
        int count = -1;
        for (int i = 0; i < binary_num.length(); i++)
        {
            if (binary_num.charAt(i) != '.')
                count++;
            else
                return count;
        }
        return 0;
    }

    // метод конвертации в IEEE754(binary32)
    public static String to_IEEE754(double num)
    {
        // считываем знак числа и продолжаем вычисления уже для модуля числа
        String sign = num > 0 ? "0" : "1";
        num = Math.abs(num);

        int integer = (int) Math.floor(num); // целая часть числа
        int exp = 0;
        String moved_exp = "";
        double fractional = num - integer; // дробная часть числа

        String integer_bin = Integer.toBinaryString(integer); // целая часть числа в 2-ой сс
        String fract_bin = fractBinary(fractional, integer_bin.length()); // дробная часть числа в 2-ой сс

        String mantissa = integer_bin + fract_bin; // мантисса
        String binary_num = integer_bin + "." + fract_bin; // число в 2-ой сс

        exp = findExponenta(binary_num);
        moved_exp = Integer.toBinaryString(exp + 127);

        String binaryStr = sign + moved_exp + mantissa.substring(1); // само число в представлении IEEE754

        // если длина binaryStr меньше 32 -> дополняем нулями до 32 символов строку
        if (binaryStr.length() < 32)
        {
            for (int i = binaryStr.length(); i < 32; i++)
                binaryStr += "0";
        }

        System.out.println("Binary: "+ binaryStr);
        System.out.println("\t\t" + binary_num);
        return binaryStr;
    }

    public static void to_IEEE754_hex(String num)
    {
        System.out.println("Hexadecimal: " + toHexString(num));
    }

    private static String toHexString(String binaryStr)
    {
        String hexStr = "";
        // берём каждые 4 бита (т.к. значение идет от 0000 (0) до 1111 (15)) и переводим их в 16-ую систему
        for (int i = 0; i <= binaryStr.length() - 4; i+=4)
        {
            hexStr += bitsToHex(binaryStr.substring(i, i+4));
        }
        return hexStr;
    }

    private static String bitsToHex(String byteStr)
    {
        int decimal = Integer.parseInt(byteStr, 2); // переводим из 2-ой в 10-ю
        return Integer.toHexString(decimal); // возвращаем переведенное значение из 10-ой в 16-ю
    }

    public static void main(String[] args)
    {
        Scanner in = new Scanner(System.in);
        System.out.print("Enter num: ");
        String inNum = in.next(); // ввод числа

        exponentNum(inNum);
        String out = to_IEEE754(Double.parseDouble(inNum));
        to_IEEE754_hex(out);
    }
}
