//
//  NEBeautySliderTableView.m
//  Beauty-iOS-ObjC
//
//  Created by zhangchenliang on 2021/10/15.
//  Copyright © 2021 NetEase. All rights reserved.
//

#import "NEBeautySliderTableView.h"

static NSString *kNEBeautySliderTableViewCellId = @"kNEBeautySliderTableViewCellId";

@interface NEBeautySliderTableView ()<UITableViewDataSource, UITableViewDelegate>

@property (nonatomic, strong) NSArray<NEBeautySliderDisplayModel *> *modelArray;
@property (nonatomic, copy) NEBeautySliderValueChangeBlock valueChangeBlock;

@end

@implementation NEBeautySliderTableView

- (instancetype)initWithFrame:(CGRect)frame valueChangeBlock:(NEBeautySliderValueChangeBlock)valueChangeBlock {
    self = [super initWithFrame:frame];
    if (self) {
        _valueChangeBlock = valueChangeBlock;
        
        self.backgroundColor = [UIColor clearColor];
        self.showsVerticalScrollIndicator = NO;
        self.showsHorizontalScrollIndicator = NO;
        self.dataSource = self;
        self.delegate = self;
        
        [self registerClass:[NEBeautySliderTableViewCell class] forCellReuseIdentifier:kNEBeautySliderTableViewCellId];
    }
    
    return self;
}

- (void)reloadWithSliderModelArray:(NSArray<NEBeautySliderDisplayModel *> *)modelArray {
    _modelArray = modelArray;
    
    [self reloadData];
}

#pragma mark - UITableViewDataSource

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.modelArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    NEBeautySliderTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:kNEBeautySliderTableViewCellId forIndexPath:indexPath];
    
    NEBeautySliderDisplayModel *model = self.modelArray[indexPath.row];
    [cell updateContentWithModel:model];
    [cell updateValueChangeBlock:_valueChangeBlock];
    
    return cell;
}

#pragma mark - UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 40;
}

@end
