module org.krmdemo.yaml.reconcile.test {
    requires lombok;
    requires org.slf4j;
    requires org.antlr.antlr4.runtime;
    requires org.apache.commons.io;
    requires org.apache.commons.lang3;
    requires org.apache.commons.text;
    requires org.snakeyaml.engine.v2;

    requires org.assertj.core;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.params;
    requires org.junit.platform.engine;
    requires org.junit.platform.launcher;

    requires org.krmdemo.yaml.reconcile;

    exports org.krmdemo.yaml.reconcile.test;
    exports org.krmdemo.yaml.reconcile.test.ansi;

    opens org.krmdemo.yaml.reconcile.test to org.junit.platform.commons;
    opens org.krmdemo.yaml.reconcile.test.ansi to org.junit.platform.commons;
    opens org.krmdemo.yaml.reconcile.test.echo to org.junit.platform.commons;

//    uses org.junit.platform.launcher.TestExecutionListener;
    provides org.junit.platform.launcher.TestExecutionListener
        with org.krmdemo.yaml.reconcile.test.FixedWidthLogsExecutionListener;
}
