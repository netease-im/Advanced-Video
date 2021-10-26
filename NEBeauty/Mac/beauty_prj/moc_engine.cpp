/****************************************************************************
** Meta object code from reading C++ file 'engine.h'
**
** Created by: The Qt Meta Object Compiler version 68 (Qt 6.2.0)
**
** WARNING! All changes made in this file will be lost!
*****************************************************************************/

#include <memory>
#include "source/include/engine.h"
#include <QtCore/qbytearray.h>
#include <QtCore/qmetatype.h>
#if !defined(Q_MOC_OUTPUT_REVISION)
#error "The header file 'engine.h' doesn't include <QObject>."
#elif Q_MOC_OUTPUT_REVISION != 68
#error "This file was generated using the moc from 6.2.0. It"
#error "cannot be used with the include files from this version of Qt."
#error "(The moc has changed too much.)"
#endif

QT_BEGIN_MOC_NAMESPACE
QT_WARNING_PUSH
QT_WARNING_DISABLE_DEPRECATED
struct qt_meta_stringdata_Engine_t {
    const uint offsetsAndSize[20];
    char stringdata0[91];
};
#define QT_MOC_LITERAL(ofs, len) \
    uint(offsetof(qt_meta_stringdata_Engine_t, stringdata0) + ofs), len 
static const qt_meta_stringdata_Engine_t qt_meta_stringdata_Engine = {
    {
QT_MOC_LITERAL(0, 6), // "Engine"
QT_MOC_LITERAL(7, 14), // "sigJoinChannel"
QT_MOC_LITERAL(22, 0), // ""
QT_MOC_LITERAL(23, 6), // "result"
QT_MOC_LITERAL(30, 13), // "sigUserJoined"
QT_MOC_LITERAL(44, 3), // "uid"
QT_MOC_LITERAL(48, 4), // "name"
QT_MOC_LITERAL(53, 17), // "sigUserVideoStart"
QT_MOC_LITERAL(71, 7), // "profile"
QT_MOC_LITERAL(79, 11) // "sigUserLeft"

    },
    "Engine\0sigJoinChannel\0\0result\0"
    "sigUserJoined\0uid\0name\0sigUserVideoStart\0"
    "profile\0sigUserLeft"
};
#undef QT_MOC_LITERAL

static const uint qt_meta_data_Engine[] = {

 // content:
      10,       // revision
       0,       // classname
       0,    0, // classinfo
       4,   14, // methods
       0,    0, // properties
       0,    0, // enums/sets
       0,    0, // constructors
       0,       // flags
       4,       // signalCount

 // signals: name, argc, parameters, tag, flags, initial metatype offsets
       1,    1,   38,    2, 0x06,    1 /* Public */,
       4,    2,   41,    2, 0x06,    3 /* Public */,
       7,    2,   46,    2, 0x06,    6 /* Public */,
       9,    2,   51,    2, 0x06,    9 /* Public */,

 // signals: parameters
    QMetaType::Void, QMetaType::Int,    3,
    QMetaType::Void, QMetaType::ULongLong, QMetaType::QString,    5,    6,
    QMetaType::Void, QMetaType::ULongLong, QMetaType::Int,    5,    8,
    QMetaType::Void, QMetaType::ULongLong, QMetaType::Int,    5,    3,

       0        // eod
};

void Engine::qt_static_metacall(QObject *_o, QMetaObject::Call _c, int _id, void **_a)
{
    if (_c == QMetaObject::InvokeMetaMethod) {
        auto *_t = static_cast<Engine *>(_o);
        (void)_t;
        switch (_id) {
        case 0: _t->sigJoinChannel((*reinterpret_cast< const int(*)>(_a[1]))); break;
        case 1: _t->sigUserJoined((*reinterpret_cast< const quint64(*)>(_a[1])),(*reinterpret_cast< const QString(*)>(_a[2]))); break;
        case 2: _t->sigUserVideoStart((*reinterpret_cast< const quint64(*)>(_a[1])),(*reinterpret_cast< const int(*)>(_a[2]))); break;
        case 3: _t->sigUserLeft((*reinterpret_cast< const quint64(*)>(_a[1])),(*reinterpret_cast< const int(*)>(_a[2]))); break;
        default: ;
        }
    } else if (_c == QMetaObject::IndexOfMethod) {
        int *result = reinterpret_cast<int *>(_a[0]);
        {
            using _t = void (Engine::*)(const int & );
            if (*reinterpret_cast<_t *>(_a[1]) == static_cast<_t>(&Engine::sigJoinChannel)) {
                *result = 0;
                return;
            }
        }
        {
            using _t = void (Engine::*)(const quint64 & , const QString & );
            if (*reinterpret_cast<_t *>(_a[1]) == static_cast<_t>(&Engine::sigUserJoined)) {
                *result = 1;
                return;
            }
        }
        {
            using _t = void (Engine::*)(const quint64 & , const int & );
            if (*reinterpret_cast<_t *>(_a[1]) == static_cast<_t>(&Engine::sigUserVideoStart)) {
                *result = 2;
                return;
            }
        }
        {
            using _t = void (Engine::*)(const quint64 & , const int & );
            if (*reinterpret_cast<_t *>(_a[1]) == static_cast<_t>(&Engine::sigUserLeft)) {
                *result = 3;
                return;
            }
        }
    }
}

