package hello.container;

import hello.servlet.HelloServlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;

/**
 * http://localhost:9090/hello-servlet
 */
public class AppInitV1Servlet implements AppInit {

    @Override
    public void onStartUp(ServletContext servletContext) {
        System.out.println("AppInitV1Servlet.onStartUp");

        // 순수 서블릿 코드 등록
        ServletRegistration.Dynamic helloServlet = servletContext.addServlet("helloServlet", new HelloServlet());

        // 서블릿 매핑
        helloServlet.addMapping("/hello-servlet");
    }
}
