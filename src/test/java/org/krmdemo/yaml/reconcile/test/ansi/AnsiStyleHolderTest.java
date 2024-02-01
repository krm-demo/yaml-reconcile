package org.krmdemo.yaml.reconcile.test.ansi;

import org.junit.jupiter.api.Test;
import org.krmdemo.yaml.reconcile.ansi.AnsiStyle;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyle.empty;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.APPLY_BOLD;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.APPLY_UNDERLINE;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.RESET_BOLD;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.RESET_UNDERLINE;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.bg;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.fg;

/**
 * Unit-Test to verify the possible holders of ansi-styles (their hierarchy)
 */
public class AnsiStyleHolderTest {

    @Test
    void testStylesAreNull() {
        initStyleNull = true;
        TestStyleHolder holder_1 = new TestStyleHolder(null);
        TestStyleHolder holder_1_1 = new TestStyleHolder(holder_1);
        TestStyleHolder holder_1_2 = new TestStyleHolder(holder_1);
        TestStyleHolder holder_1_3 = new TestStyleHolder(holder_1);
        TestStyleHolder holder_2 = new TestStyleHolder(null);
        TestStyleHolder holder_2_1 = new TestStyleHolder(holder_2);
        TestStyleHolder holder_2_2 = new TestStyleHolder(holder_2);
        TestStyleHolder holder_2_2_1 = new TestStyleHolder(holder_2_2);
        TestStyleHolder holder_2_2_2 = new TestStyleHolder(holder_2_2);
        TestStyleHolder holder_2_2_1_0 = new TestStyleHolder(holder_2_2_1);

        assertThat(holder_1.style).isNull();
        assertThat(holder_1_1.style).isNull();
        assertThat(holder_1_2.style).isNull();
        assertThat(holder_1_3.style).isNull();
        assertThat(holder_2.style).isNull();
        assertThat(holder_2_1.style).isNull();
        assertThat(holder_2_2.style).isNull();
        assertThat(holder_2_2_1.style).isNull();
        assertThat(holder_2_2_2.style).isNull();
        assertThat(holder_2_2_1_0.style).isNull();

        assertThat(holder_1.styleChain().toList()).isEmpty();
        assertThat(holder_1_1.styleChain().toList()).isEmpty();
        assertThat(holder_1_2.styleChain().toList()).isEmpty();
        assertThat(holder_1_3.styleChain().toList()).isEmpty();
        assertThat(holder_2.styleChain().toList()).isEmpty();
        assertThat(holder_2_1.styleChain().toList()).isEmpty();
        assertThat(holder_2_2.styleChain().toList()).isEmpty();
        assertThat(holder_2_2_1.styleChain().toList()).isEmpty();
        assertThat(holder_2_2_2.styleChain().toList()).isEmpty();
        assertThat(holder_2_2_1_0.styleChain().toList()).isEmpty();

        assertThat(holder_1.parent).isNull();
        assertThat(holder_2.parent).isNull();

        assertThat(holder_1_1.parent).isSameAs(holder_1);
        assertThat(holder_1_2.parent).isSameAs(holder_1);
        assertThat(holder_1_3.parent).isSameAs(holder_1);

        assertThat(holder_2_1.parent).isSameAs(holder_2);
        assertThat(holder_2_2.parent).isSameAs(holder_2);
    }

    @Test
    void testStylesAreEmpty() {
        initStyleNull = false;
        TestStyleHolder holder_1 = new TestStyleHolder(null);
        TestStyleHolder holder_1_1 = new TestStyleHolder(holder_1);
        TestStyleHolder holder_1_2 = new TestStyleHolder(holder_1);
        TestStyleHolder holder_1_3 = new TestStyleHolder(holder_1);
        TestStyleHolder holder_2 = new TestStyleHolder(null);
        TestStyleHolder holder_2_1 = new TestStyleHolder(holder_2);
        TestStyleHolder holder_2_2 = new TestStyleHolder(holder_2);
        TestStyleHolder holder_2_2_1 = new TestStyleHolder(holder_2_2);
        TestStyleHolder holder_2_2_2 = new TestStyleHolder(holder_2_2);
        TestStyleHolder holder_2_2_1_0 = new TestStyleHolder(holder_2_2_1);

        assertThat(holder_1.style).isEqualTo(empty());
        assertThat(holder_1_1.style).isEqualTo(empty());
        assertThat(holder_1_2.style).isEqualTo(empty());
        assertThat(holder_1_3.style).isEqualTo(empty());
        assertThat(holder_2.style).isEqualTo(empty());
        assertThat(holder_2_1.style).isEqualTo(empty());
        assertThat(holder_2_2.style).isEqualTo(empty());
        assertThat(holder_2_2_1.style).isEqualTo(empty());
        assertThat(holder_2_2_2.style).isEqualTo(empty());
        assertThat(holder_2_2_1_0.style).isEqualTo(empty());

//        System.out.printf(format("holder_1       = %s (parent is %s)%n", holder_1, holder_1.parent));
//        System.out.printf(format("holder_1_1     = %s (parent is %s)%n", holder_1_1, holder_1_1.parent));
//        System.out.printf(format("holder_1_2     = %s (parent is %s)%n", holder_1_2, holder_1_2.parent));
//        System.out.printf(format("holder_1_3     = %s (parent is %s)%n", holder_1_3, holder_1_3.parent));
//        System.out.printf(format("holder_2       = %s (parent is %s)%n", holder_2, holder_2.parent));
//        System.out.printf(format("holder_2_1     = %s (parent is %s)%n", holder_2_1, holder_2_1.parent));
//        System.out.printf(format("holder_2_2     = %s (parent is %s)%n", holder_2_2, holder_2_2.parent));
//        System.out.printf(format("holder_2_2_1   = %s (parent is %s)%n", holder_2_2_1, holder_2_2_1.parent));
//        System.out.printf(format("holder_2_2_2   = %s (parent is %s)%n", holder_2_2_2, holder_2_2_2.parent));
//        System.out.printf(format("holder_2_2_1_0 = %s (parent is %s)%n", holder_2_2_1_0, holder_2_2_1_0.parent));

        assertThat(holder_2_2_1_0.parent).isSameAs(holder_2_2_1);
        assertThat(holder_2_2_1_0.parent.parent).isSameAs(holder_2_2);
        assertThat(holder_2_2_1_0.parent.parent.parent).isSameAs(holder_2);
        assertThat(holder_2_2_1_0.parent.parent.parent.parent).isNull();

        assertThat(holder_1.styleChain().toList()).isEmpty();
        assertThat(holder_1_1.styleChain().toList()).isEmpty();
        assertThat(holder_1_2.styleChain().toList()).isEmpty();
        assertThat(holder_1_3.styleChain().toList()).isEmpty();
        assertThat(holder_2.styleChain().toList()).isEmpty();
        assertThat(holder_2_1.styleChain().toList()).isEmpty();
        assertThat(holder_2_2.styleChain().toList()).isEmpty();
        assertThat(holder_2_2_1.styleChain().toList()).isEmpty();
        assertThat(holder_2_2_2.styleChain().toList()).isEmpty();
        assertThat(holder_2_2_1_0.styleChain().toList()).isEmpty();
    }

