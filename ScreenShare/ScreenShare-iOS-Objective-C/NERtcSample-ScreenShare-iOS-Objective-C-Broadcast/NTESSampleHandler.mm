//
//  NTESSampleHandler.m
//  NERtcScreenShareSample-Broadcast
//
//  Created by 丁文超 on 2020/7/14.
//  Copyright © 2020 丁文超. All rights reserved.
//


#import "NTESSampleHandler.h"
#import "libyuv.h"
#import "NTESYUVConverter.h"
#import "NTESI420Frame.h"

static NSString * _Nonnull kAppGroup = @"group.com.netease.nmc.NERtcSample-ScreenShare-iOS-Objective-C"; //!< 需要替换成自己的App Group

@interface NTESSampleHandler()

@property (nonatomic, strong) NSUserDefaults *userDefautls;

@end

@implementation NTESSampleHandler

- (void)broadcastStartedWithSetupInfo:(NSDictionary<NSString *,NSObject *> *)setupInfo {
    self.userDefautls = [[NSUserDefaults alloc] initWithSuiteName:kAppGroup];
}

- (void)broadcastPaused {
    // User has requested to pause the broadcast. Samples will stop being delivered.
}

- (void)broadcastResumed {
    // User has requested to resume the broadcast. Samples delivery will resume.
}

- (void)broadcastFinished {
    // User has requested to finish the broadcast.
}

- (void)processSampleBuffer:(CMSampleBufferRef)sampleBuffer withType:(RPSampleBufferType)sampleBufferType {
    
    switch (sampleBufferType) {
        case RPSampleBufferTypeVideo: {
            @autoreleasepool {
                CVPixelBufferRef pixelBuffer = CMSampleBufferGetImageBuffer(sampleBuffer);
                
                float cropRate = (float)CVPixelBufferGetWidth(pixelBuffer) / (float)CVPixelBufferGetHeight(pixelBuffer);
                CGSize targetSize = CGSizeMake(720, 720 / cropRate);
                NTESVideoPackOrientation targetOrientation = NTESVideoPackOrientationPortrait;
                if (@available(iOS 11.0, *)) {
                    CFStringRef RPVideoSampleOrientationKeyRef = (__bridge CFStringRef)RPVideoSampleOrientationKey;
                    NSNumber *orientation = (NSNumber *)CMGetAttachment(sampleBuffer, RPVideoSampleOrientationKeyRef,NULL);
                    if (orientation.integerValue == kCGImagePropertyOrientationUp ||
                        orientation.integerValue == kCGImagePropertyOrientationUpMirrored) {
                        targetOrientation = NTESVideoPackOrientationPortrait;
                    } else if(orientation.integerValue == kCGImagePropertyOrientationDown ||
                              orientation.integerValue == kCGImagePropertyOrientationDownMirrored) {
                        targetOrientation = NTESVideoPackOrientationPortraitUpsideDown;
                    } else if (orientation.integerValue == kCGImagePropertyOrientationLeft ||
                               orientation.integerValue == kCGImagePropertyOrientationLeftMirrored) {
                        targetOrientation = NTESVideoPackOrientationLandscapeLeft;
                    } else if (orientation.integerValue == kCGImagePropertyOrientationRight ||
                               orientation.integerValue == kCGImagePropertyOrientationRightMirrored) {
                        targetOrientation = NTESVideoPackOrientationLandscapeRight;
                    }
                }
                NTESI420Frame *videoFrame = [NTESYUVConverter pixelBufferToI420:pixelBuffer
                                                                       withCrop:cropRate
                                                                     targetSize:targetSize
                                                                 andOrientation:targetOrientation];
                
//                NSDictionary *frame = [self createI420VideoFrameFromPixelBuffer:pixelBuffer];
                NSDictionary *frame = @{
                    @"width": @(videoFrame.width),
                    @"height": @(videoFrame.height),
                    @"data": [videoFrame bytes],
                    @"timestamp": @(CACurrentMediaTime() * 1000)
                };
                [self.userDefautls setObject:frame forKey:@"frame"];
                [self.userDefautls synchronize];
            }
            break;
        }
        case RPSampleBufferTypeAudioApp:
            // Handle audio sample buffer for app audio
            break;
        case RPSampleBufferTypeAudioMic:
            // Handle audio sample buffer for mic audio
            break;
            
        default:
            break;
    }
}

//- (NSDictionary *)createI420VideoFrameFromPixelBuffer:(CVPixelBufferRef)pixelBuffer
//{
//    CVPixelBufferLockBaseAddress(pixelBuffer, 0);
//
//    // 转I420
//    int psrc_w = (int)CVPixelBufferGetWidth(pixelBuffer);
//    int psrc_h = (int)CVPixelBufferGetHeight(pixelBuffer);
//    uint8 *src_y = (uint8 *)CVPixelBufferGetBaseAddressOfPlane(pixelBuffer, 0);
//    uint8 *src_uv = (uint8 *)CVPixelBufferGetBaseAddressOfPlane(pixelBuffer, 1);
//    int y_stride = (int)CVPixelBufferGetBytesPerRowOfPlane(pixelBuffer, 0);
//    int uv_stride = (int)CVPixelBufferGetBytesPerRowOfPlane(pixelBuffer, 1);
//    uint8 *i420_buf = (uint8 *)malloc((psrc_w * psrc_h * 3) >> 1);
//
//    libyuv::NV12ToI420(&src_y[0],                              y_stride,
//                       &src_uv[0],                             uv_stride,
//                       &i420_buf[0],                           psrc_w,
//                       &i420_buf[psrc_w * psrc_h],             psrc_w >> 1,
//                       &i420_buf[(psrc_w * psrc_h * 5) >> 2],  psrc_w >> 1,
//                       psrc_w, psrc_h);
//
//    // 缩放至720
//    int pdst_w = 720;
//    int pdst_h = psrc_h * (pdst_w/(double)psrc_w);
//    libyuv::FilterMode filter = libyuv::kFilterNone;
//    uint8 *pdst_buf = (uint8 *)malloc((pdst_w * pdst_h * 3) >> 1);
//    libyuv::I420Scale(&i420_buf[0],                          psrc_w,
//                      &i420_buf[psrc_w * psrc_h],            psrc_w >> 1,
//                      &i420_buf[(psrc_w * psrc_h * 5) >> 2], psrc_w >> 1,
//                      psrc_w, psrc_h,
//                      &pdst_buf[0],                          pdst_w,
//                      &pdst_buf[pdst_w * pdst_h],            pdst_w >> 1,
//                      &pdst_buf[(pdst_w * pdst_h * 5) >> 2], pdst_w >> 1,
//                      pdst_w, pdst_h,
//                      filter);
//
//    free(i420_buf);
//
//    CVPixelBufferUnlockBaseAddress(pixelBuffer, 0);
//
//    NSUInteger dataLength = pdst_w * pdst_h * 3 >> 1;
//    NSData *data = [NSData dataWithBytesNoCopy:pdst_buf length:dataLength];
//
//    NSDictionary *frame = @{
//        @"width": @(pdst_w),
//        @"height": @(pdst_h),
//        @"data": data,
//        @"timestamp": @(CACurrentMediaTime() * 1000)
//    };
//    return frame;
//}


@end
