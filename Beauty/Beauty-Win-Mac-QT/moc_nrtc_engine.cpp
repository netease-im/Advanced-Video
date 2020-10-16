/****************************************************************************
** Meta object code from reading C++ file 'nrtc_engine.h'
**
** Created by: The Qt Meta Object Compiler version 67 (Qt 5.14.2)
**
** WARNING! All changes made in this file will be lost!
*****************************************************************************/

#include <memory>
#include "nrtc_engine.h"
#include <QtCore/qbytearray.h>
#include <QtCore/qmetatype.h>
#if !defined(Q_MOC_OUTPUT_REVISION)
#error "The header file 'nrtc_engine.h' doesn't include <QObject>."
#elif Q_MOC_OUTPUT_REVISION != 67
#error "This file was generated using the moc from 5.14.2. It"
#error "cannot be used with the include files from this version of Qt."
#error "(The moc has changed too much.)"
#endif

QT_BEGIN_MOC_NAMESPACE
QT_WARNING_PUSH
QT_WARNING_DISABLE_DEPRECATED
struct qt_meta_stringdata_NRTCEngine_t {
    QByteArrayData data[18];
    char stringdata0[180];
};
#define QT_MOC_LITERAL(idx, ofs, len) \
    Q_STATIC_BYTE_ARRAY_DATA_HEADER_INITIALIZER_WITH_OFFSET(len, \
    qptrdiff(offsetof(qt_meta_stringdata_NRTCEngine_t, stringdata0) + ofs \
        - idx * sizeof(QByteArrayData)) \
    )
static const qt_meta_stringdata_NRTCEngine_t qt_meta_stringdata_NRTCEngine = {
    {
QT_MOC_LITERAL(0, 0, 10), // "NRTCEngine"
QT_MOC_LITERAL(1, 11, 14), // "joiningChannel"
QT_MOC_LITERAL(2, 26, 0), // ""
QT_MOC_LITERAL(3, 27, 14), // "leavingChannel"
QT_MOC_LITERAL(4, 42, 12), // "videoStopped"
QT_MOC_LITERAL(5, 55, 3), // "uid"
QT_MOC_LITERAL(6, 59, 10), // "videoStart"
QT_MOC_LITERAL(7, 70, 11), // "max_profile"
QT_MOC_LITERAL(8, 82, 20), // "joinedChannelSuccess"
QT_MOC_LITERAL(9, 103, 10), // "userJoined"
QT_MOC_LITERAL(10, 114, 8), // "userLeft"
QT_MOC_LITERAL(11, 123, 14), // "renderFrameSig"
QT_MOC_LITERAL(12, 138, 5), // "ntype"
QT_MOC_LITERAL(13, 144, 4), // "data"
QT_MOC_LITERAL(14, 149, 8), // "uint32_t"
QT_MOC_LITERAL(15, 158, 5), // "width"
QT_MOC_LITERAL(16, 164, 6), // "height"
QT_MOC_LITERAL(17, 171, 8) // "frame_id"

    },
    "NRTCEngine\0joiningChannel\0\0leavingChannel\0"
    "videoStopped\0uid\0videoStart\0max_profile\0"
    "joinedChannelSuccess\0userJoined\0"
    "userLeft\0renderFrameSig\0ntype\0data\0"
    "uint32_t\0width\0height\0frame_id"
};
#undef QT_MOC_LITERAL

