package src;
import java.util.ArrayList;
import java.util.Collections;

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
    MKIF,
    MKLIT,
    MKEQUALITY,
    MKMINUS,
    MKDIVIDE,
    MKTIMES,
    MKAND,
    MKOR,
    MKLESSTHAN,
    MKRETURNTYPE,
    MKPROGRAM,
    MKPRINT,
    MKNEG,
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

    public static ArrayList<Lexical> isTerminal() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,FN,LEFT_PAREN,RIGHT_PAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESS_THAN,OR,PLUS,MINUS,AND,TIMES,DIVIDE,NOT,IF,ELSE,IDENTIFIER,BOOLEAN_LITERAL,INTEGER_LITERAL);
        return list;
    }

    public static ArrayList<Lexical> isExpression() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,LEFT_PAREN,MINUS,NOT,IF,IDENTIFIER,BOOLEAN_LITERAL,INTEGER_LITERAL);
        return list;
    }
    
    public static ArrayList<Lexical> isExpTail() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,FN,LEFT_PAREN,RIGHT_PAREN,COMMA,OR,PLUS,MINUS,AND,TIMES,DIVIDE,NOT,IF,ELSE,IDENTIFIER,BOOLEAN_LITERAL,INTEGER_LITERAL,$);
        return list;
    }

    public static ArrayList<Lexical> isSimpleExpTail() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,FN,LEFT_PAREN,RIGHT_PAREN,COMMA,AND,TIMES,DIVIDE,NOT,IF,ELSE,IDENTIFIER,BOOLEAN_LITERAL,INTEGER_LITERAL,EQUIVALENT,LESS_THAN,$);
        return list;
    }

    public static ArrayList<Lexical> isTermTail() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,FN,LEFT_PAREN,RIGHT_PAREN,COMMA,OR,PLUS,MINUS,NOT,IF,ELSE,IDENTIFIER,BOOLEAN_LITERAL,INTEGER_LITERAL,EQUIVALENT,LESS_THAN,$);
        return list;
    }

    public static ArrayList<Lexical> isArgList() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,FN,RIGHT_PAREN,COMMA,EQUIVALENT,LESS_THAN,OR,PLUS,MINUS,AND,TIMES,DIVIDE,NOT,IF,ELSE,IDENTIFIER,BOOLEAN_LITERAL,INTEGER_LITERAL,$);
        return list;
    }

    public static ArrayList<Lexical> definitionRules() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,MKFN,BODY,MKRETURNTYPE,TYPE,RETURN,RIGHT_PAREN,PARAMETERLIST,LEFT_PAREN,MKID,IDENTIFIER,FN);
        return list;
    }

    public static ArrayList<Lexical> printExpRules() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,MKFNCALL,RIGHT_PAREN,EXPRESSION,LEFT_PAREN,MKARGS,MKID,IDENTIFIER);
        return list;
    }

    public static ArrayList<Lexical> formalParamRules() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,FORMALPARAMTAIL,MKPARAM,IDWITHTYPE);
        return list;
    }

    public static ArrayList<Lexical> formalParamTailRules() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,FORMALPARAMETERS,COMMA);
        return list;
    }

    public static ArrayList<Lexical> idWithTypeRules() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,MKTYPE,TYPE,COLON,MKID,IDENTIFIER);
        return list;
    }

    public static ArrayList<Lexical> expRules() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,EXPRESSIONTAIL,SIMPLEEXPRESSION);
        return list;
    }

    public static ArrayList<Lexical> equivalentRules() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,MKEQUALITY,EXPRESSION,EQUIVALENT);
        return list;
    }

    public static ArrayList<Lexical> lessThanRules() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,MKLESSTHAN,EXPRESSION,LESS_THAN);
        return list;
    }

    public static ArrayList<Lexical> simpleExpRules() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,SIMPLEEXPRESSIONTAIL,TERM);
        return list;
    }

    public static ArrayList<Lexical> orRules() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,MKOR,SIMPLEEXPRESSION,OR);
        return list;
    }
    
    public static ArrayList<Lexical> plusRules() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,MKPLUS,SIMPLEEXPRESSION,PLUS);
        return list;
    }

    public static ArrayList<Lexical> minusRules() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,MKMINUS,SIMPLEEXPRESSION,MINUS);
        return list;
    }

    public static ArrayList<Lexical> termRules() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,TERMTAIL,FACTOR);
        return list;
    }

    public static ArrayList<Lexical> andRules() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,MKAND,TERM,AND);
        return list;
    }

    public static ArrayList<Lexical> timesRules() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,MKTIMES,TERM,TIMES);
        return list;
    }

    public static ArrayList<Lexical> divideRules() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,MKDIVIDE,TERM,DIVIDE);
        return list;
    }

    public static ArrayList<Lexical> leftParenFactorRules() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,RIGHT_PAREN,EXPRESSION,LEFT_PAREN);
        return list;
    }

    public static ArrayList<Lexical> ifRules() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,MKIF,EXPRESSION,ELSE,EXPRESSION,RIGHT_PAREN,EXPRESSION,LEFT_PAREN,IF);
        return list;
    }

    public static ArrayList<Lexical> idRules() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,ARGUMENTLIST,MKID,IDENTIFIER);
        return list;
    }

    public static ArrayList<Lexical> leftParenArgRules() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,MKFNCALL,RIGHT_PAREN,FORMALARGUMENTS,LEFT_PAREN,MKARGS);
        return list;
    }

    public static ArrayList<Lexical> formalArgRules() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,FORMALARGSTAIL,EXPRESSION);
        return list;
    }
    
    public static ArrayList<Lexical> commaRules() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,FORMALARGUMENTS,COMMA);
        return list;
    }

    public static ArrayList<Lexical> integerLitRules() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,MKLIT,INTEGER_LITERAL);
        return list;
    }

    public static ArrayList<Lexical> booleanLitRules() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,MKLIT,BOOLEAN_LITERAL);
        return list;
    }

    public static ArrayList<Lexical> minusFactorRules() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,MKNEG,FACTOR,MINUS);
        return list;
    }

    public static ArrayList<Lexical> definitionListError() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,LEFT_PAREN,RIGHT_PAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESS_THAN,OR,PLUS,MINUS,AND,TIMES,DIVIDE,NOT,IF,ELSE,IDENTIFIER,BOOLEAN_LITERAL,INTEGER_LITERAL);
        return list;
    }

    public static ArrayList<Lexical> definitionError() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,LEFT_PAREN,RIGHT_PAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESS_THAN,OR,PLUS,MINUS,TIMES,DIVIDE,NOT,IF,ELSE,IDENTIFIER,BOOLEAN_LITERAL,INTEGER_LITERAL,$);
        return list;
    }

    public static ArrayList<Lexical> parameterListError() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,FN,LEFT_PAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESS_THAN,OR,PLUS,MINUS,TIMES,DIVIDE,NOT,IF,ELSE,BOOLEAN_LITERAL,INTEGER_LITERAL,$);
        return list;
    }

    public static ArrayList<Lexical> formalParamError() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,FN,LEFT_PAREN,RIGHT_PAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESS_THAN,OR,PLUS,MINUS,TIMES,DIVIDE,NOT,IF,ELSE,BOOLEAN_LITERAL,INTEGER_LITERAL,$);
        return list;
    }

    public static ArrayList<Lexical> arugmentListError() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,RETURN,COLON,INTEGER,BOOLEAN,IDENTIFIER);
        return list;
    }

    public static ArrayList<Lexical> bodyError() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,FN,RIGHT_PAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESS_THAN,OR,PLUS,AND,TIMES,DIVIDE,ELSE,$);
        return list;
    }

    public static ArrayList<Lexical> expressionTailError() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,RETURN,COLON,INTEGER,BOOLEAN,IDENTIFIER);
        return list;
    }

    public static ArrayList<Lexical> simpleExpTailError() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,RETURN,COLON,INTEGER,BOOLEAN,IDENTIFIER);
        return list;
    }

    public static ArrayList<Lexical> termTailError() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,RETURN,COLON,INTEGER,BOOLEAN,IDENTIFIER);
        return list;
    }

    public static ArrayList<Lexical> expressionError() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,FN,RIGHT_PAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESS_THAN,OR,PLUS,AND,TIMES,DIVIDE,ELSE,$);
        return list;
    }

    public static ArrayList<Lexical> simpleExpError() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,FN,RIGHT_PAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESS_THAN,OR,PLUS,AND,TIMES,DIVIDE,ELSE,$);
        return list;
    }

    public static ArrayList<Lexical> termError() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,FN,RIGHT_PAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESS_THAN,OR,PLUS,AND,TIMES,DIVIDE,ELSE,$);
        return list;
    }

    public static ArrayList<Lexical> factorError() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,FN,RIGHT_PAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESS_THAN,OR,PLUS,AND,TIMES,DIVIDE,ELSE,$);
        return list;
    }

    public static ArrayList<Lexical> formalArgsError() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,FN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESS_THAN,OR,PLUS,AND,TIMES,DIVIDE,ELSE,$);
        return list;
    }

    public static ArrayList<Lexical> formalArgsTailError() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,FN,LEFT_PAREN,RETURN,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESS_THAN,OR,PLUS,MINUS,AND,TIMES,DIVIDE,NOT,IF,ELSE,IDENTIFIER,BOOLEAN_LITERAL,INTEGER_LITERAL,$);
        return list;
    }

    public static ArrayList<Lexical> literalError() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,FN,LEFT_PAREN,RIGHT_PAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESS_THAN,OR,PLUS,MINUS,AND,TIMES,DIVIDE,NOT,IF,ELSE,IDENTIFIER,$);
        return list;
    }

    public static ArrayList<Lexical> printExpError() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,FN,LEFT_PAREN,RIGHT_PAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESS_THAN,OR,PLUS,MINUS,AND,TIMES,DIVIDE,NOT,IF,ELSE,BOOLEAN_LITERAL,INTEGER_LITERAL,$);
        return list;
    }

    public static ArrayList<Lexical> programError() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,LEFT_PAREN,RIGHT_PAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESS_THAN,OR,PLUS,MINUS,AND,TIMES,DIVIDE,NOT,IF,ELSE,IDENTIFIER,BOOLEAN_LITERAL,INTEGER_LITERAL);
        return list;
    }

    public static ArrayList<Lexical> typeError() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,FN,LEFT_PAREN,RIGHT_PAREN,RETURN,COMMA,COLON,EQUIVALENT,LESS_THAN,OR,PLUS,MINUS,AND,TIMES,DIVIDE,NOT,IF,ELSE,IDENTIFIER,BOOLEAN_LITERAL,INTEGER_LITERAL,$);
        return list;
    }

    public static ArrayList<Lexical> formalParamTailError() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,FN,LEFT_PAREN,RETURN,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESS_THAN,OR,PLUS,MINUS,AND,TIMES,DIVIDE,NOT,IF,ELSE,IDENTIFIER,BOOLEAN_LITERAL,INTEGER_LITERAL,$);
        return list;
    }

    public static ArrayList<Lexical> idWithTypeError() {
        ArrayList<Lexical> list = new ArrayList<>();
        Collections.addAll(list,FN,LEFT_PAREN,RIGHT_PAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESS_THAN,OR,PLUS,MINUS,AND,TIMES,DIVIDE,NOT,IF,ELSE,BOOLEAN_LITERAL,INTEGER_LITERAL,$);
        return list;
    }
}