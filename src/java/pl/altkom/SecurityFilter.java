package pl.altkom;

import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pl.altkom.beans.AuthenticationBean;

@WebFilter(
        urlPatterns = {"/secure/*"},
        dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE},
        initParams = {
            @WebInitParam(name = "loginPage", value = "login.faces")
        }
)
public class SecurityFilter implements Filter {

    @Inject
    private AuthenticationBean aBeanLocal;
    private String loginPage;

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        //
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        // nie ma beana w sesji lub nie ma ustawionego typu ?
        if (!aBeanLocal.isLogged() || (null == aBeanLocal.getType())) {
            sendToLogin(req, resp);
            return;
        }
        String uri = req.getRequestURI();
        String ctxPath = req.getContextPath();
        // czy URI zawiera właściwą ścieżkę ?
        String rightPath = ctxPath + "/secure/" + aBeanLocal.getType();
        //
        // System.out.println("oczekiwany URI: " + uri);
        // System.out.println("uprawniony URI: " + rightPath);
        //
        if (!uri.startsWith(rightPath)) {
            sendToLogin(req, resp);
            return;
        }
        chain.doFilter(request, response);
    }

    private void sendToLogin(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.sendRedirect(req.getContextPath() + "/" + loginPage);
    }

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
        loginPage = fConfig.getInitParameter("loginPage");
    }
}
