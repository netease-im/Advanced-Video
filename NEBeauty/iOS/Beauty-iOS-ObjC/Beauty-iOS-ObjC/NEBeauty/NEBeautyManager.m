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

static NSString * const kNEBeautyLocalFilterFolderName = @"Filter";
static NSString * const kNEBeautyLocalStickerFolderName = @"Sticker";
static NSString * const kNEBeautyLocalMakeupFolderName = @"Makeup";

@interface NEBeautyManager ()<NEBeautyConfigViewDataSource, NEBeautyConfigViewDelegate>

@property (nonatomic, copy) NSString *localResourcePath;

@property (nonatomic, strong) NSMutableDictionary<NSNumber*, NEBeautyConfigView*> *menuMap;

// 标题tab数据源
@property (nonatomic, strong) NSArray<NETitleDisplayModel *> *beautyTitleModelArray;
@property (nonatomic, strong) NSArray<NETitleDisplayModel *> *filterTitleModelArray;
@property (nonatomic, strong) NSArray<NETitleDisplayModel *> *stickerTitleModelArray;
@property (nonatomic, strong) NSArray<NETitleDisplayModel *> *makeupTitleModelArray;

// 美颜UI数据源
@property (nonatomic, strong) NSArray<NEBeautySliderDisplayModel *> *baseSliderModelArray;
@property (nonatomic, strong) NSArray<NEBeautySliderDisplayModel *> *shapeSliderModelArray;
@property (nonatomic, strong) NSArray<NEBeautySliderDisplayModel *> *advancedSliderModelArray;
@property (nonatomic, strong) NSArray<NEBeautySliderDisplayModel *> *advancedSliderModelArray2;
@property (nonatomic, strong) NSArray<NEBeautySliderDisplayModel *> *advancedSliderModelArray3;

// 滤镜UI数据源
@property (nonatomic, strong) NSArray<NECollectionViewDisplayModel *> *filterItemModelArray;
@property (nonatomic, strong) NEBeautySliderDisplayModel *filterStrengthModel;

// 贴纸UI数据源
@property (nonatomic, strong) NSMutableArray<NECollectionViewDisplayModel *> *sticker2DModelArray;
@property (nonatomic, strong) NSMutableArray<NECollectionViewDisplayModel *> *sticker3DModelArray;
@property (nonatomic, strong) NSMutableArray<NECollectionViewDisplayModel *> *stickerParticleModelArray;
@property (nonatomic, strong) NSMutableArray<NECollectionViewDisplayModel *> *stickerFaceChangeModelArray;

// 美妆UI数据源
@property (nonatomic, strong) NSArray<NECollectionViewDisplayModel *> *makeupItemModelArray;

@end

@implementation NEBeautyManager {
    float _filterStrength;
}

#pragma mark - Life Cycle

- (instancetype)init {
    self = [super init];
    if (self) {
        _localResourcePath = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES).firstObject stringByAppendingPathComponent:@"NEBeauty"];
        _filterStrength = 0;
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

- (void)prepareResource {
    // 准备资源文件
    [self prepareFilterData];
    [self prepareStickerData];
    [self prepareMakeupData];
}

- (void)initNEBeauty {
    // 初始化美颜相关资源
    [[NERtcBeauty shareInstance] startBeauty];
    
    // 需要加载beauty目录下的template.json文件
    NSString *strBeauty = [[NSBundle mainBundle] pathForResource:@"beauty/template" ofType:@"json"];
    NSString *strBeautyPath = [strBeauty stringByDeletingLastPathComponent];
    NSString *dir = [strBeautyPath stringByAppendingString:@"/"];
    NSString *templateName = @"template.json";
    [[NERtcBeauty shareInstance] addTempleteWithPath:dir andName:templateName];
    
    [self applyDefaultSettings];
}

- (void)destroyNEBeauty {
    // 销毁美颜相关资源
    _filterStrength = 0;
    [NERtcBeauty shareInstance].filterStrength = 0;
    [[NERtcBeauty shareInstance] stopBeauty];
}

