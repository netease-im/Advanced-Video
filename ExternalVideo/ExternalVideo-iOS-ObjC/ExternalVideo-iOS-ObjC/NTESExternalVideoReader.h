//
//  NTESExternalVideoReader.h
//  ExternalVideo-iOS-ObjC
//
//  Created by Wenchao Ding on 2020/9/16.
//  Copyright © 2020 Wenchao Ding. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreMedia/CoreMedia.h>

@class NTESExternalVideoReader;

NS_ASSUME_NONNULL_BEGIN

@protocol NTESExternalVideoReaderDelegate <NSObject>

@required
/**
 Called by every frame interval seconds.
 
 @param videoReader The corresponding NTESExternalVideoReader object.
 @param sampleBuffer The video frame with CMSampleBufferRef type.
 @param totalFramesWritten The total  number of frames read.
 @param totalFrames The total number of frames for the video.
 */
- (void)videoReader:(NTESExternalVideoReader *)videoReader didReadSampleBuffer:(CMSampleBufferRef)sampleBuffer totalFramesWritten:(NSUInteger)totalFramesWritten totalFrames:(NSUInteger)totalFrames;

@optional

/**
 Called when the video is completely read.
 
 @param videoReader The corresponding NTESExternalVideoReader object.
 */
- (void)videoReaderDidFinishReading:(NTESExternalVideoReader *)videoReader;

@end

@interface NTESExternalVideoReader : NSObject

@property (nonatomic, weak) id<NTESExternalVideoReaderDelegate> delegate;

/**
 Interval seconds between 2 frames, which is 60 divided by FPS. Default is 2, that is FPS is 30.
 */
@property (nonatomic, assign) NSTimeInterval frameInterval;

/**
 The URL of the video.
 */
@property (nonatomic, readonly) NSURL *URL;

/**
 The rotation degree of the video.
 */
@property (nonatomic, readonly) NSInteger rotationDegree;

/**
 Initialize a reader by video URL.
 
 @param URL The video URL.
 @param error An error object. Will be nil if no error occurs.
 @return An instance of NTESEternalVideoReader. Will be nil if error occurs.
 */
- (nullable instancetype)initWithURL:(NSURL *)URL error:(NSError * _Nullable * _Nullable)error;

/**
 Start reading frames.
 */
- (void)startReading;

/**
 Stop reading frames.
 */
- (void)stopReading;

@end

NS_ASSUME_NONNULL_END
