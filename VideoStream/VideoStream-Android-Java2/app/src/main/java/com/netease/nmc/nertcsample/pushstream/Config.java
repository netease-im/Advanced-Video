package com.netease.nmc.nertcsample.pushstream;

import android.graphics.Color;
import android.graphics.Rect;

import com.netease.lava.nertc.sdk.live.NERtcLiveStreamImageInfo;

final class Config {
    /**
     *              2 x 2 layout
     *
     *               720x1280
     * \---------------------------------\
     * \      320x480        320x480     \
     * \   \----------\   \----------\   \
     * \   \          \   \          \   \
     * \   \          \   \          \   \
     * \   \----------\   \----------\   \
     * \      320x480        320x480     \
     * \   \----------\   \----------\   \
     * \   \          \   \          \   \
     * \   \          \   \          \   \
     * \   \----------\   \----------\   \
     * \                                 \
     * \---------------------------------\
     */

    private static final Rect rectLayout = new Rect(0, 0, 720, 1280);
    private static final Rect rectUser = new Rect(0, 0, 320, 480);
    private static final Rect[] rectUsers = new Rect[4];
    private static final Rect rectImage;
    private static final String imageUrl = "https://netease.im/res/image/about/banner.jpg"; // 3840x840
    private static final NERtcLiveStreamImageInfo backgroundImage = new NERtcLiveStreamImageInfo();

    static {
        for (int i = 0; i < 4; i++) {
            rectUsers[i] = new Rect(rectUser);
        }
        int width = rectUser.width();
        int height = rectUser.height();
        int paddingH = (rectLayout.width() - width * 2) / 3;
        int paddingV = 15;
        rectUsers[0].offset(paddingH, paddingV);
        rectUsers[1].offset(paddingH + width + paddingH, paddingV);
        rectUsers[2].offset(paddingH, paddingV + height + paddingV);
        rectUsers[3].offset(paddingH + width + paddingH, paddingV + height + paddingV);

        int paddingImage = 50;
        rectImage = new Rect(rectLayout);
        rectImage.inset(paddingImage, paddingImage);

        backgroundImage.url = imageUrl;
        backgroundImage.width = rectImage.width();
        backgroundImage.height = rectImage.height();
        backgroundImage.x = rectImage.left;
        backgroundImage.y = rectImage.top;
    }

    static Rect getRectLayout() {
        return rectLayout;
    }

    static Rect[] getRectUsers() {
        return rectUsers;
    }

    static int getBackgroundColor() {
        return Color.WHITE;
    }

    static NERtcLiveStreamImageInfo getBackgroundImage() {
//        return backgroundImage;
        return null;
    }
}
