module org.krmdemo.yaml.reconcile {
    exports org.krmdemo.yaml.reconcile;
    exports org.krmdemo.yaml.reconcile.impl to org.krmdemo.yaml.reconcile.test;

    requires lombok;
    requires org.slf4j;
    requires org.snakeyaml.engine.v2;
    requires org.apache.commons.io;
    requires org.apache.commons.lang3;
    requires org.apache.commons.text;
}
