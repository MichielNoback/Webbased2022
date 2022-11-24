package nl.bioinf.servlets;

import nl.bioinf.config.WebConfig;
import nl.bioinf.model.SequenceAnalaysisUtils;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@WebServlet(name = "SequenceAnalysisServlet", urlPatterns = "/seq_submit", loadOnStartup = 1)
public class SequenceAnalysisServlet extends HttpServlet {
    @Override
    public void init() throws ServletException {
        System.out.println("Initializing Thymeleaf template engine");
        final ServletContext servletContext = this.getServletContext();
        WebConfig.createTemplateEngine(servletContext);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String sequence = request.getParameter("sequence");
        System.out.println("sequence = " + sequence);
        WebConfig.configureResponse(response);
        WebContext ctx = new WebContext(
                request,
                response,
                request.getServletContext(),
                request.getLocale());

        try {
            double gCpercentage = SequenceAnalaysisUtils.getGCpercentage(sequence);
            ctx.setVariable("gc_percentage", gCpercentage);
            WebConfig.createTemplateEngine(getServletContext()).
                    process("seq_analysis_result", ctx, response.getWriter());

        } catch (IllegalArgumentException ex) {
            ctx.setVariable("error_message", ex.getMessage());
            WebConfig.createTemplateEngine(getServletContext()).
                    process("sequence_submit", ctx, response.getWriter());
        }


    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        WebConfig.configureResponse(response);
        WebContext ctx = new WebContext(
                request,
                response,
                request.getServletContext(),
                request.getLocale());

        WebConfig.createTemplateEngine(getServletContext()).
                process("sequence_submit", ctx, response.getWriter());

    }

}
