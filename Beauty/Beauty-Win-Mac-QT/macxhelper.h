
#include <QObject>

#include <QGuiApplication>

class Macxhelper : public QObject
{
    Q_OBJECT
public:
    Macxhelper(QObject* parent = nullptr);

public:
    static void  getCVPixelbufferInfo(void* cvref, void* & data, int& width, int& height);
    static int getWindowId(WId wid);
};
