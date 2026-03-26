package nl.han.ica.icss.ast;

import nl.han.ica.icss.ast.types.ExpressionType;

public abstract class Expression extends ASTNode {
    private ExpressionType type = ExpressionType.UNDEFINED;

    public ExpressionType getType() {
        return type;
    }

    public void setType(ExpressionType type) {
        this.type = type;
    }
}
