package nl.han.ica.icss.checker;

import java.util.HashMap;
import java.util.LinkedList;
import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.Declaration;
import nl.han.ica.icss.ast.Stylerule;
import nl.han.ica.icss.ast.Stylesheet;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.types.ExpressionType;

public class Checker {


  //  private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;
  private LinkedList<HashMap<String, ExpressionType>> variableTypes;

  public void check(AST ast) {
    //    variableTypes = new HANLinkedList<>();
    variableTypes = new LinkedList<>();
    checkStylesheet(ast.root);
  }

  private void checkStylesheet(Stylesheet sheet) {
    for (ASTNode child : sheet.getChildren()) {
      if (child instanceof Stylerule) {
        checkStylerule((Stylerule) child);
      }
    }
  }

  private void checkStylerule(Stylerule rule) {
    for (ASTNode child : rule.body) {
      if (child instanceof Declaration) {
        checkDeclaration((Declaration) child);
      }
    }
  }

  private void checkDeclaration(Declaration declaration) {
    if (declaration.property == null || declaration.expression == null) {
      return;
    }

    String name = declaration.property.name;

    if (name.equals("width")) {
      if (declaration.expression instanceof ColorLiteral) {
        declaration.setError("Width can't be a color");
      }
    }

    if(name.equals("color")) {
      if (declaration.expression instanceof PixelLiteral) {
        declaration.setError("Color can't be a pixel");
      }
    }
  }
}
