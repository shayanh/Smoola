class MainClass{
  def main(): int{
      #writeln(new ThirdClass().m());
      new SecondClass().method();
      return 0;
  }
 }

 class SecondClass{

   var arr_size : int;
   var array : int[];
   var array2 : int[];
   var temp : int;
   def method(): int{
    array = new int[6];
    array2 = new int[6];
    array[0] = 12;
    array[1] = 11;
    array[2] = 13;
    array[3] = 5;
    array[4] = 6;
    array[5] = 7;
    arr_size = array.length;
    writeln("Given array is");
    temp = this.printArray(arr_size);
    array2 = this.mergeSort(0, array.length - 1);
    writeln("Sorted array is");
    temp = this.printArray(arr_size);
    return 0;
   }
   def printArray(size : int) : int {
        var i : int;
        i = 0;
        while( i < size) {
            writeln(array[i]);
            i = i + 1;
        }
        return 0;
   }
   def mergeSort(l : int, r : int) : int[]{
        var m : int;
        var x : int;
        var array2 : int[];
        array2 = new int[6];
        if (l < r) then
        {
            m = l+(r-l)/2;


            array2 = this.mergeSort(l, m);


            array2 = this.mergeSort(m+1, r);
            array2 = this.merge(l, m, r);
        }
        return array2;
   }
   def merge(l : int, m : int, r : int) : int[]{
        var i : int;
        var j : int;
        var k : int;
        var n1 : int;
        var n2 : int;
        var L : int[];
        var R : int[];
        var x : int;

        n1 = (m - l) + 1;
        n2 = r - m;

        i = 0;
        j = 0;

        L = new int[10];
        R = new int[10];

        while(i < n1){
            L[i] = array[l + i];
            i = i+1;
        }
        while(j < n2){
            R[j] = array[m + 1+ j];
            j = j + 1;
        }
        i = 0;
        j = 0;
        k = l;
        while (i < n1 && j < n2){
            if (L[i] < (R[j]+1)) then
            {
                array[k] = L[i];
                i = i + 1;
            }
            else
            {
                array[k] = R[j];
                j = j + 1;
            }
            k = k + 1;
        }
        while (i < n1)
        {
            array[k] = L[i];
            i = i + 1;
            k = k + 1;
        }

         while (j < n2)
        {
            array[k] = R[j];
            j = j + 1;
            k = k + 1;
        }
        return array2;
   }
 }
