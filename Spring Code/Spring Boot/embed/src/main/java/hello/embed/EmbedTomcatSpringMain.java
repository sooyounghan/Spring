package hello.embed;

import hello.spring.HelloConfig;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;

public class EmbedTomcatSpringMain {
    public static void main(String[] args) throws LifecycleException {
        System.out.println("EmbedTomcatSpringMain.main");

        // 톰캣 설정
        // 톰캣 생성
        Tomcat tomcat = new Tomcat();
        // 커넥터 생성
        Connector connector = new Connector();
        connector.setPort(9090); // 커넥터 - 포트 설정
        tomcat.setConnector(connector); // 톰캣 - 커넥터 설정

        // 스프링 컨테이너 생성
        AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
        appContext.register(HelloConfig.class);

        // 스프링 MVC 디스패처 서블릿 생성 - 스프링 컨테이너와 연결
        DispatcherServlet dispatcher = new DispatcherServlet(appContext);

        // 디스패처 서블릿을 서블릿 컨테이너에 등록
        Context context = tomcat.addContext("", "/");

        // == 코드 추가 시작==
        File docBaseFile = new File(context.getDocBase());

        if (!docBaseFile.isAbsolute()) {
            docBaseFile = new File(((org.apache.catalina.Host) context.getParent()).getAppBaseFile(), docBaseFile.getPath());
        }

        docBaseFile.mkdirs();

        // == 코드 추가 종료==

        tomcat.addServlet("", "dispatcher", dispatcher);
        context.addServletMappingDecoded("/", "dispatcher");

        tomcat.start();
    }
}
