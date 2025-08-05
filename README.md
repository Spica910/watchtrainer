# 🏃‍♂️ Galaxy Watch Exercise Trainer

[![Build Galaxy Watch App](https://github.com/Spica910/watchtrainer/actions/workflows/build.yml/badge.svg)](https://github.com/Spica910/watchtrainer/actions/workflows/build.yml)

갤럭시 워치용 AI 기반 운동 가이드 앱입니다. 실시간 운동 데이터와 날씨 정보를 활용하여 Gemini AI가 맞춤형 운동 가이드를 제공합니다.

## 📱 주요 기능

### 1. 🏋️ 실시간 운동 추적
- 심박수, 칼로리, 거리, 걸음 수 모니터링
- 다양한 운동 유형 지원 (걷기, 달리기, 자전거, 근력운동 등)
- 운동 시간 및 강도 측정

### 2. 🤖 AI 운동 코치
- Gemini API를 활용한 개인 맞춤형 운동 가이드
- 실시간 동기부여 메시지
- 운동 중 음성 피드백 (TTS)

### 3. 🌤️ 날씨 기반 추천
- 현재 위치 기반 날씨 정보
- 날씨에 따른 운동 추천 및 장소 제안
- 시스템 날씨 API 우선 사용 (인터넷 불필요)

### 4. 📍 운동 장소 추천
- 주변 공원, 헬스장, 운동 시설 추천
- 실내/실외 구분
- 오프라인 추천 지원

### 5. 🎯 목표 관리
- 일일/주간/월간 운동 목표 설정
- 실시간 목표 달성도 추적
- 목표 달성 알림

### 6. 📊 운동 기록
- 상세한 운동 히스토리
- 통계 및 분석 차트
- 운동 패턴 분석

## 🛠️ 기술 스택

- **플랫폼**: Wear OS 3.0+
- **언어**: Kotlin
- **UI**: Jetpack Compose for Wear OS
- **데이터베이스**: Room
- **AI**: Google Gemini API
- **아키텍처**: MVVM
- **비동기 처리**: Kotlin Coroutines & Flow

## 📋 요구사항

- Galaxy Watch 4 이상
- Wear OS 3.0 이상
- Android Studio Arctic Fox 이상
- JDK 17

## 🚀 설치 방법

### 옵션 1: GitHub Actions에서 APK 다운로드

1. [Actions 탭](https://github.com/Spica910/watchtrainer/actions) 방문
2. 최신 성공한 빌드 클릭
3. Artifacts에서 `app-debug` 다운로드
4. Galaxy Watch에 설치

### 옵션 2: 소스코드에서 빌드

1. 저장소 클론
   ```bash
   git clone https://github.com/Spica910/watchtrainer.git
   cd watchtrainer
   ```

2. Gemini API 키 설정
   - `app/src/main/java/com/watchtrainer/data/GeminiAIManager.kt` 파일 수정:
   ```kotlin
   private const val API_KEY = "YOUR_GEMINI_API_KEY" // 여기에 API 키 입력
   ```
   - [Gemini API 키 받기](https://makersuite.google.com/app/apikey)

3. Android Studio에서 빌드
   - 프로젝트 열기
   - Gradle Sync 실행
   - Run 버튼 클릭 (▶️)

### Galaxy Watch에 설치

1. **개발자 모드 활성화**
   - 설정 → 워치 정보 → 소프트웨어 정보
   - 소프트웨어 버전 5번 탭

2. **디버깅 활성화**
   - 설정 → 개발자 옵션 → ADB 디버깅 ON
   - Wi-Fi 디버깅 ON

3. **ADB로 설치**
   ```bash
   adb connect [워치 IP 주소]
   adb install app-debug.apk
   ```

## 📂 프로젝트 구조

```
watchtrainer/
├── app/
│   ├── src/main/java/com/watchtrainer/
│   │   ├── data/              # 데이터 모델 및 관리자
│   │   ├── services/          # 백그라운드 서비스
│   │   ├── ui/               # UI 컴포넌트 및 화면
│   │   ├── utils/            # 유틸리티 클래스
│   │   └── viewmodels/       # ViewModel 클래스
│   └── src/main/res/         # 리소스 파일
└── gradle/                   # Gradle 설정
```

## 🔧 설정

### 필수 권한
- 위치 정보
- 신체 센서
- 활동 인식
- 인터넷

### 선택적 API 키
- **WeatherDataManager.kt**: OpenWeatherMap API 키 (선택, 시스템 날씨 우선 사용)
- **PlaceRecommendationManager.kt**: Google Places API 키 (선택, 오프라인 추천 제공)

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

This project is licensed under the MIT License.

## 👥 개발자

- **Spica910** - [GitHub](https://github.com/Spica910)

## 🙏 감사의 말

- Google Gemini AI API
- Wear OS Development Team
- Android Jetpack Compose Team