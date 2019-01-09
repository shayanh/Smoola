package ast.node.expression;

public enum BinaryOperator {
    add, sub, mult, div, and, or, eq, neq, lt, gt, assign;

    private String instruction;

    static {
        add.instruction = "iadd";
        sub.instruction = "isub";
        mult.instruction = "imul";
        div.instruction = "idiv";
        and.instruction = "ifeq";
        or.instruction = "ifeq";
        eq.instruction = "if_icmpeq";
        neq.instruction = "if_icmpne";
        lt.instruction = "if_icmplt";
        gt.instruction = "if_icmpgt";
        assign.instruction = "";
    }

    public String getInstruction() {
        return instruction;
    }
}
