-----
### 프로젝트 환경
-----
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/afa04349-f6a0-4166-a2a4-e3034333f0ee">
</div>

1. 동작 확인 : 기본 메인 클래스 실행 (ItemServiceApplication.main())
2. http://localhost:9090/ 호출 : Whitelabel Error Page가 나오면 정상 동작

-----
### Welcome 페이지 추가
-----
- resources/static/index.html
```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<ul>
    <li>상품 관리
        <ul>
            <li><a href="/basic/items">상품 관리 - 기본</a></li>
        </ul>
    </li>
</ul>
</body>
</html>
```
