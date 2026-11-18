package design.patterns.dal;

import java.util.List;


public interface IGenericDao<T extends Persistable, U> {

    public void add(T o); //create

    public T findById(Class<T> c, U id); //read

    public List<T> findByKeyword(Class<T> c, String param, String value);

    public void update(T o);
    
    public void update(T o, U id);
    
    public void merge(T o);
    
    public void merge(T o, U id);

    public void delete(T o);

    public void delete(T o, U id);

    public List<T> getAll(Class<T> c);

}
