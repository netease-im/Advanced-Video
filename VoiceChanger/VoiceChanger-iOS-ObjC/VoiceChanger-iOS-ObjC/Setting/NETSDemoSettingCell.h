//
//  NETSDemoSettingCell.h
//  VoiceChanger-iOS-ObjC
//
//  Created by Think on 2020/11/5.
//  Copyright © 2020 丁文超. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

///
/// 音效配置check类型cell
///
@interface NETSDemoSettingCheckCell : UITableViewCell

///
/// 创建设置cell
/// @param tableView    - cell所属tableView
/// @param indexPath    - cell所处位置
/// @param datas        - tableView数据集合
/// @param selectVal    - tableView默认选中项
///
+ (NETSDemoSettingCheckCell *)cellWithTableView:(UITableView *)tableView
                                      indexPath:(NSIndexPath *)indexPath
                                          datas:(NSArray *)datas
                                      selectVal:(NSInteger)selectVal;

///
/// 返回设置cell高度
/// @param tableView    - cell所属tableView
/// @param indexPath    - cell所处位置
/// @param datas        - tableView数据集合
///
+ (CGFloat)heightForTableView:(UITableView *)tableView
                    indexPath:(NSIndexPath *)indexPath
                        datas:(NSArray *)datas;

@end

///
/// 音效配置slide类型cell
///
@interface NETSDemoSettingSlideCell : UITableViewCell

///
/// 创建设置cell
/// @param tableView    - cell所属tableView
/// @param indexPath    - cell所处位置
/// @param datas        - tableView数据集合
///
+ (NETSDemoSettingSlideCell *)cellWithTableView:(UITableView *)tableView
                                      indexPath:(NSIndexPath *)indexPath
                                          datas:(NSArray *)datas;

///
/// 返回设置cell高度
/// @param tableView    - cell所属tableView
/// @param indexPath    - cell所处位置
/// @param datas        - tableView数据集合
///
+ (CGFloat)heightForTableView:(UITableView *)tableView
                    indexPath:(NSIndexPath *)indexPath
                        datas:(NSArray *)datas;

@end

NS_ASSUME_NONNULL_END
