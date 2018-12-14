class MainClass {
    def main() : int {
        return 0;
    }
}

class Test1 {
    var i : int;

    def test1() : int {
        new Test1(); # error
        return 0;
    }
}