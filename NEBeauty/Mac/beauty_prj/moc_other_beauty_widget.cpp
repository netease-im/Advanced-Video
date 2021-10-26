/****************************************************************************
** Meta object code from reading C++ file 'other_beauty_widget.h'
**
** Created by: The Qt Meta Object Compiler version 68 (Qt 6.2.0)
**
** WARNING! All changes made in this file will be lost!
*****************************************************************************/

#include <memory>
#include "source/include/other_beauty_widget.h"
#include <QtCore/qbytearray.h>
#include <QtCore/qmetatype.h>
#if !defined(Q_MOC_OUTPUT_REVISION)
#error "The header file 'other_beauty_widget.h' doesn't include <QObject>."
#elif Q_MOC_OUTPUT_REVISION != 68
#error "This file was generated using the moc from 6.2.0. It"
#error "cannot be used with the include files from this version of Qt."
#error "(The moc has changed too much.)"
#endif

QT_BEGIN_MOC_NAMESPACE
QT_WARNING_PUSH
QT_WARNING_DISABLE_DEPRECATED
struct qt_meta_stringdata_OtherBeautyWidget_t {
    const uint offsetsAndSize[14];
    char stringdata0[80];
};
#define QT_MOC_LITERAL(ofs, len) \
    uint(offsetof(qt_meta_stringdata_OtherBeautyWidget_t, stringdata0) + ofs), len 
static const qt_meta_stringdata_OtherBeautyWidget_t qt_meta_stringdata_OtherBeautyWidget = {
    {
QT_MOC_LITERAL(0, 17), // "OtherBeautyWidget"
QT_MOC_LITERAL(18, 14), // "sigBautyEnable"
QT_MOC_LITERAL(33, 0), // ""
QT_MOC_LITERAL(34, 6), // "enbale"
QT_MOC_LITERAL(41, 15), // "sigBeautyMirror"
QT_MOC_LITERAL(57, 6), // "enable"
QT_MOC_LITERAL(64, 15) // "sigBeautyMakeup"

    },
    "OtherBeautyWidget\0sigBautyEnable\0\0"
    "enbale\0sigBeautyMirror\0enable\0"
    "sigBeautyMakeup"
};
#undef QT_MOC_LITERAL

static const uint qt_meta_data_OtherBeautyWidget[] = {

 // content:
      10,       // revision
       0,       // classname
       0,    0, // classinfo
       3,   14, // methods
       0,    0, // properties
       0,    0, // enums/sets
       0,    0, // constructors
       0,       // flags
       3,       // signalCount

 // signals: name, argc, parameters, tag, flags, initial metatype offsets
       1,    1,   32,    2, 0x06,    1 /* Public */,
       4,    1,   35,    2, 0x06,    3 /* Public */,
       6,    1,   38,    2, 0x06,    5 /* Public */,

 // signals: parameters
    QMetaType::Void, QMetaType::Bool,    3,
    QMetaType::Void, QMetaType::Bool,    5,
    QMetaType::Void, QMetaType::Bool,    5,

       0        // eod
};

void OtherBeautyWidget::qt_static_metacall(QObject *_o, QMetaObject::Call _c, int _id, void **_a)
{
    if (_c == QMetaObject::InvokeMetaMethod) {
        auto *_t = static_cast<OtherBeautyWidget *>(_o);
        (void)_t;
        switch (_id) {
        case 0: _t->sigBautyEnable((*reinterpret_cast< const bool(*)>(_a[1]))); break;
        case 1: _t->sigBeautyMirror((*reinterpret_cast< const bool(*)>(_a[1]))); break;
        case 2: _t->sigBeautyMakeup((*reinterpret_cast< const bool(*)>(_a[1]))); break;
        default: ;
        }
    } else if (_c == QMetaObject::IndexOfMethod) {
        int *result = reinterpret_cast<int *>(_a[0]);
        {
            using _t = void (OtherBeautyWidget::*)(const bool & );
            if (*reinterpret_cast<_t *>(_a[1]) == static_cast<_t>(&OtherBeautyWidget::sigBautyEnable)) {
                *result = 0;
                return;
            }
        }
        {
            using _t = void (OtherBeautyWidget::*)(const bool & );
            if (*reinterpret_cast<_t *>(_a[1]) == static_cast<_t>(&OtherBeautyWidget::sigBeautyMirror)) {
                *result = 1;
                return;
            }
        }
        {
            using _t = void (OtherBeautyWidget::*)(const bool & );
            if (*reinterpret_cast<_t *>(_a[1]) == static_cast<_t>(&OtherBeautyWidget::sigBeautyMakeup)) {
                *result = 2;
                return;
            }
        }
    }
}

