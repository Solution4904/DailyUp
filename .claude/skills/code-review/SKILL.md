---
name: code-review
description: DailyUp 프로젝트에서 현재 브랜치 변경분(vs main) 코드 리뷰 요청 시 사용. "코드 리뷰", "리뷰해줘", "변경분 봐줘", "이 브랜치 점검", "PR 전 검토" 등 리뷰 의도가 감지될 때 자동 발동. Android 라이프사이클·메모리, Kotlin 관용구, MVVM 아키텍처, 보안·권한 4가지 관점에서 한국어로 지적하고 자동으로 코드를 수정하지 않는다.
---

# Code Review Guide (DailyUp)

DailyUp 프로젝트에서 현재 브랜치(vs main) 변경분에 대한 코드 리뷰 요청을 받았을 때 사용하는 스킬. **반드시 아래 5단계 절차를 순서대로 수행하고, 사용자의 명시적 승인 없이는 절대 코드를 수정하지 않는다.**

## 절차

### 1단계: 변경 범위 파악

다음 명령으로 main 대비 변경 파일과 라인 수를 먼저 확인한다.

```
git diff main...HEAD --stat
git diff main...HEAD --name-status
```

판단 분기:
- **변경 파일 0개**: "현재 브랜치가 main과 동일합니다. 리뷰할 변경분이 없습니다." 출력 후 종료
- **변경 파일 1~20개**: 그대로 진행
- **변경 파일 21개 이상**: 사용자에게 우선순위 확인. 예: "변경 파일이 많습니다. (a) 신규 파일만 (b) 특정 디렉토리 (c) 전체 — 어떻게 진행할까요?"

빌드 산출물은 리뷰 제외:
- `app/build/`, `build/`, `**/generated/`, `*.apk`, `*.aab`

이후 `git diff main...HEAD -- <대상 경로>` 로 실제 diff를 확보한다.

### 2단계: 변경 파일 정독

- 변경된 `.kt`, `.xml`, `AndroidManifest.xml`, `build.gradle.kts`, `libs.versions.toml` 을 Read 로 풀 컨텍스트 확보
- 신규 파일은 전체를, 수정 파일은 변경 hunk 주변 ±30줄을 확인
- 호출자/구현체 파악이 필요하면 Grep/Glob 으로 보강 (예: 신규 BroadcastReceiver 가 어디서 register/등록되는지)

### 3단계: 4가지 관점 분석

아래 체크리스트를 모든 변경 파일에 적용한다. 발견 항목은 `파일경로:줄번호` 형식으로 기록.

#### ① Android 라이프사이클·메모리
- `PendingIntent` 에 `FLAG_IMMUTABLE` 명시 (Android 12+ 필수)
- `AlarmManager.cancel()` 시 등록 때와 동일한 requestCode·Intent action·data
- `BroadcastReceiver.onReceive` 내 장기 작업 금지 (10초 ANR), 비동기 필요 시 `goAsync()` / WorkManager 위임
- `BroadcastReceiver` 동적 등록 시 `unregisterReceiver` 짝 보장
- `LiveData.observe` 시 Fragment 는 `viewLifecycleOwner` (this 사용은 누수 위험)
- `Context` 를 ViewModel·싱글톤·companion·top-level 에 보관 금지 (필요 시 `ApplicationContext` 만)
- `ViewModel.onCleared()` 에서 리소스 해제 (옵저버, 콜백)
- Activity·Fragment 에 long-lived listener/callback 등록 시 해제 짝
- inner class · 익명 클래스로 outer 참조 잡지 않는지 (Handler·Runnable·Callback)

#### ② Kotlin 스타일·관용구
- `!!` 남용 → `?.let` / `requireNotNull` / `checkNotNull` / 초기화 보증 패턴
- `if-else` 사슬 → `when` 표현식
- 가변 컬렉션 외부 노출 금지 → public 은 `List`, 내부만 `MutableList`
- 가변 상태 노출 → `var` public 대신 `val` + 백킹 프로퍼티 또는 `LiveData`/`MutableLiveData` 분리
- scope function 의도 일관성: `let`(nullable·치환), `apply`(this 구성), `also`(부수효과), `run`(블록 결과), `with`(non-null receiver)
- `data class` / `sealed class` / `enum class` 적절성 (현재 `RepeatTypeEnum` 등)
- 가시성 누락: top-level/클래스 멤버는 기본 `public` — `private`/`internal` 필요 시 명시
- 매직 넘버·문자열 → `const val` / 리소스로 추출
- `lateinit var` 는 non-null primitive 불가, 초기화 보증 가능한 경우만 사용
- companion object 에 Context 보관 금지

