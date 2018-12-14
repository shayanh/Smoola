class MainClass {
    def main(): int {
        return new Test2().method2();
    }
}

class Test1 {
    var i: int;
    def method1(): string {
        var j: string;
        j = "hello world!";
        return j;
    }
}

class Test2 extends Test1 {
    def method2() : int {
        i = 10;
        return 1;
    }
}