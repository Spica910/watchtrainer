# Contributing to Galaxy Watch Exercise Trainer

우리 프로젝트에 기여해주셔서 감사합니다! 이 문서는 프로젝트에 기여하는 방법을 안내합니다.

## 🤝 기여 방법

### 1. 이슈 생성
- 버그를 발견하거나 새로운 기능을 제안하고 싶다면 먼저 [이슈](https://github.com/Spica910/watchtrainer/issues)를 생성해주세요
- 이미 존재하는 이슈가 있는지 확인해주세요
- 이슈 템플릿을 사용하여 자세한 정보를 제공해주세요

### 2. Fork & Clone
```bash
# Fork 후 클론
git clone https://github.com/YOUR_USERNAME/watchtrainer.git
cd watchtrainer

# 업스트림 저장소 추가
git remote add upstream https://github.com/Spica910/watchtrainer.git
```

### 3. 브랜치 생성
```bash
# 최신 코드 동기화
git fetch upstream
git checkout master
git merge upstream/master

# 새 브랜치 생성
git checkout -b feature/your-feature-name
# 또는
git checkout -b bugfix/issue-number
```

### 4. 코드 작성
- Kotlin 코딩 컨벤션을 따라주세요
- 의미있는 커밋 메시지를 작성해주세요
- 테스트 코드를 작성해주세요
- 문서를 업데이트해주세요

### 5. 커밋
```bash
git add .
git commit -m "feat: Add new feature description

- Detailed explanation
- Related issue: #123"
```

#### 커밋 메시지 컨벤션
- `feat:` 새로운 기능 추가
- `fix:` 버그 수정
- `docs:` 문서 수정
- `style:` 코드 포맷팅, 세미콜론 누락 등
- `refactor:` 코드 리팩토링
- `test:` 테스트 추가/수정
- `chore:` 빌드 작업, 패키지 매니저 설정 등

### 6. Push & Pull Request
```bash
git push origin feature/your-feature-name
```

- GitHub에서 Pull Request를 생성합니다
- PR 템플릿을 작성합니다
- 코드 리뷰를 기다립니다

## 📋 코딩 가이드라인

### Kotlin 스타일 가이드
- [Kotlin 공식 코딩 컨벤션](https://kotlinlang.org/docs/coding-conventions.html)을 따릅니다
- Android Studio의 기본 포맷터를 사용합니다
- 의미있는 변수명과 함수명을 사용합니다

### 아키텍처
- MVVM 패턴을 따릅니다
- Repository 패턴을 사용합니다
- Coroutines를 사용한 비동기 처리

### 테스트
- Unit 테스트 작성을 권장합니다
- UI 테스트는 중요한 기능에 대해 작성합니다

## 🐛 버그 리포트

버그를 발견하셨다면:
1. 최신 버전에서도 발생하는지 확인
2. 이미 보고된 이슈가 있는지 확인
3. 재현 가능한 단계를 포함하여 이슈 생성

## 💡 기능 제안

새로운 기능을 제안하실 때:
1. 기능의 목적과 사용 사례 설명
2. 예상되는 동작 방식 설명
3. 가능하다면 목업이나 프로토타입 제공

## 📝 문서 기여

문서 개선도 환영합니다!
- README.md 개선
- 코드 주석 추가
- 위키 페이지 작성

## ❓ 질문

질문이 있으시다면:
- [Discussions](https://github.com/Spica910/watchtrainer/discussions) 사용
- 이슈에 `question` 라벨 사용

## 📜 라이선스

기여하신 코드는 프로젝트의 MIT 라이선스를 따릅니다.

감사합니다! 🙏