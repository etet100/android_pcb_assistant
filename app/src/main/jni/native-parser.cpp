//
// Created by a on 2017-06-10.
//

#include <jni.h>
#include <string>
#include <assert.h>

extern "C"

/*
 * Class:     bts_pcbassistant_parsing_BoardParser
 * Method:    Native
 * Signature: (Ljava/lang/String;)Lbts/pcbassistant/drawing/templates/Template;
 */
JNIEXPORT jobject JNICALL
Java_bts_pcbassistant_parsing_BoardParser_Native(JNIEnv *env, jobject parser, jstring input) {

    jboolean isCopy;
    jclass parserClass = env->GetObjectClass(parser);
    assert(parserClass != NULL);
    jmethodID getReadMethodID = NULL;
    int voidMethod = false;

//    jstring input,

    const char *s = env->GetStringUTFChars(input, &isCopy);
    std::string name(s);
    env->ReleaseStringUTFChars(input, s);

    //jmethodID methodId = env->GetMethodID(parserClass, "test", "()Lbts/pcbassistant/drawing/templates/Template;");
    //assert(methodId != NULL);
    //env->CallObjectMethod(parser_, methodId);

    if (!name.compare("wire")) {
        getReadMethodID = env->GetMethodID(parserClass, /*obf*/"readWire",
                                           "()Lbts/pcbassistant/drawing/templates/WireTemplate;");
        //retVal.add(readWire());
    } else if (!name.compare("smd")) {
        getReadMethodID = env->GetMethodID(parserClass, /*obf*/"readSmd",
                                           "()Lbts/pcbassistant/drawing/templates/SmdTemplate;");
        //retVal.add(readSmd());
    } else if (!name.compare("pad")) {
        getReadMethodID = env->GetMethodID(parserClass, /*obf*/"readPad",
                                           "()Lbts/pcbassistant/drawing/templates/PadTemplate;");
        //retVal.add(readPad());
    } else if (!name.compare("circle")) {
        getReadMethodID = env->GetMethodID(parserClass, /*obf*/"readCircle",
                                           "()Lbts/pcbassistant/drawing/templates/CircleTemplate;");
        //retVal.add(readCircle());
    } else if (!name.compare("rectangle")) {
        getReadMethodID = env->GetMethodID(parserClass, /*obf*/"readRectangle",
                                           "()Lbts/pcbassistant/drawing/templates/FilledRectangleTemplate;");
        //retVal.add(readRectangle());
    } else if (!name.compare("polygon")) {
        getReadMethodID = env->GetMethodID(parserClass, /*obf*/"parsePolygon",
                                           "()Lbts/pcbassistant/drawing/templates/PolygonTemplate;");
        //retVal.add(parsePolygon());
    } else if (!name.compare("text")) {
        getReadMethodID = env->GetMethodID(parserClass, /*obf*/"readText",
                                           "()Lbts/pcbassistant/drawing/templates/TextTemplate;");
        //retVal.add(readText());
    } else if (!name.compare("via")) {
        getReadMethodID = env->GetMethodID(parserClass, /*obf*/"readVia",
                                           "()Lbts/pcbassistant/drawing/templates/ViaTemplate;");
        //retVal.add(readVia());
    } else if (!name.compare("pin")) {
        getReadMethodID = env->GetMethodID(parserClass, /*obf*/"readPin",
                                           "()Lbts/pcbassistant/drawing/templates/PinTemplate;");
        //retVal.add(readPin());
    } else if (!name.compare("contactref")) {
        getReadMethodID = env->GetMethodID(parserClass, /*obf*/"readContactRef",
                                           "()");
        voidMethod = true;
    };

    jobject result = NULL;
    if (getReadMethodID != NULL) {
        if (voidMethod == true) {
            env->CallVoidMethod(parser, getReadMethodID);
        } else {
            result = env->CallObjectMethod(parser, getReadMethodID);
        }
    }

    env->DeleteLocalRef(parserClass);
    env->DeleteLocalRef(parser);

    return result;

}

extern "C"
JNIEXPORT jobject JNICALL
Java_bts_pcbassistant_parsing_SchematicParser_Native(JNIEnv *env, jobject parser, jstring input) {

    jboolean isCopy;
    jclass parserClass = env->GetObjectClass(parser);
    assert(parserClass != NULL);
    jmethodID getReadMethodID = NULL;

//    jstring input,

    const char *s = env->GetStringUTFChars(input, &isCopy);
    std::string name(s);
    env->ReleaseStringUTFChars(input, s);

    //jmethodID methodId = env->GetMethodID(parserClass, "test", "()Lbts/pcbassistant/drawing/templates/Template;");
    //assert(methodId != NULL);
    //env->CallObjectMethod(parser_, methodId);

    if (!name.compare("wire")) {
        getReadMethodID = env->GetMethodID(parserClass, /*obf*/"readWire",
                                           "()Lbts/pcbassistant/drawing/templates/WireTemplate;");
        //retVal.add(readWire());
    } else if (!name.compare("smd")) {
        getReadMethodID = env->GetMethodID(parserClass, /*obf*/"readSmd",
                                           "()Lbts/pcbassistant/drawing/templates/SmdTemplate;");
        //retVal.add(readSmd());
    } else if (!name.compare("pad")) {
        getReadMethodID = env->GetMethodID(parserClass, /*obf*/"readPad",
                                           "()Lbts/pcbassistant/drawing/templates/PadTemplate;");
        //retVal.add(readPad());
    } else if (!name.compare("circle")) {
        getReadMethodID = env->GetMethodID(parserClass, /*obf*/"readCircle",
                                           "()Lbts/pcbassistant/drawing/templates/CircleTemplate;");
        //retVal.add(readCircle());
    } else if (!name.compare("rectangle")) {
        getReadMethodID = env->GetMethodID(parserClass, /*obf*/"readRectangle",
                                           "()Lbts/pcbassistant/drawing/templates/FilledRectangleTemplate;");
        //retVal.add(readRectangle());
    } else if (!name.compare("polygon")) {
        getReadMethodID = env->GetMethodID(parserClass, /*obf*/"parsePolygon",
                                           "()Lbts/pcbassistant/drawing/templates/PolygonTemplate;");
        //retVal.add(parsePolygon());
    } else if (!name.compare("text")) {
        getReadMethodID = env->GetMethodID(parserClass, /*obf*/"readText",
                                           "()Lbts/pcbassistant/drawing/templates/TextTemplate;");
        //retVal.add(readText());
    } else if (!name.compare("pin")) {
        getReadMethodID = env->GetMethodID(parserClass, /*obf*/"readPin",
                                           "()Lbts/pcbassistant/drawing/templates/PinTemplate;");
        //retVal.add(readPin());
    };

    jobject result = NULL;
    if (getReadMethodID != NULL) {
        result = env->CallObjectMethod(parser, getReadMethodID);
    }

    env->DeleteLocalRef(parserClass);
    env->DeleteLocalRef(parser);

    return result;
}
