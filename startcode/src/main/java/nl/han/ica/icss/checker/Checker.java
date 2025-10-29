package nl.han.ica.icss.checker;

import java.util.HashMap;
import java.util.LinkedList;
import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.Declaration;
import nl.han.ica.icss.ast.Expression;
import nl.han.ica.icss.ast.IfClause;
import nl.han.ica.icss.ast.Operation;
import nl.han.ica.icss.ast.Stylerule;
import nl.han.ica.icss.ast.Stylesheet;
import nl.han.ica.icss.ast.VariableAssignment;
import nl.han.ica.icss.ast.VariableReference;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

public class Checker {


  //  private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;
  private LinkedList<HashMap<String, ExpressionType>> variableTypes;

  public void check(AST ast) {
    //    variableTypes = new HANLinkedList<>();
    variableTypes = new LinkedList<>();
    variableTypes.push(new HashMap<>());
    checkStylesheet(ast.root);
  }

  private void checkStylesheet(Stylesheet sheet) {
    for (ASTNode child : sheet.getChildren()) {
      if (child instanceof VariableAssignment) {
        checkVariableAssignment((VariableAssignment) child);
      }
    }

    for (ASTNode child : sheet.getChildren()) {
      if (child instanceof Stylerule) {
        checkStylerule((Stylerule) child);
      }
    }
  }

  private void checkVariableAssignment(VariableAssignment child) {
    ExpressionType type = getExpressionType(child.expression);
    if (type == null || type == ExpressionType.UNDEFINED) {
      child.setError("Je bestaat niet ouwe");
    } else {
      variableTypes.peek().put(child.name.name, type);
    }
  }

  private void checkStylerule(Stylerule rule) {
    for (ASTNode child : rule.body) {
      if (child instanceof Declaration) {
        checkDeclaration((Declaration) child);
      } else if (child instanceof IfClause) {
        checkIfClause((IfClause) child);
      }
    }
  }

  private void checkIfClause(IfClause ifClause) {
    ExpressionType conditionType = getExpressionType(ifClause.conditionalExpression);

    if (conditionType != ExpressionType.BOOL) {
      ifClause.setError("if state moet een boolean zijn");
    }

    for (ASTNode child : ifClause.body) {
      if (child instanceof Declaration) {
        checkDeclaration((Declaration) child);
      } else if (child instanceof IfClause) {
        checkIfClause((IfClause) child);
      }
    }

    // Else
    if (ifClause.elseClause != null) {
      for (ASTNode child : ifClause.elseClause.body) {
        if (child instanceof Declaration) {
          checkDeclaration((Declaration) child);
        } else if (child instanceof IfClause) {
          checkIfClause((IfClause) child);
        }
      }
    }
  }


  private void checkDeclaration(Declaration declaration) {
    if (declaration.property == null || declaration.expression == null) {
      return;
    }

    String name = declaration.property.name;
    ExpressionType type = getExpressionType(declaration.expression);

    if (name.equals("width") || name.equals("height")) {
      if (declaration.expression instanceof ColorLiteral) {
        declaration.setError("Nee nee nee, geen kleuren voor width/height");
      }
    }

    if (name.equals("color") || name.equals("background-color")) {
      if (declaration.expression instanceof PixelLiteral) {
        declaration.setError("Je doet weer iets doms, mag geen pixels voor kleuren gebruiken");
      }
    }
  }

  private ExpressionType getExpressionType(Expression expr) {
    if (expr instanceof PixelLiteral) {
      return ExpressionType.PIXEL;
    } else if (expr instanceof ColorLiteral) {
      return ExpressionType.COLOR;
    } else if (expr instanceof BoolLiteral) {
      return ExpressionType.BOOL;
    } else if (expr instanceof ScalarLiteral) {
      return ExpressionType.SCALAR;
    } else if (expr instanceof PercentageLiteral) {
      return ExpressionType.PERCENTAGE;
    } else if (expr instanceof VariableReference) {
      return checkVariableReference((VariableReference) expr);
    } else if (expr instanceof Operation) {
      return checkOperation((Operation) expr);
    }
    return ExpressionType.UNDEFINED;
  }

  private ExpressionType checkVariableReference(VariableReference ref) {
    String name = ref.name;
    for (HashMap<String, ExpressionType> scope : variableTypes) {
      if (scope.containsKey(name)) {
        return scope.get(name);
      }
    }
    ref.setError("variabel " + name + " bestaaaaaaat niet");
    return ExpressionType.UNDEFINED;
  }

  private ExpressionType checkOperation(Operation oper) {
    ExpressionType left = getExpressionType(oper.lhs);
    ExpressionType right = getExpressionType(oper.rhs);

    if (left == ExpressionType.UNDEFINED || right == ExpressionType.UNDEFINED) {
      return ExpressionType.UNDEFINED;
    }

    if (left == ExpressionType.COLOR || right == ExpressionType.COLOR) {
      oper.setError("Kleuren kan je niet rekenen ouwe");
      return ExpressionType.UNDEFINED;
    }

    if (oper instanceof MultiplyOperation) {
      if (left != ExpressionType.SCALAR && right != ExpressionType.SCALAR) {
        oper.setError("Bij keren min 1 scalar jongeman");
        return ExpressionType.UNDEFINED;
      }
      return (left == ExpressionType.SCALAR) ? right : left;
    }

    if (left != right) {
      oper.setError("oprrrr moeten van hetzelfde type zijn bij plus en min");
      return ExpressionType.UNDEFINED;
    }

    return left;
  }




}
