QT       += core gui

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets 

CONFIG += c++11

DEFINES += QT_DEPRECATED_WARNINGS

TEMPLATE = app
TARGET = 1v1_beauty
DESTDIR = $$PWD/bin

win32{

    INCLUDEPATH += $$PWD/ \
                   $$PWD/3rdparty/nertc/api/ \
                   $$PWD/3rdparty/jsoncpp/include \
                   $$PWD/source/include \
	
	CONFIG(debug, debug|release) {
        LIBS += -L$$PWD/3rdparty/nertc/lib/x86 -lnertc_sdk \
                -L$$PWD/3rdparty/jsoncpp/lib/debug -ljsoncpp
    }else {
		LIBS += -L$$PWD/3rdparty/nertc/lib/x86 -lnertc_sdk \
                -L$$PWD/3rdparty/jsoncpp/lib/release -ljsoncpp
	}

    QMAKE_CXXFLAGS += -wd4100 /utf-8
    QMAKE_LFLAGS_RELEASE += /MAP
    QMAKE_CFLAGS_RELEASE += /Zi
    QMAKE_LFLAGS_RELEASE += /debug /opt:ref
	
}

macx{
	INCLUDEPATH += $$PWD/ \
                   $$PWD/3rdparty/nertc/mac/NEFundation_Mac.framework/Headers \
                   $$PWD/3rdparty/nertc/mac/nertc_sdk_Mac.framework/Headers \
                   $$PWD/3rdparty/jsoncpp/include/mac \
                   $$PWD/source/include
				   
	LIBS +=  -ObjC\
             -framework CoreVideo \
             -F$$PWD/3rdparty/nertc/mac -framework NEFundation_Mac -framework nertc_sdk_Mac \
             -L$$$$PWD/3rdparty/jsoncpp/lib/mac -ljsoncpp
			 
	NEFUNDATION_FRAMEWORK.files = $$PWD/3rdparty/nertc/mac/NEFundation_Mac.framework
    NEFUNDATION_FRAMEWORK.path = /Contents/Frameworks
	
	NERTC_SDK_FRAMEWORK.files = $$PWD/3rdparty/nertc/mac/nertc_sdk_Mac.framework
    NERTC_SDK_FRAMEWORK.path = /Contents/Frameworks
	
	QMAKE_BUNDLE_DATA += NEFUNDATION_FRAMEWORK \
                         NERTC_SDK_FRAMEWORK
}

HEADERS += \
    source/include/beauty_item_widget.h \
    source/include/beauty_tabwidget.h \
    source/include/call_widget.h \
    source/include/engine.h \
    source/include/face_beauty_widget.h \
    source/include/filter_beauty_widget.h \
    source/include/item_sticker_widget.h \
    source/include/join_widget.h \
    source/include/other_beauty_widget.h \
    source/include/room_button.h \
    source/include/skin_beauty_widget.h \
    source/include/video_widget.h

SOURCES += \
    main.cpp \
    source/src/beauty_item_widget.cpp \
    source/src/beauty_tabwidget.cpp \
    source/src/call_widget.cpp \
    source/src/engine.cpp \
    source/src/face_beauty_widget.cpp \
    source/src/filter_beauty_widget.cpp \
    source/src/item_sticker_widget.cpp \
    source/src/join_widget.cpp \
    source/src/other_beauty_widget.cpp \
    source/src/room_button.cpp \
    source/src/skin_beauty_widget.cpp \
    source/src/video_widget.cpp


OBJECTIVE_SOURCES += \
    source/src/engine_beauty_mac.mm

OBJECTIVE_HEADERS += \
    source/include/engine_beauty_mac.h

BeautyFiles.files = data
BeautyFiles.path = Contents/RESOURCES
QMAKE_BUNDLE_DATA += BeautyFiles

LIBS += -framework Foundation

RESOURCES += \
    source/resource/image.qrc \
    source/resource/image/background.png \
    source/resource/image/beaut-on.png \
    source/resource/image/beauty-off.png \
    source/resource/image/btn_show_device_down_normal.png \
    source/resource/image/btn_show_device_normal.png \
    source/resource/image/cancel_call.png \
    source/resource/image/check.png \
    source/resource/image/clear.png \
    source/resource/image/data.png \
    source/resource/image/device_selector_normal.png \
    source/resource/image/device_selector_pushed.png \
    source/resource/image/networkquality_bad.svg \
    source/resource/image/networkquality_general.svg \
    source/resource/image/networkquality_good.svg \
    source/resource/image/networkquality_unknown.svg \
    source/resource/image/normal.png \
    source/resource/image/radio-check.png \
    source/resource/image/radio-uncheck.png \
    source/resource/image/right_white.svg \
    source/resource/image/setting.png \
    source/resource/image/smlie-active.svg \
    source/resource/image/thumb-up-active.svg \
    source/resource/image/thumb-up.svg \
    source/resource/image/thumbs-down-active.svg \
    source/resource/image/thumbs-down.svg \
    source/resource/image/triangle-combox.png \
    source/resource/image/triangle-down.png \
    source/resource/image/video-off.png \
    source/resource/image/video-on.png \
    source/resource/image/voice-off.png \
    source/resource/image/voice-on.png \
    source/resource/image/zoomIn.png \
    source/resource/image/zoomOut.png

