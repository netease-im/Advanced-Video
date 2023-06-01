//
//  License.m
//  Effects
//
//  Created by sunjian on 2021/5/8.
//  Copyright © 2021 sjuinan. All rights reserved.
//

#import "EffectsLicense.h"
#import "st_mobile_common.h"
#import "st_mobile_license.h"

@interface EffectsLicense ()
@end

@implementation EffectsLicense

static NSString *strActiveCodeKey = @"EFFECTS_ACTIVE_CODE";

+ (NSString *)getActiveCodeWithData:(NSData *)dataLicense{
    st_result_t iRet = ST_E_FAIL;
    char active_code[10240];
    int active_code_len = 10240;
    iRet = st_mobile_generate_activecode_from_buffer([dataLicense bytes],
                                                     (int)[dataLicense length],
                                                     active_code,
                                                     &active_code_len);
    if (iRet != ST_OK) {
        return nil;
    }
    NSString * strActiveCode = [[NSString alloc] initWithUTF8String:active_code];
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    if (strActiveCode.length) {
        [userDefaults setObject:strActiveCode forKey:strActiveCodeKey];
        [userDefaults synchronize];
    }
    return strActiveCode;
}

+ (BOOL)authorizeWithLicensePath:(NSString *)licensePath{
    NSData * dataLicense = [NSData dataWithContentsOfFile:licensePath];
    return [self authorizeWithLicenseData:dataLicense];
}

+ (BOOL)authorizeWithLicenseData:(NSData *)dataLicense{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    NSString *strActiveCode = [userDefaults objectForKey:strActiveCodeKey];
    if (strActiveCode.length) {
        st_result_t iRet = ST_E_FAIL;
        iRet = st_mobile_check_activecode_from_buffer(
                                                      [dataLicense bytes],
                                                      (int)[dataLicense length],
                                                      strActiveCode.UTF8String,
                                                      (int)[strActiveCode length]
                                                      );
        
        if (iRet == ST_OK) {
            return YES;
        }
    }
    strActiveCode = [self getActiveCodeWithData:dataLicense];
    if (strActiveCode) {
        return YES;
    }
    return NO;
}

@end
