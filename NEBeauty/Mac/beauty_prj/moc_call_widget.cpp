/****************************************************************************
** Meta object code from reading C++ file 'call_widget.h'
**
** Created by: The Qt Meta Object Compiler version 68 (Qt 6.2.0)
**
** WARNING! All changes made in this file will be lost!
*****************************************************************************/

#include <memory>
#include "source/include/call_widget.h"
#include <QtCore/qbytearray.h>
#include <QtCore/qmetatype.h>
#if !defined(Q_MOC_OUTPUT_REVISION)
#error "The header file 'call_widget.h' doesn't include <QObject>."
#elif Q_MOC_OUTPUT_REVISION != 68
#error "This file was generated using the moc from 6.2.0. It"
#error "cannot be used with the include files from this version of Qt."
#error "(The moc has changed too much.)"
#endif

QT_BEGIN_MOC_NAMESPACE
QT_WARNING_PUSH
QT_WARNING_DISABLE_DEPRECATED
struct qt_meta_stringdata_CallWidget_t {
    const uint offsetsAndSize[52];
    char stringdata0[270];
};
#define QT_MOC_LITERAL(ofs, len) \
    uint(offsetof(qt_meta_stringdata_CallWidget_t, stringdata0) + ofs), len 
static const qt_meta_stringdata_CallWidget_t qt_meta_stringdata_CallWidget = {
    {
QT_MOC_LITERAL(0, 10), // "CallWidget"
QT_MOC_LITERAL(11, 13), // "sigEnableNama"
QT_MOC_LITERAL(25, 0), // ""
QT_MOC_LITERAL(26, 6), // "enable"
QT_MOC_LITERAL(33, 13), // "onJoinChannel"
QT_MOC_LITERAL(47, 5), // "reson"
QT_MOC_LITERAL(53, 10), // "onUserJoin"
QT_MOC_LITERAL(64, 3), // "uid"
QT_MOC_LITERAL(68, 4), // "name"
QT_MOC_LITERAL(73, 16), // "onUserVideoStart"
QT_MOC_LITERAL(90, 7), // "profile"
QT_MOC_LITERAL(98, 10), // "onUserLeft"
QT_MOC_LITERAL(109, 6), // "result"
QT_MOC_LITERAL(116, 15), // "onBeautyChanged"
QT_MOC_LITERAL(132, 2), // "id"
QT_MOC_LITERAL(135, 3), // "val"
QT_MOC_LITERAL(139, 15), // "onFilterChanged"
QT_MOC_LITERAL(155, 4), // "path"
QT_MOC_LITERAL(160, 20), // "onItemStickerChanged"
QT_MOC_LITERAL(181, 11), // "std::string"
QT_MOC_LITERAL(193, 3), // "str"
QT_MOC_LITERAL(197, 13), // "onStartBeauty"
QT_MOC_LITERAL(211, 13), // "start_enabled"
QT_MOC_LITERAL(225, 14), // "onBeautyEnable"
QT_MOC_LITERAL(240, 14), // "onBeautyMirror"
QT_MOC_LITERAL(255, 14) // "onBeautyMakeup"

    },
    "CallWidget\0sigEnableNama\0\0enable\0"
    "onJoinChannel\0reson\0onUserJoin\0uid\0"
    "name\0onUserVideoStart\0profile\0onUserLeft\0"
    "result\0onBeautyChanged\0id\0val\0"
    "onFilterChanged\0path\0onItemStickerChanged\0"
    "std::string\0str\0onStartBeauty\0"
    "start_enabled\0onBeautyEnable\0"
    "onBeautyMirror\0onBeautyMakeup"
};
#undef QT_MOC_LITERAL

