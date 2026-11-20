package design.patterns.dal;

import java.util.List;


public interface IGenericDao<T extends Persistable, U> {

    public boolean add(T o); //create

    public T findById(Class<T> c, U id); //read

    public List<T> findByKeyword(Class<T> c, String param, String value);

    public boolean update(T o);
    
    public boolean updateById(T o, U id);
    
    public boolean patch(T o);
    
    public boolean patchById(T o, U id);

    public boolean delete(T o);

    public boolean deleteById(T o, U id);

    public List<T> getAll(Class<T> c);

}
