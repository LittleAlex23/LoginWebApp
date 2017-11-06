/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Filtering;

import Entity.UserAccount;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Alexandre
 */
@WebFilter(filterName = "LoginData", urlPatterns = {"/*"})
public class LoginData implements Filter {
    
    private static final boolean DEBUG = true;
    private FilterConfig config;
    private FilterConfig filterConfig = null;
    private final String[] excludedFiles = {
        "/css/.+.css$",
        "/CreateAccount.jsp",
        "/AccountServlet$",
        "/loginPage",
        "/webapi/account"
    };
    public LoginData() {
    }    
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;
        HttpSession session = httpReq.getSession();
       // ServletContext context = config.getServletContext();
        UserAccount c = (UserAccount)session.getAttribute("user");
        boolean loggedIn = c != null;
        boolean isLoggingIn = httpReq.getRequestURI().matches(httpReq.getContextPath() + "/LoginSuccess.jsp$");
        
        if (loggedIn || isExcluded(httpReq.getRequestURI(), httpReq.getContextPath()) || (isLoggingIn && loggedIn)){
          //  context.log(c.getName() + " logged on " + (new java.util.Date()));
            httpRes.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
            httpRes.setHeader("Pragma", "no-cache"); // HTTP 1.0.
            httpRes.setDateHeader("Expires", 0);
            chain.doFilter(request, response);
        } else {
            httpRes.sendRedirect("/WebApp/loginPage");
        }
    }
    private boolean isExcluded(String URI, String path){
        for(String str : excludedFiles)
            if(URI.matches(path + str))
                return true;
        return false;
    }

    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    @Override
    public void destroy() {        
    }

    @Override
    public void init(FilterConfig filterConfig) {        
        System.out.println("Instance created of " + getClass().getName());
        this.config = filterConfig;
    }

    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("LoginData()");
        }
        StringBuilder sb = new StringBuilder("LoginData(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
    }
  
    
    public static String getStackTrace(Throwable t) {
        String stackTrace = null;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (IOException ex) {
        }
        return stackTrace;
    }
    
    public void log(String msg) {
        filterConfig.getServletContext().log(msg);        
    }
}
