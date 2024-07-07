package com.example.jenproj2;

public class Stack<T extends Comparable<T>> extends LinkedList<T>{

    int l;

    public Stack() {
        super(101);
        l = createList();
    }

    public Stack(int capacity){
        super(capacity);
        l = createList();
    }

    //pushes an element to the top of the stack
    public void push(T element){
        insertAtHead(element, l);
    }

    //pops an element from the top of the stack
    public T pop(){
        return deleteAtHead(l);
    }

    //prints the stack
    public void printStack(){
        printList(l);
    }

    //checks if the stack is empty
    public boolean isEmpty(){
        return isEmpty(l);
    }


    public T peek() {
        return getFirst(l);
    }
}
