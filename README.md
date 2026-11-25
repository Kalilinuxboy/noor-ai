# Qwen27B AI — Android client + Kaggle backend

এই repository-তে দুটি অংশ:
- `app/` এবং Gradle files: Android APK source।
- `notebooks/Qween-27b_unsensor.ipynb`: Kaggle GPU-তে Qwen model, FastAPI chat server এবং Cloudflare Tunnel চালানোর backend notebook।

## APK build
1. GitHub repository-র root-এ এই ZIP extract করে পাওয়া সব ফাইল/ফোল্ডার upload করুন।
2. `main` branch-এ push হলে GitHub Actions স্বয়ংক্রিয়ভাবে Debug APK build করবে।
3. Repository → Actions → সফল `Build Qwen AI APK` run → Artifacts → `QwenAI-debug-apk` ZIP download করুন।
4. ZIP extract করে `app-debug.apk` ফোনে install করুন।

## AI server
1. Kaggle-এ `notebooks/Qween-27b_unsensor.ipynb` import করুন।
2. Notebook-এর নির্দেশমতো GPU T4 x2 ও Internet ON দিন, যদি account-এ available থাকে।
3. সব cell উপর থেকে নিচে চালান।
4. শেষ cell-এর সম্পূর্ণ `https://....trycloudflare.com/?k=...` URL APK-তে paste করুন।

## সীমাবদ্ধতা ও নিরাপত্তা
- APK-তে 27B model থাকে না; inference Kaggle GPU-তে হয়।
- Kaggle session শেষ হলে server ও tunnel বন্ধ হবে; 24/7 uptime নিশ্চিত নয়।
- Tunnel URL এবং `?k=...` secret key কাউকে দেবেন না।
- Workflow unsigned Debug APK তৈরি করে; Play Store release signing নয়।
- GPU availability, quota, model download size ও runtime সফল হবে—নিশ্চিত নয়।
