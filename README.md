# 🏃‍♂️ Galaxy Watch Exercise Trainer

[![Build Galaxy Watch App](https://github.com/Spica910/watchtrainer/actions/workflows/build.yml/badge.svg)](https://github.com/Spica910/watchtrainer/actions/workflows/build.yml)

갤럭시 워치용 AI 기반 운동 가이드 앱입니다. 삼성 헬스 데이터와 날씨 정보를 활용하여 Gemini AI가 맞춤형 운동 가이드를 제공합니다.

## 주요 기능

### 1. 운동 데이터 추적
- 실시간 심박수 모니터링
- 걸음 수 및 칼로리 추적
- 운동 시간 측정

### 2. AI 운동 코치
- Gemini API를 활용한 맞춤형 운동 가이드
- 날씨와 신체 상태를 고려한 운동 추천
- 동기부여 메시지 제공

### 3. 날씨 연동
- 현재 위치 기반 날씨 정보
- 날씨에 따른 운동 추천

### 4. 하드웨어 버튼 지원
- 메인 버튼으로 빠른 운동 시작/정지
- 진동 피드백

### 5. 실시간 알림
- 운동 중 상태 표시
- 목표 달성 알림
- AI 코치 메시지

## 기술 스택

- **플랫폼**: Wear OS 3.0+
- **언어**: Kotlin
- **UI**: Compose for Wear OS
- **데이터**: Samsung Health SDK
- **AI**: Google Gemini API
- **날씨**: OpenWeatherMap API

## 설정 방법

### 1. API 키 설정

필수 API 키:
- `app/src/main/java/com/watchtrainer/data/GeminiAIManager.kt`: Gemini API 키 (필수)
  ```kotlin
  private const val API_KEY = "YOUR_GEMINI_API_KEY" // 여기에 API 키 입력
  ```
  [Gemini API 키 받기](https://makersuite.google.com/app/apikey)

선택적 API 키:
- `WeatherDataManager.kt`: OpenWeatherMap API 키 (선택, 시스템 날씨 우선 사용)
- `PlaceRecommendationManager.kt`: Google Places API 키 (선택, 오프라인 추천 제공)

### 2. Samsung Health SDK (선택사항)

현재 Samsung Health SDK는 주석 처리되어 있습니다. 필요시 `app/libs/` 디렉토리에 AAR 파일을 추가하고 `app/build.gradle.kts`에서 주석을 해제하세요.

### 3. 권한 설정

앱 실행 시 다음 권한들이 필요합니다:
- 위치 정보
- 신체 센서
- 활동 인식
- 인터넷

## 빌드 및 실행

1. Android Studio에서 프로젝트 열기
2. Wear OS 에뮬레이터 또는 실제 기기 연결
3. Run 버튼 클릭

## 사용 방법

1. 앱 실행 또는 메인 버튼 짧게 누르기
2. "운동 시작" 버튼 탭
3. 운동 중 실시간 데이터 확인
4. AI 코치 조언 받기
5. 운동 종료 후 결과 확인