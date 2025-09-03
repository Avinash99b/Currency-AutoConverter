# 💵 Auto Currency Converter Monorepo

## Overview

Welcome to the **Auto Currency Converter** monorepo! This repository brings together both the **Android frontend** and the **backend service** that power seamless currency conversions anywhere on your device.

### 🔹 Android Frontend

* Detects highlighted text across apps using `AccessibilityService`.
* Recognizes currencies like \$99, £30, ₹2500, €10.75 using regex and OCR.
* Shows instant conversions via toast messages or floating overlay UI.
* Supports live exchange rates and customizable local rates.
* Processes everything locally for privacy and speed.

### 🔹 Backend Service

* Provides live exchange rates and currency conversion APIs.
* Optional OCR text extraction for images sent from the frontend.
* Caches rates to reduce API calls and supports fallback rates.
* Lightweight, secure, and does not store user data.

### 🌐 Why This Monorepo?

Combining frontend and backend allows for:

* Synchronized development between Android app and APIs.
* Easy versioning and updates.
* Centralized reference for features and architecture.

---

**Author:** Bathula Avinash — Android & Backend Developer

> "Making currency conversions seamless and automatic."
