/** 
 * VisualFoV: overlapping and occlusion tool for visual sensors
 * Developed by Daniel G. Costa
 * Version October 2023
 * 
 * This is a target implemented as a dot(tx,ty)
*/

class Target
{
	private int ID;
	
	//(x,y) position of the target
	private int tx;
	private int ty;

	//The number of times the Target was viewed
	private int numberViewed;

	private boolean viewed=false;
	
	public Target (int id, int x, int y)
	{
		//Initial configuration
		ID = id;
	
		tx = x;
		ty = y;

		numberViewed=0;
	}

	public int getID()
	{
		return ID;
	}

	public int getTx()
	{
		return tx;
	}
	public int getTy()
	{
		return ty;
	}

	public void addView()
	{
		numberViewed++;
	}
	public int getNumberViews()
	{
		return numberViewed;
	}

	public void setViewed()
	{
		viewed = true;
		addView(); //Add an additional viewe
	}
	public boolean isViewed()
	{
		return viewed;
	}
}


