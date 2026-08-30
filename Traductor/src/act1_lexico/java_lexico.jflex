package act1_lexico;

import java_cup.runtime.Symbol;

%%

%class LexerJava
%public
%cup
%unicode
%line
%column
%char

%{
  private Symbol token(int type) {
    return new Symbol(type, yyline + 1, yycolumn + 1, yytext());
  }

  private Symbol token(int type, Object value) {
    return new Symbol(type, yyline + 1, yycolumn + 1, value);
  }

  private Symbol reportError(String tipo, String descripcion) {
    ManejoErrores.agregar(new ErrorLexico(tipo, yytext(), yyline + 1, yycolumn + 1, descripcion));
    return token(sym.ERROR, yytext());
  }
%}

/* Expresiones Regulares Básicas */
WHITE_SPACE     = [ \t\r\n]+
LETTER          = [a-zA-Z_]
DIGIT           = [0-9]
IDENTIFIER      = {LETTER}({LETTER}|{DIGIT})*

INT_LITERAL     = 0 | [1-9]{DIGIT}*
FLOAT_LITERAL   = (0|[1-9]{DIGIT}*)\.{DIGIT}+[fF]?
STRING_LITERAL  = \"([^\"\r\n\\]|\\.)*\"

/* Comentarios en Kotlin */
LINE_COMMENT    = "//".*
BLOCK_COMMENT   = "/*" [^*] ~"*/" | "/*" "*"+ "/"
COMMENT         = {LINE_COMMENT} | {BLOCK_COMMENT}

%%

{WHITE_SPACE} { /* Ignorar */ }
{COMMENT}     { /* Ignorar */ }

/* ========================================================
   1. REGLAS DE MANEJO DE ERRORES LÉXICOS (PRIORIDAD ALTA)
   ======================================================== */

/* Cadena mal formada: usa .trim() para limpiar el salto de línea al reportar */
\"([^\"\r\n\\]|\\.)*[\r\n] { 
    String lexemaLimpio = yytext().trim();
    ManejoErrores.agregar(new ErrorLexico("CADENA_MAL_FORMADA", lexemaLimpio, yyline + 1, yycolumn + 1, "Cadena de texto sin cerrar comillas en la misma línea"));
    return token(sym.ERROR, lexemaLimpio);
}

/* Identificador inválido: inicia con números */
{DIGIT}+{LETTER}+ { 
    return reportError("IDENTIFICADOR_INVALIDO", "El identificador no puede iniciar con números"); 
}

/* Número con ceros iniciales no permitidos (ej. 01050) */
0{DIGIT}+ { 
    return reportError("NUMERO_INVALIDO", "Literal numérico octal o cero inicial no permitido"); 
}

/* Flotante con múltiples puntos (ej. 45.30.5) */
{DIGIT}+\.{DIGIT}+\.{DIGIT}+ { 
    return reportError("NUMERO_INVALIDO", "Formato numérico inválido con múltiples puntos"); 
}

/* Flotante incompleto (ej. 45.) */
{DIGIT}+\. { 
    return reportError("NUMERO_INVALIDO", "Flotante incompleto, falta parte decimal"); 
}


/* ========================================================
   2. PALABRAS RESERVA Y PALABRAS CLAVE DE KOTLIN
   ======================================================== */

"package"     { return token(sym.PACKAGE); }
"import"      { return token(sym.IMPORT); }
"class"       { return token(sym.CLASS); }
"interface"   { return token(sym.INTERFACE); }
"object"      { return token(sym.OBJECT); }
"fun"         { return token(sym.FUN); }
"val"         { return token(sym.VAL); }
"var"         { return token(sym.VAR); }
"typealias"   { return token(sym.TYPEALIAS); }
"typeof"      { return token(sym.TYPEOF); }

/* Control de Flujo */
"if"          { return token(sym.IF); }
"else"        { return token(sym.ELSE); }
"when"        { return token(sym.WHEN); }
"while"       { return token(sym.WHILE); }
"for"         { return token(sym.FOR); }
"do"          { return token(sym.DO); }
"return"      { return token(sym.RETURN); }
"break"       { return token(sym.BREAK); }
"continue"    { return token(sym.CONTINUE); }

/* Manejo de Excepciones */
"try"         { return token(sym.TRY); }
"catch"       { return token(sym.CATCH); }
"finally"     { return token(sym.FINALLY); }
"throw"       { return token(sym.THROW); }

