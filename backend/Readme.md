# 🐳 Auto Currency Converter Backend Docker Setup

This repository contains the **Dockerfile** for running the **Auto Currency Converter backend** using PHP. The backend fetches, caches, and serves currency conversion rates to the Android frontend.

---

## 📦 Dockerfile Overview

```dockerfile
FROM php:8.1-cli

# Create app folder
WORKDIR /app

# Copy everything
COPY . .

# Create a new user and give permissions
RUN adduser --disabled-password --gecos '' appuser && \
    mkdir -p /app/cache && \
    chown -R appuser:appuser /app

# Switch to new user
USER appuser

# Start PHP server
CMD ["php", "-S", "0.0.0.0:8080"]
```

### Features

* Runs PHP 8.1 CLI inside a lightweight Docker container.
* Creates a dedicated user (`appuser`) for security.
* Sets up a `cache` directory for storing conversion rates.
* Exposes port `8080` to serve HTTP requests from the Android frontend.

### How It Works

1. Docker builds an image with PHP 8.1 and all backend files.
2. A dedicated user runs the PHP server inside `/app`.
3. PHP scripts fetch conversion rates and store them in `/app/cache`.
4. The Android app requests conversion data via HTTP from the running container.

### Running the Container

```bash
# Build the image
docker build -t currency-backend .

# Run the container
docker run -p 8080:8080 currency-backend
```

The backend will now be accessible at `http://localhost:8080`.

### Notes

* Ensure your PHP scripts handle caching and API fetching appropriately.
* You can mount `/app/cache` to a host volume if you want persistent caching.

### Author

**Bathula Avinash**

> "Making currency conversions seamless and automatic."

---

### License

MIT License — free to use, modify, and distribute.