static const uint qt_meta_data_CallWidget[] = {

 // content:
      10,       // revision
       0,       // classname
       0,    0, // classinfo
      12,   14, // methods
       0,    0, // properties
       0,    0, // enums/sets
       0,    0, // constructors
       0,       // flags
       1,       // signalCount

 // signals: name, argc, parameters, tag, flags, initial metatype offsets
       1,    1,   86,    2, 0x06,    1 /* Public */,

 // slots: name, argc, parameters, tag, flags, initial metatype offsets
       4,    1,   89,    2, 0x08,    3 /* Private */,
       6,    2,   92,    2, 0x08,    5 /* Private */,
       9,    2,   97,    2, 0x08,    8 /* Private */,
      11,    2,  102,    2, 0x08,   11 /* Private */,
      13,    2,  107,    2, 0x08,   14 /* Private */,
      16,    2,  112,    2, 0x08,   17 /* Private */,
      18,    1,  117,    2, 0x08,   20 /* Private */,
      21,    1,  120,    2, 0x08,   22 /* Private */,
      23,    1,  123,    2, 0x08,   24 /* Private */,
      24,    1,  126,    2, 0x08,   26 /* Private */,
      25,    1,  129,    2, 0x08,   28 /* Private */,

 // signals: parameters
    QMetaType::Void, QMetaType::Bool,    3,

 // slots: parameters
    QMetaType::Void, QMetaType::Int,    5,
    QMetaType::Void, QMetaType::ULongLong, QMetaType::QString,    7,    8,
    QMetaType::Void, QMetaType::ULongLong, QMetaType::Int,    7,   10,
    QMetaType::Void, QMetaType::ULongLong, QMetaType::Int,    7,   12,
    QMetaType::Void, QMetaType::Int, QMetaType::Int,   14,   15,
    QMetaType::Void, QMetaType::QString, QMetaType::Int,   17,   15,
    QMetaType::Void, 0x80000000 | 19,   20,
    QMetaType::Void, QMetaType::Bool,   22,
    QMetaType::Void, QMetaType::Bool,    3,
    QMetaType::Void, QMetaType::Bool,    3,
    QMetaType::Void, QMetaType::Bool,    3,

       0        // eod
};

void CallWidget::qt_static_metacall(QObject *_o, QMetaObject::Call _c, int _id, void **_a)
{
    if (_c == QMetaObject::InvokeMetaMethod) {
        auto *_t = static_cast<CallWidget *>(_o);
        (void)_t;
        switch (_id) {
        case 0: _t->sigEnableNama((*reinterpret_cast< const bool(*)>(_a[1]))); break;
        case 1: _t->onJoinChannel((*reinterpret_cast< const int(*)>(_a[1]))); break;
        case 2: _t->onUserJoin((*reinterpret_cast< const quint64(*)>(_a[1])),(*reinterpret_cast< const QString(*)>(_a[2]))); break;
        case 3: _t->onUserVideoStart((*reinterpret_cast< const quint64(*)>(_a[1])),(*reinterpret_cast< const int(*)>(_a[2]))); break;
        case 4: _t->onUserLeft((*reinterpret_cast< const quint64(*)>(_a[1])),(*reinterpret_cast< const int(*)>(_a[2]))); break;
        case 5: _t->onBeautyChanged((*reinterpret_cast< const int(*)>(_a[1])),(*reinterpret_cast< const int(*)>(_a[2]))); break;
        case 6: _t->onFilterChanged((*reinterpret_cast< const QString(*)>(_a[1])),(*reinterpret_cast< const int(*)>(_a[2]))); break;
        case 7: _t->onItemStickerChanged((*reinterpret_cast< const std::string(*)>(_a[1]))); break;
        case 8: _t->onStartBeauty((*reinterpret_cast< const bool(*)>(_a[1]))); break;
        case 9: _t->onBeautyEnable((*reinterpret_cast< const bool(*)>(_a[1]))); break;
        case 10: _t->onBeautyMirror((*reinterpret_cast< const bool(*)>(_a[1]))); break;
        case 11: _t->onBeautyMakeup((*reinterpret_cast< const bool(*)>(_a[1]))); break;
        default: ;
        }
    } else if (_c == QMetaObject::IndexOfMethod) {
        int *result = reinterpret_cast<int *>(_a[0]);
        {
            using _t = void (CallWidget::*)(const bool & );
            if (*reinterpret_cast<_t *>(_a[1]) == static_cast<_t>(&CallWidget::sigEnableNama)) {
                *result = 0;
                return;
            }
        }
    }
}

