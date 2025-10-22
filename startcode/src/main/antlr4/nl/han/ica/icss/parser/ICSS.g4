grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';

//--- PARSER: ---
stylesheet: variableassignment* stylerule* EOF;
variableassignment: CAPITAL_IDENT ASSIGNMENT_OPERATOR expr SEMICOLON;
stylerule: selector OPEN_BRACE declaration* CLOSE_BRACE;
selector: (CLASS_IDENT | ID_IDENT | LOWER_IDENT);
declaration: property COLON expr SEMICOLON;
property: ('background-color' | 'width' | 'height' | 'color');
//expr: literal ((PLUS | MIN | MUL) literal)*;
expr
  : expr MUL expr              #mulExpr
  | expr PLUS expr             #addExpr
  | expr MIN expr              #subtrExpr
  | literal                    #literalExpr
  ;



literal:
    COLOR #colorLiteral
  | PIXELSIZE #pixelLiteral
  | PERCENTAGE # percentageLiteral
  | TRUE #boolLiteral
  | FALSE #boolLiteral
  | SCALAR #scalarLiteral
  | CAPITAL_IDENT #variableReference
  ;





