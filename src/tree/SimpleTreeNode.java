package tree;

import java.util.*;

/**
 * A simple representation of a tree structure. 
 * This class is absract because the tree nodes do not contain any information.
 * @author Omar
 */
public abstract class SimpleTreeNode implements java.io.Serializable{
	private static final long serialVersionUID = -8727710124856468534L;
	
	/** list of children of the receiver node */
	protected Vector<SimpleTreeNode> children;	
	/** checks if node allows children */
	protected boolean allowsChildren;						
	/** the parent of the receiver node in the tree where it belongs */
	protected SimpleTreeNode parent;	
	/** the root of the tree where the recevier belongs */
	protected SimpleTreeNode root;								
	
	/**
	 * creates a new tree node as root
	 */
	protected SimpleTreeNode(){
		children = new Vector<SimpleTreeNode>();
		allowsChildren = true;
		parent = null;
		root = this;
	}
	
	/**
	 * adds the given argument as a child to the receiver
	 * @param child node to add as child
	 */
	protected void addChild(SimpleTreeNode child){
		if(allowsChildren){
			child.parent = this;
			child.root = this.root;
			children.add(child);
		}
	}
	
	/**
	 * removes the receiver from the tree structure where it belongs
	 */
	protected void cutLinkToTree(){
		if(parent!=null){
			parent.children.remove(this);
		}
	}
	
	/**
	 * returns the list of children of the receiver's node
	 * @return the receiver's children list
	 */
	protected List<SimpleTreeNode> getChildrenList(){
		return children;
	}
	
	/**
	 * returns the number of children of the receiver's node
	 * @return the receiver's children list size
	 */
	protected int getChildrenCount(){
		return children.size();
	}
	
	/**
	 * checks if the node is a tree root
	 * @return a boolean to check if the receiver is a tree root
	 */
	protected boolean isRoot() {
		return parent==null;
	}
	
	/**
	 * checks if the node is a tree leaf
	 * @return a boolean to check if the receiver is a tree leaf
	 */
	protected boolean isLeaf() {
		return children.isEmpty();
	}
	
	/**
	 * returns the root node of the tree where the node belongs
	 * @return the receiver's root node of the tree where it belongs
	 */
	protected SimpleTreeNode getRoot(){
		return root;
	}
	
	/**
	 * returns the parent node in the tree where the node belongs
	 * @return the receiver's parent node
	 */
	protected SimpleTreeNode getParent(){
		return parent;
	}
	
	/**
	 * checks if the node allows children addition
	 * @return the value of allowsChildren
	 */
	protected boolean getAllowsChildren(){
		return allowsChildren;
	}
	
	/**
	 * sets the value of allowsChildren
	 * @param allowsChildren true to allow children addition, false to stop it. 
	 */
	protected void setAllowsChildren(boolean allowsChildren){
		this.allowsChildren = allowsChildren;
	}
	
	/**
	 * returns the path to the receiver's node in the tree where it belongs, starting from the root node.
	 * @return the list of nodes leading to the receiver in the tree where it belongs
	 */
	protected List<SimpleTreeNode> getPathFromRoot(){
		List<SimpleTreeNode> path = new ArrayList<SimpleTreeNode>();
		SimpleTreeNode n = this;
		while(n.parent!=null){
			path.add(0, n.parent);
			n = (SimpleTreeNode) n.parent;
		}
		return path;
	}
	
	@Override
	public abstract boolean equals(Object o);		//equals method must be implemented so that operations can be preformed.
}
