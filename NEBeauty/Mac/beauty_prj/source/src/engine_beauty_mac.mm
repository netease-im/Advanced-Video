//
//  engine_beauty_mac.cpp
//  1v1_beauty
//
//  Created by Robin on 2021/10/15.
//

#include "engine_beauty_mac.h"
#include "nertc_beauty.h"
#include <Foundation/Foundation.h>

static NSString * const kNEBeautyLocalFilePath = @"data/beauty/nebeauty/beauty/template";
static NSString * const kNEBeautyLocalMakeupFilePath = @"/data/beauty/nebeauty/StickerZipAndIcons/makeup_sticker.bundle/makeup/";
static NSString * const kNEBeautyLocalStickerFilePath = @"/data/beauty/nebeauty/StickerZipAndIcons/2d_sticker.bundle/";
static NSString * const kNEBeautyLocalFiltersFilePath = @"/data/beauty/nebeauty/Filters/filters.bundle/";

int engine_StartBeauty()
{
    [[NERtcBeauty shareInstance] startBeauty];
    
    NSString *strBeauty = [[NSBundle mainBundle] pathForResource:kNEBeautyLocalFilePath ofType:@"json"];
    NSString *strBeautyPath = [strBeauty stringByDeletingLastPathComponent];
    NSString *dir = [strBeautyPath stringByAppendingString:@"/"];
    NSString *templateName = @"template.json";
    [[NERtcBeauty shareInstance] addTempleteWithPath:dir andName:templateName];
    
    return -1;
}

void engine_StopBeauty()
{
    [[NERtcBeauty shareInstance] stopBeauty];
}

void engine_EnableNertcBeauty(const bool &enable)
{
    [NERtcBeauty shareInstance].isOpenBeauty = enable;
}

void engine_EnableNertcMirror(const bool &enable)
{
    [NERtcBeauty shareInstance].flipX = enable;
}

int engine_EnableNertcMakeup(const bool &enable)
{
    int ret = -1;
    if (false == enable)
    {
        [[NERtcBeauty shareInstance] removeBeautyMakeup];
    }
    else
    {
        NSString *resourcePath = [NSBundle mainBundle].resourcePath;
        NSString *dir = [resourcePath stringByAppendingFormat:kNEBeautyLocalMakeupFilePath];
        NSString *templateName = @"template.json";
        [[NERtcBeauty shareInstance] addBeautyMakeupWithPath:dir andName:templateName];
    }
    return ret;
}

int engine_SelectBeautySticker(const std::string &bundle_name)
{
    int ret = -1;
    if (0 != strcmp("none", bundle_name.c_str()))
    {
        NSString *resourcePath = [NSBundle mainBundle].resourcePath;
        NSString *dir = [resourcePath stringByAppendingFormat:kNEBeautyLocalStickerFilePath];
        NSString *localModelPath = [dir stringByAppendingFormat:@"%s", bundle_name.c_str()];
        
        localModelPath = [localModelPath stringByReplacingOccurrencesOfString:@"\\template.json" withString:@"/"];
       [[NERtcBeauty shareInstance] addBeautyStickerWithPath:localModelPath andName:@"template.json"];
        ret = 1;
    }
    else
    {
        [[NERtcBeauty shareInstance] removeBeautySticker];
        ret = 1;
    }
    
    return ret;
}

int engine_SelectBeautyFilter(const std::string &bundle_name, const int &val)
{
    int ret = -1;
    if (0 != strcmp("none", bundle_name.c_str()))
    {
        NSString *resourcePath = [NSBundle mainBundle].resourcePath;
        NSString *dir = [resourcePath stringByAppendingFormat:kNEBeautyLocalFiltersFilePath];
        NSString *fileName = [NSString stringWithUTF8String:bundle_name.c_str()];
        NSString *localModelPath = [dir stringByAppendingFormat:@"%@", fileName];
        localModelPath = [localModelPath stringByReplacingOccurrencesOfString:@"\\template.json" withString:@"/"];
        [[NERtcBeauty shareInstance] addBeautyFilterWithPath:localModelPath andName:@"template.json"];
        [NERtcBeauty shareInstance].filterStrength = val / 100.f;
    }
    else
    {
        [[NERtcBeauty shareInstance] removeBeautyFilter];
    }
    
    return ret;
}

int engine_SetBeautyEffect(const int &type, float level)
{
    int ret = -1;
    [[NERtcBeauty shareInstance] setBeautyEffectWithValue:level atType:type];
    return ret;
}
