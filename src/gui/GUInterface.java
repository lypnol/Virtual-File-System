package gui;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import core.*;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.*;

import javax.swing.JButton;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import java.awt.FlowLayout;
import java.awt.Color;

/**
 * 
 * @author ayoub
 *
 */
public class GUInterface extends JFrame implements TreeSelectionListener,ActionListener,KeyListener, MouseListener {

	private static final long serialVersionUID = -2972526722161637357L;
	static JPanel contentPane;
	private static VFSApp vfsApp = new VFSApp();
	private static JMenuItem mntmNewMenuItem = new JMenuItem("get help");
	private static JMenuItem mntmNewMenuItem_1 = new JMenuItem("about");
	private JPanel explorerPanel;
	ArrayList<JTree> trees = new ArrayList<JTree>();
	HashMap<VNode,JLabel> contents = new HashMap<VNode,JLabel>();
	private JTextField searchTextField;
	private JLabel lblPropreties;
	private Directory workingDirectory = null;
	
	private static final ImageIcon VFS_ICON = new ImageIcon("gui\\Devices-drive-harddisk-icon.png");
	private static final ImageIcon VFS_ICON_SELECTED = new ImageIcon("gui\\Devices-drive-harddisk-icon-selected.png");
	
	private static final ImageIcon FILE_ICON = new ImageIcon("gui\\Document-Blank-icon.png");
	private static final ImageIcon FILE_ICON_SELECTED = new ImageIcon("gui\\Document-Blank-icon-selected.png");
	
	private static final ImageIcon DIRECTORY_ICON = new ImageIcon("gui\\Folder-icon.png");
	private static final ImageIcon DIRECTORY_ICON_SELECTED = new ImageIcon("gui\\Folder-icon-selected.png");
	
	private static final ImageIcon DIRECTORY_IMAGE = new ImageIcon("gui\\folder.png");
	private static final ImageIcon FILE_IMAGE = new ImageIcon("gui\\file.png");;
	//private static final ImageIcon DISK_IMAGE = new ImageIcon("gui\\Disk.png");;
	
	private static final ImageIcon DIRECTORY_IMAGE_SELECTED = new ImageIcon("gui\\folder-selected.png");;
	private static final ImageIcon FILE_IMAGE_SELECTED = new ImageIcon("gui\\file-selected.png");;
	
	// Creates a tree structure for the graphic interface from a given root node
	public static DefaultMutableTreeNode buildTree(VNode node){
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("");
		if(node.isRoot()){
			root = new DefaultMutableTreeNode(node.getVirtualDisk().name);
		}
		else{
			root = new DefaultMutableTreeNode(node);
		}
			
		if(node.isDirectory()){
			for(VNode c : ((Directory)node).getContentList()){
				DefaultMutableTreeNode a = buildTree((VNode) c);
			
				root.add(a);
			}
		}
		return root;
	}
	
	
	class VFSTreeCellRendrer implements TreeCellRenderer{
		private JLabel label;
		
		public VFSTreeCellRendrer() {
			label = new JLabel();
			label.setForeground(Color.WHITE);
        }
		
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			Object o = ((DefaultMutableTreeNode) value).getUserObject();
			if((o instanceof String)){
				if(selected)
					label.setIcon(VFS_ICON_SELECTED);
				else
					label.setIcon(VFS_ICON);
                label.setText(o.toString());
				return label;
			}
			VNode node = (VNode) o;

