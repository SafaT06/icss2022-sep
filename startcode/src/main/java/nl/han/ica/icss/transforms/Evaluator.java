package nl.han.ica.icss.transforms;

import java.util.HashMap;
import java.util.LinkedList;
import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.Declaration;
import nl.han.ica.icss.ast.Expression;
import nl.han.ica.icss.ast.Literal;
import nl.han.ica.icss.ast.Stylerule;
import nl.han.ica.icss.ast.Stylesheet;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

public class Evaluator implements Transform {

  //  private IHANLinkedList<HashMap<String, Literal>> variableValues;
  private LinkedList<HashMap<String, Literal>> variableValues;

  public Evaluator() {
//    variableValues = new HANLinkedList<>();
    variableValues = new LinkedList<>();
  }

  @Override
  public void apply(AST ast) {
//    variableValues = new HANLinkedList<>();
    variableValues = new LinkedList<>();
    applyStylesheet(ast.root);
  }

  private void applyStylesheet(Stylesheet node) {
    for (ASTNode child : node.getChildren()) {
      if (child instanceof Stylerule) {
        applyStylerule((Stylerule) child);
      }

    }
  }

  private void applyStylerule(Stylerule node) {
    for (ASTNode child : node.getChildren()) {
      if (child instanceof Declaration) {
        applyDeclartion((Declaration) child);
      }
    }
  }

  private void applyDeclartion(Declaration node) {
    node.expression = evalExpression(node.expression);
  }

  private Literal evalExpression(Expression expression) {
    if (expression instanceof Literal) {
      return (Literal) expression;
    }
    if (expression instanceof MultiplyOperation) {
      return evalMultiplyOperation((MultiplyOperation) expression);
    }
    if (expression instanceof AddOperation) {
      return evalAddOperation((AddOperation) expression);
    }
    if (expression instanceof SubtractOperation) {
      return evalSubtractOperation((SubtractOperation) expression);
    }
    return null;
  }


  private Literal evalMultiplyOperation(MultiplyOperation multiOpp) {
    Literal left = evalExpression(multiOpp.lhs);
    Literal right = evalExpression(multiOpp.rhs);

    if (left instanceof ScalarLiteral && right instanceof PixelLiteral) {
      int result = ((ScalarLiteral) left).value * ((PixelLiteral) right).value;
      return new PixelLiteral(result);
    }
    if (left instanceof PixelLiteral && right instanceof ScalarLiteral) {
      int result = ((PixelLiteral) left).value * ((ScalarLiteral) right).value;
      return new PixelLiteral(result);
    }


    return null;
  }

  private Literal evalAddOperation(AddOperation addOpp) {
    Literal left = evalExpression(addOpp.lhs);
    Literal right = evalExpression(addOpp.rhs);

    if (left instanceof PixelLiteral && right instanceof PixelLiteral) {
      int result = ((PixelLiteral) left).value + ((PixelLiteral) right).value;
      return new PixelLiteral(result);
    }

    return null;
  }

  private Literal evalSubtractOperation(SubtractOperation subtrOpp) {
    return null;
  }
}