const QMetaObject CallWidget::staticMetaObject = { {
    QMetaObject::SuperData::link<QWidget::staticMetaObject>(),
    qt_meta_stringdata_CallWidget.offsetsAndSize,
    qt_meta_data_CallWidget,
    qt_static_metacall,
    nullptr,
qt_incomplete_metaTypeArray<qt_meta_stringdata_CallWidget_t
, QtPrivate::TypeAndForceComplete<CallWidget, std::true_type>, QtPrivate::TypeAndForceComplete<void, std::false_type>, QtPrivate::TypeAndForceComplete<const bool &, std::false_type>
, QtPrivate::TypeAndForceComplete<void, std::false_type>, QtPrivate::TypeAndForceComplete<const int &, std::false_type>, QtPrivate::TypeAndForceComplete<void, std::false_type>, QtPrivate::TypeAndForceComplete<const quint64 &, std::false_type>, QtPrivate::TypeAndForceComplete<const QString &, std::false_type>, QtPrivate::TypeAndForceComplete<void, std::false_type>, QtPrivate::TypeAndForceComplete<const quint64 &, std::false_type>, QtPrivate::TypeAndForceComplete<const int &, std::false_type>, QtPrivate::TypeAndForceComplete<void, std::false_type>, QtPrivate::TypeAndForceComplete<const quint64 &, std::false_type>, QtPrivate::TypeAndForceComplete<const int &, std::false_type>, QtPrivate::TypeAndForceComplete<void, std::false_type>, QtPrivate::TypeAndForceComplete<const int &, std::false_type>, QtPrivate::TypeAndForceComplete<const int &, std::false_type>, QtPrivate::TypeAndForceComplete<void, std::false_type>, QtPrivate::TypeAndForceComplete<const QString &, std::false_type>, QtPrivate::TypeAndForceComplete<const int &, std::false_type>, QtPrivate::TypeAndForceComplete<void, std::false_type>, QtPrivate::TypeAndForceComplete<const std::string &, std::false_type>, QtPrivate::TypeAndForceComplete<void, std::false_type>, QtPrivate::TypeAndForceComplete<const bool &, std::false_type>, QtPrivate::TypeAndForceComplete<void, std::false_type>, QtPrivate::TypeAndForceComplete<const bool &, std::false_type>, QtPrivate::TypeAndForceComplete<void, std::false_type>, QtPrivate::TypeAndForceComplete<const bool &, std::false_type>, QtPrivate::TypeAndForceComplete<void, std::false_type>, QtPrivate::TypeAndForceComplete<const bool &, std::false_type>


>,
    nullptr
} };


const QMetaObject *CallWidget::metaObject() const
{
    return QObject::d_ptr->metaObject ? QObject::d_ptr->dynamicMetaObject() : &staticMetaObject;
}

void *CallWidget::qt_metacast(const char *_clname)
{
    if (!_clname) return nullptr;
    if (!strcmp(_clname, qt_meta_stringdata_CallWidget.stringdata0))
        return static_cast<void*>(this);
    return QWidget::qt_metacast(_clname);
}

int CallWidget::qt_metacall(QMetaObject::Call _c, int _id, void **_a)
{
    _id = QWidget::qt_metacall(_c, _id, _a);
    if (_id < 0)
        return _id;
    if (_c == QMetaObject::InvokeMetaMethod) {
        if (_id < 12)
            qt_static_metacall(this, _c, _id, _a);
        _id -= 12;
    } else if (_c == QMetaObject::RegisterMethodArgumentMetaType) {
        if (_id < 12)
            *reinterpret_cast<QMetaType *>(_a[0]) = QMetaType();
        _id -= 12;
    }
    return _id;
}

// SIGNAL 0
void CallWidget::sigEnableNama(const bool & _t1)
{
    void *_a[] = { nullptr, const_cast<void*>(reinterpret_cast<const void*>(std::addressof(_t1))) };
    QMetaObject::activate(this, &staticMetaObject, 0, _a);
}
QT_WARNING_POP
QT_END_MOC_NAMESPACE
