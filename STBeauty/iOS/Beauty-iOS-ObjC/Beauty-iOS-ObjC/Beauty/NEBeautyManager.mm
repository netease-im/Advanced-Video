//
//  NEBeautyManager.m
//  Beauty-iOS-ObjC
//
//  Created by zhangchenliang on 2021/10/12.
//  Copyright © 2021 NetEase. All rights reserved.
//

#import "NEBeautyManager.h"
#import "NEBeautyConfigView.h"

#import <NERtcSDK/NERtcSDK.h>
#import <SSZipArchive/SSZipArchive.h>

// ST_SDK
#import "EffectsProcess.h"

@interface NEBeautyManager ()<NEBeautyConfigViewDataSource, NEBeautyConfigViewDelegate>

@property (nonatomic, strong) NSMutableDictionary<NSNumber*, NEBeautyConfigView*> *menuMap;

// 标题tab数据源
@property (nonatomic, strong) NSArray<NETitleDisplayModel *> *beautyTitleModelArray;

// 美颜UI数据源
@property (nonatomic, strong) NSArray<NEBeautySliderDisplayModel *> *baseSliderModelArray;
@property (nonatomic, strong) NSArray<NEBeautySliderDisplayModel *> *shapeSliderModelArray;
@property (nonatomic, strong) NSArray<NEBeautySliderDisplayModel *> *advancedSliderModelArray;
@property (nonatomic, strong) NSArray<NEBeautySliderDisplayModel *> *advancedSliderModelArray2;
@property (nonatomic, strong) NSArray<NEBeautySliderDisplayModel *> *advancedSliderModelArray3;

@property (nonatomic, assign) BOOL beautyEnabled;

// ST_SDK
@property (nonatomic, strong) EAGLContext* glContext;
@property (nonatomic, strong) EffectsProcess* stEffectProcess;
@property (nonatomic, assign) BOOL stModelLoaded;

@end

@implementation NEBeautyManager {
//    float _filterStrength;
    
    // ST_SDK
    GLuint _currentFrameWidth;
    GLuint _currentFrameHeight;
    GLuint _outTexture;
    CVPixelBufferRef _outputPixelBuffer;
    CVOpenGLESTextureRef _outputCVTexture;
    unsigned char* _outputBuffer;
}

#pragma mark - Life Cycle

- (instancetype)init {
    self = [super init];
    if (self) {
        // ST_SDK
        _currentFrameWidth = 0;
        _currentFrameHeight = 0;
        _outTexture = 0;
        _outputPixelBuffer = NULL;
        _outputCVTexture = NULL;
        _outputBuffer = NULL;
    }
    
    return self;
}

#pragma mark - Public

+ (instancetype)sharedManager {
    static NEBeautyManager *manager = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        manager = [[NEBeautyManager alloc] init];
    });
    
    return manager;
}

- (void)enableBeauty:(BOOL)enable {
    // 开启/关闭美颜效果
    self.beautyEnabled = enable;
}

- (void)displayMenuWithType:(NEBeautyConfigViewType)type container:(UIView *)container {
    NEBeautyConfigView *view = [self.menuMap objectForKey:@(type)];
    if (!view) {
        view = [[NEBeautyConfigView alloc] initWithType:type dataSource:self delegate:self];
        [self.menuMap setObject:view forKey:@(type)];
    }
    [view displayWithContainer:container];
}

- (void)dismissMenuWithType:(NEBeautyConfigViewType)type {
    NEBeautyConfigView *view = [self.menuMap objectForKey:@(type)];
    if (!view) {
        return;
    }
    [view dismiss];
}

// ST_SDK
- (void)initSTSDK {
    // 鉴权
    NSString* licensePath = [[NSBundle mainBundle] pathForResource:@"license" ofType:@"lic"];
    BOOL result = [EffectsProcess authorizeWithLicensePath:licensePath];
    if (!result) {
        NSLog(@"***** error: license is invalid *****");
    }
    
    // 初始化 EAGLContext
    self.glContext = [[EAGLContext alloc] initWithAPI:kEAGLRenderingAPIOpenGLES3];
    
    // 初始化 EffectProcess
    self.stEffectProcess = [[EffectsProcess alloc] initWithType:EffectsTypePreview glContext:self.glContext];
    
    // 添加 model
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        self.stModelLoaded = NO;
        NSString* modelPath = [[NSBundle mainBundle] pathForResource:@"model" ofType:@"bundle"];
        [self.stEffectProcess setModelPath:modelPath];
        self.stModelLoaded = YES;
    });
}

