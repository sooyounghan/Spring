< 필요할 때 마다 찾아서 추가 예정 >
- 참고 주소 : https://gmlwjd9405.github.io/2019/05/21/intellij-shortkey.html
  
-----
### 기본 단축키
-----
1. 디렉터리, 패키지, 클래스 등 생성 목록 보기
  - MacOS: Cmd + n
  - Win/Linux: Alt + Insert

2. 코드 Edit
   - Main method 생성 및 실행
      + 메인 메서드 선언
        * live template 이용: psvm (live template은 아래 참고)
      
      + 메인 메서드 실행
        * 좌측 실행 버튼
        * 단축키 (현재 Focus 가 해당 메서드에 있어야 함)
          - MacOS: Ctrl + Shift + r
          - Win/Linux: Ctrl + Shift + F10 - 이전 실행문 재실행 (우측 상단에 실행문 목록 확인 가능)
          - MacOS: Ctrl + r
          - Win/Linux: Shift + F10

-----
### 포커스
-----
1. 포커스 에디터
  - 단어별 이동
    + MacOS: Opt + ←→
    + Win/Linux: Ctrl + ←→
        
  -  단어별 선택 (Move Caret to Next Word with Selection)
    + MacOS: Opt + Shift + ←→
    + Win/Linux: Ctrl + Shift + ←→

  -  라인 첫/끝 이동
    + MacOS: fn + ←→
    + Win/Linux: Home, End

  -  라인 전체 선택
    + MacOS: fn + Shift + ←→
    + Win/Linux: Shift + Home, End
        
  -  Page Up/Down
    + MacOS: fn + ↑↓
    + Win/Linux: Page Up, Page Down

-----
### 자동 완성
-----
1. 기본 자동완성 (Completion -> Basic)
  - MacOS: Ctrl + Space
  - Win/Linux: Ctrl + Space

2. 스마트 자동완성 (Completion -> SmartType)
  - MacOS: Ctrl + Shift + Space
  - Win/Linux: Ctrl + Shift + Space

3. Static Method 자동완성
  - MacOS: Ctrl + Space + Space
  - Win/Linux: Ctrl + Space + Space

4. Getter / Setter / 생성자 자동완성 (Generate)
  - MacOS: Cmd + n
  - Win/Linux: Alt + Insert

5. Override 메서드 자동완성 (Implement Methods)
  - MacOS: Ctrl + i
  - Win/Linux: Ctrl + i

-----
### Refactoring Extract
-----
1. 변수 추출하기 (Extract -> Variable)
  - MacOS: Cmd + Opt + v
  - Win/Linux: Ctrl + Alt + v

2. 파라미터 추출하기 (Extract -> Parameter)
  - MacOS: Cmd + Opt + p
  - Win/Linux: Ctrl + Alt + p

3. 메서드 추출하기 (Extract -> Method)
  - MacOS: Cmd + Opt + m
  - Win/Linux: Ctrl + Alt + m

4. Inner Class 추출하기
  - MacOS: F6
  - Win/Linux: F6

-----
### Refactoring 기타
-----
1. 이름 일괄 변경하기 (Rename)
  - MacOS: Shift + F6
  - Win/Linux: Shift + F6

2. 타입 일괄 변경하기 (Type Migration)
  - MacOS: Cmd + Shift + F6
  - Win/Linux: Ctrl + Shift + F6

3. Import 정리하기 (Optimize Imports)
  - MacOS: Ctrl + Opt + o
  - Win/Linux: Ctrl + Alt + o
  - 자동 설정: [Find Action] -> Optimize imports on 입력 -> “Auto import: …”

4. 코드 자동 정렬하기 (Reformat Code)
  - MacOS: Cmd + Opt + l
  - Win/Linux: Ctrl + Alt + l

-----
### Debugging
-----   
1. Break Point 걸기 (Toggle Line Breakpoint)
  - 해당 라인 number 옆 클릭
  - MacOS: Cmd + F8
  - Win/Linux: Ctrl + F8
  - Break Point의 라인은 아직 실행하기 전 상태이다.

2. Conditional Break Point
  - 반복문에서 특정값을 가지고 있는 객체가 나왔을 때만 멈추고자 할 때 유용
  - Break Point (빨간원) 우클릭 -> 조건 입력
    + Ex. “HEEE”.equals(user.name)

3. Debug 모드로 실행하기 - 즉시 실행 (Debug)
  - 현재 Focus 가 해당 메서드에 있어야 함
  - 좌측 디버그 실행 버튼
    + MacOS: Ctrl + Shift + d
    + Win/Linux: 없음 (커스텀해서 사용하거나 마우스 이용)

4. Debug 모드로 실행하기 - 이전 실행
  - 이전 실행문 재실행 (우측 상단에 실행문 목록 확인 가능)
  - MacOS: Ctrl + d
  - Win/Linux: Shift + F9


-----
### Git & Github
-----   
1. Git View On
  - View 탭 -> Tool Windows -> Version Control
  - MacOS: Cmd + 9
  - Win/Linux: Alt + 9

2. Git Option Popup (VCS Operations Popup)
   - MacOS: Ctrl + v
   - Win/Linux: Alt + `(Back Quote)

3. Git History
  - MacOS: Ctrl + v => 4
  - Win/Linux: Alt + ` => 4

4. Branch
  - MacOS: Ctrl + v => 7
  - Win/Linux: Alt + ` => 7

5. Commit
  - MacOS: Cmd + k
  - Win/Linux: Ctrl + k

6. Push
  - MacOS: Cmd + Shift + k
  - Win/Linux: Ctrl + Shift + k

7. Pull
  - MacOS: [Find Action] => git pull 검색
  - Win/Linux: [Find Action] => git pull 검색
