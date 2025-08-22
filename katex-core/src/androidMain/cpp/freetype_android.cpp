#pragma clang diagnostic push
#pragma ide diagnostic ignored "UnusedParameter"
#pragma ide diagnostic ignored "cppcoreguidelines-narrowing-conversions"

#include <jni.h>
#include "freetype_cinterop.h"


extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_init(JNIEnv *env, jobject thiz) {
    return initLibrary();
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_doneFreeType(JNIEnv *env, jobject thiz, jlong library) {
    return doneFreeType(library);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_libraryVersion(JNIEnv *env, jobject thiz,
                                                          jlong library) {
    int major, minor, patch;
    FT_Library_Version((FT_Library) library, &major, &minor, &patch);

    jclass cls = env->FindClass("com/pvporbit/freetype/LibraryVersion");
    jmethodID methodID = env->GetMethodID(cls, "<init>", "(III)V");
    jobject a = env->NewObject(cls, methodID, major, minor, patch);
    return a;
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_newMemoryFace(JNIEnv *env, jobject thiz, jlong library,
                                                         jobject data, jint length,
                                                         jlong face_index) {
    char *buffer = (char *) (data ? env->GetDirectBufferAddress(data) : nullptr);
    return newMemoryFace(library, reinterpret_cast<char *>(buffer), length, face_index);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_getMathTableLength(JNIEnv *env, jobject thiz, jlong face) {
    return getMathTableLength(face);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_loadMathTable(JNIEnv *env, jobject thiz, jlong face,
                                                         jobject data, jlong length) {
    char *buffer = (char *) (data ? env->GetDirectBufferAddress(data) : nullptr);
    return loadMathTable(face, buffer, length);
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_faceGetAscender(JNIEnv *env, jobject thiz, jlong face) {
    return faceGetAscender(face);
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_faceGetDescender(JNIEnv *env, jobject thiz, jlong face) {
    return faceGetDescender(face);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_faceGetFaceFlags(JNIEnv *env, jobject thiz, jlong face) {
    return faceGetFaceFlags(face);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_faceGetFaceIndex(JNIEnv *env, jobject thiz, jlong face) {
    return faceGetFaceIndex(face);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_faceGetFamilyName(JNIEnv *env, jobject thiz,
                                                             jlong face) {
    char *familyName = faceGetFamilyName(face);
    return env->NewStringUTF(familyName ? familyName : "");
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_faceGetHeight(JNIEnv *env, jobject thiz, jlong face) {
    return faceGetHeight(face);
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_faceGetMaxAdvanceHeight(JNIEnv *env, jobject thiz,
                                                                   jlong face) {
    return faceGetMaxAdvanceHeight(face);
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_faceGetMaxAdvanceWidth(JNIEnv *env, jobject thiz,
                                                                  jlong face) {
    return faceGetMaxAdvanceWidth(face);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_faceGetNumFaces(JNIEnv *env, jobject thiz, jlong face) {
    return faceGetNumFaces(face);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_faceGetNumGlyphs(JNIEnv *env, jobject thiz, jlong face) {
    return faceGetNumGlyphs(face);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_faceGetStyleFlags(JNIEnv *env, jobject thiz,
                                                             jlong face) {
    return faceGetStyleFlags(face);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_faceGetStyleName(JNIEnv *env, jobject thiz, jlong face) {
    auto styleName = faceGetStyleName(face);
    return env->NewStringUTF(styleName ? styleName : "");
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_faceGetUnderlinePosition(JNIEnv *env, jobject thiz,
                                                                    jlong face) {
    return faceGetUnderlinePosition(face);
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_faceGetUnderlineThickness(JNIEnv *env, jobject thiz,
                                                                     jlong face) {
    return faceGetUnderlineThickness(face);
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_faceGetUnitsPerEM(JNIEnv *env, jobject thiz,
                                                             jlong face) {
    return faceGetUnitsPerEM(face);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_faceGetGlyph(JNIEnv *env, jobject thiz, jlong face) {
    return faceGetGlyph(face);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_faceGetSize(JNIEnv *env, jobject thiz, jlong face) {
    return faceGetSize(face);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_getTrackKerning(JNIEnv *env, jobject thiz, jlong face,
                                                           jint point_size, jint degree) {
    return getTrackKerning(face, point_size, degree);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_getKerning(JNIEnv *env, jobject thiz, jlong face,
                                                      jchar left, jchar right, jint mode) {
    long *kerning = getKerning(face, left, right, mode);
    jclass cls = env->FindClass("com/pvporbit/freetype/Kerning");
    jmethodID methodID = env->GetMethodID(cls, "<init>", "(JJ)V");
    jlong x = kerning[0];
    jlong y = kerning[1];
    jobject kerningObj = env->NewObject(cls, methodID, x, y);
    delete[] kerning; // Clean up the allocated memory
    return kerningObj;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_doneFace(JNIEnv *env, jobject thiz, jlong face) {
    return doneFace(face);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_referenceFace(JNIEnv *env, jobject thiz, jlong face) {
    return referenceFace(face);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_hasKerning(JNIEnv *env, jobject thiz, jlong face) {
    return hasKerning(face);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_getPostscriptName(JNIEnv *env, jobject thiz,
                                                             jlong face) {
    const char *postscriptName = getPostscriptName(face);
    return env->NewStringUTF(postscriptName ? postscriptName : "");
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_selectCharMap(JNIEnv *env, jobject thiz, jlong face,
                                                         jint encoding) {
    return selectCharMap(face, encoding);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_setCharMap(JNIEnv *env, jobject thiz, jlong face,
                                                      jlong char_map) {
    return setCharMap(face, char_map);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_faceCheckTrueTypePatents(JNIEnv *env, jobject thiz,
                                                                    jlong face) {
    return faceCheckTrueTypePatents(face);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_faceSetUnpatentedHinting(JNIEnv *env, jobject thiz,
                                                                    jlong face, jboolean value) {
    return faceSetUnpatentedHinting(face, value);
}

extern "C"
JNIEXPORT jlongArray JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_getFirstChar(JNIEnv *env, jobject thiz, jlong face) {
    unsigned long *firstChar = getFirstChar(face);
    jlongArray result = env->NewLongArray(2);
    if (result == nullptr) {
        return nullptr; // Out of memory error
    }
    jlong values[2] = {
            static_cast<jlong>(firstChar[0]),
            static_cast<jlong>(firstChar[1])
    };
    env->SetLongArrayRegion(result, 0, 2, values);
    delete[] firstChar; // Clean up the allocated memory
    return result;
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_getNextChar(JNIEnv *env, jobject thiz, jlong face,
                                                       jlong charcode) {
    return getNextChar(face, charcode);
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_getCharIndex(JNIEnv *env, jobject thiz, jlong face,
                                                        jint code) {
    return getCharIndex(face, code);
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_getNameIndex(JNIEnv *env, jobject thiz, jlong face,
                                                        jstring name) {
    const char *nameStr = env->GetStringUTFChars(name, nullptr);
    jint index = getNameIndex(face, nameStr);
    env->ReleaseStringUTFChars(name, nameStr);
    return index;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_getGlyphName(JNIEnv *env, jobject thiz, jlong face,
                                                        jint glyph_index) {
    char *name[100];
    FT_Get_Glyph_Name((FT_Face) face, glyph_index, name, 100);
    return env->NewStringUTF((const char *) name);
}

extern "C"
JNIEXPORT jshort JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_getFSTypeFlags(JNIEnv *env, jobject thiz, jlong face) {
    return getFSTypeFlags(face);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_selectSize(JNIEnv *env, jobject thiz, jlong face,
                                                      jint strike_index) {
    return selectSize(face, strike_index);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_loadChar(JNIEnv *env, jobject thiz, jlong face, jchar c,
                                                    jint flags) {
    return loadChar(face, c, flags);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_requestSize(JNIEnv *env, jobject thiz, jlong face,
                                                       jobject size_request) {
    jclass cls = env->GetObjectClass(size_request);
    jfieldID widthField = env->GetFieldID(cls, "width", "I");
    jfieldID heightField = env->GetFieldID(cls, "height", "I");
    jfieldID horizonResolutionField = env->GetFieldID(cls, "horiResolution", "I");
    jfieldID vertResolutionField = env->GetFieldID(cls, "vertResolution", "I");
    jfieldID typeField = env->GetFieldID(cls, "type", "I");
    jint width = env->GetIntField(size_request, widthField);
    jint height = env->GetIntField(size_request, heightField);
    jint horizonResolution = env->GetIntField(size_request, horizonResolutionField);
    jint vertResolution = env->GetIntField(size_request, vertResolutionField);
    jint type = env->GetIntField(size_request, typeField);
    return requestSize(face, width, height, horizonResolution, vertResolution, type);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_setPixelSizes(JNIEnv *env, jobject thiz, jlong face,
                                                         jint width, jint height) {
    return setPixelSizes(face, width, height);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_loadGlyph(JNIEnv *env, jobject thiz, jlong face,
                                                     jint glyph_index, jint load_flags) {
    return loadGlyph(face, glyph_index, load_flags);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_setCharSize(JNIEnv *env, jobject thiz, jlong face,
                                                       jint char_width, jint char_height,
                                                       jint horizon_resolution,
                                                       jint vert_resolution) {
    return setCharSize(face, char_width, char_height, horizon_resolution, vert_resolution);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_sizeGetMetrics(JNIEnv *env, jobject thiz, jlong size) {
    return sizeGetMetrics(size);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_sizeMetricsGetAscender(JNIEnv *env, jobject thiz,
                                                                  jlong size_metrics) {
    return sizeMetricsGetAscender(size_metrics);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_sizeMetricsGetDescender(JNIEnv *env, jobject thiz,
                                                                   jlong size_metrics) {
    return sizeMetricsGetDescender(size_metrics);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_sizeMetricsGetHeight(JNIEnv *env, jobject thiz,
                                                                jlong size_metrics) {
    return sizeMetricsGetHeight(size_metrics);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_sizeMetricsGetMaxAdvance(JNIEnv *env, jobject thiz,
                                                                    jlong size_metrics) {
    return sizeMetricsGetMaxAdvance(size_metrics);
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_sizeMetricsGetXPPEM(JNIEnv *env, jobject thiz,
                                                               jlong size_metrics) {
    return sizeMetricsGetXPPEM(size_metrics);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_sizeMetricsGetXScale(JNIEnv *env, jobject thiz,
                                                                jlong size_metrics) {
    return sizeMetricsGetXScale(size_metrics);
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_sizeMetricsGetYPPEM(JNIEnv *env, jobject thiz,
                                                               jlong size_metrics) {
    return sizeMetricsGetYPPEM(size_metrics);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_sizeMetricsGetYScale(JNIEnv *env, jobject thiz,
                                                                jlong size_metrics) {
    return sizeMetricsGetYScale(size_metrics);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_glyphSlotGetLinearHoriAdvance(JNIEnv *env, jobject thiz,
                                                                         jlong glyph_slot) {
    return glyphSlotGetLinearHoriAdvance(glyph_slot);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_glyphSlotGetLinearVertAdvance(JNIEnv *env, jobject thiz,
                                                                         jlong glyph_slot) {
    return glyphSlotGetLinearVertAdvance(glyph_slot);
}

extern "C"
JNIEXPORT jlongArray JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_glyphSlotGetAdvance(JNIEnv *env, jobject thiz,
                                                               jlong glyph_slot) {
    long *advance = glyphSlotGetAdvance(glyph_slot);
    jlongArray result = env->NewLongArray(2);
    if (result == nullptr) {
        return nullptr; // Out of memory error
    }
    jlong values[2] = {advance[0], advance[1]};
    env->SetLongArrayRegion(result, 0, 2, values);
    delete[] advance; // Clean up the allocated memory
    return result;
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_glyphSlotGetFormat(JNIEnv *env, jobject thiz,
                                                              jlong glyph_slot) {
    return glyphSlotGetFormat(glyph_slot);
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_glyphSlotGetBitmapLeft(JNIEnv *env, jobject thiz,
                                                                  jlong glyph_slot) {
    return glyphSlotGetBitmapLeft(glyph_slot);
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_glyphSlotGetBitmapTop(JNIEnv *env, jobject thiz,
                                                                 jlong glyph_slot) {
    return glyphSlotGetBitmapTop(glyph_slot);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_glyphSlotGetBitmap(JNIEnv *env, jobject thiz,
                                                              jlong glyph_slot) {
    return glyphSlotGetBitmap(glyph_slot);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_glyphSlotGetMetrics(JNIEnv *env, jobject thiz,
                                                               jlong glyph_slot) {
    return glyphSlotGetMetrics(glyph_slot);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_renderGlyph(JNIEnv *env, jobject thiz, jlong glyph_slot,
                                                       jint render_mode) {
    return renderGlyph(glyph_slot, render_mode);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_glyphMetricsGetWidth(JNIEnv *env, jobject thiz,
                                                                jlong glyph_metrics) {
    return glyphMetricsGetWidth(glyph_metrics);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_glyphMetricsGetHeight(JNIEnv *env, jobject thiz,
                                                                 jlong glyph_metrics) {
    return glyphMetricsGetHeight(glyph_metrics);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_glyphMetricsGetHoriAdvance(JNIEnv *env, jobject thiz,
                                                                      jlong glyph_metrics) {
    return glyphMetricsGetHoriAdvance(glyph_metrics);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_glyphMetricsGetVertAdvance(JNIEnv *env, jobject thiz,
                                                                      jlong glyph_metrics) {
    return glyphMetricsGetVertAdvance(glyph_metrics);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_glyphMetricsGetHoriBearingX(JNIEnv *env, jobject thiz,
                                                                       jlong glyph_metrics) {
    return glyphMetricsGetHoriBearingX(glyph_metrics);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_glyphMetricsGetHoriBearingY(JNIEnv *env, jobject thiz,
                                                                       jlong glyph_metrics) {
    return glyphMetricsGetHoriBearingY(glyph_metrics);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_glyphMetricsGetVertBearingX(JNIEnv *env, jobject thiz,
                                                                       jlong glyph_metrics) {
    return glyphMetricsGetVertBearingX(glyph_metrics);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_glyphMetricsGetVertBearingY(JNIEnv *env, jobject thiz,
                                                                       jlong glyph_metrics) {
    return glyphMetricsGetVertBearingY(glyph_metrics);
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_bitmapGetWidth(JNIEnv *env, jobject thiz, jlong bitmap) {
    return bitmapGetWidth(bitmap);
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_bitmapGetRows(JNIEnv *env, jobject thiz, jlong bitmap) {
    return bitmapGetRows(bitmap);
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_bitmapGetPitch(JNIEnv *env, jobject thiz, jlong bitmap) {
    return bitmapGetPitch(bitmap);
}

extern "C"
JNIEXPORT jshort JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_bitmapGetNumGrays(JNIEnv *env, jobject thiz,
                                                             jlong bitmap) {
    return bitmapGetNumGrays(bitmap);
}

extern "C"
JNIEXPORT jchar JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_bitmapGetPaletteMode(JNIEnv *env, jobject thiz,
                                                                jlong bitmap) {
    return bitmapGetPaletteMode(bitmap);
}

extern "C"
JNIEXPORT jchar JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_bitmapGetPixelMode(JNIEnv *env, jobject thiz,
                                                              jlong bitmap) {
    return bitmapGetPixelMode(bitmap);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_nativeBitmapGetBuffer(JNIEnv *env, jobject thiz,
                                                                 jlong bitmap) {
    auto buffer = bitmapGetBuffer(bitmap);
    return env->NewDirectByteBuffer(buffer.ptr, buffer.length);
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_getCharMapIndex(JNIEnv *env, jobject thiz,
                                                           jlong char_map) {
    return getCharMapIndex(char_map);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_newNativeBuffer(JNIEnv *env, jobject thiz, jint size) {
    return env->NewDirectByteBuffer((char *) malloc(size), size);
}

extern "C"
JNIEXPORT void JNICALL
Java_io_github_darriousliu_katex_freetype_FreeTypeAndroid_deleteNativeBuffer(JNIEnv *env, jobject thiz,
                                                        jobject buffer) {
    char *b = (char *) (buffer ? env->GetDirectBufferAddress(buffer) : nullptr);
    if (b) {
        free(b);
    }
}