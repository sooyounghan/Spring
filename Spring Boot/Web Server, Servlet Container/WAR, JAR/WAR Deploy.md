-----
### WAR 배포 방식 단점
-----
1. 웹 애플리케이션을 개발 - 배포 과정
   - 톰캣 같은 웹 애플리케이션 서버 (WAS) 별도 설치
   - 애플리케이션 코드를 WAR로 빌드
   - 빌드한 WAR 파일을 WAS에 배포

2. 웹 애플리케이션을 구동하고 싶으면, 웹 애플리케이션 서버를 별도로 설치해야 하는 구조
3. 과거에는 웹 애플리케이션 서버와 웹 애플리케이션 빌드 파일(WAR)이 분리되어 있는 것이 당연했음
4. 이 방식의 단점
   - 톰캣 같은 WAS를 별도 설치
   - 개발 환경 설정이 복잡
     + 단순한 자바라면 별도의 설정 고민 없이, main() 메서드만 실행하면 됨
     + 웹 애플리케이션은 WAS 실행하고, 또 WAS와 연동하기 위한 복잡한 설정 들어감
   - 배포 과정이 복잡 : WAR를 만들고, 이를 또 WAS에 전달해서 배포
   - 톰캣의 버전을 변경하려면 톰캣 다시 설치

5. 고민
   - 이런 방식의 불편함을 고민했을 것
   - 단순히 자바의 main() 메서드만 실행하면 웹 서버까지 같이 실행하도록 된다면?
   - 톰캣 또한 자바로 만들어져 있으므로 톰캣을 마치 하나의 라이브러리처럼 포함해서 사용할 수 있다면?
   - 즉, 톰캣 같은 웹 서버를 라이브러리로 내장해버리는 것
   - 이런 문제를 해결하기 위해 톰캣을 라이브러리로 제공하는 내장 톰캣(Embed Tomcat) 기능 제공

6. 외장 서버와 내장 서버
<div align="center">
<img src="https://github.com/user-attachments/assets/da015412-4e86-4c4f-b715-cc53935940d6">
</div>

   - 왼쪽 : 웹 애플리케이션 서버에 WAR 파일을 배포하는 방식, WAS를 실행해 동작
   - 오른쪽 : 애플리케이션 JAR 안에 다양한 라이브러리들과 WAS 라이브러리가 포함되는 방식, main() 메서드를 실행해서 동작
