import java.util.Iterator;
import java.util.NoSuchElementException;

public class Queue<E> implements Iterable<E> {

	private class Node {
		
		E data;
		Node prev, next;
		
		public Node(E data) {
			this.data = data;
		}
		
	}
	
	private Node first, last;
	
	public int size() {
		
		int size = 0;
		Node currentNode = this.first;
		while (currentNode != null) {
			size++;
			currentNode = currentNode.next;
		}
		return size;
		
	}
	
	public void push(E data) {
		
		if (first == null) {
			this.first = new Node(data);
			this.last = this.first;
		}
		else {
			this.last.next = new Node(data);
			this.last.next.prev = this.last;
			this.last = this.last.next;
		}
		
	}
	
	public E peek() {
		if (this.first == null) 
			throw new IllegalStateException();
		else
			return this.first.data;
	}
	
	public E pop() {
		if (this.first == null)
			throw new IllegalStateException();
		else {
			Node popNode = this.first;
			if (this.first.next == null) {
				this.first = null;
				this.last = null;
			}
			else {
				this.first.next.prev = null;
				this.first = this.first.next;
			}
			return popNode.data;
		}
	}
	
	public E rearPeek() {
		if (this.last == null)
			throw new IllegalStateException();
		else
			return this.last.data;
	}
	
	public boolean isEmpty() { return first == null; }

	public Iterator<E> iterator() {
		
		return new Iterator<E>() {
			
			private Node currentNode = first;
			
			public boolean hasNext() {
				return currentNode.next != null;
			}
			
			public E next() {
				if (!hasNext())
					throw new NoSuchElementException();
				else {
					Node origNode = currentNode;
					currentNode = currentNode.next;
					return origNode.data;
				}
			}
			
			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
		
	}
	
}
