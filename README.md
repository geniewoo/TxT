# TEXT GAME FOR ANDROID

### 간단한 설명

widget을 이용한 텍스트 게임으로 선택지를 따라가 실행 할 수 있는 간단한 게임

### 게임제작

게임제작 툴을 제공한다. 로컬저장소를 이용하여 게임을 이어서 제작 할 수 있으며 게임을 서버에 올릴 수도 있다.

### 게임로컬저장

게임을 한페이지 만들 때 마다 로컬에 저장한다. 후에 원하는 인덱스로 들어가서 내용을 교체 할 수도 있다.

### 게임서버저장

완성된 게임을 서버에 저장하면 여러사람이 즐길 수 있다.

### 게임다운로드

서버에서 게임을 겁색하여 게임을 즐길 수 있다.

### 게임플레이

게임다운로드를 선택하면 위젯에서 게임을 즐길 수 있다.


### 게임로직

##### 시스템

- 각 페이지마다 index가 있으며 선택지마다 index를 가지며 이 index를 따라간다.
-

##### 플레이

- 글자를 타이머를 이용해 천천히 보여줘서 플레이 시간을 늘린다.
- 간단한 이미지, 진동, 소리등을 넣어 생동감 있게 한다. (애니메이션은 위젯한계로 불가능하다)

### 데이터베이스 모델

#### UserInfo // 서버쪽

~~~
UserInfo userInfo;
PlayedInfo[] playedInfos;

class UserInfo{
    String email;
    String nickName;
    String password;
}

class PlayedInfo{
    int gameID;
    String gameTitle;
    String makerNickName; //
    int stars; // 내가 준 평점
    boolean rated;
    int try; // 최소 try수
    int clear; // 클리어 횟수
}
~~~

#### GameInfo // 서버, 로컬 둘 다

~~~
GameInfo gameInfo; // 게임정보
PlayInfo playInfo; // 플레이 정보
Maker maker; // 만든사람 정보
class gameInfo{
    String gameTitle; // 게임제목
    int gameID; // game id;
    String gameTheme; // 게임 장르
    String gameColor; // 게임배경색
    int pagesNum; // 페이지 총 수
    Page[] pages; // 각 페이지 정보
    class Page{
        int index; // 페이지 번호
        String title; // 페이지 제목
        String text; // 페이지 내용
        boolean gameOver; // gameOver 페이지 인지
        boolean gameSuccess; // gameSuccess 페이지 인지
        int selectNums; //선택지 개수
        String Select[] selects; // 페이지 선택지
        class Select{
            boolean isClicked; // 클릭된 선택지인지 아닌지
            int NextIndex; // 선택했을 때 갈 페이지번호
            String selectionText; // 선택지 내용
        }
    }
}
class Maker{
    String email; //만든이 이메일
    String nickName ; //만든이 닉네임
}
class PlayInfo{
    int stars // 평점
    int downLoads // 다운로드 수
    int try // 게임클리어를 위한 시도 횟수
    int clear // 총 클리어 횟수
}
~~~

### Will Do

1. 처음 로그인, 가입 화면 만들기\

    - 이메일, 닉네임만 필요 서버에 저장
    - 로그인 시 계속 로그인 유지

2. 첫 화면 제작하기

    - 자신의 사진
    - 게임 찾기
    - 게임 제작
    - 내가 했던 게임 보기
    - 오류보고/피드백
    - 로그아웃

3. 게임메이킹 툴 제작하기

    - Realm 이용한 클래스 만들기 (위의 스키마에서 필요한 필드만 사용);
    - 메이킹을 위한 레이아웃 만들기
    - 페이지를 만들 때 마다 저장 할 수 있게 만들기 (모두 localDB로)
    - default 사진 및 사용자가 원하는 사진 입력 가능하게 만들기 (사진 편집하여 저용량으로 저장)
    - 진동 여부
    - 소리 여부(default 값만 사용 가능)
    - 테스트 할 수 있게 만들기
    - 완성시 서버 업로드 만들기

4. 게임 검색 및 다운로드 만들기

    - 게임을 장르별, 인기순, 최신순으로 볼 수 있게 만들기
    - 키워드로 검색 할 수 있게 만들기
    - 다운로드 클릭시 나의 현재게임 db에 저장(기존 게임 사라짐 주의)

5. 게임 플레이

    - 위젯은 broadcast를 받을 수 있으므로 어플에서 정보를 받아 올 시 intent이용
    - 위젯에서 현재 게임 데이터베이스(Realm)을 필요 할 때 마다 열어서 위젯을 변경
    - 글자를 한글자씩 띄우는 방법 구상
    - 미리 레이아웃을 모두 정해둔 뒤 Visibility를 조정하여 필요한 View만 사용가능
    - 소리, 진동 제공
    - 버튼 누를 시 broadcast를 보내는 pendingintent를 이용해 widget조정
