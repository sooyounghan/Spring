-----
### H2 Database
-----
1. 개발이나 테스트 용도로 가볍고 편리한 DB, 웹 화면 제공
2. https://www.h2database.com
3. 데이터베이스 파일 생성 방법
   - jdbc:h2:~/jpashop (최소 한번) : 파일 모드로 실행되어 생성
   - ~/jpashop.mv.db 파일 생성 확인
   - 이후 부터는 jdbc:h2:tcp://localhost/~/jpashop 접속
