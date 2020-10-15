//
//  NTESAppDelegate.m
//  MediaStats-iOS-ObjC
//
//  Created by NetEase on 2020/08/01.
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "NTESAppDelegate.h"
#import "AppKey.h"

@interface NTESAppDelegate ()

@end

@implementation NTESAppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    [[NSURLSession.sharedSession dataTaskWithRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:@"https://www.baidu.com"]]] resume];
    NSAssert(![kAppKey isEqualToString:@"请填入您的AppKey"],
             @"请在AppKey.h中填入您的APPKey之后，再运行Demo");
    return YES;
}
@end
