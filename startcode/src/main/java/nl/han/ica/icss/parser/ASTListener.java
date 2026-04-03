package nl.han.ica.icss.parser;

import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.DivOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

public class ASTListener extends ICSSBaseListener {

	private AST ast;
	private IHANStack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack<>();
	}

	public AST getAST() {
		return ast;
	}

	@Override
	public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
		ast.setRoot(new Stylesheet());
		currentContainer.push(ast.root);
	}

	@Override
	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
		currentContainer.pop();
	}

	@Override
	public void enterStylerule(ICSSParser.StyleruleContext ctx) {
		Stylerule stylerule = new Stylerule();
		currentContainer.peek().addChild(stylerule);
		currentContainer.push(stylerule);
	}

	@Override
	public void exitStylerule(ICSSParser.StyleruleContext ctx) {
		currentContainer.pop();
	}

	@Override
	public void enterSelector(ICSSParser.SelectorContext ctx) {
		ASTNode selector;

		if (ctx.ID_IDENT() != null) {
			selector = new IdSelector(ctx.ID_IDENT().getText());
		} else if (ctx.CLASS_IDENT() != null) {
			selector = new ClassSelector(ctx.CLASS_IDENT().getText());
		} else {
			selector = new TagSelector(ctx.LOWER_IDENT().getText());
		}

		currentContainer.peek().addChild(selector);
	}

	@Override
	public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		Declaration declaration = new Declaration(ctx.LOWER_IDENT().getText());
		currentContainer.peek().addChild(declaration);
		currentContainer.push(declaration);
	}

	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		currentContainer.pop();
	}

	@Override
	public void enterValue(ICSSParser.ValueContext ctx) {
		ASTNode value;

		if (ctx.PIXELSIZE() != null) {
			value = new PixelLiteral(ctx.PIXELSIZE().getText());
		} else if (ctx.COLOR() != null) {
			value = new ColorLiteral(ctx.COLOR().getText());
		} else if (ctx.PERCENTAGE() != null) {
			value = new PercentageLiteral(ctx.PERCENTAGE().getText());
		} else if (ctx.SCALAR() != null) {
			value = new ScalarLiteral(ctx.SCALAR().getText());
		} else if (ctx.FALSE() != null) {
			value = new BoolLiteral(false);
		} else if (ctx.TRUE() != null) {
			value = new BoolLiteral(true);
		} else {
			value = new VariableReference(ctx.CAPITAL_IDENT().getText());
		}

		currentContainer.peek().addChild(value);
	}

	@Override
	public void enterPropertyname(ICSSParser.PropertynameContext ctx) {
		VariableAssignment assignment = new VariableAssignment();
		assignment.addChild(new VariableReference(ctx.CAPITAL_IDENT().getText()));
		currentContainer.peek().addChild(assignment);
		currentContainer.push(assignment);
	}

	@Override
	public void exitPropertyname(ICSSParser.PropertynameContext ctx) {
		currentContainer.pop();
	}

	@Override
	public void enterAddExpr(ICSSParser.AddExprContext ctx) {
		if (ctx.getChildCount() > 1) {
			AddOperation op = new AddOperation();
			currentContainer.peek().addChild(op);
			currentContainer.push(op);
		}
	}

	@Override
	public void exitAddExpr(ICSSParser.AddExprContext ctx) {
		if (ctx.getChildCount() > 1) {
			currentContainer.pop();
		}
	}

	@Override
	public void enterMulExpr(ICSSParser.MulExprContext ctx) {
		if (ctx.getChildCount() > 1) {
			MultiplyOperation op = new MultiplyOperation();
			currentContainer.peek().addChild(op);
			currentContainer.push(op);
		}
	}

	@Override
	public void enterDivExpr(ICSSParser.DivExprContext ctx) {
		if (ctx.getChildCount() > 1) {
			DivOperation op = new DivOperation();
			currentContainer.peek().addChild(op);
		}
	}

	@Override
	public void exitDivExpr(ICSSParser.DivExprContext ctx) {
		if (ctx.getChildCount() > 1) {
			currentContainer.pop();
		}
	}

	@Override
	public void exitMulExpr(ICSSParser.MulExprContext ctx) {
		if (ctx.getChildCount() > 1) {
			currentContainer.pop();
		}
	}

	@Override
	public void enterIfClause(ICSSParser.IfClauseContext ctx) {
		IfClause ifClause = new IfClause();
		currentContainer.peek().addChild(ifClause);
		currentContainer.push(ifClause);
	}

	@Override
	public void exitIfClause(ICSSParser.IfClauseContext ctx) {
		currentContainer.pop();
	}

	@Override
	public void enterElseClause(ICSSParser.ElseClauseContext ctx) {
		ElseClause elseClause = new ElseClause();
		currentContainer.peek().addChild(elseClause);
		currentContainer.push(elseClause);
	}

	@Override
	public void exitElseClause(ICSSParser.ElseClauseContext ctx) {
		currentContainer.pop();
	}
}