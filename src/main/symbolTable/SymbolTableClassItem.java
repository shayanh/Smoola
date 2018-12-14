package symbolTable;

public class SymbolTableClassItem extends SymbolTableItem {

    public SymbolTableClassItem(String name) {
        this.name = name;
    }

    @Override
    public String getKey() {
        return name;
    }
}