- (void)destroySTSDK {
    self.stModelLoaded = NO;
    self.stEffectProcess = nil;
    self.glContext = nil;
    
    _currentFrameWidth = 0;
    _currentFrameHeight = 0;
    
    if (_outTexture) {
        _outTexture = 0;
        
        CVPixelBufferRelease(_outputPixelBuffer);
        _outputPixelBuffer = NULL;
        
        CFRelease(_outputCVTexture);
        _outputCVTexture = NULL;
    }
    
    if (_outputBuffer) {
        free(_outputBuffer);
        _outputBuffer = NULL;
    }
}

- (void)processCapturedVideoFrameWithPixelBuffer:(CVPixelBufferRef)pixelBuffer
                                        rotation:(NERtcVideoRotationType)rotation {
    if (!self.beautyEnabled) {
        return;
    }
    
    if (!self.stModelLoaded) {
        return;
    }
    
    CVPixelBufferRetain(pixelBuffer);
    CVPixelBufferLockBaseAddress(pixelBuffer, 0);
    
    st_rotate_type rotationType = ST_CLOCKWISE_ROTATE_0;
    switch (rotation) {
        case kNERtcVideoRotation_0:
            rotationType = ST_CLOCKWISE_ROTATE_0;
            break;
        case kNERtcVideoRotation_90:
            rotationType = ST_CLOCKWISE_ROTATE_90;
            break;
        case kNERtcVideoRotation_180:
            rotationType = ST_CLOCKWISE_ROTATE_180;
            break;
        case kNERtcVideoRotation_270:
            rotationType = ST_CLOCKWISE_ROTATE_270;
            break;
            
        default:
            break;
    }
    
    size_t width = CVPixelBufferGetWidth(pixelBuffer);
    size_t height = CVPixelBufferGetHeight(pixelBuffer);
    
    // craete texture if needed
    if (!_outTexture || _currentFrameWidth != width || _currentFrameHeight != height) {
        _currentFrameWidth = (uint32_t)width;
        _currentFrameHeight = (uint32_t)height;
        
        if (_outTexture) {
            CVPixelBufferRelease(_outputPixelBuffer);
            _outputPixelBuffer = NULL;
            CFRelease(_outputCVTexture);
            _outputCVTexture = NULL;
        }
        
        if (_outputBuffer) {
            free(_outputBuffer);
            _outputBuffer = NULL;
        }
        
        _outputBuffer = (unsigned char*)malloc(width * height * 3 / 2);
        
        [self.stEffectProcess createGLObjectWith:(int)width height:(int)height texture:&_outTexture pixelBuffer:&_outputPixelBuffer cvTexture:&_outputCVTexture];
    }
    
    // face detect
    st_mobile_human_action_t detectResult;
    memset(&detectResult, 0, sizeof(st_mobile_human_action_t));
    st_mobile_animal_result_t animalResult;
    memset(&animalResult, 0, sizeof(st_mobile_animal_result_t));
    st_result_t result = [self.stEffectProcess detectWithPixelBuffer:pixelBuffer
                                                              rotate:rotationType
                                                      cameraPosition:AVCaptureDevicePositionFront
                                                         humanAction:&detectResult
                                                        animalResult:&animalResult];
    
    // render
    result = [self.stEffectProcess renderPixelBuffer:pixelBuffer
                                              rotate:rotationType
                                         humanAction:detectResult
                                        animalResult:&animalResult
                                          outTexture:_outTexture
                                      outPixelFormat:ST_PIX_FMT_BGRA8888
                                             outData:nil];
    
    // convert rgb texture to nv12 buffer
    [self.stEffectProcess convertRGBATextureToNV12BufferWithTexture:_outTexture outputBuffer:_outputBuffer size:CGSizeMake(width, height)];
    
    // copy nv12 buffer to pixel buffer
    [self copyNV12BufferToPixelBufferWithBuffer:_outputBuffer width:(uint32_t)width height:(uint32_t)height pixelBuffer:pixelBuffer];
    
    CVPixelBufferUnlockBaseAddress(pixelBuffer, 0);
    CVPixelBufferRelease(pixelBuffer);
}

