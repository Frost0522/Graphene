package src;

public enum Lexical {

    KEYWORD,
    INTEGER,
    BOOLEAN,
    IF,
    ELSE,
    NOT,
    AND,
    FN,
    FNCALL,
    OR,
    IDENTIFIER,
    UNDERSCORE,
    BOOLEAN_LITERAL,
    TRUE,
    FALSE,
    INTEGER_LITERAL,
    ZERO,
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    NINE,
    PUNCTUATION,
    LEFT_PAREN,
    RIGHT_PAREN,
    COMMA,
    COLON,
    RETURN,
    GREATERTHAN,
    COMMENT,
    OPERATOR,
    PLUS,
    MINUS,
    TIMES,
    DIVIDE,
    LESS_THAN,
    EQUIVALENT,
    EQUALS,
    
    PROGRAM,
    DEFINITIONLIST,
    DEFINITION,
    PARAMETERLIST,
    FORMALPARAMETERS,
    FORMALPARAMTAIL,
    IDWITHTYPE,
    TYPE,
    BODY,
    PRINTEXPRESSION,
    EXPRESSION,
    EXPRESSIONTAIL,
    SIMPLEEXPRESSION,
    SIMPLEEXPRESSIONTAIL,
    TERM,
    TERMTAIL,
    FACTOR,
    ARGUMENTLIST,
    FORMALARGUMENTS,
    FORMALARGSTAIL,
    LITERAL,
    SKIPPABLE,
    NEWLINE,
    $,

    MKID,
    MKTYPE,
    MKPARAM,
    MKARGS,
    MKFN,
    MKFNCALL,
    MKLIT,
    MKEQUALITY,
    MKMINUS,
    MKDIVIDE,
    MKTIMES,
    MKAND,
    MKOR,
    MKLESSTHAN,
    MKRETURNTYPE,
    MKPLUS;

    public int value() {
        switch (this) {
            case ZERO: {return 48;}
            case LEFT_PAREN: {return 40;}
            case RIGHT_PAREN: {return 41;}
            case COMMA: {return 44;}
            case COLON: {return 58;}
            case MINUS: {return 45;}
            case GREATERTHAN: {return 62;}
            case PLUS: {return 43;}
            case TIMES: {return 42;}
            case DIVIDE: {return 47;}
            case LESS_THAN: {return 60;}
            case EQUALS: {return 61;}
            case UNDERSCORE: {return 95;}
        }   return -1;
    }

    public static Lexical toType(Integer _int) {
        switch (_int) {
            case 40: {return LEFT_PAREN;}
            case 41: {return RIGHT_PAREN;}
            case 44: {return COMMA;}
            case 58: {return COLON;}
            case 45: {return MINUS;}
            case 62: {return RETURN;}
            case 43: {return PLUS;}
            case 42: {return TIMES;}
            case 47: {return DIVIDE;}
            case 60: {return LESS_THAN;}
            case 61: {return EQUIVALENT;}
            case 95: {return IDENTIFIER;}
        }   return $;
    }
}