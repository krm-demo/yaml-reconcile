#!/usr/bin/java --source 21

import static java.util.Arrays.stream;

public class Hello {
    public static void main(String[] args) {
        System.out.println("Hello, from Java as a bash-script! Passed arguments are:");
        stream(args).forEach(System.out::println);
    }
}

