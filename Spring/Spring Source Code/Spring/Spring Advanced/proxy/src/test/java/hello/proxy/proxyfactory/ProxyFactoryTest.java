package hello.proxy.proxyfactory;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ConcreteService;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;

import static org.assertj.core.api.Assertions.*;

@Slf4j
public class ProxyFactoryTest {

    @Test
    @DisplayName("인터페이스가 있으면 JDK 동적 프록시 사용")
    void interfaceProxy() {
        ServiceInterface target = new ServiceImpl();

        // 프록시 팩토리 생성
        ProxyFactory proxyFactory = new ProxyFactory(target);

        // Advice 추가
        proxyFactory.addAdvice(new TimeAdvice());

        // 생성 Proxy 반환
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        log.info("target Class = {}", target.getClass());
        log.info("proxy Class = {}", proxy.getClass());

        proxy.save();

        // 💡 프록시 팩토리를 통해 생성된 프록시만 true
        assertThat(AopUtils.isAopProxy(proxy)).isTrue();
        assertThat(AopUtils.isJdkDynamicProxy(proxy)).isTrue();
        assertThat(AopUtils.isCglibProxy(proxy)).isFalse();
    }

    @Test
    @DisplayName("구체 클래스만 있으면 CGLIB 사용")
    void concreteProxy() {
        ConcreteService target = new ConcreteService();

        // 프록시 팩토리 생성
        ProxyFactory proxyFactory = new ProxyFactory(target);

        // Advice 추가
        proxyFactory.addAdvice(new TimeAdvice());

        // 생성 Proxy 반환
        ConcreteService proxy = (ConcreteService) proxyFactory.getProxy();

        log.info("target Class = {}", target.getClass());
        log.info("proxy Class = {}", proxy.getClass());

        proxy.call();

        // 💡 프록시 팩토리를 통해 생성된 프록시만 true
        assertThat(AopUtils.isAopProxy(proxy)).isTrue();
        assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse();
        assertThat(AopUtils.isCglibProxy(proxy)).isTrue();
    }

    @Test
    @DisplayName("ProxyTargetClass 옵션을 사용하면, 인터페이스가 있어도 CGLIB를 사용하고, 클래스 기반 프록시 사용")
    void proxyTargetClass() {
        ServiceInterface target = new ServiceImpl();

        // 프록시 팩토리 생성
        ProxyFactory proxyFactory = new ProxyFactory(target);

        // 타겟 클래스를 기반으로 프록시 생성 (CGLIB)
        proxyFactory.setProxyTargetClass(true); // true : 타켓 클래스 기반 - CGLIB 기반

        // Advice 추가
        proxyFactory.addAdvice(new TimeAdvice());

        // 생성 Proxy 반환
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        log.info("target Class = {}", target.getClass());
        log.info("proxy Class = {}", proxy.getClass());

        proxy.save();

        // 💡 프록시 팩토리를 통해 생성된 프록시만 true
        assertThat(AopUtils.isAopProxy(proxy)).isTrue();
        assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse();
        assertThat(AopUtils.isCglibProxy(proxy)).isTrue();
    }
}
