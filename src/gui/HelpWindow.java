package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

/**
 * 
 * @author ayoub
 *
 */
public class HelpWindow extends JDialog implements ActionListener{

	private static final long serialVersionUID = 2757735139534036146L;
	private final JPanel contentPanel = new JPanel();
	/**
	 * Create the dialog.
	 */
	public HelpWindow() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Help");
		setBounds(100, 100, 598, 196);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JLabel lblCreatedBy = new JLabel("<html>This is a virtual file system application. <br>\r\nThe trees shown at the left side contain all the virtual disk created on the host file system. <br>\r\nThe search bar allows to look for all files/directories with a certain name. <br>\r\nAll the information concerning VNodes and virtual disks are shown in the center of the screen.</html>");
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
	


