#include <QApplication>
#include <QFont>
#include "join_widget.h"

int main(int argc, char *argv[])
{
#ifdef WIN32
    QApplication::setAttribute(Qt::AA_EnableHighDpiScaling);
    QApplication::setHighDpiScaleFactorRoundingPolicy(Qt::HighDpiScaleFactorRoundingPolicy::PassThrough);
#else
    qputenv("QT_MAC_WANTS_LAYER", "1");
    QApplication::setAttribute(Qt::AA_EnableHighDpiScaling);
#endif
    
    QApplication::setAttribute(Qt::AA_EnableHighDpiScaling);
    //QApplication::setHighDpiScaleFactorRoundingPolicy(Qt::HighDpiScaleFactorRoundingPolicy::PassThrough);
    QApplication a(argc, argv);
    QFont font;
    font.setFamily("Microsoft YaHei");
    a.setFont(font);

    JoinWidget demo;
    demo.show();

    return a.exec();
}
