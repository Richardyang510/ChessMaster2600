import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class roundRobin extends JPanel implements ActionListener {

	static ArrayList<Player> a;
	static int num;
	static int K;
	static String filename;

	JButton[][] rrGrid;
	double[][] score;

	JPanel roundRobin = new JPanel();
	JPanel setName = new JPanel();

	Color Lavender = new Color(255, 20, 147);
	Color ABLue = new Color(0, 255, 255);
	Color Lemon = new Color(255, 255, 0);
	Color Salmon = new Color(250, 128, 114);
	Color plum = new Color(221, 160, 221);

	public roundRobin(ArrayList<Player> A, int NUM, int KK, String FILENAME) throws RuntimeException, IOException {
		a = A;
		num = NUM;
		K = KK;
		filename = FILENAME;
		
		setLayout(new GridLayout(1,1));
		
		// scratch
		roundRobin.removeAll();
		roundRobin.setLayout(new GridLayout(num + 1, num + 2));
		String grid[][] = new String[num + 1][num + 2];
		File f = new File("saves/roundRobin/" + filename);
		if (f.exists() && !f.isDirectory()) {
			readRR(filename);
		} else {
			score = new double[num + 1][num + 2];
			for (int i = 0; i < num + 1; i++) {
				for (int j = 0; j < num + 1; j++) {
					score[i][j] = -1;
				}
			}
		}

		grid[0][0] = " ";

		for (int i = 1; i < num + 1; i++) {
			grid[0][i] = a.get(i - 1).getName();
		}
		for (int i = 1; i < num + 1; i++) {
			grid[i][0] = a.get(i - 1).getName();
		}

		for (int j = 1; j < num + 1; j++) {
			for (int i = 1; i < num + 1; i++) {
				if (i == j) {
					grid[j][i] = "-";
				} else if (j < i) {
					grid[j][i] = "-";
				} else
					grid[i][j] = "";
			}
		}

		grid[0][num + 1] = "Total";
		for (int i = 1; i < num + 1; i++) {
			grid[i][num + 1] = " ";
		}

		rrGrid = new JButton[num + 1][num + 2];

		for (int i = 0; i < num + 1; i++) {
			for (int j = 0; j < num + 2; j++) {

				if (filename == "") {
					rrGrid[i][j] = new JButton(grid[i][j]);
					rrGrid[i][j].setBackground(Color.white);
				} else {
					if (j == num + 1) {
						rrGrid[i][num + 1] = new JButton(String.valueOf(score[i][num + 1]));
					} else if (score[i][j] == 1) {
						rrGrid[i][j] = new JButton("Win");
						rrGrid[i][j].setBackground(Lavender);
					} else if (score[i][j] == 0.5) {
						rrGrid[i][j] = new JButton("Tie");
						rrGrid[i][j].setBackground(Lemon);
					} else if (score[i][j] == 0) {
						rrGrid[i][j] = new JButton("Lose");
						rrGrid[i][j].setBackground(ABLue);
					} else {
						rrGrid[i][j] = new JButton(grid[i][j]);
					}
				}

				rrGrid[i][j].addActionListener(this);
				if (j == 0) {
					rrGrid[i][j].setEnabled(false);
					rrGrid[i][j].setBackground(Salmon);
				} else if (i == 0) {
					rrGrid[i][j].setEnabled(false);
					rrGrid[i][j].setBackground(Salmon);
				} else if (i == j) {
					rrGrid[i][j].setEnabled(false);
					rrGrid[i][j].setBackground(new Color(216, 191, 216));
				} else if (j == num + 1) {
					rrGrid[i][j].setEnabled(false);
					rrGrid[i][j].setBackground(plum);
				}
				roundRobin.add(rrGrid[i][j]);
			}
		}

		add(roundRobin);
		validate();
		repaint();
	}

	public void actionPerformed(ActionEvent e) {

		for (int i = 0; i < num + 1; i++) {
			for (int j = 0; j < num + 1; j++) {
				if (e.getSource() == rrGrid[i][j]) {
					if (rrGrid[i][j].getText().equals("Win")) {
						rrGrid[i][j].setText("Tie");
						rrGrid[j][i].setText("Tie");
						rrGrid[i][j].setBackground(Lemon);
						rrGrid[j][i].setBackground(Lemon);
						score[i][j] = 0.5;
						score[j][i] = 0.5;
					} else {
						rrGrid[i][j].setText("Win");
						rrGrid[j][i].setText("Lose");
						rrGrid[i][j].setBackground(Lavender);
						rrGrid[j][i].setBackground(ABLue);
						score[i][j] = 1;
						score[j][i] = 0;
					}
					double playerScore = 0;
					for (int k = 1; k < num + 1; k++) {
						if (score[i][k] > 0) {
							playerScore += score[i][k];
						}
					}
					score[i][num + 1] = playerScore;
					rrGrid[i][num + 1].setText(String.valueOf(playerScore));

					playerScore = 0;
					for (int k = 1; k < num + 1; k++) {
						if (score[j][k] > 0) {
							playerScore += score[j][k];
						}
					}
					score[j][num + 1] = playerScore;
					rrGrid[j][num + 1].setText(String.valueOf(playerScore));
				}
			}
		}
		try {
			saveRR();
		} catch (FileNotFoundException | UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		validate();
		repaint();
	}
	public ArrayList<Player> update(){
		for (int i = 1; i < num + 1; i++) {
			for (int j = 1; j < num + 1; j++) {
				if (score[i][j] == 1) {
					a.get(i - 1).vs(a.get(j - 1), "WIN", K);
				}
				else if (score[i][j] == 0.5) {
					a.get(i - 1).vs(a.get(j - 1), "TIE", K);
				}
			}
		}
		return a;
	}

	public void saveRR() throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("saves/roundRobin/" + filename, "UTF-8");
		writer.println(num);
		for (int i = 0; i < num + 1; i++) {
			for (int j = 0; j < num + 2; j++) {
				writer.println(score[i][j]);
			}
		}
		writer.close();
	}

	public void readRR(String filename) throws IllegalArgumentException, IOException {
		FileInputStream fs = new FileInputStream("saves/roundRobin/" + filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		num = Integer.valueOf(br.readLine());
		score = new double[num + 1][num + 2];
		for (int i = 0; i < num + 1; i++) {
			for (int j = 0; j < num + 2; j++) {
				score[i][j] = Double.valueOf(br.readLine());
			}
		}
		br.close();
	}

}
