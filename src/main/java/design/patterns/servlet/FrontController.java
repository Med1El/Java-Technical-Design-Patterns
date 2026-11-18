package design.patterns.servlet;

import design.patterns.config.ConfigReader;
import design.patterns.dal.CreateDBandTables;
import design.patterns.dal.IGenericDao;
import design.patterns.entities.User;
import design.patterns.ioc.DependencyInject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/FrontController")
public class FrontController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		route(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		route(request, response);
	}

	private void route(HttpServletRequest request, HttpServletResponse response) {

		System.out.println("route running");
		try {
			CreateDBandTables.createDBAndTables();
			IGenericDao dao = DependencyInject.instance.getDao();
			dao.add(new User("john_doe", "john.doe@example.com", "John", "Doe", "password123", "123-456-7890", "123 Main St"));
			response.getWriter().write("User added successfully");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
