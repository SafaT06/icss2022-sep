package nl.han.ica.icss.parser;

import java.util.Stack;
import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.Declaration;
import nl.han.ica.icss.ast.ElseClause;
import nl.han.ica.icss.ast.IfClause;
import nl.han.ica.icss.ast.PropertyName;
import nl.han.ica.icss.ast.Selector;
import nl.han.ica.icss.ast.Stylerule;
import nl.han.ica.icss.ast.Stylesheet;
import nl.han.ica.icss.ast.VariableAssignment;
import nl.han.ica.icss.ast.VariableReference;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;
import nl.han.ica.icss.parser.ICSSParser.AddExprContext;
import nl.han.ica.icss.parser.ICSSParser.BoolLiteralContext;
import nl.han.ica.icss.parser.ICSSParser.ColorLiteralContext;
import nl.han.ica.icss.parser.ICSSParser.DeclarationContext;
import nl.han.ica.icss.parser.ICSSParser.ElseStateContext;
import nl.han.ica.icss.parser.ICSSParser.IfBodyContext;
import nl.han.ica.icss.parser.ICSSParser.IfStateContext;
import nl.han.ica.icss.parser.ICSSParser.LiteralExprContext;
import nl.han.ica.icss.parser.ICSSParser.MulExprContext;
import nl.han.ica.icss.parser.ICSSParser.PercentageLiteralContext;
import nl.han.ica.icss.parser.ICSSParser.PixelLiteralContext;
import nl.han.ica.icss.parser.ICSSParser.PropertyContext;
import nl.han.ica.icss.parser.ICSSParser.ScalarLiteralContext;
import nl.han.ica.icss.parser.ICSSParser.SelectorContext;
import nl.han.ica.icss.parser.ICSSParser.StyleruleBodyContext;
import nl.han.ica.icss.parser.ICSSParser.StyleruleContext;
import nl.han.ica.icss.parser.ICSSParser.StylesheetContext;
import nl.han.ica.icss.parser.ICSSParser.SubtrExprContext;
import nl.han.ica.icss.parser.ICSSParser.VariableReferenceContext;
import nl.han.ica.icss.parser.ICSSParser.VariableassignmentContext;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {

  //Accumulator attributes:
  private AST ast;

  //Use this to keep track of the parent nodes when recursively traversing the ast
	private IHANStack<ASTNode> currentContainer;
//  private Stack<ASTNode> currentContainer;


  public ASTListener() {
    ast = new AST();
		currentContainer = new HANStack<>();
//    currentContainer = new Stack<>();
  }

  public AST getAST() {
    return ast;
  }

  @Override
  public void enterStylesheet(StylesheetContext ctx) {
    Stylesheet stylesheet = new Stylesheet();
    currentContainer.push(stylesheet);
  }

  @Override
  public void exitStylesheet(StylesheetContext ctx) {
    Stylesheet stylesheet = (Stylesheet) currentContainer.pop();
    ast.setRoot(stylesheet);
  }

  @Override
  public void enterStylerule(StyleruleContext ctx) {
    Stylerule stylerule = new Stylerule();
    currentContainer.push(stylerule);
  }

  @Override
  public void exitStylerule(StyleruleContext ctx) {
    Stylerule stylerule = (Stylerule) currentContainer.pop();
    currentContainer.peek().addChild(stylerule);
  }

  @Override
  public void enterVariableassignment(VariableassignmentContext ctx) {
    VariableAssignment variableAssignment = new VariableAssignment();
    currentContainer.push(variableAssignment);

    VariableReference variableReference = new VariableReference(ctx.CAPITAL_IDENT().getText());
    variableAssignment.addChild(variableReference);
  }

  @Override
  public void exitVariableassignment(VariableassignmentContext ctx) {
    VariableAssignment variableAssignment = (VariableAssignment) currentContainer.pop();
    currentContainer.peek().addChild(variableAssignment);
  }

  @Override
  public void enterSelector(SelectorContext ctx) {
    Selector selector = null;

    if (ctx.CLASS_IDENT() != null) {
      selector = new ClassSelector(ctx.CLASS_IDENT().getText());
    } else if (ctx.ID_IDENT() != null) {
      selector = new IdSelector(ctx.ID_IDENT().getText());
    } else if (ctx.LOWER_IDENT() != null) {
      selector = new TagSelector(ctx.LOWER_IDENT().getText());
    }

    currentContainer.push(selector);
  }

  @Override
  public void exitSelector(SelectorContext ctx) {
    Selector selector = (Selector) currentContainer.pop();
    currentContainer.peek().addChild(selector);
  }

  @Override
  public void enterDeclaration(DeclarationContext ctx) {
    Declaration declaration = new Declaration(ctx.getText());
    currentContainer.push(declaration);
  }

  @Override
  public void exitDeclaration(DeclarationContext ctx) {
    Declaration declaration = (Declaration) currentContainer.pop();
    currentContainer.peek().addChild(declaration);
  }

  @Override
  public void enterIfBody(IfBodyContext ctx) {
  }

  @Override
  public void exitIfBody(IfBodyContext ctx) {
  }

  @Override
  public void enterStyleruleBody(StyleruleBodyContext ctx) {

  }

  @Override
  public void exitStyleruleBody(StyleruleBodyContext ctx) {
  }

  @Override
  public void enterProperty(PropertyContext ctx) {
    PropertyName propertyName = new PropertyName(ctx.getText());
    currentContainer.push(propertyName);
  }

  @Override
  public void exitProperty(PropertyContext ctx) {
    PropertyName propertyName = (PropertyName) currentContainer.pop();
    currentContainer.peek().addChild(propertyName);
  }

  @Override
  public void enterMulExpr(MulExprContext ctx) {
    MultiplyOperation multiplyOperation = new MultiplyOperation();
    currentContainer.push(multiplyOperation);
  }

  @Override
  public void exitMulExpr(MulExprContext ctx) {
    MultiplyOperation multiplyOperation = (MultiplyOperation) currentContainer.pop();
    currentContainer.peek().addChild(multiplyOperation);
  }

  @Override
  public void enterAddExpr(AddExprContext ctx) {
    AddOperation addOperation = new AddOperation();
    currentContainer.push(addOperation);
  }

  @Override
  public void exitAddExpr(AddExprContext ctx) {
    AddOperation addOperation = (AddOperation) currentContainer.pop();
    currentContainer.peek().addChild(addOperation);
  }

  @Override
  public void enterSubtrExpr(SubtrExprContext ctx) {
    SubtractOperation subtractOperation = new SubtractOperation();
    currentContainer.push(subtractOperation);
  }

  @Override
  public void exitSubtrExpr(SubtrExprContext ctx) {
    SubtractOperation subtractOperation = (SubtractOperation) currentContainer.pop();
    currentContainer.peek().addChild(subtractOperation);
  }


  @Override
  public void enterPixelLiteral(PixelLiteralContext ctx) {
    PixelLiteral pixelLiteral = new PixelLiteral(ctx.getText());
    currentContainer.push(pixelLiteral);
  }

  @Override
  public void exitPixelLiteral(PixelLiteralContext ctx) {
    PixelLiteral pixelLiteral = (PixelLiteral) currentContainer.pop();
    currentContainer.peek().addChild(pixelLiteral);
  }

  @Override
  public void enterColorLiteral(ColorLiteralContext ctx) {
    ColorLiteral colorLiteral = new ColorLiteral(ctx.getText());
    currentContainer.push(colorLiteral);
  }

  @Override
  public void exitColorLiteral(ColorLiteralContext ctx) {
    ColorLiteral colorLiteral = (ColorLiteral) currentContainer.pop();
    currentContainer.peek().addChild(colorLiteral);
  }

  @Override
  public void enterScalarLiteral(ScalarLiteralContext ctx) {
    ScalarLiteral scalarLiteral = new ScalarLiteral(ctx.getText());
    currentContainer.push(scalarLiteral);
  }

  @Override
  public void exitScalarLiteral(ScalarLiteralContext ctx) {
    ScalarLiteral scalarLiteral = (ScalarLiteral) currentContainer.pop();
    currentContainer.peek().addChild(scalarLiteral);
  }

  @Override
  public void enterBoolLiteral(BoolLiteralContext ctx) {
    BoolLiteral boolLiteral = new BoolLiteral(ctx.getText());
    currentContainer.push(boolLiteral);
  }

  @Override
  public void exitBoolLiteral(BoolLiteralContext ctx) {
    BoolLiteral boolLiteral = (BoolLiteral) currentContainer.pop();
    currentContainer.peek().addChild(boolLiteral);
  }

  @Override
  public void enterPercentageLiteral(PercentageLiteralContext ctx) {
    PercentageLiteral percentageLiteral = new PercentageLiteral(ctx.getText());
    currentContainer.push(percentageLiteral);
  }

  @Override
  public void exitPercentageLiteral(PercentageLiteralContext ctx) {
    PercentageLiteral percentageLiteral = (PercentageLiteral) currentContainer.pop();
    currentContainer.peek().addChild(percentageLiteral);
  }


  @Override
  public void enterVariableReference(VariableReferenceContext ctx) {
    VariableReference variableReference = new VariableReference(ctx.getText());
    currentContainer.push(variableReference);
  }

  @Override
  public void exitVariableReference(VariableReferenceContext ctx) {
    VariableReference variableReference = (VariableReference) currentContainer.pop();
    currentContainer.peek().addChild(variableReference);
  }

  @Override
  public void enterIfState(IfStateContext ctx) {
    IfClause ifClause = new IfClause();
    currentContainer.push(ifClause);
  }

  @Override
  public void exitIfState(IfStateContext ctx) {
    IfClause ifClause = (IfClause) currentContainer.pop();
    currentContainer.peek().addChild(ifClause);
  }

  @Override
  public void enterElseState(ElseStateContext ctx) {
    ElseClause elseClause = new ElseClause();
    currentContainer.push(elseClause);
  }

  @Override
  public void exitElseState(ElseStateContext ctx) {
    ElseClause elseClause = (ElseClause) currentContainer.pop();
    currentContainer.peek().addChild(elseClause);
  }

  @Override
  public void enterLiteralExpr(LiteralExprContext ctx) {
  }

  @Override
  public void exitLiteralExpr(LiteralExprContext ctx) {
  }


}


