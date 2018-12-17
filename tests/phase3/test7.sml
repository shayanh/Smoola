class MainClass {
    def main() : int {
        return 0;
    }
}

class A extends B {
    def test1() : int {
        return 1;
    }
}

class B extends C {
    def test2() : int {
        return 2;
    }
}

class C extends B {
    def test3() : int {
        return 3;
    }
}