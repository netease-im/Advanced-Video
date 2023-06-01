//
//  NECollectionView.h
//
//  Created by Ycq on 05/13/2019.
//  Copyright © 2019 NetEase Audio Lab. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NECollectionViewDisplayModel.h"

typedef void(^NECollectionViewSelectionBlock)(NECollectionViewDisplayModel *model);


@interface NECollectionView : UICollectionView

- (instancetype)initWithFrame:(CGRect)frame selectionBlock:(NECollectionViewSelectionBlock)selectionBlock;

- (void)reloadWithModelArray:(NSArray <NECollectionViewDisplayModel *> *)modelArray;

- (void)clearSelection;

@end


@interface NEFilterCollectionView: UICollectionView

- (instancetype)initWithFrame:(CGRect)frame selectionBlock:(NECollectionViewSelectionBlock)selectionBlock;

- (void)reloadWithModelArray:(NSArray <NECollectionViewDisplayModel *> *)modelArray;

- (void)clearSelection;

@end

