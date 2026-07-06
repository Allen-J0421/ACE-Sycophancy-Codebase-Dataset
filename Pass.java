import java.util.function.ToIntFunction;

class Pass<T> {

    private final ToIntFunction<T> keyOf;
    private final DigitExtractor digits;
    private final int index;

    Pass(ToIntFunction<T> keyOf, DigitExtractor digits, int index) {
        this.keyOf  = keyOf;
        this.digits = digits;
        this.index  = index;
    }

    int base()         { return digits.base(); }
    int bucket(T item) { return digits.digit(keyOf.applyAsInt(item), index); }
}
