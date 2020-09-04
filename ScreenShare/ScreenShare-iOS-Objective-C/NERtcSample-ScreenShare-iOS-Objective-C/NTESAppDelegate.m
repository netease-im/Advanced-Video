//
//  AppDelegate.m
//  NERtcScreenShareSample
//
//  Created by 丁文超 on 2020/7/15.
//  Copyright © 2020 丁文超. All rights reserved.
//

#import "NTESAppDelegate.h"

@implementation NTESAppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
    [[NSURLSession.sharedSession dataTaskWithRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:@"https://www.baidu.com"]]] resume];
    return YES;
}

@end
