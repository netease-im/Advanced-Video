//
//  NTESAudioStreamUserCell.m
//  AudioStream-iOS-ObjC
//
//  Created by 丁文超 on 2020/6/23.
//  Copyright © 2020 丁文超. All rights reserved.
//

#import "NTESAudioStreamUserCell.h"

@implementation NTESAudioStreamUserCell

- (void)awakeFromNib
{
    [super awakeFromNib];
    self.userIDLabel.layer.cornerRadius = 5;
    self.userIDLabel.layer.borderWidth = 0.5;
}

@end
