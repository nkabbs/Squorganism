//import java.awt.*;
import java.util.*;
import java.io.*;

public class Squorganism {
	
	public static void main(String[] args){
		int numSquigs = 8;
		int squigX = 1000;
		int squigY = 1000;
		
		Scanner sc = new Scanner(System.in);
		boolean newGeneration = true;
		int userChoice;
		
		ArrayList<Squig> SQs = new ArrayList<Squig>();
		
		for(int i = 0; i < numSquigs; i++){
			SQs.add(new Squig(squigX, squigY));
			SQs.get(i).initializeSwitches();
		}
		
		while (newGeneration){
			for (int i = 0; i < numSquigs; i++) {
				SQs.get(i).run(i);
			}
			System.out.println("Which one is your favorite?");
			userChoice = sc.nextInt();
			int[] parent = SQs.get(userChoice).switches;
			for(Squig sq: SQs){
				sq.panel.clear();
				sq.mutate(parent);
			}
			
		}
		sc.close();
	}
}