static const uint qt_meta_data_NRTCEngine[] = {

 // content:
       8,       // revision
       0,       // classname
       0,    0, // classinfo
       8,   14, // methods
       0,    0, // properties
       0,    0, // enums/sets
       0,    0, // constructors
       0,       // flags
       8,       // signalCount

 // signals: name, argc, parameters, tag, flags
       1,    0,   54,    2, 0x06 /* Public */,
       3,    0,   55,    2, 0x06 /* Public */,
       4,    1,   56,    2, 0x06 /* Public */,
       6,    2,   59,    2, 0x06 /* Public */,
       8,    0,   64,    2, 0x06 /* Public */,
       9,    1,   65,    2, 0x06 /* Public */,
      10,    1,   68,    2, 0x06 /* Public */,
      11,    5,   71,    2, 0x06 /* Public */,

 // signals: parameters
    QMetaType::Void,
    QMetaType::Void,
    QMetaType::Void, QMetaType::ULongLong,    5,
    QMetaType::Void, QMetaType::ULongLong, QMetaType::Int,    5,    7,
    QMetaType::Void,
    QMetaType::Void, QMetaType::ULongLong,    5,
    QMetaType::Void, QMetaType::ULongLong,    5,
    QMetaType::Void, QMetaType::Int, QMetaType::VoidStar, 0x80000000 | 14, 0x80000000 | 14, QMetaType::Int,   12,   13,   15,   16,   17,

       0        // eod
};

void NRTCEngine::qt_static_metacall(QObject *_o, QMetaObject::Call _c, int _id, void **_a)
{
    if (_c == QMetaObject::InvokeMetaMethod) {
        auto *_t = static_cast<NRTCEngine *>(_o);
        Q_UNUSED(_t)
        switch (_id) {
        case 0: _t->joiningChannel(); break;
        case 1: _t->leavingChannel(); break;
        case 2: _t->videoStopped((*reinterpret_cast< unsigned long long(*)>(_a[1]))); break;
        case 3: _t->videoStart((*reinterpret_cast< unsigned long long(*)>(_a[1])),(*reinterpret_cast< int(*)>(_a[2]))); break;
        case 4: _t->joinedChannelSuccess(); break;
        case 5: _t->userJoined((*reinterpret_cast< quint64(*)>(_a[1]))); break;
        case 6: _t->userLeft((*reinterpret_cast< quint64(*)>(_a[1]))); break;
        case 7: _t->renderFrameSig((*reinterpret_cast< int(*)>(_a[1])),(*reinterpret_cast< void*(*)>(_a[2])),(*reinterpret_cast< uint32_t(*)>(_a[3])),(*reinterpret_cast< uint32_t(*)>(_a[4])),(*reinterpret_cast< int(*)>(_a[5]))); break;
        default: ;
        }
    } else if (_c == QMetaObject::IndexOfMethod) {
        int *result = reinterpret_cast<int *>(_a[0]);
        {
            using _t = void (NRTCEngine::*)();
            if (*reinterpret_cast<_t *>(_a[1]) == static_cast<_t>(&NRTCEngine::joiningChannel)) {
                *result = 0;
                return;
            }
        }
        {
            using _t = void (NRTCEngine::*)();
            if (*reinterpret_cast<_t *>(_a[1]) == static_cast<_t>(&NRTCEngine::leavingChannel)) {
                *result = 1;
                return;
            }
        }
        {
            using _t = void (NRTCEngine::*)(unsigned long long );
            if (*reinterpret_cast<_t *>(_a[1]) == static_cast<_t>(&NRTCEngine::videoStopped)) {
                *result = 2;
                return;
            }
        }
        {
            using _t = void (NRTCEngine::*)(unsigned long long , int );
            if (*reinterpret_cast<_t *>(_a[1]) == static_cast<_t>(&NRTCEngine::videoStart)) {
                *result = 3;
                return;
            }
        }
        {
            using _t = void (NRTCEngine::*)();
            if (*reinterpret_cast<_t *>(_a[1]) == static_cast<_t>(&NRTCEngine::joinedChannelSuccess)) {
                *result = 4;
                return;
            }
        }
        {
            using _t = void (NRTCEngine::*)(quint64 );
            if (*reinterpret_cast<_t *>(_a[1]) == static_cast<_t>(&NRTCEngine::userJoined)) {
                *result = 5;
                return;
            }
        }
        {
            using _t = void (NRTCEngine::*)(quint64 );
            if (*reinterpret_cast<_t *>(_a[1]) == static_cast<_t>(&NRTCEngine::userLeft)) {
                *result = 6;
                return;
            }
        }
        {
            using _t = void (NRTCEngine::*)(int , void * , uint32_t , uint32_t , int );
            if (*reinterpret_cast<_t *>(_a[1]) == static_cast<_t>(&NRTCEngine::renderFrameSig)) {
                *result = 7;
                return;
            }
        }
    }
}

