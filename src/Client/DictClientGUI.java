package Client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.JTextPane;

import StateCode.StateCode;

import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.GridLayout;

public class DictClientGUI {

	private JFrame frame;
	private JTextPane meaningPane;
	private JTextField wordField;
	private DictClient dictClient;
	
	public JFrame getFrame() {
		return frame;
	}

	/**
	 * Create the application.
	 */
	public DictClientGUI(DictClient client) {
		dictClient =client;
		initialize();
	}
	
	private Boolean isValid(String word, String meaning, int command) {
		if (word.equals("")) {
			JOptionPane.showMessageDialog(frame, "Please Enter a word.", "Warning", JOptionPane.WARNING_MESSAGE);
			return false;
		} else if (command == StateCode.ADD && meaning.equals("")) {
			JOptionPane.showMessageDialog(frame, "Please Enter the word's meaning.", "Warning", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(0, 3, 0, 0));
		
		JLabel lblMeaning = new JLabel("The Meaning(s) of the word: ");
		frame.getContentPane().add(lblMeaning);
		
		JLabel label = new JLabel("");
		frame.getContentPane().add(label);
		
		JLabel label_1 = new JLabel("");
		frame.getContentPane().add(label_1);
		
		JLabel label_2 = new JLabel("");
		frame.getContentPane().add(label_2);
		
		meaningPane = new JTextPane();
		frame.getContentPane().add(meaningPane);
		
		JLabel label_3 = new JLabel("");
		frame.getContentPane().add(label_3);
		
		JLabel lblWord = new JLabel("Word:");
		frame.getContentPane().add(lblWord);
		
		JLabel label_4 = new JLabel("");
		frame.getContentPane().add(label_4);
		
		JLabel label_5 = new JLabel("");
		frame.getContentPane().add(label_5);
		
		wordField = new JTextField();
		frame.getContentPane().add(wordField);
		wordField.setColumns(10);
		
		JButton btnAdd = new JButton("ADD");
		btnAdd.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String word = wordField.getText();
				String meaning = meaningPane.getText();
				if (isValid(word, meaning, StateCode.ADD)) {
					int confirm = JOptionPane.showConfirmDialog(frame,  "Confirm to Add a new word?", "Confirm Window", JOptionPane.YES_NO_OPTION);
					if (confirm == JOptionPane.YES_OPTION) {
						int state = dictClient.add(word, meaning);
						if(state != StateCode.SUCCESS) {
							JOptionPane.showMessageDialog(frame, "Word Exist!", "Warning", JOptionPane.WARNING_MESSAGE);
						}
					}
				}
			}
		});
		
		JLabel label_6 = new JLabel("");
		frame.getContentPane().add(label_6);
		
		JLabel label_7 = new JLabel("");
		frame.getContentPane().add(label_7);
		btnAdd.setToolTipText("");
		frame.getContentPane().add(btnAdd);
		
		JButton btnQuery = new JButton("Query");
		btnQuery.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String word = wordField.getText();
				if (isValid(word, "", StateCode.QUERY)) {
					String[] resultArr = dictClient.query(word);
					int state = Integer.parseInt(resultArr[0]);
					if(state != StateCode.SUCCESS) {
						JOptionPane.showMessageDialog(frame, "Query Fail\nWord Not Exist!", "Warning", JOptionPane.WARNING_MESSAGE);
					} else {
						meaningPane.setText(resultArr[1]);
					}
				}
			}
		});
		frame.getContentPane().add(btnQuery);
		
		JButton btnRemove = new JButton("Remove");
		btnRemove.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String word = wordField.getText();
				if (isValid(word, "", StateCode.REMOVE)) {
					int confirm = JOptionPane.showConfirmDialog(frame,  "Confirm to Remove a new word?", "Confirm Window", JOptionPane.YES_NO_OPTION);
					if (confirm == JOptionPane.YES_OPTION) {
						int state = dictClient.remove(word);
						if(state != StateCode.SUCCESS) {
							JOptionPane.showMessageDialog(frame, "Remove Fail\nWord Not Exist!", "Warning", JOptionPane.WARNING_MESSAGE);
						}
					}
				}
			}
		});
		frame.getContentPane().add(btnRemove);
	}
}
