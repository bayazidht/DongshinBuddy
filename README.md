# DongshinBuddy: AI-Powered Campus Guide 🎓🤖

![GitHub last commit](https://img.shields.io/github/last-commit/bayazidht/DongshinBuddy)
![GitHub language count](https://img.shields.io/github/languages/count/bayazidht/DongshinBuddy)
![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)

## 🌟 Overview

**DongshinBuddy** is an intelligent campus companion application built specifically for the students and faculty of Dongshin University. By integrating cutting-edge AI models (Gemini & Groq) and real-time cloud synchronization, the app simplifies campus navigation, provides instant academic assistance, and centralizes university resources into a single, sleek interface.

### 📥 Download Now

<a href="https://play.google.com/store/apps/details?id=com.bayazidht.dongshinbuddy"> <img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" width="200"/> </a>

## 🖼️ App Screenshots: A Quick Tour

Take a look at the core functionalities and interface of DongshinBuddy:

| Home & AI Chat | 
| :---: |
|<img src="https://github.com/user-attachments/assets/2dcaa052-6e20-40a1-8d5e-123f7359da7e"/> |


## 🚀 Features

### 🧠 Smart AI Assistant

-   **Dual-Engine Intelligence:** Powered by **Google Gemini** and **Groq Cloud (LPU)** for lightning-fast responses.
    
-   **Campus-Aware:** Pre-configured with university-specific context to answer academic and administrative queries accurately.
    
-   **Markdown Support:** Rich text rendering for better readability of AI-generated tables, lists, and code snippets.
    

### 📍 Interactive Campus Map

-   **Building Locator:** Custom pins for every major building on the Dongshin University campus.
    
-   **Seamless Navigation:** One-tap integration with **Kakao Map** via Intent API for precise walking or driving directions.
    
-   **Lightweight WebView:** Optimized map rendering for smooth performance on all devices.
    

### 🔗 Quick University Portal

-   **Centralized Links:** Instant access to the Student Portal, LMS, Library, and Course Schedules.
    
-   **Real-time Updates:** Link data is managed via **Firebase**, allowing for instant updates without requiring a new app release.
    

### 🎨 Modern UI/UX

-   **Material Design 3:** Follows the latest Google design guidelines for a premium feel.
    
-   **Dynamic Theme:** Full support for **Dark Mode** and System Default themes.
    
-   **Privacy-First:** No login required, no data collection, and 100% anonymous AI interactions.


----------


## 🛠 Technology Stack

-   **Language:** Kotlin (Native Android)
    
-   **Architecture:** MVVM (Model-View-ViewModel)
    
-   **AI Engines:** Google Gemini API & Groq Cloud (LPU)
    
-   **Backend:** Firebase Realtime Database
    
-   **Networking:** Retrofit, OkHttp, Coroutines & Flow
    
-   **UI/UX:** Material Design 3 (M3), Lottie Animations
    
-   **Rich Text:** Markwon (For AI Markdown rendering)
    
-   **Navigation:** Kakao Map Intent API & WebView


## 🏗 System Architecture

The app follows a clean **MVVM architecture** to ensure the codebase is scalable and maintainable.

1.  **View:** Handles UI rendering (Activities/Fragments).
    
2.  **ViewModel:** Manages UI states and acts as a bridge to data.
    
3.  **Repository:** Orchestrates data flow between AI APIs and Firebase.
    
4.  **Remote Data:** Fetches intelligence from Groq/Gemini and metadata from Firebase.



## **🧠 RAG (Retrieval-Augmented Generation)**

DongshinBuddy utilizes **Dynamic RAG** by fetching university-specific context from **Firebase Realtime Database** to ensure the AI provides up-to-date campus information.

-   **Knowledge Source**: Verified campus data (schedules, building details, portal links) is stored and managed in **Firebase**.
    
-   **Dynamic Retrieval**: The `AppRepository` fetches the latest context from Firebase during the app session.
    
-   **Context Injection**: The `ChatViewModel` injects this real-time data into the AI prompt via `AIConfig`.
    
-   **Benefit**: This allows for "Over-the-Air" (OTA) updates to the AI's knowledge base without requiring a new app update on the Play Store.


## 📂 Project Structure

```text
com.bayazidht.dongshinbuddy
├── api             # Retrofit Client and Groq/Gemini Service interfaces
├── data
│   ├── local       # Local datasets (Chips, Context, and Links)
│   └── repository  # Logic for fetching data (AIConfig, AppRepository)
├── model           # Data classes (ChatMessage and API response models)
├── ui
│   ├── activities  # Main and Chat screens
│   └── fragments   # Home and Settings fragments
├── adapter         # RecyclerView adapter for the Chat interface
├── viewmodel       # ViewModel for managing UI state and AI logic
└── utils           # Helper classes (Prefs, Constants, and Custom Tabs)

```


----------


## 📄 Project Resources & Documentation

For a deeper dive into the technical details and quality assurance of **DongshinBuddy**, please refer to the documents below:

-   **[Software Requirement Specification (SRS)](https://github.com/bayazidht/DongshinBuddy/docs/SRS_DongshinBuddy.pdf)** – Detailed functional and non-functional requirements.
    
-   **[Software Design Specification (SDS)](https://github.com/bayazidht/DongshinBuddy/docs/SDS_DongshinBuddy.pdf)** – Technical architecture, database schema, and API designs.
    
-   **[Testing & Quality Report](https://github.com/bayazidht/DongshinBuddy/docs/TestReport_DongshinBuddy.pdf)** – Summary of closed testing (12+ testers) and AI response verification.


----------


## 📦 Installation & Setup

1.  **Clone the Repository:**
    
    Bash
    
    ```
    git clone https://github.com/bayazidht/DongshinBuddy.git
    
    ```
    
2.  **API Keys:** Create a `secrets.properties` or add your keys to `BuildConfig`:
    
    -   `GROQ_API_KEY=your_key_here`
        
    -   `GEMINI_API_KEY=your_key_here`
        
3.  **Firebase Setup:** Add your `google-services.json` to the `app/` directory.
    
4.  **Build:** Sync with Gradle and run on an Android device (API 24+).
    

----------


## 👨‍💻 Development Team

-   **[Syed Bayazid Hossain](https://github.com/bayazidht)** – _Developer & System Architect_
    
-   **[Md Rizvee Hasan](https://github.com/rizveehasan19)** – _Data Research & Analytics_
    
-   **Published By:** **[Coder Spark](https://www.linkedin.com/company/coder-spark/)**
    

----------

## 📄 License & Credits

- **Copyright:** © 2026 [Coder Spark](https://www.linkedin.com/company/coder-spark/). All rights reserved.
- **License:** Distributed under the [MIT License](https://opensource.org/licenses/MIT).
- **Acknowledgments:** Special thanks to **Dongshin University** for the campus data and the developers of **Groq** and **Google Gemini** for providing the AI infrastructure for this project.
