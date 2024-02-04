package org.krmdemo.yaml.reconcile.test.ansi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.yaml.reconcile.ansi.AnsiText.ansiText;

public class AnsiTextErrorTest {

    @DisplayName("output '@|' and '|@'")
    @Test void testAtSymbolWithDoublePipe() {
        System.out.println("no ansi-styles:");
        System.out.println(ansiText(">>> @|| <<<").renderAnsi());
        System.out.println(ansiText(">>> ||@ <<<").renderAnsi());
        System.out.println(ansiText(">>> @||la-la-la <<<").renderAnsi());
        System.out.println(ansiText(">>> la-la-la||@ <<<").renderAnsi());
        System.out.println(ansiText(">>> @||la-la-la||@ <<<").renderAnsi());

        System.out.println("ansi-styles (some foreground colors):");
        System.out.println(ansiText(">>> @|red;@|||@ <<<").renderAnsi());
        System.out.println(ansiText(">>> @|blue;||@|@ <<<").renderAnsi());
        System.out.println(ansiText(">>> @|red;@||la-la-la|@ <<<").renderAnsi());
        System.out.println(ansiText(">>> @|blue;la-la-la||@|@ <<<").renderAnsi());
        System.out.println(ansiText(">>> @|magenta;@||la-la-la||@|@ <<<").renderAnsi());

        assertThat(ansiText(">>> @|| <<<").content()).isEqualTo(">>> @| <<<");
        assertThat(ansiText(">>> ||@ <<<").content()).isEqualTo(">>> |@ <<<");
        assertThat(ansiText(">>> @||la-la-la <<<").content()).isEqualTo(">>> @|la-la-la <<<");
        assertThat(ansiText(">>> la-la-la||@ <<<").content()).isEqualTo(">>> la-la-la|@ <<<");
        assertThat(ansiText(">>> @||la-la-la||@ <<<").content()).isEqualTo(">>> @|la-la-la|@ <<<");

        assertThat(ansiText(">>> @|red;@|||@ <<<").content()).isEqualTo(">>> @| <<<");
        assertThat(ansiText(">>> @|blue;||@|@ <<<").content()).isEqualTo(">>> |@ <<<");
        assertThat(ansiText(">>> @|red;@||la-la-la|@ <<<").content()).isEqualTo(">>> @|la-la-la <<<");
        assertThat(ansiText(">>> @|blue;la-la-la||@|@ <<<").content()).isEqualTo(">>> la-la-la|@ <<<");
        assertThat(ansiText(">>> @|magenta;@||la-la-la||@|@ <<<").content()).isEqualTo(">>> @|la-la-la|@ <<<");
    }
}
