precision highp float;
uniform sampler2D OriginTexture;
varying highp vec2 OriginTextureCoordsVarying;

uniform sampler2D MaskTexture;
varying highp vec2 MaskTextureCoordsVarying;

void main (void) {
    vec4 origin = texture2D(OriginTexture, OriginTextureCoordsVarying);
    vec4 mask = texture2D(MaskTexture, MaskTextureCoordsVarying);
    vec4 maskColor = vec4(0.0, 0.0, 1.0, 1.0);
    gl_FragColor = vec4(mix(origin.rgb, maskColor.rgb, 1.0-mask.r), origin.a);
}