    @Test
    void testStylesArePresentAndNull() {
        initStyleNull = true;
        checkThatStylesArePresent();
    }

    @Test
    void testStylesArePresentAndEmpty() {
        initStyleNull = false;
        checkThatStylesArePresent();
    }

    private void checkThatStylesArePresent() {
        TestStyleHolder holder_1 = new TestStyleHolder(null, true, true);
        TestStyleHolder holder_1_1 = new TestStyleHolder(holder_1, 1, 1);
        TestStyleHolder holder_1_2 = new TestStyleHolder(holder_1, 1, 2);
        TestStyleHolder holder_1_3 = new TestStyleHolder(holder_1, 1, 3);
        TestStyleHolder holder_2 = new TestStyleHolder(null);
        TestStyleHolder holder_2_1 = new TestStyleHolder(holder_2, 2, 1);
        TestStyleHolder holder_2_2 = new TestStyleHolder(holder_2, 2, 2);
        TestStyleHolder holder_2_2_1 = new TestStyleHolder(holder_2_2, 221, 221);
        TestStyleHolder holder_2_2_2 = new TestStyleHolder(holder_2_2, false, true);
        TestStyleHolder holder_2_2_1_0 = new TestStyleHolder(holder_2_2_1, true, false);

        assertThat(holder_1.styleChain().map(AnsiStyle::dump).toList()).containsExactly(
            "ansi-style<bold,underline>");
        assertThat(holder_1_1.styleChain().map(AnsiStyle::dump).toList()).containsExactly(
            "ansi-style<bold,underline>", "ansi-style<fg(#01),bg(#01)>");
        assertThat(holder_1_2.styleChain().map(AnsiStyle::dump).toList()).containsExactly(
            "ansi-style<bold,underline>", "ansi-style<fg(#01),bg(#02)>");
        assertThat(holder_1_3.styleChain().map(AnsiStyle::dump).toList()).containsExactly(
            "ansi-style<bold,underline>", "ansi-style<fg(#01),bg(#03)>");
        assertThat(holder_2.styleChain().map(AnsiStyle::dump).toList()).isEmpty();
        assertThat(holder_2_1.styleChain().map(AnsiStyle::dump).toList()).containsExactly(
            "ansi-style<fg(#02),bg(#01)>");
        assertThat(holder_2_2.styleChain().map(AnsiStyle::dump).toList()).containsExactly(
            "ansi-style<fg(#02),bg(#02)>");
        assertThat(holder_2_2_1.styleChain().map(AnsiStyle::dump).toList()).containsExactly(
            "ansi-style<fg(#02),bg(#02)>", "ansi-style<fg(#DD),bg(#DD)>");
        assertThat(holder_2_2_2.styleChain().map(AnsiStyle::dump).toList()).containsExactly(
            "ansi-style<fg(#02),bg(#02)>", "ansi-style<!bold,underline>");
        assertThat(holder_2_2_1_0.styleChain().map(AnsiStyle::dump).toList()).containsExactly(
            "ansi-style<fg(#02),bg(#02)>", "ansi-style<fg(#DD),bg(#DD)>", "ansi-style<bold,!underline>");
    }

    boolean initStyleNull = true;

    class TestStyleHolder implements AnsiStyle.Holder {

        final AnsiStyle style;
        final TestStyleHolder parent;

        TestStyleHolder(TestStyleHolder parent) {
            this.parent = parent;
            this.style = initStyleNull ? null : empty();
        }

        TestStyleHolder(TestStyleHolder parent, int fg256, int bg256) {
            this.parent = parent;
            this.style = empty().builder()
                .accept(fg(fg256))
                .accept(bg(bg256))
                .build();
        }

        TestStyleHolder(TestStyleHolder parent, boolean bold, boolean underline) {
            this.parent = parent;
            this.style = empty().builder()
                .accept(bold ? APPLY_BOLD : RESET_BOLD)
                .accept(underline ? APPLY_UNDERLINE : RESET_UNDERLINE)
                .build();
        }

        @Override
        public Optional<AnsiStyle> style() {
            return Optional.ofNullable(style);
        }

        @Override
        public Optional<AnsiStyle.Holder> parent() {
            return Optional.ofNullable(parent);
        }
    }
}
