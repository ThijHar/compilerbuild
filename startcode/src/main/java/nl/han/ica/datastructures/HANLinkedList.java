package nl.han.ica.datastructures;

import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;
import java.util.LinkedList;

public class HANLinkedList implements IHANLinkedList<HashMap<String, ExpressionType>> {
    private LinkedList<HashMap<String, ExpressionType>> list;

    public HANLinkedList() {
        list = new LinkedList<>();
    }

    @Override
    public void addFirst(HashMap<String, ExpressionType> value) {
        list.addFirst(value);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public void insert(int index, HashMap<String, ExpressionType> value) {
        list.add(index, value);
    }

    @Override
    public void delete(int pos) {
        list.remove(pos);
    }

    @Override
    public HashMap<String, ExpressionType> get(int pos) {
        return list.get(pos);
    }

    @Override
    public void removeFirst() {
        list.removeFirst();
    }

    @Override
    public HashMap<String, ExpressionType> getFirst() {
        return list.getFirst();
    }

    @Override
    public int getSize() {
        return list.size();
    }
}
