-----
### HTML Form 전송 방식
-----
1. application/x-www-form-urlencoded
2. multipart/form-data

-----
### application/x-www-form-urlencoded 방식
-----
<div align="center">
<img src="https://github.com/user-attachments/assets/f07bad9d-a98e-44e0-8a19-2e118be45bd8">
</div>

1. HTML Form 데이터를 서버로 전송하는 가장 기본적인 방법
2. Form 태그에 별도의 enctype 옵션이 없으면, 웹 브라우저는 요청 HTTP 메세지 헤더에 다음 내용 추가
  -  Content-Type: application/x-www-form-urlencoded
3. 그리고 폼에 입력한 전송할 항목을 HTTP Body에 문자로 username=kim&age=20와 같이 &로 구분해서 전송
4. 💡 파일을 업로드 하려면 파일은 문자가 아니라 바이너리 데이터를 전송해야함 (문자를 전송하는 방식으로 파일 전송은 어려움)
5. 또한, 폼을 전송할 때 파일만 전송하는 것이 없음
  - 예시
```
A. 이름
B. 나이
C. 첨부파일
```
  - 여기서는 이름과 나이도 전송해야하고, 첨부파일도 함께 전송해야 함
  - 문제는 이름과 나이는 문자로 전송해야하고, 파일은 바이너리로 전송해야 함
  - 즉, 문자와 바이너리를 동시에 전송해야 함

-----
### multipart/form-data 방식
-----
1. 이 문제를 해결하기 위해 multipart/form-data라는 전송 방식 제공
<div align="center">
<img src="https://github.com/user-attachments/assets/48a85f35-2775-4063-b5f8-5194f346331b">
</div>

2. 이 방식을 사용하려면 Form 태그에 별도의 enctype="multipart/form-data"를 지정
3. multipart/form-data 방식은 다른 종류의 여러 파일과 폼의 내용을 함께 전송 가능 (이름이 multipart인 이유)
4. 폼의 입력 결과로 생성된 HTTP 메세지를 보면 각 전송 항목이 구분
  - Content-Disposition이라는 항목별 헤더가 추가되어 있고, 여기에 부가 정보 존재
  - 예제에서는 username, age, file1이 각각 분리되어 있고, 폼의 일반 데이터는 각 항목별로 문자가 전송
  - 파일의 경우, 파일 이름의 Content-Type이 추가되고, 바이너리 데이터가 전송
5. 즉, multipart/form-data는 이렇게 각 항목을 구분해서, 한 번에 전송하는 것

6. part
   - multipart/form-data는 application/x-www-form-urlencoded와 비교해 매우 복잡하고 각 부분(part)으로 나눠짐