const QMetaObject OtherBeautyWidget::staticMetaObject = { {
    QMetaObject::SuperData::link<QWidget::staticMetaObject>(),
    qt_meta_stringdata_OtherBeautyWidget.offsetsAndSize,
    qt_meta_data_OtherBeautyWidget,
    qt_static_metacall,
    nullptr,
qt_incomplete_metaTypeArray<qt_meta_stringdata_OtherBeautyWidget_t
, QtPrivate::TypeAndForceComplete<OtherBeautyWidget, std::true_type>, QtPrivate::TypeAndForceComplete<void, std::false_type>, QtPrivate::TypeAndForceComplete<const bool &, std::false_type>, QtPrivate::TypeAndForceComplete<void, std::false_type>, QtPrivate::TypeAndForceComplete<const bool &, std::false_type>, QtPrivate::TypeAndForceComplete<void, std::false_type>, QtPrivate::TypeAndForceComplete<const bool &, std::false_type>



>,
    nullptr
} };


const QMetaObject *OtherBeautyWidget::metaObject() const
{
    return QObject::d_ptr->metaObject ? QObject::d_ptr->dynamicMetaObject() : &staticMetaObject;
}

void *OtherBeautyWidget::qt_metacast(const char *_clname)
{
    if (!_clname) return nullptr;
    if (!strcmp(_clname, qt_meta_stringdata_OtherBeautyWidget.stringdata0))
        return static_cast<void*>(this);
    return QWidget::qt_metacast(_clname);
}

int OtherBeautyWidget::qt_metacall(QMetaObject::Call _c, int _id, void **_a)
{
    _id = QWidget::qt_metacall(_c, _id, _a);
    if (_id < 0)
        return _id;
    if (_c == QMetaObject::InvokeMetaMethod) {
        if (_id < 3)
            qt_static_metacall(this, _c, _id, _a);
        _id -= 3;
    } else if (_c == QMetaObject::RegisterMethodArgumentMetaType) {
        if (_id < 3)
            *reinterpret_cast<QMetaType *>(_a[0]) = QMetaType();
        _id -= 3;
    }
    return _id;
}

// SIGNAL 0
void OtherBeautyWidget::sigBautyEnable(const bool & _t1)
{
    void *_a[] = { nullptr, const_cast<void*>(reinterpret_cast<const void*>(std::addressof(_t1))) };
    QMetaObject::activate(this, &staticMetaObject, 0, _a);
}

// SIGNAL 1
void OtherBeautyWidget::sigBeautyMirror(const bool & _t1)
{
    void *_a[] = { nullptr, const_cast<void*>(reinterpret_cast<const void*>(std::addressof(_t1))) };
    QMetaObject::activate(this, &staticMetaObject, 1, _a);
}

// SIGNAL 2
void OtherBeautyWidget::sigBeautyMakeup(const bool & _t1)
{
    void *_a[] = { nullptr, const_cast<void*>(reinterpret_cast<const void*>(std::addressof(_t1))) };
    QMetaObject::activate(this, &staticMetaObject, 2, _a);
}
QT_WARNING_POP
QT_END_MOC_NAMESPACE