QT_INIT_METAOBJECT const QMetaObject NRTCEngine::staticMetaObject = { {
    QMetaObject::SuperData::link<QObject::staticMetaObject>(),
    qt_meta_stringdata_NRTCEngine.data,
    qt_meta_data_NRTCEngine,
    qt_static_metacall,
    nullptr,
    nullptr
} };


const QMetaObject *NRTCEngine::metaObject() const
{
    return QObject::d_ptr->metaObject ? QObject::d_ptr->dynamicMetaObject() : &staticMetaObject;
}

void *NRTCEngine::qt_metacast(const char *_clname)
{
    if (!_clname) return nullptr;
    if (!strcmp(_clname, qt_meta_stringdata_NRTCEngine.stringdata0))
        return static_cast<void*>(this);
    return QObject::qt_metacast(_clname);
}

int NRTCEngine::qt_metacall(QMetaObject::Call _c, int _id, void **_a)
{
    _id = QObject::qt_metacall(_c, _id, _a);
    if (_id < 0)
        return _id;
    if (_c == QMetaObject::InvokeMetaMethod) {
        if (_id < 8)
            qt_static_metacall(this, _c, _id, _a);
        _id -= 8;
    } else if (_c == QMetaObject::RegisterMethodArgumentMetaType) {
        if (_id < 8)
            *reinterpret_cast<int*>(_a[0]) = -1;
        _id -= 8;
    }
    return _id;
}

// SIGNAL 0
void NRTCEngine::joiningChannel()
{
    QMetaObject::activate(this, &staticMetaObject, 0, nullptr);
}

// SIGNAL 1
void NRTCEngine::leavingChannel()
{
    QMetaObject::activate(this, &staticMetaObject, 1, nullptr);
}

// SIGNAL 2
void NRTCEngine::videoStopped(unsigned long long _t1)
{
    void *_a[] = { nullptr, const_cast<void*>(reinterpret_cast<const void*>(std::addressof(_t1))) };
    QMetaObject::activate(this, &staticMetaObject, 2, _a);
}

// SIGNAL 3
void NRTCEngine::videoStart(unsigned long long _t1, int _t2)
{
    void *_a[] = { nullptr, const_cast<void*>(reinterpret_cast<const void*>(std::addressof(_t1))), const_cast<void*>(reinterpret_cast<const void*>(std::addressof(_t2))) };
    QMetaObject::activate(this, &staticMetaObject, 3, _a);
}

// SIGNAL 4
void NRTCEngine::joinedChannelSuccess()
{
    QMetaObject::activate(this, &staticMetaObject, 4, nullptr);
}

// SIGNAL 5
void NRTCEngine::userJoined(quint64 _t1)
{
    void *_a[] = { nullptr, const_cast<void*>(reinterpret_cast<const void*>(std::addressof(_t1))) };
    QMetaObject::activate(this, &staticMetaObject, 5, _a);
}

// SIGNAL 6
void NRTCEngine::userLeft(quint64 _t1)
{
    void *_a[] = { nullptr, const_cast<void*>(reinterpret_cast<const void*>(std::addressof(_t1))) };
    QMetaObject::activate(this, &staticMetaObject, 6, _a);
}

// SIGNAL 7
void NRTCEngine::renderFrameSig(int _t1, void * _t2, uint32_t _t3, uint32_t _t4, int _t5)
{
    void *_a[] = { nullptr, const_cast<void*>(reinterpret_cast<const void*>(std::addressof(_t1))), const_cast<void*>(reinterpret_cast<const void*>(std::addressof(_t2))), const_cast<void*>(reinterpret_cast<const void*>(std::addressof(_t3))), const_cast<void*>(reinterpret_cast<const void*>(std::addressof(_t4))), const_cast<void*>(reinterpret_cast<const void*>(std::addressof(_t5))) };
    QMetaObject::activate(this, &staticMetaObject, 7, _a);
}
QT_WARNING_POP
QT_END_MOC_NAMESPACE
