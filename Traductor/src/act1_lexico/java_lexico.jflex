package act1_lexico;

import java_cup.runtime.Symbol;

%%

%class LexerJava
%public
%line
%column
%cup
%cupsym act1_lexico.sym

%{
    private Symbol symbol(int type) {
        return new Symbol(type, yyline + 1, yycolumn + 1);
    }

    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline + 1, yycolumn + 1, value);
    }
%}

/* Expresiones Regulares Básicas */
LineTerminator = \r|\n|\r\n
Whitespace     = {LineTerminator} | [ \t\f]
Digit          = [0-9]
Letter         = [a-zA-Z_]
Identifier     = {Letter}({Letter}|{Digit})*
IntLiteral     = {Digit}+
FloatLiteral   = {Digit}+\.{Digit}+[fF]?
DoubleLiteral  = {Digit}+\.{Digit}+[dD]?
CharLiteral    = '([^'\\]|\\.)'
StringLiteral  = \"([^\"\\]|\\.)*\"

/* Comentarios */
TraditionalComment = "/*" [^*] ~"*/" | "/*" "*"+ "/"
EndOfLineComment   = "//" [^\r\n]* {LineTerminator}?
Comment            = {TraditionalComment} | {EndOfLineComment}

%%

<YYINITIAL> {

    /* Espacios en blanco y comentarios (Ignorados) */
    {Whitespace}       { /* Ignorar */ }
    {Comment}          { /* Ignorar */ }

    /* Palabras Reservadas */
    "package"          { return symbol(sym.PACKAGE, yytext()); }
    "import"           { return symbol(sym.IMPORT, yytext()); }
    "class"            { return symbol(sym.CLASS, yytext()); }
    "final"            { return symbol(sym.FINAL, yytext()); }
    "public"           { return symbol(sym.PUBLIC, yytext()); }
    "private"          { return symbol(sym.PRIVATE, yytext()); }
    "protected"        { return symbol(sym.PROTECTED, yytext()); }
    "internal"         { return symbol(sym.INTERNAL, yytext()); }
    "var"              { return symbol(sym.VAR, yytext()); }
    "val"              { return symbol(sym.VAL, yytext()); }
    "fun"              { return symbol(sym.FUN, yytext()); }
    "if"               { return symbol(sym.IF, yytext()); }
    "else"             { return symbol(sym.ELSE, yytext()); }
    "true"             { return symbol(sym.TRUE, yytext()); }
    "false"            { return symbol(sym.FALSE, yytext()); }
    "null"             { return symbol(sym.NULL, yytext()); }

    /* Tipos de Datos */
    "int"              { return symbol(sym.INT, yytext()); }
    "float"            { return symbol(sym.FLOAT, yytext()); }
    "double"           { return symbol(sym.DOUBLE, yytext()); }
    "char"             { return symbol(sym.CHAR, yytext()); }
    "boolean"          { return symbol(sym.BOOLEAN, yytext()); }
    "String"           { return symbol(sym.STRING, yytext()); }

    /* Operadores y Símbolos */
    "="                { return symbol(sym.EQ, yytext()); }
    "+"                { return symbol(sym.PLUS, yytext()); }
    "-"                { return symbol(sym.MINUS, yytext()); }
    "*"                { return symbol(sym.MULT, yytext()); }
    "/"                { return symbol(sym.DIV, yytext()); }
    "%"                { return symbol(sym.MOD, yytext()); }
    "=="               { return symbol(sym.EQEQ, yytext()); }
    "!="               { return symbol(sym.NEQ, yytext()); }
    "<"                { return symbol(sym.LT, yytext()); }
    "<="               { return symbol(sym.LE, yytext()); }
    ">"                { return symbol(sym.GT, yytext()); }
    ">="               { return symbol(sym.GE, yytext()); }
    "&&"               { return symbol(sym.AND, yytext()); }
    "||"               { return symbol(sym.OR, yytext()); }
    "!"                { return symbol(sym.NOT, yytext()); }

    /* Delimitadores */
    "("                { return symbol(sym.LPAREN, yytext()); }
    ")"                { return symbol(sym.RPAREN, yytext()); }
    "{"                { return symbol(sym.LBRACE, yytext()); }
    "}"                { return symbol(sym.RBRACE, yytext()); }
    "["                { return symbol(sym.LBRACK, yytext()); }
    "]"                { return symbol(sym.RBRACK, yytext()); }
    ";"                { return symbol(sym.SEMICOLON, yytext()); }
    ","                { return symbol(sym.COMMA, yytext()); }
    "."                { return symbol(sym.DOT, yytext()); }
    ":"                { return symbol(sym.COLON, yytext()); }

    /* Literales */
    {IntLiteral}       { return symbol(sym.INT_LITERAL, yytext()); }
    {FloatLiteral}     { return symbol(sym.FLOAT_LITERAL, yytext()); }
    {DoubleLiteral}    { return symbol(sym.DOUBLE_LITERAL, yytext()); }
    {CharLiteral}      { return symbol(sym.CHAR_LITERAL, yytext()); }
    {StringLiteral}    { return symbol(sym.STRING_LITERAL, yytext()); }

    /* Identificadores */
    {Identifier}       { return symbol(sym.IDENTIFIER, yytext()); }

    /* Errores Léxicos (IDs mal formados o caracteres inválidos) */
    {Digit}+{Letter}+  { 
                         System.err.println("Error Lexico [Linea " + (yyline+1) + ", Columna " + (yycolumn+1) + "]: Identificador invalido '" + yytext() + "'");
                         return symbol(sym.ERROR, yytext()); 
                       }

}

/* Carácter no reconocido */
[^]                    { 
                         System.err.println("Error Lexico [Linea " + (yyline+1) + ", Columna " + (yycolumn+1) + "]: Caracter no permitido '" + yytext() + "'");
                         return symbol(sym.ERROR, yytext()); 
                       } 