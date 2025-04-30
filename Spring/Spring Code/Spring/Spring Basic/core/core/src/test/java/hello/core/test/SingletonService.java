package hello.core.test;

public class SingletonService {

    // 1. static 영역에 객체를 1개만 생성
    private static final SingletonService instance = new SingletonService();

    // 2. public으로 접근제어자를 설정해 객체 인스턴스가 필요하면 이 static 메서드를 통해서마 조회를 허용하도록 함
    public static SingletonService getInstance() {
        return instance;
    }

    // 3. 생성자를 private로 선언하여 외부에서 new 키워드를 사용한 객체 생성 방지
    private SingletonService() {

    }

    public void logic() {
        System.out.println("싱글톤 객체 로직 호출");
    }
}
