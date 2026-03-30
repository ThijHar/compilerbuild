package nl.han.ica.datastructures;

import java.util.ArrayList;

public class HANStack<T> implements IHANStack<T> {

    private ArrayList<T> elements;

    public HANStack() {
        elements = new ArrayList<>();
    }

    @Override
    public void push(T value) {
        elements.add(value);
    }

    @Override
    public T pop() {
        if (elements.isEmpty()) {
            throw new RuntimeException("Stack is empty");
        }
        return elements.remove(elements.size() - 1);
    }

    @Override
    public T peek() {
        if (elements.isEmpty()) {
            throw new RuntimeException("Stack is empty");
        }
        return elements.get(elements.size() - 1);
    }
}