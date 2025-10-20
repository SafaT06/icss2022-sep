package nl.han.ica.icss.checker;

import java.util.HashMap;
import java.util.LinkedList;
import javafx.scene.paint.Color;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.Declaration;
import nl.han.ica.icss.ast.Stylerule;
import nl.han.ica.icss.ast.Stylesheet;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.types.ExpressionType;


public class Checker {

//  private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;
  private LinkedList<HashMap<String, ExpressionType>> variableTypes;


  public void check(AST ast) {
    // variableTypes = new HANLinkedList<>();
    checkStylesheet(ast.root);

  }

  private void checkStylesheet(Stylesheet sheet) {
    checkStylerule((Stylerule)sheet.getChildren().get(0));
  }

  private void checkStylerule(Stylerule stylerule) {
    for(ASTNode child: stylerule.getChildren()) {
      if (child instanceof Declaration) {
        checkDeclaration((Declaration) child);
      }
    }
  }

  private void checkDeclaration(Declaration declaration) {
    if (declaration.property.name.equals("width")) {
      if (declaration.expression instanceof ColorLiteral) {
        declaration.setError("Width can't be a color");
      }
    }
  }


}
