import java.awt.*;
import java.util.*;

public class Squig {
	Graphics g;
	DrawingPanel panel;
	Random r;
	
	//These variables are all "starting values" of the Squig. They'll change as the code executes and "flippers" are activated.
	int xSize; 
	int ySize;
	int xCursor;
	int yCursor;
	int rVal;
	int gVal;
	int bVal;
	ArrayList<Integer> storedSizes;
	ArrayList<Integer> storedColors;
	int numTurns; //will be deleted when I actually implement the flippers method of executing turns
	
	
	int[] switches;
	int numSwitches = 14;
	/* Switches outline the basic properties of the program, and are covered here:
	 * [0]: base width of shape (starting point for randomized width)
	 * [1]: base height of shape (^^ height)
	 * [2]: r-value base (^^)
	 * [3]: b-value base (^^)
	 * [4]: g-value base (^^)
	 * [5]: width range (amount by which width can change)
	 * [6]: height range (^^ height)
	 * [7]: r-value range (^^)
	 * [8]: b-value range (^^)
	 * [9]: g-value range (^^)
	 * [10]: size randomization style (0 == uniform distribution, 1 == n values)
	 * [11]: n sizes (only if switches[10] == 1)
	 * [12]: color randomization style (^^)
	 * [13]: n colors (only if switches[12] == 1)
	 * [14]: shape type (0 = circle, 1 = rectangle, 2 = triangle)
	 * [15]: movement type (figure out later)
	 * [16]: x-axis mirror (0 = not mirroring along x-axis, 1 = mirroring)
	 * [17]: y-axis mirror (^^)
	 * [18]: num turns (PROBABLY TEMPORARY)
	 * []:
	 */
	ArrayList<int[]> flippers = new ArrayList<int[]>(); /* flippers change the settings in 
	* a specific order and in a specific way, as the code executes, giving each set of flippers
	* a code-like quality in themselves, like a strand of DNA. Really it's just DNA. [0] is the value being changed, [1] is the
	* replacement value, and [2] is the number of "turns" before the next flipper is activated.
	*/
	
	public void initializeSwitches(){
		switches = new int[30];
		switches[0] = (int) (50 + r.nextInt(50) * Math.pow(-1, r.nextInt(2)));
		switches[1] = (int) (50 + r.nextInt(50) * Math.pow(-1, r.nextInt(2)));
		switches[2] = r.nextInt(256); //add 2 to each value so that the randomization of RGB values based on 
		switches[3] = r.nextInt(256);
		switches[4] = r.nextInt(256);
		switches[5] = r.nextInt(100) + 2;	//add 2 to each value so that the randomization of RGB values based on them is greater than 1
		switches[6] = r.nextInt(100) + 2;
		switches[7] = r.nextInt(100) + 2;
		switches[8] = r.nextInt(100) + 2;
		switches[9] = r.nextInt(100) + 2;
		switches[10] = 0;	
		switches[12] = 0;				
		switches[14] = r.nextInt(2);
		if (r.nextInt(2) != 0)		//Currently: 0 = standard (base width/height + uniformly random width/height range); 1 = n set values (same method of randomization for values, but only gives n total values to alternate between) 
			switches[10] = 1;
		//if (r.nextInt(2) != 0)
			switches[12] = 1;			//Currently: 0 = standard (base rgb + uniformly random rgb range); 1 = n set values (same method of randomization for values, but only gives n total values to alternate between) 
		switches[11] = 	1 + r.nextInt(4);
		switches[13] = 	2;
		if (r.nextInt(5) == 0)
			switches[14] = 1;
		switches[18] = r.nextInt(6000);
	}
	
	Squig(int x, int y){
		xSize = x;
		ySize = y;
		panel = new DrawingPanel(xSize, ySize);
		g = panel.getGraphics();
		switches = new int[numSwitches];
		r = new Random();
		xCursor = xSize / 2;
		yCursor = ySize / 2;
	}
	
	public void mutate(int[] parentDNA){
		int[] childDNA = new int[switches.length];
		for(int i = 0; i < parentDNA.length; i++) {
			childDNA[i] = parentDNA[i];
		}
		for(int i = 0; i < 10; i++) {		//Part of the DNA that has base width/length, width/length variability, base rgb, rgb variability
			childDNA[i] = (r.nextInt(2) == 0) ? (int) (parentDNA[i] * (r.nextDouble() / 2 + .5)): (int) (parentDNA[i] * (r.nextDouble() + 1)); //hopefully gives even odds of doubling and halving, hopefully making most things balance naturally? OLD WAY--> parentDNA[i] + r.nextInt(parentDNA[i] + 1) - parentDNA[i] / 2;
			if (childDNA[i] < 0) {
				childDNA[i] = 2;
			} else if (childDNA[i] > 255){
				childDNA[i] = 255;
			}
		}
		childDNA[18] = (r.nextInt(2) == 0) ? (int) (parentDNA[18] * (r.nextDouble() / 2 + .5)): (int) (parentDNA[18] * (r.nextDouble() + 1));
		switch (r.nextInt(10)){		//This switch statement gives the child a 10% chance of changing the size randomization style
			case 0: childDNA[10] = (childDNA[10] + 1) % 2;	//only 2 size randomization styles ATM
		}	
		switch (r.nextInt(2)){		//10% chance of changing color randomization style
			case 0: childDNA[12] = (childDNA[12] + 1) % 2;	//only 2 color randomization styles ATM
		}
		switch(r.nextInt(10)){		//10% chance of changing shapes
			case 0: childDNA[14] = (childDNA[14] + 1) % 2;	////modulus 2 because only oval and rect have been programmed, add more with more shapes. Also, give it a random addition once there's more than 2 shapes to choose from
		}
		switches = childDNA;
	}
	
