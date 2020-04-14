#include <jni.h>
#include "com_example_detectscreen_MainActivity.h"

#include <opencv2/opencv.hpp>

using namespace cv;

extern "C" {
            JNIEXPORT void JNICALL Java_com_example_detectscreen_MainActivity_ConvertRGBtoGray
            (
                JNIEnv *env,
                jobject instance,
                jlong matAddrInput,
                jlong matAddrResult){

                Mat &matInput = *(Mat *)matAddrInput;
                Mat &matResult = *(Mat *)matAddrResult;

                Mat gray;
                Mat canny;

                cvtColor(matInput, gray, COLOR_BGR2GRAY);
                GaussianBlur(gray, gray, Size(9,9), 0);
                Canny(gray, matResult, 105, 200);



            }
}
