package org.krmdemo.yaml.reconcile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class YamlReconcile {

    /**
     * JSV entry-point
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        System.out.println("Hello " + YamlReconcile.class.getSimpleName());
        log.info("Hello from {}'s logging !!!", YamlReconcile.class.getSimpleName());
    }
}
