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
            VariableAssignment va = (VariableAssignment) node;
            va.expression = evaluateExpression(va.expression);
            variableValues.addFirst(va);
            return;
        }

        if (node instanceof Declaration) {
            Declaration decl = (Declaration) node;
            decl.expression = evaluateExpression(decl.expression);
            return;
        }

        if (node instanceof IfClause) {
            IfClause ifClause = (IfClause) node;

            Expression cond = evaluateExpression(ifClause.conditionalExpression);

            if (cond instanceof BoolLiteral && ((BoolLiteral) cond).value) {
                for (ASTNode child : ifClause.body) {
                    evaluate(child);
                }
            } else if (ifClause.elseClause != null) {
                for (ASTNode child : ifClause.elseClause.body) {
                    evaluate(child);
                }
            }
            return;
        }

        for (ASTNode child : node.getChildren()) {
            evaluate(child);
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

    private Expression evaluateExpression(Expression expr) {
        if (expr instanceof Literal) {
            return expr;
        }

        if (expr instanceof VariableReference) {
            Expression val = getVariableValue(((VariableReference) expr).name);
            if (val == null){
                return expr;
            }
            return evaluateExpression(val);
        }

        if (expr instanceof Operation) {
            Operation op = (Operation) expr;

            Expression left = evaluateExpression(op.lhs);
            Expression right = evaluateExpression(op.rhs);

            Literal result = calculateOperation(op, left, right);
            if (result != null) {
                return result;
            }
        }

        return expr;
    }
}

