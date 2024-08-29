package lyc.compiler;

import java_cup.runtime.Symbol;
import lyc.compiler.ParserSym;
import lyc.compiler.model.*;
import static lyc.compiler.constants.Constants.*;

%%

%public
%class Lexer
%unicode
%cup
%line
%column
%throws CompilerException
%eofval{
  return symbol(ParserSym.EOF);
%eofval}


%{
  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
  }
%}


LineTerminator = \r|\n|\r\n
Identation =  [ \t\f]

Plus = "+"
Mult = "*"
Sub = "-"
Div = "/"
Assig = ":="
if = "if"
si = "si"
sino = "sino"
elseif = "elseif"
else = "else"
and = "AND"
or = "OR"
not = "NOT"
OpenParen = "("
CloseParen = ")"
OpenBrace = "{"
CloseBrace = "}"
GreaterThan = ">"
LessThan = "<"
GreaterEqual = ">="
LessEqual = "<="
Equals = "=="
NotEquals = "!="
Float = "Float"
Int = "Int"
String = "String"
Comentarios = "*-" ~ "-*"
write = "escribir"
read = "leer"
while = "while"
init="init"
colon=":"
comma=","


Letter = [a-zA-Z]
Digit = [0-9]

IntegerConstant = {Digit}+
StringConstant =  \"([^\"]|\\\")*\"
FloatConstant = {IntegerConstant} "." {IntegerConstant} | "." {IntegerConstant} | {IntegerConstant} "."

WhiteSpace = {LineTerminator} | {Identation}
Identifier = {Letter} ({Letter}|{Digit})*
IntegerConstant = {Digit}+

%%

/* keywords */

<YYINITIAL> {
  /* Comentarios */
  {Comentarios}                          { /* ignore */ }
  /* operators */
  {Plus}                                   { return symbol(ParserSym.PLUS); }
  {Sub}                                    { return symbol(ParserSym.SUB); }
  {Mult}                                   { return symbol(ParserSym.MULT); }
  {Div}                                    { return symbol(ParserSym.DIV); }
  {Assig}                                  { return symbol(ParserSym.ASSIG); }
  {GreaterThan}                            { return symbol(ParserSym.GREATERTHAN); }
  {LessThan}                               { return symbol(ParserSym.LESSTHAN); }
  {GreaterEqual}                           { return symbol(ParserSym.GREATEREQUAL); }
  {LessEqual}                              { return symbol(ParserSym.LESSEQUAL); }
  {Equals}                                 { return symbol(ParserSym.EQUALS); }
  {NotEquals}                              { return symbol(ParserSym.NOTEQUALS); }

  /* keywords */
  {init}                                    { return symbol(ParserSym.INIT); }
  {comma}                                    { return symbol(ParserSym.COMMA); }
  {colon}                                    { return symbol(ParserSym.COLON); }
  {if}                                     { return symbol(ParserSym.IF); }
  {si}                                     { return symbol(ParserSym.IF); }
  {elseif}                                 { return symbol(ParserSym.ELSEIF); }
  {sino}                                 { return symbol(ParserSym.ELSE); }
  {else}                                   { return symbol(ParserSym.ELSE); }
  {and}                                    { return symbol(ParserSym.AND); }
  {or}                                     { return symbol(ParserSym.OR); }
  {not}                                    { return symbol(ParserSym.NOT); }
  {write}                                     { return symbol(ParserSym.WRITE); }
  {read}                                    { return symbol(ParserSym.READ); }
  {while}                                    { return symbol(ParserSym.WHILE); }
  {Float}                                   { return symbol(ParserSym.FLOAT); }
  {Int}                                     { return symbol(ParserSym.INT); }
  {String}                                  { return symbol(ParserSym.STRING); }
  


  /* Constants */
  {FloatConstant}                         {
    if (Float.parseFloat(yytext()) >= MIN_FLOAT && Float.parseFloat(yytext()) <= MAX_FLOAT) {
        return symbol(ParserSym.FLOAT_CONSTANT, yytext());
    } else {
        throw new NumberFormatException("Variable de tipo Float fuera de rango");
    }
  }
  {IntegerConstant}       
  {
    // Verificamos si el int está dentro del rango
    if (Integer.parseInt(yytext()) >= MIN_INT && Integer.parseInt(yytext()) <= MAX_INT) {
        return symbol(ParserSym.INTEGER_CONSTANT, yytext());
    } else {
        throw new InvalidIntegerException("Variable de tipo Int fuera de rango");
    }
  }
  {StringConstant}                         { 


    String textoSinComillas = yytext().substring(1, yytext().length() - 1);

    // Verificamos que el string no supere los 40 caracteres
    if (textoSinComillas.length() <= MAX_LENGTH_STRING) {
        return symbol(ParserSym.STRING_CONSTANT, yytext()); 
    } else {
        throw new InvalidLengthException("Largo de variable de tipo string no valido");
    }

  }

  /* identifiers */
  {Identifier}                            { return symbol(ParserSym.IDENTIFIER, yytext()); }
  
  /* parentheses and braces */
  {OpenParen}                              { return symbol(ParserSym.OPEN_PAREN); }
  {CloseParen}                             { return symbol(ParserSym.CLOSE_PAREN); }
  {OpenBrace}                              { return symbol(ParserSym.OPEN_BRACE); }
  {CloseBrace}                             { return symbol(ParserSym.CLOSE_BRACE); }

  /* whitespace */
  {WhiteSpace}                             { /* ignore */ }
}

/* error fallback */
[^]                                        { throw new UnknownCharacterException(yytext()); }