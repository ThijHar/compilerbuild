package nl.han.ica.icss.parser;

import java.util.Stack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.ASTNode;

public class HANStack implements IHANStack<ASTNode> {
    private final Stack<ASTNode> stack = new Stack<>();

    @Override
    public void push(ASTNode value) {
        stack.push(value);
    }

    @Override
    public ASTNode pop() {
        return stack.pop();
    }

    @Override
    public ASTNode peek() {
        return stack.peek();
    }
}