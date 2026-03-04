import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Implements the IndexedUnsortedList interface using a doubly linked node based
 * list.
 * Supports Iterators and ListIterators.
 * 
 * @author Broden
 */
public class IUDoubleLinkedList<T> implements IndexedUnsortedList<T> {
    enum WhereStart {
        HEADSTART, TAILSTART
    };

    private Node<T> head, tail;
    private int size;
    private int modCount;

    IUDoubleLinkedList() {
        head = tail = null;
        size = 0;
        modCount = 0;
    }

    @Override
    public void addToFront(T element) {
        Node<T> newNode = new Node<>(element);
        newNode.setNextNode(head);
        // Empty list case
        if (tail == null) {
            tail = newNode;
        } else {
            head.setPrevNode(newNode);
        }
        head = newNode;
        size++;
        modCount++;
    }

    @Override
    public void addToRear(T element) {
        Node<T> newNode = new Node<T>(element);
        // Empty list case
        if (size == 0) {
            head = newNode;
            tail = newNode;
            // Normal case
        } else {
            tail.setNextNode(newNode);
            newNode.setPrevNode(tail);
            tail = newNode;
        }
        size++;
        modCount++;
    }

    @Override
    public void add(T element) {
        addToRear(element);
    }

    @Override
    public void addAfter(T element, T target) {
        Node<T> currNode = head;
        Node<T> newNode = new Node<T>(element);
        boolean inserted = false;
        // Loops through all nodes until broken out of or until there is no more nodes
        while (currNode != null) {
            if (currNode.getElement().equals(target)) {
                Node<T> tempNext = currNode.getNextNode();
                currNode.setNextNode(newNode);
                newNode.setPrevNode(currNode);
                newNode.setNextNode(tempNext);

                if (tempNext != null) {
                    tempNext.setPrevNode(newNode);
                } else {
                    tail = newNode;
                }
                size++;
                modCount++;
                inserted = true;
                // Ends loop
                break;
            }
            currNode = currNode.getNextNode();
        }
        if (inserted == false) {
            throw new NoSuchElementException();
        }

    }

    @Override
    public void add(int index, T element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        // If index is zero
        if (index == 0) {
            addToFront(element);
            // If index is the end of the list
        } else if (index == size) {
            addToRear(element);
            // Normal case
        } else {
            Node<T> newNode = new Node<>(element);
            int currIndex = 0;
            Node<T> currNode = null;
            // Initialize as headstart just in case something breaks the logic the loop will
            // run at head no matter what
            WhereStart startPosition = WhereStart.HEADSTART;
            if (index > size / 2) {
                startPosition = WhereStart.TAILSTART;
            }
            switch (startPosition) {
                case HEADSTART:
                    // Start at head
                    currNode = head;
                    while (currNode != null) {
                        if (currIndex == index - 1) {
                            Node<T> tempNext = currNode.getNextNode();
                            currNode.setNextNode(newNode);
                            newNode.setPrevNode(currNode);
                            tempNext.setPrevNode(newNode);
                            newNode.setNextNode(tempNext);
                            size++;
                            modCount++;
                            break;
                        }
                        currNode = currNode.getNextNode();
                        // Move forward
                        currIndex++;
                    }
                    break;
                case TAILSTART:
                    // Start at tail
                    currNode = tail;
                    currIndex = size - 1;
                    while (currNode != null) {
                        if (currIndex == index - 1) {
                            Node<T> tempNext = currNode.getNextNode();
                            currNode.setNextNode(newNode);
                            newNode.setPrevNode(currNode);
                            tempNext.setPrevNode(newNode);
                            newNode.setNextNode(tempNext);
                            size++;
                            modCount++;
                            break;
                        }
                        currNode = currNode.getPrevNode();
                        // Move backward
                        currIndex--;
                    }
                    break;
            }
        }
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        T returnValue = head.getElement();
        head = head.getNextNode();
        // Removes the reference to the original
        if (head != null) {
            head.setPrevNode(null);
        }
        // Handles the case where the list becomes empty
        if (head == null) {
            tail = null;
        }
        size--;
        modCount++;
        return returnValue;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        T returnValue = tail.getElement();
        // Case where list becomes empty
        if (size == 1) {
            returnValue = head.getElement();
            head = null;
            tail = null;
            // Normal case
        } else {
            returnValue = tail.getElement();
            tail = tail.getPrevNode();
            tail.setNextNode(null);
        }
        size--;
        modCount++;
        return returnValue;
    }

