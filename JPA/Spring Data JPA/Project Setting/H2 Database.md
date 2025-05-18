-----
### H2 데이터베이스
-----
1. 개발이나 테스트 용도로 가볍고 편리한 DB, 웹 화면 제공
2. https://www.h2database.com
3. 스프링 부트 3.0 이상 : 2.1.214 버전 이상 사용
4. H2 데이터베이스 버전은 스프링 부트 버전에 맞춤
5. 권한 주기 : chmod 755 h2.sh
6. 데이터베이스 파일 생성 방법
   - 최소 한 번 : jdbc:h2:~/datajpa (파일 생성)
   - ~/datajpa.mv.db 파일 생성 확인
   - 이후로는 jdbc:h2:tcp://localhost/~/datajpa로 접속 (원격 접근)
