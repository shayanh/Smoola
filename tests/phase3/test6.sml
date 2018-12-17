class MainClass {
    def main() : int {
        new Test1().testMethod();
        return 0;
    }
}

class Test1 {
    def testMethod() : int {
        var res: int;
        res = 2;
        if (new Test2().check()) then {
            res = 1;
        }
        return res;
    }

    def test2() : Test2 {
        return new Test2();
    }
}

class Test2 {
    def check() : boolean {
        new Test1().testMethod();
        return false;
    }
}