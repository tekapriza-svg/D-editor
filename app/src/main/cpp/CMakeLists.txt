cmake_minimum_required(VERSION 3.22.1)

project(deditor_native)

add_library(
    deditor_native
    SHARED
    native-effects.cpp
)

target_link_libraries(
    deditor_native
    android
    jnigraphics
    log
)
