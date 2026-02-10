# ✅ DoneRight – Get Things Done.

DoneRight is a clean, offline-first task management Android app built with modern Android architecture.  
It helps users create, track, and manage tasks efficiently while optionally syncing data to the cloud.

---

## ✨ Features

- 📋 Create, edit, update, and delete tasks
- 🏷 Task statuses: **To Do**, **In Progress**, **Done**
- 💾 Offline-first using **Room Database**
- ☁️ Optional cloud sync using **Firebase Firestore**
- 🔐 Google Sign-In for cloud backup
- 🔄 Automatic sync when internet is available
- 🧭 Navigation Drawer with Help & Account options
- 🆘 Help page with usage guide and FAQs
- 🎨 Minimal, modern UI with Material Design
- ⚡ Fast, lightweight, and responsive

---

## 🏗 Architecture

The app follows **MVVM (Model–View–ViewModel)** architecture and clean code principles.

**Tech stack used:**

- **Kotlin**
- **Room DB** (local storage)
- **Firebase Firestore** (cloud sync)
- **Firebase Authentication** (Google Sign-In)
- **RecyclerView**
- **LiveData & ViewModel**
- **Material Design Components**
- **Single Activity + Fragments**

---

## 📱 Offline & Sync Behavior

- Tasks are always saved locally
- App works fully offline
- When signed in, tasks sync automatically to the cloud
- If offline, changes are queued and synced later
- Signing in restores tasks after reinstall or device change

---

## 🆘 Help & Support

The app includes a built-in Help section explaining:
- How to use the app
- Task statuses
- Offline usage
- Cloud sync behavior

Users can also contact the developer directly via email from the app.

---

## 📦 Build Info

- **Min SDK:** 24
- **Target SDK:** Latest
- **Build system:** Gradle
- **Language:** Kotlin

---

## THANK YOU

