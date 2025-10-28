package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.Declaration;
import nl.han.ica.icss.ast.Literal;
import nl.han.ica.icss.ast.Selector;
import nl.han.ica.icss.ast.Stylerule;
import nl.han.ica.icss.ast.Stylesheet;

public class Generator {

  public String generate(AST ast) {
    return generateStylesheet(ast.root);
  }

  private String generateStylesheet(Stylesheet node) {
    StringBuilder css = new StringBuilder();

    for (ASTNode child : node.getChildren()) {
      if (child instanceof Stylerule) {
        css.append(generateStylerule((Stylerule) child));
        css.append("\n");
      }
    }

    return css.toString();
  }

  private String generateStylerule(Stylerule node) {
    StringBuilder css = new StringBuilder();

    for (Selector selector : node.selectors) {
      css.append(selector.toString());
    }
    css.append(" {\n");

    for (ASTNode child : node.body) {
      if (child instanceof Declaration) {
        css.append(generateDeclaration((Declaration) child, 1));
      }
    }
    css.append("}\n");
    return css.toString();
  }

  private String generateDeclaration(Declaration declaration, int indentLevel) {
    StringBuilder css = new StringBuilder();

    css.append("  ".repeat(indentLevel));

    css.append(declaration.property.name);
    css.append(": ");

    if (declaration.expression instanceof Literal) {
      css.append(((Literal) declaration.expression).toString());
    }

    css.append(";\n");
    return css.toString();
  }
}
