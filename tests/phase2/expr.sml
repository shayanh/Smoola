class Test {
  def main() : int {
      a = b = c = d = e;
      a = b * d;
      a = b || c || d;
      a = (b || c) || d;
      a = (b || !c) * d || e;
      a = b = c + d + e * f = !d = 2 + !g = e = t + r;
      a = i = i + i + (3 * !4 / 6 + 3);
      a = b && c / d * e * r + t - 1 * (!1 + (1 * 2 * 3));
      a = !b + ((d * 1 + (1 * 2 * 3) * 3 && -c || 3) * 4 );
      a = ((c + e) || (e + r) && (c || r));
      a = r * 4 / 3 + k * (a + b) * ((c + d) * d || d * (4 * 3));
      a = a < 3 > 5 > 4 = 3 < 4 + -5 < 3 + -c - 3 + 2 + 2 - 3 * 3;
      return 0;
  }
}