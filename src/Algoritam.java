import java.awt.Button;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.Delayed;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

public class Algoritam extends JPanel {

	private int n = 20;
	private int hullN;
	private Graphics g;

	private Point[] dots;
	private Point[] hull;
	private Point pointTL = new Point();
	private Point pointTR = new Point();
	private Point pointDL = new Point();
	private Point pointDR = new Point();

	private Point PointTL = new Point();
	private Point PointTR = new Point();
	private Point PointDL = new Point();
	private Point PointDR = new Point();

	private double distanceLeft = 0;
	private double distanceRight = 0;
	private double distanceOpposite = 0;
	private int pointLeft = 0;
	private int pointRight = 0;
	private int pointOpposite = 0;

	private int currentLine = 0;

	private double LowestSurface = Double.MAX_VALUE;
	
	public Algoritam() {
		addMouseClick();

		dots = new Point[100];
		hull = new Point[102];

		generateDots();
		
		

		Button bPocni = new Button("Pocni");
		this.add(bPocni);

		bPocni.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

//				PointTL = new Point();
//				PointTR = new Point();
//				PointDL = new Point();
//				PointDR = new Point();

				LowestSurface = Double.MAX_VALUE;
				createHull();
				repaint();
			}
		});

		Button bGenerisi = new Button("Zavrsi");
		this.add(bGenerisi);
		bGenerisi.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				while (currentLine <= hullN) {
					step();
					repaint();
					currentLine++;
				}

			}
		});

		Button bObrisi = new Button("Resetuj");
		this.add(bObrisi);
		bObrisi.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				n = 0;
				hullN = 0;
				PointTL = new Point();
				PointTR = new Point();
				PointDL = new Point();
				PointDR = new Point();

				repaint();
			}
		});

		Button bSacuvaj = new Button("Sacuvaj");
		this.add(bSacuvaj);
		bSacuvaj.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				saveFile();
			}
		});

		Button bOtvori = new Button("Otvori fajl");
		this.add(bOtvori);
		bOtvori.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				n = 0;
				hullN = 0;
				PointTL = new Point();
				PointTR = new Point();
				PointDL = new Point();
				PointDR = new Point();

				openFile();
				createHull();

				repaint();

			}
		});

	}

	private void addMouseClick() {

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {

				if (me.isMetaDown()) {

					if (currentLine > hullN)
						return;

					step();
					repaint();
					currentLine++;

				}

				else {
					Point p = new Point();
					p.x = me.getX();
					p.y = me.getY();

					dots[n++] = p;

					repaint();
				}
			}
		});

	}

	private void generateDots() {
		Random r = new Random();
		for (int i = 0; i < n; i++) {
			dots[i] = new Point();
			dots[i].x = Math.abs(r.nextInt() % 300) + 200;
			dots[i].y = Math.abs(r.nextInt() % 300) + 200;
		}
	}

	private void sortDots() {

		Point[] zaSort = new Point[n];

		for (int i = 0; i < n; i++)
			zaSort[i] = dots[i];

		Arrays.sort(zaSort);

		for (int i = 0; i < n; i++)
			dots[i] = zaSort[i];

	}

	private void createHull() {
		hullN = 0;

		distanceLeft = 0;
		distanceRight = 0;
		distanceOpposite = 0;
		pointLeft = 0;
		pointRight = 0;
		pointOpposite = 0;
		pointTL = new Point();
		pointTR = new Point();
		pointDL = new Point();
		pointDR = new Point();

		currentLine = 0;

		sortDots();

		for (int i = 0; i < n; i++) {
//			while (hullN >= 2 && cross(hullN, i) <= 0)
			while (hullN >= 2 && whichSideOfTheLine(hull[hullN - 1], hull[hullN - 2], dots[i]) > 0)
				hullN--;

			hull[hullN++] = dots[i];
		}

		
		for (int i = n - 2, t = hullN + 1; i >= 0; i--) {
			while (hullN >= t && whichSideOfTheLine(hull[hullN - 1], hull[hullN - 2], dots[i]) > 0)
				hullN--;

			hull[hullN++] = dots[i];
		}

		hullN--;
	}

	private void step() {

		System.out.println("We are in step");
		System.out.println("hullN" + hullN);

		int fr = currentLine;
		int sc = (fr + 1) % hullN;

		double sqrt = Math.sqrt(Math.pow((hull[sc].x - hull[fr].x), 2) + Math.pow((hull[sc].y - hull[fr].y), 2));

		int D = 100;
		Point normal3 = new Point();
		normal3.x = (int) ((hull[fr].x + hull[sc].x) / 2.0 + (D * (-(hull[sc].y - hull[fr].y)) / sqrt));
		normal3.y = (int) ((hull[fr].y + hull[sc].y) / 2.0 + D * (hull[sc].x - hull[fr].x) / sqrt);

		Point normal4 = new Point();
		normal4.x = (int) ((hull[fr].x + hull[sc].x) / 2.0 - (D * (-(hull[sc].y - hull[fr].y)) / sqrt));
		normal4.y = (int) ((hull[fr].y + hull[sc].y) / 2.0 - D * (hull[sc].x - hull[fr].x) / sqrt);

		// do it just first time
		if (fr == 0)
			for (int i = 0; i < hullN; i++) {

				double pointDistance = whichSideOfTheLine(normal3, normal4, hull[i]);
				double pointOppositeDistance = whichSideOfTheLine(hull[fr], hull[sc], hull[i]);
				if (distanceLeft < pointDistance) {
					pointLeft = i;
					distanceLeft = pointDistance;
				}

				if (distanceRight > pointDistance) {
					pointRight = i;
					distanceRight = pointDistance;
				}

				if (distanceOpposite < Math.abs(pointOppositeDistance)) {
					pointOpposite = i;
					distanceOpposite = pointOppositeDistance;
				}

			}

		else {

			while (whichSideOfTheLine(normal3, normal4, hull[pointLeft]) < whichSideOfTheLine(normal3, normal4,
					hull[getPointInc(pointLeft + 1)])) {
				pointLeft = getPointInc(pointLeft + 1);
			}

			while (whichSideOfTheLine(normal3, normal4, hull[pointRight]) > whichSideOfTheLine(normal3, normal4,
					hull[getPointInc(pointRight + 1)])) {
				pointRight = getPointInc(pointRight + 1);
			}

			while (Math.abs(whichSideOfTheLine(hull[fr], hull[sc], hull[pointOpposite])) < Math
					.abs(whichSideOfTheLine(hull[fr], hull[sc], hull[getPointInc(pointOpposite + 1)]))) {
				pointOpposite = getPointInc(pointOpposite + 1);
			}
		}

		// k i n za liniju
		double broj = 1;
		double k1 = (hull[fr].y - hull[sc].y) / (double) (hull[fr].x - hull[sc].x);
		double n1 = hull[fr].y - k1 * hull[fr].x;

		// k i n za normalu u odnosu na nasu liniju
		double kn = (normal3.y - normal4.y) / (double) (normal3.x - normal4.x);
		// double kn = Math.atan2(normal3.y - normal4.y, normal3.x - normal4.x);
		double nn = normal3.y - kn * normal3.x;

		// n za ovu sa leve strane
		double nl = hull[pointLeft].y - kn * hull[pointLeft].x;

		// n za onu sa desne strane
		double nr = hull[pointRight].y - kn * hull[pointRight].x;

		// n za onu naspramnu
		double no = hull[pointOpposite].y - k1 * hull[pointOpposite].x;

		double pom = ((nl - n1) / (double) (k1 - kn));
		pointTL.y = (int) (pom * kn + nl);
		pointTL.x = (int) Math.round(pom);

		pom = (-(nr - n1) / (double) (kn - k1));
		pointTR.y = (int) (pom * kn + nr);
		pointTR.x = (int) Math.round(pom);

		pom = (no - nl) / (kn - k1);
		pointDL.y = (int) (pom * kn + nl);
		pointDL.x = (int) Math.round(pom);

		pom = (no - nr) / (kn - k1);
		pointDR.y = (int) (pom * kn + nr);
		pointDR.x = (int) Math.round(pom);

		checkRectangeSurface();
	}

	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		this.setBackground(Color.WHITE);
		g = gr;

		// DRAW ALL DOTS
		g.setColor(Color.BLACK);
		for (int i = 0; i < n; i++) {
			drawPoint(dots[i]);
		}

		if (hullN <= 1)
			return;
		System.out.println("in paint: " + currentLine + " " + hullN);
		if (currentLine > hullN) {
			drawLine(PointTL, PointTR);
			drawLine(PointTR, PointDR);
			drawLine(PointDR, PointDL);
			drawLine(PointDL, PointTL);
			System.out.println("Gotovo");
			return;
		}

		System.out.println("Usli " + n);

		// DRAW HULL
		g.setColor(Color.GRAY);
		for (int i = 1; i < hullN; i++) {
			g.drawLine(hull[i - 1].x, hull[i - 1].y, hull[i].x, hull[i].y);
		}
		g.drawLine(hull[0].x, hull[0].y, hull[hullN - 1].x, hull[hullN - 1].y);

		g.setColor(Color.PINK);
		drawLine(pointTL, pointTR);
		drawLine(pointTR, pointDR);
		drawLine(pointDR, pointDL);
		drawLine(pointDL, pointTL);

	}


	private void checkRectangeSurface() {

		double width = Math.sqrt(Math.pow(pointTR.x - pointTL.x, 2) + Math.pow(pointTR.y - pointTL.y, 2));
		double height = Math.sqrt(Math.pow(pointTR.x - pointDR.x, 2) + Math.pow(pointTR.y - pointDR.y, 2));

		if (width * height < LowestSurface) {
			LowestSurface = width * height;
			PointTL = pointTL;
			PointTR = pointTR;
			PointDL = pointDL;
			PointDR = pointDR;
		}

	}

	private void drawLine(Point A, Point B) {
		g.drawLine(A.x, A.y, B.x, B.y);
	}

	private void drawPoint(Point point) {
		g.drawLine(point.x - 5, point.y - 5, point.x + 5, point.y + 5);
		g.drawLine(point.x - 5, point.y + 5, point.x + 5, point.y - 5);
	}

	private int cross(int k, int n) {
		return (hull[k - 1].x - hull[k - 2].x) * (dots[n].y - hull[k - 2].y)
				- (hull[k - 1].y - hull[k - 2].y) * (dots[n].x - hull[k - 2].x);
	}

	private double whichSideOfTheLine(Point A, Point B, Point P) {
		return (B.x - A.x) * (P.y - A.y) - (P.x - A.x) * (B.y - A.y);
	}

	private void swap(int i, int j) {

		Point pom = dots[i];
		dots[i] = dots[j];
		dots[j] = pom;
	}

	private int getPointInc(int n) {
		return n % hullN;
	}

	public void openFile() {
		JFileChooser fc = new JFileChooser();
		File file;
		BufferedReader br;
		StringTokenizer st;

		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
			if (file.exists()) {
				try {
					br = new BufferedReader(new FileReader(file.getAbsolutePath()));
					try {

						n = Integer.parseInt(br.readLine());

						for (int i = 0; i < n; i++) {
							String str = br.readLine().toString();

							Point p = new Point();
							p.x = Integer.parseInt(str.substring(0, str.indexOf(' ')));
							p.y = Integer.parseInt(str.substring(str.indexOf(' ') + 1));

							dots[i] = p;
						}

					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void saveFile() {
		JFileChooser fc = new JFileChooser();
		if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				FileWriter fw = new FileWriter(fc.getSelectedFile().getAbsolutePath());
				String str = "";

				fw.write(n + "\n");
				for (int i = 0; i < n; i++) {
					fw.write(dots[i].x + " " + dots[i].y + "\n");
				}
				fw.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
