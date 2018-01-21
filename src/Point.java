
public class Point implements Comparable<Point>{
	
	public int x, y;
	
	public Point(){
		
		x = 0;
		y = 0;
	}

	@Override
	public int compareTo(Point o) {
		
		if(this == null)
			return -1;
		
		if(o == null)
			return 1;
		
		if(this.x == o.x)
			  return this.y - o.y;
			  
		return this.x - o.x;
	}
	

}
