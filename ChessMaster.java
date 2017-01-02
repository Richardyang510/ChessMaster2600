import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import javax.swing.*;

/*
 * ____________________________
 * |current file:|   config    |
 * _____________________________
 * |   round rob | list        |
 * |   (LEFTTOP) | (RIGHT)     |
 * |_____________|             |
 * |    bracket  |             |
 * |  (LEFTBOT)  |             |
 * _____________________________
 */

public class ChessMaster extends JFrame implements ActionListener {

	static ArrayList<Player> a = new ArrayList();
	static int num;
	static String filename;
	static int K;

	JPanel welcome = new JPanel();
	JPanel top = new JPanel();
	JPanel mid = new JPanel();
	JPanel left = new JPanel();

	// MAIN - RIGHT PANEL
	JList<String> list;
	JScrollPane scrollPane;
	JPopupMenu popupMenu = new JPopupMenu();
	JMenuItem newPlayer = new JMenuItem("New Player");
	JMenuItem editPlayer = new JMenuItem("Edit Player");
	JMenuItem saveAs = new JMenuItem("Save List As");

	// MAIN - LEFT PANEL
	JButton rb = new JButton("Round Robin");
	JButton br = new JButton("Bracket");

	// TOP PANEL
	JLabel currentFile;
	JButton config = new JButton("Config");

	// RETURN BUTTONS FOR ROUND ROBIN
	JPanel RRbackPanel = new JPanel();
	JButton RRok = new JButton("Back");
	JButton RRquit = new JButton("Finish Game");

	// RETURN BUTTONS FOR BRACKET
	JPanel BbackPanel = new JPanel();
	JButton Bok = new JButton("Back");
	JButton Bquit = new JButton("Finish Game");

	// INIT PANELS
	roundRobin r;
	bracket b;

	// SETTINGS WINDOW
	JFrame Settings = new JFrame();
	JButton loadFile = new JButton("Load File");
	JButton changeK = new JButton("Change K");
	JButton SETok = new JButton("Comfirm");
	boolean changedF = false;

	// Player Editor
	JFrame Editor = new JFrame();
	JButton changeName = new JButton("Change Name");
	JButton changeELO = new JButton("Change ELO");
	JButton EDITok = new JButton("Confirm");
	JButton deletePlayer = new JButton("Delete Player");

	public ChessMaster() throws RuntimeException, IOException {

		setSize(1000, 1000);
		setTitle("ChessMaster 2600");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		readConfigFile();
		if (filename != "") {
			read();
			sortList();
		}

		// ALL
		welcome.setLayout(new BorderLayout());

		// TOP
		top.setLayout(new GridLayout(1, 2));
		currentFile = new JLabel("Current File: " + filename);
		top.add(currentFile);
		config.addActionListener(this);
		top.add(config);
		welcome.add(top, BorderLayout.NORTH);

		// MID
		mid.setLayout(new GridLayout(1, 2));

		// LEFT
		left.setLayout(new GridLayout(2, 1));
		rb.addActionListener(this);
		br.addActionListener(this);
		left.add(rb);
		left.add(br);
		mid.add(left);
		welcome.add(mid, BorderLayout.CENTER);

		// RIGHT
		createList();

		// OTHER
		RRbackPanel.setLayout(new GridLayout(1, 2));
		RRquit.addActionListener(this);
		RRok.addActionListener(this);
		RRbackPanel.add(RRok);
		RRbackPanel.add(RRquit);
		
		BbackPanel.setLayout(new GridLayout(1,2));
		Bquit.addActionListener(this);
		Bok.addActionListener(this);
		BbackPanel.add(Bok);
		BbackPanel.add(Bquit);

		add(welcome, BorderLayout.CENTER);

		setVisible(true);

	}

