//
//  FUManager.m
//  FULiveDemo
//
//  Created by 刘洋 on 2017/8/18.
//  Copyright © 2017年 刘洋. All rights reserved.
//

#import "FUManager.h"
#import "authpack.h"
#import "FULiveModel.h"
#import <sys/utsname.h>
#import <CoreMotion/CoreMotion.h>

@interface FUManager ()
{
    int items[FUNamaHandleTotal];
}

@property (nonatomic, strong) dispatch_queue_t asyncLoadQueue;

@end

static FUManager *shareManager = NULL;

@implementation FUManager

+ (FUManager *)shareManager
{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        shareManager = [[FUManager alloc] init];
    });
    
    return shareManager;
}

- (instancetype)init
{
    if (self = [super init]) {
        _asyncLoadQueue = dispatch_queue_create("com.faceLoadItem", DISPATCH_QUEUE_SERIAL);
        /**这里新增了一个参数shouldCreateContext，设为YES的话，不用在外部设置context操作，我们会在内部创建并持有一个context。
         还有设置为YES,则需要调用FURenderer.h中的接口，不能再调用funama.h中的接口。*/
        
        CFAbsoluteTime startTime = CFAbsoluteTimeGetCurrent();

        [[FURenderer shareRenderer] setupWithData:nil dataSize:0 ardata:nil authPackage:&g_auth_package authSize:sizeof(g_auth_package) shouldCreateContext:YES];

        CFAbsoluteTime endTime = (CFAbsoluteTimeGetCurrent() - startTime);
        
        NSLog(@"---%lf",endTime);
    }
    
    return self;
}

#pragma mark -  加载bundle
/**加载美颜道具*/
- (void)loadFilter
{
    dispatch_async(_asyncLoadQueue, ^{
        if (self->items[FUNamaHandleTypeBeauty] == 0) {

            CFAbsoluteTime startTime = CFAbsoluteTimeGetCurrent();

            NSString *path = [[NSBundle mainBundle] pathForResource:@"face_beautification.bundle" ofType:nil];
            self->items[FUNamaHandleTypeBeauty] = [FURenderer itemWithContentsOfFile:path];

            /* 默认精细磨皮 */
            [FURenderer itemSetParam:self->items[FUNamaHandleTypeBeauty] withName:@"heavy_blur" value:@(0)];
            [FURenderer itemSetParam:self->items[FUNamaHandleTypeBeauty] withName:@"blur_type" value:@(2)];
            /* 默认自定义脸型 */
            [FURenderer itemSetParam:self->items[FUNamaHandleTypeBeauty] withName:@"face_shape" value:@(4)];
            
            CFAbsoluteTime endTime = (CFAbsoluteTimeGetCurrent() - startTime);

            NSLog(@"加载美颜道具耗时: %f ms", endTime * 1000.0);
     
        }
    });
}

/**将道具绘制到pixelBuffer*/
- (CVPixelBufferRef)renderItemsToPixelBuffer:(CVPixelBufferRef)pixelBuffer
{
    CVPixelBufferRef buffer = [[FURenderer shareRenderer] renderPixelBuffer:pixelBuffer withFrameId:0 items:items itemCount:sizeof(items)/sizeof(int) flipx:YES];//flipx 参数设为YES可以使道具做水平方向的镜像翻转
    
    return buffer;
}

@end
