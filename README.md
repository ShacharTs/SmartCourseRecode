# SmartCourse

A modern **EdTech platform** Android app that connects students with tutors. Built with Kotlin and Jetpack Compose.  
**Course:** Android development

---


## App Purpose

**SmartCourse** is an education technology (EdTech) application that enables:

- **Students** to discover tutors, browse courses, and manage learning.
- **Tutors** to showcase profiles, courses, and connect with students.
- **Real-time chat** and **posts feed** for engagement.
- **Theme and language** preferences for a personalized experience.

The app supports **two distinct user types (Tutor / Student)** with role-based flows and a unified user model.

---

## Tech Stack

| Category   | Technologies |
|-----------|---------------|
| **Language** | Kotlin |
| **UI**       | Jetpack Compose, Material 3 |
| **Database** | Firebase (Firestore, Realtime DB, Auth, FCM, Storage); Supabase (Auth, PostgREST, Storage). *Room* can be added for local caching. |
| **Async**    | Kotlin Coroutines, Flow |
| **DI**       | Hilt (Dagger) |
| **Navigation** | Jetpack Navigation Compose |
| **Serialization** | kotlinx.serialization |
| **Images**   | Coil for Compose |

**Summary list for grading:** Language: **Kotlin**. DB: **Firebase** (primary); **Room** (optional/local).

---

## System Architecture (MVVM)

The app follows the **MVVM (Model–View–ViewModel)** pattern — the **(MV)\*** architecture required for the course:

- **Model:** Data layer — domain models (`data/models/usermodel/User.kt`, `Course.kt`, `Post.kt`, chat models), data sources (Firebase, Supabase in `data/remote/`), and **repositories** (`AuthRepository`, `ProfileRepository`, `ChatRepositoryImpl`, `PostRepository`, `CourseRepository`, etc.) that abstract data access.
- **View:** UI layer — Jetpack Compose screens in `ui/screens/` (e.g. `LoginScreen`, `UserProfileScreen`, `SearchUserScreen`, `ChatScreen`, `UserRootScreen`). Views observe state and emit events only; they do not hold business logic.
- **ViewModel:** Presentation logic — ViewModels (`AuthViewModel`, `LoginViewModel`, `UserProfileViewModel`, `SearchUserViewModel`, `ChatListViewModel`, etc.) expose state (e.g. `StateFlow`/`LiveData`) and handle user actions by calling repositories. They do not reference the View (Compose) directly.

**Data flow:** User action → View → ViewModel → Repository → Model/Backend; result flows back as state → ViewModel → View (UI update).  
**Modular structure:** `data/` (models, repositories, remote), `ui/` (screens, theme), `navigation/`, `auth/`, `di/` (Hilt modules).

---

## Team & Contributions

*To satisfy grading: list who developed which specific features (avoid “we did it together”).*

| Contributor | Features / Areas Developed |
|-------------|----------------------------|
| *[Shachar Tsrafati]*   | *e.g. Auth (Email + Google), Login/Register screens, AuthViewModel* |
| *[Shachar Tsrafati]*   | *e.g. Tutor/Student home layouts, Search (SearchUserScreen, SearchUserViewModel)* |
| *[Shachar Tsrafati]*   | *e.g. Chat (ChatScreen, ChatViewModel, Camera/Location in chat), FCM* |
| *[Shachar Tsrafati]*      | *e.g. Profile (UserProfileScreen, ProfileRepository), Theme, Navigation, Posts* |


---

## Project Structure (High Level)

```
app/src/main/java/com/smartcourse/
├── auth/                    # Auth strategies (Email, Google), AuthViewModel, AuthState
├── data/
│   ├── models/              # User, Course, Post, Chat (Message, ChatItem)
│   ├── repositories/        # Auth, Profile, Chat, Post, Course, Theme, Notification
│   └── remote/              # Supabase client, Firebase (Firestore, Auth, FCM)
├── di/                      # Hilt modules (RepositoryModule, FirebaseModule, SupabaseModule)
├── navigation/              # RootNavHost, Screen routes
├── notifications/          # FCM (MyFirebaseMessagingService)
└── ui/
    ├── screens/             # login, register, home, profile, chat, search, posts, settings
    └── theme/               # SmartCourseTheme, AppPalette, ThemeMode
```

---

## Getting Started

1. Open the project in Android Studio (or compatible IDE).
2. Add `google-services.json` (Firebase) and configure Supabase as required.
3. Build and run on a device or emulator (minSdk 24, targetSdk 35).
