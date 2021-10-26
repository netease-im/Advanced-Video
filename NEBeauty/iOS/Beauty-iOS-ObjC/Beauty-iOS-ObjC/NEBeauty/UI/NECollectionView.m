//
//  NEStickersCollectionView.m
//
//  Created by Ycq on 05/13/2019.
//  Copyright © 2019 NetEase Audio Lab. All rights reserved.
//

#import "NECollectionView.h"
#import "NECollectionViewCell.h"


@interface NECollectionView()<UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout>

@property (nonatomic, strong) NSArray<NECollectionViewDisplayModel *> *modelArray;
@property (nonatomic, strong) NECollectionViewDisplayModel *selectedModel;
@property (nonatomic, copy) NECollectionViewSelectionBlock selectionBlock;

@end

@implementation NECollectionView

- (instancetype)initWithFrame:(CGRect)frame selectionBlock:(NECollectionViewSelectionBlock)selectionBlock {
    UICollectionViewFlowLayout *flowLayout = [[UICollectionViewFlowLayout alloc] init];
    flowLayout.scrollDirection = UICollectionViewScrollDirectionVertical;
    flowLayout.itemSize = CGSizeMake(60, 60);
    flowLayout.minimumLineSpacing = 5;
    flowLayout.minimumInteritemSpacing = 5;
    flowLayout.sectionInset = UIEdgeInsetsMake(5, 5, 5, 5);
    flowLayout.footerReferenceSize = CGSizeMake([UIScreen mainScreen].bounds.size.width, 30);
    
    self = [super initWithFrame:frame collectionViewLayout:flowLayout];
    if (self) {
        self.selectionBlock = selectionBlock;
        
        [self setBackgroundColor:[UIColor clearColor]];
        self.alwaysBounceVertical = NO;
        self.showsHorizontalScrollIndicator = NO;
        self.showsVerticalScrollIndicator = NO;
        self.delegate = self;
        self.dataSource = self;
        
        [self registerClass:[NECollectionViewCell class] forCellWithReuseIdentifier:@"NECollectionViewCell"];
    }
    
    return self;
}

- (void)reloadWithModelArray:(NSArray<NECollectionViewDisplayModel *> *)modelArray {
    self.modelArray = modelArray;
    
    [self reloadData];
}

- (void)clearSelection {
    self.selectedModel.isSelected = NO;
    self.selectedModel = nil;
    
    [self reloadData];
}

#pragma mark - UICollectionViewDataSource

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return self.modelArray.count;
}

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView {
    return 1;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    NECollectionViewCell *cell = [self dequeueReusableCellWithReuseIdentifier:@"NECollectionViewCell" forIndexPath:indexPath];
    
    NECollectionViewDisplayModel *model = self.modelArray[indexPath.row];
    [cell updateWithModel:model];
    
    return cell;
}

#pragma mark - UICollectionViewDelegate

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    if (self.selectedModel) {
        self.selectedModel.isSelected = NO;
    }
    
    if (self.modelArray[indexPath.row].type == self.selectedModel.type
        && self.modelArray[indexPath.row].index == self.selectedModel.index) {
        self.selectedModel = nil;
    } else {
        self.modelArray[indexPath.row].isSelected = YES;
        self.selectedModel = self.modelArray[indexPath.row];
    }
    
    [collectionView reloadData];
    
    if (self.selectionBlock) {
        self.selectionBlock(self.selectedModel);
    }
}

@end


@interface NEFilterCollectionView()<UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout>

@property (nonatomic, strong) NSArray<NECollectionViewDisplayModel *> *modelArray;
@property (nonatomic, strong) NECollectionViewDisplayModel *selectedModel;
@property (nonatomic, copy) NECollectionViewSelectionBlock selectionBlock;

@end

@implementation NEFilterCollectionView

- (instancetype)initWithFrame:(CGRect)frame selectionBlock:(NECollectionViewSelectionBlock)selectionBlock {
    UICollectionViewFlowLayout *flowLayout = [[UICollectionViewFlowLayout alloc] init];
    flowLayout.scrollDirection = UICollectionViewScrollDirectionHorizontal;
    flowLayout.itemSize = CGSizeMake(65, 90);
    flowLayout.minimumInteritemSpacing = 180;
    flowLayout.minimumLineSpacing = 5;
    flowLayout.sectionInset = UIEdgeInsetsMake(-10, 5, 5, 5);
    
    self = [super initWithFrame:frame collectionViewLayout:flowLayout];
    if (self) {
        self.selectionBlock = selectionBlock;
        
        self.backgroundColor = [UIColor clearColor];
        self.showsHorizontalScrollIndicator = NO;
        self.showsVerticalScrollIndicator = NO;
        self.delegate = self;
        self.dataSource = self;
        
        [self registerClass:[NEFilterCollectionViewCell class] forCellWithReuseIdentifier:@"NEFilterCollectionViewCell"];
    }
    
    return self;
}

- (void)reloadWithModelArray:(NSArray<NECollectionViewDisplayModel *> *)modelArray {
    self.modelArray = modelArray;
    
    [self reloadData];
}

- (void)clearSelection {
    self.selectedModel.isSelected = NO;
    self.selectedModel = nil;
    
    [self reloadData];
}

#pragma mark - UICollectionViewDataSource

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return self.modelArray.count;
}

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView {
    return 1;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    NEFilterCollectionViewCell *cell = [self dequeueReusableCellWithReuseIdentifier:@"NEFilterCollectionViewCell" forIndexPath:indexPath];
    cell.backgroundColor = [UIColor whiteColor];
    
    NECollectionViewDisplayModel *model = self.modelArray[indexPath.row];
    [cell updateWithModel:model];
    
    return cell;
}

#pragma mark - UICollectionViewDelegate

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    if (self.selectedModel) {
        self.selectedModel.isSelected = NO;
    }
    
    if (self.modelArray[indexPath.row].type == self.selectedModel.type
        && self.modelArray[indexPath.row].index == self.selectedModel.index) {
        self.selectedModel = nil;
    } else {
        self.modelArray[indexPath.row].isSelected = YES;
        self.selectedModel = self.modelArray[indexPath.row];
    }
    
    [collectionView reloadData];
    
    if (self.selectionBlock) {
        self.selectionBlock(self.selectedModel);
    }
}

@end

