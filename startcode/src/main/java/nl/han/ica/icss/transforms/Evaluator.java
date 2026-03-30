package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.ArrayList;
import java.util.List;

public class Evaluator implements Transform {

    private HANLinkedList<ASTNode> variableValues;

    public Evaluator() {
        variableValues = new HANLinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        variableValues = new HANLinkedList<>();
        evaluate(ast.root);
        removeIfClauses(ast.root);
    }

    private void evaluate(ASTNode node) {
        if (node == null) {
            return;
        }

        if (node instanceof VariableAssignment) {
            VariableAssignment assignment = (VariableAssignment) node;
            evaluate(assignment.expression);
            variableValues.addFirst(assignment);
            return;
        }

        if (node instanceof Declaration) {
            Declaration declaration = (Declaration) node;
            evaluate(declaration.expression);
            return;
        }

        if (node instanceof IfClause) {
            IfClause ifClause = (IfClause) node;
            evaluate(ifClause.conditionalExpression);

            for (ASTNode child : ifClause.body) {
                evaluate(child);
            }

            if (ifClause.elseClause != null) {
                for (ASTNode child : ifClause.elseClause.body) {
                    evaluate(child);
                }
            }
            return;
        }

        for (ASTNode child : node.getChildren()) {
            evaluate(child);
        }

        if (node instanceof Expression) {
            replaceExpressionWithLiteral((Expression) node);
        }
    }

    private void replaceExpressionWithLiteral(Expression expression) {
        if (expression instanceof Literal) {
            return;
        }

        if (expression instanceof VariableReference) {
            VariableReference ref = (VariableReference) expression;
            Expression replacement = getVariableValue(ref.name);
            if (replacement != null) {
                copyExpressionValue(expression, replacement);
            }
            return;
        }

        if (expression instanceof Operation) {
            Operation op = (Operation) expression;
            Expression left = op.lhs;
            Expression right = op.rhs;

            if (left == null || right == null) {
                return;
            }

            if (left instanceof VariableReference) {
                Expression replacement = getVariableValue(((VariableReference) left).name);
                if (replacement != null) left = replacement;
            }
            if (right instanceof VariableReference) {
                Expression replacement = getVariableValue(((VariableReference) right).name);
                if (replacement != null) right = replacement;
            }

            Literal result = calculateOperation(op, left, right);
            if (result != null) {
                copyExpressionValue(expression, result);
            }
        }
    }

    private Literal calculateOperation(Operation op, Expression left, Expression right) {
        if (left instanceof ScalarLiteral && right instanceof ScalarLiteral) {
            int l = ((ScalarLiteral) left).value;
            int r = ((ScalarLiteral) right).value;

            if (op instanceof AddOperation) {
                return new ScalarLiteral(l + r);
            }
            if (op instanceof SubtractOperation) {
                return new ScalarLiteral(l - r);
            }
            if (op instanceof MultiplyOperation) {
                return new ScalarLiteral(l * r);
            }
        }

        if (left instanceof PixelLiteral && right instanceof PixelLiteral) {
            int l = ((PixelLiteral) left).value;
            int r = ((PixelLiteral) right).value;

            if (op instanceof AddOperation) {
                return new PixelLiteral(l + r);
            }
            if (op instanceof SubtractOperation) {
                return new PixelLiteral(l - r);
            }
            if (op instanceof MultiplyOperation) {
                return new PixelLiteral(l * r);
            }
        }

        if (left instanceof PixelLiteral && right instanceof ScalarLiteral) {
            int l = ((PixelLiteral) left).value;
            int r = ((ScalarLiteral) right).value;

            if (op instanceof MultiplyOperation) {
                return new PixelLiteral(l * r);
            }
        }

        if (left instanceof ScalarLiteral && right instanceof PixelLiteral) {
            int l = ((ScalarLiteral) left).value;
            int r = ((PixelLiteral) right).value;

            if (op instanceof MultiplyOperation) {
                return new PixelLiteral(l * r);
            }
        }

        return null;
    }

    private void copyExpressionValue(Expression target, Expression source) {
        target.setType(source.getType());

        if (source instanceof PixelLiteral) {
            target.setType(ExpressionType.PIXEL);
        } else if (source instanceof ScalarLiteral) {
            target.setType(ExpressionType.SCALAR);
        } else if (source instanceof ColorLiteral) {
            target.setType(ExpressionType.COLOR);
        } else if (source instanceof BoolLiteral) {
            target.setType(ExpressionType.BOOL);
        }
    }

    private Expression getVariableValue(String name) {
        for (int i = 0; i < variableValues.getSize(); i++) {
            ASTNode node = variableValues.get(i);
            if (node instanceof VariableAssignment) {
                VariableAssignment assignment = (VariableAssignment) node;
                if (assignment.name != null && assignment.name.name.equals(name)) {
                    return assignment.expression;
                }
            }
        }
        return null;
    }

    private void processBody(List<ASTNode> body) {
        ArrayList<ASTNode> newBody = new ArrayList<>();

        for (ASTNode child : body) {
            if (child instanceof IfClause) {
                addResolvedIfClause((IfClause) child, newBody);
            } else {
                removeIfClauses(child);
                newBody.add(child);
            }
        }

        body.clear();
        body.addAll(newBody);
    }

    private void removeIfClauses(ASTNode node) {
        if (node == null) return;

        if (node instanceof Stylesheet) {
            processBody(((Stylesheet) node).body);
        }
        else if (node instanceof Stylerule) {
            processBody(((Stylerule) node).body);
        }
    }

    private void addResolvedIfClause(IfClause ifClause, ArrayList<ASTNode> targetBody) {
        boolean conditionIsTrue = isTrue(ifClause.conditionalExpression);

        if (conditionIsTrue) {
            targetBody.addAll(ifClause.body);
            return;
        }

        if (ifClause.elseClause != null) {
            targetBody.addAll(ifClause.elseClause.body);
        }
    }

    private boolean isTrue(Expression expression) {
        if (expression instanceof BoolLiteral) {
            return ((BoolLiteral) expression).value;
        }
        return false;
    }
}

