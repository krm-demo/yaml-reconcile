module org.krmdemo.yaml.reconcile.test {
    requires lombok;
    requires org.slf4j;
    requires org.snakeyaml.engine.v2;
    requires org.apache.commons.io;
    requires org.apache.commons.lang3;
    requires org.apache.commons.text;

    requires org.krmdemo.yaml.reconcile;
    requires org.junit.jupiter.api;
    requires org.junit.platform.engine;
    requires org.junit.platform.launcher;
    requires org.assertj.core;
    requires JColor;

    exports org.krmdemo.yaml.reconcile.test;
    opens org.krmdemo.yaml.reconcile.test to org.junit.platform.commons;
    opens org.krmdemo.yaml.reconcile.test.ansi to org.junit.platform.commons;
    opens org.krmdemo.yaml.reconcile.test.echo to org.junit.platform.commons;

    uses org.junit.platform.launcher.TestExecutionListener;
    provides org.junit.platform.launcher.TestExecutionListener
        with org.krmdemo.yaml.reconcile.test.FixedWidthLogsExecutionListener;
}
