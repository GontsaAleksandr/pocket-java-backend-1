package ru.geekbrains.pocket.backend.util;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class RandomString {

    private static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String lower = upper.toLowerCase(Locale.ROOT);
    private static final String digits = "0123456789";
    private static final String alphanum = upper + lower + digits;
    private final Random random;
    private final char[] symbols;
    private final char[] buf;

    public RandomString(int length, Random random, String symbols) {
        if (length < 1) throw new IllegalArgumentException();
        if (symbols.length() < 2) throw new IllegalArgumentException();
        this.random = Objects.requireNonNull(random);
        this.symbols = symbols.toCharArray();
        this.buf = new char[length];
    }

    /**
     * Create an alphanumeric string generator.
     */
    public RandomString(int length, Random random) {
        this(length, random, alphanum);
    }

    /**
     * Create an alphanumeric strings from a secure generator.
     */
    public RandomString(int length) {
        this(length, new SecureRandom());
    }

    /**
     * Create session identifiers.
     */
    public RandomString() {
        this(21);
    }

    /**
     * Generate a random string.
     */
    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }

}

//http://qaru.site/questions/934/how-to-generate-a-random-alpha-numeric-string
// Примеры использования
//Создайте небезопасный генератор для 8-символьных идентификаторов:
//RandomString gen = new RandomString(8, ThreadLocalRandom.current());
//
//Создайте безопасный генератор для идентификаторов сессии:
//RandomString session = new RandomString();
//
//Создайте генератор с удобочитаемыми кодами для печати. Строки длиннее буквенно-цифровых строк, чтобы компенсировать использование меньшего количества символов:
//String easy = RandomString.digits + "ACEFGHJKLMNPQRUVWXYabcdefhijkprstuvwx";
//RandomString tickets = new RandomString(23, new SecureRandom(), easy);
