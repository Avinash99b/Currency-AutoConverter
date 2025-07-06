
# 📱 Auto Currency Converter (Main Branch)

## 🧠 Convert Highlighted Text into Local Currency — Instantly, Anywhere on Android

Auto Currency Converter is an Android app that uses an **AccessibilityService** to detect when a user long clicks. If the selected text contains currency patterns like `£30`, `$99`, `€200`, etc., it automatically shows a **toast message with the converted currency** based on predefined or live exchange rates.

---

## ✨ Features

- 🔍 **Global Text Detection**  
  Monitors highlighted text across all apps using `AccessibilityService`.

- 💱 **Currency Pattern Recognition**  
  Detects currency strings like: £30, $99.50, ₹2500, €10.75

- ⚡ **Instant Conversion via Toast**  
  Matches currency text using regex and shows:

   "$99 ≈ ₹8250"


- 🌐 **Customizable Rates**  
  Uses hardcoded or dynamic exchange rates for local conversion (e.g., £1 = ₹105).

---

## 🚀 How It Works

1. User selects (highlights) text anywhere.
2. The app listens for `TYPE_VIEW_TEXT_SELECTION_CHANGED` events.
3. It extracts the selected text using `AccessibilityEvent.getText()`.
4. Matches against a regex pattern like `[£$€₹]\s?\d+(\.\d{1,2})?`.
5. Converts the amount using predefined logic.
6. Displays a toast with the converted value.

---

## 🧪 Example

**User selects:**
```
"Can I pay you £30 tomorrow?"
```

**App response:**
```
Toast: "£30 ≈ ₹3150"
```

*(Assuming £1 = ₹105)*

---
## App Usage

   <p align="center">
     <a href="https://youtube.com/shorts/IDFwKtcL4Ps">
       <img src="https://img.youtube.com/vi/IDFwKtcL4Ps/maxresdefault.jpg" alt="Video Title" width="500px">
     </a>
   </p>



---
## 📦 Tech Stack

- Android SDK (API 24+)
- Java/Kotlin
- AccessibilityService API
- Regex for currency pattern detection
- Toast UI

---

## 🛠️ Setup Instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/Avinash99b/Currency-AutoConverter.git
   cd Currency-AutoConverter
   ```

2. Open the project in Android Studio.

3. Build and install the app on your Android device.

4. Go to:
   ```
   Settings → Accessibility → Auto Currency Converter → Enable
   ```

5. Select text like `£30`, and the app will display the converted amount as a toast.

---

## ⚙️ Permissions

- `BIND_ACCESSIBILITY_SERVICE` — required to detect selected text.
- No internet is required unless using live exchange rates.

---

## 🔐 Privacy First

- No text is stored or sent anywhere.
- Everything is processed **locally** on-device.
- The app only reacts to **user-initiated text selections**.

---

## 🧭 Roadmap

🚧 This is the main branch with core functionality.

🔜 Upcoming branches will add:
- 🖼️ **OCR detection** using ML Kit + MediaProjection
- 🧊 **Floating overlay UI** instead of toasts
- 🌍 **Live exchange rates** via open APIs
- 🎯 OCR limited to user-highlighted screen area

---

## 👨‍💻 Author

**Bathula Avinash**  
Android developer building system-level tools to be lazy in daily life.
> “I am very lazy to convert by copying the text and searching it everytime.”

---

## 📃 License

This project is licensed under the MIT License — free to use, modify, and distribute.