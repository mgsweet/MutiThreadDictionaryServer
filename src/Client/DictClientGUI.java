/**
 * @author Aaron-Qiu, mgsweet@126.com, student_id:1101584
 */
package Client;

import javax.swing.JFrame;
import javax.swing.JButton;
import StateCode.StateCode;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import java.awt.Dimension;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;

public class DictClientGUI {

	private JFrame frame;
	private JTextArea meaningPane;
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
		frame.setMinimumSize(new Dimension(450, 340));
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		wordField = new JTextField();
		wordField.setColumns(10);
		
		JButton btnAdd = new JButton("ADD");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String word = wordField.getText();
				String meaning = meaningPane.getText();
				if (isValid(word, meaning, StateCode.ADD)) {
					int confirm = JOptionPane.showConfirmDialog(frame,  "Confirm to Add a new word?", "Confirm Window", JOptionPane.YES_NO_OPTION);
					if (confirm == JOptionPane.YES_OPTION) {
						int state = dictClient.add(word, meaning);
						if(state == StateCode.UNKNOWN_HOST) {
							JOptionPane.showMessageDialog(frame, "Unknown Host!\nPlease restart with a correct Address and IP.", "Warning", JOptionPane.ERROR_MESSAGE);
						} else if (state == StateCode.FAIL) {
							JOptionPane.showMessageDialog(frame, "Word Exist!", "Warning", JOptionPane.WARNING_MESSAGE);
						} else {
							JOptionPane.showMessageDialog(frame, "Add Success!", "Tips", JOptionPane.INFORMATION_MESSAGE);
						}
					}
				}
			}
		});
		
		JButton btnQuery = new JButton("Query");
		btnQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String word = wordField.getText();
				if (isValid(word, "", StateCode.QUERY)) {
					String[] resultArr = dictClient.query(word);
					int state = Integer.parseInt(resultArr[0]);
					if (state == StateCode.UNKNOWN_HOST) {
						JOptionPane.showMessageDialog(frame, "Unknown Host!\nPlease restart with a correct Address and IP.", "Warning", JOptionPane.ERROR_MESSAGE);
					} else if (state == StateCode.FAIL) {
						JOptionPane.showMessageDialog(frame, "Query Fail\nWord Not Exist!", "Warning", JOptionPane.WARNING_MESSAGE);
					} else {
						meaningPane.setText(resultArr[1]);
					}
				}
			}
		});
		
		JButton btnRemove = new JButton("Remove");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String word = wordField.getText();
				if (isValid(word, "", StateCode.REMOVE)) {
					int confirm = JOptionPane.showConfirmDialog(frame,  "Confirm to Remove a new word?", "Confirm Window", JOptionPane.YES_NO_OPTION);
					if (confirm == JOptionPane.YES_OPTION) {
						int state = dictClient.remove(word);
						if(state == StateCode.UNKNOWN_HOST) {
							JOptionPane.showMessageDialog(frame, "Unknown Host!\nPlease restart with a correct Address and IP.", "Warning", JOptionPane.ERROR_MESSAGE);
						}
						else if(state == StateCode.FAIL) {
							JOptionPane.showMessageDialog(frame, "Remove Fail\nWord Not Exist!", "Warning", JOptionPane.WARNING_MESSAGE);
						} else {
							JOptionPane.showMessageDialog(frame, "Remove Success!", "Tips", JOptionPane.INFORMATION_MESSAGE);
						}
					}
				}
			}
		});
		
		JLabel lblMeaning = new JLabel("The Meaning(s) of the word: ");
		
		JScrollPane scrollPane = new JScrollPane();
		
		JLabel lblWord = new JLabel("Word:");
		
		meaningPane = new JTextArea();
		scrollPane.setViewportView(meaningPane);
		meaningPane.setLineWrap(true);
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(5)
					.addComponent(lblMeaning, GroupLayout.PREFERRED_SIZE, 244, GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(5)
					.addComponent(lblWord, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
				.addComponent(wordField, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(5)
					.addComponent(btnAdd, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
					.addGap(25)
					.addComponent(btnQuery, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
					.addGap(25)
					.addComponent(btnRemove, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
					.addGap(5))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(5)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(5)
					.addComponent(lblMeaning, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
					.addGap(5)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
					.addGap(5)
					.addComponent(lblWord, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
					.addComponent(wordField, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
					.addGap(5)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnAdd, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnQuery, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnRemove, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))
					.addGap(8))
		);
		frame.getContentPane().setLayout(groupLayout);
	}
}
