package org.krmdemo.yaml.reconcile.test.ansi;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.Color.*;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.bg;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.fg;

public class AnsiStyleTest {

    @Test
    void testColor16() {
        assertThat(fg(BLACK).ansiCodeStr()).isEqualTo("30");
        assertThat(fg(RED).ansiCodeStr()).isEqualTo("31");
        assertThat(fg(GREEN).ansiCodeStr()).isEqualTo("32");
        assertThat(fg(YELLOW).ansiCodeStr()).isEqualTo("33");
        assertThat(fg(BLUE).ansiCodeStr()).isEqualTo("34");
        assertThat(fg(MAGENTA).ansiCodeStr()).isEqualTo("35");
        assertThat(fg(CYAN).ansiCodeStr()).isEqualTo("36");
        assertThat(fg(WHITE).ansiCodeStr()).isEqualTo("37");

        assertThat(bg(BLACK).ansiCodeStr()).isEqualTo("40");
        assertThat(bg(RED).ansiCodeStr()).isEqualTo("41");
        assertThat(bg(GREEN).ansiCodeStr()).isEqualTo("42");
        assertThat(bg(YELLOW).ansiCodeStr()).isEqualTo("43");
        assertThat(bg(BLUE).ansiCodeStr()).isEqualTo("44");
        assertThat(bg(MAGENTA).ansiCodeStr()).isEqualTo("45");
        assertThat(bg(CYAN).ansiCodeStr()).isEqualTo("46");
        assertThat(bg(WHITE).ansiCodeStr()).isEqualTo("47");

        assertThat(fg(BRIGHT_BLACK).ansiCodeStr()).isEqualTo("90");
        assertThat(fg(BRIGHT_RED).ansiCodeStr()).isEqualTo("91");
        assertThat(fg(BRIGHT_GREEN).ansiCodeStr()).isEqualTo("92");
        assertThat(fg(BRIGHT_YELLOW).ansiCodeStr()).isEqualTo("93");
        assertThat(fg(BRIGHT_BLUE).ansiCodeStr()).isEqualTo("94");
        assertThat(fg(BRIGHT_MAGENTA).ansiCodeStr()).isEqualTo("95");
        assertThat(fg(BRIGHT_CYAN).ansiCodeStr()).isEqualTo("96");
        assertThat(fg(BRIGHT_WHITE).ansiCodeStr()).isEqualTo("97");

        assertThat(bg(BRIGHT_BLACK).ansiCodeStr()).isEqualTo("100");
        assertThat(bg(BRIGHT_RED).ansiCodeStr()).isEqualTo("101");
        assertThat(bg(BRIGHT_GREEN).ansiCodeStr()).isEqualTo("102");
        assertThat(bg(BRIGHT_YELLOW).ansiCodeStr()).isEqualTo("103");
        assertThat(bg(BRIGHT_BLUE).ansiCodeStr()).isEqualTo("104");
        assertThat(bg(BRIGHT_MAGENTA).ansiCodeStr()).isEqualTo("105");
        assertThat(bg(BRIGHT_CYAN).ansiCodeStr()).isEqualTo("106");
        assertThat(bg(BRIGHT_WHITE).ansiCodeStr()).isEqualTo("107");

        assertThat(fg(RED).dump()).isEqualTo("fg(red)");
        assertThat(fg(BRIGHT_GREEN).dump()).isEqualTo("fg(^green)");
        assertThat(bg(YELLOW).dump()).isEqualTo("bg(yellow)");
        assertThat(bg(BRIGHT_BLUE).dump()).isEqualTo("bg(^blue)");
    }

    @Test
    void testColor256() {
        assertThat(fg(123).ansiCodeStr()).isEqualTo("38;5;123");
        assertThat(bg(234).ansiCodeStr()).isEqualTo("48;5;234");
        assertThat(fg(345).ansiCodeStr()).isEmpty();
        assertThat(bg(456).ansiCodeStr()).isEmpty();

        assertThat(fg(123).dump()).isEqualTo("fg(#7B)");
        assertThat(bg(234).dump()).isEqualTo("bg(#EA)");
        assertThat(fg(345).dump()).isEqualTo("??\"fg(?'#159~345')\"");
        assertThat(bg(456).dump()).isEqualTo("??\"bg(?'#1C8~456')\"");
    }

    @Test
    void testColorRGB() {
        assertThat(fg(12, 34, 56).ansiCodeStr()).isEqualTo("38;2;12;34;56");
        assertThat(bg(23, 45, 67).ansiCodeStr()).isEqualTo("48;2;23;45;67");

        assertThat(fg(12, 34, 56).dump()).isEqualTo("fg(#0C2238)");
        assertThat(bg(23, 45, 67).dump()).isEqualTo("bg(#172D43)");

        assertThat(fg(-2, 34, 56).ansiCodeStr()).isEmpty();
        assertThat(fg(-3, 534, 56).ansiCodeStr()).isEmpty();
        assertThat(fg(444, 134, 256).ansiCodeStr()).isEmpty();

        assertThat(fg(-2, 34, 56).dump()).isEqualTo("??\"fg(#?'FFFFFFFE~-2'2238)\"");
        assertThat(fg(-3, 534, 56).dump()).isEqualTo("??\"fg(#?'FFFFFFFD~-3'?'216~534'38)\"");
        assertThat(fg(444, 134, 256).dump()).isEqualTo("??\"fg(#?'1BC~444'86?'100~256')\"");
    }
}
