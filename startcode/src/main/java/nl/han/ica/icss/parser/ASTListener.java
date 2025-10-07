package nl.han.ica.icss.parser;

import com.google.errorprone.annotations.Var;
import java.util.Stack;
import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.PropertyName;
import nl.han.ica.icss.ast.Stylerule;
import nl.han.ica.icss.ast.Stylesheet;
import nl.han.ica.icss.ast.VariableAssignment;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.parser.ICSSParser.ExprContext;
import nl.han.ica.icss.parser.ICSSParser.IdselectorContext;
import nl.han.ica.icss.parser.ICSSParser.MathexprContext;
import nl.han.ica.icss.parser.ICSSParser.PropertyContext;
import nl.han.ica.icss.parser.ICSSParser.StyleruleContext;
import nl.han.ica.icss.parser.ICSSParser.StylesheetContext;
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
    currentContainer.peek().addChild(variableAssignment);
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
  public void enterIdselector(IdselectorContext ctx) {
    IdSelector idSelector = new IdSelector(ctx.getText());
    currentContainer.peek().addChild(idSelector);
  }

  @Override
  public void exitIdselector(IdselectorContext ctx) {
//    IdSelector idSelector = (IdSelector) currentContainer.pop();
//    currentContainer.peek().addChild(idSelector);
  }

  @Override
  public void enterProperty(PropertyContext ctx) {
    PropertyName propertyName = new PropertyName();
    currentContainer.peek().addChild(propertyName);
  }

  @Override
  public void exitProperty(PropertyContext ctx) {
//  PropertyName propertyName = (PropertyName) currentContainer.pop();
//  currentContainer.peek().addChild(propertyName);
  }

  @Override
  public void enterExpr(ExprContext ctx) {


  }

  @Override
  public void exitExpr(ExprContext ctx) {

  }

  @Override
  public void enterMathexpr(MathexprContext ctx) {

  }

  @Override
  public void exitMathexpr(MathexprContext ctx) {

  }
}
