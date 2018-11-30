package symbolTable;

import ast.Type.Type;

public class SymbolTableVariableItem extends SymbolTableVariableItemBase {

    private static int counter;

    public SymbolTableVariableItem(String name, Type type) {
        super(name, type, counter++);
    }
}
