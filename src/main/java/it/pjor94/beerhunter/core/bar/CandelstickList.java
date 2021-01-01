package it.pjor94.beerhunter.core.bar;

import it.pjor94.beerhunter.model.Candlestick;

import java.util.*;

public class CandelstickList extends AbstractList<Candlestick> {

    private List<Candlestick> internalList = new ArrayList<Candlestick>();

    CandelstickList(){}

    public CandelstickList(List<Candlestick> list){
        internalList = new ArrayList<Candlestick>(list);
        sort();
    }

    @Override 
    public void add(int position, Candlestick e) {
        internalList.add(e);
        sort();
    }

    @Override
    public Candlestick get(int i) {
        return internalList.get(i);
    }

    @Override
    public int size() {
        return internalList.size();
    }

    private void sort(){
        internalList.sort(new Comparator<Candlestick>() {
            public int compare(Candlestick o1, Candlestick o2) {
                return new Date(o1.getOpenTime()).compareTo(new Date(o2.getOpenTime()));
            }
        });
    }

    @Override
    public Candlestick remove(int index) {
        return internalList.remove(index);
    }

}