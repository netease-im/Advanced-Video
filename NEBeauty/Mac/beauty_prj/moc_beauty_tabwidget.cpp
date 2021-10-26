/****************************************************************************
** Meta object code from reading C++ file 'beauty_tabwidget.h'
**
** Created by: The Qt Meta Object Compiler version 68 (Qt 6.2.0)
**
** WARNING! All changes made in this file will be lost!
*****************************************************************************/

#include <memory>
#include "source/include/beauty_tabwidget.h"
#include <QtCore/qbytearray.h>
#include <QtCore/qmetatype.h>
#if !defined(Q_MOC_OUTPUT_REVISION)
#error "The header file 'beauty_tabwidget.h' doesn't include <QObject>."
#elif Q_MOC_OUTPUT_REVISION != 68
#error "This file was generated using the moc from 6.2.0. It"
#error "cannot be used with the include files from this version of Qt."
#error "(The moc has changed too much.)"
#endif

QT_BEGIN_MOC_NAMESPACE
QT_WARNING_PUSH
QT_WARNING_DISABLE_DEPRECATED
struct qt_meta_stringdata_BeautyTabWidget_t {
    const uint offsetsAndSize[26];
    char stringdata0[150];
};
#define QT_MOC_LITERAL(ofs, len) \
    uint(offsetof(qt_meta_stringdata_BeautyTabWidget_t, stringdata0) + ofs), len 
static const qt_meta_stringdata_BeautyTabWidget_t qt_meta_stringdata_BeautyTabWidget = {
    {
QT_MOC_LITERAL(0, 15), // "BeautyTabWidget"
QT_MOC_LITERAL(16, 16), // "sigBeautyChanged"
QT_MOC_LITERAL(33, 0), // ""
QT_MOC_LITERAL(34, 2), // "id"
QT_MOC_LITERAL(37, 3), // "val"
QT_MOC_LITERAL(41, 16), // "sigFilterChanged"
QT_MOC_LITERAL(58, 21), // "sigItemStickerChanged"
QT_MOC_LITERAL(80, 11), // "std::string"
QT_MOC_LITERAL(92, 3), // "str"
QT_MOC_LITERAL(96, 14), // "sigBautyEnable"
QT_MOC_LITERAL(111, 6), // "enable"
QT_MOC_LITERAL(118, 15), // "sigBeautyMirror"
QT_MOC_LITERAL(134, 15) // "sigBeautyMakeup"

    },
    "BeautyTabWidget\0sigBeautyChanged\0\0id\0"
    "val\0sigFilterChanged\0sigItemStickerChanged\0"
    "std::string\0str\0sigBautyEnable\0enable\0"
    "sigBeautyMirror\0sigBeautyMakeup"
};
#undef QT_MOC_LITERAL

static const uint qt_meta_data_BeautyTabWidget[] = {

 // content:
      10,       // revision
       0,       // classname
       0,    0, // classinfo
       6,   14, // methods
       0,    0, // properties
       0,    0, // enums/sets
       0,    0, // constructors
       0,       // flags
       6,       // signalCount

 // signals: name, argc, parameters, tag, flags, initial metatype offsets
       1,    2,   50,    2, 0x06,    1 /* Public */,
       5,    2,   55,    2, 0x06,    4 /* Public */,
       6,    1,   60,    2, 0x06,    7 /* Public */,
       9,    1,   63,    2, 0x06,    9 /* Public */,
      11,    1,   66,    2, 0x06,   11 /* Public */,
      12,    1,   69,    2, 0x06,   13 /* Public */,

 // signals: parameters
    QMetaType::Void, QMetaType::Int, QMetaType::Int,    3,    4,
    QMetaType::Void, QMetaType::QString, QMetaType::Int,    3,    4,
    QMetaType::Void, 0x80000000 | 7,    8,
    QMetaType::Void, QMetaType::Bool,   10,
    QMetaType::Void, QMetaType::Bool,   10,
    QMetaType::Void, QMetaType::Bool,   10,

       0        // eod
};

