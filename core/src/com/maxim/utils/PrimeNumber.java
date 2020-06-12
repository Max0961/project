package com.maxim.utils;

public final class PrimeNumber {
    private static void init(boolean[] array) {
        for (int i = 2; i < array.length; i++)
            array[i] = true;
    }

    public static boolean[] getFlags(int max) {
        boolean[] flags = new boolean[max + 1];
        double sqrt = Math.sqrt(max);
        init(flags);
        int prime = 2;

        while (prime <= sqrt) {
            for (int i = prime * prime; i < flags.length; i += prime)
                flags[i] = false;

            prime = getNextPime(flags, prime);
        }

        return flags;
    }

    private static int getNextPime(boolean[] flags, int prime) {
        int next = prime + 1;
        while (next < flags.length && !flags[next]) {
            next++;
        }
        return next;
    }
}
