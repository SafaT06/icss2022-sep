package nl.han.ica.icss.transforms;

import java.util.ArrayList;
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
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

public class Evaluator implements Transform {

  private LinkedList<HashMap<String, Literal>> variableValues;

  public Evaluator() {
    variableValues = new LinkedList<>();
  }

  @Override
  public void apply(AST ast) {
    variableValues = new LinkedList<>();
    applyStylesheet(ast.root);
  }

  private void applyStylesheet(Stylesheet node) {
    pushScope();

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
    popScope();
  }

  private void applyVariableAssignment(VariableAssignment varAssignment) {
    Literal value = evalExpression(varAssignment.expression);
    variableValues.peek().put(varAssignment.name.name, value);
  }

  private void applyStylerule(Stylerule node) {
    pushScope();

    for (ASTNode child : node.body) {
      if (child instanceof VariableAssignment) {
        applyVariableAssignment((VariableAssignment) child);
      } else if (child instanceof Declaration) {
        applyDeclartion((Declaration) child);
      } else if (child instanceof IfClause) {
        ApplyIfClause((IfClause) child);
      }
    }

    node.body = flattenBody(node.body);

    popScope();
  }

  private void ApplyIfClause(IfClause ifClause) {
    for (ASTNode child : ifClause.body) {
      if (child instanceof VariableAssignment) {
        applyVariableAssignment((VariableAssignment) child);
      } else if (child instanceof Declaration) {
        applyDeclartion((Declaration) child);
      } else if (child instanceof IfClause) {
        ApplyIfClause((IfClause) child);
      }
    }

    if (ifClause.elseClause != null) {
      for (ASTNode child : ifClause.elseClause.body) {
        if (child instanceof VariableAssignment) {
          applyVariableAssignment((VariableAssignment) child);
        } else if (child instanceof Declaration) {
          applyDeclartion((Declaration) child);
        } else if (child instanceof IfClause) {
          ApplyIfClause((IfClause) child);
        }
      }
    }
  }

  private ArrayList<ASTNode> flattenBody(ArrayList<ASTNode> body) {
    ArrayList<ASTNode> result = new ArrayList<>();

    for (ASTNode child : body) {
      if (child instanceof IfClause) {
        IfClause ifClause = (IfClause) child;
        Literal condition = evalExpression(ifClause.conditionalExpression);

        if (condition instanceof BoolLiteral && ((BoolLiteral) condition).value) {
          result.addAll(flattenBody(ifClause.body));
        } else if (ifClause.elseClause != null) {
          result.addAll(flattenBody(ifClause.elseClause.body));
        }
      } else if (!(child instanceof VariableAssignment)) {
        result.add(child);
      }
    }

    return result;
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
      return new PixelLiteral(((ScalarLiteral) left).value * ((PixelLiteral) right).value);
    }
    if (left instanceof PixelLiteral && right instanceof ScalarLiteral) {
      return new PixelLiteral(((PixelLiteral) left).value * ((ScalarLiteral) right).value);
    }
    if (left instanceof ScalarLiteral && right instanceof ScalarLiteral) {
      return new ScalarLiteral(((ScalarLiteral) left).value * ((ScalarLiteral) right).value);
    }
    return null;
  }

  private Literal evalAddOperation(AddOperation addOpp) {
    Literal left = evalExpression(addOpp.lhs);
    Literal right = evalExpression(addOpp.rhs);

    if (left instanceof PixelLiteral && right instanceof PixelLiteral) {
      return new PixelLiteral(((PixelLiteral) left).value + ((PixelLiteral) right).value);
    }
    if (left instanceof PercentageLiteral && right instanceof PercentageLiteral) {
      return new PercentageLiteral(((PercentageLiteral) left).value + ((PercentageLiteral) right).value);
    }
    return null;
  }

  private Literal evalSubtractOperation(SubtractOperation subtrOpp) {
    Literal left = evalExpression(subtrOpp.lhs);
    Literal right = evalExpression(subtrOpp.rhs);

    if (left instanceof PixelLiteral && right instanceof PixelLiteral) {
      return new PixelLiteral(((PixelLiteral) left).value - ((PixelLiteral) right).value);
    }
    if (left instanceof PercentageLiteral && right instanceof PercentageLiteral) {
      return new PercentageLiteral(((PercentageLiteral) left).value - ((PercentageLiteral) right).value);
    }
    return null;
  }

  private Literal evalVariableReference(VariableReference var) {
    for (HashMap<String, Literal> scope : variableValues) {
      if (scope.containsKey(var.name)) {
        return scope.get(var.name);
      }
    }
    return null;
  }

  private void pushScope() {
    variableValues.push(new HashMap<>());
  }

  private void popScope() {
    variableValues.pop();
  }
}