// st_sdk
- (void)copyNV12BufferToPixelBufferWithBuffer:(unsigned char*)buffer
                                        width:(uint32_t)width
                                       height:(uint32_t)height
                                  pixelBuffer:(CVPixelBufferRef)pixelBuffer {
    if (!buffer) {
        NSLog(@"%s, buffer is invalid", __func__);
        return;
    }
    
    OSType type = CVPixelBufferGetPixelFormatType(pixelBuffer);
    if (type != kCVPixelFormatType_420YpCbCr8BiPlanarFullRange &&
        type != kCVPixelFormatType_420YpCbCr8BiPlanarVideoRange) {
        NSLog(@"%s, pixel buffer format %u is not supported", __func__, type);
        return;
    }
    
    size_t pixelWidth = CVPixelBufferGetWidth(pixelBuffer);
    size_t pixelHeight = CVPixelBufferGetHeight(pixelBuffer);
    if (width != pixelWidth || height != pixelHeight) {
        NSLog(@"%s, pixel buffer width %zu or height %zu is invalid", __func__, pixelWidth, pixelHeight);
        return;
    }
    
    // y 分量
    unsigned char* yData = (unsigned char*)CVPixelBufferGetBaseAddressOfPlane(pixelBuffer, 0);
    size_t yBytesPerRow = CVPixelBufferGetBytesPerRowOfPlane(pixelBuffer, 0);
    
    // uv 分量
    unsigned char* uvData = (unsigned char*)CVPixelBufferGetBaseAddressOfPlane(pixelBuffer, 1);
    size_t uvBytesPerRow = CVPixelBufferGetBytesPerRowOfPlane(pixelBuffer, 1);
    
    if (width == yBytesPerRow) {
        size_t yLength = yBytesPerRow * height;
        size_t uvLength = uvBytesPerRow * height / 2;
        memcpy(yData, buffer, yLength);
        memcpy(uvData, buffer + yLength, uvLength);
    } else {
        // 逐行拷贝
        size_t yBufferLength = width * height;
        for (uint32_t i = 0; i < height; i++) {
            memcpy(yData + yBytesPerRow * i, buffer + width * i, width);
        }
        for (uint32_t i = 0; i < (height / 2); i++) {
            memcpy(uvData + uvBytesPerRow * i, buffer + yBufferLength + width * i, width);
        }
    }
}

#pragma mark - Private

- (float)convertValue:(float)value withRangeStride:(float)stride {
    return value - (stride / 2);
}

#pragma mark - NEBeautyConfigViewDelegate

- (void)didTriggerResetActionWithConfigViewType:(NEBeautyConfigViewType)type {
    switch (type) {
        case NEBeautyConfigViewTypeBeauty: {
            break;
        }
            
        default:
            break;
    }
}