void BeautyTabWidget::qt_static_metacall(QObject *_o, QMetaObject::Call _c, int _id, void **_a)
{
    if (_c == QMetaObject::InvokeMetaMethod) {
        auto *_t = static_cast<BeautyTabWidget *>(_o);
        (void)_t;
        switch (_id) {
        case 0: _t->sigBeautyChanged((*reinterpret_cast< const int(*)>(_a[1])),(*reinterpret_cast< const int(*)>(_a[2]))); break;
        case 1: _t->sigFilterChanged((*reinterpret_cast< const QString(*)>(_a[1])),(*reinterpret_cast< const int(*)>(_a[2]))); break;
        case 2: _t->sigItemStickerChanged((*reinterpret_cast< const std::string(*)>(_a[1]))); break;
        case 3: _t->sigBautyEnable((*reinterpret_cast< const bool(*)>(_a[1]))); break;
        case 4: _t->sigBeautyMirror((*reinterpret_cast< const bool(*)>(_a[1]))); break;
        case 5: _t->sigBeautyMakeup((*reinterpret_cast< const bool(*)>(_a[1]))); break;
        default: ;
        }
    } else if (_c == QMetaObject::IndexOfMethod) {
        int *result = reinterpret_cast<int *>(_a[0]);
        {
            using _t = void (BeautyTabWidget::*)(const int & , const int & );
            if (*reinterpret_cast<_t *>(_a[1]) == static_cast<_t>(&BeautyTabWidget::sigBeautyChanged)) {
                *result = 0;
                return;
            }
        }
        {
            using _t = void (BeautyTabWidget::*)(const QString & , const int & );
            if (*reinterpret_cast<_t *>(_a[1]) == static_cast<_t>(&BeautyTabWidget::sigFilterChanged)) {
                *result = 1;
                return;
            }
        }
        {
            using _t = void (BeautyTabWidget::*)(const std::string & );
            if (*reinterpret_cast<_t *>(_a[1]) == static_cast<_t>(&BeautyTabWidget::sigItemStickerChanged)) {
                *result = 2;
                return;
            }
        }
        {
            using _t = void (BeautyTabWidget::*)(const bool & );
            if (*reinterpret_cast<_t *>(_a[1]) == static_cast<_t>(&BeautyTabWidget::sigBautyEnable)) {
                *result = 3;
                return;
            }
        }
        {
            using _t = void (BeautyTabWidget::*)(const bool & );
            if (*reinterpret_cast<_t *>(_a[1]) == static_cast<_t>(&BeautyTabWidget::sigBeautyMirror)) {
                *result = 4;
                return;
            }
        }
        {
            using _t = void (BeautyTabWidget::*)(const bool & );
            if (*reinterpret_cast<_t *>(_a[1]) == static_cast<_t>(&BeautyTabWidget::sigBeautyMakeup)) {
                *result = 5;
                return;
            }
        }
    }
}

const QMetaObject BeautyTabWidget::staticMetaObject = { {
    QMetaObject::SuperData::link<QDialog::staticMetaObject>(),
    qt_meta_stringdata_BeautyTabWidget.offsetsAndSize,
    qt_meta_data_BeautyTabWidget,
    qt_static_metacall,
    nullptr,
qt_incomplete_metaTypeArray<qt_meta_stringdata_BeautyTabWidget_t
, QtPrivate::TypeAndForceComplete<BeautyTabWidget, std::true_type>, QtPrivate::TypeAndForceComplete<void, std::false_type>, QtPrivate::TypeAndForceComplete<const int &, std::false_type>, QtPrivate::TypeAndForceComplete<const int &, std::false_type>, QtPrivate::TypeAndForceComplete<void, std::false_type>, QtPrivate::TypeAndForceComplete<const QString &, std::false_type>, QtPrivate::TypeAndForceComplete<const int &, std::false_type>, QtPrivate::TypeAndForceComplete<void, std::false_type>, QtPrivate::TypeAndForceComplete<const std::string &, std::false_type>, QtPrivate::TypeAndForceComplete<void, std::false_type>, QtPrivate::TypeAndForceComplete<const bool &, std::false_type>, QtPrivate::TypeAndForceComplete<void, std::false_type>, QtPrivate::TypeAndForceComplete<const bool &, std::false_type>, QtPrivate::TypeAndForceComplete<void, std::false_type>, QtPrivate::TypeAndForceComplete<const bool &, std::false_type>



>,
    nullptr
} };


