#ifndef _ITEM_STICKER_WIDGET_H
#define _ITEM_STICKER_WIDGET_H

#include <QWidget>
#include <QVector>

class BeautyItemWidget;
class ItemStickerWidget :public QWidget
{
    Q_OBJECT

public:
    ItemStickerWidget(QWidget* parent = nullptr);
    ~ItemStickerWidget();

private:
    void setUi();

Q_SIGNALS:
    void sigItemStickerChanged(const std::string &str);

private Q_SLOTS:
    void onItemStickerChanged(const int& id, const int &val);

private:
    QVector<QString>           label_name_;
    QVector<QString>           type_;
    QVector<QString>           value_;
    QVector<BeautyItemWidget*> items_;
    const std::string          bundle_path_[6] = { 
                                                  "bunny\\template.json", 
                                                  "chick\\template.json",
                                                  "glass\\template.json",    
                                                  "rabbiteating\\template.json",
                                                  "snow\\template.json",
                                                  "spot\\template.json" };
};
#endif //_ITEM_STICKER_WIDGET_H
