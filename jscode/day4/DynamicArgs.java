
public class DynamicArgs {
    static int args(int... x) {
    	int temp=0;
    	for(int value : x) {
    		temp+=value;
    	}
    	return temp;
    }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
      
      int v1=args(1,2,3,4,5);
      int v2=args(1,2,3,4,5,6,7,8,9,10);
      System.out.printf("v1=%d v2=%d", v1,v2);
	}

}
