package implementation;

//import eg.edu.alexu.csd.filestructure.btree.IBTreeNode;

import javax.management.RuntimeErrorException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

//import IBTree;

/*
 * here i implement IBTreeNode interface and its methods
 * 
 */
class TreeNode <K extends Comparable<K>, V> implements IBTreeNode<K,V>
{
  private int numOfKeys;
  private boolean ifLeaf;   //variable to check if the node is children and leaf
  private List<K> keys;
  private List<V> values;   
  private List<IBTreeNode<K, V>> children;
  
   public TreeNode()
   {
	   this.ifLeaf=false;
	   this.numOfKeys=0;
	   this.keys=new ArrayList<>();
	   this.values=new ArrayList<>();
	   this.children=new ArrayList<>();
   }
	@Override
	public int getNumOfKeys() {
		
		return this.numOfKeys;
	}

	@Override
	public void setNumOfKeys(int numOfKeys) {
		this.numOfKeys=numOfKeys;
		
	}

	@Override
	public boolean isLeaf() {
		
		return this.ifLeaf;
	}

	@Override
	public void setLeaf(boolean isLeaf) {
		this.ifLeaf=isLeaf;
		
	}

	@Override
	public List<K> getKeys() {
		
		return this.keys;
	}

	@Override
	public void setKeys(List<K> keys) {
		// TODO Auto-generated method stub
		this.keys=keys;
	}

	@Override
	public List<V> getValues() {
		return this.values;
		
	}

	@Override
	public void setValues(List<V> values) {
		// TODO Auto-generated method stub
		this.values=values;
	}

	@Override
	public List<IBTreeNode<K, V>> getChildren() {
		
		return this.children;
	}

	@Override
	public void setChildren(List<IBTreeNode<K, V>> children) {
		// TODO Auto-generated method stub
		this.children=children;
	}
	
}

class KeyPair<K extends Comparable<K>,V>{
	public IBTreeNode<K,V> node;
	public int index;

	KeyPair(IBTreeNode<K,V> node , int index){
		this.node = node;
		this.index = index;
	}
}

public class BTreeImpl <K extends Comparable<K>, V> implements IBTree<K,V> {
    private int size;
	private int t;  //this is private variable that represent the minimum degree of B tree
	IBTreeNode<K, V> root;  /*here this is the root of B tree and it contain values and key and children we
	access all method of treeNode from it*/
	
	public  BTreeImpl(int minimumDeg)
	{
		if (minimumDeg < 2) {
			throw new RuntimeErrorException(null);
		}
		this.t=minimumDeg;
        this.size = 0;
	}
	
	@Override
	public int getMinimumDegree() {
		return t;
	}

	@Override
	public IBTreeNode<K, V> getRoot() {
		// TODO Auto-generated method stub
		return root;
	}

