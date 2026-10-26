
# Study Zone — বাংলা নির্দেশিকা

**Study Zone** হলো Android 7.0+ (API 24+) লক্ষ্য করে তৈরি একটি Java/AndroidX প্রজেক্ট। Home UI, Browser WebView, Favorites, Downloads, Settings, About Me এবং GitHub Actions workflow অন্তর্ভুক্ত।

## ফোল্ডার ও ফাইল

- `app/src/main/java/com/firoz/studyzone/MainActivity.java` — Splash, Welcome, Home, navigation, drawer, favorites, settings, about ও update UI
- `BrowserActivity.java` — WebView, back/home/refresh, progress, offline notice ও Download Manager
- `LocalStore.java` — Favorites এবং theme preference স্থানীয়ভাবে সংরক্ষণ
- `UpdateChecker.java` — HTTPS JSON metadata যাচাই
- `AppConfig.java` — Website, Facebook, developer, version, theme colors ও metadata URL
- `app/src/main/assets/about_photo.jpg` — About Me-তে দেখানো আপনার আপলোড করা ছবি
- `app/src/main/res/drawable/app_logo.xml` — ভেক্টর অ্যাপ লোগো
- `.github/workflows/android-build.yml` — অনলাইনে APK build ও artifact upload

## গুরুত্বপূর্ণ কনফিগারেশন

`AppConfig.java`-তে `WEBSITE_URL`, `FACEBOOK_URL`, `DEVELOPER`, `VERSION`, রঙ এবং `UPDATE_METADATA_URL` বদলান। মূল ওয়েবসাইট WebView-তে সরাসরি লোড হয়; এর HTML/CSS/JavaScript পরিবর্তন করা হয় না।

### Home Page পরিবর্তন
`MainActivity.java`-এর `home()` মেথডে Home cards ও welcome copy পরিবর্তন করুন। ব্যক্তিগত ছবি Home-এ দেখানো হয় না।

### Theme পরিবর্তন
রঙ `AppConfig.java`-তে আছে। বর্তমানে theme preference Dark/Light হিসেবে সংরক্ষিত হয়; তবে **Light Mode-এর সম্পূর্ণ আলাদা palette এখনো বাস্তবায়িত নয়**—বর্তমান UI মূলত navy/gold palette ব্যবহার করে।

### About Me ছবি
আপনার আপলোড করা ছবিটি `app/src/main/assets/about_photo.jpg`-এ রাখা হয়েছে এবং শুধু About Me পেজে ব্যবহৃত হয়। অন্য ছবি দিতে চাইলে একই ফাইলের জায়গায় নতুন JPEG রাখুন (একই নাম)।

## GitHub-এ Upload ও APK Build

1. GitHub-এ নতুন repository তৈরি করুন।
2. ZIP extract করে সব ফাইল repository-র root-এ upload/commit করুন; `.github` folder-টিও রাখতে হবে।
3. GitHub-এর **Actions** tab খুলুন। Workflow অনুমোদন চাইলে enable করুন।
4. `Android APK Build` workflow চালান, অথবা `main`/`master`-এ push করুন।
5. সফল run খুলে **Artifacts → study-zone-debug-apk** ডাউনলোড করুন।
6. ZIP extract করে `app-debug.apk` Android ফোনে ইনস্টল করুন। Debug APK কেবল পরীক্ষা/সাইডলোডের জন্য।

Workflow Gradle wrapper ব্যবহার করে না; GitHub runner-এ ইনস্টল থাকা Gradle ব্যবহার করে। ভবিষ্যতে reproducible build-এর জন্য Gradle Wrapper যোগ করা ভালো।

## GitHub Releases ও Signing

Release APK প্রকাশের আগে:
- `app/build.gradle`-এ `versionCode` প্রতিটি release-এ বাড়ান এবং `versionName` বদলান।
- একই `applicationId` (`com.firoz.studyzone`) অপরিবর্তিত রাখুন।
- পুরোনো APK-কে update করতে একই signing key ব্যবহার করতে হবে। Keystore/password কখনো source code বা repository-তে commit করবেন না; GitHub Secrets ও secure signing step ব্যবহার করুন।
- বর্তমান workflow শুধু **debug APK** তৈরি করে। Signed release workflow/keystore integration এখনো যোগ করা হয়নি।
- `update-metadata.example.json`-কে আপনার নিজস্ব HTTPS hosting-এ publish করে বাস্তব release APK URL বসান; তারপর `AppConfig.UPDATE_METADATA_URL`-এ সেই JSON URL দিন। উদাহরণ JSON-এ placeholder URL ইচ্ছাকৃতভাবে আছে, বাস্তব download link নয়।
- Metadata format:
  - `version`: version name string, যেমন `"1.1"`
  - `notes`: update description
  - `apkUrl`: বাস্তব HTTPS APK URL

## Downloads ও Privacy

Browser downloads Android Download Manager ব্যবহার করে এবং public `Downloads/StudyZone` folder-এ যায়। System notification-এ progress/completion দেখা যায়। অ্যাপের Downloads পেজ একটি নির্দেশনা দেখায়; এটি এখনো নিজস্ব পূর্ণ download-history database নয়। Downloaded file deletion UI-ও এখনো নেই। Website offline reading সব সাইটে সম্ভব নয়।

Favorites ও theme preference `SharedPreferences`-এ থাকে। কোনো login, analytics বা external personal-data collection নেই। ওয়েবসাইট নিজস্ব cookies/storage ব্যবহার করতে পারে; WebView-তে DOM Storage চালু।

## Build ও সীমাবদ্ধতা

- Minimum SDK 24 (Android 7.0), compile/target SDK 35, Java 17।
- GitHub Actions build সফল না হওয়া পর্যন্ত APK build হয়েছে ধরে নেবেন না।
- এই পরিবেশে Android SDK/Gradle build চালিয়ে যাচাই করা হয়নি; তাই ZIP-টি buildable source project হিসেবে দেওয়া হচ্ছে, **পরীক্ষিত APK হিসেবে নয়**।
- Update version comparison সাধারণ numeric version-এর জন্য উপযোগী; complex semantic versioning পুরোপুরি সমর্থিত নয়।
- Light theme-এর সম্পূর্ণ UI palette, download list/progress history এবং signed release workflow ভবিষ্যৎ কাজ।
- Offline screen-এ Retry করতে browser toolbar-এর Refresh ব্যবহার করুন; network capability check কখনো কখনো captive portal/limited connectivity শনাক্ত নাও করতে পারে।