	//run() will be very complicated and filled with conditionals about the state of switches[]
	//(might try to think of a way to fix this)	
	public void run(int place){
		//panel = new DrawingPanel(xSize, ySize);
		//g = panel.getGraphics();
		int directionCounter = -1;
		int xMovement = 0;
		int yMovement = 0;
		Color c = new Color(switches[2], switches[3], switches[4]);
		g.setColor(c);
		numTurns = switches[18];	//instead of numTurns, we will be working with flippers and their respective length
		
		if (switches[10] == 1) {
			storedSizes = createStoredSizes();
		}if (switches[12] == 1) {
			storedColors = createStoredColors();
		}
		

		System.out.println(numTurns);
		System.out.println(switches[18]);
		while (numTurns > 0){
			if (directionCounter < 0){
				directionCounter = r.nextInt(20);
				xMovement = r.nextInt(65) - 32; 
				yMovement = r.nextInt(65) - 32;
				xCursor = r.nextInt(xSize);
				yCursor = r.nextInt(ySize);
			}
			getColors();
			c = new Color(rVal, gVal, bVal);
			g.setColor(c);
			if (switches[14] == 0){
				g.fillOval(xCursor, yCursor, getWidth(), getHeight());

			} else if (switches[14] == 1) {
				g.fillRect(xCursor, yCursor, getWidth(), getHeight());
			}
			xCursor += xMovement;
			yCursor += yMovement;
			if (xCursor > xSize || xCursor < 0)
				xMovement *= -1;
			if (yCursor > ySize || yCursor < 0)
				yMovement *= -1;
			--directionCounter;
			--numTurns;
		}
		label(place);
	}
	
	public ArrayList<Integer> createStoredSizes(){
		ArrayList<Integer> vals = new ArrayList<Integer>();
		for(int i = 0; i < switches[11]; i++) {
			vals.add(switches[0] + r.nextInt(switches[5]));
			vals.add(switches[1] + r.nextInt(switches[6]));
		}
		return vals;
	}	
	
	public ArrayList<Integer> createStoredColors(){
		ArrayList<Integer> vals = new ArrayList<Integer>();
		for(int i = 0; i < switches[13]; i++) {
			vals.add(switches[2] + r.nextInt(switches[7]));
			vals.add(switches[3] + r.nextInt(switches[8]));
			vals.add(switches[4] + r.nextInt(switches[9]));
		}
		return vals;
	}
	
	public int getWidth(){
		if (switches[10] == 0) {
			return switches[0] + r.nextInt(switches[5]);
		}
		if (switches[10] == 1) {
			if (switches[11] > 1)
				return storedSizes.get(r.nextInt(switches[11]) * 2);
			else 
				return storedSizes.get(0);
		}
		return 0;
	}
	public int getHeight(){
		if (switches[10] == 0) {
			return switches[1] + r.nextInt(switches[6]);
		}
		if (switches[10] == 1) {
			if (switches[11] > 1)
				return storedSizes.get(r.nextInt(switches[11]) * 2 + 1);
			else
				return storedSizes.get(1);
		}
		return 0;
	}
	
	public void getColors(){
		if (switches[12] == 0) {
			rVal = (int) (switches[2] + r.nextInt(switches[7]) * Math.pow(-1, r.nextInt(2)));
			gVal = (int) (switches[3] + r.nextInt(switches[8]) * Math.pow(-1, r.nextInt(2)));
			bVal = (int) (switches[4] + r.nextInt(switches[9]) * Math.pow(-1, r.nextInt(2)));
		} else if (switches[12] == 1){
			if (switches[13] > 1) {
				int x = r.nextInt(switches[13]);
				rVal = storedColors.get(x * 3);
				gVal = storedColors.get(x * 3 + 1);
				bVal = storedColors.get(x * 3 + 2);
			} else {
				rVal = storedColors.get(0);
				gVal = storedColors.get(1);
				bVal = storedColors.get(2);
			}
			
		}
		if (rVal > 255)
			rVal = 255;
		if (gVal > 255)
			gVal = 255;
		if (bVal > 255)
			bVal = 255;
		if (rVal < 0)
			rVal = 0;
		if (gVal < 0)
			gVal = 0;
		if (bVal < 0)
			bVal = 0;
	}
	
	public void label(int place){
		g.setColor(Color.BLACK);
		if (switches[10] == 1)
			g.setColor(Color.RED);
		for(int i = 0; i < place; i++){
			g.drawLine(50 + i * 15, 50, 50 + i * 15, 70);
		}
	}
}
