package src;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public enum Lex {

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
    PRIMITIVEFN, PRIMITIVEPARAM, NOMAIN, INTOPERROR, BOOLOPERROR, NULLOPERAND, RETURNTYPEERROR,
    DIFFOPERANDS;

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

    public static Lex toType(Integer _int) {
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
    public static ConcurrentHashMap<Lex, int[]> getMap() {
        ConcurrentHashMap<Lex, int[]> map = new ConcurrentHashMap<>();
        map.put(Lex.INTEGER, new int[]{105,110,116,101,103,101,114});
        map.put(Lex.BOOLEAN, new int[]{98,111,111,108,101,97,110});
        map.put(Lex.IF, new int[]{105,102});
        map.put(Lex.ELSE, new int[]{101,108,115,101});
        map.put(Lex.NOT, new int[]{110,111,116});
        map.put(Lex.AND, new int[]{97,110,100});
        map.put(Lex.FN, new int[]{102,110});
        map.put(Lex.OR, new int[]{111,114});
        map.put(Lex.TRUE, new int[]{116,114,117,101});
        map.put(Lex.FALSE, new int[]{102,97,108,115,101});
        map.put(Lex.COMMA, new int[]{44});
        map.put(Lex.COLON, new int[]{58});
        map.put(Lex.RETURN, new int[]{45,62});
        map.put(Lex.PLUS, new int[]{43});
        map.put(Lex.MINUS, new int[]{45});
        map.put(Lex.TIMES, new int[]{42});
        map.put(Lex.DIVIDE, new int[]{47});
        map.put(Lex.LESSTHAN, new int[]{60});
        map.put(Lex.EQUIVALENT, new int[]{61,61});
        return map;
    }

// Used for getting integer arrays of keywords in parsing process.
    public static int[] getPrint() {return new int[]{112,114,105,110,116};}
    public static int[] getIf() {return new int[]{105,102};}

// Used for checking if a parse stack value, of type lexical, adheres to a particular set of rules.
    public static Lex[] isTerminal() {
        return new Lex[]{FN,LEFTPAREN,RIGHTPAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,
                       OR,PLUS,MINUS,AND,TIMES,DIVIDE,NOT,IF,ELSE,ID,BOOLEANLITERAL,INTEGERLITERAL};
    }

    public static Lex[] isOperator() {return new Lex[]{PLUS,MINUS,TIMES,DIVIDE,AND,OR,EQUIVALENT,LESSTHAN};}

    public static Lex[] isExpression() {return new Lex[]{LEFTPAREN,MINUS,NOT,IF,ID,BOOLEANLITERAL,INTEGERLITERAL};}
    
    public static Lex[] isExpTail() {
        return new Lex[]{FN,LEFTPAREN,RIGHTPAREN,COMMA,OR,PLUS,MINUS,AND,TIMES,DIVIDE,NOT,IF,ELSE,ID,BOOLEANLITERAL,INTEGERLITERAL,$};
    }

    public static Lex[] isSimpleExpTail() {
        return new Lex[]{FN,LEFTPAREN,RIGHTPAREN,COMMA,AND,TIMES,DIVIDE,NOT,IF,ELSE,ID,BOOLEANLITERAL,INTEGERLITERAL,EQUIVALENT,LESSTHAN,$};
    }

    public static Lex[] isTermTail() {
        return new Lex[]{FN,LEFTPAREN,RIGHTPAREN,COMMA,OR,PLUS,MINUS,NOT,IF,ELSE,ID,BOOLEANLITERAL,INTEGERLITERAL,EQUIVALENT,LESSTHAN,$};
    }

    public static Lex[] isArgList() {
        return new Lex[]{FN,RIGHTPAREN,COMMA,EQUIVALENT,LESSTHAN,OR,PLUS,MINUS,AND,TIMES,DIVIDE,NOT,IF,ELSE,ID,BOOLEANLITERAL,INTEGERLITERAL,$};
    }

// Rules of the parse table that get pushed on to the parse stack. new Lex[]{
    public static Lex[] definitionRules() {return new Lex[]{MKFN,BODY,MKRETURNTYPE,TYPE,RETURN,RIGHTPAREN,PARAMLIST,LEFTPAREN,MKID,ID,FN};}
    public static Lex[] printExpRules() {return new Lex[]{MKFNCALL,RIGHTPAREN,EXP,LEFTPAREN,MKARGS,MKID,ID};}
    public static Lex[] formalParamRules() {return new Lex[]{FORMALPARAMTAIL,MKPARAM,IDWITHTYPE};}
    public static Lex[] formalParamTailRules() {return new Lex[]{FORMALPARAM,COMMA};}
    public static Lex[] idWithTypeRules() {return new Lex[]{MKTYPE,TYPE,COLON,MKID,ID};}
    public static Lex[] expRules() {return new Lex[]{EXPTAIL,SIMPLEEXP};}
    public static Lex[] equivalentRules() {return new Lex[]{MKEQ,EXP,EQUIVALENT};}
    public static Lex[] lessThanRules() {return new Lex[]{MKLESSTHAN,EXP,LESSTHAN};}
    public static Lex[] simpleExpRules() {return new Lex[]{SIMPLEEXPTAIL,TERM};}
    public static Lex[] orRules() {return new Lex[]{MKOR,SIMPLEEXP,OR};}
    public static Lex[] plusRules() {return new Lex[]{MKPLUS,SIMPLEEXP,PLUS};}
    public static Lex[] minusRules() {return new Lex[]{MKMINUS,SIMPLEEXP,MINUS};}
    public static Lex[] termRules() {return new Lex[]{TERMTAIL,FACTOR};}
    public static Lex[] andRules() {return new Lex[]{MKAND,TERM,AND};}
    public static Lex[] timesRules() {return new Lex[]{MKTIMES,TERM,TIMES};}
    public static Lex[] divideRules() {return new Lex[]{MKDIVIDE,TERM,DIVIDE};}
    public static Lex[] leftParenFactorRules() {return new Lex[]{RIGHTPAREN,MKEXP,EXP,LEFTPAREN};}
    public static Lex[] ifRules() {return new Lex[]{MKIF,EXP,ELSE,EXP,RIGHTPAREN,EXP,LEFTPAREN,IF};}
    public static Lex[] idRules() {return new Lex[]{ARGLIST,MKID,ID};}
    public static Lex[] leftParenArgRules() {return new Lex[]{MKFNCALL,RIGHTPAREN,FORMALARG,LEFTPAREN,MKARGS};}
    public static Lex[] formalArgRules() {return new Lex[]{FORMALARGTAIL,EXP};}
    public static Lex[] commaRules() {return new Lex[]{FORMALARG,COMMA};}
    public static Lex[] integerLitRules() {return new Lex[]{MKLIT,INTEGERLITERAL};}
    public static Lex[] booleanLitRules() {return new Lex[]{MKLIT,BOOLEANLITERAL};}
    public static Lex[] minusFactorRules() {return new Lex[]{MKNEG,FACTOR,MINUS};}

// Methods for parse table rules whos terminal values signal an error. 
    public static Lex[] definitionListError() {
        return new Lex[]{LEFTPAREN,RIGHTPAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,MINUS,AND,TIMES,DIVIDE,NOT,
                       IF,ELSE,ID,BOOLEANLITERAL,INTEGERLITERAL,$};
    }

    public static Lex[] definitionError() {
        return new Lex[]{LEFTPAREN,RIGHTPAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,MINUS,TIMES,DIVIDE,NOT,IF,
                       ELSE,ID,BOOLEANLITERAL,INTEGERLITERAL,$};
    }

    public static Lex[] parameterListError() {
        return new Lex[]{FN,LEFTPAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,MINUS,TIMES,DIVIDE,NOT,IF,ELSE,
                       BOOLEANLITERAL,INTEGERLITERAL,$};
    }

    public static Lex[] formalParamError() {
        return new Lex[]{FN,LEFTPAREN,RIGHTPAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,MINUS,TIMES,DIVIDE,NOT,
                       IF,ELSE,BOOLEANLITERAL,INTEGERLITERAL,$};
    }

    public static Lex[] arugmentListError() {return new Lex[]{RETURN,COLON,INTEGER,BOOLEAN,ID};}

    public static Lex[] bodyError() {return new Lex[]{FN,RIGHTPAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,AND,TIMES,DIVIDE,ELSE,$};}

    public static Lex[] expressionTailError() {return new Lex[]{RETURN,COLON,INTEGER,BOOLEAN,ID};}

    public static Lex[] simpleExpTailError() {return new Lex[]{RETURN,COLON,INTEGER,BOOLEAN,ID};}

    public static Lex[] termTailError() {return new Lex[]{RETURN,COLON,INTEGER,BOOLEAN,ID};}

    public static Lex[] expressionError() {
        return new Lex[]{FN,RIGHTPAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,AND,TIMES,DIVIDE,ELSE};
    }

    public static Lex[] simpleExpError() {
        return new Lex[]{FN,RIGHTPAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,AND,TIMES,DIVIDE,ELSE,$};
    }

    public static Lex[] termError() {
        return new Lex[]{FN,RIGHTPAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,AND,TIMES,DIVIDE,ELSE,$};
    }

    public static Lex[] factorError() {
        return new Lex[]{FN,RIGHTPAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,AND,TIMES,DIVIDE,ELSE,$};
    }

    public static Lex[] formalArgsError() {
        return new Lex[]{FN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,AND,TIMES,DIVIDE,ELSE,$};
    }

    public static Lex[] formalArgsTailError() {
        return new Lex[]{FN,LEFTPAREN,RETURN,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,MINUS,AND,TIMES,DIVIDE,NOT,IF,ELSE,ID,BOOLEANLITERAL,INTEGERLITERAL,$};
    }

    public static Lex[] literalError() {
        return new Lex[]{FN,LEFTPAREN,RIGHTPAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,MINUS,AND,TIMES,DIVIDE,NOT,IF,ELSE,ID,$};
    }

    public static Lex[] printExpError() {
        return new Lex[]{FN,LEFTPAREN,RIGHTPAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,MINUS,AND,TIMES,
                       DIVIDE,NOT,IF,ELSE,BOOLEANLITERAL,INTEGERLITERAL,$};
    }

    public static Lex[] programError() {
        return new Lex[]{LEFTPAREN,RIGHTPAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,MINUS,AND,TIMES,DIVIDE,
                       NOT,IF,ELSE,ID,BOOLEANLITERAL,INTEGERLITERAL};
    }

    public static Lex[] typeError() {
        return new Lex[]{FN,LEFTPAREN,RIGHTPAREN,RETURN,COMMA,COLON,EQUIVALENT,LESSTHAN,OR,PLUS,MINUS,AND,TIMES,DIVIDE,NOT,IF,ELSE,ID,BOOLEANLITERAL,INTEGERLITERAL,$};
    }

    public static Lex[] formalParamTailError() {
        return new Lex[]{FN,LEFTPAREN,RETURN,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,MINUS,AND,TIMES,DIVIDE,NOT,IF,ELSE,ID,BOOLEANLITERAL,INTEGERLITERAL,$};
    }

    public static Lex[] idWithTypeError() {
        return new Lex[]{FN,LEFTPAREN,RIGHTPAREN,RETURN,COMMA,COLON,INTEGER,BOOLEAN,EQUIVALENT,LESSTHAN,OR,PLUS,MINUS,AND,TIMES,
                       DIVIDE,NOT,IF,ELSE,BOOLEANLITERAL,INTEGERLITERAL,$};
    }
}