	@Override
	public void insert(K key, V value) {
       if(key ==null || value ==null)
	   {
		   throw new RuntimeErrorException(null);
	   }
		if(root ==null)
		{
			root=new TreeNode<>();
			root.setLeaf(true);
			root.getKeys().add(key);
			root.getValues().add(value);
			root.setNumOfKeys(1);
		}
		if(root.getNumOfKeys()==((2 * t)-1)) //if root is full
			{
				IBTreeNode<K, V> oldRoot =root;
				root=new TreeNode<>();
				root.getChildren().add(oldRoot);
				split(0,root);
			}
		insertNonFull(key ,root,value);  //if the root is not null or after splitting full root i go here
        this.size++;
	}
	public void insertNonFull(K key ,IBTreeNode<K, V> rt,V value)
	{
		if(rt.isLeaf())
		{
				int indx = 0;
				while (indx < rt.getNumOfKeys() && key.compareTo(rt.getKeys().get(indx)) > 0)
					indx++;
				if (indx < rt.getNumOfKeys() && key.compareTo(rt.getKeys().get(indx)) == 0)
					return;
				else {
					rt.getKeys().add(indx, key);
					rt.getValues().add(indx, value);
					rt.setNumOfKeys(rt.getNumOfKeys() + 1);
				 }
			return;
		}
		int indx=0;
		while(indx<rt.getNumOfKeys() && key.compareTo(rt.getKeys().get(indx))>0)
			indx++;
		if(indx<rt.getNumOfKeys() && key.compareTo(rt.getKeys().get(indx))==0)
			return ;
		else {
			if(rt.getChildren().get(indx).getNumOfKeys()== ((2 * t)-1)) //if full then split
				split(indx,rt);
			if(indx<rt.getNumOfKeys() && key.compareTo(rt.getKeys().get(indx))>0)
				indx++;
			else if(indx<rt.getNumOfKeys() && key.compareTo(rt.getKeys().get(indx))==0)
				return ;
			
			insertNonFull(key ,rt.getChildren().get(indx),value);
			
		}
		
	}
   public void split(int indx ,IBTreeNode<K, V> parent )
   {
	   IBTreeNode<K, V> z=new TreeNode<>();
	   IBTreeNode<K, V> y =parent.getChildren().get(indx);
	   
	   // make element at t-1 go up to root array
	   parent.getKeys().add(indx,y.getKeys().get(t-1));
	   parent.getValues().add(indx,y.getValues().get(t-1));
	   parent.getChildren().add(indx+1,z);//////
	   parent.setNumOfKeys(parent.getNumOfKeys()+1);
	   
	   //handling the list of splitted nodes 
	   
	   z.setKeys(new ArrayList<>(y.getKeys().subList(t, 2 * t-1)));
	   z.setValues(new ArrayList<>(y.getValues().subList(t, 2 * t-1)));
	   y.getKeys().subList(t-1, 2*t-1).clear();
	   y.getValues().subList(t-1, 2*t-1).clear();
	   
	   //setting number of keys of each sublist
	   z.setNumOfKeys(t-1);
	   y.setNumOfKeys(t-1);
	   z.setLeaf(y.isLeaf());
	   
	   // if y is not a leaf node and has children to added
	   if(! y.isLeaf())
	   {
		   z.setChildren(new ArrayList<>(y.getChildren().subList(t, 2 * t)));
		   y.getChildren().subList(t, 2*t).clear();
	   }
	   
   }
	
	@Override
	public V search(K key) {
		// TODO Auto-generated method stub
		if(key ==null)
		{
			throw new RuntimeErrorException(null);
		}
		if(root==null)
		{
			return null;
		}
		return treeSearch(root,key);
	}

	@Override
	public boolean delete(K key) {
		// TODO Auto-generated method stub
		Stack<KeyPair<K,V>> deleteKeyPath = new Stack<>();
		deleteKeyPath = treeNodeSearch(root , key , deleteKeyPath);
		if (deleteKeyPath == null) {
			return false;
		}

		KeyPair<K,V> deleteKeyPair = deleteKeyPath.pop();
		KeyPair<K,V> deleteKeyParentPair = deleteKeyPath.pop();

		int childrenNumberAfterDeletion = deleteKeyPair.node.getNumOfKeys();//+1-1
		//case I (key is in leaf node
		if (deleteKeyPair.node.isLeaf()){
			//case I1 (The deletion does not violate the property of Btree)
			if (childrenNumberAfterDeletion >= this.getMinimumDegree()){
				//decrement keys number , delete key , delete value , delete child (assumption)
				deleteKeyPair.node.setNumOfKeys(deleteKeyPair.node.getNumOfKeys()-1);
				deleteKeyPair.node.getKeys().remove(deleteKeyPair.index);
				deleteKeyPair.node.getValues().remove(deleteKeyPair.index);
				deleteKeyPair.node.getChildren().remove(deleteKeyPair.index);
			}
			//case I2 (the deletion violates)
			else{
				//if one sibling is borrowable
				IBTreeNode<K,V> leftSiblingNode = getLeftSiblingNode(deleteKeyPair.node , deleteKeyParentPair);
				IBTreeNode<K,V> rightSiblingNode = getRightSiblingNode(deleteKeyPair.node , deleteKeyParentPair);
				if (leftSiblingNode != null && leftSiblingNode.getNumOfKeys() != this.getMinimumDegree()-1){//if left sibling is borrowable
					borrowFromSibling(deleteKeyPair , leftSiblingNode , deleteKeyParentPair , true);
				}
				else if (rightSiblingNode != null && rightSiblingNode.getNumOfKeys() != this.getMinimumDegree()){
					borrowFromSibling(deleteKeyPair , rightSiblingNode , deleteKeyParentPair , false);
				}
				//if neither are borrowable
				else{
//					replace deletion key with parent then merge this node with sibling and
//					//delete key >> add parent key instead  >> merge with sibling
					//left sibling
					if (leftSiblingNode != null){
						removeData(deleteKeyPair.node , deleteKeyPair.index);
						addData(deleteKeyParentPair.node, deleteKeyParentPair.index , deleteKeyPair.node , 0);
						IBTreeNode<K,V> mergedNode = merge(leftSiblingNode , deleteKeyPair.node);

						//adjust parent children
						//remove deleteNode from parent`s children and add merged node
						deleteKeyParentPair.node.getChildren().remove(deleteKeyParentPair.index);
						deleteKeyParentPair.node.getChildren().remove(deleteKeyParentPair.index-1);
						deleteKeyParentPair.node.getChildren().add(deleteKeyParentPair.index-1 , mergedNode );

					}
					//right sibling
					else if(rightSiblingNode != null){
						//delete key >> add parent key instead  >> merge with sibling
						removeData(deleteKeyPair.node , deleteKeyPair.index);
						addData(deleteKeyParentPair.node, deleteKeyParentPair.index, deleteKeyPair.node
								, deleteKeyPair.node.getNumOfKeys());

						IBTreeNode<K,V> mergedNode = merge(deleteKeyPair.node , rightSiblingNode);

						//adjust parent`s children
						//remove deleteNode from parent`s children and add merged node
						deleteKeyParentPair.node.getChildren().remove(deleteKeyParentPair.index);
						deleteKeyParentPair.node.getChildren().remove(deleteKeyParentPair.index);
						deleteKeyParentPair.node.getChildren().add(deleteKeyParentPair.index ,mergedNode );

					}
					else{
						System.out.println("trying to borrow from two nulls siblings");
						System.exit(0);
					}

				}
			}
		}




		return true;
	}


