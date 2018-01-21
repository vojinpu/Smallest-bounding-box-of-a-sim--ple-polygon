import java.awt.Button;

import javax.swing.JFrame;

public class Main {

	public static void main(String[] args) {

		JFrame f = new JFrame("Title");
		
		Algoritam al = new Algoritam();
		
		f.add(al);
		f.setSize(700, 700);
		f.setVisible(true);
	}

}
