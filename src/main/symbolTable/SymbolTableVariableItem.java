package symbolTable;

import ast.Type.Type;

public class SymbolTableVariableItem extends SymbolTableVariableItemBase {

    public SymbolTableVariableItem(String name, Type type) {
        super(name, type, -1);
    }

    public SymbolTableVariableItem(String name, Type type, int index) {
        super(name, type, index);
    }
}
