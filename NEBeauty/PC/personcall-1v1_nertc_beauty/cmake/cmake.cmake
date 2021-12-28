#**3rdparty************************************************************
#**jsoncpp***
set(JSONCPP_INCLUDE_DIR "${PROJECT_SOURCE_DIR}/3rdparty/jsoncpp/include")
#**NERTC***
set(NERTC_INCLUDE_DIR "${PROJECT_SOURCE_DIR}/3rdparty/nertc/include")

if(CMAKE_CL_64)
    set(NERTC_LIBRARY_DIRS "${PROJECT_SOURCE_DIR}/3rdparty/nertc/lib/x64")
	set(NERTC_DLL_DIRS "${PROJECT_SOURCE_DIR}/3rdparty/nertc/bin/x64")
	set(JSONCPP_LIBRARY_DIRS "${PROJECT_SOURCE_DIR}/3rdparty/jsoncpp/lib/x64")
else(CMAKE_CL_64)
    set(NERTC_LIBRARY_DIRS "${PROJECT_SOURCE_DIR}/3rdparty/nertc/lib/x86")
	set(NERTC_DLL_DIRS "${PROJECT_SOURCE_DIR}/3rdparty/nertc/bin/x86")
	set(JSONCPP_LIBRARY_DIRS "${PROJECT_SOURCE_DIR}/3rdparty/jsoncpp/lib/x86")
endif(CMAKE_CL_64)
file(GLOB_RECURSE nertc_dlls "${NERTC_DLL_DIRS}/*.dll")
foreach(nertc_dll ${nertc_dlls})
     message("*****" ${nertc_dll})
    file(COPY ${nertc_dll} DESTINATION ${CMAKE_BINARY_DIR}/Debug)
	file(COPY ${nertc_dll} DESTINATION ${CMAKE_BINARY_DIR}/Release)
endforeach()

#**Enable Solution Folders*********************************************
set_property(GLOBAL PROPERTY USE_FOLDERS ON)
set_property(GLOBAL PROPERTY PREDEFINED_TARGETS_FOLDER "CMakeTargets")