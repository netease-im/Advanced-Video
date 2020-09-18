//
//  NTESExternalVideoReader.h
//  ExternalVideo-iOS-Objc
//
//  Created by Wenchao Ding on 2020/9/16.
//  Copyright © 2020 Wenchao Ding. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreMedia/CoreMedia.h>

@class NTESExternalVideoReader;

NS_ASSUME_NONNULL_BEGIN

@protocol NTESExternalVideoProcessorDelegate <NSObject>

- (void)videoProcessor:(NTESExternalVideoReader *)processor didReadSampleBuffer:(CMSampleBufferRef)sampleBuffer;

@end

@interface NTESExternalVideoReader : NSObject

@property (nonatomic, weak) id<NTESExternalVideoProcessorDelegate> delegate;

- (void)startReading;

@end

NS_ASSUME_NONNULL_END
