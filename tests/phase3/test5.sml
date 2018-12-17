class MainClass {
    def main() : int {
        writeln(new Test8().toString());
        writeln(new NotDeclaredClass3().toString()); # error
        writeln(new NotDeclaredClass3()); # error
        return 0;
    }
}

class Test1 {
    var a: Test1;

    def method1() : NotDeclaredClass0 { # error
        var b: int;
        this = 5; # error

        return 0; # error
    }
}

class Test2 extends NotDeclaredClass1 { # error
    var a: int;
    var b: string;
    var c: NotDeclaredClass2; # error

    def method2(t1: int, t2: int[], t3: Test3, t4: Object, t5: NotDeclaredClass4) : int { # error
        this = false; # error
        return 0;
    }
}

class Test3 {
    var i: int;
    var arr: int[];
    var self: t3;   # error

    def method3() : Test1 {
        return new Test3(); # error
    }
    def method4() : int {
        return false; # error
    }
    def method5() : Object {
        return new Test3();
    }
    def method6() : Test3 {
        return new Object(); # error
    }
    def method12() : Test4 {
        return new Test4();
    }

    def testThis() : int {
        var a : Test4;
        a = new Test4();
        this = new Test3();
        this = new Test5();
        this = new Test4() = new Test5();
        # this.i = this.arr.length;
        # this.arr = 4; # error

        a = this.method12();
        return 0;
    }
}

class Test4 extends Test3 {
    def method7() : Test3 {
        return new Test4();
    }
}

class Test5 extends Test4 {
    def method8() : Test3 {
        return new Test5();
    }
}

class Test7 {
    var obj: Object;
    var t3: Test3;
    var t4: Test4;
    var t5: Test5;

    def testMethodCall() : int {
        obj = t3.method5();
        obj = t4.method5();
        obj = t5.method5();
        obj = t5.method2(); # error

        obj = t5.method8().method12().method7();
        obj = t5.method8().method12().method8(); # error

        return 0;
    }

    def testMethodCallInMain() : int {
        t4 = new Test3().method12(); # error

        return 0;
    }
}

class Test6 {
    var obj: Object;
    var i : int;
    var j : int;
    var b: boolean;
    var s: string;
    var arr: int[];
    var t1: Test3;
    var t2: Test5;

    def testOp() : int {
        arr[5] = 3;
        arr[-1] = arr[2];
        arr[5] = false; # error
        arr["str"] = 2; # error

        arr[i + i + 10 - 3] = 10;
        i = 8 + 9889 + i;
        i = i + j;
        b = i; # error
        b = i > 10;
        b = b < false; # error
        i = b; # error
        i = arr[3];
        s = arr[3]; # error

        b = s == "shayan";
        b = (false == false);
        b = arr == arr;
        b = obj == obj;
        i = arr <> arr; # error

        b = true && b || false;
        s = b && false; # error
        b = true || i; # error

        obj = new Test1();
        t1 = obj; # error
        t1 = t2;
        t1 = new Test3();
        t2 = t1; # error
        b = t1 <> new Test3();
        b = t1 == t2;
        b = obj == obj;

        i = j = i = j;
        j = i = 4 / 5 * 10 + i;
        i = 5 = 4; # error
        i = i + j = 3 + 3; # error;
        !b = false; # error

        b = !b;
        i = -i;
        b = !i; # error

        return 1;
    }

    def testVarDecAndLength() : int {
        i = j;
        i = k; # error
        k = s; # error

        i = arr.length;
        i = (new int[10]).length;
        s = arr.length; # error
        arr.length = 4; # error

        return 2;
    }

    def testCond() : int {
        if (b && !b && -i > 10 || j <> 0) then {
            while (true || !false)
                i = 10;
        }
        if (i || i < 10 || s) then # error
            i = 10;
        if (i) then # error
            i = 5;

        return 3;
    }

    def testWriteln() : int {
        var t8: Test8;
        writeln(i);
        writeln(s);
        writeln(t8.toString());
        writeln(b); # error
        writeln(t1); # error

        return 4;
    }
}

class Test8 {
    def toString() : string {
        return "Test8";
    }
}

class Test9 {
    var arr: int[];
    var i: int;

    def testArr() : int {
        arr = new int[100];
        i = arr; #error

        return 5;
    }
}