	public void setValues(String FN, int KK) {
		K = KK;
		filename = FN;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(rb)) {
			remove(welcome);
			try {
				r = new roundRobin(a, num, K, filename);
				add(r, BorderLayout.CENTER);
				add(RRbackPanel, BorderLayout.SOUTH);
				validate();
				repaint();

			} catch (RuntimeException | IOException ee) {
				ee.printStackTrace();
			}
		} else if (e.getSource().equals(br)) {
			b = new bracket(a, num, K, filename);
			add(b, BorderLayout.CENTER);
			add(BbackPanel, BorderLayout.SOUTH);
			remove(welcome);
			validate();
			repaint();
		} else if (e.getSource().equals(RRok) || e.getSource().equals(RRquit)) {
			try {
				if (e.getSource().equals(RRquit)) {
					a = r.update();
					File f = new File("saves/roundRobin/" + filename);
					f.delete();
				}
				save();
			} catch (FileNotFoundException | UnsupportedEncodingException ee) {
				ee.printStackTrace();
			}

			remove(r);
			remove(RRbackPanel);
			add(welcome);
			mid.remove(scrollPane);
			try {
				createList();
			} catch (FileNotFoundException | UnsupportedEncodingException ee) {
				ee.printStackTrace();
			}
			validate();
			repaint();
		} else if (e.getSource().equals(Bok) || e.getSource().equals(Bquit)) {
			try {
				if (e.getSource().equals(Bquit)) {
					a = b.update();
					File f = new File("saves/bracket/" + filename);
					f.delete();
				}
				save();
			} catch (FileNotFoundException | UnsupportedEncodingException ee) {
				ee.printStackTrace();
			}

			remove(b);
			remove(BbackPanel);
			add(welcome);
			mid.remove(scrollPane);
			try {
				createList();
			} catch (FileNotFoundException | UnsupportedEncodingException ee) {
				ee.printStackTrace();
			}
			validate();
			repaint();
		} else if (e.getSource().equals(config)) {
			changedF = false;

			Settings.setSize(400, 400);
			Settings.setTitle("Settings");
			Settings.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			Settings.setLayout(new GridLayout(3, 1));

			if (filename != "") {
				loadFile.setText("Load File (currently " + filename + ")");
				changeK.setText("Change K (currently " + K + ")");
			}
			loadFile.addActionListener(this);
			changeK.addActionListener(this);
			SETok.addActionListener(this);

			Settings.add(loadFile);
			Settings.add(changeK);
			Settings.add(SETok);

			Settings.setVisible(true);
		}