- (void)didChangeSliderValueWithType:(NEBeautySliderType)type value:(float)value {
    switch (type) {
        case NEBeautySliderTypeWhiteTeeth: {
            [self.stEffectProcess setEffectType:EFFECT_BEAUTY_PLASTIC_WHITE_TEETH value:value];
            
            break;
        }
        case NEBeautySliderTypeLightEye: {
            [self.stEffectProcess setEffectType:EFFECT_BEAUTY_PLASTIC_BRIGHT_EYE value:value];
            
            break;
        }
        case NEBeautySliderTypeWhiten: {
            [self.stEffectProcess setEffectType:EFFECT_BEAUTY_BASE_WHITTEN value:value];
            
            break;
        }
        case NEBeautySliderTypeSmooth: {
            [self.stEffectProcess setEffectType:EFFECT_BEAUTY_BASE_FACE_SMOOTH value:value];
            
            break;
        }
        case NEBeautySliderTypeSmallNose: {
            [self.stEffectProcess setEffectType:EFFECT_BEAUTY_PLASTIC_NARROW_NOSE value:value];
            
            break;
        }
        case NEBeautySliderTypeEyeDis: {
            // EFFECT_BEAUTY_PLASTIC_EYE_DISTANCE [-1, 1]
            value = [self convertValue:value withRangeStride:2];
            [self.stEffectProcess setEffectType:EFFECT_BEAUTY_PLASTIC_EYE_DISTANCE value:value];
            
            break;
        }
        case NEBeautySliderTypeEyeAngle: {
            // EFFECT_BEAUTY_PLASTIC_EYE_ANGLE [-1, 1]
            value = [self convertValue:value withRangeStride:2];
            [self.stEffectProcess setEffectType:EFFECT_BEAUTY_PLASTIC_EYE_ANGLE value:value];
            
            break;
        }
        case NEBeautySliderTypeMouth: {
            // EFFECT_BEAUTY_PLASTIC_MOUTH_SIZE [-1, 1]
            value = [self convertValue:value withRangeStride:2];
            [self.stEffectProcess setEffectType:EFFECT_BEAUTY_PLASTIC_MOUTH_SIZE value:value];
            
            break;
        }
        case NEBeautySliderTypeBigEye: {
            [self.stEffectProcess setEffectType:EFFECT_BEAUTY_RESHAPE_ENLARGE_EYE value:value];
            
            break;
        }
        case NEBeautySliderTypeSmallFace: {
            [self.stEffectProcess setEffectType:EFFECT_BEAUTY_RESHAPE_SHRINK_JAW value:value];
            
            break;
        }
        case NEBeautySliderTypeJaw: {
            // EFFECT_BEAUTY_PLASTIC_CHIN_LENGTH [-1, 1]
            value = [self convertValue:value withRangeStride:2];
            [self.stEffectProcess setEffectType:EFFECT_BEAUTY_PLASTIC_CHIN_LENGTH value:value];
            
            break;
        }
        case NEBeautySliderTypeThinFace: {
            [self.stEffectProcess setEffectType:EFFECT_BEAUTY_RESHAPE_SHRINK_FACE value:value];
            
            break;
        }
        case NEBeautySliderTypeFaceRuddy: {
            [self.stEffectProcess setEffectType:EFFECT_BEAUTY_BASE_REDDEN value:value];
            
            break;
        }
        case NEBeautySliderTypeLongNose: {
            // EFFECT_BEAUTY_PLASTIC_NOSE_LENGTH [-1, 1]
            value = [self convertValue:value withRangeStride:2];
            [self.stEffectProcess setEffectType:EFFECT_BEAUTY_PLASTIC_NOSE_LENGTH value:value];
            
            break;
        }
        case NEBeautySliderTypeRenZhong: {
            // EFFECT_BEAUTY_PLASTIC_PHILTRUM_LENGTH [-1, 1]
            value = [self convertValue:value withRangeStride:2];
            [self.stEffectProcess setEffectType:EFFECT_BEAUTY_PLASTIC_PHILTRUM_LENGTH value:value];
            
            break;
        }
        case NEBeautySliderTypeMouthAngle: {
            // not supported
            
            break;
        }
        case NEBeautySliderTypeRoundEye: {
            [self.stEffectProcess setEffectType:EFFECT_BEAUTY_RESHAPE_ROUND_EYE value:value];
            
            break;
        }
        case NEBeautySliderTypeOpenEyeAngle: {
            [self.stEffectProcess setEffectType:EFFECT_BEAUTY_PLASTIC_OPEN_CANTHUS value:value];
            
            break;
        }
        case NEBeautySliderTypeVFace: {
            // not supported
            
            break;
        }
        case NEBeautySliderTypeThinUnderjaw: {
            [self.stEffectProcess setEffectType:EFFECT_BEAUTY_PLASTIC_SHRINK_JAWBONE value:value];
            
            break;
        }
        case NEBeautySliderTypeNarrowFace: {
            [self.stEffectProcess setEffectType:EFFECT_BEAUTY_RESHAPE_NARROW_FACE value:value];
            
            break;
        }
        case NEBeautySliderTypeCheekBone: {
            [self.stEffectProcess setEffectType:EFFECT_BEAUTY_PLASTIC_SHRINK_CHEEKBONE value:value];
            
            break;
        }
        case NEBeautySliderTypeFaceSharpen: {
            [self.stEffectProcess setEffectType:EFFECT_BEAUTY_TONE_SHARPEN value:value];
            
            break;
        }
            
        default:
            break;
    }
}

#pragma mark - NEBeautyConfigViewDataSource

- (NSArray<NETitleDisplayModel *> *)titleModelArrayForConfigViewWithType:(NEBeautyConfigViewType)type {
    switch (type) {
        case NEBeautyConfigViewTypeBeauty: {
            return self.beautyTitleModelArray;
        }
            
        default: {
            return nil;
        }
    }
}

