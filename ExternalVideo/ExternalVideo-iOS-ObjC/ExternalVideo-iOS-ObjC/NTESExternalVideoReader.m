//
//  NTESExternalVideoReader.m
//  ExternalVideo-iOS-ObjC
//
//  Created by Wenchao Ding on 2020/9/16.
//  Copyright © 2020 Wenchao Ding. All rights reserved.
//

#import "NTESExternalVideoReader.h"
#import <AVFoundation/AVFoundation.h>

@interface _NTESBlockObject : NSObject

@property (nonatomic, copy) void(^block)(void);

@end

@implementation _NTESBlockObject

+ (instancetype)objectWithBlock:(void(^)(void))block
{
    _NTESBlockObject *obj = [[_NTESBlockObject alloc] init];
    obj.block = block;
    return obj;
}

- (void)fire:(id)sender
{
    if (self.block) {
        self.block();
    }
}

@end

@interface NTESExternalVideoReader()

@property (nonatomic, strong) AVAssetReader *reader;
@property (nonatomic, strong) AVAssetReaderTrackOutput *output;
@property (nonatomic, strong) AVAsset *asset;
@property (nonatomic, strong) AVAssetTrack *track;

@property (nonatomic, assign) NSUInteger totalFramesWritten;
@property (nonatomic, assign) NSUInteger totalFrames;
@property (nonatomic, assign) NSInteger rotationDegree;

@property (nonatomic, strong) CADisplayLink *displayLink;

@end

@implementation NTESExternalVideoReader

- (instancetype)initWithURL:(NSURL *)URL error:(NSError * _Nullable __autoreleasing * _Nullable)error
{
    self = [super init];
    if (self) {
        self.asset = [[AVURLAsset alloc] initWithURL:URL options:nil];
        self.reader = [[AVAssetReader alloc] initWithAsset:self.asset error:error];
        if (!self.reader) {
            return nil;
        }
        self.track = [[self.asset tracksWithMediaType:AVMediaTypeVideo] firstObject];
        self.output = [[AVAssetReaderTrackOutput alloc] initWithTrack:self.track outputSettings:@ {
            (id)kCVPixelBufferPixelFormatTypeKey : @(kCVPixelFormatType_420YpCbCr8BiPlanarVideoRange)
        }];
        [self.reader addOutput:self.output];
        
        self.frameInterval = 2;  // FPS = 30
        self.rotationDegree = [self rotationDegreeForAsset:self.asset];
        self.totalFrames = [self numberOfFramesForAsset:self.asset];
        self.totalFramesWritten = 1;
    }
    return self;
}

- (void)dealloc
{
    [self.displayLink invalidate];
}

#pragma mark - Public methods

- (void)startReading
{
    __weak typeof(self) wself = self;
    self.displayLink = [CADisplayLink displayLinkWithTarget:[_NTESBlockObject objectWithBlock:^{
        [wself pollNextFrame];
    }] selector:@selector(fire:)];
    self.displayLink.frameInterval = 2;
    [self.displayLink addToRunLoop:NSRunLoop.currentRunLoop forMode:NSRunLoopCommonModes];
    [self.reader startReading];
}

#pragma mark - Private methods

- (void)pollNextFrame
{
    CMSampleBufferRef sampleBuffer = [self.output copyNextSampleBuffer];
    if (sampleBuffer) {
        if (self.delegate && [self.delegate respondsToSelector:@selector(videoReader:didReadSampleBuffer:totalFramesWritten:totalFrames:)]) {
            [self.delegate videoReader:self didReadSampleBuffer:sampleBuffer totalFramesWritten:self.totalFramesWritten++ totalFrames:self.totalFrames];
        }
        CMSampleBufferInvalidate(sampleBuffer);
        CFRelease(sampleBuffer);
    } else if (self.reader.status == AVAssetReaderStatusCompleted) {
        if (self.delegate && [self.delegate respondsToSelector:@selector(videoReaderDidFinishReading:)]) {
            [self.delegate videoReaderDidFinishReading:self];
        }
        [self.displayLink invalidate];
    }
}

- (CGFloat)rotationDegreeForAsset:(AVAsset *)asset
{
    AVAssetTrack *videoTrack = [[asset tracksWithMediaType:AVMediaTypeVideo] objectAtIndex:0];
    CGAffineTransform txf = [videoTrack preferredTransform];
    CGFloat videoAngleInDegree  = atan2(txf.b, txf.a) * 180 / M_PI;
    return videoAngleInDegree;
}

- (NSInteger)numberOfFramesForAsset:(AVAsset *)asset
{
    float durationInSeconds = CMTimeGetSeconds(asset.duration);
    float framesPerSecond = self.track.nominalFrameRate;
    float numberOfFrames = durationInSeconds * framesPerSecond;
    return numberOfFrames;
}

@end
