package org.krmdemo.yaml.reconcile.ansi;

import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;

@Slf4j
public class UppercaseMethodListener extends Java8BaseListener {

    private List<String> errors = new ArrayList<>();

    public List<String> getErrors() {
        return errors;
    }

    @Override
    public void enterMethodDeclarator(Java8Parser.MethodDeclaratorContext ctx) {
        TerminalNode node = ctx.Identifier();
        String methodName = node.getText();

        if (Character.isUpperCase(methodName.charAt(0))) {
            String error = String.format("Method '%s' is uppercased!", methodName);
            errors.add(error);
            log.info("An error was detected --> " + error);
        }
    }


}
