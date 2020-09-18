//
//  NTESDemoViewController.m
//  MediaStats-iOS-ObjC
//
//  Created by NetEase on 2020/08/01.
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//  

#import "NTESDemoViewController.h"
#import "NTESDemoP2PViewController.h"

@interface NTESDemoViewController () <UITextFieldDelegate>

@property (strong, nonatomic) IBOutlet UITextField *roomIDTextField; //房间输入框
@property (strong, nonatomic) IBOutlet UIButton *joinButton;  //加入按钮

@end

@implementation NTESDemoViewController

- (void)viewDidLoad {
    [super viewDidLoad];
}

- (void)touchesEnded:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [super touchesBegan:touches withEvent:event];
    [self.view endEditing:YES]; //点击空白，回收键盘
}

#pragma mark - Actions

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    if ([segue.identifier isEqualToString:@"Call1to1"]) {
        NTESDemoP2PViewController *destVC = (NTESDemoP2PViewController *)segue.destinationViewController;
        destVC.roomId = self.roomId;
        destVC.userId = self.userId;
    }
}

#pragma mark - Getter
//用户Id，随机生成
- (uint64_t)userId {
    return (10000 + arc4random() % (99999 - 10000));
}

//房间Id，用户输入
- (NSString *)roomId {
    return _roomIDTextField.text;
}

#pragma mark - <UITextFieldDelegate>
- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string {
    
    //删除字符操作，直接允许
    if (string.length == 0)
        return YES;
    
    //生成输入后的roomId
    NSString *roomId = [textField.text stringByReplacingCharactersInRange:range
                                                               withString:string];
    
    //检测输入的字符规格，判断是否允许输入roomId
    BOOL allowInput = [self isValidRoomId:roomId];
    
    //根据输入后的roomId，更新加入按钮的状态，如果长度为0则不允许点击
    _joinButton.enabled = (roomId.length != 0);
    
    //返回是否允许输入结果
    return allowInput;
}

//房间名校验
- (BOOL)isValidRoomId:(NSString *)roomId {
    
    //长度限制，限制在12以内
    if (roomId.length >= 12) {
        _roomIDTextField.text = [roomId substringWithRange:NSMakeRange(0, 12)];
        return NO;
    }
    
    //符号限制，仅数字
    NSRegularExpression *regex = [[NSRegularExpression alloc] initWithPattern:@"^[0-9]+$" options:0 error:nil];
    NSArray *results = [regex matchesInString:roomId options:0 range:NSMakeRange(0, roomId.length)];
    return results.count > 0;
}

@end
