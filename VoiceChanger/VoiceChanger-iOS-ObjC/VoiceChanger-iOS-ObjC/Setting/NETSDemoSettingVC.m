//
//  NETSDemoSettingVC.m
//  VoiceChanger-iOS-ObjC
//
//  Created by Think on 2020/11/5.
//  Copyright © 2020 丁文超. All rights reserved.
//

#import "NETSDemoSettingVC.h"
#import "NETSDemoSettingCell.h"
#import "NETSDemoSoundConfig.h"
#import <NERtcSDK/NERtcSDK.h>

@interface NETSDemoSettingVC () <UITableViewDelegate, UITableViewDataSource>

@property (nonatomic, strong)   UITableView         *tableView;
@property (nonatomic, strong)   NETSDemoSettingVM   *viewModel;

@end

@implementation NETSDemoSettingVC

- (instancetype)initWithType:(NETSDemoSettingType)type
{
    self = [super init];
    if (self) {
        _viewModel = [[NETSDemoSettingVM alloc] initWithType:type];
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.title = _viewModel.title;
    self.view.backgroundColor = [UIColor whiteColor];
    if (_viewModel.type == NETSDemoSettingCustomEqualizer) {
        self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        UIBarButtonItem *rightBar = [[UIBarButtonItem alloc] initWithTitle:@"完成" style:UIBarButtonItemStylePlain target:self action:@selector(finishSetting)];
        self.navigationItem.rightBarButtonItem = rightBar;
    }
    [self.view addSubview:self.tableView];
}

- (void)dealloc
{
    NSLog(@"%@ dealloc...", [[self class] description]);
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    [self.navigationController setNavigationBarHidden:NO animated:YES];
    [self.tableView reloadData];
}

/// 自定义 EQ 设置结束, 点击触发设置生效
- (void)finishSetting
{
    if (_viewModel.type != NETSDemoSettingCustomEqualizer) {
        return;
    }
    NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:[_viewModel.datas count]];
    for (NETSDemoSettingSlideModel *model in _viewModel.datas) {
        [tmp addObject:@(model.value)];
    }
    int res = [[NERtcEngine sharedEngine] setLocalVoiceEqualizations:[tmp copy]];
    if (0 == res) {
        [NETSDemoSoundConfig shared].equalization = 3; // 标记EQ为自定义选项
        [NETSDemoSoundConfig shared].customEqualization = [_viewModel.datas copy];
        [self.navigationController popViewControllerAnimated:YES];
    } else {
        NSLog(@"自定义设置均衡器失败, Err: %d", res);
    }
}

#pragma mark - UITableView delegate

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [_viewModel.datas count];
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (_viewModel.type == NETSDemoSettingCustomEqualizer) {
        // 返回自定义 EQ 配置cell高度
        return [NETSDemoSettingSlideCell heightForTableView:tableView indexPath:indexPath datas:_viewModel.datas];
    }
    // 返回其他配置类型cell高度
    return [NETSDemoSettingCheckCell heightForTableView:tableView indexPath:indexPath datas:_viewModel.datas];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSInteger selectVal = -1;
    switch (_viewModel.type) {
        case NETSDemoSettingEqualizer:
        {
            selectVal = [NETSDemoSoundConfig shared].equalization;
            return [NETSDemoSettingCheckCell cellWithTableView:tableView indexPath:indexPath datas:_viewModel.datas selectVal:selectVal];
        }
            break;
        case NETSDemoSettingCustomEqualizer:
        {
            return [NETSDemoSettingSlideCell cellWithTableView:tableView indexPath:indexPath datas:_viewModel.datas];
        }
            break;
        case NETSDemoSettingMixing:
        {
            selectVal = [NETSDemoSoundConfig shared].reverb;
            return [NETSDemoSettingCheckCell cellWithTableView:tableView indexPath:indexPath datas:_viewModel.datas selectVal:selectVal];
        }
            break;
        case NETSDemoSettingChange:
        {
            selectVal = [NETSDemoSoundConfig shared].changer;
            return [NETSDemoSettingCheckCell cellWithTableView:tableView indexPath:indexPath datas:_viewModel.datas selectVal:selectVal];
        }
            break;
            
        default:
            return [UITableViewCell new];
            break;
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if ([_viewModel.datas count] <= indexPath.row) {
        return;
    }
    
    NETSDemoSettingCheckModel *model = _viewModel.datas[indexPath.row];
    switch (_viewModel.type) {
        case NETSDemoSettingEqualizer:
        {
            if ([model.title isEqualToString:@"自定义"]) {
                // 跳转至自定义 EQ 设置页面
                NETSDemoSettingVC *vc = [[NETSDemoSettingVC alloc] initWithType:NETSDemoSettingCustomEqualizer];
                [self.navigationController pushViewController:vc animated:YES];
            } else {
                // 设置 EQ
                NSInteger value = model.value;
                int res = [[NERtcEngine sharedEngine] setLocalVoiceEqualizationPreset:value];
                if (0 == res) {
                    [NETSDemoSoundConfig shared].equalization = value;
                    [self.tableView reloadData];
                } else {
                    NSLog(@"设置均衡器失败, Err: %d", res);
                }
            }
        }
            break;
        case NETSDemoSettingMixing:
        {
            // 设置 混音
            NSInteger value = model.value;
            int res = [[NERtcEngine sharedEngine] setLocalVoiceReverbPreset:value];
            if (0 == res) {
                [NETSDemoSoundConfig shared].reverb = value;
                [self.tableView reloadData];
            } else {
                NSLog(@"设置混响失败, Err: %d", res);
            }
        }
            break;
        case NETSDemoSettingChange:
        {
            // 设置 变声/美声
            NSInteger value = model.value;
            int res = [[NERtcEngine sharedEngine] setLocalVoiceChangerPreset:value];
            if (0 == res) {
                [NETSDemoSoundConfig shared].changer = value;
                [self.tableView reloadData];
            } else {
                NSLog(@"设置变声失败, Err: %d", res);
            }
        }
            break;
            
        default:
            break;
    }
}

#pragma mark - lazy load

- (UITableView *)tableView
{
    if (!_tableView) {
        _tableView = [[UITableView alloc] initWithFrame:self.view.bounds];
        _tableView.delegate = self;
        _tableView.dataSource = self;
        _tableView.rowHeight = 44;
        [_tableView registerClass:[NETSDemoSettingCheckCell class] forCellReuseIdentifier:[NETSDemoSettingCheckCell description]];
        [_tableView registerClass:[NETSDemoSettingSlideCell class] forCellReuseIdentifier:[NETSDemoSettingSlideCell description]];
    }
    return _tableView;
}

@end
