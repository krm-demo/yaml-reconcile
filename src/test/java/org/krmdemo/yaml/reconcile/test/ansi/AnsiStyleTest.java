package org.krmdemo.yaml.reconcile.test.ansi;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.Color.*;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.bg;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.fg;

public class AnsiStyleTest {

    @Test
    void testColor16() {
        assertThat(fg(BLACK).ansiCodeSeq()).isEqualTo("30");
        assertThat(fg(RED).ansiCodeSeq()).isEqualTo("31");
        assertThat(fg(GREEN).ansiCodeSeq()).isEqualTo("32");
        assertThat(fg(YELLOW).ansiCodeSeq()).isEqualTo("33");
        assertThat(fg(BLUE).ansiCodeSeq()).isEqualTo("34");
        assertThat(fg(MAGENTA).ansiCodeSeq()).isEqualTo("35");
        assertThat(fg(CYAN).ansiCodeSeq()).isEqualTo("36");
        assertThat(fg(WHITE).ansiCodeSeq()).isEqualTo("37");

        assertThat(bg(BLACK).ansiCodeSeq()).isEqualTo("40");
        assertThat(bg(RED).ansiCodeSeq()).isEqualTo("41");
        assertThat(bg(GREEN).ansiCodeSeq()).isEqualTo("42");
        assertThat(bg(YELLOW).ansiCodeSeq()).isEqualTo("43");
        assertThat(bg(BLUE).ansiCodeSeq()).isEqualTo("44");
        assertThat(bg(MAGENTA).ansiCodeSeq()).isEqualTo("45");
        assertThat(bg(CYAN).ansiCodeSeq()).isEqualTo("46");
        assertThat(bg(WHITE).ansiCodeSeq()).isEqualTo("47");

        assertThat(fg(BRIGHT_BLACK).ansiCodeSeq()).isEqualTo("90");
        assertThat(fg(BRIGHT_RED).ansiCodeSeq()).isEqualTo("91");
        assertThat(fg(BRIGHT_GREEN).ansiCodeSeq()).isEqualTo("92");
        assertThat(fg(BRIGHT_YELLOW).ansiCodeSeq()).isEqualTo("93");
        assertThat(fg(BRIGHT_BLUE).ansiCodeSeq()).isEqualTo("94");
        assertThat(fg(BRIGHT_MAGENTA).ansiCodeSeq()).isEqualTo("95");
        assertThat(fg(BRIGHT_CYAN).ansiCodeSeq()).isEqualTo("96");
        assertThat(fg(BRIGHT_WHITE).ansiCodeSeq()).isEqualTo("97");

        assertThat(bg(BRIGHT_BLACK).ansiCodeSeq()).isEqualTo("100");
        assertThat(bg(BRIGHT_RED).ansiCodeSeq()).isEqualTo("101");
        assertThat(bg(BRIGHT_GREEN).ansiCodeSeq()).isEqualTo("102");
        assertThat(bg(BRIGHT_YELLOW).ansiCodeSeq()).isEqualTo("103");
        assertThat(bg(BRIGHT_BLUE).ansiCodeSeq()).isEqualTo("104");
        assertThat(bg(BRIGHT_MAGENTA).ansiCodeSeq()).isEqualTo("105");
        assertThat(bg(BRIGHT_CYAN).ansiCodeSeq()).isEqualTo("106");
        assertThat(bg(BRIGHT_WHITE).ansiCodeSeq()).isEqualTo("107");

        assertThat(fg(RED).dump()).isEqualTo("fg(red)");
        assertThat(fg(BRIGHT_GREEN).dump()).isEqualTo("fg(^green)");
        assertThat(bg(YELLOW).dump()).isEqualTo("bg(yellow)");
        assertThat(bg(BRIGHT_BLUE).dump()).isEqualTo("bg(^blue)");
    }

    @Test
    void testColor256() {
        assertThat(fg(123).ansiCodeSeq()).isEqualTo("38;5;123");
        assertThat(bg(234).ansiCodeSeq()).isEqualTo("48;5;234");

        assertThat(fg(123).dump()).isEqualTo("fg(#7B)");
        assertThat(bg(234).dump()).isEqualTo("bg(#EA)");

        assertThatIllegalArgumentException()
            .isThrownBy(() -> fg(345))
            .withMessageContaining("foreground 256-color")
            .withMessageContaining("was 345");
        assertThatIllegalArgumentException()
            .isThrownBy(() -> bg(456))
            .withMessageContaining("background 256-color")
            .withMessageContaining("was 456");
    }

    @Test
    void testColorRGB() {
        assertThat(fg(12, 34, 56).ansiCodeSeq()).isEqualTo("38;2;12;34;56");
        assertThat(bg(23, 45, 67).ansiCodeSeq()).isEqualTo("48;2;23;45;67");

        assertThat(fg(12, 34, 56).dump()).isEqualTo("fg(#0C2238)");
        assertThat(bg(23, 45, 67).dump()).isEqualTo("bg(#172D43)");

        assertThatIllegalArgumentException()
            .isThrownBy(() -> fg(-2, 34, 56))
            .withMessageContaining("Red part of foreground true-color")
            .withMessageContaining("was -2");
        assertThatIllegalArgumentException()
            .isThrownBy(() -> fg(12, 534, 56))
            .withMessageContaining("Green part of foreground true-color")
            .withMessageContaining("was 534");
        assertThatIllegalArgumentException()
            .isThrownBy(() -> fg(12, 34, 0x123))
            .withMessageContaining("Blue part of foreground true-color")
            .withMessageContaining("was 291");
    }
}