- (NSArray<NEBeautySliderDisplayModel *> *)sliderModelArrayForTitleType:(NEBeautyEffectType)type {
    switch (type) {
        case NEBeautyEffectTypeBeautyBase: {
            return self.baseSliderModelArray;
        }
        case NEBeautyEffectTypeBeautyShape: {
            return self.shapeSliderModelArray;
        }
        case NEBeautyEffectTypeBeautyAdv: {
            return self.advancedSliderModelArray;
        }
        case NEBeautyEffectTypeBeautyAdv2: {
            return self.advancedSliderModelArray2;
        }
        case NEBeautyEffectTypeBeautyAdv3: {
            return self.advancedSliderModelArray3;
        }
            
        default: {
            return nil;
        }
    }
}

#pragma mark - Getter

- (NSMutableDictionary<NSNumber *,NEBeautyConfigView *> *)menuMap {
    if (!_menuMap) {
        _menuMap = [NSMutableDictionary dictionary];
    }
    
    return _menuMap;
}

- (NSArray<NETitleDisplayModel *> *)beautyTitleModelArray {
    if (!_beautyTitleModelArray) {
        NSMutableArray *modelArray = [NSMutableArray array];
        NSArray *titleArray = @[@"基础美颜", @"美形", @"高级", @"高级2", @"高级3"];
        NSArray *typeArray = @[@(NEBeautyEffectTypeBeautyBase),
                               @(NEBeautyEffectTypeBeautyShape),
                               @(NEBeautyEffectTypeBeautyAdv),
                               @(NEBeautyEffectTypeBeautyAdv2),
                               @(NEBeautyEffectTypeBeautyAdv3)];
        for (int i = 0; i < titleArray.count; i++) {
            NETitleDisplayModel *model = [[NETitleDisplayModel alloc] init];
            model.type = (NEBeautyEffectType)[typeArray[i] integerValue];
            model.title = titleArray[i];
            [modelArray addObject:model];
        }
        
        _beautyTitleModelArray = modelArray;
    }
    
    return _beautyTitleModelArray;
}

- (NSArray<NEBeautySliderDisplayModel *> *)baseSliderModelArray {
    if (!_baseSliderModelArray) {
        NSMutableArray *modelArray = [NSMutableArray array];
        NSArray *titleArray = @[@"磨皮", @"美白", @"瘦脸", @"嘴巴", @"红润", @"锐化"];
        NSArray *typeArray = @[@(NEBeautySliderTypeSmooth),
                               @(NEBeautySliderTypeWhiten),
                               @(NEBeautySliderTypeThinFace),
                               @(NEBeautySliderTypeMouth),
                               @(NEBeautySliderTypeFaceRuddy),
                               @(NEBeautySliderTypeFaceSharpen)];
        NSArray *imageNameArray = @[@"mopi", @"meibai", @"thinner_face", @"mouth", @"btn_beauty", @"btn_beauty"];
        NSArray *initialValueArray = @[@(0), @(0), @(0), @(0.5), @(0), @(0)];
        for (int i = 0; i < titleArray.count; i++) {
            NEBeautySliderDisplayModel *model = [[NEBeautySliderDisplayModel alloc] init];
            model.title = titleArray[i];
            model.type = (NEBeautySliderType)[typeArray[i] integerValue];
            model.imageName = imageNameArray[i];
            model.value = [initialValueArray[i] floatValue];
            
            [modelArray addObject:model];
        }
        
        _baseSliderModelArray = modelArray;
    }
    
    return _baseSliderModelArray;
}

- (NSArray<NEBeautySliderDisplayModel *> *)shapeSliderModelArray {
    if (!_shapeSliderModelArray) {
        NSMutableArray *modelArray = [NSMutableArray array];
        NSArray *titleArray = @[@"小脸", @"大眼", @"下巴"];
        NSArray *typeArray = @[@(NEBeautySliderTypeSmallFace),
                               @(NEBeautySliderTypeBigEye),
                               @(NEBeautySliderTypeJaw)];
        NSArray *imageNameArray = @[@"thin_face", @"enlarge_eyes", @"small_face"];
        NSArray *initialValueArray = @[@(0), @(0), @(0.5)];
        for (int i = 0; i < titleArray.count; i++) {
            NEBeautySliderDisplayModel *model = [[NEBeautySliderDisplayModel alloc] init];
            model.title = titleArray[i];
            model.type = (NEBeautySliderType)[typeArray[i] integerValue];
            model.imageName = imageNameArray[i];
            model.value = [initialValueArray[i] floatValue];
            
            [modelArray addObject:model];
        }
        
        _shapeSliderModelArray = modelArray;
    }
    
    return _shapeSliderModelArray;
}

