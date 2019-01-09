class MainClass {
    def main() : int {
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
        var t: Test;
        t = new Test();
        t.method1()[2] = 3;
        return 0;
    }
}