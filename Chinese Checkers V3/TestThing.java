public class TestThing {
  public static void main (String [] args) {
    
    int[][] array = new int[2][2];
    int count = 0;
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        count++;
        array[i][j] = count;
        System.out.println(array[i][j]);
      }
    }
    
    System.out.println(" ");
    array[0][0] = array[0][0];
    array[0][0] = 5;
    System.out.println(array[0][0]);
  }
}