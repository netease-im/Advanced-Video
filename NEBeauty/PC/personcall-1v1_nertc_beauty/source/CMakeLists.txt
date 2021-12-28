#
set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTOUIC ON)
set(CMAKE_AUTORCC ON)

# Qt
find_package(Qt5 COMPONENTS Core Gui Widgets REQUIRED)

#
include_directories(${NERTC_INCLUDE_DIR})
link_directories(${NERTC_LIBRARY_DIRS})
#
include_directories(${JSONCPP_INCLUDE_DIR})
link_directories(${JSONCPP_LIBRARY_DIRS})
#
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include)
# source code
#
file(GLOB_RECURSE demo_srcs "*.cpp" "*.c")
file(GLOB_RECURSE demo_hdrs "*.hpp" "*.h")
set(qrcs ${CMAKE_CURRENT_SOURCE_DIR}/resource/image.qrc)

#
set(EXECUTABLE_OUTPUT_PATH "${CMAKE_BINARY_DIR}")

# execute
add_executable(1v1_beauty ${demo_srcs} ${demo_hdrs} ${qrcs})

#
qt5_use_modules(1v1_beauty Core)
qt5_use_modules(1v1_beauty Gui)
qt5_use_modules(1v1_beauty Widgets)

#
target_link_libraries(1v1_beauty nertc_sdk.lib debug jsoncppd optimized jsoncpp)

#
set_target_properties(1v1_beauty PROPERTIES LINK_FLAGS_RELEASE "/SUBSYSTEM:WINDOWS /ENTRY:mainCRTStartup")
