package nl.han.ica.icss.parser;

import java.util.Stack;
import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.Declaration;
import nl.han.ica.icss.ast.PropertyName;
import nl.han.ica.icss.ast.Stylerule;
import nl.han.ica.icss.ast.Stylesheet;
import nl.han.ica.icss.ast.VariableAssignment;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.parser.ICSSParser.BoolLiteralContext;
import nl.han.ica.icss.parser.ICSSParser.ColorLiteralContext;
import nl.han.ica.icss.parser.ICSSParser.DeclarationContext;
import nl.han.ica.icss.parser.ICSSParser.ExprContext;
import nl.han.ica.icss.parser.ICSSParser.PercentageLiteralContext;
import nl.han.ica.icss.parser.ICSSParser.PixelLiteralContext;
import nl.han.ica.icss.parser.ICSSParser.PropertyContext;
import nl.han.ica.icss.parser.ICSSParser.ScalarLiteralContext;
import nl.han.ica.icss.parser.ICSSParser.SelectorContext;
import nl.han.ica.icss.parser.ICSSParser.StyleruleContext;
import nl.han.ica.icss.parser.ICSSParser.StylesheetContext;
import nl.han.ica.icss.parser.ICSSParser.VarLiteralContext;
import nl.han.ica.icss.parser.ICSSParser.VariableassignmentContext;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {

  //Accumulator attributes:
  private AST ast;

  //Use this to keep track of the parent nodes when recursively traversing the ast
//	private IHANStack<ASTNode> currentContainer;
  private Stack<ASTNode> currentContainer;


  public ASTListener() {
    ast = new AST();
//		currentContainer = new HANStack<>();
    currentContainer = new Stack<>();
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
  public void enterVariableassignment(VariableassignmentContext ctx) {
    VariableAssignment variableAssignment = new VariableAssignment();
    currentContainer.push(variableAssignment);
  }

  @Override
  public void exitVariableassignment(VariableassignmentContext ctx) {
    VariableAssignment variableAssignment = (VariableAssignment) currentContainer.pop();
    currentContainer.peek().addChild(variableAssignment);
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
  public void enterSelector(SelectorContext ctx) {
    IdSelector idSelector = new IdSelector(ctx.getText());
    currentContainer.push(idSelector);
  }

  @Override
  public void exitSelector(SelectorContext ctx) {
    IdSelector idSelector = (IdSelector) currentContainer.pop();
    currentContainer.peek().addChild(idSelector);
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
  public void enterExpr(ExprContext ctx) {
  }

  @Override
  public void exitExpr(ExprContext ctx) {
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
    ScalarLiteral scalarLiteral = new ScalarLiteral(ctx.getText());
    currentContainer.push(scalarLiteral);
  }

  @Override
  public void exitPercentageLiteral(PercentageLiteralContext ctx) {
    ScalarLiteral scalarLiteral = (ScalarLiteral) currentContainer.pop();
    currentContainer.peek().addChild(scalarLiteral);
  }

  @Override
  public void enterVarLiteral(VarLiteralContext ctx) {
  }

  @Override
  public void exitVarLiteral(VarLiteralContext ctx) {
  }
}


