//
//  MHMGLHelper.m
//  GLToolsOf3DMath
//
//  Created by 马浩萌 on 2022/5/18.
//

#import <Foundation/Foundation.h>
#import <OpenGLES/EAGL.h>
#import <OpenGLES/ES2/gl.h>
#import <OpenGLES/ES2/glext.h>
#import <OpenGLES/ES3/gl.h>

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Weverything"

GLuint compileShaderByContent(NSString *shaderContent, GLenum shaderType, EAGLContext *glContext) {
    if ([EAGLContext currentContext] != glContext) {
        [EAGLContext setCurrentContext:glContext];
    }
    
    const GLchar *utf8_content = shaderContent.UTF8String;
    const GLint length = (GLint)shaderContent.length;
    GLuint shader = glCreateShader(shaderType);
    glShaderSource(shader, 1, &utf8_content, &length);
    glCompileShader(shader);
    
    GLint params;
    glGetShaderiv(shader, GL_COMPILE_STATUS, &params);
    if (params == GL_FALSE) {
        GLchar messages[256];
        glGetShaderInfoLog(shader, sizeof(messages), 0, &messages[0]);
        NSString *messageString = [NSString stringWithUTF8String:messages];
        NSLog(@"shader compile failed : %@", messageString);
        exit(1);
    }
    
    return shader;
}

GLuint compileShaderByName(NSString *shaderName, GLenum shaderType, EAGLContext *glContext) {
    if ([EAGLContext currentContext] != glContext) {
        [EAGLContext setCurrentContext:glContext];
    }
    
    NSString *shaderPath = [[NSBundle mainBundle] pathForResource:shaderName ofType:shaderType == GL_VERTEX_SHADER ? @"vsh" : @"fsh"];
    NSError *error;
    NSString *shaderContent = [NSString stringWithContentsOfFile:shaderPath encoding:NSUTF8StringEncoding error:&error];
    if (error) {
        NSLog(@"get shader's content failed : %@", error.localizedDescription);
        exit(1);
    }
    
    return compileShaderByContent(shaderContent, shaderType, glContext);
}

GLuint getShaderProgram(NSString *shaderName, EAGLContext *glContext) {
    if ([EAGLContext currentContext] != glContext) {
        [EAGLContext setCurrentContext:glContext];
    }
    
    GLuint program = glCreateProgram();
    
    glAttachShader(program, compileShaderByName(shaderName, GL_VERTEX_SHADER, glContext));
    glAttachShader(program, compileShaderByName(shaderName, GL_FRAGMENT_SHADER, glContext));
    
    glLinkProgram(program);
    GLint params;
    glGetProgramiv(program, GL_LINK_STATUS, &params);
    if (params == GL_FALSE) {
        GLchar messages[256];
        glGetProgramInfoLog(program, sizeof(messages), 0, &messages[0]);
        NSString *messageString = [NSString stringWithUTF8String:messages];
        NSLog(@"program link failed : %@", messageString);
        exit(1);
    }
    
    return program;
}

GLenum glCheckError_(const char *file, int line) {
    GLenum errorCode;
    while ((errorCode = glGetError()) != GL_NO_ERROR) {
        char* error;
        switch (errorCode) {
            case GL_INVALID_ENUM:                  error = "INVALID_ENUM"; break;
            case GL_INVALID_VALUE:                 error = "INVALID_VALUE"; break;
            case GL_INVALID_OPERATION:             error = "INVALID_OPERATION"; break;
            case GL_OUT_OF_MEMORY:                 error = "OUT_OF_MEMORY"; break;
            case GL_INVALID_FRAMEBUFFER_OPERATION: error = "INVALID_FRAMEBUFFER_OPERATION"; break;
            default: error = "unknown error%d"; break;
        }
        printf("glCheckError-%s-%d：%d-%s", file, line, errorCode, error);
    }
    return errorCode;
}

#pragma clang diagnostic pop
