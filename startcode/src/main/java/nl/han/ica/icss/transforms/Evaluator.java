package nl.han.ica.icss.transforms;

import java.util.HashMap;
import java.util.LinkedList;
import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.Declaration;
import nl.han.ica.icss.ast.Expression;
import nl.han.ica.icss.ast.IfClause;
import nl.han.ica.icss.ast.Literal;
import nl.han.ica.icss.ast.Stylerule;
import nl.han.ica.icss.ast.Stylesheet;
import nl.han.ica.icss.ast.VariableAssignment;
import nl.han.ica.icss.ast.VariableReference;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
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
    variableValues.push(new HashMap<>());

    for (ASTNode child : node.getChildren()) {
      if (child instanceof VariableAssignment) {
        applyVariableAssignment((VariableAssignment) child);
      }
    }

    for (ASTNode child : node.getChildren()) {
      if (child instanceof Stylerule) {
        applyStylerule((Stylerule) child);
      }
    }
  }

  private void applyVariableAssignment(VariableAssignment varAssignment) {
    Literal value = evalExpression(varAssignment.expression);
    variableValues.peek().put(varAssignment.name.name, value);
  }


  private void applyStylerule(Stylerule node) {
    for (ASTNode child : node.body) {
      if (child instanceof Declaration) {
        applyDeclartion((Declaration) child);
      } else if (child instanceof IfClause) {
        applyIfClause((IfClause) child);
      }
    }
  }


  private void applyIfClause(IfClause ifClause) {

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
    if (expression instanceof VariableReference) {
      return evalVariableReference((VariableReference) expression);
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
    if (left instanceof PercentageLiteral && right instanceof PercentageLiteral) {
      int result = ((PercentageLiteral) left).value + ((PercentageLiteral) right).value;
      return new PercentageLiteral(result);
    }
    return null;
  }

  private Literal evalSubtractOperation(SubtractOperation subtrOpp) {
    Literal left = evalExpression(subtrOpp.lhs);
    Literal right = evalExpression(subtrOpp.rhs);

    if (left instanceof PixelLiteral && right instanceof PixelLiteral) {
      int result = ((PixelLiteral) left).value - ((PixelLiteral) right).value;
      return new PixelLiteral(result);
    }
    if (left instanceof PercentageLiteral && right instanceof PercentageLiteral) {
      int result = ((PercentageLiteral) left).value - ((PercentageLiteral) right).value;
      return new PercentageLiteral(result);
    }
    return null;
  }




  private Literal evalVariableReference(VariableReference var) {
    String name = var.name;
    for (HashMap<String, Literal> scope : variableValues) {
      if (scope.containsKey(name)) {
        return scope.get(name);
      }
    }
    return null;
  }

}

