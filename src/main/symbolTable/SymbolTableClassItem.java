package symbolTable;

public class SymbolTableClassItem extends SymbolTableItem {

    public SymbolTableClassItem(String name) {
        this.name = name;
    }

    public String getKey() {
        return name;
    }
}