- (NSArray<NEBeautySliderDisplayModel *> *)advancedSliderModelArray {
    if (!_advancedSliderModelArray) {
        NSMutableArray *modelArray = [NSMutableArray array];
        NSArray *titleArray = @[@"亮眼", @"美牙", @"小鼻", @"眼距", @"眼角"];
        NSArray *typeArray = @[@(NEBeautySliderTypeLightEye),
                               @(NEBeautySliderTypeWhiteTeeth),
                               @(NEBeautySliderTypeSmallNose),
                               @(NEBeautySliderTypeEyeDis),
                               @(NEBeautySliderTypeEyeAngle)];
        NSArray *imageNameArray = @[@"eyebright", @"whitenteeth", @"thinnose", @"eyedis", @"eyeangle"];
        NSArray *initialValueArray = @[@(0), @(0), @(0), @(0.5), @(0.5)];
        for (int i = 0; i < titleArray.count; i++) {
            NEBeautySliderDisplayModel *model = [[NEBeautySliderDisplayModel alloc] init];
            model.title = titleArray[i];
            model.type = (NEBeautySliderType)[typeArray[i] integerValue];
            model.imageName = imageNameArray[i];
            model.value = [initialValueArray[i] floatValue];
            
            [modelArray addObject:model];
        }
        
        _advancedSliderModelArray = modelArray;
    }
    
    return _advancedSliderModelArray;
}

- (NSArray<NEBeautySliderDisplayModel *> *)advancedSliderModelArray2 {
    if (!_advancedSliderModelArray2) {
        NSMutableArray *modelArray = [NSMutableArray array];
        NSArray *titleArray = @[@"长鼻", @"人中", @"嘴角", @"圆眼", @"开眼角"];
        NSArray *typeArray = @[@(NEBeautySliderTypeLongNose),
                               @(NEBeautySliderTypeRenZhong),
                               @(NEBeautySliderTypeMouthAngle),
                               @(NEBeautySliderTypeRoundEye),
                               @(NEBeautySliderTypeOpenEyeAngle)];
        NSArray *initialValueArray = @[@(0.5), @(0.5), @(0.5), @(0), @(0)];
        for (int i = 0; i < titleArray.count; i++) {
            NEBeautySliderDisplayModel *model = [[NEBeautySliderDisplayModel alloc] init];
            model.title = titleArray[i];
            model.type = (NEBeautySliderType)[typeArray[i] integerValue];
            model.imageName = @"btn_beauty";
            model.value = [initialValueArray[i] floatValue];
            
            [modelArray addObject:model];
        }
        
        _advancedSliderModelArray2 = modelArray;
    }
    
    return _advancedSliderModelArray2;
}

- (NSArray<NEBeautySliderDisplayModel *> *)advancedSliderModelArray3 {
    if (!_advancedSliderModelArray3) {
        NSMutableArray *modelArray = [NSMutableArray array];
        NSArray *titleArray = @[@"V脸", @"瘦下巴", @"窄脸", @"瘦颧骨"];
        NSArray *typeArray = @[@(NEBeautySliderTypeVFace),
                               @(NEBeautySliderTypeThinUnderjaw),
                               @(NEBeautySliderTypeNarrowFace),
                               @(NEBeautySliderTypeCheekBone)];
        NSArray *initialValueArray = @[@(0), @(0), @(0), @(0)];
        for (int i = 0; i < titleArray.count; i++) {
            NEBeautySliderDisplayModel *model = [[NEBeautySliderDisplayModel alloc] init];
            model.title = titleArray[i];
            model.type = (NEBeautySliderType)[typeArray[i] integerValue];
            model.imageName = @"btn_beauty";
            model.value = [initialValueArray[i] floatValue];
            
            [modelArray addObject:model];
        }
        
        _advancedSliderModelArray3 = modelArray;
    }
    
    return _advancedSliderModelArray3;
}

@end
