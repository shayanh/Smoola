class MainClass {
    def main() : int {
        writeln((new Class1()).testMethod("hi there!"));
        return 0;
    }
}

class Class1 {
    def testMethod(s: string) : string {
        return s;
    }
}