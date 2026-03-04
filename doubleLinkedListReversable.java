public class doubleLinkedListReversable<T> extends IUDoubleLinkedList<T> {
    public doubleLinkedListReversable () {
        super();
    }

    public void reverse() {
        Node<T> currNode = this.head;
        for (int i = 0; i < this.size(); i++) {
            Node<T> nextNode = currNode.getNextNode();
            currNode.setNextNode(currNode.getPrevNode());
            currNode.setPrevNode(nextNode);
            currNode = nextNode;
        }
        currNode = this.head;
        this.head = this.tail;
        this.tail = currNode;
    }
    
}

