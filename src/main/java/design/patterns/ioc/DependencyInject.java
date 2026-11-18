package design.patterns.ioc;

import design.patterns.config.ConfigReader;
import design.patterns.dal.IGenericDao;
import design.patterns.entities.User;

import java.util.logging.Level;
import java.util.logging.Logger;
//import java.util.ServiceLoader;

public enum DependencyInject {
	instance;

	private IGenericDao<User, Long> idao;

	private DependencyInject() {
	}

	public IGenericDao getDao() {
		// idao = (IGenericDao)
		// IGenericDao.class.getClassLoader().loadClass(ConfigReader.getConfigClassName()).newInstance();

		try {
			idao = (IGenericDao) Class.forName(ConfigReader.getConfigProperty("dao.class"))
					.getDeclaredConstructor().newInstance();
		} catch (Exception ex) {
			Logger.getLogger(DependencyInject.class.getName()).log(Level.SEVERE, null, ex);
		}

		return idao;
	}
	
}
