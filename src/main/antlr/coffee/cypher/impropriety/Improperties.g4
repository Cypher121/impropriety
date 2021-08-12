grammar Improperties;
@header {package coffee.cypher.impropriety;}

@members {
    private static String unescape(String input) {
       return input.replaceAll("\\\\(.)", "$1");
    }
}

file returns [java.util.Map<String, Object> val]
    @init {$val = new java.util.LinkedHashMap<>();}
    @after {$val = java.util.Collections.unmodifiableMap($val);}
    :
      (
        ( keyValuePair {$val.put($keyValuePair.key, $keyValuePair.val);} ) |
        LineEnd
      )*
      EOF
    ;

keyValuePair returns [String key, Object val]
    :
      ( Literal {$key = unescape($Literal.text);} )
      (
        ( compositeValue {$val = $compositeValue.val;} ) |
        ( Associator literalValue {$val = $literalValue.val;})
      )
    ;

value returns [Object val]
    :
      ( compositeValue {$val = $compositeValue.val;} ) |
      ( literalValue {$val = $literalValue.val;} )
    ;

literalValue returns [Object val]
    :
      Literal
      LineEnd
      {$val = unescape($Literal.text);}
    ;
compositeValue returns [Object val]
    :
      CompositeStarter
      (
        ( list {$val = $list.val;} ) |
        ( object {$val = $object.val;} )
      )
      CompositeCloser
    ;

object returns [java.util.Map<String, Object> val]
    @init {$val = new java.util.LinkedHashMap<>();}
    @after {$val = java.util.Collections.unmodifiableMap($val);}
    :
      (
        ( keyValuePair {$val.put($keyValuePair.key, $keyValuePair.val);} ) |
        LineEnd
      )*
    ;

list returns [java.util.List<Object> val]
    @init {$val = new java.util.ArrayList<>();}
    @after {$val = java.util.Collections.unmodifiableList($val);}
    :
      (
        ( listValue {$val.add($listValue.val);} ) |
        LineEnd
      )*
    ;

listValue returns [Object val]
    :
      ListItemMarker
      value
      {$val = $value.val;}
    ;


CompositeStarter : ( '->' LineEnd ) ;
CompositeCloser : '--' LineEnd ;
ListItemMarker : '-' ;
Associator : ':' | '=' ;

fragment AllowedCharacter
    : ~(' ' | '\t' | '\n' | '\r' | '\\' | ':' | '=' | '#' | '!' )
    ;

fragment EscapeCharacter : '\\' ;
fragment Control : ( '!' | '#' | '-' | ' ' | '\t' | '\r' | LineEnd | EscapeCharacter | Associator ) ;
fragment EscapedControl : EscapeCharacter Control ;

Literal : ( AllowedCharacter | EscapedControl )+ ;

LineEnd : '\n' ;

fragment CommentMarker : '#' | '!' ;

CommentText: CommentMarker (~('\n'))* -> skip;

WS : [ \t\r]+ -> skip ;