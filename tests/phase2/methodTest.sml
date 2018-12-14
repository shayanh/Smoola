class Test1 {
  def main() : int {
    return 0;
  }
}

class Test2 extends Test1 {
  def main2() : int {
    a = b = c;
    a = new Test2().main().func().armit().length;
    if (!x && a > b && c > d) then {
        c = true;
    }
    if (true) then {
        x = y;
    } else {
        y = x;
    }
    return 0;
  }
}