/* Modificadores de Visibilidad y Clases */
"public"      { return token(sym.PUBLIC); }
"private"     { return token(sym.PRIVATE); }
"protected"   { return token(sym.PROTECTED); }
"internal"    { return token(sym.INTERNAL); }
"abstract"    { return token(sym.ABSTRACT); }
"final"       { return token(sym.FINAL); }
"open"        { return token(sym.OPEN); }
"override"    { return token(sym.OVERRIDE); }
"sealed"      { return token(sym.SEALED); }
"data"        { return token(sym.DATA); }
"enum"        { return token(sym.ENUM); }
"inner"       { return token(sym.INNER); }
"value"       { return token(sym.VALUE); }
"annotation"  { return token(sym.ANNOTATION); }

/* Modificadores Especiales de Kotlin */
"infix"       { return token(sym.INFIX); }
"operator"    { return token(sym.OPERATOR); }
"inline"      { return token(sym.INLINE); }
"noinline"    { return token(sym.NOINLINE); }
"crossinline" { return token(sym.CROSSINLINE); }
"tailrec"     { return token(sym.TAILREC); }
"external"    { return token(sym.EXTERNAL); }
"lateinit"    { return token(sym.LATEINIT); }
"vararg"      { return token(sym.VARARG); }
"suspend"     { return token(sym.SUSPEND); }
"const"       { return token(sym.CONST); }

/* Verificación y Verbos */
"is"          { return token(sym.IS); }
"!is"         { return token(sym.NOT_IS); }
"as"          { return token(sym.AS); }
"as?"         { return token(sym.AS_SAFE); }
"in"          { return token(sym.IN); }
"!in"         { return token(sym.NOT_IN); }

/* Valores Literales Especiales */
"this"        { return token(sym.THIS); }
"super"       { return token(sym.SUPER); }
"null"        { return token(sym.NULL); }
"true"        { return token(sym.TRUE, yytext()); }
"false"       { return token(sym.FALSE); }

/* Constructores y Bloques de Inicialización */
"constructor" { return token(sym.CONSTRUCTOR); }
"init"        { return token(sym.INIT); }
"companion"   { return token(sym.COMPANION); }
"expect"      { return token(sym.EXPECT); }
"actual"      { return token(sym.ACTUAL); }
"where"       { return token(sym.WHERE); }
"out"         { return token(sym.OUT); }


/* ========================================================
   3. OPERADORES Y SÍMBOLOS
   ======================================================== */

/* Operadores Aritméticos */
"+"           { return token(sym.PLUS); }
"-"           { return token(sym.MINUS); }
"*"           { return token(sym.MULT); }
"/"           { return token(sym.DIV); }
"%"           { return token(sym.MOD); }

/* Operadores Relacionales y de Igualdad */
"==="         { return token(sym.EQEQEQ); }
"!=="         { return token(sym.NEQEQEQ); }
"=="          { return token(sym.EQEQ); }
"!="          { return token(sym.NEQ); }
"<="          { return token(sym.LE); }
">="          { return token(sym.GE); }
"<"           { return token(sym.LT); }
">"           { return token(sym.GT); }
"="           { return token(sym.EQ); }

/* Operadores Lógicos */
"&&"          { return token(sym.AND); }
"||"          { return token(sym.OR); }
"!"           { return token(sym.NOT); }

/* Símbolos Específicos */
"?:"          { return token(sym.ELVIS); }
"?."          { return token(sym.SAFE_CALL); }
"::"          { return token(sym.DOUBLE_COLON); }
".."          { return token(sym.RANGE); }
"("           { return token(sym.LPAREN); }
")"           { return token(sym.RPAREN); }
"{"           { return token(sym.LBRACE); }
"}"           { return token(sym.RBRACE); }
"["           { return token(sym.LBRACK); }
"]"           { return token(sym.RBRACK); }
","           { return token(sym.COMMA); }
";"           { return token(sym.SEMICOLON); }
"."           { return token(sym.DOT); }
":"           { return token(sym.COLON); }


/* ========================================================
   4. LITERALES E IDENTIFICADORES VÁLIDOS
   ======================================================== */

{INT_LITERAL}     { return token(sym.INT_LITERAL, yytext()); }
{FLOAT_LITERAL}   { return token(sym.FLOAT_LITERAL, yytext()); }
{STRING_LITERAL}  { return token(sym.STRING_LITERAL, yytext()); }
{IDENTIFIER}      { return token(sym.IDENTIFIER, yytext()); }

/* Literales de Carácter Válidos e Inválidos */
'\\?.' { 
    return token(sym.CHAR_LITERAL, yytext()); 
}

'[^'\r\n]*'? { 
    return reportError("CARACTER_MAL_FORMADO", "Literal de carácter inválido o sin cerrar"); 
}


/* ========================================================
   5. FALLBACK (CARACTERES NO PERMITIDOS EN EL ALFABETO)
   ======================================================== */

. { 
    return reportError("CARACTER_NO_RECONOCIDO", "Carácter no pertenece al lenguaje");
}