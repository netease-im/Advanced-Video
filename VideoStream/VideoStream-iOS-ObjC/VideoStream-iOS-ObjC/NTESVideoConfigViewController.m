//
//  NTESVideoConfigViewController.m
//  NEPushStreamBypath
//
//  Created by I am Groot on 2020/9/15.
//  Copyright © 2020 丁文超. All rights reserved.
//

#import "NTESVideoConfigViewController.h"

@interface NTESVideoConfigViewController ()
@property (weak, nonatomic) IBOutlet UITextField *textField;

@end

@implementation NTESVideoConfigViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
}
- (IBAction)pushVideoStream:(id)sender {
    if (self.textField.text.length) {
        if ([self.delegate respondsToSelector:@selector(didGetStreamURL:)]) {
            [self.delegate didGetStreamURL:self.textField.text];
        }
        [self.navigationController popViewControllerAnimated:YES];
    }
}

@end
