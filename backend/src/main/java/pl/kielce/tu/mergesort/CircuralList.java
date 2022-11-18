package pl.kielce.tu.mergesort;

public interface CircuralList {
    public void insertFirst(Object value);
    public void insertAfterActual(Object value);
    public boolean delete(Object value);
    public boolean deleteActualElement();
    public boolean goToNextElement();
    public Object getActualElementData();
    public boolean isEmpty();
    public boolean contains(Object value);
}
