package hello.proxy.pureproxy.decorator.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageDecorator implements Component {

    private Component component; // 실제 객체

    public MessageDecorator(Component component) {
        this.component = component;
    }

    @Override
    public String operation() {
        log.info("MessageDecorator 실행");

        // Data -> *****Data****
        String result = component.operation(); // 실제 객체에 대한 반환 값 저장 (Data)
        String decoResult = "*****" + result + "*****" ; // 실제 객체에 대한 반환 값에 추가 메세지 작성
        log.info("MessageDecorator 꾸미기 적용 전 = {}, 적용 후 = {}", result, decoResult);
        return decoResult;
    }
}
