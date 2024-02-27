package org.krmdemo.yaml.reconcile.test.echo;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class Solution {

    public boolean isNotPrime(long num) {
        return !isPrime(num);
    }

    /**
     * num = 100
     * i = 5
     * 100 / i = 20 < 10 = sqrt(100)
     *
     * j1, j2, j3 ..... < sqrt(100)
     * (100 / j1), (100 / j2), ....
     *
     * if "i" * "num / i" = "num"  (sqrt(num) + ...) * (sqrt(num) + ...) = num + ... > num
     *
     *
     * @param num
     * @return
     */
    public boolean isPrime(long num) {
        if (num <= 1) {
            return false;
        }
        if (num < 4 || num == 5) {
            return true;
        }
        long lastDigit = num % 10;
        if ((lastDigit % 2) == 0 || lastDigit == 5) {
            return false;
        }
        for (long i = 3; i < num; i += 2) {
            if (i * i > num) {
                // if "num" is divided by "k", it means "num" is divided by "num / k" - so, no need to check "num / k",
                break;
            }
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }

    @Test
    void test() {
        assertThat(Stream.of(2, 3, 5, 7, 11, 13,
//            17, 19, 23, 29, 31,
//            37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97,
            101).filter(this::isNotPrime).count()).isEqualTo(0);
    }
}
