package src;
import java.util.Stack;

public class AstPrinter {

    private StringBuilder astString = new StringBuilder();
    private Stack<Node> helperStack = new Stack<>();
    private Stack<Node> nStack = new Stack<>();
    private int strDepth;

    public AstPrinter(Stack<Node> stack) {
        nStack = stack;
    }

    public String toString() {

        while (!nStack.empty()) {

            Node node = nStack.pop();
            switch (node.valueOf()) {
                case PROGRAM: {
                    if (node.getProgram().getFunctions().isEmpty()) {return "";}
                    int size = node.getProgram().getFunctions().size()-1;
                    for (int dex = size; dex >= 0; dex--) {
                        nStack.push(node.getProgram().getFunctions().get(dex));
                    }
                    break;
                }
                case FN: {
                    strDepth = 6;
                    astString.append("\n");
                    astString.append("function\n   "+node.getFn().getName()+"\n   parameters "+
                    node.getFn().getParameters()+"\n   "+node.getFn().getNodeType().getType()+"\n   body\n");
                    int size = node.getFn().getBodyNodes().size()-1;
                    for (int dex = size; dex >= 0; dex--) {
                        nStack.push(node.getFn().getBodyNodes().remove(dex));
                    }
                    break;
                }
                case FNCALL: {
                    nStack.push(new Node());
                    String fnName = node.getFnCall().getName().toString().replace("identifier ", "");
                    if (node.getFnCall().getId().isNegative()) {
                        astString.append(formatStr(strDepth, "(neg) function_call "+"\n   "+fnName+"\n   "+"args"));
                    }
                    else {astString.append(formatStr(strDepth, "function_call "+"\n   "+fnName+"\n   "+"args"));}
                    int size = node.getFnCall().getArgs().size()-1;
                    for (int dex = size; dex >= 0; dex--) {
                        nStack.push(node.getFnCall().getArgs().remove(dex));
                    }
                    strDepth += 6;
                    break;
                }
                case EXP: {
                    if (node.getExp().symbol==Lexicon.MINUS) {
                        astString.append(formatStr(strDepth, "(neg) expression"));
                    }
                    else {astString.append(formatStr(strDepth, "expression"));}
                    strDepth += 3;
                    helperStack.push(node.getExp().getNode());
                    astString.append(formatStr(strDepth, new AstPrinter(helperStack).toString()));
                    strDepth -= 3;
                    break;
                }
                case IF: {
                    astString.append(formatStr(strDepth, "if"));
                    strDepth += 3;
                    helperStack.push(node.getIfState().getIf());
                    astString.append(formatStr(strDepth, new AstPrinter(helperStack).toString()));
                    strDepth -= 3;
                    astString.append(formatStr(strDepth, "then"));
                    strDepth += 3;
                    helperStack.push(node.getIfState().getThen());
                    astString.append(formatStr(strDepth, new AstPrinter(helperStack).toString()));
                    strDepth -= 3;
                    astString.append(formatStr(strDepth, "else"));
                    strDepth += 3;
                    helperStack.push(node.getIfState().getElse());
                    astString.append(formatStr(strDepth, new AstPrinter(helperStack).toString()));
                    strDepth -= 3;
                    break;
                }
                case DIVIDE: {
                    astString.append(formatStr(strDepth, "operator /"));
                    strDepth += 3;
                    helperStack.push(node.getDivide().getLeftNode());
                    astString.append(formatStr(strDepth, "left "+new AstPrinter(helperStack)));
                    helperStack.push(node.getDivide().getRightNode());
                    astString.append(formatStr(strDepth, "right "+new AstPrinter(helperStack)));
                    helperStack = new Stack<>();
                    strDepth -= 3;
                    break;
                }
                case MINUS: {
                    astString.append(formatStr(strDepth, "operator -"));
                    strDepth += 3;
                    helperStack.push(node.getMinus().getLeftNode());
                    astString.append(formatStr(strDepth, "left "+new AstPrinter(helperStack)));
                    helperStack.push(node.getMinus().getRightNode());
                    astString.append(formatStr(strDepth, "right "+new AstPrinter(helperStack)));
                    strDepth -= 3;
                    helperStack = new Stack<>();
                    break;
                }
                case PLUS: {
                    astString.append(formatStr(strDepth, "operator +"));
                    strDepth += 3;
                    helperStack.push(node.getPlus().getLeftNode());
                    astString.append(formatStr(strDepth, "left "+new AstPrinter(helperStack)));
                    helperStack.push(node.getPlus().getRightNode());
                    astString.append(formatStr(strDepth, "right "+new AstPrinter(helperStack)));
                    helperStack = new Stack<>();
                    strDepth -= 3;
                    break;
                }
                case TIMES: {
                    astString.append(formatStr(strDepth, "operator *"));
                    strDepth += 3;
                    helperStack.push(node.getTimes().getLeftNode());
                    astString.append(formatStr(strDepth, "left "+new AstPrinter(helperStack)));
                    helperStack.push(node.getTimes().getRightNode());
                    astString.append(formatStr(strDepth, "right "+new AstPrinter(helperStack)));
                    helperStack = new Stack<>();
                    strDepth -= 3;
                    break;
                }
                case EQUIVALENT: {
                    astString.append(formatStr(strDepth, "operator =="));
                    strDepth += 3;
                    helperStack.push(node.getEquality().getLeftNode());
                    astString.append(formatStr(strDepth, "left "+new AstPrinter(helperStack)));
                    helperStack.push(node.getEquality().getRightNode());
                    astString.append(formatStr(strDepth, "right "+new AstPrinter(helperStack)));
                    helperStack = new Stack<>();
                    strDepth -= 3;
                    break;
                }
                case OR: {
                    astString.append(formatStr(strDepth, "operator or"));
                    strDepth += 3;
                    helperStack.push(node.getOr().getLeftNode());
                    astString.append(formatStr(strDepth, "left "+new AstPrinter(helperStack)));
                    helperStack.push(node.getOr().getRightNode());
                    astString.append(formatStr(strDepth, "right "+new AstPrinter(helperStack)));
                    helperStack = new Stack<>();
                    strDepth -= 3;
                    break;
                }
                case AND: {
                    astString.append(formatStr(strDepth, "operator and"));
                    strDepth += 3;
                    helperStack.push(node.getAnd().getLeftNode());
                    astString.append(formatStr(strDepth, "left "+new AstPrinter(helperStack)));
                    helperStack.push(node.getAnd().getRightNode());
                    astString.append(formatStr(strDepth, "right "+new AstPrinter(helperStack)));
                    helperStack = new Stack<>();
                    strDepth -= 3;
                    break;
                }
                case LESSTHAN: {
                    astString.append(formatStr(strDepth, "operator <"));
                    strDepth += 3;
                    helperStack.push(node.getLessThan().getLeftNode());
                    astString.append(formatStr(strDepth, "left "+new AstPrinter(helperStack)));
                    helperStack.push(node.getLessThan().getRightNode());
                    astString.append(formatStr(strDepth, "right "+new AstPrinter(helperStack)));
                    helperStack = new Stack<>();
                    strDepth -= 3;
                    break;
                }
                case ID: {
                    astString.append(formatStr(strDepth, node.getId().toString()));
                    break;
                }
                case LITERAL: {
                    astString.append(formatStr(strDepth, node.getLiteral().toString()));
                    break;
                }
                case $: {strDepth -= 6;}
                default: {break;}
            }
        }
        return astString.toString();
    }

    private String formatStr(int space, String string) {
        StringBuilder builder = new StringBuilder();
        String[] strArray = string.split("\n");
        for (String str : strArray) {
            for (int _int = 0; _int < space; _int++) {
                str = " "+str;
            }
            builder.append(str+"\n");
        }
        return builder.toString();
    }
}
