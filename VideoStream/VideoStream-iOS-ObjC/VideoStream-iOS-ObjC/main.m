//
//  main.m
//  NERtcVideoStreamSample
//
//  Created by 丁文超 on 2020/6/22.
//  Copyright © 2020 丁文超. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NTESAppDelegate.h"

int main(int argc, char * argv[]) {
    NSString * appDelegateClassName;
    @autoreleasepool {
        // Setup code that might create autoreleased objects goes here.
        appDelegateClassName = NSStringFromClass([NTESAppDelegate class]);
    }
    return UIApplicationMain(argc, argv, nil, appDelegateClassName);
}
