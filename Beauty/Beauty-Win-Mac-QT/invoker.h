#include <QThread>
typedef std::function<void()> InvokerFunc;
class Invoker: public QObject
{
    Q_OBJECT
public:
    Invoker(QObject *parent=0):
        QObject(parent)
    {
        qRegisterMetaType<InvokerFunc>("InvokerFunc");
    }

    void execute(const InvokerFunc &func, bool block = false)
    {
        if (QThread::currentThread()==thread())
        {//is same thread
            func();
            return;
        }
        if (block)
        {
            metaObject()->invokeMethod(this, "onExecute", Qt::BlockingQueuedConnection,
                                       Q_ARG(InvokerFunc, func));
        }
        else{
            metaObject()->invokeMethod(this, "onExecute", Qt::QueuedConnection,
                                       Q_ARG(InvokerFunc, func));
        }
    }

private slots:
    void onExecute(const InvokerFunc &func)
    {
        func();
    }
};
