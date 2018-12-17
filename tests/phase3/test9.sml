class Test2 {
    def main() : int {
        return 0;
    }
}

class Test1 {
    def test() : int {
        var a: int;
        a = new Test2().main();
        return 1;
    }
}