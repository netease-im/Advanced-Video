attribute vec4 Position;
attribute vec4 OriginTextureCoords;
attribute vec4 MaskTextureCoords;
varying vec2 OriginTextureCoordsVarying;
varying vec2 MaskTextureCoordsVarying;

void main (void) {
    gl_Position = Position;
    OriginTextureCoordsVarying = OriginTextureCoords.xy;
    MaskTextureCoordsVarying = MaskTextureCoords.xy;
}
