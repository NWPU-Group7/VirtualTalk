# $Id: Application.mk 165 2012-12-28 19:55:23Z oparviai $
#
# Build both ARMv5TE and ARMv7-A machine code.
#

APP_ABI := armeabi-v7a, arm64-v8a, x86, x86_64
APP_OPTIM := release
APP_STL := gnustl_static