		if (e.getSource().equals(loadFile)) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
			int result = fileChooser.showOpenDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				changedF = true;
				File selectedFile = fileChooser.getSelectedFile();
				filename = selectedFile.getName();
				loadFile.setText("Load File (currently " + filename + ")");
				try {
					read();
					mid.remove(scrollPane);
					createList();
				} catch (NumberFormatException | IOException e1) {
					e1.printStackTrace();
				}
			}
		} else if (e.getSource().equals(changeK)) {
			K = Integer.valueOf(JOptionPane.showInputDialog("Please enter new K"));
			changeK.setText("Change K (currently " + K + ")");
		} else if (e.getSource().equals(SETok)) {
			if (changedF) {
				currentFile.setText("Current File: " + filename);
			}
			Settings.dispose();
			try {
				saveConfigFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		if (e.getSource().equals(newPlayer)) {
			JPanel inputPanel = new JPanel();
			JTextField fnameTF = new JTextField(20);
			JTextField lnameTF = new JTextField(20);
			JTextField eloTF = new JTextField(4);
			inputPanel.add(new JLabel("First Name: "));
			inputPanel.add(fnameTF);
			inputPanel.add(Box.createHorizontalStrut(15));
			inputPanel.add(new JLabel("Last Name: "));
			inputPanel.add(lnameTF);
			inputPanel.add(Box.createHorizontalStrut(15));
			inputPanel.add(new JLabel("ELO: "));
			inputPanel.add(eloTF);
			int result = JOptionPane.showConfirmDialog(null, inputPanel, "New Player", JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				a.add(new Player(fnameTF.getText(), lnameTF.getText(), Integer.valueOf(eloTF.getText())));
				try {
					save();
					mid.remove(scrollPane);
					createList();
					validate();
					repaint();
				} catch (FileNotFoundException | UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
			}
		} else if (e.getSource().equals(editPlayer)) {

			Editor = new JFrame();

			Editor.setSize(400, 400);
			Editor.setTitle("Player Editor");
			Editor.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			Editor.setLayout(new GridLayout(4, 1));

			changeName = new JButton("Change Name (currently: " + a.get(list.getSelectedIndex()).getName() + ")");
			changeELO = new JButton("Change ELO (currently: " + a.get(list.getSelectedIndex()).getELO() + ")");
			EDITok = new JButton("Confirm");
			deletePlayer = new JButton("Delete Player");

			changeName.addActionListener(this);
			changeELO.addActionListener(this);
			EDITok.addActionListener(this);
			deletePlayer.addActionListener(this);

			Editor.add(changeName);
			Editor.add(changeELO);
			Editor.add(deletePlayer);
			Editor.add(EDITok);

			Editor.setVisible(true);
		} else if (e.getSource().equals(saveAs)) {
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
			int actionDialog = chooser.showSaveDialog(this);
			if (actionDialog == JFileChooser.APPROVE_OPTION) {
				File fileName = new File(chooser.getSelectedFile() + "");
				if (fileName == null) {
					return;
				}
				if (fileName.exists()) {
					actionDialog = JOptionPane.showConfirmDialog(this, "Replace existing file?");
					if (actionDialog == JOptionPane.NO_OPTION)
						return;
				}
				filename = fileName.getName();
				try {
					save();
				} catch (FileNotFoundException | UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
			}
		}

		if (e.getSource().equals(changeName)) {
			JPanel inputPanel = new JPanel();
			JTextField fnameTF = new JTextField(20);
			JTextField lnameTF = new JTextField(20);
			inputPanel.add(new JLabel("First Name: "));
			inputPanel.add(fnameTF);
			inputPanel.add(Box.createHorizontalStrut(15));
			inputPanel.add(new JLabel("Last Name: "));
			inputPanel.add(lnameTF);
			int result = JOptionPane.showConfirmDialog(null, inputPanel, "Edit Name", JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				a.get(list.getSelectedIndex()).setFirstName(fnameTF.getText());
				a.get(list.getSelectedIndex()).setLastName(lnameTF.getText());
				changeName.setText(("Change Name (currently: " + a.get(list.getSelectedIndex()).getName() + ")"));
			}
		} else if (e.getSource().equals(changeELO)) {
			a.get(list.getSelectedIndex()).setELO(Integer.valueOf(JOptionPane.showInputDialog("Please enter new ELO")));
			changeELO.setText("Change ELO (currently: " + a.get(list.getSelectedIndex()).getELO() + ")");
		} else if (e.getSource().equals(deletePlayer) || e.getSource().equals(EDITok)) {
			if (e.getSource().equals(deletePlayer)) {
				a.remove(list.getSelectedIndex());
			}
			try {
				save();
				mid.remove(scrollPane);
				createList();
				validate();
				repaint();
			} catch (FileNotFoundException | UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			Editor.dispose();
		}
	}

	public void createList() throws FileNotFoundException, UnsupportedEncodingException {
		String[] nameElo = new String[num];
		sortList();
		for (int i = 0; i < num; i++) {
			nameElo[i] = a.get(i).getName() + " " + a.get(i).getELO();
		}
		list = new JList<String>(nameElo);
		scrollPane = new JScrollPane(list);
		popupMenu.add(newPlayer);
		popupMenu.add(editPlayer);
		popupMenu.addSeparator();
		popupMenu.add(saveAs);
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				if (SwingUtilities.isRightMouseButton(me) && !list.isSelectionEmpty()
						&& list.locationToIndex(me.getPoint()) == list.getSelectedIndex()) {
					popupMenu.show(list, me.getX(), me.getY());
				}
			}
		});
		newPlayer.addActionListener(this);
		editPlayer.addActionListener(this);
		saveAs.addActionListener(this);
		mid.add(scrollPane);
	}

	public void sortList() throws FileNotFoundException, UnsupportedEncodingException {
		int j;
		Player temp;
		for (int i = 1; i < a.size(); i++) {
			temp = a.get(i);
			for (j = i; j > 0 && a.get(j - 1).getELO() < temp.getELO(); j--)
				a.set(j, a.get(j - 1));

			a.set(j, temp);
		}
		save();
	}

	public void readConfigFile() throws IOException {
		FileInputStream fs = new FileInputStream("config.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		filename = br.readLine();
		K = Integer.valueOf(br.readLine());
	}

	public void saveConfigFile() throws IOException {
		PrintWriter writer = new PrintWriter("config.txt", "UTF-8");
		writer.println(filename);
		writer.println(K);
		writer.close();
	}

	public void save() throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("saves/playerDetails/" + filename, "UTF-8");
		num = a.size();
		writer.println(num);
		for (int i = 0; i < a.size(); i++) {
			writer.println(a.get(i).firstname);
			writer.println(a.get(i).lastname);
			writer.println(a.get(i).getELO());
		}
		writer.close();
	}

	public void read() throws NumberFormatException, IOException {
		a.clear();
		FileInputStream fs = new FileInputStream("saves/playerDetails/" + filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		num = Integer.valueOf(br.readLine());
		for (int i = 0; i < num; i++) {
			String fname = br.readLine();
			String lname = br.readLine();
			int elo = Integer.valueOf(br.readLine());
			a.add(new Player(fname, lname, elo));
		}
		br.close();
	}

}