    @Override
    public T remove(T element) {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        T returnVal = null;
        // Checks head to see if that is the correct node
        if (head.getElement().equals(element)) {
            returnVal = head.getElement();
            head = head.getNextNode();
            if (head == null) {
                tail = null;
            } else {
                head.setPrevNode(null);
            }
            size--;
            modCount++;
            // Begins checking the rest of the nodes
        } else {
            Node<T> currNode = head;
            while (currNode != null) {
                if (currNode.getElement().equals(element)) {
                    returnVal = currNode.getElement();
                    Node<T> tempPrevNode = currNode.getPrevNode();
                    Node<T> tempNextNode = currNode.getNextNode();
                    // Makes sure next node is not null before switching values
                    if (tempNextNode != null) {
                        tempPrevNode.setNextNode(tempNextNode);
                        tempNextNode.setPrevNode(tempPrevNode);
                        // If nextNode is null, remove tail
                    } else {
                        tail = tempPrevNode;
                        tail.setNextNode(null);
                    }
                    size--;
                    modCount++;
                    break;
                }
                currNode = currNode.getNextNode();
            }
        }
        if (returnVal == null) {
            throw new NoSuchElementException();
        }
        return returnVal;
    }

    @Override
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        T returnValue = null;
        // Remove first
        if (index == 0) {
            returnValue = removeFirst();
            // Remove last
        } else if (index == size - 1) {
            returnValue = removeLast();
            // Remove middle
        } else {
            int currIndex = 0;
            Node<T> currNode = null;
            // Initialize as headstart just in case something breaks the logic
            WhereStart startPosition = WhereStart.HEADSTART;
            if (index > size / 2) {
                startPosition = WhereStart.TAILSTART;
            }
            switch (startPosition) {
                case HEADSTART:
                    currNode = head;
                    while (currNode != null) {
                        if (currIndex == index) {
                            returnValue = currNode.getElement();
                            // Store values surrounding currNode
                            Node<T> tempPrevNode = currNode.getPrevNode();
                            Node<T> tempNextNode = currNode.getNextNode();
                            // Destroy currNode
                            currNode.setNextNode(null);
                            currNode.setPrevNode(null);
                            // Connect currNode surrounding values
                            tempPrevNode.setNextNode(tempNextNode);
                            tempNextNode.setPrevNode(tempPrevNode);
                            size--;
                            modCount++;
                            break;
                        }
                        // Forward
                        currNode = currNode.getNextNode();
                        currIndex++;
                    }
                    break;
                case TAILSTART:
                    currNode = tail;
                    currIndex = size - 1;
                    while (currNode != null) {
                        if (currIndex == index) {
                            returnValue = currNode.getElement();
                            Node<T> tempPrevNode = currNode.getPrevNode();
                            Node<T> tempNextNode = currNode.getNextNode();
                            currNode.setNextNode(null);
                            currNode.setPrevNode(null);
                            tempPrevNode.setNextNode(tempNextNode);
                            tempNextNode.setPrevNode(tempPrevNode);
                            size--;
                            modCount++;
                            break;
                        }
                        // Backward
                        currNode = currNode.getPrevNode();
                        currIndex--;
                    }
                    break;
            }

        }
        return returnValue;
    }

    @Override
    public void set(int index, T element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        // Initialize as headstart just in case something breaks the logic
        WhereStart startPosition = WhereStart.HEADSTART;
        if (index > size / 2) {
            startPosition = WhereStart.TAILSTART;
        }
        int currIndex = 0;
        Node<T> currNode = null;
        // Each case goes through the list until reaching that index
        switch (startPosition) {
            case HEADSTART:
                currIndex = 0;
                currNode = head;
                while (currIndex < index) {
                    currNode = currNode.getNextNode();
                    currIndex++;
                }
                break;
            case TAILSTART:
                currIndex = size - 1;
                currNode = tail;
                while (currIndex > index) {
                    currNode = currNode.getPrevNode();
                    currIndex--;
                }
                break;
        }
        // Set the node element to element that the case ends on
        currNode.setElement(element);
        modCount++;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        // Initialize as headstart just in case something breaks the logic
        WhereStart startPosition = WhereStart.HEADSTART;
        if (index > size / 2) {
            startPosition = WhereStart.TAILSTART;
        }
        int currIndex = 0;
        Node<T> currNode = null;
        // Navigate through list
        switch (startPosition) {
            case HEADSTART:
                currIndex = 0;
                currNode = head;
                while (currIndex < index) {
                    currNode = currNode.getNextNode();
                    currIndex++;
                }
                break;
            case TAILSTART:
                currIndex = size - 1;
                currNode = tail;
                while (currIndex > index) {
                    currNode = currNode.getPrevNode();
                    currIndex--;
                }
                break;
        }
        return currNode.getElement();
    }

    @Override
    public int indexOf(T element) {
        Node<T> currNode = head;
        int currIndex = 0;
        // Initialize at -1 since index of returns -1 if the element is never found
        int foundIndex = -1;
        while (currNode != null) {
            // Checks to see if equal
            if (currNode.getElement().equals(element)) {
                foundIndex = currIndex;
            }
            // If our index is no longer -1, found the element and break out of the loop
            if (foundIndex != -1) {
                break;
            }
            currNode = currNode.getNextNode();
            currIndex++;
        }
        return foundIndex;
    }

    @Override
    public T first() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return head.getElement();
    }

    @Override
    public T last() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return tail.getElement();
    }

    @Override
    public boolean contains(T target) {
        return indexOf(target) > -1;
    }

    @Override
    public boolean isEmpty() {
        boolean isEmpty = false;
        if (head == null && tail == null) {
            isEmpty = true;
        }
        return isEmpty;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String toString() {
        StringBuilder arrayStringBuilder = new StringBuilder();
        arrayStringBuilder.append("[");

        for (T element : this) {
            arrayStringBuilder.append(element.toString());
            arrayStringBuilder.append(", ");
        }
        if (!isEmpty()) {
            arrayStringBuilder.delete(arrayStringBuilder.length() - 2, arrayStringBuilder.length());
        }
        arrayStringBuilder.append("]");
        return arrayStringBuilder.toString();
    }

    @Override
    public Iterator<T> iterator() {
        return new IUDoubleLinkedListListIterator();
    }

    /**
     * Private inner class containing all of the functionality of a ListIterator
     * compatible with
     * a double linked list. This class also provides functionality for our basic
     * iterator.
     * 
     */
    private class IUDoubleLinkedListListIterator implements ListIterator<T> {
        private Node<T> currLocation;
        private Node<T> lastReturnedNode;
        private Boolean canRemoveOrAdd;
        private int expectedModCount;
        private int currIndex;

        IUDoubleLinkedListListIterator() {
            currLocation = head;
            lastReturnedNode = null;
            canRemoveOrAdd = false;
            expectedModCount = modCount;
            currIndex = 0;
        }

        IUDoubleLinkedListListIterator(int startingIndex) {
            if (startingIndex < 0 || startingIndex > size) {
                throw new IndexOutOfBoundsException();
            }
            lastReturnedNode = null;
            canRemoveOrAdd = false;
            expectedModCount = modCount;
            // Initialize as headstart just in case something breaks the logic, it will work anyway.
            WhereStart startPosition = WhereStart.HEADSTART;
            if (startingIndex > size / 2) {
                startPosition = WhereStart.TAILSTART;
            }
            switch (startPosition) {
                case HEADSTART:
                    currLocation = head;
                    currIndex = 0;
                    while (currIndex < startingIndex) {
                        currLocation = currLocation.getNextNode();
                        currIndex++;
                    }
                    break;
                case TAILSTART:
                    currLocation = null;
                    currIndex = size;
                    while (currIndex > startingIndex) {
                        // this case is for the very end of the list where there is no value
                        if (currLocation == null) {
                            currLocation = tail;
                        } else {
                            currLocation = currLocation.getPrevNode();
                        }
                        currIndex--;
                    }
                    break;
            }
        }

        /**
         * Checks to see if the array has changed using outside methods,
         * to help enforce fail fast behavior
         */
        private void hasChanged() {
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public boolean hasNext() {
            hasChanged();
            return currLocation != null;
        }

        @Override
        public T next() {
            T returnValue = null;
            if (hasNext()) {
                lastReturnedNode = currLocation;
                returnValue = currLocation.getElement();
                currLocation = currLocation.getNextNode();
                canRemoveOrAdd = true;
                currIndex++;
            } else {
                throw new NoSuchElementException();
            }
            return returnValue;
        }

        @Override
        public boolean hasPrevious() {
            hasChanged();
            return currIndex > 0;
        }

        @Override
        public T previous() {
            hasChanged();
            T returnValue = null;
            if (hasPrevious()) {
                // If starting at the end of the list
                if (currLocation == null) {
                    currLocation = tail;
                    // Normal Case
                } else {
                    currLocation = currLocation.getPrevNode();
                }
                lastReturnedNode = currLocation;
                returnValue = currLocation.getElement();
                canRemoveOrAdd = true;
            } else {
                throw new NoSuchElementException();
            }
            currIndex--;
            return returnValue;
        }

        @Override
        public int nextIndex() {
            hasChanged();
            return currIndex;
        }

        @Override
        public int previousIndex() {
            hasChanged();
            return currIndex - 1;
        }

        @Override
        public void remove() {
            hasChanged();
            if (!canRemoveOrAdd) {
                throw new IllegalStateException();
            }
            Node<T> tempPrevNode = lastReturnedNode.getPrevNode();
            Node<T> tempNextNode = lastReturnedNode.getNextNode();
            // Node being removed is head
            if (tempPrevNode == null) {
                head = tempNextNode;
            } else {
                tempPrevNode.setNextNode(tempNextNode);
            }
            // Node being removed is tail
            if (tempNextNode == null) {
                tail = tempPrevNode;
            } else {
                tempNextNode.setPrevNode(tempPrevNode);
            }
            // Normal case
            if (lastReturnedNode == currLocation) {
                currLocation = tempNextNode;
            } else {
                currIndex--;
            }
            lastReturnedNode = null;
            size--;
            modCount++;
            expectedModCount++;
            canRemoveOrAdd = false;
        }

        @Override
        public void set(T e) {
            hasChanged();
            if (!canRemoveOrAdd) {
                throw new IllegalStateException();
            }
            lastReturnedNode.setElement(e);
            modCount++;
            expectedModCount++;
        }

        @Override
        public void add(T e) {
            hasChanged();
            if (currIndex == 0) {
                IUDoubleLinkedList.this.addToFront(e);
            } else if (currIndex == size) {
                IUDoubleLinkedList.this.addToRear(e);
            } else {
                Node<T> newNode = new Node<T>(e);
                Node<T> tempNextNode = currLocation;
                Node<T> tempPrevNode = tempNextNode.getPrevNode();
                newNode.setPrevNode(tempPrevNode);
                newNode.setNextNode(tempNextNode);
                tempPrevNode.setNextNode(newNode);
                tempNextNode.setPrevNode(newNode);
                size++;
                modCount++;

            }
            expectedModCount++;
            currIndex++;
            canRemoveOrAdd = false;
        }
    }

    @Override
    public ListIterator<T> listIterator() {
        return new IUDoubleLinkedListListIterator();
    }

    @Override
    public ListIterator<T> listIterator(int startingIndex) {
        return new IUDoubleLinkedListListIterator(startingIndex);
    }

}