const QMetaObject *BeautyTabWidget::metaObject() const
{
    return QObject::d_ptr->metaObject ? QObject::d_ptr->dynamicMetaObject() : &staticMetaObject;
}

void *BeautyTabWidget::qt_metacast(const char *_clname)
{
    if (!_clname) return nullptr;
    if (!strcmp(_clname, qt_meta_stringdata_BeautyTabWidget.stringdata0))
        return static_cast<void*>(this);
    return QDialog::qt_metacast(_clname);
}

int BeautyTabWidget::qt_metacall(QMetaObject::Call _c, int _id, void **_a)
{
    _id = QDialog::qt_metacall(_c, _id, _a);
    if (_id < 0)
        return _id;
    if (_c == QMetaObject::InvokeMetaMethod) {
        if (_id < 6)
            qt_static_metacall(this, _c, _id, _a);
        _id -= 6;
    } else if (_c == QMetaObject::RegisterMethodArgumentMetaType) {
        if (_id < 6)
            *reinterpret_cast<QMetaType *>(_a[0]) = QMetaType();
        _id -= 6;
    }
    return _id;
}

// SIGNAL 0
void BeautyTabWidget::sigBeautyChanged(const int & _t1, const int & _t2)
{
    void *_a[] = { nullptr, const_cast<void*>(reinterpret_cast<const void*>(std::addressof(_t1))), const_cast<void*>(reinterpret_cast<const void*>(std::addressof(_t2))) };
    QMetaObject::activate(this, &staticMetaObject, 0, _a);
}

// SIGNAL 1
void BeautyTabWidget::sigFilterChanged(const QString & _t1, const int & _t2)
{
    void *_a[] = { nullptr, const_cast<void*>(reinterpret_cast<const void*>(std::addressof(_t1))), const_cast<void*>(reinterpret_cast<const void*>(std::addressof(_t2))) };
    QMetaObject::activate(this, &staticMetaObject, 1, _a);
}

// SIGNAL 2
void BeautyTabWidget::sigItemStickerChanged(const std::string & _t1)
{
    void *_a[] = { nullptr, const_cast<void*>(reinterpret_cast<const void*>(std::addressof(_t1))) };
    QMetaObject::activate(this, &staticMetaObject, 2, _a);
}

// SIGNAL 3
void BeautyTabWidget::sigBautyEnable(const bool & _t1)
{
    void *_a[] = { nullptr, const_cast<void*>(reinterpret_cast<const void*>(std::addressof(_t1))) };
    QMetaObject::activate(this, &staticMetaObject, 3, _a);
}

// SIGNAL 4
void BeautyTabWidget::sigBeautyMirror(const bool & _t1)
{
    void *_a[] = { nullptr, const_cast<void*>(reinterpret_cast<const void*>(std::addressof(_t1))) };
    QMetaObject::activate(this, &staticMetaObject, 4, _a);
}

// SIGNAL 5
void BeautyTabWidget::sigBeautyMakeup(const bool & _t1)
{
    void *_a[] = { nullptr, const_cast<void*>(reinterpret_cast<const void*>(std::addressof(_t1))) };
    QMetaObject::activate(this, &staticMetaObject, 5, _a);
}
QT_WARNING_POP
QT_END_MOC_NAMESPACE