- (void)enableNEBeauty:(BOOL)enable {
    // 开启/关闭美颜效果
    [NERtcBeauty shareInstance].isOpenBeauty = enable;
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

#pragma mark - Private

- (void)prepareFilterData {
    NSFileManager *fileManager = [NSFileManager defaultManager];
    
    NSString *localFilterPath = [NSString pathWithComponents:@[self.localResourcePath, kNEBeautyLocalFilterFolderName]];
    if ([fileManager fileExistsAtPath:localFilterPath]) {
        [fileManager removeItemAtPath:localFilterPath error:nil];
    }
    [fileManager createDirectoryAtPath:localFilterPath withIntermediateDirectories:YES attributes:nil error:nil];
    
    NSString *bundlePath = [[[NSBundle mainBundle] resourcePath] stringByAppendingPathComponent:@"filters.bundle"];
    NSArray *fileNameArray = [fileManager contentsOfDirectoryAtPath:bundlePath error:nil];
    
    NSMutableArray *modelArray = [NSMutableArray array];
    
    for (int i = 0; i < fileNameArray.count; i++) {
        NSString *fileName = fileNameArray[i];
        
        if ([fileName hasPrefix:@"filter_style"] && [fileName hasSuffix:@"zip"]) {
            NSString *filterPath = [NSString pathWithComponents:@[bundlePath, fileName]];
            [SSZipArchive unzipFileAtPath:filterPath toDestination:localFilterPath];
            
            NSString *filterName = [[filterPath lastPathComponent] stringByDeletingPathExtension];
            
            NECollectionViewDisplayModel *model = [[NECollectionViewDisplayModel alloc] init];
            model.resourcePath = [[NSString pathWithComponents:@[localFilterPath, filterName]] stringByAppendingString:@"/"];
            model.name = [filterName stringByReplacingOccurrencesOfString:@"filter_style_" withString:@""];
            model.image = [UIImage imageWithContentsOfFile:[[filterPath stringByDeletingPathExtension] stringByAppendingPathExtension:@"png"]];
            model.index = i;
            model.isSelected = NO;
            model.type = NEBeautyEffectTypeFilter;
            
            [modelArray addObject:model];
        }
    }
    
    _filterItemModelArray = modelArray;
}

- (void)prepareStickerData {
    NSFileManager *fileManager = [NSFileManager defaultManager];
    
    NSArray *stickerCategoryArray = @[@"2d_sticker",
                                      @"3d_sticker",
                                      @"particle_sticker",
                                      @"face_change_sticker"];
    NSArray *stickerTypeArray = @[@(NEBeautyEffectTypeSticker2D),
                                  @(NEBeautyEffectTypeSticker3D),
                                  @(NEBeautyEffectTypeStickerParticle),
                                  @(NEBeautyEffectTypeStickerFaceChange)];
    NSArray *stickerDataArray = @[self.sticker2DModelArray,
                                  self.sticker3DModelArray,
                                  self.stickerParticleModelArray,
                                  self.stickerFaceChangeModelArray];
    
    for (int i = 0; i < stickerCategoryArray.count; i++) {
        NSString *category = stickerCategoryArray[i];
        NSInteger type = [stickerTypeArray[i] integerValue];
        NSMutableArray *stickerModelArray = stickerDataArray[i];
        [stickerModelArray removeAllObjects];
        
        NSString *localStickerPath = [[NSString pathWithComponents:@[self.localResourcePath, kNEBeautyLocalStickerFolderName]] stringByAppendingPathComponent:category];
        if ([fileManager fileExistsAtPath:localStickerPath]) {
            [fileManager removeItemAtPath:localStickerPath error:nil];
        }
        [fileManager createDirectoryAtPath:localStickerPath withIntermediateDirectories:YES attributes:nil error:nil];
        
        NSString *bundlePath = [[[NSBundle mainBundle] resourcePath] stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.bundle", category]];
        NSArray *fileNameArray = [fileManager contentsOfDirectoryAtPath:bundlePath error:nil];
        
        for (int j = 0; j < fileNameArray.count; j++) {
            NSString *fileName = fileNameArray[j];
            
            if ([fileName hasSuffix:@"zip"]) {
                NSString *stickerPath = [NSString pathWithComponents:@[bundlePath, fileName]];
                [SSZipArchive unzipFileAtPath:stickerPath toDestination:localStickerPath];
                
                NSString *stickerName = [[stickerPath lastPathComponent] stringByDeletingPathExtension];
                
                NECollectionViewDisplayModel *model = [[NECollectionViewDisplayModel alloc] init];
                model.resourcePath = [[NSString pathWithComponents:@[localStickerPath, stickerName]] stringByAppendingString:@"/"];
                model.name = @"";
                model.image = [UIImage imageWithContentsOfFile:[[stickerPath stringByDeletingPathExtension] stringByAppendingPathExtension:@"png"]];
                model.index = j;
                model.isSelected = NO;
                model.type = type;
                
                [stickerModelArray addObject:model];
            }
        }
    }
}

- (void)prepareMakeupData {
    NSFileManager *fileManager = [NSFileManager defaultManager];
    
    NSString *localMakeupPath = [NSString pathWithComponents:@[self.localResourcePath, kNEBeautyLocalMakeupFolderName]];
    if ([fileManager fileExistsAtPath:localMakeupPath]) {
        [fileManager removeItemAtPath:localMakeupPath error:nil];
    }
    [fileManager createDirectoryAtPath:localMakeupPath withIntermediateDirectories:YES attributes:nil error:nil];
    
    NSString *bundlePath = [[[NSBundle mainBundle] resourcePath] stringByAppendingPathComponent:@"makeup_sticker.bundle"];
    NSArray *fileNameArray = [fileManager contentsOfDirectoryAtPath:bundlePath error:nil];
    
    NSMutableArray *modelArray = [NSMutableArray array];
    
    for (int i = 0; i < fileNameArray.count; i++) {
        NSString *fileName = fileNameArray[i];
        
        if ([fileName hasSuffix:@"zip"]) {
            NSString *makeupPath = [NSString pathWithComponents:@[bundlePath, fileName]];
            [SSZipArchive unzipFileAtPath:makeupPath toDestination:localMakeupPath];
            
            NSString *makeupName = [[makeupPath lastPathComponent] stringByDeletingPathExtension];
            
            NECollectionViewDisplayModel *model = [[NECollectionViewDisplayModel alloc] init];
            model.resourcePath = [[NSString pathWithComponents:@[localMakeupPath, makeupName]] stringByAppendingString:@"/"];
            model.name = @"";
            model.image = [UIImage imageWithContentsOfFile:[[makeupPath stringByDeletingPathExtension] stringByAppendingPathExtension:@"png"]];
            model.index = i;
            model.isSelected = NO;
            model.type = NEBeautyEffectTypeMakeup;
            
            [modelArray addObject:model];
        }
    }
    
    _makeupItemModelArray = modelArray;
}

- (void)applyDefaultSettings {
    for (NEBeautySliderDisplayModel *model in self.baseSliderModelArray) {
        if (model.type == NEBeautySliderTypeSmooth) {
            model.value = 0.65;
        } else if (model.type == NEBeautySliderTypeWhiten) {
            model.value = 0.8;
        } else if (model.type == NEBeautySliderTypeMouth) {
            model.value = 0.8;
        } else if (model.type == NEBeautySliderTypeThinFace) {
            model.value = 0.35;
        } else if (model.type == NEBeautySliderTypeFaceRuddy) {
            model.value = 0.1;
        } else if (model.type == NEBeautySliderTypeFaceSharpen) {
            model.value = 0.1;
        }
    }
    
    for (NEBeautySliderDisplayModel *model in self.shapeSliderModelArray) {
        if (model.type == NEBeautySliderTypeBigEye) {
            model.value = 0.3;
        } else if (model.type == NEBeautySliderTypeSmallFace) {
            model.value = 0.1;
        } else if (model.type == NEBeautySliderTypeJaw) {
            model.value = 0.4;
        }
    }
    
    for (NEBeautySliderDisplayModel *model in self.advancedSliderModelArray) {
        if (model.type == NEBeautySliderTypeLightEye) {
            model.value = 0.6;
        } else if (model.type == NEBeautySliderTypeWhiteTeeth) {
            model.value = 0.3;
        } else if (model.type == NEBeautySliderTypeSmallNose) {
            model.value = 0.4;
        } else if (model.type == NEBeautySliderTypeEyeDis) {
            model.value = 0.4;
        } else if (model.type == NEBeautySliderTypeEyeAngle) {
            model.value = 0.5;
        }
    }
    
    for (NEBeautySliderDisplayModel *model in self.advancedSliderModelArray2) {
        if (model.type == NEBeautySliderTypeLongNose) {
            model.value = 0;
        } else if (model.type == NEBeautySliderTypeRenZhong) {
            model.value = 0.5;
        } else if (model.type == NEBeautySliderTypeMouthAngle) {
            model.value = 0.5;
        } else if (model.type == NEBeautySliderTypeRoundEye) {
            model.value = 0.8;
        } else if (model.type == NEBeautySliderTypeOpenEyeAngle) {
            model.value = 0;
        }
    }
    
    for (NEBeautySliderDisplayModel *model in self.advancedSliderModelArray3) {
        if (model.type == NEBeautySliderTypeVFace) {
            model.value = 0;
        } else if (model.type == NEBeautySliderTypeThinUnderjaw) {
            model.value = 0;
        } else if (model.type == NEBeautySliderTypeNarrowFace) {
            model.value = 0;
        } else if (model.type == NEBeautySliderTypeCheekBone) {
            model.value = 0;
        }
    }
    
    [NERtcBeauty shareInstance].smoothSkin = 0.65;
    [NERtcBeauty shareInstance].whiteSkin = 0.8;
    [NERtcBeauty shareInstance].mouth = 0.8;
    [NERtcBeauty shareInstance].thinFace = 0.35;
    [NERtcBeauty shareInstance].faceRuddyStrength = 0.1;
    [NERtcBeauty shareInstance].faceSharpenStrength = 0.1;
    [NERtcBeauty shareInstance].bigEye = 0.3;
    [NERtcBeauty shareInstance].smallFace = 0.1;
    [NERtcBeauty shareInstance].jaw = 0.4;
    [NERtcBeauty shareInstance].brightEye = 0.6;
    [NERtcBeauty shareInstance].teeth = 0.3;
    [NERtcBeauty shareInstance].smallNose = 0.4;
    [NERtcBeauty shareInstance].eyesDistance = 0.4;
    [NERtcBeauty shareInstance].eyesAngle = 0.5;
    [NERtcBeauty shareInstance].longNoseStrength = 0;
    [NERtcBeauty shareInstance].renZhongStrength = 0.5;
    [NERtcBeauty shareInstance].mouthAngle = 0.5;
    [NERtcBeauty shareInstance].roundEyeStrength = 0.8;
    [NERtcBeauty shareInstance].openEyeAngleStrength = 0;
    [NERtcBeauty shareInstance].vFaceStrength = 0;
    [NERtcBeauty shareInstance].thinUnderjawStrength = 0;
    [NERtcBeauty shareInstance].narrowFaceStrength = 0;
    [NERtcBeauty shareInstance].cheekBoneStrength = 0;
    
//    self.filterStrengthModel.value = 0.7;
//    NECollectionViewDisplayModel *selectedModel = nil;
//    for (NECollectionViewDisplayModel *model in self.filterItemModelArray) {
//        if ([model.name isEqualToString:@"白皙"]) {
//            selectedModel = model;
//
//            break;
//        }
//    }
//    if (!selectedModel) {
//        return;
//    }
//    selectedModel.isSelected = YES;
//    _filterStrength = 0.7;
//    [[NERtcBeauty shareInstance] removeBeautyFilter];
//    [[NERtcBeauty shareInstance] addBeautyFilterWithPath:selectedModel.resourcePath andName:@"template.json"];
//    [NERtcBeauty shareInstance].filterStrength = 0.7;
}

#pragma mark - NEBeautyConfigViewDelegate

- (void)didTriggerResetActionWithConfigViewType:(NEBeautyConfigViewType)type {
    switch (type) {
        case NEBeautyConfigViewTypeBeauty: {
            break;
        }
        case NEBeautyConfigViewTypeFilter: {
            [[NERtcBeauty shareInstance] removeBeautyFilter];
            
            break;
        }
        case NEBeautyConfigViewTypeSticker: {
            [[NERtcBeauty shareInstance] removeBeautySticker];
            
            break;
        }
        case NEBeautyConfigViewTypeMakeup: {
            [[NERtcBeauty shareInstance] removeBeautyMakeup];
            
            break;
        }
            
        default:
            break;
    }
}

- (void)didSelectItemWithConfigViewType:(NEBeautyConfigViewType)type model:(NECollectionViewDisplayModel *)model {
    switch (type) {
        case NEBeautyConfigViewTypeFilter: {
            if (!model) {
                [[NERtcBeauty shareInstance] removeBeautyFilter];
            } else {
                [[NERtcBeauty shareInstance] addBeautyFilterWithPath:model.resourcePath andName:@"template.json"];
                [NERtcBeauty shareInstance].filterStrength = _filterStrength;
            }
            
            break;
        }
        case NEBeautyConfigViewTypeSticker: {
            if (!model) {
                [[NERtcBeauty shareInstance] removeBeautySticker];
            } else {
                [[NERtcBeauty shareInstance] addBeautyStickerWithPath:model.resourcePath andName:@"template.json"];
            }
            
            break;
        }
        case NEBeautyConfigViewTypeMakeup: {
            if (!model) {
                [[NERtcBeauty shareInstance] removeBeautyMakeup];
            } else {
                [[NERtcBeauty shareInstance] addBeautyMakeupWithPath:model.resourcePath andName:@"template.json"];
            }
        }
            
        default:
            break;
    }
}

- (void)didChangeSliderValueWithType:(NEBeautySliderType)type value:(float)value {
    switch (type) {
        case NEBeautySliderTypeWhiteTeeth: {
            [NERtcBeauty shareInstance].teeth = value;
            
            break;
        }
        case NEBeautySliderTypeLightEye: {
            [NERtcBeauty shareInstance].brightEye = value;
            
            break;
        }
        case NEBeautySliderTypeWhiten: {
            [NERtcBeauty shareInstance].whiteSkin = value;
            
            break;
        }
        case NEBeautySliderTypeSmooth: {
            [NERtcBeauty shareInstance].smoothSkin = value;
            
            break;
        }
        case NEBeautySliderTypeSmallNose: {
            [NERtcBeauty shareInstance].smallNose = value;
            
            break;
        }
        case NEBeautySliderTypeEyeDis: {
            [NERtcBeauty shareInstance].eyesDistance = value;
            
            break;
        }
        case NEBeautySliderTypeEyeAngle: {
            [NERtcBeauty shareInstance].eyesAngle = value;
            
            break;
        }
        case NEBeautySliderTypeMouth: {
            [NERtcBeauty shareInstance].mouth = value;
            
            break;
        }
        case NEBeautySliderTypeBigEye: {
            [NERtcBeauty shareInstance].bigEye = value;
            
            break;
        }
        case NEBeautySliderTypeSmallFace: {
            [NERtcBeauty shareInstance].smallFace = value;
            
            break;
        }
        case NEBeautySliderTypeJaw: {
            [NERtcBeauty shareInstance].jaw = value;
            
            break;
        }
        case NEBeautySliderTypeThinFace: {
            [NERtcBeauty shareInstance].thinFace = value;
            
            break;
        }
        case NEBeautySliderTypeFaceRuddy: {
            [NERtcBeauty shareInstance].faceRuddyStrength = value;
            
            break;
        }
        case NEBeautySliderTypeLongNose: {
            [NERtcBeauty shareInstance].longNoseStrength = value;
            
            break;
        }
        case NEBeautySliderTypeRenZhong: {
            [NERtcBeauty shareInstance].renZhongStrength = value;
            
            break;
        }
        case NEBeautySliderTypeMouthAngle: {
            [NERtcBeauty shareInstance].mouthAngle = value;
            
            break;
        }
        case NEBeautySliderTypeRoundEye: {
            [NERtcBeauty shareInstance].roundEyeStrength = value;
            
            break;
        }
        case NEBeautySliderTypeOpenEyeAngle: {
            [NERtcBeauty shareInstance].openEyeAngleStrength = value;
            
            break;
        }
        case NEBeautySliderTypeVFace: {
            [NERtcBeauty shareInstance].vFaceStrength = value;
            
            break;
        }
        case NEBeautySliderTypeThinUnderjaw: {
            [NERtcBeauty shareInstance].thinUnderjawStrength = value;
            
            break;
        }
        case NEBeautySliderTypeNarrowFace: {
            [NERtcBeauty shareInstance].narrowFaceStrength = value;
            
            break;
        }
        case NEBeautySliderTypeCheekBone: {
            [NERtcBeauty shareInstance].cheekBoneStrength = value;
            
            break;
        }
        case NEBeautySliderTypeFaceSharpen: {
            [NERtcBeauty shareInstance].faceSharpenStrength = value;
            
            break;
        }
        case NEBeautySliderTypeFilterStrength: {
            _filterStrength = value;
            [NERtcBeauty shareInstance].filterStrength = value;
            
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
        case NEBeautyConfigViewTypeFilter: {
            return self.filterTitleModelArray;
        }
        case NEBeautyConfigViewTypeSticker: {
            return self.stickerTitleModelArray;
        }
        case NEBeautyConfigViewTypeMakeup: {
            return self.makeupTitleModelArray;
        }
            
        default: {
            return nil;
        }
    }
}

- (NSArray<NECollectionViewDisplayModel *> *)itemModelArrayForConfigViewWithType:(NEBeautyConfigViewType)type effectType:(NEBeautyEffectType)effectType {
    switch (type) {
        case NEBeautyConfigViewTypeFilter: {
            return self.filterItemModelArray;
        }
        case NEBeautyConfigViewTypeSticker: {
            switch (effectType) {
                case NEBeautyEffectTypeSticker2D: {
                    return self.sticker2DModelArray;
                }
                case NEBeautyEffectTypeSticker3D: {
                    return self.sticker3DModelArray;
                }
                case NEBeautyEffectTypeStickerParticle: {
                    return self.stickerParticleModelArray;
                }
                case NEBeautyEffectTypeStickerFaceChange: {
                    return self.stickerFaceChangeModelArray;
                }
                    
                default:
                    return nil;
            }
        }
        case NEBeautyConfigViewTypeMakeup: {
            return self.makeupItemModelArray;
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

- (NEBeautySliderDisplayModel *)sliderModelForFilterStrength {
    return self.filterStrengthModel;
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
            model.type = [typeArray[i] integerValue];
            model.title = titleArray[i];
            [modelArray addObject:model];
        }
        
        _beautyTitleModelArray = modelArray;
    }
    
    return _beautyTitleModelArray;
}

- (NSArray<NETitleDisplayModel *> *)filterTitleModelArray {
    if (!_filterTitleModelArray) {
        NETitleDisplayModel *model = [[NETitleDisplayModel alloc] init];
        model.type = NEBeautyEffectTypeFilter;
        model.title = @"滤镜";
        
        _filterTitleModelArray = @[model];
    }
    
    return _filterTitleModelArray;
}

- (NSArray<NETitleDisplayModel *> *)stickerTitleModelArray {
    if (!_stickerTitleModelArray) {
        NSMutableArray *modelArray = [NSMutableArray array];
        NSArray *titleArray = @[@"2D", @"3D", @"粒子", @"换脸"];
        NSArray *typeArray = @[@(NEBeautyEffectTypeSticker2D),
                               @(NEBeautyEffectTypeSticker3D),
                               @(NEBeautyEffectTypeStickerParticle),
                               @(NEBeautyEffectTypeStickerFaceChange)];
        for (int i = 0; i < titleArray.count; i++) {
            NETitleDisplayModel *model = [[NETitleDisplayModel alloc] init];
            model.title = titleArray[i];
            model.type = [typeArray[i] integerValue];
            [modelArray addObject:model];
        }
        
        _stickerTitleModelArray = modelArray;
    }
    
    return _stickerTitleModelArray;
}

- (NSArray<NETitleDisplayModel *> *)makeupTitleModelArray {
    if (!_makeupTitleModelArray) {
        NETitleDisplayModel *model = [[NETitleDisplayModel alloc] init];
        model.type = NEBeautyEffectTypeMakeup;
        model.title = @"美妆";
        
        _makeupTitleModelArray = @[model];
    }
    
    return _makeupTitleModelArray;
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
            model.type = [typeArray[i] integerValue];
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
        NSArray *initialValueArray = @[@(0), @(0), @(0)];
        for (int i = 0; i < titleArray.count; i++) {
            NEBeautySliderDisplayModel *model = [[NEBeautySliderDisplayModel alloc] init];
            model.title = titleArray[i];
            model.type = [typeArray[i] integerValue];
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
            model.type = [typeArray[i] integerValue];
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
            model.type = [typeArray[i] integerValue];
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
            model.type = [typeArray[i] integerValue];
            model.imageName = @"btn_beauty";
            model.value = [initialValueArray[i] floatValue];
            
            [modelArray addObject:model];
        }
        
        _advancedSliderModelArray3 = modelArray;
    }
    
    return _advancedSliderModelArray3;
}

- (NEBeautySliderDisplayModel *)filterStrengthModel {
    if (!_filterStrengthModel) {
        _filterStrengthModel = [[NEBeautySliderDisplayModel alloc] init];
        _filterStrengthModel.type = NEBeautySliderTypeFilterStrength;
        _filterStrengthModel.title = @"强度";
        _filterStrengthModel.imageName = nil;
        _filterStrengthModel.value = 0;
    }
    
    return _filterStrengthModel;
}

- (NSMutableArray<NECollectionViewDisplayModel *> *)sticker2DModelArray {
    if (!_sticker2DModelArray) {
        _sticker2DModelArray = [NSMutableArray array];
    }
    
    return _sticker2DModelArray;
}

- (NSMutableArray<NECollectionViewDisplayModel *> *)sticker3DModelArray {
    if (!_sticker3DModelArray) {
        _sticker3DModelArray = [NSMutableArray array];
    }
    
    return _sticker3DModelArray;
}

- (NSMutableArray<NECollectionViewDisplayModel *> *)stickerParticleModelArray {
    if (!_stickerParticleModelArray) {
        _stickerParticleModelArray = [NSMutableArray array];
    }
    
    return _stickerParticleModelArray;
}

- (NSMutableArray<NECollectionViewDisplayModel *> *)stickerFaceChangeModelArray {
    if (!_stickerFaceChangeModelArray) {
        _stickerFaceChangeModelArray = [NSMutableArray array];
    }
    
    return _stickerFaceChangeModelArray;
}

@end
