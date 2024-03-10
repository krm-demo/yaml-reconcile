package org.krmdemo.yaml.reconcile.test.ansi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.krmdemo.yaml.reconcile.ansi.AlignHorizontal;
import org.krmdemo.yaml.reconcile.ansi.AlignVertical;
import org.krmdemo.yaml.reconcile.ansi.AnsiBlock;
import org.krmdemo.yaml.reconcile.ansi.AnsiText;
import org.krmdemo.yaml.reconcile.ansi.Layout;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.bg;
import static org.krmdemo.yaml.reconcile.ansi.AnsiText.ansiText;
import static org.krmdemo.yaml.reconcile.ansi.Layout.horizontal;
import static org.krmdemo.yaml.reconcile.ansi.Layout.vertical;

public class LayoutTest {

    final AnsiText txtOne = ansiText("one line");
    final AnsiText txtTwo = ansiText("two\nlines");
    final AnsiText txtThree = ansiText("and\nthree\nlines");

    final AnsiBlock blockOne = txtOne.blockBuilder()
        .horizontal(AlignHorizontal.CENTER)
        .leftIndentWidth(1).rightIndentWidth(1)
        .style(bg(240, 240, 255))
        .build();
    final AnsiBlock blockTwo = txtTwo.blockBuilder()
        .horizontal(AlignHorizontal.CENTER)
        .leftIndentWidth(1).rightIndentWidth(1)
        .style(bg(240, 255, 240))
        .build();
    final AnsiBlock blockThree = txtThree.blockBuilder()
        .horizontal(AlignHorizontal.CENTER)
        .leftIndentWidth(1).rightIndentWidth(1)
        .style(bg(255, 240, 240))
        .build();

    @BeforeEach
    void beforeAll() {
        assertThat(blockOne.content()).isEqualTo(" one line ");
        assertThat(blockTwo.content()).isEqualTo("  two  \n lines ");
        assertThat(blockThree.content()).isEqualTo("  and  \n three \n lines ");
    }

    @Test
    void testHorz() {
        Layout layoutHorzTop = horizontal(AlignVertical.TOP, blockOne, blockTwo, blockThree);
        System.out.println(layoutHorzTop + " - AlignVertical.TOP:");
        System.out.println(layoutHorzTop.renderAnsi());
        assertThat(layoutHorzTop.content()).isEqualTo("""
             one line   two    and \s
                       lines  three\s
                              lines \
            """);
        Layout layoutHorzMiddle = horizontal(AlignVertical.MIDDLE, blockOne, blockTwo, blockThree);
        System.out.println(layoutHorzMiddle + " - AlignVertical.MIDDLE:");
        System.out.println(layoutHorzMiddle.renderAnsi());
        assertThat(layoutHorzMiddle.content()).isEqualTo("""
                               and \s
             one line   two   three\s
                       lines  lines \
            """);
        Layout layoutHorzBottom = horizontal(AlignVertical.BOTTOM, blockOne, blockTwo, blockThree);
        System.out.println(layoutHorzBottom + " - AlignVertical.BOTTOM:");
        System.out.println(layoutHorzBottom.renderAnsi());
        assertThat(layoutHorzBottom.content()).isEqualTo("""
                               and \s
                        two   three\s
             one line  lines  lines \
            """);
        assertThat(asList(layoutHorzTop, layoutHorzMiddle, layoutHorzBottom))
            .allMatch(layout -> layout.height() == 3)
            .allMatch(layout -> layout.width() == 24);
    }

    @Test
    void testVert() {
        Layout layoutVertLeft = vertical(AlignHorizontal.LEFT, blockOne, blockTwo, blockThree);
        System.out.println(layoutVertLeft + " - AlignHorizontal.LEFT:");
        System.out.println(layoutVertLeft.renderAnsi());
        assertThat(layoutVertLeft.content()).isEqualTo("""
             one line\s
              two    \s
             lines   \s
              and    \s
             three   \s
             lines    \
            """);
        Layout layoutVertCenter = vertical(AlignHorizontal.CENTER, blockOne, blockTwo, blockThree);
        System.out.println(layoutVertCenter + " - AlignHorizontal.CENTER:");
        System.out.println(layoutVertCenter.renderAnsi());
        assertThat(layoutVertCenter.content()).isEqualTo("""
             one line\s
               two   \s
              lines  \s
               and   \s
              three  \s
              lines   \
            """);
        Layout layoutVertRight = vertical(AlignHorizontal.RIGHT, blockOne, blockTwo, blockThree);
        System.out.println(layoutVertRight + " - AlignHorizontal.RIGHT:");
        System.out.println(layoutVertRight.renderAnsi());
        assertThat(layoutVertRight.content()).isEqualTo("""
             one line\s
                 two \s
                lines\s
                 and \s
                three\s
                lines \
            """);
        assertThat(asList(layoutVertLeft, layoutVertCenter, layoutVertRight))
            .allMatch(layout -> layout.height() == 6)
            .allMatch(layout -> layout.width() == 10);
    }
}
