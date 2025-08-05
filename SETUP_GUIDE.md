# 갤럭시 워치 운동 트레이너 앱 설정 가이드

## 🚀 빠른 시작 (Gemini API만 필요)

### 1. Gemini API 키 설정 (필수)
`app/src/main/java/com/watchtrainer/data/GeminiAIManager.kt` 파일에서:
```kotlin
private const val API_KEY = "YOUR_GEMINI_API_KEY" // 여기에 실제 API 키 입력
```

[Gemini API 키 받기](https://makersuite.google.com/app/apikey)

### 2. Android SDK 설정
`local.properties` 파일에서 SDK 경로 설정:
```
sdk.dir=C:\\Users\\[사용자명]\\AppData\\Local\\Android\\Sdk
```

### 3. 빌드 및 실행
```bash
# Windows
gradlew.bat assembleDebug

# Mac/Linux
./gradlew assembleDebug
```

## 📱 주요 기능 (API 키 없이도 작동)

### ✅ Gemini API만으로 작동하는 기능:
- **AI 운동 코치**: 맞춤형 운동 가이드 및 동기부여
- **운동 추적**: 시간, 걸음 수, 칼로리 측정
- **운동 기록**: 모든 운동 데이터 저장
- **목표 설정**: 일일/주간 운동 목표
- **음성 피드백**: TTS 음성 안내
- **운동 타입 선택**: 6가지 운동 모드

### 🌤️ 선택적 기능 (API 키 필요):
- **날씨 정보** (OpenWeather API): 시스템 날씨가 없을 때만 사용
- **장소 추천** (Google Places API): 오프라인 추천도 제공

## 🛠️ 설치 방법

### Android Studio에서:
1. 프로젝트 열기
2. Gemini API 키만 설정
3. Run 버튼 클릭

### 명령줄에서:
```bash
# APK 빌드
./gradlew assembleDebug

# 워치에 설치
adb install app/build/outputs/apk/debug/app-debug.apk
```

## 🎯 작동 방식

### 날씨 정보 (API 키 없어도 OK)
1. **우선순위 1**: Wear OS 시스템 날씨 데이터
2. **우선순위 2**: OpenWeather API (키가 있는 경우)
3. **기본값**: 맑은 날씨 20°C

### 장소 추천 (API 키 없어도 OK)
1. **API 있을 때**: 실시간 주변 장소 검색
2. **API 없을 때**: 일반적인 운동 장소 추천
   - 공원, 운동장, 헬스장 등

## 📝 최소 요구사항

- **필수**: Gemini API 키
- **선택**: OpenWeather API, Google Places API
- **기기**: Galaxy Watch 4 이상 (Wear OS 3.0+)

## 🔧 문제 해결

### "SDK location not found" 오류
1. Android Studio 설치 확인
2. `local.properties`에 올바른 경로 설정
3. 또는 ANDROID_HOME 환경변수 설정

### Gemini API 오류
- API 키가 올바른지 확인
- 인터넷 연결 확인
- API 할당량 확인

## 💡 팁

- Gemini API 키만 있으면 핵심 기능은 모두 작동합니다
- 날씨와 장소는 선택사항이며, 없어도 기본값으로 작동합니다
- 삼성 헬스 SDK는 주석처리되어 있어 별도 설치 불필요