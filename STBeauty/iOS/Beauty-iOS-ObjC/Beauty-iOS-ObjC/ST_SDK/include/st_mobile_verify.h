/**
*@file st_mobile_verify.h
* 提供人脸特征提取及比对方法
*
*@attention 该文件中的API不保证线程安全.多线程调用时,需要确保安全调用.
*该文件中的API不保证线程安全.多线程调用时,需要确保安全调用.例如在 detect/reset 没有执行完就执行 process 可能造成crash;在 detect 执行过程中调用 reset 函数可能会造成crash.

* 一般调用步骤：创建句柄->获取特征->特征比对->销毁句柄

* st_mobile_verify_create

* st_mobile_verify_get_feature

* st_mobile_verify_get_features_compare_score

* st_mobile_verify_destroy

**/
#ifndef ST_MOBILE_VERIFY_H
#define ST_MOBILE_VERIFY_H

#include "st_mobile_common.h"

/// @brief 创建verify句柄
/// @param[in] p_model_path 模型文件路径
/// @param[out] p_handle handle信息，由库负责创建，创建完成供后续接口调用时使用
/// @return 成功返回ST_OK, 失败返回其他错误码, 错误码定义在st_mobile_common.h中, 如ST_E_FAIL等
ST_SDK_API st_result_t
st_mobile_verify_create(const char *p_model_path, st_handle_t *p_handle);

/// @brief 创建verify句柄
/// @param[in] p_buffer 模型buffer指针
/// @param[in] buffer_size 模型buffer大小
/// @param[out] p_handle handle信息，由库负责创建，创建完成供后续接口调用时使用
/// @return 成功返回ST_OK, 失败返回其他错误码, 错误码定义在st_mobile_common.h中, 如ST_E_FAIL等
ST_SDK_API st_result_t
st_mobile_verify_create_from_buffer(const unsigned char *p_buffer,
                                    int buffer_len,
                                    st_handle_t *p_handle);

/// @brief 根据人脸关键点获取人脸特征
/// @param[in] handle verify句柄
/// @param[in] p_image 图像数据
/// @param[in] p_face_key_points 人脸关键点
/// @param[in] face_key_points_size 人脸关键点数量
/// @param[out] feature 特征数组，用户使用前需要将特征数组拷贝到自己的内存空间
/// @param[out] feature_size 特征数组长度
/// @return 成功返回ST_OK, 失败返回其他错误码, 错误码定义在st_mobile_common.h中, 如ST_E_FAIL等
ST_SDK_API st_result_t
st_mobile_verify_get_feature(st_handle_t handle,
                             const st_image_t *p_image,
                             const st_pointf_t *p_face_key_points,
                             unsigned int face_key_points_size,
                             char **feature,
                             unsigned int *feature_size);

#define ST_MOBILE_FEATURE_MAX_SIZE 5000
/// @brief 获取多个人脸的人脸特征
/// @param[in] handle verify句柄
/// @param[in] p_image 图像数据
/// @param[in] p_faces 人脸信息, 必须包含106点
/// @param[in] face_count 人脸个数
/// @param[out] features 特征数组, 由用户分配内存, 需要包含face_count个char数组, 建议分配的每个字符串长度不小于ST_MOBILE_FEATURE_MAX_SIZE
/// @return 成功返回ST_OK, 失败返回其他错误码, 错误码定义在st_mobile_common.h中, 如ST_E_FAIL等
ST_SDK_API st_result_t
st_mobile_verify_get_feature_multi_face(
	st_handle_t handle,
	const st_image_t *p_image,
	const st_mobile_face_t * p_faces,
	unsigned int face_count,
	char** features
);

/// @brief 特征比较
/// @param[in] handle
/// @param[in] p_feature 第一个特征信息
/// @param[in] feature_size 第一个特征信息长度
/// @param[in] p_other_feature 第二个特征信息
/// @param[in] other_feature_size 第二个特征信息长度
/// @param[out] score 两个特征信息的比较结果分数，有效范围（0,1）
/// @return 成功返回ST_OK, 失败返回其他错误码, 错误码定义在st_mobile_common.h中, 如ST_E_FAIL等
ST_SDK_API st_result_t
st_mobile_verify_get_features_compare_score(st_handle_t handle,
                                            const char *p_feature,
                                            unsigned int feature_size,
                                            const char *p_other_feature,
                                            unsigned int other_feature_size,
                                            float *score);

/// @brief 从一组特征数组中搜索最近的若干特征
/// @param[in] handle 已初始化的句柄
/// @param[in] p_query 待搜索的特征信息
/// @param[in] p_list_feature 特征信息数组
/// @param[in] list_count 特征信息数量
/// @param[in] top_k 最大的特征搜索数量
/// @param[out] top_idxs 搜索到的特征数据库索引值数组(由用户分配和释放)
/// @param[out] top_scores 搜索到的特征相似度得分数组(由用户分配和释放), 范围0-1, 得分越接近1越相似
/// @param[out] result_length 实际搜索到的特征数量
/// @return 成功返回ST_OK, 否则返回错误类型
/// @note 返回的结果以1作为起始索引
ST_SDK_API
st_result_t
st_mobile_verify_search_nearest_features(st_handle_t handle,
                                         const char *p_query,
                                         char * const *p_list_feature,
                                         int list_count,
                                         unsigned int top_k,
                                         int *top_idxs,
                                         float *top_scores,
                                         unsigned int *result_length);

/// @brief 销毁句柄
/// @param[in] handle
/// @return 成功返回ST_OK, 失败返回其他错误码, 错误码定义在st_mobile_common.h中, 如ST_E_FAIL等
ST_SDK_API st_result_t
st_mobile_verify_destroy(st_handle_t handle);

#endif //ST_MOBILE_VERIFY_H
