class MainClass {
    def main() : int {
        writeln((new Test()).method2());
        return 0;
    }
}

class Test {
    def method1() : int[] {
        var a: int[];
        a = new int[10];
        return a;
    }

    def method2() : int {
        var t: int[];
        t = new int[10];
        t[1] = 3;
        return 0;
    }
}