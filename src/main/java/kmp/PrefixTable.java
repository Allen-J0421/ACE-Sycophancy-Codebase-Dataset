package kmp;

final class PrefixTable {

    private PrefixTable() {
    }

    static int[] build(CharSequence pattern) {
        int[] table = new int[pattern.length()];
        int prefixLength = 0;
        int index = 1;

        while (index < pattern.length()) {
            if (pattern.charAt(index) == pattern.charAt(prefixLength)) {
                table[index] = ++prefixLength;
                index++;
            } else if (prefixLength > 0) {
                prefixLength = table[prefixLength - 1];
            } else {
                table[index] = 0;
                index++;
            }
        }

        return table;
    }
}
