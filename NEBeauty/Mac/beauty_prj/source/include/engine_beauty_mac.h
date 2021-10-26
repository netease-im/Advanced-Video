//
//  engine_beauty_mac.hpp
//  1v1_beauty
//
//  Created by Robin on 2021/10/15.
//

#ifndef engine_beauty_mac_hpp
#define engine_beauty_mac_hpp

#include <stdio.h>
#include <string>

int engine_StartBeauty();
void engine_StopBeauty();
void engine_EnableNertcBeauty(const bool &enable);
void engine_EnableNertcMirror(const bool &enable);
int engine_EnableNertcMakeup(const bool &enable);
int engine_SelectBeautySticker(const std::string &bundle_name);
int engine_SelectBeautyFilter(const std::string &bundle_name, const int &val);
int engine_SetBeautyEffect(const int &type, float level);

#endif /* engine_beauty_mac_hpp */
