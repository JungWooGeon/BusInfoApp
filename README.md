# BusInfoApp
<img src="https://user-images.githubusercontent.com/61993128/187889792-5b4f4dd8-3c59-4de8-9e4b-973aeaec7eb8.png" width="300" height="300" />

<br>

#### 이 앱은 시각 장애인들이 사용하는 기기에서 버스 정보를 알려줍니다. 
시각 장애인들이 대상이기 때문에 UI 부분은 개발자가 알아보기 위한 용도로만 사용되었고, 간단하게 화면을 이동하고 주요 기능을 이용할 수 있도록 제작되었습니다. (터치 방식 x, 전용 기기 키보드로 이동 및 기능 사용)

<br><br>

### 화면
* 메인 화면 <img src="https://user-images.githubusercontent.com/61993128/187890784-f803cc91-859f-4139-9e2c-475c55fc2d37.PNG"/>
* 버스 노선 정보 화면 <img src="https://user-images.githubusercontent.com/61993128/187892178-e6f9b65f-c39d-4df7-9333-f216af2d69ce.PNG"/>
* 버스 도착 정보 화면 <img src="https://user-images.githubusercontent.com/61993128/187892236-8ec00b1f-07e6-496c-8efd-2a3a98c882ca.PNG"/>

<br><br>

### 기능
 * 버스 노선 정보
   * 버스 번호 : 버스 번호를 검색하여 결과를 확인한 후 결과 목록에서 원하는 버스를 선택할 수 있습니다.
   * 버스 경로 : 버스 번호를 검색한 후 원하는 버스 번호를 선택하여 상세 검색을 하면 해당 버스의 경로가 결과로 나타납니다.
 * 버스 도착 정보
   * 버스 정류장 : 버스 정류장 이름을 검색하여 결과를 확인한 후 결과 목록에서 원하는 정류장을 선택할 수 있습니다.
   * 버스 도착 : 버스 정류장을 검색한 후 원하는 정류장을 선택하여 상세 검색을 하면 해당 정류장에 오는 버스들의 도착 예정 시간이 결과로 나타납니다. (30초 마다 갱신)
 * 즐겨찾기 기능
   * 즐겨 찾기 추가 : 버스 노선이나 도착 정보에 대해 검색을 하고 '즐겨 찾기 추가' 버튼을 누르면 즐겨 찾기에 추가 되고, 메인 화면에 즐겨 찾기 목록에 추가됩니다.
   * 메인화면에 즐겨 찾기 목록에서 엔터를 통해 해당 정보에 대한 빠른 검색이 가능합니다. (버스 경로, 버스 도착 정보)
   * 즐겨 찾기 삭제 : 메인 화면 '즐겨 찾기 삭제' 버튼
 * 옵션 기능
   * 옵션에서 시간을 설정할 수 있습니다. (사용안함, 3분, 7분, 10분)
   * 옵션을 설정하고, 메인 화면에 즐겨 찾기 목록에서 버스 도착 정보를 검색하면 목록이 30초마다 업데이트되는데, 이 때 목록에서 포커싱되어 있는 버스가 옵션 설정 시간 이내에 도착 예정일 경우 TTS를 출력한다.

<br>

### 주의할 점
* 안드로이드 휴대폰이나 에뮬레이터가 아닌 시각 장애인용 기기를 타겟으로 제작되었습니다. (일반적인 에뮬레이터에서 실행 불가)
* 안드로이드 스튜디오 버전이 3.2.1에 맞추어 있습니다.
* gradle 버전이 최신과 맞지 않습니다.
