package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.Declaration;
import nl.han.ica.icss.ast.ElseClause;
import nl.han.ica.icss.ast.Expression;
import nl.han.ica.icss.ast.IfClause;
import nl.han.ica.icss.ast.Operation;
import nl.han.ica.icss.ast.VariableAssignment;
import nl.han.ica.icss.ast.VariableReference;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;

public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableScopes;

    public void check(AST ast) {
        variableScopes = new HANLinkedList<>();
        variableScopes.addFirst(new HashMap<>());

        checkNode(ast.root);
    }

    private void checkNode(ASTNode node) {
        if (node == null) {
            return;
        }

        if (node instanceof VariableAssignment) {
            checkVariableAssignment((VariableAssignment) node);
            return;
        }

        if (node instanceof Declaration) {
            checkDeclaration((Declaration) node);
            return;
        }

        if (node instanceof IfClause) {
            checkIfClause((IfClause) node);
            return;
        }

        if (node instanceof ElseClause) {
            enterScope();
            for (ASTNode child : node.getChildren()) {
                checkNode(child);
            }
            exitScope();
            return;
        }

        if (node instanceof VariableReference) {
            VariableReference reference = (VariableReference) node;
            if (!isDefined(reference.name)) {
                reference.setError("Variabele '" + reference.name + "' is niet gedefinieerd");
            }
        }

        if (node instanceof Expression) {
            checkExpression((Expression) node);
        }

        for (ASTNode child : node.getChildren()) {
            checkNode(child);
        }
    }

    private void checkVariableAssignment(VariableAssignment assignment) {
        checkNode(assignment.expression);

        ExpressionType type = assignment.expression.getType();

        //todo: check variable kloppen

        if (type == ExpressionType.UNDEFINED) {
            assignment.setError("Variabele '" + assignment.name.name + "' krijgt een ongeldige expressie");
        }

        variableScopes.getFirst().put(assignment.name.name, type);
    }

    private void checkDeclaration(Declaration declaration) {
        checkNode(declaration.expression);

        ExpressionType expected = getExpectedTypeForProperty(declaration.property.name);
        ExpressionType actual = declaration.expression.getType();

        if (expected != ExpressionType.UNDEFINED && actual != expected) {
            declaration.setError(
                    "Property '" + declaration.property.name + "' verwacht type " + expected +
                            " maar kreeg " + actual
            );
        }
    }

    private void checkIfClause(IfClause ifClause) {
        checkNode(ifClause.conditionalExpression);

        if (ifClause.conditionalExpression.getType() != ExpressionType.BOOL) {
            ifClause.setError("De conditie van een if-statement moet boolean zijn");
        }

        enterScope();
        for (ASTNode child : ifClause.body) {
            checkNode(child);
        }
        exitScope();

        if (ifClause.elseClause != null) {
            checkNode(ifClause.elseClause);
        }
    }

    private void checkExpression(Expression expression) {
        if (expression instanceof PixelLiteral) {
            expression.setType(ExpressionType.PIXEL);
            return;
        }
        if (expression instanceof PercentageLiteral) {
            expression.setType(ExpressionType.PERCENTAGE);
            return;
        }
        if (expression instanceof ColorLiteral) {
            expression.setType(ExpressionType.COLOR);
            return;
        }
        if (expression instanceof ScalarLiteral) {
            expression.setType(ExpressionType.SCALAR);
            return;
        }
        if (expression instanceof BoolLiteral) {
            expression.setType(ExpressionType.BOOL);
            return;
        }
        if (expression instanceof VariableReference) {
            VariableReference reference = (VariableReference) expression;
            expression.setType(getVariableType(reference.name));
            return;
        }
        if (expression instanceof Operation) {
            checkOperation((Operation) expression);
        }
    }

    private void checkOperation(Operation operation) {
        checkNode(operation.lhs);
        checkNode(operation.rhs);

        ExpressionType left = operation.lhs.getType();
        ExpressionType right = operation.rhs.getType();

        if (left == ExpressionType.COLOR || right == ExpressionType.COLOR) {
            operation.setError("Kleuren mogen niet in operaties gebruikt worden");
            operation.setType(ExpressionType.UNDEFINED);
            return;
        }

        boolean isAddOrSub = operation.getClass().getSimpleName().contains("Add")
                || operation.getClass().getSimpleName().contains("Subtract");

        boolean isMultiply = operation.getClass().getSimpleName().contains("Multiply");

        if (isAddOrSub) {
            if (left != right) {
                operation.setError("Bij + en - moeten beide operanden hetzelfde type hebben");
                operation.setType(ExpressionType.UNDEFINED);
                return;
            }
            operation.setType(left);
            return;
        }

        if (isMultiply) {
            if (left != ExpressionType.SCALAR && right != ExpressionType.SCALAR) {
                operation.setError("Bij * moet minimaal één operand een scalar zijn");
                operation.setType(ExpressionType.UNDEFINED);
                return;
            }

            if (left == ExpressionType.SCALAR) {
                operation.setType(right);
            } else {
                operation.setType(left);
            }
        }
    }

    private void enterScope() {
        variableScopes.addFirst(new HashMap<>());
    }

    private void exitScope() {
        variableScopes.removeFirst();
    }

    private boolean isDefined(String name) {
        for (int i = 0; i < variableScopes.getSize(); i++) {
            if (variableScopes.get(i).containsKey(name)) {
                return true;
            }
        }
        return false;
    }

    private ExpressionType getVariableType(String name) {
        for (int i = 0; i < variableScopes.getSize(); i++) {
            if (variableScopes.get(i).containsKey(name)) {
                return variableScopes.get(i).get(name);
            }
        }
        return ExpressionType.UNDEFINED;
    }

    private ExpressionType getExpectedTypeForProperty(String propertyName) {
        switch (propertyName) {
            case "width":
            case "height":
            case "font-size":
            case "margin":
            case "padding":
            case "top":
            case "left":
            case "right":
            case "bottom":
                return ExpressionType.PIXEL;
            case "opacity":
                return ExpressionType.SCALAR;
            case "color":
            case "background-color":
            case "border-color":
                return ExpressionType.COLOR;
            default:
                return ExpressionType.UNDEFINED;
        }
    }
}