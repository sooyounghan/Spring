package hello.embed;

import hello.servlet.HelloServlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class EmbedTomcatServletMain {
    public static void main(String[] args) throws LifecycleException {
        System.out.println("EmbedTomcatServletMain.main");

        // 톰캣 설정
        // 톰캣 생성
        Tomcat tomcat = new Tomcat();

        // 커넥터 생성
        Connector connector = new Connector();
        connector.setPort(9090); // 포트 설정
        tomcat.setConnector(connector); // 톰캣 - 커넥터 설정

        // 서블릿 등록
        Context context = tomcat.addContext("", "/"); // 톰캣 Context 생성

        // == 코드 추가 시작==
        File docBaseFile = new File(context.getDocBase());

        if (!docBaseFile.isAbsolute()) {
            docBaseFile = new File(((org.apache.catalina.Host) context.getParent()).getAppBaseFile(), docBaseFile.getPath());
        }

        docBaseFile.mkdirs();

        // == 코드 추가 종료==

        tomcat.addServlet("", "helloServlet", new HelloServlet()); // 서블릿 설정
        context.addServletMappingDecoded("/hello-servlet", "helloServlet"); // 서블릿에 대한 URL 매핑

        tomcat.start(); // 톰캣 시작
    }
}
