package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.icss.ast.*;

public class Evaluator implements Transform {

    private HANLinkedList variableValues;

    public Evaluator() {
        variableValues = new HANLinkedList();
    }

    @Override
    public void apply(AST ast) {
        variableValues = new HANLinkedList();

    }

    
}
