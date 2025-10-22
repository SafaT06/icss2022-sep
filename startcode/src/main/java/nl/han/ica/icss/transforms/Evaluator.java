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
import nl.han.ica.icss.ast.operations.AddOperation;

public class Evaluator implements Transform {

  //    private IHANLinkedList<HashMap<String, Literal>> variableValues;
  private LinkedList<HashMap<String, Literal>> variableValues;


  public Evaluator() {
    //variableValues = new HANLinkedList<>();
    variableValues = new LinkedList<>();
  }

  @Override
  public void apply(AST ast) {
    //variableValues = new HANLinkedList<>();
    applyStylesheet(ast.root);
  }

  private void applyStylesheet(Stylesheet node) {
    applyStylerule((Stylerule) node.getChildren().get(0));
  }

  private void applyStylerule(Stylerule node) {
    for (ASTNode child : node.getChildren()) {
      if (child instanceof Declaration) {
        applyDeclartion((Declaration) child);
      }
    }
  }

  //
  private void applyDeclartion(Declaration node) {
    node.expression = evalExpression(node.expression);
  }

  private Literal evalExpression(Expression expression) {
    if (expression instanceof Literal) {
      return (Literal) expression;
    } else {
      return evalAddOperation((AddOperation) expression);
    }
  }

  private Literal evalAddOperation(AddOperation expression) {
//    Literal left = evalExpression(expression.lhs);
//    Literal right = evalExpression(expression.rhs);
    return null;
  }
}

