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
}

class Test2 {
    def check() : boolean {
        return false;
    }
}