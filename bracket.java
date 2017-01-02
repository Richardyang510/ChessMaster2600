import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class bracket extends JPanel implements ActionListener {

	static ArrayList<Player> a;
	static int num;
	static int K;
	static String filename;

	JButton[][] vsButton;
	double[][] score;
	int[][] playerID;

	JPanel VS;

	ArrayList<Player> A = new ArrayList();
	ArrayList<Player> B = new ArrayList();

	int l, odd;

	public void actionPerformed(ActionEvent e) {

		for (int i = 0; i < a.size() / 2; i++) {
			int w1 = 0, w2;
			if (vsButton[i][1] == e.getSource()) {
				w1 = 1;
				w2 = 2;
			} else if (vsButton[i][2] == e.getSource()) {
				w1 = 2;
				w2 = 1;
			} else {
				continue;
			}
			if (score[i][w1] == 0) {
				score[i][w1] = 1;
				score[i][w2] = 0;
			} else if (score[i][w1] == 1) {
				score[i][w1] = 0.5;
				score[i][w2] = 0.5;
			} else if (score[i][w1] == 0.5) {
				score[i][w1] = 1;
				score[i][w2] = 0;
			} else {
				score[i][w1] = 1;
				score[i][w2] = 0;
			}
		}

		addText();

		try {
			saveB();
		} catch (FileNotFoundException | UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

	}

	public bracket(ArrayList<Player> A, int NUM, int KK, String FILENAME) {
		a = A;
		num = NUM;
		K = KK;
		filename = FILENAME;

		setLayout(new BorderLayout());

		l = a.size() / 2;

		VS = new JPanel();

		File f = new File("saves/bracket/" + filename);

		startBracket();

		if (f.exists() && !f.isDirectory()) {
			try {
				readB();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// ANNOUNCE
		for (int i = 0; i < l; i++) {
			vsButton[i][1] = new JButton(A.get(i).getName());
			vsButton[i][2] = new JButton(B.get(i).getName());
			vsButton[i][1].setBackground(Color.WHITE);
			vsButton[i][2].setBackground(Color.BLACK);
			if (i % 2 == 0) {
				vsButton[i][1].setBackground(Color.BLACK);
				vsButton[i][2].setBackground(Color.WHITE);
				vsButton[i][1].setForeground(Color.WHITE);
			} else {
				vsButton[i][2].setForeground(Color.WHITE);
			}
			vsButton[i][1].addActionListener(this);
			vsButton[i][2].addActionListener(this);
			vsButton[i][0] = new JButton("Game#: " + String.valueOf(i + 1));
			for (int h = 0; h < 3; h++) {
				VS.add(vsButton[i][h]);
			}
		}
		if (a.size() % 2 != 0) {
			vsButton[l][0] = new JButton("Sit out");
			vsButton[l][1] = new JButton(A.get(l).getName());
			VS.add(vsButton[l][0]);
			VS.add(vsButton[l][1]);
		}

		addText();
		add(VS, BorderLayout.CENTER);

	}

	public void addText() {
		for (int i = 0; i < l + odd; i++) {
			String txt1 = A.get(i).getName();
			String txt2 = "";
			if (i < l) {
				txt2 = B.get(i).getName();
			}
			String modify1 = "";
			String modify2 = "";
			if (score[i][1] == 0 && score[i][2] == 1) {
				modify1 = "LOSE";
				modify2 = "WIN";
			} else if (score[i][1] == 1 && score[i][2] == 0) {
				modify1 = "WIN";
				modify2 = "LOSE";
			} else if (score[i][1] == 0.5 && score[i][2] == 0.5) {
				modify1 = "TIE";
				modify2 = "TIE";
			}
			vsButton[i][1].setText(txt1 + " " + modify1);
			vsButton[i][2].setText(txt2 + " " + modify2);
		}
		validate();
		repaint();
	}

	public void startBracket() {

		// sort
		int j;
		Player temp;

		for (int i = 1; i < a.size(); i++) {
			temp = a.get(i);
			for (j = i; j > 0 && a.get(j - 1).getELO() < temp.getELO(); j--) {
				a.set(j, a.get(j - 1));
			}
			a.set(j, temp);
		}

		odd = 0;

		// SPLIT
		if (a.size() % 2 == 0) {
			VS.setLayout(new GridLayout(l, 2));
			vsButton = new JButton[l][3];
			score = new double[l][3];
		} else {
			VS.setLayout(new GridLayout(l + 1, 2));
			vsButton = new JButton[l + 1][3];
			score = new double[l + 1][3];
			odd++;
		}

		for (int i = 0; i < l; i++) {
			A.add(a.get(i));
			B.add(a.get(i + l));
		}
		if (a.size() % 2 != 0) {
			A.add(a.get(a.size() - 1));
		}

		for (int i = 0; i < l + odd; i++) {
			for (int k = 0; k < 3; k++) {
				vsButton[i][k] = new JButton();
				score[i][k] = -1;
			}
		}

		playerID = new int[l + odd][3];

		int count = 0;
		outerloop: for (int i = 1; i < 3; i++) {
			for (int k = 0; k < l; k++) {
				playerID[k][i] = count;
				count++;
				if (count == num) {
					break outerloop;
				}
			}
		}

		if (odd == 1) {
			score[l][1] = 1;
		}

	}

	public ArrayList<Player> update() {
		for (int i = 0; i < l; i++) {
			if (score[i][1] == 1) {
				a.get(playerID[i][1]).vs(a.get(playerID[i][2]), "WIN", K);
			} else if (score[i][1] == 0.5) {
				a.get(playerID[i][1]).vs(a.get(playerID[i][2]), "TIE", K);
			} else if (score[i][1] == 0){
				a.get(playerID[i][1]).vs(a.get(playerID[i][2]), "LOSE", K);
			}
		}
		return a;
	}

	public void readB() throws FileNotFoundException, IOException {
		FileInputStream fs = new FileInputStream("saves/bracket/" + filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		for (int i = 0; i < l + odd; i++) {
			for (int j = 0; j < 3; j++) {
				score[i][j] = Double.valueOf(br.readLine());
			}
		}
		br.close();
	}

	public void saveB() throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("saves/bracket/" + filename, "UTF-8");
		for (int i = 0; i < l + odd; i++) {
			for (int j = 0; j < 3; j++) {
				writer.println(score[i][j]);
			}
		}
		writer.close();
	}

}
