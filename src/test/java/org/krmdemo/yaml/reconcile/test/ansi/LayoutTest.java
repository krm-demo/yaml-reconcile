package org.krmdemo.yaml.reconcile.test.ansi;

import org.junit.jupiter.api.Test;
import org.krmdemo.yaml.reconcile.ansi.AlignHorizontal;
import org.krmdemo.yaml.reconcile.ansi.AlignVertical;
import org.krmdemo.yaml.reconcile.ansi.AnsiBlock;
import org.krmdemo.yaml.reconcile.ansi.AnsiText;
import org.krmdemo.yaml.reconcile.ansi.Layout;

import static java.util.Arrays.asList;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.bg;
import static org.krmdemo.yaml.reconcile.ansi.AnsiText.ansiText;
import static org.krmdemo.yaml.reconcile.ansi.Layout.horizontal;

public class LayoutTest {

    @Test
    void testHorz() {
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
        Layout layoutHorz = horizontal(AlignVertical.TOP, asList(
            blockOne,
            blockTwo,
            blockThree
        ));
        System.out.println(layoutHorz);
        System.out.println(layoutHorz.renderAnsi());
    }
}