    public int getSize(){
        return this.size;
    }

	private TreeNode<K,V> merge(IBTreeNode<K,V> leftNode , IBTreeNode<K,V> rightNode){
		TreeNode<K,V> mergedNode = new TreeNode<>();

		List<K> mergedKeys = leftNode.getKeys();
		List<V> mergedValues = leftNode.getValues();
		for (int i = 0; i < rightNode.getNumOfKeys(); i++) {
			mergedKeys.add(rightNode.getKeys().get(i));
			mergedValues.add(rightNode.getValues().get(i));
		}

		mergedNode.setKeys(mergedKeys);
		mergedNode.setValues(mergedValues);
		mergedNode.setNumOfKeys(leftNode.getNumOfKeys() + rightNode.getNumOfKeys());
		mergedNode.setLeaf(leftNode.isLeaf());

		return mergedNode;
	}

	private IBTreeNode<K,V> getPredecessorNode(KeyPair<K,V> keyPair){
		IBTreeNode<K,V> p = keyPair.node;
		p = p.getChildren().get(keyPair.index);
		if (p == null){
			return null;
		}
		while (p.getChildren().get(p.getNumOfKeys()) != null){
			p = p.getChildren().get(p.getNumOfKeys());
		}

		return p;
	}

	private IBTreeNode<K,V> getSuccessorNode(KeyPair<K,V> keyPair){
		IBTreeNode<K,V> p = keyPair.node;
		p = p.getChildren().get(keyPair.index + 1);
		if (p == null){
			return null;
		}
		while (p.getChildren().get(0) != null){
			p = p.getChildren().get(0);
		}

		return p;
	}

	private IBTreeNode<K,V> getLeftSiblingNode(IBTreeNode<K,V> node , KeyPair<K,V> parent){
		if (parent == null || parent.index == 0){
			return null;
		}
		return parent.node.getChildren().get(parent.index-1);
	}

	private IBTreeNode<K,V> getRightSiblingNode(IBTreeNode<K,V> node , KeyPair<K,V> parent){
		if (parent == null || parent.index == parent.node.getNumOfKeys()){
			return null;
		}
		return parent.node.getChildren().get(parent.index+1);
	}

