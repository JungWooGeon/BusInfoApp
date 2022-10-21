# BusInfoApp
<img src="https://user-images.githubusercontent.com/61993128/187889792-5b4f4dd8-3c59-4de8-9e4b-973aeaec7eb8.png" width="300" height="300" />

<br>

### 💡 이 앱은 시각 장애인들이 사용하는 기기에서 버스 정보를 알려줍니다. 
시각 장애인들이 대상이기 때문에 UI 부분은 개발자가 알아보기 위한 용도로만 사용되었고, 간단하게 화면을 이동하고 주요 기능을 이용할 수 있도록 제작되었습니다. (터치 방식 x, 전용 기기 키보드로 이동 및 기능 사용)

<br><br>

### 🛠 사용
 * android, android studio
 * java, AsyncTask, Timer
 * MVVM, Data Binding, SharedPreferences, Files
 * Restful API (공공데이터 포털)

<br><br>

### 📷 화면
* 메인 화면 <img src="https://user-images.githubusercontent.com/61993128/187890784-f803cc91-859f-4139-9e2c-475c55fc2d37.PNG"/>
* 버스 노선 정보 화면 <img src="https://user-images.githubusercontent.com/61993128/187892178-e6f9b65f-c39d-4df7-9333-f216af2d69ce.PNG"/>
* 버스 도착 정보 화면 <img src="https://user-images.githubusercontent.com/61993128/187892236-8ec00b1f-07e6-496c-8efd-2a3a98c882ca.PNG"/>

<br><br>

### ⭐️ 기능
 * :bus: 버스 노선 정보
   * 버스 번호 : 버스 번호를 검색하여 결과를 확인한 후 결과 목록에서 원하는 버스를 선택할 수 있습니다.
   * 버스 경로 : 버스 번호를 검색한 후 원하는 버스 번호를 선택하여 상세 검색을 하면 해당 버스의 경로가 결과로 나타납니다.
 * :busstop: 버스 도착 정보
   * 버스 정류장 : 버스 정류장 이름을 검색하여 결과를 확인한 후 결과 목록에서 원하는 정류장을 선택할 수 있습니다.
   * 버스 도착 : 버스 정류장을 검색한 후 원하는 정류장을 선택하여 상세 검색을 하면 해당 정류장에 오는 버스들의 도착 예정 시간이 결과로 나타납니다. (30초 마다 갱신)
 * :bookmark: 즐겨찾기 기능
   * 즐겨 찾기 추가 : 버스 노선이나 도착 정보에 대해 검색을 하고 '즐겨 찾기 추가' 버튼을 누르면 즐겨 찾기에 추가 되고, 메인 화면에 즐겨 찾기 목록에 추가됩니다.
   * 메인화면에 즐겨 찾기 목록에서 엔터를 통해 해당 정보에 대한 빠른 검색이 가능합니다. (버스 경로, 버스 도착 정보)
   * 즐겨 찾기 삭제 : 메인 화면 '즐겨 찾기 삭제' 버튼
 * 옵션 기능
   * 옵션에서 시간을 설정할 수 있습니다. (사용안함, 3분, 7분, 10분)
   * 옵션을 설정하고, 메인 화면에 즐겨 찾기 목록에서 버스 도착 정보를 검색하면 목록이 30초마다 업데이트되는데, 이 때 목록에서 포커싱되어 있는 버스가 옵션 설정 시간 이내에 도착 예정일 경우 TTS를 출력한다.

<br><br>

### 🤚🏻 주의할 점
* 안드로이드 휴대폰이나 에뮬레이터가 아닌 시각 장애인용 기기를 타겟으로 제작되었습니다. (일반적인 에뮬레이터에서 실행 불가)
* 안드로이드 스튜디오 버전이 3.2.1에 맞추어 있습니다.
* gradle 버전이 최신과 맞지 않습니다.

<br><br>

### 📃 Learned
 * Rest API 를 android에서 사용해볼 수 있었음 (공공데이터 포털에서 사용, xml)
 * 디자인 패턴을 만들어보려고 도전해보면서 공부해볼 수 있었고, 최종적으로 MVVM 패턴으로 만들어 볼 수 있었음
   <br>   (MVC -> MVP -> MVVM 으로 변경)
 * java에 맞게 객체지향적으로 앱을 만들어볼 수 있었음 (Base와 상속, singleton 패턴, private과 getter setter 사용 등)
 * SharedPreferences, Files를 사용해보며, 기기 내부에 정보를 저장해볼 수 있었음
 * 이 애플리케이션을 탑재할 기기에 사용하는 전용 library가 있었는데, 버전이 낮은 android studio와 gradle에서만 호환이 되어 앱을 만드는데 어려움을 느꼈음
   - 구현하고자 하는 기능이나 주의할 점 등을 검색을 하면 최신 정보 위주로 나와 있어 원하는 정보를 찾는 데에 어려움을 겪었음
   - 최신 기능들을 추가하고자 gradle에 추가하면, 전용 library에서 error가 발생하여 기능들을 추가하지 못하였음
     <br> (예) Rxjava로 구현해보고자 하였지만, 기기 전용 library와 호환이 되지 않아 Deprecated 된 AsyncTask를 사용하였음)
 * 전용 library에서 AppCompatAcitivy를 사용하지 않고, Activity를 상속받아 사용하여 MVVM을 구현하기 위한 Lifecycle을 직접 저장하고 관리해주었음
 * 이 앱에서는 UI 구성요소나 기능이 많지 않아 Data Binding이 딱히 필요하지 않았지만, MVVM과 잘 맞기 때문에 사용해보았음
 * sonarqube를 이용하여 code smell과 bug를 확인해보고 수정해볼 수 있었음
