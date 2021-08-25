//
//  NTESMediaStatsViewController.m
//  MediaStats-iOS-Objc
//
//  Created by Wenchao Ding on 2020/9/15.
//  Copyright © 2020 Wenchao Ding. All rights reserved.
//

#import "NTESMediaStatsViewController.h"
#import <NERtcSDK/NERtcSDK.h>

@interface NTESMediaStatsViewController () <NERtcEngineMediaStatsObserver>

@property (strong, nonatomic) IBOutlet UILabel *remoteResolutionLabel;    // 下行分辨率
@property (strong, nonatomic) IBOutlet UILabel *remoteFrameRateLabel;     // 下行帧率
@property (strong, nonatomic) IBOutlet UILabel *remoteVideoJitterLabel;   // 下行视频抖动
@property (strong, nonatomic) IBOutlet UILabel *remoteRttLabel;  // 下行延时
@property (strong, nonatomic) IBOutlet UILabel *remoteVideoPacketLossLabel; // 下行视频丢包数
@property (strong, nonatomic) IBOutlet UILabel *remoteVideoPacketLossRateLabel; // 下行视频丢包率
@property (strong, nonatomic) IBOutlet UILabel *remoteVideoBitRateLabel; // 下行视频码率
@property (strong, nonatomic) IBOutlet UILabel *remoteAudioJitterLabel; // 下行音频抖动
@property (strong, nonatomic) IBOutlet UILabel *remoteAudioPacketLossLabel; // 下行音频丢包数
@property (strong, nonatomic) IBOutlet UILabel *remoteAudioPacketLossRateLabel; // 下行音频丢包率
@property (strong, nonatomic) IBOutlet UILabel *remoteAudioBitRateLabel; // 下行音频码率

@property (strong, nonatomic) IBOutlet UILabel *localResolutionLabel;    // 上行分辨率
@property (strong, nonatomic) IBOutlet UILabel *localFrameRateLabel;    // 上行帧率
@property (strong, nonatomic) IBOutlet UILabel *localVideoJitterLabel;  // 上行视频抖动
@property (strong, nonatomic) IBOutlet UILabel *localRttLabel;  // 上行延时
@property (strong, nonatomic) IBOutlet UILabel *localVideoPacketLossLabel; // 上行视频丢包数
@property (strong, nonatomic) IBOutlet UILabel *localVideoPacketLossRateLabel; // 上行视频丢包率
@property (strong, nonatomic) IBOutlet UILabel *localVideoBitRateLabel; // 上行视频码率
@property (strong, nonatomic) IBOutlet UILabel *localAudioJitterLabel; // 上行音频抖动
@property (strong, nonatomic) IBOutlet UILabel *localAudioPacketLossLabel; // 上行音频丢包数
@property (strong, nonatomic) IBOutlet UILabel *localAudioPacketLossRateLabel; // 上行音频丢包率
@property (strong, nonatomic) IBOutlet UILabel *localAudioBitRateLabel; // 上行音频码率

@end

@implementation NTESMediaStatsViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [NERtcEngine.sharedEngine addEngineMediaStatsObserver:self];
}

- (void)onRtcStats:(NERtcStats *)stat
{
    self.remoteVideoJitterLabel.text = [NSString stringWithFormat:@"%@ms", @(stat.rxVideoJitter)];
    self.remoteRttLabel.text = [NSString stringWithFormat:@"%@ms", @(stat.upRtt)];
    self.remoteVideoPacketLossLabel.text = @(stat.rxVideoPacketLossSum).stringValue;
    self.remoteVideoPacketLossRateLabel.text = [NSString stringWithFormat:@"%@%%",@(stat.rxVideoPacketLossRate)];
    self.remoteVideoBitRateLabel.text = [NSString stringWithFormat:@"%@ kbps",@(stat.rxVideoKBitRate)];
    self.remoteAudioJitterLabel.text = [NSString stringWithFormat:@"%@ms",@(stat.rxAudioJitter)];
    self.remoteAudioPacketLossLabel.text = @(stat.rxAudioPacketLossSum).stringValue;
    self.remoteAudioPacketLossRateLabel.text = [NSString stringWithFormat:@"%@%%",@(stat.rxAudioPacketLossRate)];
    self.remoteAudioBitRateLabel.text = [NSString stringWithFormat:@"%@ kbps",@(stat.rxAudioKBitRate)];

    self.localVideoJitterLabel.text = [NSString stringWithFormat:@"%@ms", @(stat.txVideoJitter)];
    self.localRttLabel.text = [NSString stringWithFormat:@"%@ms", @(stat.upRtt)];
    self.localVideoPacketLossLabel.text = @(stat.txVideoPacketLossSum).stringValue;
    self.localVideoPacketLossRateLabel.text =[NSString stringWithFormat:@"%@%%",@(stat.txVideoPacketLossRate)];
    self.localVideoBitRateLabel.text = [NSString stringWithFormat:@"%@ kbps",@(stat.txVideoKBitRate)];
    self.localAudioJitterLabel.text = [NSString stringWithFormat:@"%@ms", @(stat.txAudioJitter)];
    self.localAudioPacketLossLabel.text = @(stat.txAudioPacketLossSum).stringValue;
    self.localAudioPacketLossRateLabel.text = [NSString stringWithFormat:@"%@%%",@(stat.txAudioPacketLossRate)];
    self.localAudioBitRateLabel.text = [NSString stringWithFormat:@"%@ kbps",@(stat.txAudioKBitRate)];
}

- (void)onLocalVideoStat:(NERtcVideoSendStats *)stat
{
    NERtcVideoLayerSendStats *videoLayerSendStats = stat.videoLayers.lastObject;
    if (!videoLayerSendStats)
        return;
    
    self.localResolutionLabel.text = [NSString stringWithFormat:@"%@x%@",@(videoLayerSendStats.height),@(videoLayerSendStats.width)];
    self.localFrameRateLabel.text = [NSString stringWithFormat:@"%@fps", @(videoLayerSendStats.sentFrameRate)];
}

- (void)onRemoteVideoStats:(NSArray<NERtcVideoRecvStats *> *)stats
{
    NERtcVideoRecvStats *stat = stats.lastObject;
    NERtcVideoLayerRecvStats *videoLayerRecvStats = stat.videoLayers.lastObject;
    if (!videoLayerRecvStats)
        return;
    
    self.remoteResolutionLabel.text = [NSString stringWithFormat:@"%@x%@",@(videoLayerRecvStats.height),@(videoLayerRecvStats.width)];
    self.remoteFrameRateLabel.text = [NSString stringWithFormat:@"%@fps", @(videoLayerRecvStats.rendererOutputFrameRate)];
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return section == 0 ? UITableViewAutomaticDimension : CGFLOAT_MIN;
}

- (BOOL)tableView:(UITableView *)tableView shouldHighlightRowAtIndexPath:(NSIndexPath *)indexPath
{
    return indexPath.section == 2;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.section == 2) {
        [self dismissViewControllerAnimated:YES completion:nil];
    }
}

- (void)dealloc
{
    [NERtcEngine.sharedEngine removeEngineMediaStatsObserver:self];
}

@end
