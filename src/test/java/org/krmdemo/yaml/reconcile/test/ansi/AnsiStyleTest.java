package org.krmdemo.yaml.reconcile.test.ansi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.krmdemo.yaml.reconcile.ansi.AnsiStyle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyle.empty;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyle.resetAll;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.APPLY_BOLD;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.APPLY_DIM;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.APPLY_ITALIC;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.Color.*;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.RESET_BOLD;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.RESET_DIM;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.RESET_ITALIC;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.bg;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.fg;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.lookupByName;

/**
 * Unit-test to check the functionality of {@link AnsiStyle} class
 */
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class AnsiStyleTest {

    @Test
    @DisplayName("AnsiStyle.empty()")
    void testStyleEmpty() {
        assertThat(empty().renderAnsi()).isEmpty();
        assertThat(empty().dump()).isEqualTo("ansi-style-empty");
    }

    @Test
    @DisplayName("AnsiStyle.resetAll()")
    void testStyleResetAll() {
        assertThat(resetAll().renderAnsi()).isEqualTo("\u001b[0m");;
        assertThat(resetAll().dump()).isEqualTo("ansi-style<!!>");
    }

    @Test
    @DisplayName("ANSI-codes of foreground Color-16 (regular names)")
    void testForegroundRegularColor16() {
        assertThat(fg(BLACK).ansiCodeSeq()).isEqualTo("30");
        assertThat(fg(RED).ansiCodeSeq()).isEqualTo("31");
        assertThat(fg(GREEN).ansiCodeSeq()).isEqualTo("32");
        assertThat(fg(YELLOW).ansiCodeSeq()).isEqualTo("33");
        assertThat(fg(BLUE).ansiCodeSeq()).isEqualTo("34");
        assertThat(fg(MAGENTA).ansiCodeSeq()).isEqualTo("35");
        assertThat(fg(CYAN).ansiCodeSeq()).isEqualTo("36");
        assertThat(fg(WHITE).ansiCodeSeq()).isEqualTo("37");
    }

    @Test
    @DisplayName("ANSI-codes of foreground Color-16 (bright names)")
    void testForegroundBrightColor16() {
        assertThat(fg(BRIGHT_BLACK).ansiCodeSeq()).isEqualTo("90");
        assertThat(fg(BRIGHT_RED).ansiCodeSeq()).isEqualTo("91");
        assertThat(fg(BRIGHT_GREEN).ansiCodeSeq()).isEqualTo("92");
        assertThat(fg(BRIGHT_YELLOW).ansiCodeSeq()).isEqualTo("93");
        assertThat(fg(BRIGHT_BLUE).ansiCodeSeq()).isEqualTo("94");
        assertThat(fg(BRIGHT_MAGENTA).ansiCodeSeq()).isEqualTo("95");
        assertThat(fg(BRIGHT_CYAN).ansiCodeSeq()).isEqualTo("96");
        assertThat(fg(BRIGHT_WHITE).ansiCodeSeq()).isEqualTo("97");
    }

    @Test
    @DisplayName("ANSI-codes of background Color-16 (regular names)")
    void testBackgroundRegularColor16() {
        assertThat(bg(BLACK).ansiCodeSeq()).isEqualTo("40");
        assertThat(bg(RED).ansiCodeSeq()).isEqualTo("41");
        assertThat(bg(GREEN).ansiCodeSeq()).isEqualTo("42");
        assertThat(bg(YELLOW).ansiCodeSeq()).isEqualTo("43");
        assertThat(bg(BLUE).ansiCodeSeq()).isEqualTo("44");
        assertThat(bg(MAGENTA).ansiCodeSeq()).isEqualTo("45");
        assertThat(bg(CYAN).ansiCodeSeq()).isEqualTo("46");
        assertThat(bg(WHITE).ansiCodeSeq()).isEqualTo("47");
    }

    @Test
    @DisplayName("ANSI-codes of background Color-16 (bright names)")
    void testBackgroundBrightColor16() {
        assertThat(bg(BRIGHT_BLACK).ansiCodeSeq()).isEqualTo("100");
        assertThat(bg(BRIGHT_RED).ansiCodeSeq()).isEqualTo("101");
        assertThat(bg(BRIGHT_GREEN).ansiCodeSeq()).isEqualTo("102");
        assertThat(bg(BRIGHT_YELLOW).ansiCodeSeq()).isEqualTo("103");
        assertThat(bg(BRIGHT_BLUE).ansiCodeSeq()).isEqualTo("104");
        assertThat(bg(BRIGHT_MAGENTA).ansiCodeSeq()).isEqualTo("105");
        assertThat(bg(BRIGHT_CYAN).ansiCodeSeq()).isEqualTo("106");
        assertThat(bg(BRIGHT_WHITE).ansiCodeSeq()).isEqualTo("107");
    }

    @Test
    @DisplayName("names of Color-16")
    void testNamesColor16() {
        assertThat(fg(RED).name()).isEqualTo("fg(red)");
        assertThat(fg(BRIGHT_GREEN).name()).isEqualTo("fg(^green)");
        assertThat(bg(YELLOW).name()).isEqualTo("bg(yellow)");
        assertThat(bg(BRIGHT_BLUE).name()).isEqualTo("bg(^blue)");
    }

    @Test
    @DisplayName("ANSI-codes and names of Color-256")
    void testColor256() {
        assertThat(fg(123).ansiCodeSeq()).isEqualTo("38;5;123");
        assertThat(bg(234).ansiCodeSeq()).isEqualTo("48;5;234");

        assertThat(fg(123).name()).isEqualTo("fg(#7B)");
        assertThat(bg(234).name()).isEqualTo("bg(#EA)");

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
    @DisplayName("ANSI-codes and names of Color-RGB")
    void testColorRGB() {
        assertThat(fg(12, 34, 56).ansiCodeSeq()).isEqualTo("38;2;12;34;56");
        assertThat(bg(23, 45, 67).ansiCodeSeq()).isEqualTo("48;2;23;45;67");

        assertThat(fg(12, 34, 56).name()).isEqualTo("fg(#0C2238)");
        assertThat(bg(23, 45, 67).name()).isEqualTo("bg(#172D43)");

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

    @Test
    @DisplayName("AnsiStyle.lookupByName()")
    void testLookupByName() {
        assertThat(lookupByName("la-la-la")).isNotPresent();

        assertThat(lookupByName("dim")).hasValue(APPLY_DIM);
        assertThat(lookupByName("italic")).hasValue(APPLY_ITALIC);

        assertThat(lookupByName("!bold")).hasValue(RESET_BOLD);
        assertThat(lookupByName("!dim")).hasValue(RESET_DIM);
    }

    @Test
    @DisplayName("AnsiStyle.builder()..build()")
    void testStyleBuilder() {
        AnsiStyle styleDirect = empty().builder()
            .accept(APPLY_BOLD)
            .accept(bg(123))
            .accept(fg(45))
            .accept(RESET_ITALIC)
            .build();
        assertThat(styleDirect.renderAnsi()).isEqualTo("\u001b[1;23;38;5;45;48;5;123m");
        assertThat(styleDirect.dump()).isEqualTo("ansi-style<bold,!italic,fg(#2D),bg(#7B)>");

        AnsiStyle styleLookup = empty().builder()
            .acceptByName("underline")
            .acceptByName("strikethrough")
            .build();
        assertThat(styleLookup.renderAnsi()).isEqualTo("\u001b[4;9m");
        assertThat(styleLookup.dump()).isEqualTo("ansi-style<underline,strikethrough>");
    }
}
