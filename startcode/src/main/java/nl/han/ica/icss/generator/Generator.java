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
      }
    }

    return css.toString();
  }

  private String generateStylerule(Stylerule node) {
    StringBuilder css = new StringBuilder();

    for (int i = 0; i < node.selectors.size(); i++) {
      css.append(generateSelector(node.selectors.get(i)));
      if (i < node.selectors.size() - 1) {
        css.append(", ");
      }
    }
    css.append(" {\n");

    for (ASTNode child : node.body) {
      if (child instanceof Declaration) {
        css.append(generateDeclaration((Declaration) child));
      }
    }

    css.append("}\n");
    return css.toString();
  }

  private String generateDeclaration(Declaration declaration) {
    StringBuilder css = new StringBuilder();

    css.append("  ");
    css.append(declaration.property.name);
    css.append(": ");

    if (declaration.expression instanceof Literal) {
      Literal literal = (Literal) declaration.expression;
      css.append(literal.toString());
    }

    css.append(";\n");
    return css.toString();
  }

  private String generateSelector(Selector selector) {
    return selector.toString();
  }
}
