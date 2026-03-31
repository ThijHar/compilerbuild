package nl.han.ica.icss.generator;


import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;

import java.util.HashMap;

public class Generator {
	String generatedCSS;
	IHANLinkedList<HashMap<String, Expression>> variableScopes;

	public String generate(AST ast) {
		this.generatedCSS = "";
		variableScopes = new HANLinkedList<>();
		variableScopes.addFirst(new HashMap<>());
		GenStylesheet(ast.root);

		return this.generatedCSS;
	}

	private void GenStylesheet(ASTNode root) {
		for (ASTNode child : root.getChildren()) {
			if (child instanceof Stylerule){
				Stylerule stylerule = (Stylerule) child;
				this.generatedCSS += stylerule.selectors.get(0) + " {\n";
				GenStylerule(stylerule);
				this.generatedCSS += "}\n";
			} else if (child instanceof VariableAssignment) {
				VariableAssignment variable = (VariableAssignment) child;
				variableScopes.getFirst().put(variable.name.name, variable.expression);
			}
		}
	}

	private void GenStylerule(Stylerule stylerule) {
		for (ASTNode child : stylerule.getChildren()) {
			if (child instanceof Declaration){
				Declaration declaration = (Declaration) child;
				this.generatedCSS += "  ";
				this.generatedCSS += declaration.property.name;
				this.generatedCSS += ": ";
                if (!(declaration.expression instanceof VariableReference)) {
                    String expression = literalToString((Literal) declaration.expression);
					this.generatedCSS += expression + ";\n";
				} else {
					this.generatedCSS += getVariableValue(((VariableReference) declaration.expression).name) + ";\n";
				}

                System.out.println();

			}
		}
	}

	private String literalToString(Literal literal) {
		if (literal instanceof PixelLiteral) {
			return ((PixelLiteral) literal).value + "px";
		} else if (literal instanceof PercentageLiteral) {
			return ((PercentageLiteral) literal).value + "%";
		} else if (literal instanceof ScalarLiteral) {
			return Integer.toString(((ScalarLiteral) literal).value);
		} else if (literal instanceof ColorLiteral) {
			return ((ColorLiteral) literal).value;
		} else if (literal instanceof BoolLiteral) {
			return Boolean.toString(((BoolLiteral) literal).value);
		}

		return "";
	}

	private String getVariableValue(String name) {
		int size = variableScopes.getSize();
		for (int i = 0; i < size; i++) {
			HashMap<String, Expression> scope = variableScopes.get(i);
			if (scope.containsKey(name)) {
				Expression expression = scope.get(name);
				if (expression instanceof VariableReference) {
					getVariableValue(((VariableReference) expression).name);
				} else {
					return literalToString((Literal) expression);
				}
			}
		}
		return "";
	}
}