	private void borrowFromSibling(KeyPair<K,V> deleteKeyPair , IBTreeNode<K,V> sibling , KeyPair<K,V> parent , boolean leftSibling){
		IBTreeNode<K,V> node = deleteKeyPair.node;
		int deleteKeyIndex = deleteKeyPair.index;
		int siblingKeyIndex;
		int parentKeyIndex;
		int parentKeyDestIndex;
		if (leftSibling){//left sibling
			siblingKeyIndex = sibling.getNumOfKeys()-1;
			parentKeyIndex = parent.index-1;
			parentKeyDestIndex = 0;
		}
		else{ // right sibling
			siblingKeyIndex = 0;
			parentKeyIndex = parent.index;
			parentKeyDestIndex = -1;
		}

		//moving parent data to deleted node
		//delete the data and add parent data
			removeData(deleteKeyPair.node , deleteKeyIndex);
			addData(parent.node, parentKeyIndex , deleteKeyPair.node , parentKeyDestIndex);

//			replaceData(parent.node,parentKeyIndex , node , deleteKeyIndex);

			//moving data from sibling to parent
			//delete from parent and add from sibling to parent
			removeData(parent.node , parentKeyIndex);
			addData(sibling , siblingKeyIndex , parent.node , parent.index);

//			replaceData(sibling , siblingKeyIndex , parent.node,  parentKeyIndex);

			//deleting the key from sibling
			removeData(sibling , siblingKeyIndex);

	}

	private void addData(IBTreeNode<K,V> fromNode, int fromIndex , IBTreeNode<K,V> toNode, int parentKeyDestIndex) {
		if (parentKeyDestIndex == -1){
			toNode.getKeys().add(fromNode.getKeys().get(fromIndex));
			toNode.getValues().add(fromNode.getValues().get(fromIndex));
		}
		else{
			toNode.getKeys().add(parentKeyDestIndex , fromNode.getKeys().get(fromIndex));
			toNode.getValues().add(parentKeyDestIndex , fromNode.getValues().get(fromIndex));
		}
		toNode.setNumOfKeys(toNode.getKeys().size());
	}

	private void removeData(IBTreeNode<K,V> node , int index){
		node.getKeys().remove(index);
		node.getValues().remove(index);
		node.setNumOfKeys(node.getKeys().size());
	}

	private void replaceData(IBTreeNode<K,V> node1 , int i1 , IBTreeNode<K,V> node2 , int i2){
		node2.getKeys().remove(i2);
		node2.getKeys().add(i2 , node1.getKeys().get(i1));

		node2.getValues().remove(i2);
		node2.getValues().add(i2 , node1.getValues().get(i1));


		node1.setNumOfKeys(node1.getKeys().size());
	}

//	private void rearrangeData(IBTreeNode<K,V> node){
//		List<K> keys = node.getKeys();
//		List<V> values = node.getValues();
//		for (int i = 0; i < keys.size() ; i++) {
//			for (int j = 0; j < keys.size()-i; j++) {
//				if (keys.get(i).compareTo(keys.get(j)) >= 1){
//					K tempK = keys.get(j);
//					V tempV = values.get(j);
//
////					keys.get(j) = keys.get(i);
//				}
//			}
//		}
//	}

	private V treeSearch(IBTreeNode<K, V> rt,K key)
    {
    	int i=0;
		while(i<rt.getNumOfKeys() && key.compareTo(rt.getKeys().get(i))>0)
		{
			i++;
		}
		if(i<rt.getNumOfKeys() && key.compareTo(rt.getKeys().get(i))==0)
		{
			return rt.getValues().get(i);
		}
		if(rt.isLeaf())  //if we go to leaf and can not reach to the certain key
		{
			return null;
		}
	   return treeSearch(rt.getChildren().get(i),key) ;
    }

	private Stack<KeyPair<K,V>> treeNodeSearch(IBTreeNode<K, V> rt, K key , Stack<KeyPair<K,V>> pathStack){
    	int i=0;
		while(i<rt.getNumOfKeys() && key.compareTo(rt.getKeys().get(i))>0)
		{
			i++;
		}
		if(i<rt.getNumOfKeys() && key.compareTo(rt.getKeys().get(i))==0)
		{
			KeyPair<K,V> keyPair = new KeyPair<>(rt , i);
			pathStack.push(keyPair);
			return pathStack;
		}
		if(rt.isLeaf())  //if we go to leaf and can not reach to the certain key
		{
			return null;
		}
		KeyPair<K,V> keyPair = new KeyPair<>(rt , i);
		pathStack.push(keyPair);
	   return treeNodeSearch(rt.getChildren().get(i),key , pathStack) ;
    }

	public static void main(String[] args)
	{
		BTreeImpl<Integer, String> btree =new BTreeImpl<Integer,String>(3);
		btree.insert(2, "add");
		btree.insert(7, "fds");
		btree.insert(9, "nhg");
		btree.insert(15, "eeg");
		
		System.out.println(btree.search(2));
		System.out.println(btree.search(9));
		System.out.println(btree.search(15));
	}
}
