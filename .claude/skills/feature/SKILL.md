---
name: feature
description: DailyUp 프로젝트에서 새 Android 기능 구현 요청 시 사용. "~기능 만들고 싶어", "~추가해줘", "~구현해줘", "~만드는 법" 등 기능 추가 의도가 감지될 때 자동 발동. 공식 문서 기반 개념 설명, 권한/Manifest 안내, 변경 파일 위치 매핑, 주의사항을 한국어로 제시하고 코드는 작성하지 않는다.
---

# Feature Implementation Guide (DailyUp)

DailyUp 프로젝트에서 새 기능 구현 요청을 받았을 때 사용하는 스킬. **반드시 아래 5단계 절차를 순서대로 수행하고, 절대 코드를 작성하거나 파일을 수정하지 않는다.**

## 절차

### 1단계: 기능 영역 식별

요청을 다음 영역 중 하나(또는 복수)로 분류한다:

- **Android 프레임워크**: Activity, Service, BroadcastReceiver, AlarmManager, NotificationManager, WorkManager, ContentProvider, Widget, Intent 등
- **Kotlin 언어**: Coroutines, Flow, sealed class, data class, extension function 등
- **라이브러리**: Hilt(DI), Room(DB), Glide(이미지), Gson(JSON), Material Design, DataBinding 등

분류 결과를 사용자에게 한 줄로 명시한다. (예: "다크모드 → Android 프레임워크 (Configuration / Theme)")

### 2단계: 공식 문서 조회 (WebFetch 필수)

식별된 영역에 따라 **반드시 실제 공식 도메인**에서 문서를 조회한다. URL을 추측하지 않는다.

| 영역 | 공식 소스 |
|---|---|
| Android 프레임워크 | `developer.android.com` |
| Kotlin 언어 | `kotlinlang.org` |
| Hilt | `dagger.dev/hilt` |
| Room | `developer.android.com/training/data-storage/room` |
| Glide | `bumptech.github.io/glide` |
| Material Design | `m3.material.io` 또는 `developer.android.com/jetpack/androidx/releases/compose-material3` |
| 그 외 라이브러리 | 해당 라이브러리 GitHub 또는 공식 사이트 |

WebFetch로 핵심 가이드 페이지를 가져와 요약 근거로 사용한다.

### 3단계: 4섹션 고정 포맷 출력 (한국어)

다음 4개 섹션을 **순서와 헤더 그대로** 출력한다.

```
## 📖 개념/원리
- 기능이 무엇이고 어떻게 동작하는지 (공식 문서 근거)
- 핵심 컴포넌트/클래스 이름
- 출처: <실제 공식 문서 URL>

## 🔐 권한/Manifest 설정
- AndroidManifest.xml 경로: app/src/main/AndroidManifest.xml
- 추가해야 할 <uses-permission> 또는 <uses-feature>
- <application> / <activity> / <receiver> / <service> 등록 필요 여부
- API level별 차이 (예: Android 13+, 12+, 8+ 분기)

## 📂 DailyUp 변경 위치
DailyUp 프로젝트의 실제 패키지 구조를 기준으로 수정/추가가 필요한 파일을 path 형식으로 안내. 가능하면 기존 유사 파일을 레퍼런스로 명시.

기본 패키지 루트: app/src/main/java/app/solution/dailyup/

- view/         — Activities (MainActivity, AddScheduleActivity, LoginActivity, SettingsActivity, ChartActivity)
- viewmodel/    — ViewModels (MVVM)
- model/        — Data models (ScheduleModel 등)
- adapter/      — RecyclerView Adapters
- utility/      — Utilities (LocalDataManager, ScheduleAlarmScheduler, *Enum)
- receiver/     — BroadcastReceivers (ScheduleAlarmReceiver)
- event/        — UI Event classes

리소스/빌드:
- app/src/main/res/layout/      — DataBinding XML 레이아웃
- app/src/main/res/values/      — strings, colors, themes
- app/src/main/AndroidManifest.xml
- app/build.gradle.kts          — 의존성 추가
- gradle/libs.versions.toml     — 버전 카탈로그

유사 기능 레퍼런스 (해당 시 인용):
- AlarmManager / 알림 → app/src/main/java/app/solution/dailyup/utility/ScheduleAlarmScheduler.kt
- BroadcastReceiver → app/src/main/java/app/solution/dailyup/receiver/ScheduleAlarmReceiver.kt

## ⚠️ 주의사항 (Pitfalls)
- 해당 기능에서 자주 놓치는 함정
- API level 호환성 이슈 (minSdk 대비)
- DailyUp의 MVVM + DataBinding + Hilt(2.51.1) 패턴과 충돌 가능성
- 런타임 권한 / 백그라운드 제약 / Doze / 배터리 최적화 영향
```

### 4단계: 정지

설명이 끝나면 다음 한 줄로 응답을 마친다:

> 구현이 필요하시면 별도로 요청해주세요.

이 단계에서 **절대로** 다음 도구를 호출하지 않는다:
- Edit / Write / NotebookEdit
- Bash / PowerShell의 변경 명령(git commit, gradle build 등)
- 그 외 파일/시스템을 변경하는 모든 도구

읽기/검색/WebFetch만 허용된다.

### 5단계: 제약 (전 과정 공통)

- **출력 언어**: 한국어
- **URL 추측 금지**: WebFetch로 확인하지 않은 URL은 인용하지 않는다
- **패턴 일관성**: DailyUp의 MVVM + DataBinding + Hilt 패턴을 위배하는 안내 금지
- **코드 작성 금지**: 공식 문서 인용 스니펫(짧은 시그니처 수준)은 허용, 실제 구현 코드는 작성하지 않는다
- **프로젝트 경로 검증**: 안내하는 변경 위치가 실제로 존재하는 패키지/파일인지 필요 시 Glob/Read로 확인

## 활용 예시

사용자: "다크모드 추가하고 싶어"
→ 영역: Android 프레임워크 (Theme / DayNight)
→ WebFetch: developer.android.com/develop/ui/views/theming/darktheme
→ 4섹션 출력 → "구현이 필요하시면 별도로 요청해주세요." 종료

사용자: "/feature 위젯 만드는 법"
→ 영역: Android 프레임워크 (App Widget)
→ WebFetch: developer.android.com/develop/ui/views/appwidgets/overview
→ 4섹션 출력 → 종료