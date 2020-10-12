//
//  NTESVideoConfigViewController.m
//  NEPushStreamBypath
//
//  Created by I am Groot on 2020/9/15.
//  Copyright © 2020 丁文超. All rights reserved.
//

#import "NTESVideoConfigViewController.h"

@interface NTESVideoConfigViewController ()<UITextViewDelegate>
@property (weak, nonatomic) IBOutlet UITextView *textView;
@property (weak, nonatomic) IBOutlet UIButton *pushButton;

@end

@implementation NTESVideoConfigViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.textView.text = self.currentURL;
    self.textView.layer.cornerRadius = 6;
    self.textView.layer.borderWidth = 1.0;
    self.textView.layer.borderColor = [UIColor lightGrayColor].CGColor;
    self.pushButton.selected = self.isPushingStream;
    
    
}
- (IBAction)pushVideoStream:(UIButton *)sender {
    if (self.isPushingStream) {
        //停止推流
        if ([self.delegate respondsToSelector:@selector(stopPushStream)]) {
            [self.delegate stopPushStream];
            [self.navigationController popViewControllerAnimated:YES];
        }
        
    }else {
        if (self.textView.text.length) {
            if ([self.delegate respondsToSelector:@selector(didGetStreamURL:)]) {
                [self.delegate didGetStreamURL:self.textView.text];
            }
            [self.navigationController popViewControllerAnimated:YES];
        }
        
    }
}
- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    if (self.textView.isFirstResponder) {
        [self.textView resignFirstResponder];
    }
}
@end
