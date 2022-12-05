package nl.bioinf.servlets;

import nl.bioinf.config.WebConfig;
import nl.bioinf.model.UserFirst;
import org.thymeleaf.context.WebContext;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "TrackingServlet", urlPatterns = "/track_me")
public class TrackingServlet extends HttpServlet {
    @Override
    public void init() throws ServletException {
        System.out.println("Initializing Thymeleaf template engine");
        final ServletContext servletContext = this.getServletContext();
        WebConfig.createTemplateEngine(servletContext);
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        WebConfig.configureResponse(response);
        WebContext ctx = new WebContext(
                request,
                response,
                request.getServletContext(),
                request.getLocale());

        WebConfig.createTemplateEngine(getServletContext()).
                process("login", ctx, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        WebConfig.configureResponse(response);
        WebContext ctx = new WebContext(
                request,
                response,
                request.getServletContext(),
                request.getLocale());

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null) {
            UserFirst loggedInUserFirst = authenticateUser(username, password);
            if (loggedInUserFirst == null) {
                WebConfig.createTemplateEngine(getServletContext()).
                        process("login", ctx, response.getWriter());
            } else {
                session.setAttribute("user", loggedInUserFirst);
                WebConfig.createTemplateEngine(getServletContext()).
                        process("visits", ctx, response.getWriter());
            }
        } else {
            //increase count?
            UserFirst userFirst = (UserFirst)session.getAttribute("user");
            userFirst.addVisit();
            WebConfig.createTemplateEngine(getServletContext()).
                    process("visits", ctx, response.getWriter());
        }
    }

    private UserFirst authenticateUser(String username, String password) {
        if (username.equals("henk@example.nl") && password.equals("henk")){
            return new UserFirst("Guest", username);
        }
        else return null;
    }
}
