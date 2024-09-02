package hello.container;

import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HandlesTypes;

import java.util.Set;

@HandlesTypes(AppInit.class) // Set<Class<?>>에 AppInit 구현체를 주입받아 넘겨줌
public class MyContainerInitV2 implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        System.out.println("MyContainerInitV2.onStartup");
        System.out.println("c = " + c);
        System.out.println("ctx = " + ctx);

        // class hello.container.AppInitV1Servlet
        for (Class<?> appInitClass : c) {
            try {
                // = new AppInitV1Servlet()과 동일
                // appInitClass의 생성자를 통해 인스턴스 생성
                AppInit appInit = (AppInit) appInitClass.getDeclaredConstructor().newInstance();
                appInit.onStartUp(ctx); // 서블릿 컨텍스트 초기화 실행
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
