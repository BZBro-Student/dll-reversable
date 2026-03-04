/**
 * Linkable node class for use in linked lists.
 * Supports both singly and doubly linked lists.
 * 
 * @author Broden
 */

public class Node<T> {
    private Node<T> nextNode;
    private Node<T> prevNode;
    private T element;

    /**
     * Initialize node with the given element
     * 
     * @param element element to be stored in node
     */
    public Node(T element) {
        this.element = element;
        nextNode = null;
        prevNode = null;
    }
    /**
     * gets NextNode
     * @return the value of NextNode
     */
    public Node<T> getNextNode() {
        return nextNode;
    }
    /**
     * sets value of nextNode
     * @param nextNode
     */
    public void setNextNode(Node<T> nextNode) {
        this.nextNode = nextNode;
    }
    
    /**
     * gets element
     * @return the value of the element
     */
    public T getElement() {
        return element;
    }
    /**
     * sets value of the element
     * @param element value of the element
     */
    public void setElement(T element) {
        this.element = element;
    }
    /**
     * gets prevNode
     * @return the value of prevNode
     */
    public Node<T> getPrevNode() {
        return prevNode;
    }
    /**
     * sets value of prevNode
     * @param prevNode
     */
    public void setPrevNode(Node<T> prevNode) {
        this.prevNode = prevNode;
    }
}
