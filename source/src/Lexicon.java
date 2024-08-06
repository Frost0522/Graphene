package src;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public enum Lexicon {

    // Keywords
    INTEGER, BOOLEAN, IF, ELSE, NOT, AND, FN, FNCALL, OR, ID, UNDERSCORE, BOOLEANLITERAL,
    TRUE, FALSE, INTEGERLITERAL, LEFTPAREN, RIGHTPAREN, COMMA, COLON, RETURN,
    GREATERTHAN, COMMENT, PLUS, MINUS, TIMES, DIVIDE, LESSTHAN, EQUIVALENT, 
    EQUALS,
    
    // Parsing rules
    PROGRAM, DEFINITIONLIST, DEFINITION, PARAMLIST, FORMALPARAM, FORMALPARAMTAIL, 
    IDWITHTYPE, TYPE, BODY, PRINTEXP, EXP, EXPTAIL, SIMPLEEXP, SIMPLEEXPTAIL, TERM, 
    TERMTAIL, FACTOR, ARGLIST, FORMALARG, FORMALARGTAIL, LITERAL, SKIP, NL, $,

    // Semantic actions
    MKID, MKTYPE, MKPARAM, MKARGS, MKFN, MKFNCALL, MKIF, MKLIT, MKEQ, MKMINUS,
    MKDIVIDE, MKTIMES, MKAND, MKOR, MKLESSTHAN, MKRETURNTYPE, MKPROGRAM, MKPRINT,
    MKNEG, MKPLUS, MKEXP,

    //Error types for symantic analyzer
    PRIMITIVEFN, PRIMITIVEPARAM, NOMAIN, INTOPERROR, BOOLOPERROR, NULLOPERAND, RETURNTYPEERROR;

    public int value() {
        switch (this) {
            case LEFTPAREN: {return 40;}
            case RIGHTPAREN: {return 41;}
            case COMMA: {return 44;}
            case COLON: {return 58;}
            case MINUS: {return 45;}
            case GREATERTHAN: {return 62;}
            case PLUS: {return 43;}
            case TIMES: {return 42;}
            case DIVIDE: {return 47;}
            case LESSTHAN: {return 60;}
            case EQUALS: {return 61;}
            case UNDERSCORE: {return 95;}
        }   return -1;
    }

    public static Lexicon toType(Integer _int) {
        switch (_int) {
            case 40: {return LEFTPAREN;}
            case 41: {return RIGHTPAREN;}
            case 44: {return COMMA;}
            case 58: {return COLON;}
            case 45: {return MINUS;}
            case 62: {return RETURN;}
            case 43: {return PLUS;}
            case 42: {return TIMES;}
            case 47: {return DIVIDE;}
            case 60: {return LESSTHAN;}
            case 61: {return EQUIVALENT;}
            case 95: {return ID;}
        }   return $;
    }

    public static ArrayList<Lexicon> toArray(Lexicon ... types) {
        ArrayList<Lexicon> list = new ArrayList<>();
        for (Lexicon type : types) {
            list.add(type);
        }
        return list;
    }

    public static boolean compareList(int[] intLst, ArrayList<Integer>intArray) {        
        if (intLst.length==intArray.size()) {
            for (int i : intLst) {
                if (!intArray.contains(i)) {
                    return false;
                }
            } return true;
        } else return false;
    }

// Used in state 1 to create various tokens of the language.
    public static ConcurrentHashMap<Lexicon, int[]> getMap() {
        ConcurrentHashMap<Lexicon, int[]> map = new ConcurrentHashMap<>();
        map.put(Lexicon.INTEGER, new int[]{105,110,116,101,103,101,114});
        map.put(Lexicon.BOOLEAN, new int[]{98,111,111,108,101,97,110});
        map.put(Lexicon.IF, new int[]{105,102});
        map.put(Lexicon.ELSE, new int[]{101,108,115,101});
        map.put(Lexicon.NOT, new int[]{110,111,116});
        map.put(Lexicon.AND, new int[]{97,110,100});
        map.put(Lexicon.FN, new int[]{102,110});
        map.put(Lexicon.OR, new int[]{111,114});
        map.put(Lexicon.TRUE, new int[]{116,114,117,101});
        map.put(Lexicon.FALSE, new int[]{102,97,108,115,101});
        map.put(Lexicon.COMMA, new int[]{44});
        map.put(Lexicon.COLON, new int[]{58});
        map.put(Lexicon.RETURN, new int[]{45,62});
        map.put(Lexicon.PLUS, new int[]{43});
        map.put(Lexicon.MINUS, new int[]{45});
        map.put(Lexicon.TIMES, new int[]{42});
        map.put(Lexicon.DIVIDE, new int[]{47});
        map.put(Lexicon.LESSTHAN, new int[]{60});
        map.put(Lexicon.EQUIVALENT, new int[]{61,61});
        return map;
    }

// Used for getting integer arrays of keywords in parsing process.
    public static int[] getPrint() {return new int[]{112,114,105,110,116};}
    public static int[] getIf() {return new int[]{105,102};}

// Used for checking if a parse stack value, of type lexical, adheres to a particular set of rules.
    public static ArrayList<Lexicon> isTerminal() {
        return toArray(FN,LEFTPAREN,RIGHTPAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,
                       OR,PLUS,MINUS,AND,TIMES,DIVIDE,NOT,IF,ELSE,ID,BOOLEANLITERAL,INTEGERLITERAL);
    }

    public static ArrayList<Lexicon> isBooleanOp() {return toArray(AND,OR,EQUIVALENT);}

    public static ArrayList<Lexicon> isNumericOp() {return toArray(PLUS,MINUS,TIMES,DIVIDE,LESSTHAN,EQUIVALENT);}

    public static ArrayList<Lexicon> isOperator() {return toArray(PLUS,MINUS,TIMES,DIVIDE,AND,OR,EQUIVALENT,LESSTHAN);}

    public static ArrayList<Lexicon> isExpression() {return toArray(LEFTPAREN,MINUS,NOT,IF,ID,BOOLEANLITERAL,INTEGERLITERAL);}
    
    public static ArrayList<Lexicon> isExpTail() {
        return toArray(FN,LEFTPAREN,RIGHTPAREN,COMMA,OR,PLUS,MINUS,AND,TIMES,DIVIDE,NOT,IF,ELSE,ID,BOOLEANLITERAL,INTEGERLITERAL,$);
    }

    public static ArrayList<Lexicon> isSimpleExpTail() {
        return toArray(FN,LEFTPAREN,RIGHTPAREN,COMMA,AND,TIMES,DIVIDE,NOT,IF,ELSE,ID,BOOLEANLITERAL,INTEGERLITERAL,EQUIVALENT,LESSTHAN,$);
    }

    public static ArrayList<Lexicon> isTermTail() {
        return toArray(FN,LEFTPAREN,RIGHTPAREN,COMMA,OR,PLUS,MINUS,NOT,IF,ELSE,ID,BOOLEANLITERAL,INTEGERLITERAL,EQUIVALENT,LESSTHAN,$);
    }

    public static ArrayList<Lexicon> isArgList() {
        return toArray(FN,RIGHTPAREN,COMMA,EQUIVALENT,LESSTHAN,OR,PLUS,MINUS,AND,TIMES,DIVIDE,NOT,IF,ELSE,ID,BOOLEANLITERAL,INTEGERLITERAL,$);
    }

// Rules of the parse table that get pushed on to the parse stack.
    public static ArrayList<Lexicon> definitionRules() {return toArray(MKFN,BODY,MKRETURNTYPE,TYPE,RETURN,RIGHTPAREN,PARAMLIST,LEFTPAREN,MKID,ID,FN);}
    public static ArrayList<Lexicon> printExpRules() {return toArray(MKFNCALL,RIGHTPAREN,EXP,LEFTPAREN,MKARGS,MKID,ID);}
    public static ArrayList<Lexicon> formalParamRules() {return toArray(FORMALPARAMTAIL,MKPARAM,IDWITHTYPE);}
    public static ArrayList<Lexicon> formalParamTailRules() {return toArray(FORMALPARAM,COMMA);}
    public static ArrayList<Lexicon> idWithTypeRules() {return toArray(MKTYPE,TYPE,COLON,MKID,ID);}
    public static ArrayList<Lexicon> expRules() {return toArray(EXPTAIL,SIMPLEEXP);}
    public static ArrayList<Lexicon> equivalentRules() {return toArray(MKEQ,EXP,EQUIVALENT);}
    public static ArrayList<Lexicon> lessThanRules() {return toArray(MKLESSTHAN,EXP,LESSTHAN);}
    public static ArrayList<Lexicon> simpleExpRules() {return toArray(SIMPLEEXPTAIL,TERM);}
    public static ArrayList<Lexicon> orRules() {return toArray(MKOR,SIMPLEEXP,OR);}
    public static ArrayList<Lexicon> plusRules() {return toArray(MKPLUS,SIMPLEEXP,PLUS);}
    public static ArrayList<Lexicon> minusRules() {return toArray(MKMINUS,SIMPLEEXP,MINUS);}
    public static ArrayList<Lexicon> termRules() {return toArray(TERMTAIL,FACTOR);}
    public static ArrayList<Lexicon> andRules() {return toArray(MKAND,TERM,AND);}
    public static ArrayList<Lexicon> timesRules() {return toArray(MKTIMES,TERM,TIMES);}
    public static ArrayList<Lexicon> divideRules() {return toArray(MKDIVIDE,TERM,DIVIDE);}
    public static ArrayList<Lexicon> leftParenFactorRules() {return toArray(RIGHTPAREN,MKEXP,EXP,LEFTPAREN);}
    public static ArrayList<Lexicon> ifRules() {return toArray(MKIF,EXP,ELSE,EXP,RIGHTPAREN,EXP,LEFTPAREN,IF);}
    public static ArrayList<Lexicon> idRules() {return toArray(ARGLIST,MKID,ID);}
    public static ArrayList<Lexicon> leftParenArgRules() {return toArray(MKFNCALL,RIGHTPAREN,FORMALARG,LEFTPAREN,MKARGS);}
    public static ArrayList<Lexicon> formalArgRules() {return toArray(FORMALARGTAIL,EXP);}
    public static ArrayList<Lexicon> commaRules() {return toArray(FORMALARG,COMMA);}
    public static ArrayList<Lexicon> integerLitRules() {return toArray(MKLIT,INTEGERLITERAL);}
    public static ArrayList<Lexicon> booleanLitRules() {return toArray(MKLIT,BOOLEANLITERAL);}
    public static ArrayList<Lexicon> minusFactorRules() {return toArray(MKNEG,FACTOR,MINUS);}

// Methods for parse table rules whos terminal values signal an error. 
    public static ArrayList<Lexicon> definitionListError() {
        return toArray(LEFTPAREN,RIGHTPAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,MINUS,AND,TIMES,DIVIDE,NOT,
                       IF,ELSE,ID,BOOLEANLITERAL,INTEGERLITERAL);
    }

    public static ArrayList<Lexicon> definitionError() {
        return toArray(LEFTPAREN,RIGHTPAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,MINUS,TIMES,DIVIDE,NOT,IF,
                       ELSE,ID,BOOLEANLITERAL,INTEGERLITERAL,$);
    }

    public static ArrayList<Lexicon> parameterListError() {
        return toArray(FN,LEFTPAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,MINUS,TIMES,DIVIDE,NOT,IF,ELSE,
                       BOOLEANLITERAL,INTEGERLITERAL,$);
    }

    public static ArrayList<Lexicon> formalParamError() {
        return toArray(FN,LEFTPAREN,RIGHTPAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,MINUS,TIMES,DIVIDE,NOT,
                       IF,ELSE,BOOLEANLITERAL,INTEGERLITERAL,$);
    }

    public static ArrayList<Lexicon> arugmentListError() {return toArray(RETURN,COLON,INTEGER,BOOLEAN,ID);}

    public static ArrayList<Lexicon> bodyError() {return toArray(FN,RIGHTPAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,AND,TIMES,DIVIDE,ELSE,$);}

    public static ArrayList<Lexicon> expressionTailError() {return toArray(RETURN,COLON,INTEGER,BOOLEAN,ID);}

    public static ArrayList<Lexicon> simpleExpTailError() {return toArray(RETURN,COLON,INTEGER,BOOLEAN,ID);}

    public static ArrayList<Lexicon> termTailError() {return toArray(RETURN,COLON,INTEGER,BOOLEAN,ID);}

    public static ArrayList<Lexicon> expressionError() {
        return toArray(FN,RIGHTPAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,AND,TIMES,DIVIDE,ELSE);
    }

    public static ArrayList<Lexicon> simpleExpError() {
        return toArray(FN,RIGHTPAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,AND,TIMES,DIVIDE,ELSE,$);
    }

    public static ArrayList<Lexicon> termError() {
        return toArray(FN,RIGHTPAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,AND,TIMES,DIVIDE,ELSE,$);
    }

    public static ArrayList<Lexicon> factorError() {
        return toArray(FN,RIGHTPAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,AND,TIMES,DIVIDE,ELSE,$);
    }

    public static ArrayList<Lexicon> formalArgsError() {
        return toArray(FN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,AND,TIMES,DIVIDE,ELSE,$);
    }

    public static ArrayList<Lexicon> formalArgsTailError() {
        return toArray(FN,LEFTPAREN,RETURN,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,MINUS,AND,TIMES,DIVIDE,NOT,IF,ELSE,ID,BOOLEANLITERAL,INTEGERLITERAL,$);
    }

    public static ArrayList<Lexicon> literalError() {
        return toArray(FN,LEFTPAREN,RIGHTPAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,MINUS,AND,TIMES,DIVIDE,NOT,IF,ELSE,ID,$);
    }

    public static ArrayList<Lexicon> printExpError() {
        return toArray(FN,LEFTPAREN,RIGHTPAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,MINUS,AND,TIMES,
                       DIVIDE,NOT,IF,ELSE,BOOLEANLITERAL,INTEGERLITERAL,$);
    }

    public static ArrayList<Lexicon> programError() {
        return toArray(LEFTPAREN,RIGHTPAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,MINUS,AND,TIMES,DIVIDE,
                       NOT,IF,ELSE,ID,BOOLEANLITERAL,INTEGERLITERAL);
    }

    public static ArrayList<Lexicon> typeError() {
        return toArray(FN,LEFTPAREN,RIGHTPAREN,RETURN,COMMA,COLON,EQUIVALENT,LESSTHAN,OR,PLUS,MINUS,AND,TIMES,DIVIDE,NOT,IF,ELSE,ID,BOOLEANLITERAL,INTEGERLITERAL,$);
    }

    public static ArrayList<Lexicon> formalParamTailError() {
        return toArray(FN,LEFTPAREN,RETURN,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,MINUS,AND,TIMES,DIVIDE,NOT,IF,ELSE,ID,BOOLEANLITERAL,INTEGERLITERAL,$);
    }

    public static ArrayList<Lexicon> idWithTypeError() {
        return toArray(FN,LEFTPAREN,RIGHTPAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,MINUS,AND,TIMES,
                       DIVIDE,NOT,IF,ELSE,BOOLEANLITERAL,INTEGERLITERAL,$);
    }
}