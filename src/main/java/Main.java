import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        /*
        Программа распаковывания строки. На вход поступает строка вида число[строка],
        на выход - строка, содержащая повторяющиеся подстроки.

        Пример:
        Вход: 3[xyz]4[xy]z
        Выход: xyzxyzxyzxyxyxyxyz

        Ограничения:
        - одно повторение может содержать другое. Например: 2[3[x]y]  = xxxyxxxy
        - допустимые символы на вход: латинские буквы, числа и скобки []
        - числа означают только число повторений
        - скобки только для обозначения повторяющихся подстрок
         */
        for (;;) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Введите строку: например, qwe3[qq]r2[ty2[u2[ii]u]x]");
            String input = scanner.nextLine();

            try {
                if (isStringValid(input)) {
                    StringBuffer result = unpackBrackets(input);
                    System.out.println("Распакованная строка: \n" + result.toString());
                } else {
                    System.out.println("Введите, пожалуйста, строку с правильным расположением кавычек [ и ]" +
                            "\n и целым числом перед кавычкой [" +
                            "\n и с использованием латинского алфавита");
                }
            } catch (Exception ex) {
//                ex.printStackTrace();
                System.out.println("Введите, пожалуйста, строку корректно. " +
                        "\nЧисла целые и указываются только перед \"[\"");
            }
        }

    }

    public static StringBuffer unpackBrackets(String input) {
        char[] charArr = input.toCharArray();
        StringBuffer result = new StringBuffer();
        // массивы с индексами левых и правых скобок
        ArrayList<Integer> leftBracketsIndexes = new ArrayList<>();
        ArrayList<Integer> rightBracketsIndexes = new ArrayList<>();
        ArrayList<ArrayList<Integer>> borderBracketsIndexes = new ArrayList<>();
        int currentIndexBrackets = 0;
        for (int i = 0; i < charArr.length; i++) {
            if (charArr[i] == '[') {
                leftBracketsIndexes.add(i);
            } else if (charArr[i] == ']') {
                rightBracketsIndexes.add(i);
                // расчет числа парных скобок на текущем уровне и запись в массив их индексов
                if (leftBracketsIndexes.size() == rightBracketsIndexes.size()) {
                    int finalCurrentIndexBrackets = currentIndexBrackets;
                    ArrayList<Integer> couple = new ArrayList<>()
                    {{
                        add(leftBracketsIndexes.get(finalCurrentIndexBrackets));
                        add(rightBracketsIndexes.get(rightBracketsIndexes.size() - 1));
                    }};
                    borderBracketsIndexes.add(couple);
                    currentIndexBrackets++;
                }
            }
        }
        // цикл перебора скобок текущего уровня и парсинг текста перед скобками
        if (borderBracketsIndexes.size() != 0) {
            for (int i = 0; i < borderBracketsIndexes.size(); i++) {
                // парсинг текста перед скобкой [
                String before = "";
                if (i == 0) {
                    before = input.substring(0, borderBracketsIndexes.get(i).get(0));
                } else {
                    before = input.substring(
                        borderBracketsIndexes.get(i - 1).get(1) + 1,
                        borderBracketsIndexes.get(i).get(0));
                }
                // парсинг строки перед скобкой [
                ArrayList<String> parseBefore = getParsingBefore(before);
                // добавдение текста перед скобкой в финальную строку
                result.append(parseBefore.get(0));
                // j - это число повторов скобки при ее раскрытии
                for (int j = 1; j <= Integer.parseInt(parseBefore.get(1)); j++) {
                    String intoBrackets = input.substring(
                            borderBracketsIndexes.get(i).get(0) + 1,
                            borderBracketsIndexes.get(i).get(1));
                    // рекурсия проверки вложенной строки во вложенных скобках (если есть)
                    StringBuffer parseIntoBrackets = unpackBrackets(intoBrackets);
                    result.append(parseIntoBrackets);
                }
            }
            // парсинг текста перед последней скобкой "]"
            String lastText = input.substring(borderBracketsIndexes.get(borderBracketsIndexes.size() - 1).get(1) + 1);
            result.append(lastText);
        } else {
            result.append(input);
        }
        return result;
    }

    public static boolean isStringValid(String input) {
        // проверка на валидность использования скобок "[" и "]", числ перед ними и использования латинского алфавита
        char[] charArr = input.toCharArray();
        ArrayList<Integer> leftBracketsIndexes = new ArrayList<>();
        ArrayList<Integer> rightBracketsIndexes = new ArrayList<>();
        for (int i = 0; i < charArr.length; i++) {
            if (charArr[i] == '[' && i != 0) {
                leftBracketsIndexes.add(i);
                Pattern pattern = Pattern.compile("[0-9]");
                Matcher matcher = pattern.matcher(String.valueOf(charArr[i - 1]));
                if (!matcher.matches()) {
                    return false;
                }
            } else if (charArr[i] == ']') {
                rightBracketsIndexes.add(i);
            } else {
                Pattern patternInput = Pattern.compile("[a-zA-Z0-9]");
                Matcher matcherInput = patternInput.matcher(String.valueOf(charArr[i]));
                if (!matcherInput.matches()) {
                    return false;
                }
            }
        }
        return leftBracketsIndexes.size() == rightBracketsIndexes.size();
    }

    public static ArrayList<String> getParsingBefore(String before) {
        ArrayList<String> result = new ArrayList<>();
        // парсинг числа перед скобкой "[" (число повторений значений внутри скобки)
        Pattern pattern = Pattern.compile("[0-9]*[0-9]+");
        Matcher matcher = pattern.matcher(before);
        if (matcher.find()) {
            // текстовая часть
            result.add(before.substring(0, matcher.start()));
            // цифровая часть
            result.add(before.substring(matcher.start()));
        }
        return result;
    }
}
