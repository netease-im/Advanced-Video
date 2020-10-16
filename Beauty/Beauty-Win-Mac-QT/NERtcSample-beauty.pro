# ----------------------------------------------------
# This file is generated by the Qt Visual Studio Tools.
# ------------------------------------------------------
QT += core gui opengl
greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

CONFIG += c++11

TEMPLATE = app
TARGET = demo
DESTDIR = ./bin

HEADERS += ./mainwindow.h \
    $$PWD/invoker.h \
    ./Toast.h \
    ./videoWindow.h \
    ./videowidget.h \
    ./nrtc_engine.h
SOURCES += ./mainwindow.cpp \
    ./main.cpp \
    ./Toast.cpp \
    ./videowidget.cpp \
    ./videoWindow.cpp \
    ./nrtc_engine.cpp
macx {
    HEADERS += macxhelper.h
    SOURCES += macxhelper.mm
}
FORMS += ./mainwindow.ui \
    ./Toast.ui \
    ./videowindow.ui \
    ./videowidget.ui
RESOURCES += mainwindow.qrc


win32 {
    INCLUDEPATH += $$PWD/ \
                   $$PWD/nertc_sdk/api/ \
                   $$PWD/CNamaSDK/api/ \
                   $$PWD/CNamaSDK/auth/

    CONFIG(debug, debug|release) {
        LIBS += -L$$PWD/nertc_sdk/lib/x86 -lnertc_sdk \
                -L$$PWD/CNamaSDK/lib/x86 -lCNamaSDK

    } else {
        LIBS += -L$$PWD/nertc_sdk/lib/x86 -lnertc_sdk \
                -L$$PWD/CNamaSDK/lib/x86 -lCNamaSDK
    }

    LIBS+=-lopengl32 -lglu32

    QMAKE_CXXFLAGS += -wd4100 /utf-8
}

macx {
    INCLUDEPATH += $$PWD/ \
                   $$PWD/CNamaSDK/auth \
                   $$PWD/CNamaSDK/CNamaSDK.framework/Headers \
                   $$PWD/nertc_sdk/mac/NEFundation_Mac.framework/Headers \
                   $$PWD/nertc_sdk/mac/nertc_sdk_Mac.framework/Headers



    LIBS +=  -ObjC\
             -framework CoreVideo \
             -F$$PWD/nertc_sdk/mac -framework NEFundation_Mac -framework core_Mac -framework nertc_sdk_Mac \
             -F$$PWD/CNamaSDK -framework CNamaSDK \






    NEFUNDATION_FRAMEWORK.files = $$PWD/nertc_sdk/mac/NEFundation_Mac.framework
    NEFUNDATION_FRAMEWORK.path = /Contents/Frameworks

    CORE_FRAMEWORK.files = $$PWD/nertc_sdk/mac/core_Mac.framework
    CORE_FRAMEWORK.path = /Contents/Frameworks


    NERTC_SDK_FRAMEWORK.files = $$PWD/nertc_sdk/mac/nertc_sdk_Mac.framework
    NERTC_SDK_FRAMEWORK.path = /Contents/Frameworks


    FUSDK_FRAMEWORK.files = $$PWD/CNamaSDK/CNamaSDK.framework
    FUSDK_FRAMEWORK.path = /Contents/Frameworks




    FU_BUNDLE.files = $$PWD/CNamaSDK/Resources/graphics/face_beautification.bundle
    FU_BUNDLE.path = /Contents/MacOS

    QMAKE_BUNDLE_DATA += NEFUNDATION_FRAMEWORK \
                         CORE_FRAMEWORK \
                         NERTC_SDK_FRAMEWORK\
                         FUSDK_FRAMEWORK\
                         FU_BUNDLE
}

#MOC_DIR += .
#OBJECTS_DIR += debug
#UI_DIR += .
#RCC_DIR += .


