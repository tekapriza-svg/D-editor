#include <jni.h>
#include <android/bitmap.h>
#include <cstdint>
#include <cmath>

static inline uint8_t clampByte(int value) {
    if (value < 0) return 0;
    if (value > 255) return 255;
    return static_cast<uint8_t>(value);
}

static bool lockBitmap(JNIEnv *env, jobject bitmap, AndroidBitmapInfo &info, void **pixels) {
    if (AndroidBitmap_getInfo(env, bitmap, &info) < 0) return false;
    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) return false;
    return AndroidBitmap_lockPixels(env, bitmap, pixels) >= 0;
}

static inline void unpack(uint32_t color, uint8_t &r, uint8_t &g, uint8_t &b, uint8_t &a) {
    r = color & 0xFF;
    g = (color >> 8) & 0xFF;
    b = (color >> 16) & 0xFF;
    a = (color >> 24) & 0xFF;
}

static inline uint32_t pack(uint8_t r, uint8_t g, uint8_t b, uint8_t a) {
    return (static_cast<uint32_t>(a) << 24) |
           (static_cast<uint32_t>(b) << 16) |
           (static_cast<uint32_t>(g) << 8) |
           static_cast<uint32_t>(r);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_deditor_nativefx_NativeEffects_grayscale(JNIEnv *env, jobject, jobject bitmap) {
    AndroidBitmapInfo info;
    void *pixels = nullptr;
    if (!lockBitmap(env, bitmap, info, &pixels)) return;

    auto *base = static_cast<uint8_t *>(pixels);

    for (uint32_t y = 0; y < info.height; y++) {
        auto *row = reinterpret_cast<uint32_t *>(base + y * info.stride);

        for (uint32_t x = 0; x < info.width; x++) {
            uint8_t r, g, b, a;
            unpack(row[x], r, g, b, a);
            uint8_t gray = static_cast<uint8_t>((r * 30 + g * 59 + b * 11) / 100);
            row[x] = pack(gray, gray, gray, a);
        }
    }

    AndroidBitmap_unlockPixels(env, bitmap);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_deditor_nativefx_NativeEffects_invert(JNIEnv *env, jobject, jobject bitmap) {
    AndroidBitmapInfo info;
    void *pixels = nullptr;
    if (!lockBitmap(env, bitmap, info, &pixels)) return;

    auto *base = static_cast<uint8_t *>(pixels);

    for (uint32_t y = 0; y < info.height; y++) {
        auto *row = reinterpret_cast<uint32_t *>(base + y * info.stride);

        for (uint32_t x = 0; x < info.width; x++) {
            uint8_t r, g, b, a;
            unpack(row[x], r, g, b, a);
            row[x] = pack(255 - r, 255 - g, 255 - b, a);
        }
    }

    AndroidBitmap_unlockPixels(env, bitmap);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_deditor_nativefx_NativeEffects_warm(JNIEnv *env, jobject, jobject bitmap) {
    AndroidBitmapInfo info;
    void *pixels = nullptr;
    if (!lockBitmap(env, bitmap, info, &pixels)) return;

    auto *base = static_cast<uint8_t *>(pixels);

    for (uint32_t y = 0; y < info.height; y++) {
        auto *row = reinterpret_cast<uint32_t *>(base + y * info.stride);

        for (uint32_t x = 0; x < info.width; x++) {
            uint8_t r, g, b, a;
            unpack(row[x], r, g, b, a);
            row[x] = pack(clampByte(r + 32), clampByte(g + 10), clampByte(b - 18), a);
        }
    }

    AndroidBitmap_unlockPixels(env, bitmap);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_deditor_nativefx_NativeEffects_cool(JNIEnv *env, jobject, jobject bitmap) {
    AndroidBitmapInfo info;
    void *pixels = nullptr;
    if (!lockBitmap(env, bitmap, info, &pixels)) return;

    auto *base = static_cast<uint8_t *>(pixels);

    for (uint32_t y = 0; y < info.height; y++) {
        auto *row = reinterpret_cast<uint32_t *>(base + y * info.stride);

        for (uint32_t x = 0; x < info.width; x++) {
            uint8_t r, g, b, a;
            unpack(row[x], r, g, b, a);
            row[x] = pack(clampByte(r - 14), clampByte(g + 8), clampByte(b + 35), a);
        }
    }

    AndroidBitmap_unlockPixels(env, bitmap);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_deditor_nativefx_NativeEffects_vignette(JNIEnv *env, jobject, jobject bitmap) {
    AndroidBitmapInfo info;
    void *pixels = nullptr;
    if (!lockBitmap(env, bitmap, info, &pixels)) return;

    auto *base = static_cast<uint8_t *>(pixels);

    float cx = info.width / 2.0f;
    float cy = info.height / 2.0f;
    float maxDistance = std::sqrt(cx * cx + cy * cy);

    for (uint32_t y = 0; y < info.height; y++) {
        auto *row = reinterpret_cast<uint32_t *>(base + y * info.stride);

        for (uint32_t x = 0; x < info.width; x++) {
            uint8_t r, g, b, a;
            unpack(row[x], r, g, b, a);

            float dx = x - cx;
            float dy = y - cy;
            float distance = std::sqrt(dx * dx + dy * dy) / maxDistance;
            float factor = 1.0f - 0.6f * distance * distance;

            row[x] = pack(
                clampByte(static_cast<int>(r * factor)),
                clampByte(static_cast<int>(g * factor)),
                clampByte(static_cast<int>(b * factor)),
                a
            );
        }
    }

    AndroidBitmap_unlockPixels(env, bitmap);
}