const QMetaObject Engine::staticMetaObject = { {
    QMetaObject::SuperData::link<QObject::staticMetaObject>(),
    qt_meta_stringdata_Engine.offsetsAndSize,
    qt_meta_data_Engine,
    qt_static_metacall,
    nullptr,
qt_incomplete_metaTypeArray<qt_meta_stringdata_Engine_t
, QtPrivate::TypeAndForceComplete<Engine, std::true_type>, QtPrivate::TypeAndForceComplete<void, std::false_type>, QtPrivate::TypeAndForceComplete<const int &, std::false_type>, QtPrivate::TypeAndForceComplete<void, std::false_type>, QtPrivate::TypeAndForceComplete<const quint64 &, std::false_type>, QtPrivate::TypeAndForceComplete<const QString &, std::false_type>, QtPrivate::TypeAndForceComplete<void, std::false_type>, QtPrivate::TypeAndForceComplete<const quint64 &, std::false_type>, QtPrivate::TypeAndForceComplete<const int &, std::false_type>, QtPrivate::TypeAndForceComplete<void, std::false_type>, QtPrivate::TypeAndForceComplete<const quint64 &, std::false_type>, QtPrivate::TypeAndForceComplete<const int &, std::false_type>



>,
    nullptr
} };


const QMetaObject *Engine::metaObject() const
{
    return QObject::d_ptr->metaObject ? QObject::d_ptr->dynamicMetaObject() : &staticMetaObject;
}

void *Engine::qt_metacast(const char *_clname)
{
    if (!_clname) return nullptr;
    if (!strcmp(_clname, qt_meta_stringdata_Engine.stringdata0))
        return static_cast<void*>(this);
    if (!strcmp(_clname, "nertc::IRtcEngineEventHandlerEx"))
        return static_cast< nertc::IRtcEngineEventHandlerEx*>(this);
    return QObject::qt_metacast(_clname);
}

int Engine::qt_metacall(QMetaObject::Call _c, int _id, void **_a)
{
    _id = QObject::qt_metacall(_c, _id, _a);
    if (_id < 0)
        return _id;
    if (_c == QMetaObject::InvokeMetaMethod) {
        if (_id < 4)
            qt_static_metacall(this, _c, _id, _a);
        _id -= 4;
    } else if (_c == QMetaObject::RegisterMethodArgumentMetaType) {
        if (_id < 4)
            *reinterpret_cast<QMetaType *>(_a[0]) = QMetaType();
        _id -= 4;
    }
    return _id;
}

// SIGNAL 0
void Engine::sigJoinChannel(const int & _t1)
{
    void *_a[] = { nullptr, const_cast<void*>(reinterpret_cast<const void*>(std::addressof(_t1))) };
    QMetaObject::activate(this, &staticMetaObject, 0, _a);
}

// SIGNAL 1
void Engine::sigUserJoined(const quint64 & _t1, const QString & _t2)
{
    void *_a[] = { nullptr, const_cast<void*>(reinterpret_cast<const void*>(std::addressof(_t1))), const_cast<void*>(reinterpret_cast<const void*>(std::addressof(_t2))) };
    QMetaObject::activate(this, &staticMetaObject, 1, _a);
}

// SIGNAL 2
void Engine::sigUserVideoStart(const quint64 & _t1, const int & _t2)
{
    void *_a[] = { nullptr, const_cast<void*>(reinterpret_cast<const void*>(std::addressof(_t1))), const_cast<void*>(reinterpret_cast<const void*>(std::addressof(_t2))) };
    QMetaObject::activate(this, &staticMetaObject, 2, _a);
}

// SIGNAL 3
void Engine::sigUserLeft(const quint64 & _t1, const int & _t2)
{
    void *_a[] = { nullptr, const_cast<void*>(reinterpret_cast<const void*>(std::addressof(_t1))), const_cast<void*>(reinterpret_cast<const void*>(std::addressof(_t2))) };
    QMetaObject::activate(this, &staticMetaObject, 3, _a);
}
QT_WARNING_POP
QT_END_MOC_NAMESPACE
