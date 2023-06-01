//
//  NECollectionViewCell.h
//
//  Created by Ycq on 05/13/2019.
//  Copyright © 2019 NetEase Audio Lab. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NECollectionViewDisplayModel.h"


@interface NECollectionViewCell : UICollectionViewCell

- (void)updateWithModel:(NECollectionViewDisplayModel *)model;

@end


@interface NEFilterCollectionViewCell : UICollectionViewCell

- (void)updateWithModel:(NECollectionViewDisplayModel *)model;

@end

