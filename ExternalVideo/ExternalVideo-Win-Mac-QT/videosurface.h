#ifndef VIDEOSURFACE_H
#define VIDEOSURFACE_H

#include <QAbstractVideoSurface>
#include <QObject>

class VideoSurface : public QAbstractVideoSurface {
    Q_OBJECT
public:
    explicit VideoSurface(QObject* parent = nullptr);

    QList<QVideoFrame::PixelFormat> supportedPixelFormats(QAbstractVideoBuffer::HandleType handleType = QAbstractVideoBuffer::NoHandle) const;
    bool present(const QVideoFrame& frame);

    bool isFormatSupported(const QVideoSurfaceFormat &format){return true;};
signals:
    void frameAvailable(QVideoFrame& frame);
};

#endif  // VIDEOSURFACE_H
