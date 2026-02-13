# EggChef 🍳

EggChef is an open-source Android egg boiling timer app that helps you perfectly boil eggs every time. Built with modern Android technologies and clean architecture principles, it offers preset boil profiles and custom timers with step-by-step instructions for achieving your desired egg consistency.

---

## 🔗 Links

- 🌐 **Website**: [https://deepankumarpn.github.io/egg_chef/index.html](https://deepankumarpn.github.io/egg_chef/index.html)
- 🔒 **Privacy Policy**: [https://deepankumarpn.github.io/egg_chef/privacy_policy.html](https://deepankumarpn.github.io/egg_chef/privacy_policy.html)
- 📱 **Google Play Store**: [Download on Play Store](https://play.google.com/store/apps/details?id=deepankumarpn.github.io.eggchef)

---

## ✨ Features

### ⏱️ Boiling Timer

Preset boil profiles:

- **Dippy Soldiers** — 3 min  
- **Runny Yolks** — 6 min  
- **Soft Boiled** — 8 min  
- **Hard Boiled** — 10 min  
- **Overcooked** — 12 min  

### ⚙️ Custom Boil Profiles

- Add custom boil types  
- Delete boil profiles  
- Persistent encrypted storage  

### 📖 Instructions

- Step-by-step boiling instructions  
- Easy-to-follow user experience  

### 🎛️ Timer Controls

- Start  
- Pause  
- Resume  
- Reset  
- Stop  

### 🔔 Alerts and Notifications

- Audio alert when timer completes  
- Push notifications support  

### 🔐 Secure Storage

- AES-256 GCM encryption  
- Android KeyStore integration  
- Encrypted DataStore storage  

---

## 🧱 Technologies Used

EggChef is built using modern Android development tools and best practices.

| Category | Technology |
|----------|------------|
| **Language** | Kotlin 2.0.21 |
| **UI** | Jetpack Compose |
| **UI Design** | Material 3 |
| **Architecture** | MVI + Clean Architecture |
| **State Management** | Contract Pattern (Unidirectional Data Flow) |
| **Dependency Injection** | Hilt |
| **Async Programming** | Kotlin Coroutines |
| **Local Storage** | Encrypted DataStore |
| **Security** | Android KeyStore + AES-256 GCM |
| **Navigation** | Jetpack Compose Navigation |
| **Serialization** | Gson |
| **Analytics** | Firebase Analytics |
| **Build Tool** | Gradle |

---

## ⚙️ Build Configuration

| Setting | Value |
|---------|-------|
| **Min SDK** | 28 |
| **Target SDK** | 36 |
| **Java Version** | 11 |

---

## 🏗️ Architecture

EggChef follows **MVI Architecture (Contract Pattern)** built on **Clean Architecture** principles with clear separation into three layers.

### Unidirectional Data Flow

```
UI ──▶ Event (Intent) ──▶ ViewModel ──▶ State ──▶ UI
```

- **Event** = Intent (user action)
- **State** = Model (single source of truth)
- **Compose Screen** = View

The ViewModel is an Android Jetpack component used to hold the MVI logic. This ensures:
- Predictable state management
- Single source of truth
- Clear separation of concerns
- Testable business logic

---

## 📁 Project Structure

```
app/
├── presentation/
├── domain/
├── data/
├── di/
├── navigation/
└── utils/

base/
├── BaseViewModel.kt
├── BaseUseCase.kt
├── SuspendUseCase.kt
├── FlowUseCase.kt
├── UiState.kt
├── UiEvent.kt
└── UiEffect.kt
```

---

## 🤝 Contribution Guide

Contributions are welcome! Please follow the project standards outlined below.

---

## 🔒 Branch Protection Policy

The `main` branch is protected.

**Rules:**

- Direct push to `main` is **NOT** allowed  
- Pull Request is **required**  
- Code Owner approval is **required**  
- All review comments must be resolved  
- Only **Squash and Merge** is allowed  

**Code Owner:**

[@deepankumarpn](https://github.com/deepankumarpn)

---

## 🌿 Branch Naming Standards

All branches must follow this format:

```
type_githubusername_shortdescription
```

### Branch Prefix Types

| Prefix | Type | Purpose                          |
|--------|------|----------------------------------|
| `fea` | Feature | New feature                      |
| `bug` | Bugfix | Bug fix, crash fix, ANR fix      |
| `ref` | Refactor | Code improvement or optimization |
| `doc` | Documentation | Documentation update             |
| `ver` | Version | Version update                   |

### Examples

```
fea_deepankumarpn_add-timer-feature
bug_johndoe_fix-crash-on-startup
ref_alice_optimize-performance
doc_mike_update-readme
ver_deepankumarpn_1.0.13_bump
```

---

## 🔁 Pull Request Process

**Step 1:** Fork the repository  

**Step 2:** Create branch  

```bash
git checkout -b fea_yourusername_feature-name
```

**Step 3:** Commit changes  

```bash
git commit -m "Add: new feature"
```

**Step 4:** Push branch

```bash
git push origin branch-name
```

**Step 5 (Mandatory):** Rebase with latest `main` before creating PR

```bash
git checkout main
git pull origin main
git checkout your_branch_name
git rebase main
git push --force-with-lease
```

> **Why Rebase?** Ensures clean linear history, no unnecessary merge commits, and no conflicts during merge.

**Step 6:** Create Pull Request to `main` branch

When creating your Pull Request, provide a **detailed description** including:

- **What was the issue/requirement?** — Describe the problem or feature request
- **What did you modify?** — Explain the changes you made and why
- **What's the impact?** — Describe how this affects the app
- **Testing done** — List the scenarios you tested
- **Screenshots/Videos** — (Optional) Add visual proof if UI changes are involved

**Example PR Description:**

```
## Issue
Timer notifications were not working on Android 13+

## Changes Made
- Added POST_NOTIFICATIONS permission check
- Implemented runtime permission request flow
- Updated notification channel configuration

## Impact
- Users on Android 13+ will now receive timer completion notifications
- No impact on older Android versions

## Testing
- Tested on Android 13, 14
- Verified notification shows when timer completes
- Verified permission request flow
```  

---

## 🔍 Pull Request Review Rules

Pull Request must:

- Follow branch naming standards  
- Be reviewed by Code Owner  
- Have at least 1 approval  
- Resolve all review comments  

Only Code Owner can merge Pull Requests.

---

## 🔀 Merge Strategy

EggChef uses:

**Squash and Merge**

**Benefits:**

- Clean commit history  
- Easy tracking of changes  
- Easy rollback

---

## 🔢 Version Bump Process

### What is Version Bump?

Version bump means increasing the version values in `versions.properties`.

**Example:**

| | `major` | `minor` | `patch` | `build` |
|---|---|---|---|---|
| Before release | `1` | `0` | `12` | `1` |
| After release (bumped) | `1` | `0` | `13` | `1` |

### Why Version Bump is Done AFTER Release

Version bump is performed **after** the GitHub Release and Play Store Release, not before.

**Example release cycle:**

1. **Release current version**
   - GitHub Release: `1.0.12`
   - Play Store Release: `1.0.12`
2. **Immediately bump version for next release**
   - `main` branch `versions.properties` becomes: `patch = 13`

**This ensures:**

- `main` branch always contains the next upcoming version
- No duplicate `versionCode` errors in Play Store
- No release conflicts
- Future features automatically use the correct version
- Proper CI/CD readiness

This is standard practice in professional production apps.

### Version Bump Branch Strategy

Version bump must **NEVER** be done directly in `main`. Always use a branch.

**Branch naming format:**

```
ver_<githubusername>_<version>_bump
```

**Example:**

```
ver_deepankumarpn_1.0.13_bump
```

### Version Bump Commit Strategy

**Commit message format:**

```
Version bump: <oldVersion> → <newVersion> (<oldCode> → <newCode>)
```

**Example:**

```
Version bump: 1.0.12 → 1.0.13 (1001201 → 1001301)
```

### Complete Version Bump Workflow

**Step 1 — Checkout latest main**

```bash
git checkout main
git pull origin main
```

**Step 2 — Create version bump branch**

```bash
git checkout -b ver_deepankumarpn_1.0.13_bump
```

**Step 3 — Update version in `versions.properties`**

```properties
major =1
minor =0
patch =13
build =1
```

**Step 4 — Commit changes**

```bash
git add .
git commit -m "Version bump: 1.0.12 → 1.0.13"
```

**Step 5 — Push branch**

```bash
git push origin ver_deepankumarpn_1.0.13_bump
```

---

## 🔢 Version Code Strategy

EggChef uses a structured `versionCode` format:

| Major | Minor | Patch | Build |
|-------|-------|-------|-------|
| 1     | 00    | 13    | 01    |

**Formula:**

```
versionCode = Major × 1000000 + Minor × 10000 + Patch × 100 + Build
```

**Example:** `versionCode = 1001301`

**Benefits:**

- Predictable versioning
- Easy tracking
- Supports future builds

---

## 🔄 Release Cycle Example

| Step | Action | Value |
|------|--------|-------|
| 1 | Current version | `1.0.12` |
| 2 | GitHub Release | `1.0.12` |
| 3 | Play Store Release | `1.0.12` |
| 4 | Create bump branch | `ver_deepankumarpn_1.0.13_bump` |
| 5 | Update `versions.properties` | `patch = 13` |
| 6 | Create PR and merge | Squash and Merge |
| 7 | `main` branch ready | `1.0.13` — ready for next development |

---

## 📋 Summary of Mandatory Rules

| MUST DO | NEVER DO |
|---------|----------|
| Version bump after every release | Direct commit to `main` |
| Use version bump branch | Skip version bump after release |
| Create Pull Request | Create PR without rebase |
| Rebase branch before creating PR | Push version change directly to `main` |
| Use squash merge | |

---

## 🚫 Do NOT

**Do NOT:**

- Push directly to `main` branch  
- Force push to `main` branch  
- Commit secrets or local configs  
- Submit unrelated large changes  

---

## ✅ Best Practices

- Create small focused Pull Requests  
- Write clear commit messages  
- Follow project structure  
- Test before submitting Pull Request  

---

## 👨‍💻 Maintainer

**Deepankumar Pachiappan**  

GitHub: [@deepankumarpn](https://github.com/deepankumarpn)

---

## 💬 Contributor Discussions

Have questions or want to discuss contributions?

📧 Email: **deepankumarpn@gmail.com**

Feel free to reach out for:
- Feature discussions
- Technical questions
- Contribution guidance
- Bug reports and suggestions

---

## 📄 License

Licensed under **Apache License 2.0**.  

See [LICENSE](https://github.com/deepankumarpn/EggChef?tab=Apache-2.0-1-ov-file) file for details.

---

## ⭐ Support

If you like this project:

- ⭐ Star the repository  
- 🍴 Fork and contribute  
- 🐛 Report issues  
- 💡 Suggest improvements  

---

**Happy Coding! 🍳**
