package com.example.jenproj2;

public class LinkedList<T extends Comparable<T>> {
    Node[] cursorArray;


    public LinkedList(int capacity) {
        cursorArray = new Node[capacity];
        initialization();
    }

    private int initialization() {
        for (int i = 0; i < cursorArray.length - 1; i++)
            cursorArray[i] = new Node<>(null, i + 1);
        cursorArray[cursorArray.length - 1] = new Node<>(null, 0);
        return 0;
    }

    private int malloc() {
        int p = cursorArray[0].next;
        cursorArray[0].next = cursorArray[p].next;
        return p;
    }

    private void free(int p) {
        cursorArray[p] = new Node(null, cursorArray[0].next);
        cursorArray[0].next = p;
    }

    public boolean isNull(int l) {
        return cursorArray[l] == null;
    }

    public boolean isEmpty(int l) {
        return cursorArray[l].next == 0;
    }

    public boolean isLast(int p) {
        return cursorArray[p].next == 0;
    }

    public int createList() {
        int l = malloc();
        if (l == 0)
            System.out.println("Error: Out of space!!!");
        else
            cursorArray[l] = new Node("Dummy", 0);
        return l;
    }

    public void insertAtHead(T data, int l) {
        if (isNull(l)) // list not created
            return;
        int p = malloc();
        if (p != 0) {
            cursorArray[p] = new Node(data, cursorArray[l].next);
            cursorArray[l].next = p;
        } else
            System.out.println("Error: Out of space!!!");
    }

    //delete the first element of the list
    public T deleteAtHead(int l){
        if(isNull(l) || isEmpty(l))
            return null;
        int p = cursorArray[l].next;
        cursorArray[l].next = cursorArray[p].next;
        T data = (T) cursorArray[p].data;
        free(p);
        return data;
    }

    public void printList(int l) {
        if (isNull(l)) {
            System.out.println("Error: List not created!!!");
            return;
        }
        if (isEmpty(l)) {
            System.out.println("List is empty!!!");
            return;
        }
        int p = cursorArray[l].next;
        while (p != 0) {
            System.out.println(cursorArray[p].data);
            p = cursorArray[p].next;
        }
    }

    //method to get the first element of the list
    public T getFirst(int l){
        if(isNull(l) || isEmpty(l))
            return null;
        int p = cursorArray[l].next;
        return (T) cursorArray[p].data;
    }





}


