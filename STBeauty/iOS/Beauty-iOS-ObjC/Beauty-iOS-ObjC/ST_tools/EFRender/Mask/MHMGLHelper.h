//
//  MHMGLHelper.h
//  HelloOpenGLES
//
//  Created by 马浩萌 on 2022/5/18.
//

#ifndef MHMGLHelper_h
#define MHMGLHelper_h

#import <OpenGLES/EAGL.h>
#import <OpenGLES/ES2/gl.h>
#import <OpenGLES/ES2/glext.h>
#import <OpenGLES/ES3/gl.h>

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Weverything"

// 编译shader
FOUNDATION_EXPORT GLuint compileShaderByName(NSString *shaderName, GLenum shaderType, EAGLContext *glContext);
FOUNDATION_EXPORT GLuint compileShaderByContent(NSString *shaderContent, GLenum shaderType, EAGLContext *glContext);
//
//// 获取program
FOUNDATION_EXPORT GLuint getShaderProgram(NSString *shaderName, EAGLContext *glContext);

FOUNDATION_EXPORT GLenum glCheckError_(const char *file, int line);
#define glCheckError() glCheckError_(__FILE__, __LINE__)

#pragma clang diagnostic pop


#endif /* MHMGLHelper_h */