#### ③ MVVM·아키텍처 (DailyUp 패턴)
- ViewModel 에 `Context`/`Activity`/`View` 직접 주입 금지 — 필요 시 `@ApplicationContext` 한정
- ViewModel 이 Android 프레임워크 클래스(`AlarmManager`, `NotificationManager`, `SharedPreferences`) 직접 호출 금지 → Repository/DataSource 경유
- View(Activity·Fragment·XML)에 비즈니스 로직 유출 금지 (분기·계산은 ViewModel)
- DataBinding 식 안에는 표시 로직만 (`@{vm.title}` OK, 복잡한 계산 금지)
- Hilt 스코프 적정성: 전역 자원만 `@Singleton`, 화면 한정은 `@ActivityScoped`/`@ViewModelScoped`
- `@Inject` 생성자 주입 우선 — 필드 주입은 Activity/Fragment/Receiver 같은 프레임워크 진입점에서만
- LocalDataManager 등 데이터 계층을 ViewModel 이 직접 사용해도, 향후 Repository 추출 여지가 있으면 `💡 제안` 으로 표기
- 리소스 ID 를 ViewModel 이 들고 다니지 않는지 (`R.string.*` 는 View 또는 Resources 래퍼 경유)

#### ④ 보안·권한
- `SCHEDULE_EXACT_ALARM` 사용 시 Android 12+ `AlarmManager.canScheduleExactAlarms()` 체크 + 권한 요청 화면 fallback
- `POST_NOTIFICATIONS` (Android 13+) 런타임 권한 요청 흐름 존재 여부
- `RECEIVE_BOOT_COMPLETED` 사용 시 `<receiver android:enabled="true" android:exported="true">` + `BOOT_COMPLETED` 인텐트 필터, 권한 회복 로직
- `<receiver>`, `<activity>`, `<service>` 의 `android:exported` 명시 (Android 12+ 필수)
- 외부 노출 컴포넌트는 `android:permission` 또는 signature-level 보호 검토
- `PendingIntent.FLAG_IMMUTABLE` (Android 12+ 필수, 변경 시 `FLAG_UPDATE_CURRENT` 추가)
- 민감 데이터 평문 저장 금지 (SharedPreferences 평문 → EncryptedSharedPreferences 검토)
- 로그에 PII·토큰 노출 금지 (release 빌드 `Log.d` 제거 여부)
- WebView 사용 시 `setJavaScriptEnabled(true)` 필요성 재검토, `addJavascriptInterface` 위험성

### 4단계: 4섹션 고정 포맷 출력 (한국어)

다음 4개 섹션을 **순서와 헤더 그대로** 출력한다. 항목이 없는 섹션은 "해당 없음" 한 줄로 유지 (섹션을 생략하지 않는다).

```
## 🚨 심각 (즉시 수정)
- 크래시·메모리 누수·권한 위반·보안 결함·데이터 손실 위험
- 형식: `- [파일경로:줄번호] 한 줄 요약 — 근거 1~2줄`

## ⚠️ 경고 (개선 필요)
- 아키텍처 위반·안티패턴·향후 버그 가능성
- 형식 동일

## 💡 제안 (선택)
- 스타일·관용구·가독성·소규모 리팩터
- 형식 동일

## ✅ 잘된 점
- 의도적으로 잘 분리된 책임, 적절한 패턴 적용, 깔끔한 네이밍 등
- 형식 동일 (위치는 대표 1~2개만)
```

각 항목은 한 줄 요약 + 필요 시 1~2줄 근거. **`파일경로:줄번호` 위치 명시는 필수**(잘된 점 제외 시 대표 위치).

마지막에 한 줄 요약 헤더로 마무리:

```
---
🚨 N건 · ⚠️ N건 · 💡 N건 · ✅ N건
```

### 5단계: 정지·자동 수정 금지

리뷰 출력 직후 다음 한 줄로 응답을 마친다:

> 수정이 필요한 항목이 있으면 알려주세요. (예: "🚨 1번 고쳐줘")

이 단계에서 **절대로** 다음을 수행하지 않는다:
- 사용자 승인 없는 Edit / Write / NotebookEdit
- 사용자 승인 없는 Bash/PowerShell 변경 명령 (git commit, push, gradle build 등)
- main 브랜치 자체 코드 리뷰 (변경분만)
- 변경되지 않은 파일에 대한 지적

사용자가 명시적으로 수정을 요청한 경우에만 해당 항목에 한정해 Edit 진행.

## 제약 (전 과정 공통)

- **출력 언어**: 한국어
- **비교 기준**: 항상 `main...HEAD` (3-dot, merge-base 기준). 단순 `main..HEAD` (2-dot) 금지
- **사실 검증**: "이 코드는 X일 것이다" 추측 금지 — 변경 파일을 Read 로 직접 확인
- **위치 명시**: 모든 지적은 `파일경로:줄번호` 필수
- **DailyUp 패턴 존중**: MVVM + DataBinding + Hilt(2.51.1) + LiveData (Coroutine/Flow/Compose 미사용) 가 현재 표준 — Coroutine·Compose 도입 강요 금지, 단순 제안 수준 유지
- **자동 수정 금지**: 5단계 참조

## 활용 예시

사용자: "코드 리뷰해줘"
→ `git diff main...HEAD --stat` → 변경 파일 5개 확인
→ Read 로 각 파일 정독 → 4가지 관점 분석
→ 4섹션 출력 → "수정이 필요한 항목이 있으면 알려주세요."

사용자: "/code-review"
→ 동일 절차 수행

사용자: "🚨 1번 고쳐줘"
→ 해당 항목만 Edit 으로 수정 후 결과 보고
