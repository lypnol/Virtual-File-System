package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

/**
 * 
 * @author ayoub
 *
 */
public class AboutWindow extends JDialog implements ActionListener{

	private static final long serialVersionUID = 2757735139534036146L;
	private final JPanel contentPanel = new JPanel();
	/**
	 * Create the dialog.
	 */
	public AboutWindow() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("About");
		setBounds(100, 100, 374, 190);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JLabel lblCreatedBy = new JLabel("<html> Version 0.1.0 (Beta) <br> Created by: <br>\r\n<b>Ayoub Sbai lypnox@gmail.com</b> <br>");
			contentPanel.add(lblCreatedBy);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				okButton.addActionListener(this);
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("OK"))
			this.dispose();
	}

}
