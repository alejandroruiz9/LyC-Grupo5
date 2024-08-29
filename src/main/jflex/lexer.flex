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

  /* Constants */
  {IntegerConstant}                        { return symbol(ParserSym.INTEGER_CONSTANT, yytext()); }
  {StringConstant}                         { return symbol(ParserSym.STRING_CONSTANT, yytext()); }
  {FloatConstant}                          { return symbol(ParserSym.FLOAT_CONSTANT, yytext()); }

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
  {if}                                     { return symbol(ParserSym.IF); }
  {elseif}                                 { return symbol(ParserSym.ELSEIF); }
  {else}                                   { return symbol(ParserSym.ELSE); }
  {and}                                    { return symbol(ParserSym.AND); }
  {or}                                     { return symbol(ParserSym.OR); }
  {not}                                    { return symbol(ParserSym.NOT); }
  
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