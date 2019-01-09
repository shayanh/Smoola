class MainClass{
  def main(): int{
      #writeln(new ThirdClass().m());
      new SecondClass().method();
      return 0;
  }
 }

 class SecondClass{

   var arr : int[];
   var arr_size : int;
   var temp : int;
   def method(): int{
    arr = new int[10];
    arr[0] = 12;
    arr[1] = 11;
    arr[2] = 13;
    arr[3] = 5;
    arr[4] = 6;
    arr[5] = 7;
    arr_size = 6;
    writeln("Given array is");
    temp = this.printArray(arr, arr_size);
    arr = this.mergeSort(arr, 0, arr_size - 1);
    writeln("Sorted array is");
    temp = this.printArray(arr, arr_size);
    return 0;
   }
   def printArray(ar : int[], size : int) : int {
        var i : int;
        i = 0;
        while( i < size) {
            writeln(ar[i]);
            i = i + 1;
        }
        return 0;
   }
   def mergeSort(array : int[], l : int, r : int) : int[]{
        var m : int;
        if (l < r) then
        {
            m = l+(r-l)/2;

            array = this.mergeSort(array, l, m);
            array = this.mergeSort(array, m+1, r);
            array = this.merge(array, l, m, r);
        }
        return array;
   }
   def merge(array : int[], l : int, m : int, r : int) : int[]{
        var i : int;
        var j : int;
        var k : int;
        var n1 : int;
        var n2 : int;
        var L : int[];
        var R : int[];

        n1 = m - l + 1;
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
            R[j] = arr[m + 1+ j];
            j = j + 1;
        }
        i = 0;
        j = 0;
        k = l;
        while (i < n1 && j < n2){
            if (L[i] < R[j]+1) then
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
        while (j < n2)
        {
            array[k] = R[j];
            j = j + 1;
            k = k + 1;
        }
        return array;
   }
 }
