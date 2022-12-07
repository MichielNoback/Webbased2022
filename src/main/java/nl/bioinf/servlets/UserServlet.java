package nl.bioinf.servlets;

import nl.bioinf.config.WebConfig;
import nl.bioinf.dao.DatabaseException;
import nl.bioinf.dao.MysqlDbConnector;
import nl.bioinf.dao.UserDao;
import nl.bioinf.model.User;
import nl.bioinf.noback.db_utils.DbCredentials;
import nl.bioinf.noback.db_utils.DbUser;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "UserManagement", urlPatterns = "/users")
public class UserServlet extends HttpServlet {
    private UserDao userDao;

    @Override
    public void init() throws ServletException {
        System.out.println("Initializing Thymeleaf template engine");
        final ServletContext servletContext = this.getServletContext();
        WebConfig.createTemplateEngine(servletContext);

        try {
            DbUser mySQLuser = DbCredentials.getMySQLuser();
            MysqlDbConnector.createInstance(
                    "jdbc:mysql://staffdb.bin.bioinf.nl:3306/Michiel",
                    mySQLuser.getUserName(),
                    mySQLuser.getDatabasePassword());
            userDao = MysqlDbConnector.getInstance();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

//    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
//    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String userName = request.getParameter("user_name");
        String userPass = request.getParameter("user_pass");

        try {
            User user = userDao.getUser(userName, userPass);
            response.getWriter().write(user.toString());
            response.getWriter().flush();

        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }


//        //this step is optional; standard settings also suffice
//        WebConfig.configureResponse(response);
//        WebContext ctx = new WebContext(
//                request,
//                response,
//                request.getServletContext(),
//                request.getLocale());
//        ctx.setVariable("currentDate", new Date());
//        ctx.setVariable("species", species);
//
//        WebConfig.createTemplateEngine(getServletContext()).
//                process("welcome", ctx, response.getWriter());
    }

}
