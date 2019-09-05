/**
 * @author Aaron-Qiu, mgsweet@126.com, student_id:1101584
 */
package Server;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

public class DictServerUI {

	private JFrame frame;
	private JTextArea logArea;
	private JLabel addressLabel;
	private JLabel portLabel;
	private JLabel pathLabel;
	
	public JFrame getFrame() {
		return frame;
	};
	
	public JTextArea getlogArea() {
		return logArea;
	}

	/**
	 * Create the application.
	 */
	public DictServerUI(String address, String port, String path) {
		initialize(address, port, path);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(String address, String port, String path) {
		frame = new JFrame();
		frame.setMinimumSize(new Dimension(450, 300));
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				System.out.println("Server Close!");
			}
		});
		
		JScrollPane scrollPane = new JScrollPane();
		
		logArea = new JTextArea();
		logArea.setLineWrap(true);
		logArea.setEditable(false);
		scrollPane.setViewportView(logArea);
		
		addressLabel = new JLabel("Address: " + address);
		
		portLabel = new JLabel("Port: " + port);
		
		pathLabel = new JLabel("Dictionary Path: " + path);
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(20)
					.addComponent(addressLabel, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(20)
					.addComponent(portLabel, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(20)
					.addComponent(pathLabel, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(20)
					.addComponent(addressLabel, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
					.addComponent(portLabel, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
					.addComponent(pathLabel)
					.addGap(29)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
					.addContainerGap())
		);
		frame.getContentPane().setLayout(groupLayout);
		
		
	}

}
