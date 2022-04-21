
#
set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

#
set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTOUIC ON)
set(CMAKE_AUTORCC ON)

# Qt
find_package(Qt5 COMPONENTS Core Gui Widgets REQUIRED)

# source code
file(GLOB_RECURSE All_SOURCES ${CMAKE_CURRENT_LIST_DIR}/*.cpp 
                              ${CMAKE_CURRENT_LIST_DIR}/*.h
                              ${CMAKE_CURRENT_LIST_DIR}/*.qrc)

#
include_directories(${CMAKE_SOURCE_DIR}/3rdparty/nertc/include)
include_directories(${CMAKE_SOURCE_DIR}/3rdparty/jsoncpp/include)

IF(CMAKE_CL_64)
    link_directories(${CMAKE_SOURCE_DIR}/3rdparty/nertc/lib/x64)
	link_directories(${CMAKE_SOURCE_DIR}/3rdparty/jsoncpp/lib/x64)
	set(Nertc_DLL_DIR ${CMAKE_SOURCE_DIR}/3rdparty/nertc/bin/x64)
ELSE(CMAKE_CL_64)
    link_directories(${CMAKE_SOURCE_DIR}/3rdparty/nertc/lib/x86)
	link_directories(${CMAKE_SOURCE_DIR}/3rdparty/jsoncpp/lib/x86)
	set(Nertc_DLL_DIR ${CMAKE_SOURCE_DIR}/3rdparty/nertc/bin/x86)
ENDIF(CMAKE_CL_64)

#
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include)

# execute
add_executable(1v1_beauty ${All_SOURCES})

#
target_link_libraries(1v1_beauty nertc_sdk.lib debug jsoncppd optimized jsoncpp)

#
qt5_use_modules(1v1_beauty Core)
qt5_use_modules(1v1_beauty Gui)
qt5_use_modules(1v1_beauty Widgets)

set(EXECUTABLE_OUTPUT_PATH ${CMAKE_BINARY_DIR})

#
file(GLOB dll_src "${Nertc_DLL_DIR}/*.dll")
foreach(nertc_dll ${dll_src})
     message("*****" ${dll_src})
    file(COPY ${nertc_dll} DESTINATION ${CMAKE_BINARY_DIR}/Debug)
	file(COPY ${nertc_dll} DESTINATION ${CMAKE_BINARY_DIR}/Release)
endforeach()

set_target_properties(1v1_beauty PROPERTIES LINK_FLAGS_RELEASE "/SUBSYSTEM:WINDOWS /ENTRY:mainCRTStartup")