			if (node.isFile()) {
				if(selected)
					label.setIcon(FILE_ICON_SELECTED);
				else
					label.setIcon(FILE_ICON);
                label.setText(node.getName());
            } 
			else {
				if(selected)
					label.setIcon(DIRECTORY_ICON_SELECTED);
				else
					label.setIcon(DIRECTORY_ICON);
                label.setText(node.getName());
            }
            return label;
		}
	}


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUInterface frame = new GUInterface();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GUInterface() {
		setTitle("VFS Explorer");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 798, 511);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setFont(new Font("Tahoma", Font.PLAIN, 16));
		menuBar.setForeground(Color.WHITE);
		menuBar.setBackground(Color.DARK_GRAY);
		setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("Help");
		mnNewMenu.setForeground(Color.WHITE);
		mnNewMenu.setBackground(Color.DARK_GRAY);
		menuBar.add(mnNewMenu);
		mntmNewMenuItem.setFont(new Font("Tahoma", Font.PLAIN, 16));
		mntmNewMenuItem.setForeground(Color.WHITE);
		mntmNewMenuItem.setBackground(Color.DARK_GRAY);

		
		mnNewMenu.add(mntmNewMenuItem);
		
		mntmNewMenuItem.addActionListener(this);
		mntmNewMenuItem_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
		mntmNewMenuItem_1.setBackground(Color.DARK_GRAY);
		mntmNewMenuItem_1.setForeground(Color.WHITE);
		
		mnNewMenu.add(mntmNewMenuItem_1);
		
		mntmNewMenuItem_1.addActionListener(this);
		
		contentPane = new JPanel();
		contentPane.setBackground(Color.BLACK);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		//panel_1.add(scrollPane);
		
		
		JPanel searchPanel = new JPanel();
		searchPanel.setBackground(Color.BLACK);
		
		JScrollPane treeScrollPane = new JScrollPane();
		
		explorerPanel = new JPanel();
		explorerPanel.setForeground(Color.WHITE);
		explorerPanel.setBackground(Color.DARK_GRAY);
		
		JScrollPane explorerScrollPane = new JScrollPane(explorerPanel);
		explorerPanel.setLayout(new BoxLayout(explorerPanel, BoxLayout.PAGE_AXIS));
		
		
		JScrollPane infoScrollPane = new JScrollPane();
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(treeScrollPane, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(explorerScrollPane, GroupLayout.PREFERRED_SIZE, 378, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(infoScrollPane, GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE))
						.addComponent(searchPanel, GroupLayout.DEFAULT_SIZE, 748, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(12)
					.addComponent(searchPanel, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(infoScrollPane, GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
						.addComponent(explorerScrollPane, GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
						.addComponent(treeScrollPane, GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE))
					.addContainerGap())
		);
		
		JPanel infoPanel = new JPanel();
		infoPanel.setForeground(Color.WHITE);
		infoPanel.setBackground(Color.DARK_GRAY);
		infoScrollPane.setViewportView(infoPanel);
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
		
		lblPropreties = new JLabel("");
		lblPropreties.setForeground(Color.WHITE);
		lblPropreties.setFont(new Font("Tahoma", Font.PLAIN, 14));
		infoPanel.add(lblPropreties);
		
		JPanel treePanel = new JPanel();
		treePanel.setForeground(Color.WHITE);
		treePanel.setBackground(Color.DARK_GRAY);
		treeScrollPane.setViewportView(treePanel);
		treePanel.setLayout(new BoxLayout(treePanel, BoxLayout.PAGE_AXIS));
		searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		JButton btnPrevious = new JButton("Previous");
		btnPrevious.setForeground(Color.WHITE);
		btnPrevious.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnPrevious.setBackground(Color.DARK_GRAY);
		btnPrevious.setActionCommand("PREVIOUS");
		btnPrevious.addActionListener(this);
		searchPanel.add(btnPrevious);
		
		searchTextField = new JTextField();
		searchTextField.setBackground(Color.DARK_GRAY);
		searchTextField.setForeground(Color.WHITE);
		searchTextField.setFont(new Font("Tahoma", Font.PLAIN, 15));
		searchTextField.setCaretColor(Color.WHITE);
		searchPanel.add(searchTextField);
		searchTextField.setColumns(45);
		
		JButton btnGo = new JButton("go");
		btnGo.setForeground(Color.WHITE);
		btnGo.setBackground(Color.DARK_GRAY);
		btnGo.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnGo.setActionCommand("GO");
		searchPanel.add(btnGo);
		contentPane.setLayout(gl_contentPane);
		
		for(VirtualFileSystem vfs : vfsApp.getVFSDataBase()){
			JTree tree = new JTree(buildTree(vfs.rootDirectory));
			tree.setBackground(Color.DARK_GRAY);
			tree.setForeground(Color.WHITE);
			
			trees.add(tree);
			tree.setCellRenderer(new VFSTreeCellRendrer());
			tree.addTreeSelectionListener(this);
			tree.addMouseListener(this);
			
			JPanel flow = new JPanel(new FlowLayout(FlowLayout.LEFT));
			flow.setOpaque(false);
			
			flow.add(tree);
			
			treePanel.add(flow);
		}
	}


	@Override
	public void valueChanged(TreeSelectionEvent e) {
		if(!(e.getSource() instanceof JTree) ) return;
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)((JTree) e.getSource()).getLastSelectedPathComponent();

		/* if nothing is selected */ 
		if (node == null) return;

		/* React to the node selection. */
		if(node.getUserObject() instanceof String)
			updatePropreties(vfsApp.getVFSByName(node.toString()).rootDirectory);
		else{
			updatePropreties((VNode)node.getUserObject());
		}
		
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getActionCommand().equals("GO"))
			search();
		else if(arg0.getActionCommand().equals("PREVIOUS")){
			if(workingDirectory.isRoot()) return;
			updateExplorer(workingDirectory.getParent());
		}
		else if(arg0.getActionCommand().equals(mntmNewMenuItem.getText())){
			HelpWindow hw = new HelpWindow();
			hw.setVisible(true);
		}
		else if(arg0.getActionCommand().equals(mntmNewMenuItem_1.getText())){
			AboutWindow aw = new AboutWindow();
			aw.setVisible(true);
		}
	}
	

	@Override
	public void keyPressed(KeyEvent arg0) {

	}


	@Override
	public void keyReleased(KeyEvent arg0) {
		if(arg0.getSource().equals(searchTextField)){
			if(arg0.getKeyCode()==KeyEvent.VK_ENTER)
				search();
		}
	}


	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}
	
	
	private void updateExplorer(Object node){
		VNode vnode = null;
		if(node instanceof String){
			vnode = vfsApp.getVFSByName(node.toString()).rootDirectory;
		}
		else vnode = (VNode) node;
		
		if(vnode.isFile())
			return;
		
		Directory dir = (Directory) vnode;
		
		contents = new HashMap<VNode,JLabel>();
		explorerPanel.removeAll();
		
		int i=0;
		JPanel flow = new JPanel(new FlowLayout(FlowLayout.LEFT));
		flow.setOpaque(false);
		for(VNode vn : dir.getContentList()){
			
			JLabel label;
			if(vn.isFile())
				label = new JLabel(vn.getName(), FILE_IMAGE, JLabel.CENTER);
			else
				label = new JLabel(vn.getName(), DIRECTORY_IMAGE, JLabel.CENTER);
			label.setHorizontalTextPosition(JLabel.CENTER);
			label.setVerticalTextPosition(JLabel.BOTTOM);

			label.setForeground(Color.WHITE);
			label.setBackground(Color.DARK_GRAY);
			label.setFont(new Font("Tahoma", Font.PLAIN, 15));
			label.addMouseListener(this);
			
			flow.add(label);
			
			contents.put(vn,label);
			
			i++;
			if(i%6==0){
				explorerPanel.add(flow);
				flow = new JPanel(new FlowLayout(FlowLayout.LEFT));
				flow.setOpaque(false);
			}
		}
		
		if(!(i%6==0)){
			explorerPanel.add(flow);
		}
		
		explorerPanel.revalidate();
		explorerPanel.repaint();
		
		workingDirectory = dir;
		searchTextField.setText(workingDirectory.getAbsolutePathName());
		vfsApp.getPrompt(workingDirectory.getVirtualDisk().name);
		vfsApp.changeDir("", workingDirectory.getAbsolutePathName());
	}
	
	private void updatePropreties(VNode node){
		
		if(node.isRoot()){
			String text="<html> <b> Propreties: </b> <br> <br>";
			VirtualFileSystem vfs = node.getVirtualDisk();
			text +=   "<b>name :</b> "+vfs.name+"<br>"+
					  "<b>type :</b> Virtual File System <br>"+
					  "<b>free space :</b> "+VirtualFileSystem.getSimplifiedSize(vfs.getFreeSpace())+"<br>"+
					  "<b>occupied space :</b> "+VirtualFileSystem.getSimplifiedSize(vfs.getOccupiedSpace())+"<br>"+
					  "<b>total space :</b> "+VirtualFileSystem.getSimplifiedSize(vfs.totalSpace)+"<br>"+
					  "<b>virtual disk file name :</b> "+vfs.VDFileName+"<br></html>";
			
			lblPropreties.setText(text);
			return;
		}
		
		VNode vnode = node;
		String text="<html> <b> Propreties: </b> <br> <br>";
		
		text += 	  "<b>name :</b> "+((vnode.getParent()!=null)?vnode.getName():VirtualFileSystem.separator)+"<br>"+
					  "<b>type : </b>"+vnode.getType()+"<br>"+
					  "<b>size : </b>"+VirtualFileSystem.getSimplifiedSize(vnode.getSize())+"<br>"+
					  "<b>absolute path : </b>"+vnode.getAbsolutePath();
		
		if(vnode.isDirectory()){
			text += "<br> <b>files/directories included: </b><br> \t";
			for(VNode c : ((Directory) vnode).getContentList()){
				text += "&nbsp;&nbsp;&nbsp;"+c.getName()+"  :  "+c.getType()+"<br>";
			}
			text += "</html>";
		}
		
		/* Displays the files contained in the selected node */
		lblPropreties.setText(text);
	}
	
	private void search(){
		String name = searchTextField.getText();
		String text="<html> <b> search results: </b> <br> <br>";
		for(VirtualFileSystem vfs : vfsApp.getVFSDataBase()){
			try{
				List<VNode> found = vfs.rootDirectory.find(name);
				for(VNode n : found)
					text += n.getAbsolutePathName()+"\n";
			} catch (exceptions.NoSuchFileOrDirectoryException e){
				
			}
			text += "<br>";
		}
		for(int i=0;i<text.length();i++)
			if(text.charAt(i)=='\n')
				text = text.substring(0, i) + "<br>" + text.substring(i+1);
		text += "</html>";
		lblPropreties.setText(text);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getClickCount()==2){
			if(e.getSource() instanceof JTree){
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)((JTree) e.getSource()).getLastSelectedPathComponent();

				/* if nothing is selected */ 
				if (node == null) return;

				/* retrieve the node that was selected */ 
				Object nodeInfo = node.getUserObject();
				
				updateExplorer(nodeInfo);
			}
			else if(e.getSource() instanceof JLabel){
				JLabel label = (JLabel) e.getSource();
				for(VNode vn: contents.keySet()){
					if(vn.getName().equals(label.getText())){
						updateExplorer(vn);
						return;
					}
				}
			}
		}
		else if(e.getClickCount()==1){
			if(e.getSource() instanceof JLabel){
				JLabel label = (JLabel) e.getSource();
				
				
				for(VNode vn: contents.keySet()){
					if(vn.getName().equals(label.getText())){
						if(vn.isDirectory()){
							if(label.getIcon().equals(DIRECTORY_IMAGE_SELECTED))
								label.setIcon(DIRECTORY_IMAGE);
							else
								label.setIcon(DIRECTORY_IMAGE_SELECTED);
						}
						else{
							if(label.getIcon().equals(FILE_IMAGE_SELECTED))
								label.setIcon(FILE_IMAGE);
							else
								label.setIcon(FILE_IMAGE_SELECTED);
						}
						updatePropreties(vn);
					}
					
					else{
						if(vn.isDirectory()){
							contents.get(vn).setIcon(DIRECTORY_IMAGE);
						}
						else{
							contents.get(vn).setIcon(FILE_IMAGE);
						}
					}
				}
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
